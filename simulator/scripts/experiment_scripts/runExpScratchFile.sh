#!/bin/bash
for j in $(seq 1 10);
do
    test=$(echo "scale = 2; $j / 10" | bc)
    printf "\nTesting for stub/transit percentage: %f\n"  $test
    for i in $(seq 1 10);
    do
	printf "\nTesting on Transit seed %s\n" $i
	python ../asTypes.py --seedWiser 1 ../../formattedData/annotatedBrite.txt ../../formattedData/asTypes.txt --numTransits $test --seedTransit $i --sim 505
	java -Xmx6000M -Dfile.encoding=Cp1252 -classpath "C:\cygwin64\home\David\repos\commonlanguage\simulator\src;C:\Users\spart\.m2\repository\net\sourceforge\argparse4j\argparse4j\0.6.0\argparse4j-0.6.0.jar;C:\Users\spart\.m2\repository" simulator.Simulator ../../formattedData/annotatedBrite.txt ../../formattedData/asTypes.txt ../../results/delete.txt --seed 1 --sim 5 --allMonitor 1 --useBandwidth 1
    done
done
