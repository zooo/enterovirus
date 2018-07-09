import unittest
import random
from urllib.parse import urljoin

from selenium import webdriver

root_url = "http://localhost:8887"


class TestSignUp(unittest.TestCase):

    def setUp(self):
        self.driver = webdriver.Chrome()

    def tearDown(self):
        self.driver.close()

    def test_register_and_successfully_login_fill_form(self):
        distingisher = str(random.randint(0, 10000))

        username = "username"+distingisher
        password = "password"
        display_name = "User Name "+distingisher
        email = "username"+distingisher+"@email.com"

        self.driver.get(urljoin(root_url, "register"))

        assert "GitEnter" in self.driver.title
        assert "Register" in self.driver.page_source

        self._signup_fill_form(self.driver, username, password, display_name, email)

        self.driver.get(urljoin(root_url, "login"))
        assert "Log in" in self.driver.page_source

        self._login_fill_form(self.driver, username, password)
        self.assertEqual(len(self.driver.get_cookies()), 1)

        # TODO:
        # Check the user can successfully login.

        # print(self.driver.get_cookies())

        self.driver.get(urljoin(root_url, "logout"))

        # Logout and login again with remember_me checked
        self.driver.get(urljoin(root_url, "login"))

        self._login_fill_form(self.driver, username, password, remember_me=True)
        self.assertEqual(len(self.driver.get_cookies()), 2)
        self.assertEqual(self.driver.get_cookies()[0]['name'], 'remember-me')
        self.assertTrue(self.driver.get_cookies()[0]['expiry'] > 0)

        self.driver.get(urljoin(root_url, "login"))

    def test_login_fill_form_with_nonexistent_user(self):
        username = "nonexistent_username"
        password = "password"

        self.driver.get(urljoin(root_url, "login"))

        self._login_fill_form(self.driver, username, password)
        assert "Invalid username and password!" in self.driver.page_source

    def test_register_with_invalid_input(self):
        username = "u"
        password = "p"
        display_name = "U"
        email = "not_a_email_address"

        self.driver.get(urljoin(root_url, "register"))
        self._signup_fill_form(self.driver, username, password, display_name, email)

        assert "size" in self.driver.find_element_by_id("username.errors").text
        assert "size" in self.driver.find_element_by_id("password.errors").text
        assert "size" in self.driver.find_element_by_id("displayName.errors").text
        assert "not a well-formed email addres" in self.driver.find_element_by_id("email.errors").text

    @staticmethod
    def _signup_fill_form(driver, username, password, display_name, email):
        form_start = driver.find_element_by_id("username")
        form_start.send_keys(username)
        driver.find_element_by_id("password").send_keys(password)
        driver.find_element_by_id("displayName").send_keys(display_name)
        driver.find_element_by_id("email").send_keys(email)

        form_start.submit()

    @staticmethod
    def _login_fill_form(driver, username, password, remember_me=False):
        form_start = driver.find_element_by_id("username")
        form_start.send_keys(username)
        driver.find_element_by_id("password").send_keys(password)
        if remember_me:
            driver.find_element_by_id("remember_me").click()

        form_start.submit()


if __name__ == '__main__':
    unittest.main()
