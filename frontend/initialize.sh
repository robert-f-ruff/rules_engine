#!/bin/bash

python3 containerpause.py

#Initialize Django by making migrations and migrating the database.
echo "Initializing database..."
python3 manage.py makemigrations rules --noinput
python3 manage.py migrate --noinput
python3 manage.py loaddata actions_parameters

DJANGO_SUPERUSER_USERNAME=$(cat /run/secrets/django_super_user_name)
export DJANGO_SUPERUSER_USERNAME
DJANGO_SUPERUSER_EMAIL=$(cat /run/secrets/django_super_user_email)
export DJANGO_SUPERUSER_EMAIL
DJANGO_SUPERUSER_PASSWORD=$(cat /run/secrets/django_super_user_password)
export DJANGO_SUPERUSER_PASSWORD
python3 manage.py createsuperuser --noinput
unset DJANGO_SUPERUSER_USERNAME DJANGO_SUPERUSER_EMAIL DJANGO_SUPERUSER_PASSWORD

exec "$@"
