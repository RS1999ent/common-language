#!/bin/bash

echo arg1 is the astypes to generate, 501-wiser, 504 sbgp, 505 bw 507 replacement
echo arg2 is the sim to perform in the simulator; 4 replacement, 5 truecost
echo arg3 is where to monitor in the simulator, participating 0, all 1, gulf 2
echo arg4 is whether to use bandwidth numbers in the simulator
echo arg5 is what metric to use RIBMETRIC 0, FIBMETRIC 1

astypesFile=$*astypes.txt
echo $astypesFile
for j in $(seq 0 10);
do
    test=$(echo "scale = 2; $j / 10" | bc)
    printf "\nTesting for stub/transit percentage: %f\n"  $test
    for i in $(seq 1 1);
    do
	printf "\nTesting on Transit seed %s\n" $i
	python ../asTypes.py --seedWiser 1 ../../formattedData/annotatedBrite.txt ../../formattedData/"$astypesFile" --numTransits $test --seedTransit $i --sim $1
#	java -Xmx4000M -Dfile.encoding=Cp1252 -cp .:../../src:../../../deps/argparse4j-0.6.0.jar  simulator.Simulator ../../formattedData/annotatedBrite.txt ../../formattedData/"$astypesFile" ../../results/delete.txt --seed 1 --sim $2 --monitorFrom $3 --useBandwidth $4 --forX $test --metric $5
	java -Xmx1500M -Dfile.encoding=Cp1252 -classpath "C:\cygwin64\home\David\repos\commonlanguage\simulator\src;C:\Users\spart\.m2\repository\net\sourceforge\argparse4j\argparse4j\0.6.0\argparse4j-0.6.0.jar;C:\Users\spart\.m2\repository" simulator.Simulator ../../formattedData/annotatedBrite.txt ../../formattedData/"$astypesFile" ../../results/delete.txt --seed 1 --sim $2 --monitorFrom $3 --useBandwidth $4 --forX $test --metric $5
    done
done
