#!/bin/bash

#for i in `seq -w 0 50`; do
#	if test `echo $i % 10 | bc` = 0; then
#	    wait
#	fi

#	java -Xmx512M Simulator as-relationships-short-2006.txt finallinks.valid.$i single-homed-parents.txt 1 5 > out.$i &
#java -Xmx2048M Simulator ../formattedData/annotatedIplaneData.txt finallinks.valid.$i single-homed-parents-2013.txt 1 5 > out.$i &
#done

java -Dfile.encoding=Cp1252  -classpath /cygdrive/c/cygwin64/home/David/repos/Simulator/src;/cygdrive/c/Users/spart/.m2/repository/net/sourceforge/argparse4j/argparse4j/0.6.0/argparse4j-0.6.0.jar  ../src/Simulator ../formattedData/annotatedIplaneData.txt ../formattedData/failLinks.txt --seed 1 --sim 2

wait
