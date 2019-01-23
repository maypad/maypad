FROM java:8-alpine

RUN mkdir -p /usr/share/maypad/frontend && \
    mkdir -p /home/maypad/repositories && \
    chmod 777 -R /home/maypad/repositories

COPY frontend/dist/maypad-frontend/* /usr/share/maypad/frontend/
COPY backend/target/maypad-backend-0.0.1-SNAPSHOT.jar /usr/share/maypad/backend.jar
COPY config.yaml /usr/share/maypad/config.yaml


EXPOSE 8080

ENTRYPOINT java -jar /usr/share/maypad/backend.jar
