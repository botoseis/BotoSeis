#!/bin/bash

cat /dev/stdin > /tmp/in_ivelan.su

javacmd=java
`$javacmd -cp "$BOTOSEIS_ROOT/../libs/*:$BOTOSEIS_ROOT/../dist/botoseis.jar" botoseis.ivelan.temp.MainWindow  in=/tmp/in_ivelan.su "$@"`
