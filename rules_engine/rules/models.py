"""Define the models used by the rules application."""
from django.db import models
from django.forms import (Field, BooleanField, DateField, DateTimeField,
                          EmailField, IntegerField, RegexField, CharField,
                          TimeField)


class Criterion(models.Model):
    """Implement the Criterion model.

    A criterion defines a logic string that evaluates to a boolean
    value.
    """
    name = models.CharField(max_length=30, primary_key=True)
    logic = models.TextField()

    class Meta:
        """Define non-field attributes.
        
        (1) Change the plural name in the admin site.
        """
        verbose_name_plural = "Criteria"

    def __str__(self) -> str:
        """ This function returns a string describing the object.
        """
        return str(self.name)


class Parameter(models.Model):
    """Implement the Parameter model.

    A parameter has a data type, a flag to denote whether it is required,
    and help text. This model is used when adding an action to a rule.
    """
    name = models.CharField(max_length=30, primary_key=True)
    BOOLEAN = "BO"
    DATE = "DA"
    DATETIME = "DT"
    EMAIL = "EM"
    NUMBER = "NU"
    TELEPHONE = "TE"
    TEXT = "TX"
    TIME = "TI"
    DATA_TYPE_CHOICES = [
        (BOOLEAN, 'boolean'),
        (DATE, 'date'),
        (DATETIME, 'datetime-local'),
        (EMAIL, 'email'),
        (NUMBER, 'number'),
        (TELEPHONE, 'tel'),
        (TEXT, 'text'),
        (TIME, 'time')
    ]
    data_type = models.CharField(max_length=2, choices=DATA_TYPE_CHOICES)
    required = models.BooleanField(default=False)
    help_text = models.TextField()

    @property
    def form_control(self) -> Field:
        """ This function will return the appropriate form control for this
            parameter.
        """
        if self.data_type == Parameter.BOOLEAN:
            new_field = BooleanField(label=self.name, required=self.required,
                                     help_text=self.help_text)
        elif self.data_type == Parameter.DATE:
            new_field = DateField(label=self.name, required=self.required,
                                  help_text=self.help_text)
        elif self.data_type == Parameter.DATETIME:
            new_field = DateTimeField(label=self.name, required=self.required,
                                      help_text=self.help_text)
        elif self.data_type == Parameter.EMAIL:
            new_field = EmailField(label=self.name, required=self.required,
                                   help_text=self.help_text)
        elif self.data_type == Parameter.NUMBER:
            new_field = IntegerField(label=self.name, required=self.required,
                                     help_text=self.help_text)
        elif self.data_type == Parameter.TELEPHONE:
            pattern = r'[(]\d{3}[)] \d{3}-\d{4}'
            new_field = RegexField(regex=pattern, label=self.name,
                                   required=self.required,
                                   help_text=self.help_text)
        elif self.data_type == Parameter.TEXT:
            new_field = CharField(label=self.name, required=self.required,
                                  help_text=self.help_text)
        elif self.data_type == Parameter.TIME:
            new_field = TimeField(label=self.name, required=self.required,
                                  help_text=self.help_text)
        else:
            new_field = CharField(label=self.name, required=self.required,
                                  help_text=self.help_text)
        new_field.widget.attrs['class'] = 'form-control'
        return new_field

    def __str__(self) -> str:
        """ This function returns a string describing the object.
        """
        return str(self.name)


class Action(models.Model):
    """Implement the Action model.

    An action determines the software function that is called and the
    parameters expected by that function. This model is used when
    adding an action to a rule and when the engine is running.
    """
    name = models.CharField(max_length=30, primary_key=True)
    function = models.CharField(max_length=30)
    parameters = models.ManyToManyField(Parameter,
                                        through='ActionParameters')

    def __str__(self) -> str:
        """ This function returns a string describing the object.
        """
        return str(self.name)


class ActionParameters(models.Model):
    """Implement the Action Parameters model.

    An action has one or more parameters ordered by parameter_number.
    This model is used when adding an action to a rule to help
    the user set the parameters correctly.
    """
    action = models.ForeignKey(Action, on_delete=models.CASCADE)
    parameter_number = models.PositiveSmallIntegerField()
    parameter = models.ForeignKey(Parameter, on_delete=models.CASCADE)

    class Meta:
        """Define non-field attributes.
        
        (1) An action cannot have duplicate parameter numbers.
        (2) Change the plural name in the admin site.
        """
        models.UniqueConstraint(fields=['action', 'parameter_number'],
            name='unique_parameter_number')
        verbose_name_plural = 'Action Parameters'

    def __str__(self) -> str:
        """ This function returns a string describing the object.
        """
        return ('Action ' + str(self.action) + ', Parameter #'
            + str(self.parameter_number) + ': ' + str(self.parameter))


class Rule(models.Model):
    """Implement the Rule model.

    The criteria determine if the rule is applicable and if it is
    applicable, the actions determine what the engine will do.
    """
    name = models.CharField(max_length=30,
                            help_text='Descriptive text used to identify this '
                                + 'rule, from one to thirty characters in '
                                + 'length.')
    criteria = models.ManyToManyField(Criterion,
                            help_text='Select one or more items.<br> If all '
                                + 'of the selected items evaluate as true, '
                                + 'the rule is considered applicable and the '
                                + 'actions listed below are executed.')
    actions = models.ManyToManyField(Action, through='RuleActions')

    def __str__(self) -> str:
        """ This function returns a string describing the object.
        """
        return str(self.name)


class RuleActions(models.Model):
    """Implement the Rule Actions model.

    This model associates actions and their parameter values to a rule.
    """
    rule = models.ForeignKey(Rule, on_delete=models.CASCADE)
    action_number = models.PositiveSmallIntegerField(help_text='Enter a whole '
                                    + 'number greater than zero.<br>Actions '
                                    + 'are executed in numerical order, from '
                                    + 'lowest to highest.')
    action = models.ForeignKey(Action, on_delete=models.CASCADE,
                               help_text='Select an action to take when all '
                                + 'the criteria above evaluate as true.')
    parameters = models.ManyToManyField(Parameter,
                                        through='RuleActionParameters')

    class Meta:
        """Define non-field attributes.
        
        (1) A rule cannot have duplicate action numbers.
        (2) Default ordering is on action_number field. 
        """
        models.UniqueConstraint(fields=['rule', 'action_number'],
            name='unique_action_number')
        ordering = ['action_number']

    def __str__(self) -> str:
        """ This function returns a string describing the object.
        """
        return ('Rule ' + str(self.rule) + ', Action #'
            + str(self.action_number) + ': ' + str(self.action))

class RuleActionParameters(models.Model):
    """Implement the Rule Action Parameters model.

    This model associates parameters and their value to a rule's action.
    """
    rule_action = models.ForeignKey(RuleActions, on_delete=models.CASCADE)
    parameter = models.ForeignKey(Parameter, on_delete=models.CASCADE)
    parameter_value = models.TextField()

    def __str__(self) -> str:
        """ This function returns a string describing the object.
        """
        return (str(self.rule_action) + ', Parameter: ' + str(self.parameter)
                + ', Value: ' + str(self.parameter_value))
