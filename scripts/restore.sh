#!/bin/bash

# This script assumes that the backup has been created by the backup.sh script.

#------------------------------------------------------------------------------
# Script parameters
#------------------------------------------------------------------------------

# Usage example:
# $ ./restore.sh localhost 27017 hrstka /home/rado/hrstka-backup 2015_07_12_14_31_28

MONGO_HOST=$1
MONGO_PORT=$2
MONGO_DB=$3
BACKUP_DIR=$4
BACKUP_NAME=$5

#------------------------------------------------------------------------------
# Global variables
#------------------------------------------------------------------------------

BACKUP_FILE="$BACKUP_DIR/$BACKUP_NAME.tar.gz"

#------------------------------------------------------------------------------
# Helper methods
#------------------------------------------------------------------------------

checkDump()
{
    if [ ! -f "$BACKUP_FILE" ]; then
        echo "ERROR: Dump file does not exist! [$BACKUP_FILE]"
        exit 1;
    fi
}

unzipDump()
{
    export UNZIPPED_DUMP_DIR="$(mktemp -d)"
    echo "Extracting zipped dump file to $UNZIPPED_DUMP_DIR ..."
    tar zxf "$BACKUP_FILE" -C "$UNZIPPED_DUMP_DIR"
}

cleanup()
{
    echo "Deleting temporary unzipped dump $UNZIPPED_DUMP_DIR ..."
    rm -rf $UNZIPPED_DUMP_DIR
}

#------------------------------------------------------------------------------
# Main
#------------------------------------------------------------------------------

echo "Restoring MongoDB $MONGO_DB database $MONGO_HOST:$MONGO_PORT to $BACKUP_NAME in $BACKUP_DIR..."

# Check if the backup exists
checkDump

# Extract zipped dump file
unzipDump

# Do the restore
DUMP_DIR="$UNZIPPED_DUMP_DIR/$BACKUP_NAME/$MONGO_DB"
echo "Restoring from $DUMP_DIR..."
mongorestore  --host $MONGO_HOST:$MONGO_PORT --db $MONGO_DB --dir $DUMP_DIR

# Cleanup
cleanup

# Done
echo "Restore finished successfully."