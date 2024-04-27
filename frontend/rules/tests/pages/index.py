"""Define the index.html page object model."""
import re
import selenium.common.exceptions
from selenium.webdriver.remote.webdriver import WebDriver
from selenium.webdriver.common.by import By
from .base import BasePage, WrongPageError


class NoSuchRuleError(Exception):
    """ Define the exception for no such rule."""


class IndexPage(BasePage):
    """ This class defines the interface for interacting with the index.html
        page.
    """
    def __init__(self, page_driver: WebDriver) -> None:
        super().__init__(page_driver=page_driver)
        if self._driver.title != 'List of Rules':
            raise WrongPageError(self._driver.current_url, page_name='Index')
        rule_links = []
        delete_links = []
        rules_list = self._driver.find_element(by=By.ID, value='rules_list')
        for link in rules_list.find_elements(by=By.TAG_NAME, value='a'):
            if link.text != 'Delete':
                rule_links.append(link)
            elif link.text == 'Delete':
                delete_links.append(link)
        self._rules = list(zip(rule_links, delete_links))

    def get_header(self) -> str:
        """ This function returns the text in the first <h1> tag."""
        page_header = self._driver.find_element(by=By.TAG_NAME, value='h1')
        return page_header.text

    def get_no_rules_message(self) -> str:
        """ This function returns the text that should appear if there are
            no rules on the page.
        """
        if len(self._rules) == 0:
            return self._driver.find_element(by=By.ID, value='no_rules_message').text
        return ''

    def get_rule(self, index: int) -> str:
        """ This function returns the rule text at the specified index."""
        if index < len(self._rules):
            return self._rules[index][0].text
        return ''

    def get_engine_status_area_color(self) -> str:
        """ This function returns the background color of the engine status box.
        """
        classes = self._driver.find_element(by=By.ID,
                                            value='engine_status_area').get_attribute('class')
        if classes is not None:
            if re.search(r'\balert-danger\b', classes):
                return 'red'
            if re.search(r'\balert-success\b', classes):
                return 'green'
        return ''

    def get_engine_status(self) -> str:
        """ This function returns the engine status displayed on the page.
        """
        return self._driver.find_element(by=By.ID, value='engine_status').text

    def get_reload_status(self) -> str:
        """ This function returns the engine reload status displayed on the page.
        """
        try:
            return self._driver.find_element(by=By.ID, value='reload_status').text
        except selenium.common.exceptions.NoSuchElementException:
            return ''

    def click_reload_ruleset(self) -> None:
        """ This function clicks on the Reload Ruleset button.
        """
        reload_button = self._driver.find_element(by=By.ID, value='reload_button')
        reload_button.click()

    def get_reload_button_visible(self) -> bool:
        """ This function returns when the Reload Ruleset button is visible on the page
        """
        try:
            reload_button = self._driver.find_element(by=By.ID, value='reload_button')
            if reload_button is not None:
                classes = reload_button.get_attribute('class')
                if classes is not None:
                    match = re.search(r'\bd-none\b', classes)
                    if not match:
                        return True
        except selenium.common.exceptions.NoSuchElementException:
            pass
        return False

    def click_rule(self, index: int) -> None:
        """ This function clicks on the specified rule link."""
        if index < len(self._rules):
            self._rules[index][0].click()
        else:
            raise NoSuchRuleError('Index ' + str(index))

    def click_rule_delete(self, index: int):
        """ This function clicks on the delete link for the specified rule."""
        if index < len(self._rules):
            self._rules[index][1].click()
        raise NoSuchRuleError('Index ' + str(index))

    def click_add_rule(self) -> None:
        """ This function clicks on the Add a rule link.
        """
        add_rule_link = self._driver.find_element(by=By.LINK_TEXT, value='Add a rule')
        add_rule_link.click()
