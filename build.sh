#!/bin/bash

# Build MAYPAD

cd frontend
ng b

cd ../backend
mvn install

cd ..
docker build -t maypad:latest .

echo -e "\e[95m RUN MAYPAD via 'docker-compose up'\e[0m"
