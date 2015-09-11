#!/bin/bash
for j in $(seq 0 10);
do
    test=$(echo "scale = 2; $j / 10" | bc)
    printf "\nTesting for transit percentage: %f\n"  $test
  #  for i in $(seq 1 10);
  #  do
#	printf "\nTesting on Transit %s\n" $i
	python ../asTypes.py --seedWiser 2 ../../formattedData/annotatedCAIDAData.txt ../../formattedData/asTypes.txt --numTransits $test --seedTransit 1 #$i
	java -Xmx6000M -Dfile.encoding=Cp1252 -classpath "C:\cygwin64\home\David\repos\commonlanguage\simulator\src;C:\Users\spart\.m2\repository\net\sourceforge\argparse4j\argparse4j\0.6.0\argparse4j-0.6.0.jar;C:\Users\spart\.m2\repository" simulator.Simulator ../../formattedData/annotatedCAIDAData.txt ../../formattedData/asTypes.txt ../../results/delete.txt --seed 2 --sim 6
 #   done
done
