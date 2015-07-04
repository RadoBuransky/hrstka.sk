#!/bin/bash
git pull
./activator stopProd
./activator stage
nohup ./target/universal/stage/bin/website &
