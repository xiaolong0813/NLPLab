#!/usr/bin/env bash

./gradlew --stop
sudo kill `sudo lsof -t -i:4200`
sudo kill `sudo lsof -t -i:7890`
