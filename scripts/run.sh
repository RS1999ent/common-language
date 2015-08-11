#!/bin/bash

for i in `seq -w 0 50`; do
	if test `echo $i % 10 | bc` = 0; then
	    wait
	fi

#	java -Xmx512M Simulator as-relationships-short-2006.txt finallinks.valid.$i single-homed-parents.txt 1 5 > out.$i &
	java -Xmx2048M Simulator as-relationships-short-20131101.txt finallinks.valid.$i single-homed-parents-2013.txt 1 5 > out.$i &
done

wait
