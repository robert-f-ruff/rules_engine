"""Define the index.html page object model."""
from selenium.webdriver import Firefox
from selenium.webdriver.common.by import By
from .base import BasePage, WrongPageError


class NoSuchRuleError(Exception):
    """ Define the exception for no such rule."""


class IndexPage(BasePage):
    """ This class defines the interface for interacting with the index.html
        page.
    """
    def __init__(self, page_driver: Firefox) -> None:
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
        """ This function returns the text in the first <p> tag if there are
            no rules on the page.
        """
        if len(self._rules) == 0:
            return self._driver.find_element(by=By.TAG_NAME, value='p').text
        return ''

    def get_rule(self, index: int) -> str:
        """ This function returns the rule text at the specified index."""
        if index < len(self._rules):
            return self._rules[index][0].text
        return ''

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
