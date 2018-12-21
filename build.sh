#!/bin/bash

# Build MAYPAD

cd frontend
ng b

cd ../backend
mvn install

cd ..
docker build -t maypad:latest .
