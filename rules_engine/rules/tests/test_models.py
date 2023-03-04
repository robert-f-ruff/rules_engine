"""Define tests for the models."""
from django.db import IntegrityError
from django.test import TestCase
from django.forms import IntegerField, CharField, RegexField
from rules.models import (Parameter, ActionParameters, Action, Rule,
                          RuleActions)

class ParameterModelTests(TestCase):
    """ Test the Parameter model.
    """
    def test_form_control_with_number(self):
        """ form_control is a property that returns a form control
            based on the given parameter's attributes.
        """
        parameter = Parameter(data_type=Parameter.NUMBER, required=True, help_text='Sample help.')
        self.assertIsInstance(parameter.form_control, IntegerField, 'return IntegerField control')

    def test_form_control_with_invalid_type(self):
        """ form_control is a property that returns a form control
            based on the given parameter's attributes.
        """
        parameter = Parameter(data_type='XX', required=True, help_text='Sample help.')
        self.assertIsInstance(parameter.form_control, CharField, 'return CharField control')

    def test_form_control_without_help_text(self):
        """ form_control is a property that returns a form control
            based on the given parameter's attributes.
        """
        parameter = Parameter(data_type=Parameter.TELEPHONE, required=False)
        self.assertIsInstance(parameter.form_control,
                              RegexField, 'return control without help text')


class ActionParametersTests(TestCase):
    """ Test the ActionParameters model.
    """
    @classmethod
    def setUpTestData(cls):
        """ Create entries in the test database.
        """
        cls.parameter_one = Parameter.objects.create(name='Test Parameter 1',
                                                     data_type=Parameter.TEXT)
        cls.parameter_two = Parameter.objects.create(name='Test Parameter 2',
                                                     data_type=Parameter.DATE)
        cls.action = Action.objects.create(name="Test Action 1", function='test()')
        cls.action_parameters_one = ActionParameters.objects.create(action=cls.action,
                                        parameter_number=1, parameter=cls.parameter_one)

    def test_non_unique_parameter_numbers(self):
        """ An action cannot have multiple parameters with the same parameter
            number.
        """
        action_parameters_two = ActionParameters(action=self.action, parameter_number=1,
                                                 parameter=self.parameter_two)
        self.assertRaisesMessage(IntegrityError, 'unique constraint', action_parameters_two.save())

    def test_unique_parameter_numbers(self):
        """ An action cannot have multiple parameters with the same parameter
            number.
        """
        action_parameters_two = ActionParameters(action=self.action, parameter_number=2,
                                                 parameter=self.parameter_two)
        try:
            action_parameters_two.save()
        except IntegrityError:
            self.fail('IntegrityError raised when saving an ActionParameters '
                      + 'object with a unique parameter number.')


class RuleActionsTests(TestCase):
    """ Test the RuleActions model.
    """
    @classmethod
    def setUpTestData(cls):
        cls.rule_one = Rule.objects.create(name='Test Rule 1')
        cls.action_one = Action.objects.create(name='Test Action 1', function='test()')
        cls.action_two = Action.objects.create(name='Test Action 2', function='test()')
        rule_actions_one = RuleActions.objects.create(rule=cls.rule_one, action_number=1,
                                        action=cls.action_one)

    def test_non_unqiue_action_numbers(self):
        """ A rule cannot have multiple actions with the same action
            number.
        """
        rule_actions_two = RuleActions(rule=self.rule_one, action_number=1,
                                        action=self.action_two)
        self.assertRaisesMessage(IntegrityError, 'unique constraint', rule_actions_two.save())

    def test_unique_action_numbers(self):
        """ A rule cannot have multiple actions with the same action
            number.
        """
        rule_actions_two = RuleActions(rule=self.rule_one, action_number=2,
                                        action=self.action_two)
        try:
            rule_actions_two.save()
        except IntegrityError:
            self.fail('IntegrityError raised when saving a RuleActions '
                        + 'object with a unique parameter number.')
