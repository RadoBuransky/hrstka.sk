#!/bin/bash

#------------------------------------------------------------------------------
# Script parameters
#------------------------------------------------------------------------------

MONGO_HOST=localhost
MONGO_PORT=27017
PARENT_DIR="/home/rado/backup"
DB=hrstka

#------------------------------------------------------------------------------
# Helper methods
#------------------------------------------------------------------------------

checkParentDir()
{
    if [ ! -d "$PARENT_DIR" ]; then
        echo "ERROR: Backup directory does not exist! [$PARENT_DIR]"
        exit 1;
    fi
}

createBackupDir()
{
    export BACKUP_NAME=$(date '+%Y_%m_%d_%H_%M_%S')
    export BACKUP_DIR="$PARENT_DIR/$BACKUP_NAME"
    echo "Creating backup directory $BACKUP_DIR..."
    mkdir "$BACKUP_DIR"
}

checkDump()
{
    if [ ! "$(ls -A $BACKUP_DIR/$DB)" ]; then
        echo "ERROR: Dump is empty!"
        exit 1;
    fi
}

zipDump()
{
    export ZIP_PATH="$PARENT_DIR/$BACKUP_NAME.tar.gz"

    echo "Zipping dump to $ZIP_PATH..."
    tar -C "$PARENT_DIR" -zcvf "$ZIP_PATH" "$BACKUP_NAME"

    echo "Removing dump directory..."
    rm -rf "$BACKUP_DIR"
}

checkSuccess()
{
    if [ ! -f "$ZIP_PATH" ]; then
        echo "ERROR: Zipped dump does not exist! [$ZIP_PATH]"
        exit 1;
    fi

    if [ -d "$BACKUP_DIR" ]; then
        echo "ERROR: Backup directory still exists! [$BACKUP_DIR]"
        exit 1;
    fi

    echo "Backup finished successfully."
}

#------------------------------------------------------------------------------
# Main
#------------------------------------------------------------------------------

echo "Performing backup of MongoDB $MONGO_HOST:$MONGO_PORT to $PARENT_DIR..."

# Check if parent backup directory exists
checkParentDir

# Create subdirectory for the current date and time
createBackupDir

# Do the backup
echo "Running mongodump..."
mongodump --host $MONGO_HOST:$MONGO_PORT --db $DB --out $BACKUP_DIR

# Check dump directory
checkDump

# Zip dump directory
zipDump

# Final check
checkSuccess
