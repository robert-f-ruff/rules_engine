"""Define tests for the views."""
from django.test import TestCase
from django.urls import reverse
from rules.models import Rule, RuleActions, RuleActionParameters


class ParametersViewTests(TestCase):
    """ Test the parameters() view function.
    """
    fixtures = ['view_tests.json']

    def test_invalid_http_method(self):
        """ The parameters view only responds to a certain type of HTTP
            request method.
        """
        url = reverse('rules:action_parameters', kwargs={'action_name': 'Send Email'})
        data = {'ruleaction_set-id': '0', 'ruleaction-id': ''}
        response = self.client.post(url, data)
        self.assertEqual(response.status_code, 405)

    def test_valid_http_method(self):
        """ The parameters view only responds to a certain type of HTTP
            request method.
        """
        url = reverse('rules:action_parameters', kwargs={'action_name': 'Send Email'})
        response = self.client.get(url + '?ruleaction_set-id=0&ruleaction-id=')
        self.assertEqual(response.status_code, 200)

    def test_return_content_is_json(self):
        """ The parameters view should return data in the JSON format.
        """
        url = reverse('rules:action_parameters', kwargs={'action_name': 'Send Email'})
        response = self.client.get(url + '?ruleaction_set-id=0&ruleaction-id=')
        self.assertEqual(response.headers['Content-Type'], 'application/json', # type: ignore
                         'return data in JSON format')

    def test_non_blank_parameter_form_returned(self):
        """ The parameters view should not return a blank parameter form when a
            matching RuleAction record exists.
        """
        url = reverse('rules:action_parameters', kwargs={'action_name': 'Send Text Message'})
        response = self.client.get(url + '?ruleaction_set-id=0&ruleaction-id=2')
        self.assertIsNot(response.json()['parameter_form'], '', 'view should return a form')

    def test_blank_parameter_form_returned(self):
        """ The parameters view should return a blank parameter form when a
            matching RuleAction record does not exist.
        """
        url = reverse('rules:action_parameters', kwargs={'action_name': 'Send Email'})
        response = self.client.get(url + '?ruleaction_set-id=0&ruleaction-id=2')
        self.assertEqual(response.json()['parameter_form'], '', 'view should not return a form')


class IndexViewTests(TestCase):
    """ Test the IndexView class.
    """
    def test_no_rules(self):
        """ When there are no rules, the appropriate message is displayed.
        """
        response = self.client.get(reverse('rules:index'))
        self.assertEqual(response.status_code, 200)
        self.assertContains(response, 'There are no rules to manage.')
        self.assertQuerysetEqual(response.context['rule_list'], [])

    def test_rules(self):
        """ When there is one or more rules, the rule(s) are displayed.
        """
        rule_one = Rule.objects.create(name='Test Rule 1')
        response = self.client.get(reverse('rules:index'))
        self.assertEqual(response.status_code, 200)
        self.assertQuerysetEqual(qs=response.context['rule_list'], values=[rule_one]) # type: ignore


class RuleDeleteViewTests(TestCase):
    """ Test the RuleDeleteView class.
    """
    fixtures = ['view_tests.json']

    def test_confirm_delete(self):
        """ Verify the confirm page is returned.
        """
        url = reverse('rules:delete', kwargs={'pk': 2})
        response = self.client.get(url)
        self.assertEqual(response.status_code, 200)
        self.assertContains(response, 'Are you sure you want to delete ')

    def test_successful_delete(self):
        """ Delete the specified rule.
        """
        url = reverse('rules:delete', kwargs={'pk': 2})
        response = self.client.post(url)
        self.assertRedirects(response, reverse('rules:index'))
        self.assertEqual(Rule.objects.count(), 0)
        self.assertEqual(RuleActions.objects.count(), 0)
        self.assertEqual(RuleActionParameters.objects.count(), 0)

class RuleViewTests(TestCase):
    """ Test the RuleView class.
    """
    fixtures = ['view_tests.json']

    def setUp(self) -> None:
        super().setUp()
        self._post_data = {
            'name': 'Test Rule 4',
            'criteria': 'Is Pleasant',
            'ruleactions_set-TOTAL_FORMS': '1',
            'ruleactions_set-INITIAL_FORMS': '0',
            'ruleactions_set-MIN_NUM_FORMS': '0',
            'ruleactions_set-MAX_NUM_FORMS': '1000',
            'ruleactions_set-0-id': '',
            'ruleactions_set-0-rule': '',
            'ruleactions_set-0-action_number': '1',
            'ruleactions_set-0-action': 'Send Email',
            'new_parameter_form-0-parameter_name-1': 'Send email to',
            'new_parameter_form-0-parameter_name-2': 'Copy email to',
            'new_parameter_form-0-parameter_count': '2',
            'new_parameter_form-0-parameter_value-1': 'george.jetson@spacely.zz',
            'new_parameter_form-0-parameter_value-2': ''
        }

    def test_add_form(self):
        """ Present the user with an empty rule form.
        """
        response = self.client.get(reverse('rules:add'))
        self.assertEqual(response.status_code, 200)
        self.assertTemplateUsed(response, 'rules/rule_create.html')
        self.assertTemplateNotUsed(response, 'rules/rule_edit.html')

    def test_edit_form(self):
        """ Present the user with a populated rule form.
        """
        url = reverse('rules:edit', kwargs={'rule_id': 2})
        response = self.client.get(url)
        self.assertEqual(response.status_code, 200)
        self.assertTemplateUsed(response, 'rules/rule_edit.html')
        self.assertTemplateNotUsed(response, 'rules/rule_create.html')

    def test_edit_form_nonexistent_rule(self):
        """ Return a HTTP 404 response for a rule that doesn't exist.
        """
        url = reverse('rules:edit', kwargs={'rule_id': 50})
        response = self.client.get(url)
        self.assertEqual(response.status_code, 404)

    def test_rule_form_error(self):
        """ Return the rule form with an error.
        """
        self._post_data['name'] = 'Test Rule Test Rule Really Long Test Rule'
        url = reverse('rules:add')
        response = self.client.post(url, self._post_data)
        self.assertEqual(response.status_code, 200)
        self.assertContains(response,
                        '<div id="id_name_errors" class="alert alert-danger">')
        self.assertContains(response,
                            '<li>Ensure this value has at most 30 characters '
                            + '(it has 41).</li>')

    def test_action_form_error(self):
        """ Return the rule form with an error.
        """
        self._post_data['ruleactions_set-0-action_number'] = ''
        url = reverse('rules:add')
        response = self.client.post(url, self._post_data)
        self.assertEqual(response.status_code, 200)
        self.assertContains(response,
                        '<div id="id_ruleactions_set-0-action_number_errors" '
                        + 'class="alert alert-danger">')
        self.assertContains(response,
                            '<li>This field is required.</li>')
