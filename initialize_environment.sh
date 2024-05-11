#!/bin/bash

echo "Installing required Python modules:"
pip3 install -r frontend/test_requirements.txt

echo " "
ENVFILE="frontend/.env"
printf "Creating $ENVFILE file with default values... "
echo "running_in_docker=no" >> $ENVFILE
echo "logging_level=DEBUG" >> $ENVFILE
echo "Done"

echo "Populating the secret files:"
for secretfile in secrets/*.txt; do
  git update-index --assume-unchanged $secretfile
  FILE_NAME=$(sed -e "s/^secrets\///" <<< $secretfile)
  echo $FILE_NAME
  if [ "$FILE_NAME" = "db_host.txt" ]; then
    value="127.0.0.1"
    echo "Successfully updated with the default value of 127.0.0.1."
  elif [ "$FILE_NAME" = "db_host_port.txt" ]; then
    value="3306"
    echo "Successfully updated with the default value of 3306."
  elif [ "$FILE_NAME" = "db_root_password.txt" ]; then
    echo "This stores the password for the database's root user."
    read -s -p "Root user's password: " value
  elif [ "$FILE_NAME" = "db_user_name.txt" ]; then
    echo "This stores the user name the applications use to access the database server."
    read -p "Non-privileged user's name: " value
  elif [ "$FILE_NAME" = "db_user_password.txt" ]; then
    echo "This stores the password for the applications' user account."
    read -s -p "Non-privileged user's password: " value
  elif [ "$FILE_NAME" = "django_secret_key.txt" ]; then
    echo "This stores the key Django uses to secure signed data."
    read -p "Django secret key: " value
  elif [ "$FILE_NAME" = "django_super_user_email.txt" ]; then
    echo "This stores the email address to assign to the super user account."
    read -p "Django super user email address: " value
  elif [ "$FILE_NAME" = "django_super_user_name.txt" ]; then
    echo "This stores the username to assign to the super user account."
    read -p "Django super user account name: " value
  elif [ "$FILE_NAME" = "django_super_user_password.txt" ]; then
    echo "This stores the password for the super user account."
    read -s -p "Django super user's password: " value
  elif [ "$FILE_NAME" = "engine_host.txt" ]; then
    value="127.0.0.1:8080"
    echo "Successfully updated with the default value of 127.0.0.1:8080"
  elif [ "$FILE_NAME" = "engine_reload_key.txt" ]; then
    echo "This stores the key the backend Java application uses to verify reload requests."
    read -p "Engine reload key: " value
  elif [ "$FILE_NAME" = "mail_from_address.txt" ]; then
    echo "This stores the email address to use in connecting to the email service (should be fake)."
    read -p "Email address for connections to the email service: " value
  elif [ "$FILE_NAME" = "mail_host_port.txt" ]; then
    value="3025"
    echo "Successfully updated with the default value of 3025"
  elif [ "$FILE_NAME" = "mail_host.txt" ]; then
    value="email"
    echo "Successfully updated with the default value of email"
  elif [ "$FILE_NAME" = "mail_server_password.txt" ]; then
    echo "This stores the password for the email account used in connecting to the email service."
    read -s -p "Email service user's password: " value
  elif [ "$FILE_NAME" = "wildfly_user_name.txt" ]; then
    echo "This stores the user name for the WildFly management user."
    read -p "WildFly management user name: " value
  elif [ "$FILE_NAME" = "wildfly_user_password.txt" ]; then
    echo "This stores the password for the WildFly management user's password."
    read -s -p "WildFly management user's password: " value
  fi
  echo -n $value > $secretfile
  echo " "
  echo "====+====+====+====+====+====+====+====+"
  echo " "
done
echo "Done"

echo " "
echo "Configuring Docker:"
docker network create rules_engine --driver bridge
echo "Done"

echo " "
echo "Launching the supporting services:"
docker compose -f docker-compose-supporting_services.yml up --build --detach

echo " "
python3 frontend/containerpause.py

echo " "
echo "Executing migrations:"
python3 frontend/manage.py migrate --noinput
python3 frontend/manage.py loaddata actions_parameters

echo " "
echo "Creating frontend Django super user:"
DJANGO_SUPERUSER_USERNAME=$(cat secrets/django_super_user_name.txt)
export DJANGO_SUPERUSER_USERNAME
DJANGO_SUPERUSER_EMAIL=$(cat secrets/django_super_user_email.txt)
export DJANGO_SUPERUSER_EMAIL
DJANGO_SUPERUSER_PASSWORD=$(cat secrets/django_super_user_password.txt)
export DJANGO_SUPERUSER_PASSWORD
python3 frontend/manage.py createsuperuser --noinput
unset DJANGO_SUPERUSER_USERNAME DJANGO_SUPERUSER_EMAIL DJANGO_SUPERUSER_PASSWORD
