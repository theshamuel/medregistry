#!/bin/bash
if [[ -n "${MONGO_ARCHIVE}" ]]; then
  if [[ "${MONGO_AUTH}" == "true" ]]; then
  echo "Restoring start...."
  mongorestore -u ${MONGO_ADMIN} -p ${MONGO_ADMIN_PASSWORD} --authenticationDatabase admin --drop --gzip --db medregDB --archive=/archives/${MONGO_ARCHIVE}
  echo "Restoring end"
  else
    echo "Restoring start...."
    mongorestore --drop --gzip --db medregDB --archive=/archives/${MONGO_ARCHIVE}
    echo "Restoring end"
  fi
fi
