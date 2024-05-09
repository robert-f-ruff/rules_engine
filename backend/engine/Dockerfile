# syntax=docker/dockerfile:1

FROM maven:3.9.6-eclipse-temurin-21 as deps

WORKDIR /build

ADD https://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-j-8.3.0.zip .

RUN apt-get update -y \
    && apt-get install -y zip

RUN unzip *

RUN --mount=type=bind,source=backend/engine/pom.xml,target=pom.xml \
    --mount=type=cache,target=/root/.m2 \
    mvn dependency:go-offline -DskipTests

FROM deps as package

WORKDIR /build

COPY backend/engine/src src

RUN --mount=type=bind,source=backend/engine/pom.xml,target=pom.xml \
    --mount=type=cache,target=/root/.m2 \
    mvn package -DskipTests

FROM quay.io/wildfly/wildfly-runtime:latest-jdk21 as final
COPY --chown=jboss:root --from=package build/target/server $JBOSS_HOME
RUN chmod -R ug+rwX $JBOSS_HOME

WORKDIR /config
COPY --chown=jboss:root backend/engine/wildfly_config.yml.tmpl .
COPY --chown=jboss:root backend/engine/wildfly-configuration-1.0.jar .
COPY --chown=jboss:root --from=deps /build/mysql-connector-j-8.3.0/mysql-connector-j-8.3.0.jar /config/
COPY --chown=jboss:root backend/engine/initialize.sh .

ENTRYPOINT [ "/config/initialize.sh" ]

CMD ["sh", "-c", "export SERVER_ARGS=\"-y=/config/wildfly_config.yml\" && ${JBOSS_CONTAINER_WILDFLY_RUN_MODULE}/run"]
