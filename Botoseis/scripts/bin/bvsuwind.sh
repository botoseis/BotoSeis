#!/bin/bash

susort < $1 cdp offset |
suwind 2>/dev/null key=cdp $3 $4 |
sugethw offset |
sed 3q |
sed -e '/^$/ d' |
sed 's/.*offset=//' | {    
    read offa
    read offb;

    doff=$[(offb-offa)];
    
    susort < $1 cdp offset |
    suwind 2>/dev/null key=cdp $3 $4 |
    sushw key=f2 a=$offa |
    sushw key=d2 a=$doff > $2;
}



