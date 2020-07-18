#!/bin/bash

###################################################################
# bvcvs.sh
#
# BotoVelAn auxiliary script to make CVS panels.
# Project BotoSeis
#
# Universidade Federal do Para, Brasil.
# Instituto de Geociencias. Faculdade de Geofisica.
#
# Author:
#  Williams Lima, williams_al@gmx.com
#
# Adapted from CWP/SU iva.sh script
###################################################################

indata=$1
outdata=$2
picknow=$3            # CMP used to make the CVS panels
dcdp=$4
XX=$5
nc=$6
fv=$7
lv=$8

X=$[$XX - 1]       # Number of CMPs for windowing

# Window CMPs around central CMP
k1=$[$picknow - ($X*$dcdp) / 2]
k2=$[$picknow + ($X*$dcdp) / 2]

suwind < $indata key=cdp min=$k1 max=$k2 > tmp99 &&  susort < tmp99 cdp offset > tmp0

nt=`sugethw ns < tmp0 | sed 1q | sed 's/.*ns=//'`
dt=`sugethw dt < tmp0 | sed 1q | sed 's/.*dt=//'`
dt=`bc -l <<EOF
scale=6
$dt / 1000000
EOF`

dc=`bc -l <<EOF
( $lv - $fv ) / ( $nc - 1 )
EOF`

# Calculate trace spacing for CVS plot
m=`bc -l <<EOF
( $lv - $fv ) / ( ( $nc - 1 ) * $XX)
EOF`

# CVS velocity loop
j=1

>tmp1

while [ $j -le $nc ]
do
  vel=`echo "$fv + $dc * ( $j - 1 )" | bc`

  sunmo < tmp0 vnmo=$vel verbose=0 2>/dev/null |
  sustack >> tmp1
  sunull >> tmp1 nt=$nt dt=$dt ntr=2

  j=$[$j + 1]
done

# Compute lowest velocity for annotating CVS plot
loV=`bc -l <<EOF
$fv - ( $X / 2) * $m
EOF`

echo $outdata > out.txt

sushw < tmp1 key=f2 a=$fv | sushw key=d2 a=$dc > $outdata

rm tmp*
