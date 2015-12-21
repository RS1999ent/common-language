#!/bin/bash
for j in $(seq 3 10);
do
    test=$(echo "scale = 2; $j / 10" | bc)
    printf "\nTesting for stub/transit percentage: %f\n"  $test
    for i in $(seq 2 2);
    do
	printf "\nTesting on Transit seed %s\n" $i
	python ../asTypes.py --seedWiser 1 ../../formattedData/annotatedBrite.txt ../../formattedData/asTypes.txt --numTransits $test --seedTransit $i
	sleep 1
	source runCloudlabExp.sh
	sleep 5
    done
done
