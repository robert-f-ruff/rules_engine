# syntax=docker/dockerfile:1

FROM python:3.11.4

WORKDIR /app

RUN apt-get update -y \
    && apt-get install -y python3-dev default-libmysqlclient-dev build-essential
RUN pip3 install --upgrade pip
COPY requirements.txt requirements.txt
RUN pip3 install -r requirements.txt

COPY . .

EXPOSE 7000

ENTRYPOINT [ "/app/initialize.sh" ]

CMD [ "gunicorn", "--bind", "0.0.0.0:7000", "--workers", "2", "rules_engine.wsgi" ]
