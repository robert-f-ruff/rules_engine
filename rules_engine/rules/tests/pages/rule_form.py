"""Define the rule editor page object model."""
import re
import time
from abc import ABC, abstractmethod
from typing import List
from selenium.common.exceptions import (NoSuchElementException, TimeoutException,
                                        MoveTargetOutOfBoundsException,
                                        ElementNotInteractableException,
                                        NoAlertPresentException)
from selenium.webdriver import Firefox
from selenium.webdriver.common.action_chains import ActionChains
from selenium.webdriver.common.by import By
from selenium.webdriver.remote.webelement import WebElement
from selenium.webdriver.support.select import Select
from selenium.webdriver.support.wait import WebDriverWait
from selenium.webdriver.support.relative_locator import locate_with
from .base import BasePage, WrongPageError


def parse_errors(page_driver: Firefox, element: str) -> list[str]:
    """ This function parses the specified error list into a list of
        strings.
    """
    error_list = []
    try:
        errors = page_driver.find_element(by=By.ID, value=element)
        for error in errors.find_elements(by=By.TAG_NAME, value='li'):
            error_list.append(error.text)
    except NoSuchElementException:
        pass
    return error_list

def prepare_to_click(page_driver: Firefox, element: str) -> None:
    """ This function ensures the requested element is visible in the
        viewport.
    """
    try:
        component = page_driver.find_element(by=By.ID,
                                                value=element)
        ActionChains(page_driver).scroll_to_element(component).perform()
    except MoveTargetOutOfBoundsException:
        script = f'document.getElementById("{element}").scrollIntoView();'
        page_driver.execute_script(script)
        time.sleep(2)

def move_send_keys(page_driver: Firefox, element: WebElement, text: str) -> None:
    """ This function will type the specified text into the specified element,
        scrolling the page if necessary.
    """
    try:
        element.clear()
        element.send_keys(text)
    except ElementNotInteractableException:
        prepare_to_click(page_driver, element.get_attribute('id'))
        element.clear()
        element.send_keys(text)
    time.sleep(2)

def move_select(page_driver: Firefox, element: Select, element_id: str, item: str) -> None:
    """ This function will select the specified item from the specified
        drop-down, scrolling the page if necessary.
    """
    try:
        element.select_by_visible_text(item)
    except ElementNotInteractableException:
        prepare_to_click(page_driver, element_id)
        element.select_by_visible_text(item)
    time.sleep(2)

def move_click(page_driver: Firefox, element: WebElement) -> None:
    """ This function will click the specified item, scrolling the page if
        necessary.
    """
    try:
        element.click()
    except ElementNotInteractableException:
        prepare_to_click(page_driver, element.get_attribute('id'))
        element.click()
    time.sleep(2)


class ParameterElement():
    """ This class defines the interface for interacting with a single
        parameter on the rule editor page.
    """
    def __init__(self, page_driver: Firefox, action_instance: int = 0,
                 parameter_instance: int = 0, param_set_id: int = 0) -> None:
        self._driver = page_driver
        if param_set_id == 0:
            control_name = (f'id_new_parameter_form-{str(action_instance)}'
                            + f'-parameter_value-{str(parameter_instance)}')
        elif param_set_id > 0:
            control_name = f'id_param{str(param_set_id)}-{str(parameter_instance)}-parameter_value'
        else:
            control_name = ''
        if control_name != '':
            self._parameter_input = page_driver.find_element(by=By.ID,
                                                             value=control_name)
            if not re.search(r' d-none', self._parameter_input.get_attribute('class')):
                label_location = locate_with(
                    By.TAG_NAME, 'label').above(self._parameter_input) # type: ignore
                self._parameter_label = page_driver.find_element(label_location) # type: ignore
            else:
                self._parameter_label = None
            self._parameter_errors = parse_errors(
                page_driver, control_name + '_errors')
        else:
            self._parameter_input = None
            self._parameter_label = None
            self._parameter_errors = []

    def type_parameter(self, value: str) -> None:
        """ This function enters the given text into the parameter input."""
        if self._parameter_input is not None:
            move_send_keys(self._driver, self._parameter_input, value)

    def get_label(self) -> str:
        """ This function returns the label for this parameter."""
        if self._parameter_label is not None:
            return self._parameter_label.text
        return ''

    @property
    def has_errors(self) -> bool:
        """ This function returns whether this element has any server errors."""
        if len(self._parameter_errors) > 0:
            return True
        return False

    def get_error(self, number: int) -> str:
        """ This function returns the requested server error."""
        if number < len(self._parameter_errors):
            return self._parameter_errors[number]
        return ''

    @property
    def get_visible(self) -> bool:
        """ This function returns whether the control is visible on the page."""
        if self._parameter_input is not None:
            match = re.search(r' d-none', self._parameter_input.get_attribute('class'))
            if not match:
                return True
        return False


class ParameterComponent(ABC):
    """ This class defines an interface that the two types of parameter
        components must implement.
    """
    NEW_PARAMETER_FORM = 'npf'
    EXISTING_PARAMETER_FORM = 'epf'
    @abstractmethod
    def __init__(self, page_driver: Firefox, action_instance: int) -> None:
        self._driver = page_driver
        self._form_type = ''
        self._parameters: List[ParameterElement] = []
        self._component_errors = []

    def get_parameter_element(self, instance: int) -> ParameterElement | None:
        """ This function returns the specified parameter element."""
        if instance < len(self._parameters):
            return self._parameters[instance]
        return None

    @property
    def get_component_type(self) -> str:
        """ This function returns the form type of this component."""
        return self._form_type

    @property
    def is_visible(self) -> bool:
        """ This function returns whether this component has any visible
            parameter input controls.
        """
        is_visible = False
        for element in self._parameters:
            if element.get_visible:
                is_visible = True
                break
        return is_visible

    @property
    def has_errors(self) -> bool:
        """ This function returns whether this component has any server errors."""
        if len(self._component_errors) > 0:
            return True
        return False

    def get_error(self, number: int) -> str:
        """ This function returns the requested server error."""
        if number < len(self._component_errors):
            return self._component_errors[number]
        return ''


class NewParameterComponent(ParameterComponent):
    """ This class defines the layout and interface to interact with a
        parameter component that is not associated with a record in the
        database (i.e., the ActionParameterForm)
    """
    def __init__(self, page_driver: Firefox, action_instance: int) -> None:
        super().__init__(page_driver, action_instance)
        self._form_type = ParameterComponent.NEW_PARAMETER_FORM
        try:
            element_name = f'action_parameter_{str(action_instance)}_ActionParameterForm'
            form_start_element = WebDriverWait(page_driver, timeout=3).until(
                lambda driver: driver.find_element(by=By.ID, value=element_name))
            try:
                input_id = f'id_new_parameter_form-{str(action_instance)}-parameter_count'
                parameter_count_input = self._driver.find_element(by=By.ID, value=input_id)
                parameter_count = int(parameter_count_input.get_attribute('value'))
                for parameter in range(1, (parameter_count + 1)):
                    self._parameters.append(ParameterElement(page_driver=page_driver,
                                                            action_instance=action_instance,
                                                            parameter_instance=parameter))
                errors_element = form_start_element.find_element(
                    by=By.TAG_NAME, value='div')
                if errors_element.get_attribute('class') == 'alert alert-danger' and errors_element.get_attribute('id') == '':
                    for error in errors_element.find_elements(by=By.TAG_NAME, value='li'):
                        self._component_errors.append(error.text)
            except NoSuchElementException:
                pass
        except TimeoutException:
            pass


class ExistingParameterComponent(ParameterComponent):
    """ This class defines the layout and interface to interact with a
        parameter component that is associated wtih records in the database,
        one record per parameter.
        (i.e., the ActionParametersFormSet)
    """
    def __init__(self, page_driver: Firefox, action_instance: int) -> None:
        super().__init__(page_driver, action_instance)
        self._form_type = ParameterComponent.EXISTING_PARAMETER_FORM
        try:
            form_start_element = page_driver.find_element(
                by=By.ID,
                value=f'action_parameter_{str(action_instance)}_RuleActionParametersFormFormSet')
            input_id = f'id_ruleactions_set-{str(action_instance)}-id'
            record_id_input = self._driver.find_element(by=By.ID, value=input_id)
            param_set_id = record_id_input.get_attribute('value')
            if param_set_id != '':
                input_id = f'id_param{param_set_id}-TOTAL_FORMS'
                parameter_count_input = self._driver.find_element(by=By.ID, value=input_id)
                parameter_count = int(
                    parameter_count_input.get_attribute('value'))
            else:
                parameter_count = 0
            for parameter in range(0, parameter_count):
                self._parameters.append(ParameterElement(page_driver=page_driver,
                                                            parameter_instance=parameter,
                                                            param_set_id=int(param_set_id)))
            errors_element = form_start_element.find_element(
                by=By.ID, value=f'id_param{param_set_id}_errors')
            for error in errors_element.find_elements(by=By.TAG_NAME, value='li'):
                self._component_errors.append(error.text)
        except NoSuchElementException:
            pass

def parameter_component_factory(page_driver: Firefox,
                                action_instace: int) -> list[ParameterComponent]:
    """ This function creates the ParameterComponent array. A page can have
        both types of parameter forms.
    """
    components = []
    components.append(NewParameterComponent(page_driver, action_instace))
    components.append(ExistingParameterComponent(page_driver, action_instace))
    return components


class ActionComponent():
    """ This class defines the interface for interacting with one action on
        the rule editor page.
    """

    def __init__(self, page_driver: Firefox, instance: int) -> None:
        self._driver = page_driver
        self._instance = instance
        prefix = f'id_ruleactions_set-{str(instance)}-'
        self._component_errors = parse_errors(page_driver, prefix + 'errors')
        self._action_number_input = self._driver.find_element(by=By.ID,
                                                              value=prefix + 'action_number')
        self._action_number_errors = parse_errors(page_driver,
                                                  prefix + 'action_number_errors')
        self._action_select = self._driver.find_element(by=By.ID,
                                                        value=prefix + 'action')
        self._action_select_errors = parse_errors(
            page_driver, prefix + 'action_errors')
        self._action_delete_input = self._driver.find_element(by=By.ID,
                                                              value=prefix + 'DELETE')
        self._parameters = parameter_component_factory(page_driver, instance)

    def type_action_number(self, action_number: str) -> None:
        """ This function enters the given text into the specified action
            number input.
        """
        move_send_keys(self._driver, self._action_number_input, action_number)

    def get_action_number(self) -> int:
        """ This function returns the action number of this component."""
        return int(self._action_number_input.get_attribute('value'))

    @property
    def action_number_has_errors(self) -> bool:
        """ This function returns whether this component has any server errors."""
        if len(self._action_number_errors) > 0:
            return True
        return False

    def get_action_number_error(self, number: int) -> str:
        """ This function returns the requested server error."""
        if number < len(self._action_number_errors):
            return self._action_number_errors[number]
        return ''

    def select_action(self, action: str) -> None:
        """ This function selects the specified action in the action dropdown.
        """
        selector = Select(self._action_select)
        move_select(self._driver, selector, self._action_select.get_attribute('id'), action)
        self._parameters = parameter_component_factory(self._driver, self._instance)

    @property
    def action_select_has_errors(self) -> bool:
        """ This function returns whether this component has any server errors."""
        if len(self._action_select_errors) > 0:
            return True
        return False

    def get_action_select_error(self, number: int) -> str:
        """ This function returns the requested server error."""
        if number < len(self._action_select_errors):
            return self._action_select_errors[number]
        return ''

    def check_action_delete(self) -> None:
        """ This function clicks on the Delete checkbox for the specified
            action.
        """
        move_click(self._driver, self._action_delete_input)

    def is_action_delete_checked(self) -> bool:
        """ This function returns whetehr the specified action's Delete
            checkbox is checked.
        """
        return self._action_delete_input.is_selected()

    @property
    def has_errors(self) -> bool:
        """ This function returns whether this component has any server errors."""
        if len(self._component_errors) > 0:
            return True
        return False

    def get_error(self, number: int) -> str:
        """ This function returns the requested server error."""
        if number < len(self._component_errors):
            return self._component_errors[number]
        return ''

    def get_visible_parameter_component(self) -> ParameterComponent | None:
        """ This function returns the visible parameter form within this action form.
        """
        for component in self._parameters:
            if component.is_visible:
                return component
        return None

    def get_invisible_parameter_component(self) -> ParameterComponent | None:
        """ This function returns the visible parameter form within this action form."""
        for component in self._parameters:
            if not component.is_visible:
                return component
        return None


class RuleFormPage(BasePage):
    """ This class defines the interface for interacting with the rule editor
        page.
    """

    def __init__(self, page_driver: Firefox) -> None:
        super().__init__(page_driver=page_driver)
        try:
            self._submit_button = WebDriverWait(page_driver, timeout=3).until(
                lambda driver: driver.find_element(by=By.ID, value='submit_button'))
        except TimeoutException as error:
            raise WrongPageError(page_driver.current_url,
                                 page_name='Rule Editor') from error
        if page_driver.title != 'Rule Editor':
            raise WrongPageError(page_driver.current_url,
                                 page_name='Rule Editor')
        self._form_errors = parse_errors(self._driver, 'rule_form_errors')
        self._rule_name_errors = parse_errors(self._driver, 'id_name_errors')
        self._criteria = {}
        checks = page_driver.find_element(by=By.ID, value='id_criteria')
        for check_box in checks.find_elements(by=By.TAG_NAME, value='input'):
            self._criteria[check_box.get_attribute('value')] = check_box
        self._criteria_errors = parse_errors(self._driver, 'criteria_errors')
        self._form_count_input = page_driver.find_element(by=By.ID,
                                                          value='id_ruleactions_set-TOTAL_FORMS')
        self._action_forms = []
        for form in range(0, int(self._form_count_input.get_attribute('value'))):
            self._action_forms.append(ActionComponent(page_driver, form))

    @property
    def has_errors(self) -> bool:
        """ This function returns whether the form has any server errors."""
        if len(self._form_errors) > 0:
            return True
        return False

    def get_error(self, number: int) -> str:
        """ This function returns the requested server error."""
        if number < len(self._form_errors):
            return self._form_errors[number]
        return ''

    def type_name(self, rule_name: str) -> None:
        """ This function enters the given text into the name input."""
        rule_name_input = self._driver.find_element(by=By.ID, value='id_name')
        move_send_keys(self._driver, rule_name_input, rule_name)

    @property
    def name_has_errors(self) -> bool:
        """ This function returns whether the rule name element has any server errors."""
        if len(self._rule_name_errors) > 0:
            return True
        return False

    def get_name_error(self, number: int) -> str:
        """ This function returns the requested server error."""
        if number < len(self._rule_name_errors):
            return self._rule_name_errors[number]
        return ''

    def check_criterion(self, criterion: str) -> bool:
        """ This function clicks on the specified criterion."""
        if criterion in self._criteria:
            move_click(self._driver, self._criteria[criterion])
            return True
        return False

    @property
    def criteria_has_errors(self) -> bool:
        """ This function returns whether the rule name element has any server errors."""
        if len(self._criteria_errors) > 0:
            return True
        return False

    def get_criteria_error(self, number: int) -> str:
        """ This function returns the requested server error."""
        if number < len(self._criteria_errors):
            return self._criteria_errors[number]
        return ''

    def is_criterion_checked(self, criterion: str) -> bool:
        """ This function returns whether the specified criterion is checked."""
        if criterion in self._criteria:
            return self._criteria[criterion].is_selected()
        return False

    def get_action_component(self, form_index: int) -> ActionComponent | None:
        """ This function returns the specified action fieldset."""
        if form_index < len(self._action_forms):
            return self._action_forms[form_index]
        return None

    def click_add_action_button(self) -> bool:
        """ This function clicks on the Add Action button."""
        form_count = self._form_count_input.get_attribute('value')
        prepare_to_click(self._driver, 'add_action_button')
        add_action_button = self._driver.find_element(by=By.ID,
                                                      value='add_action_button')
        move_click(self._driver, add_action_button)
        if form_count != self._form_count_input.get_attribute('value'):
            self._action_forms.append(ActionComponent(self._driver,
                            int(self._form_count_input.get_attribute('value')) - 1))
            return True
        return False

    def click_submit_button(self) -> str | None:
        """ This function clicks on the Save button."""
        move_click(self._driver, self._submit_button)
        message = None
        try:
            alert = self._driver.switch_to.alert
            message = alert.text
            alert.accept()
        except NoAlertPresentException:
            pass
        return message
