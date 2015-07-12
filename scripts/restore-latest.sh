#!/bin/bash

# Find the latest backup
echo "Finding the latest backup in $HRSTKA_BACKUP_DIR ..."

# Find the latest file
LATEST_DUMP_FILE=$(ls -t "$HRSTKA_BACKUP_DIR" | head -1)

# Get rid of the .tar.gz suffix
LATEST_DUMP_FILE_NAME=${LATEST_DUMP_FILE%.tar.gz}

# Run the restore.sh script
echo "Using latest backup $LATEST_DUMP_FILE_NAME to run restore.sh ..."
$HRSTKA_DEPLOYMENT_DIR/scripts/restore.sh $HRSTKA_MONGO_HOST $HRSTKA_MONGO_PORT $HRSTKA_MONGO_DB $HRSTKA_BACKUP_DIR $LATEST_DUMP_FILE_NAME