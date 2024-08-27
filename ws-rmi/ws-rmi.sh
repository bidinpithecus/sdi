#!/bin/bash

if [ "$#" -lt 1 ]; then
  echo "Usage: $0 --database | --matrix | --publisher | --client"
  exit 1
fi

case "$1" in
  --database)
    java -cp target ServerDatabase
    ;;
  --matrix)
    java -cp target ServerMatrix
    ;;
  --publisher)
    java -cp target ws_rmi.ClientPublisher
    ;;
  --client)
    java -cp target ws_rmi.Client
    ;;
  *)
    echo "Invalid option. Use --database, --matrix, or --publisher, or --client."
    exit 1
    ;;
esac
