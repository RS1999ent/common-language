#!/bin/bash
#experiment varying number of tier 2s only. list of stub ases in files as result
for j in $(seq 0 10);
do
    test=$(echo "scale = 2; $j / 10" | bc)
    printf "\nTesting for transit percentage: %f\n"  $test
 #   printf "\nTesting on Transit %s\n" $i
    python ../asTypes.py --seedWiser 2 ../../formattedData/annotatedIplaneData.txt ../../formattedData/asTypes.txt --numTransits $test --seedTransit 1
    java -Xmx6000M -Dfile.encoding=Cp1252 -classpath "C:\cygwin64\home\David\repos\commonlanguage\simulator\src;C:\Users\spart\.m2\repository\net\sourceforge\argparse4j\argparse4j\0.6.0\argparse4j-0.6.0.jar;C:\Users\spart\.m2\repository" simulator.Simulator ../../formattedData/annotatedIplaneData.txt ../../formattedData/asTypes.txt ../../results/$(echo results$j) --seed 2 --sim 7

done
