# syntax=docker/dockerfile:1

FROM python:3.11.2

WORKDIR /app

RUN set -ex; \
# gpg: key 5072E1F5: public key "MySQL Release Engineering <mysql-build@oss.oracle.com>" imported
    key='859BE8D7C586F538430B19C2467B942D3A79BD29'; \
    export GNUPGHOME="$(mktemp -d)"; \
    gpg --batch --keyserver keyserver.ubuntu.com --recv-keys "$key"; \
    gpg --batch --export "$key" > /etc/apt/trusted.gpg.d/mysql.gpg; \
    gpgconf --kill all; \
    rm -rf "$GNUPGHOME"; \
    apt-key list > /dev/null
RUN echo "deb http://repo.mysql.com/apt/debian/ buster mysql-8.0" > /etc/apt/sources.list.d/mysql.list
RUN apt-get update \
    && apt-get install -y mysql-community-client
RUN pip3 install --upgrade pip
COPY requirements.txt requirements.txt
RUN pip3 install -r requirements.txt

COPY . .

EXPOSE 7000

ENTRYPOINT [ "/app/initialize.sh" ]

CMD [ "gunicorn", "--bind", "0.0.0.0:7000", "--workers", "2", "rules_engine.wsgi" ]
