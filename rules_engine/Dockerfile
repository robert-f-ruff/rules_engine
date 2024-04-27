# syntax=docker/dockerfile:1

FROM python:3.12.3

WORKDIR /app

RUN apt-get update -y \
    && apt-get install -y python3-dev default-libmysqlclient-dev build-essential pkg-config
RUN pip3 install --upgrade pip
COPY . .
RUN pip3 install -r requirements.txt
RUN rm -f /app/requirements.txt

RUN mkdir /app/log

RUN groupadd -r django && useradd -r -g django django
RUN chown -R django:django /app

EXPOSE 7000

USER django

ENTRYPOINT [ "/app/initialize.sh" ]

CMD [ "gunicorn", "--bind", "0.0.0.0:7000", "--workers", "2", "rules_engine.wsgi" ]
