#!/bin/sh

##########################################3###
# dvsort.sh
#
# Auxiliary script used by BotoView
#
# Author: Williams Lima, williams_al@gmx.com
#
# Creation data: 26, November 2008
#
##############################################

input="$1"

shift

susort < $input > dvsort-tmp.su "$@"