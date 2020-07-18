#!/bin/bash

input=`echo "$1" |awk -F= '{ print $2 }'`

shift

susort < "$input" "$@"