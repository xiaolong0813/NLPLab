#!/usr/bin/env bash
npm install & ng build
ng serve > client.log 2>&1 &
