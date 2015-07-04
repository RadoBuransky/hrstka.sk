#!/bin/bash
git pull
./activator stopProd stage
nohup ./target/universal/stage/bin/website &
