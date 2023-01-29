"""Define the models used by the rules application."""
from django.db import models


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
        return str(self.name)


class Parameter(models.Model):
    """Implement the Parameter model.

    A parameter has a data type and a function that is called to
    validate its value. This model is used when adding an action to a
    rule.
    """
    name = models.CharField(max_length=30, primary_key=True)
    BOOLEAN = "BO"
    DATE_TIME = "DT"
    INTEGER = "IN"
    STRING = "ST"
    DATA_TYPE_CHOICES = [
        (BOOLEAN, 'Boolean'),
        (DATE_TIME, 'Date/Time'),
        (INTEGER, 'Integer'),
        (STRING, 'String'),
    ]
    data_type = models.CharField(max_length=2, choices=DATA_TYPE_CHOICES)
    validation_function = models.CharField(max_length=30)

    def __str__(self) -> str:
        return str(self.name)


class Action(models.Model):
    """Implement the Action model.

    An action determines the software function that is called and the
    parameters expected by that function. This model is used when
    adding an action to a rule and when the engine is running.
    """
    name = models.CharField(max_length=30, primary_key=True)
    function = models.CharField(max_length=30)
    parameters = models.ManyToManyField(Parameter, through='ActionParameters')

    def __str__(self) -> str:
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
        return ('Action ' + str(self.action) + ', Parameter #'
            + str(self.parameter_number) + ': ' + str(self.parameter))


class Rule(models.Model):
    """Implement the Rule model.

    The criteria determine if the rule is applicable and if it is
    applicable, the actions determine what the engine will do.
    """
    name = models.CharField(max_length=30, primary_key=True)
    criteria = models.ManyToManyField(Criterion)
    actions = models.ManyToManyField(Action, through='RuleActions')

    def __str__(self) -> str:
        return str(self.name)


class RuleActions(models.Model):
    """Implement the Rule Actions model.

    This model associates actions and their parameter values to a rule.
    """
    rule = models.ForeignKey(Rule, on_delete=models.CASCADE)
    action_number = models.PositiveSmallIntegerField()
    action = models.ForeignKey(Action, on_delete=models.CASCADE)
    parameter_values = models.TextField()

    class Meta:
        """Define non-field attributes.
        
        (1) A rule cannot have duplicate action numbers.
        """
        models.UniqueConstraint(fields=['rule', 'action_number'],
            name='unique_action_number')

    def __str__(self) -> str:
        return ('Rule ' + str(self.rule) + ', Action #'
            + str(self.action_number) + ': ' + str(self.action))
