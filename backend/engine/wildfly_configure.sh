#!/bin/bash

#Initialize WildFly standalone.xml template
. <(sed -e '/^export/!s/^/export /' -e 's/=/="/1' -e 's/$/"/' "/build/wildfly_configuration.txt")
. <(sed -e '/^export/!s/^/export DATABASE_USER="/' -e 's/$/"/' "/build/db_user.txt")
. <(sed -e '/^export/!s/^/export DATABASE_PASSWORD="/' -e 's/$/"/' "/build/db_password.txt")
envsubst '$FROM_EMAIL $MAIL_SERVER_PASSWORD $DATABASE_USER $DATABASE_PASSWORD $RELOAD_RULES_KEY' \
  <"/build/standalone.xml.tmpl" >"/build/standalone.xml"
#Cleanup environment
unset FROM_EMAIL MAIL_SERVER_PASSWORD DATABASE_USER DATABASE_PASSWORD