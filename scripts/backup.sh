#!/bin/bash

#------------------------------------------------------------------------------
# Script parameters
#------------------------------------------------------------------------------

MONGO_HOST=localhost
MONGO_PORT=27017
PARENT_DIR="/home/rado/backup"

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
    TIMESTAMP_DIR=$(date '+%Y_%m_%d_%H_%M_%S')
    export BACKUP_DIR="$PARENT_DIR/$TIMESTAMP_DIR"
    echo "Creating backup directory $BACKUP_DIR..."
    mkdir "$BACKUP_DIR"
}

#------------------------------------------------------------------------------
# Main
#------------------------------------------------------------------------------

echo "Performing backup of MongoDB $MONGO_HOST:$MONGO_PORT to $PARENT_DIR..."

# Check if parent backup directory exists
checkParentDir

# Create subdirectory for the current date and time
createBackupDir

# TODO: Remove this (useful for development)
rm -rf "$PARENT_DIR/"*