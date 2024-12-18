# syntax=docker/dockerfile:1

FROM ubuntu:latest as deps

WORKDIR /build

ADD https://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-j-9.0.0.zip .

RUN apt-get update -y \
    && apt-get install -y zip

RUN unzip *

FROM quay.io/wildfly/wildfly-runtime:latest-jdk21 as final
COPY --chown=jboss:root backend/engine/target/server/ $JBOSS_HOME
RUN chmod -R ug+rwX $JBOSS_HOME

WORKDIR /config
COPY --chown=jboss:root backend/engine/wildfly_config.yml.tmpl .
COPY --chown=jboss:root backend/engine/wildfly-configuration-1.0.1.jar .
COPY --chown=jboss:root --from=deps /build/mysql-connector-j-9.0.0/mysql-connector-j-9.0.0.jar /config/
COPY --chown=jboss:root backend/engine/initialize.sh .

ENTRYPOINT [ "/config/initialize.sh" ]

CMD ["sh", "-c", "export SERVER_ARGS=\"-y=/config/wildfly_config.yml\" && ${JBOSS_CONTAINER_WILDFLY_RUN_MODULE}/run"]
