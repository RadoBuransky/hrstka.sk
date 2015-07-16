#!/bin/bash
cd $HRSTKA_DEPLOYMENT_DIR
git pull
./activator stopProd
./activator stage
nohup ./target/universal/stage/bin/website &
