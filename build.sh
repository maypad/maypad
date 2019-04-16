#!/bin/bash

# Build MAYPAD

cd frontend
ng b $1

cd ../backend
export MAYPAD_HOME=$(pwd)
mvn install $2 -DskipTests=true
unset MAYPAD_HOME

cd ..
docker build --no-cache -t maypad:latest .

echo -e '\033[95mRUN MAYPAD via "docker-compose up"\033[0m'
