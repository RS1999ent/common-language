#!/bin/bash
java -Xmx120000M -cp .:../../src:/users/tranlam/argparse4j-0.6.0.jar  simulator/Simulator ../../formattedData/annotatedIplaneData.txt ../../formattedData/asTypes.txt ../../results/delete.txt --seed 2 --sim 5


