# syntax=docker/dockerfile:1

FROM maven:3.9.6-eclipse-temurin-17 as deps

WORKDIR /build

ADD https://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-j-8.3.0.zip .

RUN apt-get update -y \
    && apt-get install -y zip

RUN unzip *

RUN --mount=type=bind,source=pom.xml,target=pom.xml \
    --mount=type=cache,target=/root/.m2 \
    mvn dependency:go-offline -DskipTests

FROM deps as package

WORKDIR /build

COPY ./src src

RUN --mount=type=bind,source=pom.xml,target=pom.xml \
    --mount=type=cache,target=/root/.m2 \
    mvn --offline package -DskipTests

FROM bitnami/wildfly:31.0.1 as final

COPY --from=deps /build/mysql-connector-j-8.3.0/mysql-connector-j-8.3.0.jar /app/
COPY --from=package /build/target/engine-1.0-SNAPSHOT.war /app/
COPY initialize.sh /build/
COPY standalone.xml.tmpl /build/

USER root

RUN apt-get update -y \
    && apt-get install -y gettext \
    && apt-get install -y default-mysql-client

USER 1001

EXPOSE 8080

ENTRYPOINT [ "/build/initialize.sh" ]

CMD [ "/opt/bitnami/scripts/wildfly/run.sh" ]
