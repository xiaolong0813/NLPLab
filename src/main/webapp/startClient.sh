#!/usr/bin/env bash
npm install & npm build
npm start > client.log 2>&1 &
