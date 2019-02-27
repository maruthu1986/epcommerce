#!/usr/bin/env bash

cd $1
./synctool.sh -f -p $2 -r data
