#!/usr/bin/env bash
rm -rf build
mkdir -p logs
cd src/main/webapp/
mkdir -p logs
rm -rf dist
npm install
ng build
npm start > logs/client.log 2>&1 &
cd -
./gradlew build
java -jar build/libs/autocheck-1.0.0-SNAPSHOT.jar > logs/server.log 2>&1 &
