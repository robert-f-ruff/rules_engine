#!/bin/bash
usage="$(basename "$0") [-h] [-f compose_file_name] -- build the rules engine

where:
    -h show this help text
    -f set the Docker compose file to use"
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
echo "Preparing the frontend for construction..."
(cd frontend && exec python3 manage.py collectstatic --noinput)
docker compose -f $file up --build --detach
