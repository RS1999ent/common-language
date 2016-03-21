#!/bin/bash

echo "arg1 is the astypes to generate, 501-wiser, 504 sbgp, 505 bw 507 replacement"
echo "arg2 is where to monitor in the simulator, participating 0, all 1, gulf 2"
echo "arg3 is max number of propagated paths"
echo "arg4 is the random method, 0 for pool, 1 for weigthed, 2 for uniform"
echo "arg5 is the legend name"
echo "arg6 is whether to perfomr richworld"
astypesFile=$*astypes.txt
echo $astypesFile
echo "LEGEND "$5" ENDLEGEND\n"

#python ../richWorldASTypes.py --seedWiser 1 ../../formattedData/annotatedBrite.txt ../../formattedData/"$astypesFile" --numTransits 0 --seedTransit 1 --sim $1 --randomMethod $4 --richworld $6
#java -Xmx10000M -Dfile.encoding=Cp1252 -cp .:../../src:../../../deps/argparse4j-0.6.0.jar  simulator.Simulator ../../formattedData/annotatedBrite.txt ../../formattedData/"$astypesFile" ../../results/delete.txt --seed 1 --sim $2 --monitorFrom $2 --useBandwidth $4 --forX 0 --metric $5 --maxPaths $3
#	java -Xmx1500M -Dfile.encoding=Cp1252 -classpath "C:\cygwin64\home\David\repos\commonlanguage\simulator\src;C:\Users\spart\.m2\repository\net\sourceforge\argparse4j\argparse4j\0.6.0\argparse4j-0.6.0.jar;C:\Users\spart\.m2\repository" simulator.Simulator ../../formattedData/annotatedBrite.txt ../../formattedData/"$astypesFile" ../../results/delete.txt --seed 1 --sim $2 --monitorFrom $2 --useBandwidth $4 --forX 0 --metric $5 --maxPaths $3

for j in $(seq .1 .1 .9);
do
    test=$(echo "scale = 2; $j / 10" | bc)
    printf "\nTesting for stub/transit percentage: %f\n"  $test
    for i in $(seq 1 10);
    do
	printf "\nTesting on Transit seed %s\n" $i
	python ../richWorldASTypes.py --seedWiser 1 ../../formattedData/annotatedBrite.txt ../../formattedData/"$astypesFile" --numTransits $j --seedTransit $i --sim $1 --randomMethod $4 --richworld $6
	java -Xmx10000M -Dfile.encoding=Cp1252 -cp .:../../src:../../../deps/argparse4j-0.6.0.jar:../../src:../../../deps/snakeyaml-1.17.jar  simulator.Simulator ../../formattedData/annotatedBrite.txt ../../formattedData/"$astypesFile" ../../results/delete.txt --seed 1 --sim 3 --monitorFrom $2 --forX $j  --maxPaths $3
	java -Xmx1500M -Dfile.encoding=Cp1252 -classpath "C:\cygwin64\home\David\repos\commonlanguage\simulator\src;C:\Users\spart\.m2\repository\net\sourceforge\argparse4j\argparse4j\0.6.0\argparse4j-0.6.0.jar;C:\Users\spart\.m2\repository;C:\cygwin64\home\David\repos\commonlanguage\deps\snakeyaml-1.17.jar" simulator.Simulator ../../formattedData/annotatedBrite.txt ../../formattedData/"$astypesFile" ../../results/delete.txt --seed 1 --sim 3 --monitorFrom $2 --forX $j  --maxPaths $3
    done
done

python ../richWorldASTypes.py --seedWiser 1 ../../formattedData/annotatedBrite.txt ../../formattedData/"$astypesFile" --numTransits 1 --seedTransit 1 --sim $1 --randomMethod $4 --richworld $6
java -Xmx10000M -Dfile.encoding=Cp1252 -cp .:../../src:../../../deps/argparse4j-0.6.0.jar:../../src:../../../deps/snakeyaml-1.17.jar  simulator.Simulator ../../formattedData/annotatedBrite.txt ../../formattedData/"$astypesFile" ../../results/delete.txt --seed 1 --sim 3 --monitorFrom $2  --forX 1.000 --maxPaths $3
	java -Xmx1500M -Dfile.encoding=Cp1252 -classpath "C:\cygwin64\home\David\repos\commonlanguage\simulator\src;C:\Users\spart\.m2\repository\net\sourceforge\argparse4j\argparse4j\0.6.0\argparse4j-0.6.0.jar;C:\Users\spart\.m2\repository;C:\cygwin64\home\David\repos\commonlanguage\deps\snakeyaml-1.17.jar" simulator.Simulator ../../formattedData/annotatedBrite.txt ../../formattedData/"$astypesFile" ../../results/delete.txt --seed 1 --sim 3 --monitorFrom $2 --forX 1.000 --maxPaths $3
