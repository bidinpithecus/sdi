#!/bin/bash

if [ "$#" -lt 1 ]; then
  echo "Usage: $0 --database | --matrix | --client <hostM> <hostDB>"
  exit 1
fi

case "$1" in
  --database)
    java -cp target ServerDatabase
    ;;
  --matrix)
    java -cp target ServerMatrix
    ;;
  --client)
    if [ "$#" -ne 3 ]; then
      echo "Usage: $0 --client <hostM> <hostDB>"
      exit 1
    fi
    java -cp target Client "$2" "$3"
    ;;
  *)
    echo "Invalid option. Use --database, --matrix, or --client."
    exit 1
    ;;
esac
