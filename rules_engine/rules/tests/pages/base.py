"""Define the base page object."""
from typing import Any
from selenium.webdriver import Firefox

class BasePage:
    """ Parent class for all page objects."""
    def __init__(self, page_driver:Firefox) -> None:
        self._driver = page_driver

    def get_url(self) -> str:
        """ This function returns the url for this page."""
        return self._driver.current_url


class WrongPageError(Exception):
    """ Define the exception for a wrong page."""
    def __init__(self, message: Any, page_name: str) -> None:
        super().__init__(message)
        self._page_name = page_name

    def __str__(self) -> str:
        return super().__str__() + ' is not the address for the ' + self._page_name + ' page'
