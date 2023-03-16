"""Define the views used by the rules engine."""
from typing import Any
from django.http import HttpResponseNotAllowed, JsonResponse, HttpRequest
from django.shortcuts import render, get_object_or_404, redirect
from django.template.loader import render_to_string
from django.urls import reverse_lazy
from django.views import generic, View
from .models import ActionParameters, Rule, RuleActions, RuleActionParameters, Parameter
from .forms import RuleForm, RuleActionsFormSet, ActionParametersFormSet, ActionParameterForm


class IndexView(generic.ListView):
    """ This is the first view a user sees when accessing the site.
        It lists the rules that already exist, allows the user to
        delete a rule and to add a new rule.
    """
    template_name = 'rules/index.html'
    context_object_name = 'rule_list'

    def get_queryset(self):
        """ This function returns the queryset used by the generic
            ListView view.
        """
        return Rule.objects.order_by('name')


class RuleDeleteView(generic.edit.DeleteView):
    """ This view allows the user to delete a rule. It shows a confirmation
        page to confirm that is what the user wants.
    """
    model = Rule
    context_object_name = 'rule'
    success_url = reverse_lazy('rules:index')


class RuleView(View):
    """ This view allows the user to edit or create a rule. It manages the
        rule form.
    """
    ACT_DELETE_PARAMETER_CHANGE = ('Cannot change parameter and delete the '
        + 'associated action at the same time.')
    def __init__(self, **kwargs: Any) -> None:
        super().__init__(**kwargs)
        self.template_name = ''
        self.rule = None
        self.rule_form = None
        self.rule_actions_formset = None
        self.parameter_formsets = []
        self.parameter_forms = []
        self.forms_to_save = {'form_sets': [], 'forms': []}

    def get(self, request: HttpRequest, *args, rule_id=0, **kwargs):
        """ This function responds to the HTTP GET command.
        """
        if rule_id > 0:
            self.rule = get_object_or_404(Rule, pk=rule_id)
        self.set_template_name()
        self.rule_form = RuleForm(instance=self.rule)
        self.rule_actions_formset = RuleActionsFormSet(instance=self.rule)
        for rule_action_form in self.rule_actions_formset.forms:
            if rule_action_form.instance.pk is not None:
                self.parameter_formsets.append(
                    ActionParametersFormSet(instance=rule_action_form.instance,
                                            prefix='param' + str(rule_action_form.instance.pk)))
            else:
                self.parameter_formsets.append(None)
            self.parameter_forms.append(None)
        return render(request, self.template_name, self.get_context_data())

    def post(self, request: HttpRequest, *args, rule_id=0, **kwargs):
        """ This function responds to the HTTP POST command.
        """
        all_pass = True
        if rule_id > 0:
            self.rule = get_object_or_404(Rule, pk=rule_id)
        self.set_template_name()
        self.rule_form = RuleForm(data=request.POST, instance=self.rule)
        self.rule_actions_formset = RuleActionsFormSet(data=request.POST,
                                                       instance=self.rule_form.instance)
        if not self.rule_form.is_valid() or not self.rule_actions_formset.is_valid():
            all_pass = False
        ruleactions_form_count = int(request.POST.get('ruleactions_set-TOTAL_FORMS','0'))
        for form_id in range(0, ruleactions_form_count):
            rule_action_form = self.rule_actions_formset[form_id]
            if request.POST.get('ruleactions_set-' + str(form_id) + '-id', '') != '':
                formset = ActionParametersFormSet(data=request.POST,
                            instance=rule_action_form.instance,
                            prefix='param' + str(rule_action_form.instance.pk))
                if not formset.is_valid():
                    all_pass = False
                elif rule_action_form.cleaned_data.get('DELETE'):
                    for form in formset:
                        if form.has_changed():
                            form.add_error(None, self.ACT_DELETE_PARAMETER_CHANGE)
                            all_pass = False
                self.parameter_formsets.append(formset)
                self.forms_to_save['form_sets'].append(self.parameter_formsets.index(formset))
            else:
                self.parameter_formsets.append(None)
            if int(request.POST.get('new_parameter_form-' + str(form_id)
                                    + '-parameter_count', 0)) > 0:
                parameter_form = get_action_parameter_form(
                    action_name=rule_action_form['action'].value(),
                    ruleaction_set_id=str(form_id),
                    data=request.POST
                )
                if not parameter_form.is_valid():
                    all_pass = False
                self.parameter_forms.append(parameter_form)
                self.forms_to_save['forms'].append(self.parameter_forms.index(parameter_form))
            else:
                self.parameter_forms.append(None)
        if all_pass:
            self.rule_form.save()
            self.rule_actions_formset.save()
            for index in self.forms_to_save['form_sets']:
                self.parameter_formsets[index].save()
            for index in self.forms_to_save['forms']:
                parameter_form = self.parameter_forms[index]
                rule_action_form = self.rule_actions_formset[index]
                self.save_new_parameters(parameter_form.cleaned_data,
                                         rule_action_form.instance,
                                         parameter_form.form_prefix)
            return redirect('rules:index')
        return render(request, self.template_name, self.get_context_data())

    def get_context_data(self):
        """ This function returns the context dictionary with all the
            data needed by the template.
        """
        for index in self.forms_to_save['forms']:
            self.parameter_forms[index] = render_to_string('rules/action_parameter_form.html',
                                         {'parameter_form': self.parameter_forms[index]})
        rule_action_parameters_items = zip(self.parameter_formsets,
                                           self.parameter_forms,
                                           strict=True)
        context = {
            'rule_form': self.rule_form,
            'rule_actions_formset': self.rule_actions_formset,
        }
        if self.rule_actions_formset is not None:
            context['rule_actions_empty_form'] = render_to_string('rules/action_form.html',
                                        {'action_form': self.rule_actions_formset.empty_form})
            context['rule_actions_parameter_forms'] = zip(self.rule_actions_formset.forms,
                                        rule_action_parameters_items,
                                        strict=True)
        if self.rule is not None:
            context['rule_id'] = self.rule.pk
        return context

    def set_template_name(self):
        """ This function will set the correct template name to render
            to the client.
        """
        if self.rule is not None:
            self.template_name = 'rules/rule_edit.html'
        else:
            self.template_name = 'rules/rule_create.html'

    def save_new_parameters(self, clean_data, rule_action, form_prefix):
        """ This function will save a new rule action parameter record.
        """
        parameter_count = int(clean_data[form_prefix + 'parameter_count'])
        for parameter_number in range(1, parameter_count + 1):
            parameter = Parameter.objects.get(
                pk=clean_data[form_prefix + 'parameter_name-' + str(parameter_number)])
            parameter_value = (clean_data[form_prefix + 'parameter_value-'
                               + str(parameter_number)])
            rule_action_parameter = RuleActionParameters(
                rule_action=rule_action,
                parameter=parameter,
                parameter_value=parameter_value)
            rule_action_parameter.save()

def parameters(request, action_name):
    """ This function will return a JSON object containing the parameters
        for the requested action.
    """
    if request.method == "GET":
        ruleaction_set_id = request.GET.get('ruleaction_set-id', '')
        ruleaction_id = request.GET.get('ruleaction-id', '')
        if ruleaction_id != '':
            rule_action = RuleActions.objects.get(pk=ruleaction_id)
            if rule_action.action.name == action_name:
                return JsonResponse({'parameter_form': ''})
        parameter_form = get_action_parameter_form(action_name=action_name,
                                                   ruleaction_set_id=ruleaction_set_id)
        rendered_form = render_to_string('rules/action_parameter_form.html',
                                         {'parameter_form': parameter_form})
        return JsonResponse({'parameter_form': rendered_form})
    return HttpResponseNotAllowed(['GET'])

def get_action_parameter_form(action_name, ruleaction_set_id='',
                              data=None) -> ActionParameterForm:
    """ This function will return a fully functional ActionParameterForm.
    """
    action_parameters = (ActionParameters.objects.select_related('parameter')
                      .filter(action=action_name).order_by('parameter_number'))
    fields = []
    for action_parameter in action_parameters:
        fields.append({'parameter': action_parameter.parameter,
                       'parameter_number': action_parameter.parameter_number})
    return ActionParameterForm(data, create_fields=fields,
                               ruleaction_set_id=ruleaction_set_id)
