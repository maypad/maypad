#!/bin/bash

# Build MAYPAD

cd frontend
ng b $1

cd ../backend
mvn install $2

cd ..
docker build --no-cache -t maypad:latest .

echo -e '\033[95mRUN MAYPAD via "docker-compose up"\033[0m'
