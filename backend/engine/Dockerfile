# syntax=docker/dockerfile:1

FROM maven:3.9.6-eclipse-temurin-17 as deps

WORKDIR /build

ADD https://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-j-8.3.0.zip .

RUN apt-get update -y \
    && apt-get install -y zip \
    && apt-get install -y gettext

RUN unzip *

RUN --mount=type=bind,source=backend/engine/pom.xml,target=pom.xml \
    --mount=type=cache,target=/root/.m2 \
    mvn dependency:go-offline -DskipTests

COPY backend/engine/wildfly_configure.sh /build/

RUN --mount=type=bind,source=secrets/db_user_name.txt,target=db_user.txt \
    --mount=type=bind,source=secrets/db_user_password.txt,target=db_password.txt \
    --mount=type=bind,source=backend/engine/wildfly_configuration.txt,target=wildfly_configuration.txt \
    --mount=type=bind,source=backend/engine/standalone.xml.tmpl,target=standalone.xml.tmpl \
    /build/wildfly_configure.sh

FROM deps as package

WORKDIR /build

COPY backend/engine/src src

RUN --mount=type=bind,source=backend/engine/pom.xml,target=pom.xml \
    --mount=type=cache,target=/root/.m2 \
    mvn --offline package -DskipTests

FROM bitnami/wildfly:31.0.1 as final

COPY --from=deps /build/mysql-connector-j-8.3.0/mysql-connector-j-8.3.0.jar /app/
COPY --from=deps /build/standalone.xml /opt/bitnami/wildfly/standalone/configuration/
COPY --from=package /build/target/engine-1.0-SNAPSHOT.war /app/
COPY backend/engine/initialize.sh /build/

USER root

RUN apt-get update -y \
    && apt-get install -y default-mysql-client

USER 1001

EXPOSE 8080

ENTRYPOINT [ "/build/initialize.sh" ]

CMD [ "/opt/bitnami/scripts/wildfly/run.sh" ]
