#!/bin/bash

#Initialize WildFly standalone.xml template and verify database connection
java -cp /config/wildfly-configuration-1.0.1.jar:/config/mysql-connector-j-9.1.0.jar io.github.robert_f_ruff.wildfly_configuration.Main \
    /config/wildfly_config.yml.tmpl /config/wildfly_config.yml /run/secrets/

#Add management user
printf "Creating WildFly management user: "
WILDFLY_MGMT_USER=$(cat /run/secrets/wildfly_user_name)
export WILDFLY_MGMT_USER
WILDFLY_MGMT_PASSWORD=$(cat /run/secrets/wildfly_user_password)
export WILDFLY_MGMT_PASSWORD
(cd "$JBOSS_HOME/bin" && ./add-user.sh $WILDFLY_MGMT_USER $WILDFLY_MGMT_PASSWORD 1> /dev/null)
unset WILDFLY_MGMT_USER WILDFLY_MGMT_PASSWORD
echo "Done"

exec "$@"
