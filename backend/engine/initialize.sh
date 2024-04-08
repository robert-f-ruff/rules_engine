#!/bin/bash

#Initialize WildFly standalone.xml template
. <(sed -e '/^export/!s/^/export /' -e 's/=/="/1' -e 's/$/"/' "/run/secrets/wildfly_configuration")
envsubst '$FROM_EMAIL $MAIL_SERVER_PASSWORD $DATABASE_USER $DATABASE_PASSWORD' \
  <"/build/standalone.xml.tmpl" >"/opt/bitnami/wildfly/standalone/configuration/standalone.xml"
#Wait for MySQL to start
while ! mysql -h db -u $DATABASE_USER -p$DATABASE_PASSWORD -e ";" ; do
  sleep 2
done
#Cleanup environment
unset FROM_EMAIL MAIL_SERVER_PASSWORD DATABASE_USER DATABASE_PASSWORD
exec "$@"
