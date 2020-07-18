#!/bin/bash

path=`echo "$1" |awk -F= '{ print $2 }'`
appendFlag=`echo "$2" |awk -F= '{ print $2 }'`

if [ "$appendFlag" = "yes" ]
then
    cat /dev/stdin >> "$path"
else
    cat /dev/stdin > "$path"
fi