"""Define tests that simulate an end user."""
from django.contrib.staticfiles.testing import StaticLiveServerTestCase
from django.urls import reverse
from selenium.webdriver import Firefox, FirefoxOptions
from rules.models import RuleActions, RuleActionParameters
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
        options = FirefoxOptions()
        #options.add_argument('-headless')
        options.add_argument('-purgecaches')
        cls.selenium = Firefox(options=options)

    @classmethod
    def tearDownClass(cls) -> None:
        """ Shutdown Selenium and the test server.
        """
        cls.selenium.quit()
        super().tearDownClass()

    def test_no_rules_index(self):
        """ Verify the no rules message is displayed.
        """
        self.selenium.get(self.live_server_url + reverse('rules:index'))
        try:
            index_page = IndexPage(page_driver=self.selenium)
            self.assertEqual(index_page.get_no_rules_message(), 'There are no rules to manage.')
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))

    def test_add_new_rule_link(self):
        """ Verify the Add New Rule link functions as expected.
        """
        self.selenium.get(self.live_server_url + reverse('rules:index'))
        try:
            index_page = IndexPage(page_driver=self.selenium)
            index_page.click_add_rule()
            rule_editor = RuleFormPage(page_driver=self.selenium)
            self.assertEqual(rule_editor.get_url(),
                             self.live_server_url + reverse('rules:add'),
                             'add rule url is incorrect')
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))

    def test_create_rule(self):
        """ Verify that a new rule is created.
        """
        self.selenium.get(self.live_server_url + reverse('rules:index'))
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
        action_one.type_action_number('1')
        action_one.select_action('Send Email')
        parameter_set = action_one.get_visible_parameter_component()
        if parameter_set is not None:
            first_parameter = parameter_set.get_parameter_element(0)
            if first_parameter is None:
                self.fail('No first parameter')
            first_parameter.type_parameter('george.jetson@spacely.zz')
            rule_editor.click_submit_button()
            try:
                index_page = IndexPage(page_driver=self.selenium)
                self.assertEqual(index_page.get_rule(0), 'Test Rule 1',
                                'new rule appears in list of rules')
            except WrongPageError as error:
                self.fail('Wrong page address: ' + str(error))
        else:
            self.fail('No visible parameter component')

    def test_invalid_text_number(self):
        """ Verify the rule form is returned with an error on the
            Send text to field.
        """
        self.selenium.get(self.live_server_url + reverse('rules:index'))
        try:
            index_page = IndexPage(page_driver=self.selenium)
            index_page.click_add_rule()
            rule_editor = RuleFormPage(page_driver=self.selenium)
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))
        rule_editor.type_name('Test Rule 1')
        rule_editor.check_criterion('Is Pleasant')
        action_one = rule_editor.get_action_component(0)
        action_one.type_action_number('1')
        action_one.select_action('Send Text Message')
        parameter_set = action_one.get_visible_parameter_component()
        if parameter_set is None:
            self.fail('No visible parameter component')
        first_parameter = parameter_set.get_parameter_element(0)
        if first_parameter is None:
            self.fail('No first parameter')
        first_parameter.type_parameter('555-444-1212')
        rule_editor.click_submit_button()
        try:
            rule_editor = RuleFormPage(page_driver=self.selenium)
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))
        parameter_set = rule_editor.get_action_component(0).get_visible_parameter_component()
        if parameter_set is None:
            self.fail('No visible parameter component')
        first_parameter = parameter_set.get_parameter_element(0)
        if first_parameter is None:
            self.fail('No first parameter')
        self.assertTrue(first_parameter.has_errors)
        self.assertEqual(first_parameter.get_error(0), 'Enter a valid value.')

    def test_add_action_fail(self):
        """ Verify that a blank action form is only added when the last action
            on the page is populated.
        """
        self.selenium.get(self.live_server_url + reverse('rules:index'))
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
        self.selenium.get(self.live_server_url + reverse('rules:index'))
        try:
            index_page = IndexPage(page_driver=self.selenium)
            index_page.click_add_rule()
            rule_editor = RuleFormPage(page_driver=self.selenium)
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))
        rule_editor.type_name('Test Rule 1')
        rule_editor.check_criterion('Is Pleasant')
        action_one = rule_editor.get_action_component(0)
        action_one.type_action_number('1')
        action_one.select_action('Send Email')
        parameter_set = action_one.get_visible_parameter_component()
        if parameter_set is None:
            self.fail('No visible parameter component')
        first_parameter = parameter_set.get_parameter_element(0)
        if first_parameter is None:
            self.fail('No first parameter')
        first_parameter.type_parameter('george.jetson@spacely.zz')
        self.assertTrue(rule_editor.click_add_action_button(),
                         'can add new action when last action is not empty')

    def test_wrong_first_action_number(self):
        """ Verify that an alert is displayed on submit if the action number
            for the first action sequence is not 1.
        """
        self.selenium.get(self.live_server_url + reverse('rules:index'))
        try:
            index_page = IndexPage(page_driver=self.selenium)
            index_page.click_add_rule()
            rule_editor = RuleFormPage(page_driver=self.selenium)
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))
        rule_editor.type_name('Test Rule 1')
        rule_editor.check_criterion('Is Pleasant')
        action_one = rule_editor.get_action_component(0)
        action_one.type_action_number('2')
        action_one.select_action('Send Email')
        parameter_set = action_one.get_visible_parameter_component()
        if parameter_set is None:
            self.fail('No visible parameter component')
        first_parameter = parameter_set.get_parameter_element(0)
        if first_parameter is None:
            self.fail('No first parameter')
        first_parameter.type_parameter('george.jetson@spacely.zz')
        message = rule_editor.click_submit_button()
        self.assertEqual(message, 'Execution sequence #1 should be 1 not 2')

    def test_action_number_missing_sequence(self):
        """ Verify that an alert is displayed on submit if the action number
            for the second action sequence is not 2.
        """
        self.selenium.get(self.live_server_url + reverse('rules:index'))
        try:
            index_page = IndexPage(page_driver=self.selenium)
            index_page.click_add_rule()
            rule_editor = RuleFormPage(page_driver=self.selenium)
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))
        rule_editor.type_name('Test Rule 1')
        rule_editor.check_criterion('Is Pleasant')
        action_one = rule_editor.get_action_component(0)
        action_one.type_action_number('1')
        action_one.select_action('Send Email')
        parameter_set = action_one.get_visible_parameter_component()
        if parameter_set is None:
            self.fail('No visible parameter component')
        first_parameter = parameter_set.get_parameter_element(0)
        if first_parameter is None:
            self.fail('No first parameter')
        first_parameter.type_parameter('george.jetson@spacely.zz')
        action_added = rule_editor.click_add_action_button()
        self.assertTrue(action_added, 'Action added to page')
        action_two = rule_editor.get_action_component(1)
        action_two.type_action_number('3')
        action_two.select_action('Send Text Message')
        parameter_set = action_two.get_visible_parameter_component()
        if parameter_set is None:
            self.fail('No visible parameter component')
        first_parameter = parameter_set.get_parameter_element(0)
        if first_parameter is None:
            self.fail('No first parameter')
        first_parameter.type_parameter('(888) 888-8888')
        message = rule_editor.click_submit_button()
        self.assertEqual(message, 'Execution sequence #2 should be 2 not 3')

    def test_action_number_sequence_out_of_order(self):
        """ Verify that an alert is displayed on submit if the action number
            for the second action sequence is not 2 and the action number for
            the third action sequence is not 3.
        """
        self.selenium.get(self.live_server_url + reverse('rules:index'))
        try:
            index_page = IndexPage(page_driver=self.selenium)
            index_page.click_add_rule()
            rule_editor = RuleFormPage(page_driver=self.selenium)
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))
        rule_editor.type_name('Test Rule 1')
        rule_editor.check_criterion('Is Pleasant')
        action_one = rule_editor.get_action_component(0)
        action_one.type_action_number('1')
        action_one.select_action('Send Email')
        parameter_set = action_one.get_visible_parameter_component()
        if parameter_set is None:
            self.fail('No visible parameter component')
        first_parameter = parameter_set.get_parameter_element(0)
        if first_parameter is None:
            self.fail('No first parameter')
        first_parameter.type_parameter('george.jetson@spacely.zz')
        action_added = rule_editor.click_add_action_button()
        self.assertTrue(action_added, 'First action added to page')
        action_two = rule_editor.get_action_component(1)
        action_two.type_action_number('3')
        action_two.select_action('Send Text Message')
        parameter_set = action_two.get_visible_parameter_component()
        if parameter_set is None:
            self.fail('No visible parameter component')
        first_parameter = parameter_set.get_parameter_element(0)
        if first_parameter is None:
            self.fail('No first parameter')
        first_parameter.type_parameter('(888) 888-8888')
        action_added = rule_editor.click_add_action_button()
        self.assertTrue(action_added, 'Second action added to page')
        action_three = rule_editor.get_action_component(2)
        action_three.type_action_number('2')
        action_three.select_action('Send Email')
        parameter_set = action_three.get_visible_parameter_component()
        if parameter_set is None:
            self.fail('No visible parameter component')
        first_parameter = parameter_set.get_parameter_element(0)
        if first_parameter is None:
            self.fail('No first parameter')
        first_parameter.type_parameter('rosie@home.sky')
        message = rule_editor.click_submit_button()
        self.assertEqual(message, 'Execution sequence #2 should be 2 not 3')

    def test_no_criteria_checked(self):
        """ Verify that an alert is displayed if there are no criteria
            checked.
        """
        self.selenium.get(self.live_server_url + reverse('rules:index'))
        try:
            index_page = IndexPage(page_driver=self.selenium)
            index_page.click_add_rule()
            rule_editor = RuleFormPage(page_driver=self.selenium)
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))
        rule_editor.type_name('Test Rule 1')
        action_one = rule_editor.get_action_component(0)
        action_one.type_action_number('1')
        action_one.select_action('Send Email')
        parameter_set = action_one.get_visible_parameter_component()
        if parameter_set is None:
            self.fail('No visible parameter component')
        first_parameter = parameter_set.get_parameter_element(0)
        if first_parameter is None:
            self.fail('No first parameter')
        first_parameter.type_parameter('george.jetson@spacely.zz')
        message = rule_editor.click_submit_button()
        self.assertEqual(message, 'At least one criterion is required')


class ClientTestsPopulatedDB(StaticLiveServerTestCase):
    """ Test the interface used by an end user.
    """
    fixtures = ['test_client.json']

    @classmethod
    def setUpClass(cls) -> None:
        """ Initialize Selenium and the test server.
        """
        super().setUpClass()
        options = FirefoxOptions()
        #options.add_argument('-headless')
        options.add_argument('-purgecaches')
        cls.selenium = Firefox(options=options)

    @classmethod
    def tearDownClass(cls) -> None:
        """ Shutdown Selenium and the test server.
        """
        cls.selenium.quit()
        super().tearDownClass()

    def test_change_action(self):
        """ Verify parameter form is properly updated when an action is
            changed.
        """
        self.selenium.get(self.live_server_url + reverse('rules:index'))
        try:
            index_page = IndexPage(page_driver=self.selenium)
            index_page.click_rule(0)
            rule_editor = RuleFormPage(page_driver=self.selenium)
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))
        action_one = rule_editor.get_action_component(0)
        action_one.select_action('Send Text Message')
        parameter_set = action_one.get_visible_parameter_component()
        if parameter_set is None:
            self.fail('No visible parameter component')
        self.assertEqual(parameter_set.get_component_type,
                        ParameterComponent.NEW_PARAMETER_FORM)
        action_one.select_action('Send Email')
        parameter_set = action_one.get_visible_parameter_component()
        if parameter_set is None:
            self.fail('No visible parameter component')
        self.assertEqual(parameter_set.get_component_type,
                        ParameterComponent.EXISTING_PARAMETER_FORM)

    def test_edit_rule_parameter_valid(self):
        """ Verify editing of a parameter works as expected.
        """
        self.selenium.get(self.live_server_url + reverse('rules:index'))
        try:
            index_page = IndexPage(page_driver=self.selenium)
            index_page.click_rule(0)
            rule_editor = RuleFormPage(page_driver=self.selenium)
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))
        action_one = rule_editor.get_action_component(0)
        parameter_set = action_one.get_visible_parameter_component()
        if parameter_set is None:
            self.fail('No visible parameter component')
        parameter = parameter_set.get_parameter_element(1)
        if parameter is None:
            self.fail('No second parameter')
        parameter.type_parameter('rosie@home.sky')
        rule_editor.click_submit_button()
        parameter_record = RuleActionParameters.objects.get(pk=2)
        self.assertEqual(parameter_record.parameter_value, 'rosie@home.sky',
                         'updating Copy To parameter value')

    def test_edit_rule_parameter_invalid(self):
        """ Verify editing of a parameter works as expected.
        """
        self.selenium.get(self.live_server_url + reverse('rules:index'))
        try:
            index_page = IndexPage(page_driver=self.selenium)
            index_page.click_rule(1)
            rule_editor = RuleFormPage(page_driver=self.selenium)
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))
        action_one = rule_editor.get_action_component(0)
        parameter_set = action_one.get_visible_parameter_component()
        if parameter_set is None:
            self.fail('No visible parameter component')
        parameter = parameter_set.get_parameter_element(0)
        if parameter is None:
            self.fail('No second parameter')
        parameter.type_parameter('555-444-1212')
        rule_editor.click_submit_button()
        try:
            rule_editor = RuleFormPage(page_driver=self.selenium)
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))
        parameter_set = rule_editor.get_action_component(0).get_visible_parameter_component()
        if parameter_set is None:
            self.fail('No visible parameter component')
        first_parameter = parameter_set.get_parameter_element(0)
        if first_parameter is None:
            self.fail('No first parameter')
        self.assertTrue(first_parameter.has_errors)
        self.assertEqual(first_parameter.get_error(0), 'Enter a valid value.')

    def test_add_action_to_existing_rule(self):
        """ Verify adding an action to an existing rule works as expected."""
        self.selenium.get(self.live_server_url + reverse('rules:index'))
        try:
            index_page = IndexPage(page_driver=self.selenium)
            index_page.click_rule(0)
            rule_editor = RuleFormPage(page_driver=self.selenium)
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))
        action_two = rule_editor.get_action_component(1)
        action_two.type_action_number('2')
        action_two.select_action('Send Text Message')
        parameter_set = action_two.get_visible_parameter_component()
        if parameter_set is None:
            self.fail('No visible parameter component')
        first_parameter = parameter_set.get_parameter_element(0)
        if first_parameter is None:
            self.fail('No first parameter')
        first_parameter.type_parameter('(333) 999-1212')
        rule_editor.click_submit_button()
        try:
            index_page = IndexPage(page_driver=self.selenium)
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))
        rule_actions = RuleActions.objects.filter(rule=1)
        self.assertEqual(rule_actions.count(), 2)
        self.assertEqual(rule_actions[1].action_number, 2)
        self.assertEqual(rule_actions[1].action.name, 'Send Text Message')
        parameters = RuleActionParameters.objects.filter(rule_action=rule_actions[1])
        self.assertEqual(parameters[0].parameter_value, '(333) 999-1212')

    def test_action_number_missing_sequence_first_delete(self):
        """ Verify that an alert is displayed on submit if the action number
            following an action marked for delete is not in sequence.
        """
        self.selenium.get(self.live_server_url + reverse('rules:index'))
        try:
            index_page = IndexPage(page_driver=self.selenium)
            index_page.click_rule(0)
            rule_editor = RuleFormPage(page_driver=self.selenium)
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))
        action_two = rule_editor.get_action_component(1)
        action_two.type_action_number('2')
        action_two.select_action('Send Text Message')
        parameter_set = action_two.get_visible_parameter_component()
        if parameter_set is None:
            self.fail('No visible parameter component')
        first_parameter = parameter_set.get_parameter_element(0)
        if first_parameter is None:
            self.fail('No first parameter')
        first_parameter.type_parameter('(333) 999-1212')
        action_one = rule_editor.get_action_component(0)
        action_one.check_action_delete()
        message = rule_editor.click_submit_button()
        self.assertEqual(message, 'Execution sequence #2 should be 1 not 2')

    def test_action_number_missing_sequence_second_delete(self):
        """ Verify that an alert is displayed on submit if the action number
            following an action marked for delete is not in sequence.
        """
        self.selenium.get(self.live_server_url + reverse('rules:index'))
        try:
            index_page = IndexPage(page_driver=self.selenium)
            index_page.click_rule(2)
            rule_editor = RuleFormPage(page_driver=self.selenium)
        except WrongPageError as error:
            self.fail('Wrong page address: ' + str(error))
        action_three = rule_editor.get_action_component(2)
        action_three.type_action_number('3')
        action_three.select_action('Send Text Message')
        parameter_set = action_three.get_visible_parameter_component()
        if parameter_set is None:
            self.fail('No visible parameter component')
        first_parameter = parameter_set.get_parameter_element(0)
        if first_parameter is None:
            self.fail('No first parameter')
        first_parameter.type_parameter('(333) 999-1212')
        action_two = rule_editor.get_action_component(1)
        action_two.check_action_delete()
        message = rule_editor.click_submit_button()
        self.assertEqual(message, 'Execution sequence #3 should be 2 not 3')
