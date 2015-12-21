#!/bin/bash
for j in $(seq 1 10);
do
    test=$(echo "scale = 2; $j / 10" | bc)
    printf "\nTesting for stub/transit percentage: %f\n"  $test
    for i in $(seq 1 5);
    do
	printf "\nTesting on Transit seed %s\n" $i
	python ../asTypes.py --seedWiser 1 ../../formattedData/annotatedBrite.txt ../../formattedData/asTypes.txt --numTransits $test --seedTransit $i
	source runCloudlabExp.sh
    done
done
