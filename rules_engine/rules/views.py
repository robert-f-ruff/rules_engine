"""Define the views used by the rules engine."""
import logging
from typing import Any
import requests

from django.forms import BaseInlineFormSet, ModelForm
from django.http import (HttpRequest, HttpResponse, HttpResponseNotAllowed,
                         HttpResponseRedirect, JsonResponse, QueryDict)
from django.shortcuts import get_object_or_404, redirect, render
from django.template.loader import render_to_string
from django.urls import reverse_lazy
from django.views import View, generic

from .core import retrieve_setting
from .forms import (ActionParameterForm, ActionParametersFormSet,
                    RuleActionsFormSet, RuleForm)
from .models import ActionParameters, Rule, RuleActionParameters, RuleActions

logger = logging.getLogger('rules_engine')


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

    def get_context_data(self, **kwargs: Any) -> dict[str, Any]:
        """ This function adds additional data to the context for use
            in the template.
        """
        context = super().get_context_data(**kwargs)
        status = call_engine_api('status')
        reload = {}
        reload['engine_response_status_reload'] = (
                self.request.session.pop('engine_response_status_reload','OK')
        )
        if (status['engine_response_status_status'] != 'OK'
            or reload['engine_response_status_reload'] != 'OK'):
            context['engine_alert'] = True
        else:
            context['engine_alert'] = False
        context['engine_status_text_status']=status['engine_status_text_status']
        context['engine_status_text_reload'] = (
                self.request.session.pop('engine_status_text_reload', '')
        )
        return context


class RuleDeleteView(generic.edit.DeleteView):
    """ This view allows the user to delete a rule. It shows a confirmation
        page to confirm that is what the user wants.
    """
    model = Rule
    context_object_name = 'rule'
    success_url = reverse_lazy('rules:index')

    def form_valid(self, form: ModelForm) -> HttpResponseRedirect:
        """ This function is called when a record in the database is deleted.
        """
        rule = self.get_object()
        logger.info('Deleted rule #%i %s', rule.pk, rule.name) # type: ignore
        call_status = call_engine_api('reload')
        for key in call_status:
            self.request.session[key] = call_status.get(key)
        return super().form_valid(form) # type: ignore


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

    def get(self, request: HttpRequest, *args: Any, rule_id: int = 0, **kwargs: Any) -> (
            HttpResponse):
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
                parameter_form = self.get_part_action_parameter_form(
                                                            rule_action_form=rule_action_form)
                if parameter_form is not None:
                    self.parameter_forms.append(parameter_form)
                else:
                    self.parameter_forms.append(None)
            else:
                self.parameter_formsets.append(None)
                self.parameter_forms.append(None)
        logger.info('Successfully generated rule form with rule_id %i',
                     rule_id)
        return render(request, self.template_name, self.get_context_data())

    def post(self, request: HttpRequest, *args: Any, rule_id: int = 0, **kwargs: Any) -> (
            HttpResponseRedirect | HttpResponse):
        """ This function responds to the HTTP POST command.
        """
        all_pass = True
        modified = False
        if rule_id > 0:
            self.rule = get_object_or_404(Rule, pk=rule_id)
        self.set_template_name()
        self.rule_form = RuleForm(data=request.POST, instance=self.rule)
        self.rule_actions_formset = RuleActionsFormSet(data=request.POST,
                                                       instance=self.rule_form.instance)
        if not self.rule_form.is_valid() or not self.rule_actions_formset.is_valid():
            all_pass = False
        else:
            modified = self.rule_form.has_changed()
        ruleactions_form_count = int(request.POST.get('ruleactions_set-TOTAL_FORMS','0'))
        for form_id in range(0, ruleactions_form_count):
            rule_action_form = self.rule_actions_formset[form_id]
            if not modified:
                modified = rule_action_form.has_changed()
            if request.POST.get('ruleactions_set-' + str(form_id) + '-id', '') != '':
                formset = ActionParametersFormSet(data=request.POST,
                            instance=rule_action_form.instance,
                            prefix='param' + str(rule_action_form.instance.pk))
                self.parameter_formsets.append(formset)
                if not formset.is_valid():
                    all_pass = False
                else:
                    if rule_action_form.cleaned_data.get('DELETE'):
                        for form in formset:
                            if form.has_changed():
                                form.add_error(None, self.ACT_DELETE_PARAMETER_CHANGE)
                                all_pass = False
                    else:
                        self.forms_to_save['form_sets'].append(
                                                        self.parameter_formsets.index(formset))
                        if not modified:
                            modified = formset.has_changed()
            else:
                self.parameter_formsets.append(None)
            if int(request.POST.get(ActionParameterForm.get_prefix(form_id)
                                    + 'parameter_count', 0)) > 0:
                parameter_form = self.get_part_action_parameter_form(data=request.POST,
                                                            rule_action_form=rule_action_form)
                if parameter_form is None:
                    parameter_form = get_action_parameter_form(
                        action_name=rule_action_form['action'].value(),
                        ruleaction_set_id=str(form_id),
                        data=request.POST
                    )
                self.parameter_forms.append(parameter_form)
                if not parameter_form.is_valid():
                    all_pass = False
                else:
                    self.forms_to_save['forms'].append(self.parameter_forms.index(parameter_form))
                    if not modified:
                        modified = parameter_form.has_changed()
            else:
                self.parameter_forms.append(None)
        if all_pass:
            logger.debug('All forms are valid, saving forms')
            self.rule_form.save()
            if rule_id == 0:
                logger.info('Created rule #%i %s', self.rule_form.instance.pk,
                            self.rule_form.instance.name) # type: ignore
            else:
                logger.info('Updated rule #%i %s', self.rule_form.instance.pk,
                            self.rule_form.instance.name) # type: ignore
            self.rule_actions_formset.save()
            for index in self.forms_to_save['form_sets']:
                self.parameter_formsets[index].save()
            for index in self.forms_to_save['forms']:
                parameter_form = self.parameter_forms[index]
                rule_action_form = self.rule_actions_formset[index]
                parameter_form.save(rule_action_form.instance)
            if modified:
                logger.debug('Instructing the engine to reload the ruleset')
                call_status = call_engine_api('reload')
                for key in call_status:
                    request.session[key] = call_status.get(key)
            return redirect('rules:index')
        return render(request, self.template_name, self.get_context_data())

    def get_context_data(self) -> dict[str, Any]:
        """ This function returns the context dictionary with all the
            data needed by the template.
        """
        for index, parameter_form in enumerate(self.parameter_forms):
            if self.parameter_forms[index] is not None:
                self.parameter_forms[index] = render_to_string('rules/action_parameter_form.html',
                                            {'parameter_form': parameter_form})
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

    def set_template_name(self) -> None:
        """ This function will set the correct template name to render
            to the client.
        """
        if self.rule is not None:
            self.template_name = 'rules/rule_edit.html'
        else:
            self.template_name = 'rules/rule_create.html'

    def get_part_action_parameter_form(self, rule_action_form: BaseInlineFormSet,
                                       data: QueryDict | None = None) -> (
                                           ActionParameterForm | None):
        """ This function will return a new parameter form containing just the
            parameters that do not exist in the database.
        """
        if rule_action_form.instance.pk:
            missing_parameters = ActionParameters.objects.filter(
                action=rule_action_form.instance.action).exclude(parameter__in=
                RuleActionParameters.objects.filter(rule_action=rule_action_form.instance)
                    .values_list('parameter', flat=True)).order_by('parameter_number')
            if len(missing_parameters) > 0:
                fields = []
                for action_parameter in missing_parameters:
                    fields.append({'parameter': action_parameter.parameter,
                                'parameter_number': action_parameter.parameter_number})
                return ActionParameterForm(data, create_fields=fields,
                    ruleaction_set_id=
                        self.rule_actions_formset.forms.index(rule_action_form) # type: ignore
                )
            else:
                logger.debug('No missing parameters for rule action ID #%i',
                             rule_action_form.instance.pk)
        return None

def parameters(request: HttpRequest, action_name: str) -> JsonResponse | HttpResponseNotAllowed:
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
        logger.info('Successfully returned parameters for action %s', action_name)
        return JsonResponse({'parameter_form': rendered_form})
    logger.warning('Invalid request method %s from %s', request.method, request.get_host())
    return HttpResponseNotAllowed(['GET'])

def engine_reload(request: HttpRequest) -> JsonResponse | HttpResponseNotAllowed:
    """ This function will call the engine status and ruleset reload APIs and return the
        result in a JSON object.
    """
    if request.method == "GET":
        status_data = call_engine_api('status')
        data = {'engine_status_text_status': status_data['engine_status_text_status']}
        reload_data = call_engine_api('reload')
        data['engine_status_text_reload'] = reload_data['engine_status_text_reload']
        if (status_data['engine_response_status_status'] != 'OK'
            or reload_data['engine_response_status_reload'] != 'OK'):
            data['engine_alert'] = 'True'
        else:
            data['engine_alert'] = 'False'
        logger.info('Successfully returned engine status; engine_alert = %s', data['engine_alert'])
        return JsonResponse(data=data)
    logger.warning('Invalid request method %s from %s', request.method, request.get_host())
    return HttpResponseNotAllowed(['GET'])

def get_action_parameter_form(action_name: str, ruleaction_set_id: str ='',
                              data: QueryDict | None = None) -> ActionParameterForm:
    """ This function will return a fully functional ActionParameterForm.
    """
    action_parameters = (ActionParameters.objects.select_related('parameter')
                      .filter(action=action_name).order_by('parameter_number'))
    fields = []
    for action_parameter in action_parameters:
        fields.append({'parameter': action_parameter.parameter,
                       'parameter_number': action_parameter.parameter_number})
    logger.debug('Generated ActionParameterForm for action %s and ruleaction_set_id %s',
                 action_name, ruleaction_set_id)
    return ActionParameterForm(data, create_fields=fields,
                               ruleaction_set_id=ruleaction_set_id)

def call_engine_api(endpoint: str) -> dict[str, str]:
    """ This function interacts with the backend engine via a REST API.
    """
    url = f"http://{retrieve_setting('engine_host')}/rules_engine/engine/{endpoint}"
    data = {}
    try:
        response = None
        if endpoint == 'reload':
            response = requests.put(url, json={'accessCode': retrieve_setting('engine_reload_key')},
                                    timeout=10)
        else:
            response = requests.get(url, timeout=10)
        if response.status_code == 200:
            data['engine_response_status_' + endpoint] = 'OK'
            status = response.json()['status']
            if endpoint == 'reload':
                if status != 'OK':
                    logger.warning('Rules Engine Reload: %s', status)
                status = {
                    'OK': 'Ruleset successfully reloaded',
                    'FAILED': 'Ruleset was not reloaded',
                }.get(status, status)
            data['engine_status_text_' + endpoint] = status
        else:
            data['engine_response_status_' + endpoint] = (
                                        f'Error Code {response.status_code}'
            )
            data['engine_status_text_' + endpoint] = response.reason
            logger.warning('HTTP Error Code %i: %s', response.status_code, response.reason)
    except requests.exceptions.ConnectionError as error:
        data['engine_response_status_' + endpoint] = 'Connection Error'
        error_text = str(error).split('>: ')
        if len(error_text) >= 2:
            data['engine_status_text_' + endpoint] = error_text[1].split("'))")[0]
        else:
            data['engine_status_text_' + endpoint] = str(error)
        logger.warning('Connection Error: %s', data['engine_status_text_' + endpoint])
    except requests.exceptions.RequestException as error:
        data['engine_response_status_' + endpoint] = 'Request Error'
        data['engine_status_text_' + endpoint] = str(error)
        logger.warning('Request Error: %s', data['engine_status_text_' + endpoint])
    return data
