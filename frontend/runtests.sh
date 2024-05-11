#!/bin/bash

#Setup the virtual environment
python3 -m venv .venv
source .venv/bin/activate
python3 -m pip install -r test_requirements.txt

#Run the tests
coverage run --source='.' --omit='manage.py','admin.py','apps.py','rules/migrations/*','rules/tests/*','rules_engine/*' runtests.py
coverage html

#Shutdown the virtual environment
deactivate
