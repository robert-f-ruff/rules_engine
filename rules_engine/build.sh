#!/bin/bash

#Create the Docker images
echo "Building the user web portal..."
python3 manage.py collectstatic --noinput
docker compose -f compose.yml up --build
