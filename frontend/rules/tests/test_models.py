"""Define tests for the models."""
from django.db import IntegrityError
from django.test import TestCase
from django.forms import (BooleanField, DateField, DateTimeField, IntegerField,
                          CharField, RegexField, TimeField)
from rules.models import (Criterion, Parameter, ActionParameters, Action, Rule,
                          RuleActions, RuleActionParameters)


class CriterionModelTests(TestCase):
    """ Test the Criterion model.
    """
    def test_string_method(self):
        """ __str__ method returns a string describing the instance of
            the model.
        """
        criterion = Criterion(name='Pleasant', logic='weather() = "sunny"')
        self.assertEqual(str(criterion), 'Pleasant')


class ParameterModelTests(TestCase):
    """ Test the Parameter model.
    """
    def test_form_control_with_number(self):
        """ form_control is a property that returns a form control
            based on the given parameter's attributes.
        """
        parameter = Parameter(data_type=Parameter.NUMBER, required=True,
                              help_text='Sample help.')
        self.assertIsInstance(parameter.form_control, IntegerField,
                              'return IntegerField control')

    def test_form_control_with_invalid_type(self):
        """ form_control is a property that returns a form control
            based on the given parameter's attributes.
        """
        parameter = Parameter(data_type='XX', required=True,
                              help_text='Sample help.')
        self.assertIsInstance(parameter.form_control, CharField,
                              'return CharField control')

    def test_form_control_with_boolean(self):
        """ form_control is a property that returns a form control
            based on the given parameter's attributes.
        """
        parameter = Parameter(name='Include signature',
                              data_type=Parameter.BOOLEAN,
                              help_text='Sample help.')
        self.assertIsInstance(parameter.form_control, BooleanField,
                              'return BooleanField control')

    def test_form_control_with_date(self):
        """ form_control is a property that returns a form control
            based on the given parameter's attributes.
        """
        parameter = Parameter(name='Date to send',
                              data_type=Parameter.DATE,
                              help_text='Sample help.')
        self.assertIsInstance(parameter.form_control, DateField,
                              'return DateField control')

    def test_form_control_with_datetime(self):
        """ form_control is a property that returns a form control
            based on the given parameter's attributes.
        """
        parameter = Parameter(name='Date to send',
                              data_type=Parameter.DATETIME,
                              help_text='Sample help.')
        self.assertIsInstance(parameter.form_control, DateTimeField,
                              'return DateTimeField control')

    def test_form_control_with_text(self):
        """ form_control is a property that returns a form control
            based on the given parameter's attributes.
        """
        parameter = Parameter(name='Subject',
                              data_type=Parameter.TEXT,
                              help_text='Sample help.')
        self.assertIsInstance(parameter.form_control, CharField,
                              'return CharField control')

    def test_form_control_without_help_text(self):
        """ form_control is a property that returns a form control
            based on the given parameter's attributes.
        """
        parameter = Parameter(data_type=Parameter.TELEPHONE, required=False)
        self.assertIsInstance(parameter.form_control,
                              RegexField, 'return control without help text')
        self.assertEqual(parameter.help_text, '')

    def test_default_for_required(self):
        """ required has a default value of False.
        """
        parameter = Parameter(name='Time to send', data_type=Parameter.TIME,
                              help_text='Sample help.')
        self.assertIsInstance(parameter.form_control, TimeField,
                              'return TimeField control')
        self.assertFalse(parameter.required)

    def test_string_method(self):
        """ __str__ method returns a string describing the instance of
            the model.
        """
        parameter = Parameter(name='Send email to', data_type=Parameter.EMAIL,
                              help_text='Sample help.')
        self.assertEqual(str(parameter), 'Send email to')


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
        cls.action = Action.objects.create(name="Test Action 1",
                                           function='test()')
        cls.action_parameters_one = ActionParameters.objects.create(
                                                action=cls.action,
                                                parameter_number=1,
                                                parameter=cls.parameter_one)

    def test_non_unique_parameter_numbers(self):
        """ An action cannot have multiple parameters with the same parameter
            number.
        """
        action_parameters_two = ActionParameters(action=self.action,
                                                 parameter_number=1,
                                                 parameter=self.parameter_two)
        self.assertRaisesMessage(IntegrityError, 'unique constraint',
                                 action_parameters_two.save())

    def test_unique_parameter_numbers(self):
        """ An action cannot have multiple parameters with the same parameter
            number.
        """
        action_parameters_two = ActionParameters(action=self.action,
                                                 parameter_number=2,
                                                 parameter=self.parameter_two)
        try:
            self.assertIsNone(action_parameters_two.save())
        except IntegrityError:
            self.fail('IntegrityError raised when saving an ActionParameters '
                      + 'object with a unique parameter number.')

    def test_string_method(self):
        """ __str__ method returns a string describing the instance of
            the model.
        """
        self.assertEqual(str(self.action_parameters_one), 'Action Test Action '
                            + '1, Parameter #1: Test Parameter 1')


class RuleTests(TestCase):
    """ Test the Rule model.
    """
    def test_string_method(self):
        """ __str__ method returns a string describing the instance of
            the model.
        """
        criterion_one = Criterion(name='Stormy', logic='weather="thunder"')
        criterion_one.save()
        rule_one = Rule(name='Test Rule 1')
        rule_one.save()
        rule_one.criteria.add(criterion_one)
        self.assertEqual(str(rule_one), 'Test Rule 1')


class RuleActionsTests(TestCase):
    """ Test the RuleActions model.
    """
    @classmethod
    def setUpTestData(cls):
        cls.rule_one = Rule.objects.create(name='Test Rule 1')
        cls.action_one = Action.objects.create(name='Test Action 1',
                                               function='test()')
        cls.action_two = Action.objects.create(name='Test Action 2',
                                               function='test()')
        cls.rule_actions_one = RuleActions.objects.create(rule=cls.rule_one,
                                                      action_number=1,
                                                      action=cls.action_one)

    def test_non_unqiue_action_numbers(self):
        """ A rule cannot have multiple actions with the same action
            number.
        """
        rule_actions_two = RuleActions(rule=self.rule_one, action_number=1,
                                        action=self.action_two)
        self.assertRaisesMessage(IntegrityError, 'unique constraint',
                                 rule_actions_two.save())

    def test_unique_action_numbers(self):
        """ A rule cannot have multiple actions with the same action
            number.
        """
        rule_actions_two = RuleActions(rule=self.rule_one, action_number=2,
                                        action=self.action_two)
        try:
            self.assertIsNone(rule_actions_two.save())
        except IntegrityError:
            self.fail('IntegrityError raised when saving a RuleActions '
                        + 'object with a unique parameter number.')

    def test_string_method(self):
        """ __str__ method returns a string describing the instance of
            the model.
        """
        self.assertEqual(str(self.rule_actions_one),
                         'Rule Test Rule 1, Action #1: Test Action 1')


class RuleActionParametersTests(TestCase):
    """ Test the RuleActionParameters model.
    """
    @classmethod
    def setUpTestData(cls):
        cls.rule_one = Rule.objects.create(name='Test Rule 1')
        cls.action_one = Action.objects.create(name='Test Action 1',
                                               function='test()')
        cls.rule_actions_one = RuleActions.objects.create(rule=cls.rule_one,
                                                      action_number=1,
                                                      action=cls.action_one)
        cls.parameter_one = Parameter(name='Include signature',
                                        data_type=Parameter.BOOLEAN,
                                        help_text='Sample help.')

    def test_string_method(self):
        """ __str__ method returns a string describing the instance of
            the model.
        """
        rule_action_parameter = RuleActionParameters(rule_action=self.rule_actions_one,
                                                     parameter=self.parameter_one,
                                                     parameter_value='True')
        self.assertEqual(str(rule_action_parameter), 'Rule Test Rule 1, '
                         + 'Action #1: Test Action 1, Parameter: Include '
                         + 'signature, Value: True')
