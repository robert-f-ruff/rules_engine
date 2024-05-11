#!/bin/bash
usage="$(basename "$0") [-h] [-f compose_file_name] -- build the rules engine

where:
    -h: show this help text
    -f: the Docker compose file to build from"

function prepare_frontend {
  echo "Preparing the frontend for construction:"
  (cd frontend && exec python3 manage.py collectstatic --noinput)
}

function prepare_backend {
  echo "Preparing the backend for construction:"
  (cd backend/engine && exec mvn clean package)
}

while getopts ':hf:' option; do
  case "$option" in
    h) echo "$usage"
       exit
       ;;
    f) file=$OPTARG
       if [ $file == "docker-compose-supporting_services.yml" ]; then
         printf "\n%s should not be used with this script\n\n" "$file" >&2
         exit 1
       fi
       ;;
    :) printf "mising argument for -%s\n" "$OPTARG" >&2
       echo "$usage" >&2
       exit 1
       ;;
    \?) printf "illegal option: -%s\n" "$OPTARG" >&2
        echo "$usage" >&2
        exit 1
        ;;
  esac
done
shift $((OPTIND - 1))
if [ -z ${file+x} ]; then
  echo "no option specified"
  echo "$usage"
  exit 1
fi
case "$file" in
  *frontend*) prepare_frontend
              ;;
   *backend*) prepare_backend
              ;;
   *) prepare_frontend
      prepare_backend
      ;;
esac
docker compose -f $file up --build --detach
