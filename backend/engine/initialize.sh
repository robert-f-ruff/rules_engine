#!/bin/bash

#Initialize WildFly standalone.xml template and wait verify database connection
java -cp /config/wildfly-configuration-1.0.jar:/config/mysql-connector-j-8.3.0.jar io.github.robert_f_ruff.wildfly_configuration.Main \
    /config/wildfly_config.yml.tmpl /config/wildfly_config.yml /run/secrets/

exec "$@"
