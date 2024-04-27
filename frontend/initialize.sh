#!/bin/bash

#Initialize Django by making migrations and migrating the database.
echo "Initializing database..."
python3 manage.py makemigrations rules --noinput
python3 manage.py migrate --noinput
python3 manage.py loaddata actions_parameters
exec "$@"
