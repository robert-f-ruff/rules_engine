"""Define the forms used by the rules engine."""
from django.forms import (Form, ModelForm, BaseInlineFormSet,
                          CheckboxSelectMultiple, TextInput, HiddenInput,
                          inlineformset_factory, Select, IntegerField,
                          CharField)
from .models import Rule, RuleActions, RuleActionParameters


class RuleForm(ModelForm):
    """Implement the rule form.
    
    This form allows the user to create and edit rules.
    """
    class Meta:
        model = Rule
        fields = ['name', 'criteria']
        widgets = {
            'criteria': CheckboxSelectMultiple(attrs={'class': 'form-check-input'}),
        }


class RuleActionsForm(ModelForm):
    """Implement the rule actions form.

    This form allows the user to manage the actions associated with a rule.
    """
    class Meta:
        model = RuleActions
        fields = ['action_number', 'action']
        widgets = {
            'action': Select(attrs={'onchange': 'getParameters(this)',
                                    'class': 'form-select'}),
        }

    def __init__(self, *args, **kwargs) -> None:
        """ This function executes when the form is created.
        """
        super().__init__(*args, **kwargs)
        self.fields['action'].empty_label = ''


# 02/22/2023: Uncomment can_delete_extra=False after upgrading to Django 4.2
# Issue #34349: https://code.djangoproject.com/ticket/34349
RuleActionsFormSet = inlineformset_factory(Rule,
                                           RuleActions,
                                           form=RuleActionsForm,
                                           extra=1,
                                           can_delete=True)
                                        #    can_delete_extra=False)


class RuleActionParametersForm(ModelForm):
    """Implement the rule action parameters form.

    This form allows the user to manage the parameters for an associated
    action. The parameters are retrieved from the database.
    """
    class Meta:
        model = RuleActionParameters
        fields = ['parameter_value']
        widgets = {
            'parameter_value': TextInput,
        }

    def __init__(self, *args, **kwargs) -> None:
        super().__init__(*args, **kwargs)
        self.fields['parameter_value'] = self.instance.parameter.form_control


class BaseRuleActionParametersFormSet(BaseInlineFormSet):
    """ This class modifies the behavior of the formset created with the
        RuleActionParametersForm.
    """
    deletion_widget = HiddenInput


ActionParametersFormSet = inlineformset_factory(RuleActions,
                                                RuleActionParameters,
                                                form=RuleActionParametersForm,
                                                extra=0,
                                                can_delete=True,
                                                formset=BaseRuleActionParametersFormSet)


class ActionParameterForm(Form):
    """ Implement the action parameters form.

    This form allows the user to set the parameters for an associated
    action.
    """
    form_prefix = ''

    def __init__(self, *args, **kwargs) -> None:
        """ This function executes when the form is created.

        It creates a field for each parameter with the parameter's type.
        Each field's label is the name of the parameter.
        """
        create_fields = kwargs.pop('create_fields', [])
        ruleaction_set_id = kwargs.pop('ruleaction_set_id', 0)
        super().__init__(*args, **kwargs)
        count = 0
        self.form_prefix = 'new_parameter_form-' + str(ruleaction_set_id) + '-'
        for field in create_fields:
            field_name = (self.form_prefix + 'parameter_value-'
                          + str(field['parameter_number']))
            self.fields[field_name] = field['parameter'].form_control
            pk_field_name = (self.form_prefix + 'parameter_name-'
                             + str(field['parameter_number']))
            self.fields[pk_field_name] = CharField(widget=HiddenInput)
            self.fields[pk_field_name].initial = field['parameter'].name
            count += 1
        self.fields[self.form_prefix + 'parameter_count'] = IntegerField(widget=HiddenInput)
        self.fields[self.form_prefix + 'parameter_count'].initial = count

    def get_prefix(self) -> str:
        """ This function will return the prefix that is prepended to every field.
        """
        return self.form_prefix
