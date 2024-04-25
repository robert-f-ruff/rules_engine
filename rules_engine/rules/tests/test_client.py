"""Define tests that simulate an end user."""
from django.contrib.staticfiles.testing import StaticLiveServerTestCase
from django.urls import reverse
import requests_mock
from selenium.webdriver import DesiredCapabilities
from testcontainers.selenium import BrowserWebDriverContainer
from rules.models import Rule, RuleActions, RuleActionParameters
from .pages.base import WrongPageError
from .pages.index import IndexPage
from .pages.rule_form import RuleFormPage, ParameterComponent


class ClientTestsEmptyDB(StaticLiveServerTestCase):
    """ Test the interface used by an end user.
    """
    fixtures = ['actions_parameters.json']

    @classmethod
    def setUpClass(cls) -> None:
        """ Initialize Selenium and the test server.
        """
        super().setUpClass()
        cls.firefox = BrowserWebDriverContainer(
            capabilities=DesiredCapabilities.FIREFOX,  # type: ignore
            image='selenium/standalone-firefox')
        cls.firefox.start()
        cls.selenium = cls.firefox.get_driver()

    @classmethod
    def tearDownClass(cls) -> None:
        """ Shutdown Selenium and the test server.
        """
        cls.selenium.quit()
        cls.firefox.stop()
        super().tearDownClass()

    def test_red_engine_status_area(self):
        """ Verify that the connection error is displayed.
        """
        self.selenium.get(str.replace(self.live_server_url, 'localhost', 'host.docker.internal')
                          + reverse('rules:index'))
        try:
            index_page = IndexPage(page_driver=self.selenium)
            self.assertEqual(index_page.get_engine_status_area_color(), 'red')
            self.assertEqual(index_page.get_engine_status(), 'Failed to establish a new connection:'
                             + ' [Errno 61] Connection refused')
            self.assertEqual(index_page.get_reload_status(), '')
            self.assertFalse(index_page.get_reload_button_visible())
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))

    def test_green_engine_status_area(self):
        """ Verify that the IDLE status is displayed.
        """
        with requests_mock.Mocker() as mock:
            mock.get('/rules_engine/engine/status', status_code=200, reason='OK',
                     json={'status': 'IDLE'})
            self.selenium.get(str.replace(self.live_server_url, 'localhost', 'host.docker.internal')
                              + reverse('rules:index'))
            try:
                index_page = IndexPage(page_driver=self.selenium)
                self.assertEqual(index_page.get_engine_status_area_color(), 'green')
                self.assertEqual(index_page.get_engine_status(), 'IDLE')
                self.assertEqual(index_page.get_reload_status(), '')
                self.assertFalse(index_page.get_reload_button_visible())
            except WrongPageError as error:
                self.fail('Wrong page address: ' + str(error))

    def test_no_rules_index(self):
        """ Verify the no rules message is displayed.
        """
        self.selenium.get(str.replace(self.live_server_url, 'localhost', 'host.docker.internal')
                          + reverse('rules:index'))
        try:
            index_page = IndexPage(page_driver=self.selenium)
            self.assertEqual(index_page.get_no_rules_message(), 'There are no rules to manage.')
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))

    def test_add_new_rule_link(self):
        """ Verify the Add New Rule link functions as expected.
        """
        self.selenium.get(str.replace(self.live_server_url, 'localhost', 'host.docker.internal')
                          + reverse('rules:index'))
        try:
            index_page = IndexPage(page_driver=self.selenium)
            index_page.click_add_rule()
            rule_editor = RuleFormPage(page_driver=self.selenium)
            self.assertEqual(rule_editor.get_url(),
                             str.replace(self.live_server_url, 'localhost', 'host.docker.internal')
                             + reverse('rules:add'),
                             'add rule url is incorrect')
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))

    def test_create_rule(self):
        """ Verify that a new rule is created.
        """
        self.selenium.get(str.replace(self.live_server_url, 'localhost', 'host.docker.internal')
                          + reverse('rules:index'))
        try:
            index_page = IndexPage(page_driver=self.selenium)
            index_page.click_add_rule()
            rule_editor = RuleFormPage(page_driver=self.selenium)
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))
        rule_editor.type_name('Test Rule 1')
        self.assertTrue(rule_editor.check_criterion('Is Pleasant'),
                        'checking Is Pleasant criterion')
        action_one = rule_editor.get_action_component(0)
        if action_one is None:
            self.fail('Action component 0 does not exist')
        action_one.type_action_number('1')
        action_one.select_action('Send Email')
        parameter_set = action_one.get_visible_parameter_component()
        if parameter_set is None:
            self.fail('First action has no visible parameter component')
        first_parameter = parameter_set.get_parameter_element(0)
        if first_parameter is None:
            self.fail('First action has no first parameter')
        first_parameter.type_parameter('george.jetson@spacely.zz')
        message = rule_editor.click_submit_button()
        self.assertIsNone(message)
        try:
            index_page = IndexPage(page_driver=self.selenium)
            self.assertEqual(index_page.get_rule(0), 'Test Rule 1',
                            'new rule appears in list of rules')
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))

    def test_reload_ruleset_button_success(self):
        """ Verify that when the Reload Ruleset button is pressed and the engine responds to
            communications that the engine status area is properly updated.
        """
        self.selenium.get(str.replace(self.live_server_url, 'localhost', 'host.docker.internal')
                          + reverse('rules:index'))
        try:
            index_page = IndexPage(page_driver=self.selenium)
            index_page.click_add_rule()
            rule_editor = RuleFormPage(page_driver=self.selenium)
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))
        rule_editor.type_name('Test Rule 1')
        self.assertTrue(rule_editor.check_criterion('Is Pleasant'),
                        'checking Is Pleasant criterion')
        action_one = rule_editor.get_action_component(0)
        if action_one is None:
            self.fail('Action component 0 does not exist')
        action_one.type_action_number('1')
        action_one.select_action('Send Email')
        parameter_set = action_one.get_visible_parameter_component()
        if parameter_set is None:
            self.fail('First action has no visible parameter component')
        first_parameter = parameter_set.get_parameter_element(0)
        if first_parameter is None:
            self.fail('First action has no first parameter')
        first_parameter.type_parameter('george.jetson@spacely.zz')
        message = rule_editor.click_submit_button()
        self.assertIsNone(message)
        try:
            index_page = IndexPage(page_driver=self.selenium)
            self.assertEqual(index_page.get_engine_status_area_color(), 'red')
            self.assertEqual(index_page.get_engine_status(), 'Failed to establish a new connection:'
                             + ' [Errno 61] Connection refused')
            self.assertEqual(index_page.get_reload_status(), 'Failed to establish a new connection:'
                                + ' [Errno 61] Connection refused')
            self.assertTrue(index_page.get_reload_button_visible())
            with requests_mock.Mocker() as mock:
                mock.get('/rules_engine/engine/status', status_code=200, reason='OK',
                        json={'status': 'IDLE'})
                mock.put('/rules_engine/engine/reload', status_code=200, reason='OK',
                        json={'status': 'OK'})
                index_page.click_reload_ruleset()
                self.assertEqual(index_page.get_engine_status_area_color(), 'green')
                self.assertEqual(index_page.get_engine_status(), 'IDLE')
                self.assertEqual(index_page.get_reload_status(), 'Ruleset successfully reloaded')
                self.assertFalse(index_page.get_reload_button_visible())
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))

    def test_reload_ruleset_button_failure(self):
        """ Verify that when the Reload Ruleset button is pressed and the engine does not respond
            to any communications that the engine status area remains the same.
        """
        self.selenium.get(str.replace(self.live_server_url, 'localhost', 'host.docker.internal')
                          + reverse('rules:index'))
        try:
            index_page = IndexPage(page_driver=self.selenium)
            index_page.click_add_rule()
            rule_editor = RuleFormPage(page_driver=self.selenium)
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))
        rule_editor.type_name('Test Rule 1')
        self.assertTrue(rule_editor.check_criterion('Is Pleasant'),
                        'checking Is Pleasant criterion')
        action_one = rule_editor.get_action_component(0)
        if action_one is None:
            self.fail('Action component 0 does not exist')
        action_one.type_action_number('1')
        action_one.select_action('Send Email')
        parameter_set = action_one.get_visible_parameter_component()
        if parameter_set is None:
            self.fail('First action has no visible parameter component')
        first_parameter = parameter_set.get_parameter_element(0)
        if first_parameter is None:
            self.fail('First action has no first parameter')
        first_parameter.type_parameter('george.jetson@spacely.zz')
        message = rule_editor.click_submit_button()
        self.assertIsNone(message)
        try:
            index_page = IndexPage(page_driver=self.selenium)
            self.assertEqual(index_page.get_engine_status_area_color(), 'red')
            self.assertEqual(index_page.get_reload_status(), 'Failed to establish a new connection'
                                + ': [Errno 61] Connection refused')
            self.assertTrue(index_page.get_reload_button_visible())
            index_page.click_reload_ruleset()
            self.assertEqual(index_page.get_engine_status_area_color(), 'red')
            self.assertEqual(index_page.get_reload_status(), 'Failed to establish a new connection'
                                + ': [Errno 61] Connection refused')
            self.assertTrue(index_page.get_reload_button_visible())
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))

    def test_reload_ruleset_button_partial_failure(self):
        """ Verify that the engine status area is red when the engine status is returned but the
            reload request failed.
        """
        with requests_mock.Mocker() as mock:
            mock.get('/rules_engine/engine/status', status_code=200, reason='OK',
                     json={'status': 'IDLE'})
            mock.put('/rules_engine/engine/reload', status_code=500,
                     reason='Internal Server Error')
            self.selenium.get(str.replace(self.live_server_url, 'localhost', 'host.docker.internal')
                              + reverse('rules:index'))
            try:
                index_page = IndexPage(page_driver=self.selenium)
                index_page.click_add_rule()
                rule_editor = RuleFormPage(page_driver=self.selenium)
            except WrongPageError as error:
                self.fail('Wrong page address: ' + str(error))
            rule_editor.type_name('Test Rule 1')
            self.assertTrue(rule_editor.check_criterion('Is Pleasant'),
                            'checking Is Pleasant criterion')
            action_one = rule_editor.get_action_component(0)
            if action_one is None:
                self.fail('Action component 0 does not exist')
            action_one.type_action_number('1')
            action_one.select_action('Send Email')
            parameter_set = action_one.get_visible_parameter_component()
            if parameter_set is None:
                self.fail('First action has no visible parameter component')
            first_parameter = parameter_set.get_parameter_element(0)
            if first_parameter is None:
                self.fail('First action has no first parameter')
            first_parameter.type_parameter('george.jetson@spacely.zz')
            message = rule_editor.click_submit_button()
            self.assertIsNone(message)
        try:
            index_page = IndexPage(page_driver=self.selenium)
            self.assertEqual(index_page.get_engine_status_area_color(), 'red')
            self.assertEqual(index_page.get_engine_status(), 'IDLE')
            self.assertEqual(index_page.get_reload_status(), 'Internal Server Error')
            self.assertTrue(index_page.get_reload_button_visible())
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))

    def test_invalid_text_number(self):
        """ Verify the rule form is returned with an error on the
            Send text to field.
        """
        self.selenium.get(str.replace(self.live_server_url, 'localhost', 'host.docker.internal')
                          + reverse('rules:index'))
        try:
            index_page = IndexPage(page_driver=self.selenium)
            index_page.click_add_rule()
            rule_editor = RuleFormPage(page_driver=self.selenium)
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))
        rule_editor.type_name('Test Rule 1')
        rule_editor.check_criterion('Is Pleasant')
        action_one = rule_editor.get_action_component(0)
        if action_one is None:
            self.fail('Action component 0 does not exist')
        action_one.type_action_number('1')
        action_one.select_action('Send Text Message')
        parameter_set = action_one.get_visible_parameter_component()
        if parameter_set is None:
            self.fail('First action has no visible parameter component')
        first_parameter = parameter_set.get_parameter_element(0)
        if first_parameter is None:
            self.fail('First action has no first parameter')
        first_parameter.type_parameter('555-444-1212')
        message = rule_editor.click_submit_button()
        self.assertIsNone(message)
        try:
            rule_editor = RuleFormPage(page_driver=self.selenium)
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))
        action_one = rule_editor.get_action_component(0)
        if action_one is None:
            self.fail('Action component 0 does not exist')
        parameter_set = action_one.get_visible_parameter_component()
        if parameter_set is None:
            self.fail('First action has no visible parameter component')
        first_parameter = parameter_set.get_parameter_element(0)
        if first_parameter is None:
            self.fail('First action has no first parameter')
        self.assertTrue(first_parameter.has_errors)
        self.assertEqual(first_parameter.get_error(0), 'Enter a valid value.')

    def test_add_action_fail(self):
        """ Verify that a blank action form is only added when the last action
            on the page is populated.
        """
        self.selenium.get(str.replace(self.live_server_url, 'localhost', 'host.docker.internal')
                          + reverse('rules:index'))
        try:
            index_page = IndexPage(page_driver=self.selenium)
            index_page.click_add_rule()
            rule_editor = RuleFormPage(page_driver=self.selenium)
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))
        rule_editor.type_name('Test Rule 1')
        rule_editor.check_criterion('Is Pleasant')
        self.assertFalse(rule_editor.click_add_action_button(),
                         'cannot add new action when last action is empty')

    def test_add_action_succeed(self):
        """ Verify that a blank action form is only added when the last action
            on the page is populated.
        """
        self.selenium.get(str.replace(self.live_server_url, 'localhost', 'host.docker.internal')
                          + reverse('rules:index'))
        try:
            index_page = IndexPage(page_driver=self.selenium)
            index_page.click_add_rule()
            rule_editor = RuleFormPage(page_driver=self.selenium)
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))
        rule_editor.type_name('Test Rule 1')
        rule_editor.check_criterion('Is Pleasant')
        action_one = rule_editor.get_action_component(0)
        if action_one is None:
            self.fail('Action component 0 does not exist')
        action_one.type_action_number('1')
        action_one.select_action('Send Email')
        parameter_set = action_one.get_visible_parameter_component()
        if parameter_set is None:
            self.fail('First action has no visible parameter component')
        first_parameter = parameter_set.get_parameter_element(0)
        if first_parameter is None:
            self.fail('First action has no first parameter')
        first_parameter.type_parameter('george.jetson@spacely.zz')
        self.assertTrue(rule_editor.click_add_action_button(),
                         'can add new action when last action is not empty')

    def test_blank_action_number(self):
        """ Verify an alert is displayed when the action number field is blank.
        """
        self.selenium.get(str.replace(self.live_server_url, 'localhost', 'host.docker.internal')
                          + reverse('rules:index'))
        try:
            index_page = IndexPage(page_driver=self.selenium)
            index_page.click_add_rule()
            rule_editor = RuleFormPage(page_driver=self.selenium)
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))
        rule_editor.type_name('Test Rule 1')
        rule_editor.check_criterion('Is Pleasant')
        action_one = rule_editor.get_action_component(0)
        if action_one is None:
            self.fail('Action component 0 does not exist')
        action_one.select_action('Send Email')
        parameter_set = action_one.get_visible_parameter_component()
        if parameter_set is None:
            self.fail('First action has no visible parameter component')
        parameter_one = parameter_set.get_parameter_element(0)
        if parameter_one is None:
            self.fail('First action has no first parameter')
        parameter_one.type_parameter('george.jetson@spacely.zz')
        message = rule_editor.click_submit_button()
        self.assertIsNone(message, 'unexpected alert generated')
        try:
            rule_editor = RuleFormPage(page_driver=self.selenium)
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))
        action_one = rule_editor.get_action_component(0)
        if action_one is None:
            self.fail('Action component 0 does not exist')
        self.assertTrue(action_one.action_number_has_errors)
        self.assertEqual(action_one.get_action_number_error(0), 'This field is required.')

    def test_duplicate_action_number(self):
        """ Verify an alert is displayed when an action number is duplicated
            on the form.
        """
        self.selenium.get(str.replace(self.live_server_url, 'localhost', 'host.docker.internal')
                          + reverse('rules:index'))
        try:
            index_page = IndexPage(page_driver=self.selenium)
            index_page.click_add_rule()
            rule_editor = RuleFormPage(page_driver=self.selenium)
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))
        rule_editor.type_name('Test Rule 1')
        rule_editor.check_criterion('Is Pleasant')
        action_one = rule_editor.get_action_component(0)
        if action_one is None:
            self.fail('Action component 0 does not exist')
        action_one.type_action_number('1')
        action_one.select_action('Send Email')
        parameter_set = action_one.get_visible_parameter_component()
        if parameter_set is None:
            self.fail('First action has no visible parameter component')
        parameter_one = parameter_set.get_parameter_element(0)
        if parameter_one is None:
            self.fail('First action has no first parameter')
        parameter_one.type_parameter('george.jetson@spacely.zz')
        action_added = rule_editor.click_add_action_button()
        self.assertTrue(action_added, 'Second action added to page')
        action_two = rule_editor.get_action_component(1)
        if action_two is None:
            self.fail('Action component 1 does not exist')
        action_two.type_action_number('2')
        action_two.select_action('Send Text Message')
        parameter_set = action_two.get_visible_parameter_component()
        if parameter_set is None:
            self.fail('Second action has no visible parameter component')
        parameter_one = parameter_set.get_parameter_element(0)
        if parameter_one is None:
            self.fail('Second action has no first parameter')
        parameter_one.type_parameter('(888) 444-1212')
        action_added = rule_editor.click_add_action_button()
        self.assertTrue(action_added, 'Third action added to page')
        action_three = rule_editor.get_action_component(2)
        if action_three is None:
            self.fail('Action component 2 does not exist')
        action_three.type_action_number('1')
        action_three.select_action('Send Email')
        parameter_set = action_three.get_visible_parameter_component()
        if parameter_set is None:
            self.fail('Third action has no visible parameter component')
        parameter_one = parameter_set.get_parameter_element(0)
        if parameter_one is None:
            self.fail('Third action has no first parameter')
        parameter_one.type_parameter('cosmo.spacely@spacely.zz')
        message = rule_editor.click_submit_button()
        self.assertEqual(message, 'Duplicate action number.\nAction execution '
                         + 'sequence #3: action #1 is already defined.')

    def test_blank_action(self):
        """ Verify an alert is displayed when no action is selected from the
            action dropdown.
        """
        self.selenium.get(str.replace(self.live_server_url, 'localhost', 'host.docker.internal')
                          + reverse('rules:index'))
        try:
            index_page = IndexPage(page_driver=self.selenium)
            index_page.click_add_rule()
            rule_editor = RuleFormPage(page_driver=self.selenium)
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))
        rule_editor.type_name('Test Rule 1')
        rule_editor.check_criterion('Is Pleasant')
        action_one = rule_editor.get_action_component(0)
        if action_one is None:
            self.fail('Action component 0 does not exist')
        action_one.type_action_number('1')
        message = rule_editor.click_submit_button()
        self.assertIsNone(message, 'unexpected alert generated')
        try:
            rule_editor = RuleFormPage(page_driver=self.selenium)
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))
        action_one = rule_editor.get_action_component(0)
        if action_one is None:
            self.fail('Action component 0 does not exist')
        self.assertTrue(action_one.action_select_has_errors)
        self.assertEqual(action_one.get_action_select_error(0), 'This field is required.')

    def test_no_criteria_checked(self):
        """ Verify that an alert is displayed if there are no criteria
            checked.
        """
        self.selenium.get(str.replace(self.live_server_url, 'localhost', 'host.docker.internal')
                          + reverse('rules:index'))
        try:
            index_page = IndexPage(page_driver=self.selenium)
            index_page.click_add_rule()
            rule_editor = RuleFormPage(page_driver=self.selenium)
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))
        rule_editor.type_name('Test Rule 1')
        action_one = rule_editor.get_action_component(0)
        if action_one is None:
            self.fail('Action component 0 does not exist')
        action_one.type_action_number('1')
        action_one.select_action('Send Email')
        parameter_set = action_one.get_visible_parameter_component()
        if parameter_set is None:
            self.fail('First action has no visible parameter component')
        first_parameter = parameter_set.get_parameter_element(0)
        if first_parameter is None:
            self.fail('First action has no first parameter')
        first_parameter.type_parameter('george.jetson@spacely.zz')
        message = rule_editor.click_submit_button()
        self.assertEqual(message, 'At least one criterion is required')

    def test_create_rule_empty_parameter(self):
        """ Verify that a parameter record is not created when an optional
            parameter has no value.
        """
        self.selenium.get(str.replace(self.live_server_url, 'localhost', 'host.docker.internal')
                          + reverse('rules:index'))
        try:
            index_page = IndexPage(page_driver=self.selenium)
            index_page.click_add_rule()
            rule_editor = RuleFormPage(page_driver=self.selenium)
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))
        rule_editor.type_name('Test Rule 1')
        self.assertTrue(rule_editor.check_criterion('Is Pleasant'),
                        'checking Is Pleasant criterion')
        action_one = rule_editor.get_action_component(0)
        if action_one is None:
            self.fail('Action component 0 does not exist')
        action_one.type_action_number('1')
        action_one.select_action('Send Email')
        parameter_set = action_one.get_visible_parameter_component()
        if parameter_set is None:
            self.fail('First action has no visible parameter component')
        first_parameter = parameter_set.get_parameter_element(0)
        if first_parameter is None:
            self.fail('First action has no first parameter')
        first_parameter.type_parameter('george.jetson@spacely.zz')
        message = rule_editor.click_submit_button()
        self.assertIsNone(message)
        try:
            index_page = IndexPage(page_driver=self.selenium)
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))
        rules = Rule.objects.filter(name='Test Rule 1')
        self.assertEqual(1, len(rules))
        rule_actions = RuleActions.objects.filter(rule=rules[0])
        self.assertEqual(rule_actions[0].action.name, 'Send Email')
        parameters = RuleActionParameters.objects.filter(rule_action=rule_actions[0])
        self.assertEqual(parameters.count(), 1)
        self.assertEqual(parameters[0].parameter_value, 'george.jetson@spacely.zz')

class ClientTestsPopulatedDB(StaticLiveServerTestCase):
    """ Test the interface used by an end user.
    """
    fixtures = ['test_client.json']

    @classmethod
    def setUpClass(cls) -> None:
        """ Initialize Selenium and the test server.
        """
        super().setUpClass()
        cls.firefox = BrowserWebDriverContainer(
            capabilities=DesiredCapabilities.FIREFOX,  # type: ignore
            image='selenium/standalone-firefox')
        cls.firefox.start()
        cls.selenium = cls.firefox.get_driver()

    @classmethod
    def tearDownClass(cls) -> None:
        """ Shutdown Selenium and the test server.
        """
        cls.selenium.quit()
        cls.firefox.stop()
        super().tearDownClass()

    def test_change_action(self):
        """ Verify parameter form is properly updated when an action is
            changed.
        """
        self.selenium.get(str.replace(self.live_server_url, 'localhost', 'host.docker.internal')
                          + reverse('rules:index'))
        try:
            index_page = IndexPage(page_driver=self.selenium)
            index_page.click_rule(0)
            rule_editor = RuleFormPage(page_driver=self.selenium)
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))
        action_one = rule_editor.get_action_component(0)
        if action_one is None:
            self.fail('Action component 0 does not exist')
        action_one.select_action('Send Text Message')
        parameter_set = action_one.get_visible_parameter_component()
        if parameter_set is None:
            self.fail('First action has no visible parameter component')
        self.assertEqual(parameter_set.get_component_type,
                        ParameterComponent.NEW_PARAMETER_FORM)
        action_one.select_action('Send Email')
        parameter_set = action_one.get_visible_parameter_component()
        if parameter_set is None:
            self.fail('First action has no visible parameter component')
        self.assertEqual(parameter_set.get_component_type,
                        ParameterComponent.EXISTING_PARAMETER_FORM)

    def test_change_action_save(self):
        """ Verify that a rule is updated when an action is changed."""
        self.selenium.get(str.replace(self.live_server_url, 'localhost', 'host.docker.internal')
                          + reverse('rules:index'))
        try:
            index_page = IndexPage(page_driver=self.selenium)
            index_page.click_rule(0)
            rule_editor = RuleFormPage(page_driver=self.selenium)
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))
        action_one = rule_editor.get_action_component(0)
        if action_one is None:
            self.fail('Action component 0 does not exist')
        action_one.select_action('Send Text Message')
        parameter_set = action_one.get_visible_parameter_component()
        if parameter_set is None:
            self.fail('First action has no visible parameter component')
        parameter_one= parameter_set.get_parameter_element(0)
        if parameter_one is None:
            self.fail('First action has no first parameter')
        parameter_one.type_parameter('(444) 877-1212')
        message = rule_editor.click_submit_button()
        self.assertIsNone(message, 'unexpected alert generated')
        try:
            index_page = IndexPage(page_driver=self.selenium)
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))
        rule_actions = RuleActions.objects.filter(rule=1)
        self.assertEqual(rule_actions[0].action.name, 'Send Text Message')
        parameters = RuleActionParameters.objects.filter(rule_action=rule_actions[0])
        self.assertEqual(parameters.count(), 1)
        self.assertEqual(parameters[0].parameter_value, '(444) 877-1212')

    def test_edit_rule_parameter_valid(self):
        """ Verify editing of a parameter works as expected.
        """
        self.selenium.get(str.replace(self.live_server_url, 'localhost', 'host.docker.internal')
                          + reverse('rules:index'))
        try:
            index_page = IndexPage(page_driver=self.selenium)
            index_page.click_rule(0)
            rule_editor = RuleFormPage(page_driver=self.selenium)
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))
        action_one = rule_editor.get_action_component(0)
        if action_one is None:
            self.fail('Action component 0 does not exist')
        parameter_set = action_one.get_visible_parameter_component()
        if parameter_set is None:
            self.fail('First action has no visible parameter component')
        parameter = parameter_set.get_parameter_element(1)
        if parameter is None:
            self.fail('First action has no second parameter')
        parameter.type_parameter('rosie@home.sky')
        message = rule_editor.click_submit_button()
        self.assertIsNone(message)
        try:
            index_page = IndexPage(page_driver=self.selenium)
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))
        parameter_record = RuleActionParameters.objects.get(pk=2)
        self.assertEqual(parameter_record.parameter_value, 'rosie@home.sky',
                         'updating Copy To parameter value')

    def test_edit_rule_parameter_invalid(self):
        """ Verify editing of a parameter works as expected.
        """
        self.selenium.get(str.replace(self.live_server_url, 'localhost', 'host.docker.internal')
                          + reverse('rules:index'))
        try:
            index_page = IndexPage(page_driver=self.selenium)
            index_page.click_rule(1)
            rule_editor = RuleFormPage(page_driver=self.selenium)
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))
        action_one = rule_editor.get_action_component(0)
        if action_one is None:
            self.fail('Action component 0 does not exist')
        parameter_set = action_one.get_visible_parameter_component()
        if parameter_set is None:
            self.fail('First action has no visible parameter component')
        parameter = parameter_set.get_parameter_element(0)
        if parameter is None:
            self.fail('First action has no second parameter')
        parameter.type_parameter('555-444-1212')
        message = rule_editor.click_submit_button()
        self.assertIsNone(message)
        try:
            rule_editor = RuleFormPage(page_driver=self.selenium)
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))
        action_one = rule_editor.get_action_component(0)
        if action_one is None:
            self.fail('Action component 0 does not exist')
        parameter_set = action_one.get_visible_parameter_component()
        if parameter_set is None:
            self.fail('First action has no visible parameter component')
        first_parameter = parameter_set.get_parameter_element(0)
        if first_parameter is None:
            self.fail('First action has no first parameter')
        self.assertTrue(first_parameter.has_errors)
        self.assertEqual(first_parameter.get_error(0), 'Enter a valid value.')

    def test_add_action_to_existing_rule(self):
        """ Verify adding an action to an existing rule works as expected."""
        self.selenium.get(str.replace(self.live_server_url, 'localhost', 'host.docker.internal')
                          + reverse('rules:index'))
        try:
            index_page = IndexPage(page_driver=self.selenium)
            index_page.click_rule(0)
            rule_editor = RuleFormPage(page_driver=self.selenium)
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))
        action_two = rule_editor.get_action_component(1)
        if action_two is None:
            self.fail('Action component 1 does not exist')
        action_two.type_action_number('2')
        action_two.select_action('Send Text Message')
        parameter_set = action_two.get_visible_parameter_component()
        if parameter_set is None:
            self.fail('Second action has no visible parameter component')
        first_parameter = parameter_set.get_parameter_element(0)
        if first_parameter is None:
            self.fail('Second action has no first parameter')
        first_parameter.type_parameter('(333) 999-1212')
        message = rule_editor.click_submit_button()
        self.assertIsNone(message)
        try:
            index_page = IndexPage(page_driver=self.selenium)
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))
        rule_actions = RuleActions.objects.filter(rule=1)
        self.assertEqual(rule_actions.count(), 2)
        self.assertEqual(rule_actions[1].action_number, 2)
        self.assertEqual(rule_actions[1].action.name, 'Send Text Message')
        parameters = RuleActionParameters.objects.filter(rule_action=rule_actions[1])
        self.assertEqual(parameters.count(), 1)
        self.assertEqual(parameters[0].parameter_value, '(333) 999-1212')

    def test_action_number_missing_sequence_first_delete(self):
        """ Verify that the first action is deleted and the new action is
            added.
        """
        self.selenium.get(str.replace(self.live_server_url, 'localhost', 'host.docker.internal')
                          + reverse('rules:index'))
        try:
            index_page = IndexPage(page_driver=self.selenium)
            index_page.click_rule(0)
            rule_editor = RuleFormPage(page_driver=self.selenium)
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))
        action_two = rule_editor.get_action_component(1)
        if action_two is None:
            self.fail('Action component 1 does not exist')
        action_two.type_action_number('2')
        action_two.select_action('Send Text Message')
        parameter_set = action_two.get_visible_parameter_component()
        if parameter_set is None:
            self.fail('First action has no visible parameter component')
        first_parameter = parameter_set.get_parameter_element(0)
        if first_parameter is None:
            self.fail('First action has no first parameter')
        first_parameter.type_parameter('(333) 999-1212')
        action_one = rule_editor.get_action_component(0)
        if action_one is None:
            self.fail('Action component 0 does not exist')
        action_one.check_action_delete()
        message = rule_editor.click_submit_button()
        self.assertIsNone(message)
        try:
            index_page = IndexPage(page_driver=self.selenium)
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))
        rule_actions = RuleActions.objects.filter(rule=1)
        self.assertEqual(rule_actions.count(), 1,
                         'should only be 1 RuleAction')
        self.assertEqual(rule_actions[0].action_number, 2,
                         'action number should be 2')
        self.assertEqual(rule_actions[0].action.name, 'Send Text Message',
                         'action should be Send Text Message')

    def test_action_number_missing_sequence_second_delete(self):
        """ Verify that the second action is deleted and the third action
            is added.
        """
        self.selenium.get(str.replace(self.live_server_url, 'localhost', 'host.docker.internal')
                          + reverse('rules:index'))
        try:
            index_page = IndexPage(page_driver=self.selenium)
            index_page.click_rule(2)
            rule_editor = RuleFormPage(page_driver=self.selenium)
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))
        action_three = rule_editor.get_action_component(2)
        if action_three is None:
            self.fail('Action component 2 does not exist')
        action_three.type_action_number('3')
        action_three.select_action('Send Text Message')
        parameter_set = action_three.get_visible_parameter_component()
        if parameter_set is None:
            self.fail('Third action has no visible parameter component')
        first_parameter = parameter_set.get_parameter_element(0)
        if first_parameter is None:
            self.fail('Third action has no first parameter')
        first_parameter.type_parameter('(333) 999-1212')
        action_two = rule_editor.get_action_component(1)
        if action_two is None:
            self.fail('Action component 1 does not exist')
        action_two.check_action_delete()
        message = rule_editor.click_submit_button()
        self.assertIsNone(message)
        try:
            index_page = IndexPage(page_driver=self.selenium)
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))
        rule_actions = RuleActions.objects.filter(rule=3)
        self.assertEqual(rule_actions.count(), 2,
                         'should only be 2 RuleActions')
        self.assertEqual(rule_actions[0].action_number, 1,
                         'first action number should be 1')
        self.assertEqual(rule_actions[0].action.name, 'Send Email',
                         'first action should be Send Email')
        self.assertEqual(rule_actions[1].action_number, 3,
                         'second action number should be 3')
        self.assertEqual(rule_actions[1].action.name, 'Send Text Message',
                         'second action should be Send Text Message')

    def test_save_prohibited_action_delete_param_change_exist(self):
        """ Verify that an error is displayed when an action is marked for
            deletion and its associated parameter value is changed.
        """
        self.selenium.get(str.replace(self.live_server_url, 'localhost', 'host.docker.internal')
                          + reverse('rules:index'))
        try:
            index_page = IndexPage(page_driver=self.selenium)
            index_page.click_rule(2)
            rule_editor = RuleFormPage(page_driver=self.selenium)
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))
        action_two = rule_editor.get_action_component(1)
        if action_two is None:
            self.fail('Action component 1 does not exist')
        action_two.check_action_delete()
        parameter_set = action_two.get_visible_parameter_component()
        if parameter_set is None:
            self.fail('Second action has no visible parameter component')
        first_parameter = parameter_set.get_parameter_element(0)
        if first_parameter is None:
            self.fail('Second action has no first parameter')
        first_parameter.type_parameter('(333) 999-1212')
        message = rule_editor.click_submit_button()
        self.assertIsNone(message)
        try:
            rule_editor = RuleFormPage(page_driver=self.selenium)
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))
        action_two = rule_editor.get_action_component(1)
        if action_two is None:
            self.fail('Action component 1 does not exist')
        parameter_set = action_two.get_visible_parameter_component()
        if parameter_set is None:
            self.fail('Second action has no visible parameter component')
        self.assertTrue(parameter_set.has_errors)
        self.assertEqual(parameter_set.get_error(0), 'Cannot change parameter and delete the '
        + 'associated action at the same time.', 'check for incompatible actions')

    def test_save_action_delete_param_change_new(self):
        """ Verify that an error is displayed when an action is marked for
            deletion and the action itself is changed.
        """
        self.selenium.get(str.replace(self.live_server_url, 'localhost', 'host.docker.internal')
                          + reverse('rules:index'))
        try:
            index_page = IndexPage(page_driver=self.selenium)
            index_page.click_rule(2)
            rule_editor = RuleFormPage(page_driver=self.selenium)
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))
        action_two = rule_editor.get_action_component(1)
        if action_two is None:
            self.fail('Action component 1 does not exist')
        action_two.check_action_delete()
        action_two.select_action('Send Email')
        parameter_set = action_two.get_visible_parameter_component()
        if parameter_set is None:
            self.fail('Second action has no visible parameter component')
        first_parameter = parameter_set.get_parameter_element(0)
        if first_parameter is None:
            self.fail('Second action has no first parameter')
        first_parameter.type_parameter('george.jetson@spacely.zz')
        message = rule_editor.click_submit_button()
        self.assertIsNone(message)
        try:
            rule_editor = RuleFormPage(page_driver=self.selenium)
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))
        action_two = rule_editor.get_action_component(1)
        if action_two is None:
            self.fail('Action component 1 does not exist')
        parameter_set = action_two.get_invisible_parameter_component()
        if parameter_set is None:
            self.fail('Second action has no visible parameter component')
        self.assertTrue(parameter_set.has_errors)
        self.assertEqual(parameter_set.get_error(0), 'Cannot change parameter and delete the '
        + 'associated action at the same time.', 'check for incompatible actions')

    def test_actions_sorted_ascending(self):
        """ Verify that actions are sorted by action number."""
        self.selenium.get(str.replace(self.live_server_url, 'localhost', 'host.docker.internal')
                          + reverse('rules:index'))
        try:
            index_page = IndexPage(page_driver=self.selenium)
            index_page.click_rule(3)
            rule_editor = RuleFormPage(page_driver=self.selenium)
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))
        action_one = rule_editor.get_action_component(0)
        if action_one is None:
            self.fail('Action component 0 does not exist')
        self.assertEqual(action_one.get_action_number(), 1,
                         'first action should be 1')
        action_two = rule_editor.get_action_component(1)
        if action_two is None:
            self.fail('Action component 1 does not exist')
        self.assertEqual(action_two.get_action_number(), 2,
                         'second action should be 2')
        action_three = rule_editor.get_action_component(2)
        if action_three is None:
            self.fail('Action component 2 does not exist')
        self.assertEqual(action_three.get_action_number(), 3,
                         'third action should be 3')

    def test_clear_optional_parameter(self):
        """ Verify that when an optional parameter value is cleared, its
            associated record in the database is deleted.
        """
        self.selenium.get(str.replace(self.live_server_url, 'localhost', 'host.docker.internal')
                          + reverse('rules:index'))
        try:
            index_page = IndexPage(page_driver=self.selenium)
            index_page.click_rule(0)
            rule_editor = RuleFormPage(page_driver=self.selenium)
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))
        action_one = rule_editor.get_action_component(0)
        if action_one is None:
            self.fail('Action component 0 does not exist')
        parameter_set = action_one.get_visible_parameter_component()
        if parameter_set is None:
            self.fail('First action has no visible parameter component')
        parameter = parameter_set.get_parameter_element(1)
        if parameter is None:
            self.fail('First action has no second parameter')
        parameter.type_parameter('')
        message = rule_editor.click_submit_button()
        self.assertIsNone(message)
        try:
            index_page = IndexPage(page_driver=self.selenium)
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))
        rule_actions = RuleActions.objects.filter(rule=1)
        self.assertEqual(rule_actions[0].action.name, 'Send Email')
        parameters = RuleActionParameters.objects.filter(rule_action=rule_actions[0])
        self.assertEqual(parameters.count(), 1)
        self.assertEqual(parameters[0].parameter_value, 'george.jetson@spacely.zz')
