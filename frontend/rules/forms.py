"""Define the forms used by the rules engine."""
import logging
from typing import Any
from django.forms import (Form, ModelForm, BaseInlineFormSet,
                          CheckboxSelectMultiple, TextInput, HiddenInput,
                          inlineformset_factory, Select, IntegerField,
                          CharField)
from .models import Parameter, Rule, RuleActions, RuleActionParameters

logger = logging.getLogger('rules_engine')


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


RuleActionsFormSet = inlineformset_factory(Rule,
                                           RuleActions,
                                           form=RuleActionsForm,
                                           extra=1,
                                           can_delete=True,
                                           can_delete_extra=False) # type: ignore


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

    def __init__(self, *args: Any, **kwargs: Any) -> None:
        """ This function executes when the form is created.
        """
        super().__init__(*args, **kwargs)
        self.fields['parameter_value'] = self.instance.parameter.form_control

    def clean(self) -> dict[str, Any]:
        """ This function executes when the form is cleaned.
        """
        super().clean()
        if 'parameter_value' in self.cleaned_data:
            if self.cleaned_data['parameter_value'] == '':
                self.cleaned_data['DELETE'] = True
        return self.cleaned_data


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
    def __init__(self, *args: Any, **kwargs: Any) -> None:
        """ This function executes when the form is created.

        It creates a field for each parameter with the parameter's type.
        Each field's label is the name of the parameter.
        """
        create_fields = kwargs.pop('create_fields', [])
        ruleaction_set_id = kwargs.pop('ruleaction_set_id', 0)
        super().__init__(*args, **kwargs)
        count = 0
        self._prefix = self.get_prefix(ruleaction_set_id)
        for field in create_fields:
            field_name = (self._prefix + 'parameter_value-'
                          + str(field['parameter_number']))
            self.fields[field_name] = field['parameter'].form_control
            pk_field_name = (self._prefix + 'parameter_name-'
                             + str(field['parameter_number']))
            self.fields[pk_field_name] = CharField(widget=HiddenInput)
            self.fields[pk_field_name].initial = field['parameter'].name
            count += 1
        self._parameter_count = self._prefix + 'parameter_count'
        self.fields[self._parameter_count] = IntegerField(widget=HiddenInput)
        self.fields[self._parameter_count].initial = count

    @classmethod
    def get_prefix(cls, ruleaction_set_id: int) -> str:
        """ This function will return the prefix that is prepended to every field.
        """
        return 'new_parameter_form-' + str(ruleaction_set_id) + '-'

    def save(self, rule_action: RuleActions) -> None:
        """ This function will save the data stored in this form.
        """
        logger.debug('ActionParameterForm.save(rule_action=%s) called', rule_action.__str__())
        for number in range(1, self.cleaned_data[self._parameter_count] + 1):
            id_number = number
            while (self._prefix + 'parameter_name-' + str(id_number)) not in self.cleaned_data:
                id_number += 1
            name = self.cleaned_data[self._prefix + 'parameter_name-'
                                     + str(id_number)]
            parameter = Parameter.objects.get(pk=name)
            parameter_value = self.cleaned_data[self._prefix
                                            + 'parameter_value-' + str(id_number)]
            if parameter_value != "":
                rule_action_parameter = RuleActionParameters(
                    rule_action=rule_action,
                    parameter=parameter,
                    parameter_value=parameter_value
                )
                logger.debug('ActionParameterForm: Saving %s', rule_action_parameter.__str__())
                rule_action_parameter.save()
