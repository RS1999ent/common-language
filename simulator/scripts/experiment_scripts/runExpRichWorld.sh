#!/bin/bash

echo arg1 "is fraction to main protocol" 
echo arg2 "is the max paths for replacement protocol"
echo arg3 "what protocol to make up main fraction i.e. arg1, 501 wiser 505 bw 507 replacmeent"
#echo arg3 is where to monitor in the simulator, participating 0, all 1, gulf 2
#echo arg4 is whether to use bandwidth numbers in the simulator
#echo arg5 is what metric to use RIBMETRIC 0, FIBMETRIC 1
#echo arg6 is max number of propagated paths
#echo arg7 is the legend name
astypesFile=$*astypes.txt
echo $astypesFile
echo "LEGEND "$7" ENDLEGEND\n"



    test=$(echo "scale = 2; $1 / 10" | bc)
    printf "\nTesting for stub/transit percentage: %f\n"  $test
    for i in $(seq 1 10);
    do
	printf "\nTesting on Transit seed %s\n" $i
	python ../richWorldASTypes.py --seedWiser 1 ../../formattedData/annotatedBrite.txt ../../formattedData/"$astypesFile" --numTransits $1 --seedTransit $i --sim $3
#	java -Xmx10000M -Dfile.encoding=Cp1252 -cp .:../../src:../../../deps/argparse4j-0.6.0.jar  simulator.Simulator ../../formattedData/annotatedBrite.txt ../../formattedData/"$astypesFile" ../../results/delete.txt --seed 1 --sim 3 --monitorFrom 1 --useBandwidth 0 --forX $1 --metric 0 --maxPaths $2
	java -Xmx1500M -Dfile.encoding=Cp1252 -classpath "C:\cygwin64\home\David\repos\commonlanguage\simulator\src;C:\Users\spart\.m2\repository\net\sourceforge\argparse4j\argparse4j\0.6.0\argparse4j-0.6.0.jar;C:\Users\spart\.m2\repository" simulator.Simulator ../../formattedData/annotatedBrite.txt ../../formattedData/"$astypesFile" ../../results/delete.txt --seed 1 --sim 3 --monitorFrom 1 --useBandwidth 0 --forX $1 --metric 0 --maxPaths $2
    done

