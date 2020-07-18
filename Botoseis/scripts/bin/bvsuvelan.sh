#!/bin/bash

###################################################################
# bvsuvelan.sh
#
# BotoVelAn auxiliary script to make semblance panels.
# Project BotoSeis
#
# Universidade Federal do Para, Brasil.
# Instituto de Geociencias. Faculdade de Geofisica.
#
# Author:
#  Williams Lima, williams_al@gmx.com
#
###################################################################
echo "sugain tpow=$3 < $1 | sufilter f=$4 amps=$5 | suvelan nv=$6 dv=$7 fv=$8 dtratio=5 | sushw key=f2 a=$8 | sushw key=d2 a=$7" > /tmp/lixo
echo "$@" > out.txt

sugain tpow=$3 < $1 |
sufilter f=$4 amps=$5 |
suvelan nv=$6 dv=$7 fv=$8 dtratio=5 |
sushw key=f2 a=$8 | sushw key=d2 a=$7 > $2
