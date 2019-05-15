#!/usr/bin/env bash
npm install & ng build
ng serve --host 0.0.0.0 > client.log 2>&1 &
