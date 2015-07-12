#!/bin/bash

#------------------------------------------------------------------------------
# Script parameters
#------------------------------------------------------------------------------

# Usage example:
# $ backup.sh localhost 27017 hrstka /home/rado/backup

MONGO_HOST=$1
MONGO_PORT=$2
MONGO_DB=$3
BACKUP_DIR=$4

#------------------------------------------------------------------------------
# Helper methods
#------------------------------------------------------------------------------

checkParentDir()
{
    if [ ! -d "$BACKUP_DIR" ]; then
        echo "ERROR: Backup directory does not exist! [$BACKUP_DIR]"
        exit 1;
    fi
}

createBackupDir()
{
    export DUMP_NAME=$(date '+%Y_%m_%d_%H_%M_%S')
    export DUMP_DIR="$BACKUP_DIR/$DUMP_NAME"
    echo "Creating backup directory $DUMP_DIR..."
    mkdir "$DUMP_DIR"
}

checkDump()
{
    if [ ! "$(ls -A $DUMP_DIR/$MONGO_DB)" ]; then
        echo "ERROR: Dump is empty!"
        exit 1;
    fi
}

zipDump()
{
    export ZIP_PATH="$BACKUP_DIR/$DUMP_NAME.tar.gz"

    echo "Zipping dump to $ZIP_PATH..."
    tar -C "$BACKUP_DIR" -zcvf "$ZIP_PATH" "$DUMP_NAME"

    echo "Removing dump directory..."
    rm -rf "$DUMP_DIR"
}

checkSuccess()
{
    if [ ! -f "$ZIP_PATH" ]; then
        echo "ERROR: Zipped dump does not exist! [$ZIP_PATH]"
        exit 1;
    fi

    if [ -d "$DUMP_DIR" ]; then
        echo "ERROR: Backup directory still exists! [$DUMP_DIR]"
        exit 1;
    fi

    echo "Backup finished successfully."
}

#------------------------------------------------------------------------------
# Main
#------------------------------------------------------------------------------

echo "Performing backup of MongoDB $MONGO_HOST:$MONGO_PORT to $BACKUP_DIR..."

# Check if parent backup directory exists
checkParentDir

# Create subdirectory for the current date and time
createBackupDir

# Do the backup
echo "Doing backup to $DUMP_DIR..."
mongodump --host $MONGO_HOST:$MONGO_PORT --db $MONGO_DB --out $DUMP_DIR

# Check dump directory
checkDump

# Zip dump directory
zipDump

# Final check
checkSuccess
