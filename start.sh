#!/usr/bin/env bash
rm -rf build
cd src/main/webapp/
rm -rf dist
npm install
ng build
cd -
./gradlew build
java -jar build/libs/autocheck-1.0.0-SNAPSHOT.jar > logs/server.log 2>&1 &
