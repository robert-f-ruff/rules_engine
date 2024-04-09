#!/bin/bash

#Wait for MySQL to start
. <(sed -e '/^export/!s/^/export DATABASE_USER="/' -e 's/$/"/' "/run/secrets/db_user")
. <(sed -e '/^export/!s/^/export DATABASE_PASSWORD="/' -e 's/$/"/' "/run/secrets/db_password")
while ! mysql -h db -u $DATABASE_USER -p$DATABASE_PASSWORD -e ";" ; do
  sleep 2
done
unset DATABASE_USER DATABASE_PASSWORD
exec "$@"
