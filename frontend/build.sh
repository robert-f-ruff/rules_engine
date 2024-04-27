#!/bin/bash

#Create the Docker images
echo "Building the user web portal..."
python3 manage.py collectstatic --noinput
docker compose -f docker-compose-service.yml up --build
