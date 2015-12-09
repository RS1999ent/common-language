#!/bin/bash
java -Xmx100000M -Dfile.encoding=Cp1252 -cp ~/src:~/argparse4j-0.6.0.jar simulator/Simulator ../../formattedData/annotatedIplaneData.txt ../../formattedData/asTypes.txt ../../results/delete.txt --seed 2 --sim 5


