#!/bin/bash

#Setup the virtual environment
python3 -m venv .venv
source .venv/bin/activate
python3 -m pip install -r test_requirements.txt

#Run the tests
python3 runtests.py

#Shutdown the virtual environment
deactivate
