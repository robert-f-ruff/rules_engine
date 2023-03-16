"""Define tests for the template filters."""
import unittest
from rules.templatetags.rule_extras import get_id_number, get_formset_name


class TestRuleExtras(unittest.TestCase):
    """ Test the custom template filters.
    """
    def test_no_id_number(self):
        """Verify that a non-match returns an empty string."""
        self.assertEqual(get_id_number('this-is-not-a-match_24_doh'), '')
        self.assertEqual(get_id_number('not_2-a_match_either'), '')

    def test_no_formset_name(self):
        """Verify that a non-match returns an empty string."""
        self.assertEqual(get_formset_name('some_thing_does-not-match-id'), '')
        self.assertEqual(get_formset_name('at_24-id'), '')
