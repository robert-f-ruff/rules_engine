#!/usr/bin/env python
"""Run the tests using TestContainers to host the MySQL server."""
import os
import sys
import django
from django.conf import settings
from django.test.utils import get_runner
from dotenv import load_dotenv, find_dotenv
from testcontainers.mysql import MySqlContainer


if __name__ == "__main__":
    load_dotenv(find_dotenv())
    with MySqlContainer(image='mysql:8.3.0', username=os.environ['MYSQL_USER'],
                        password=os.environ['MYSQL_PASSWORD'], dbname='test_rules') as mysql:
        os.environ['DJANGO_SETTINGS_MODULE'] = 'rules.tests.settings'
        os.environ['MYSQL_HOST_PORT'] = mysql.get_exposed_port(3306)
        django.setup()
        TestRunner = get_runner(settings)
        test_runner = TestRunner()
        test_runner.interactive = False
        failures = test_runner.run_tests(['rules.tests'])
    sys.exit(bool(failures))
