"""Define custom template filters."""
import re
from django.template.defaulttags import register
from django.template.defaultfilters import stringfilter

@register.filter(is_safe=True)
@stringfilter
def get_id_number(id_string):
    """ This function will extract the id number from the given
        auto id string.
    """
    match = re.search(r'-(\d{1,})-', id_string)
    if match:
        return match.group(1)
    return ''

@register.filter(is_safe=True)
@stringfilter
def get_formset_name(id_string):
    """ This function will extract the name of a formset from the given
        auto id string.
    """
    match = re.search(r'^(.+)-\d{1,}-id$', id_string)
    if match:
        return match.group(1)
    return ''

@register.filter(is_safe=True)
def get_form_type(form):
    """ This function will return the type of the given form.
    """
    return type(form).__name__

@register.filter(is_safe=True)
def add_class(control, class_name):
    """ This function will add the specified css class to the
        control's list of css classes.
    """
    return control.as_widget(attrs={'class': control.css_classes(class_name)})
