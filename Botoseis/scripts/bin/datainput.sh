#!/bin/bash

path=`echo "$1" |awk -F= '{ print $2 }'`

cat "$path"