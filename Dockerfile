FROM java:8-alpine

RUN mkdir -p /usr/share/maypad/frontend && \
    mkdir -p /usr/share/maypad/security && \
    mkdir -p /home/maypad/repositories

COPY frontend/dist/maypad-frontend/* /usr/share/maypad/frontend/
COPY backend/target/maypad-backend-1.0.0.jar /usr/share/maypad/backend.jar
COPY config.yaml.sample /usr/share/maypad/config.yaml

EXPOSE 8080

ENTRYPOINT java -jar /usr/share/maypad/backend.jar
