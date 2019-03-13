# Server Setup
The MAYPAD frontend and backend run together in docker container allowing it to run on any infrastructure. It requires a additional MySQL database for data persistence.

## Running the server
We provide a ready-to-use docker-compose file with which you can start a MAYPAD instance including a MySQL database within minutes. Note that you should use a proper MySQL database instead of the docker container for production environments and persistence.

### Building yourself
1. Clone the repo
2. Make sure all dependencies are installed (java, maven, nodejs, docker)
3. Install node modules `cd frontend && npm install && cd ..`
4. Build the container `./build.sh --prod`
5. Copy the sample config file `cp config.yaml{.sample,}` and make neccessary changes to it
6. Start MAYPAD using `docker-compose up`

### Using pre-build docker container
1. Replace the image for the maypad-app in the docker-compose file `maypad` by `juliantodt/maypad`
2. Start MAYPAD using `docker-compose up`


## Configuration
* MAYPAD can either be configured using it's configuration file or via environment variables (`MAYPAD_` followed by the full attribute name in SNAKE_CASE, e.g. `MAYPAD_WEBHOOK_TOKEN_LENGTH` for `webhook.tokenLength`).
* The environment variable `MAYPAD_HOME` indicates where MAYPAD will look for the configuration file and the frontend files and defaults to `/usr/share/maypad/`.
* For an example config file see `config.yaml.sample`
* Note that MAYPAD will generate an encryption key for database entries under `MAYPAD_HOME/security/key.dat` that you should save between container restarts. The default docker-compose will take care of that.

## Webserver Configuration
You can use MAYPAD without a additional webserver, but if you want to add authorization to MAYPAD, consider proxying the requests through a webserver such as nginx and then add http basic authentication. An example configuration could look like this (note the extra nginx configuration parameters because MAYPAD uses Server-Send-Events for notifications):
```
server {
  listen 80 default_server;
  listen [::]:80 default_server;
  server_name demo.maypad.de;

  location / {
    proxy_pass http://maypad-app:8080/;
    add_header X-Frame-Options SAMEORIGIN;

    auth_basic "MAYPAD Demo";
    auth_basic_user_file /etc/nginx/conf.d/htpasswd;
  }

  location /sse {
    proxy_pass http://maypad-app:8080/sse;
    add_header X-Frame-Options SAMEORIGIN;
    proxy_http_version 1.1;
    proxy_set_header Connection "";
    add_header Transfer-Encoding: chunked;
    add_header X-Accel-Buffering: no;
    proxy_buffering off;
  }
}

```
The docker-compose in this case could look like this (using a external MySQL database):
```
version: '3'

services:
  app:
    image: juliantodt/maypad
    container_name: maypad-app
    restart: 'no'
    volumes:
    - ./config.yaml:/usr/share/maypad/config.yaml
    - ./repos:/home/maypad/repositories
    - ./security:/usr/share/maypad/security
    environment:
    - SPRING_PROFILES_ACTIVE=prod
    expose:
    - 8080
  nginx:
    image: nginx
    container_name: maypad-nginx
    restart: always
    ports:
    - 80:80
    volumes:
    - ./nginx:/etc/nginx/conf.d
```

## Addtional configurations?
Using MAYPAD on a different infrastructure (AWS, Kubernetes, etc.)? Open a Pull-Request and add your configuration here for others to see.
