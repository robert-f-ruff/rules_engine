"""Define the models that are managed by the admin site."""
from django.contrib import admin
from .models import Criterion, Parameter, Action, ActionParameters

admin.site.register([Criterion, Parameter,])


class ActionParametersInline(admin.TabularInline):
    """Display the ActionParameters model inline as a table."""
    model = ActionParameters
    extra = 1


class ActionAdmin(admin.ModelAdmin):
    """Define view for the Action model."""
    inlines = (ActionParametersInline,)


admin.site.register(Action, ActionAdmin)
