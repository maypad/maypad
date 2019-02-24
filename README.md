# MAYPAD
Manage all your Projects and Dependencies

[![Build Status](https://travis-ci.com/juliantodt/maypad.svg?token=WavrDotJ1tgCTx4qgp2h&branch=master)](https://travis-ci.com/juliantodt/maypad)
[![Docker Build status](https://img.shields.io/badge/docker%20build-automated-blue.svg)](https://hub.docker.com/r/juliantodt/maypad)


## About
MAYPAD allows you to manage your projects lifecycle in one centralized place, handling CI/CD-pipelines and dependencies. It supports various tools and plattforms - you can be sure that your project can be added. MAYPAD consists of a spring-boot backend and an angular frontend conveniently running as a docker container.

MAYPAD was developed as a "Praxis of Software-Development" project of students at @KITedu for @FraunhoferIOSB. The documents during development can be found under [https://github.com/juliantodt/maypad-docs](https://github.com/juliantodt/maypad-docs) (restricted access).

## Server Setup
To start using MAYPAD you need to setup a server running the MAYPAD docker container and a MySQL database. More information about can be found here: [docs/setup.md](docs/setup.md)

## Using MAYPAD
Add your projects [docs/add.md](docs/add.md), configure them [docs/config.md](docs/config.md) and then use the intuitive web frontend.
