FROM java:8-alpine

RUN mkdir -p /usr/share/maypad/frontend

COPY frontend/dist/maypad-frontend/* /usr/share/maypad/frontend/
COPY backend/target/maypad-backend-0.0.1-SNAPSHOT.jar /usr/share/maypad/backend.jar

CMD java -jar /usr/share/maypad/backend.jar
