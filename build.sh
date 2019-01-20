#!/bin/bash

# Build MAYPAD

cd frontend
ng b $1

cd ../backend
mvn install $2

cd ..
docker build -t maypad:latest .

echo -e "\e[95m RUN MAYPAD via 'docker-compose up'\e[0m"
