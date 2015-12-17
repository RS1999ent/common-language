#!/bin/bash
javac -g -cp ../../../deps/argparse4j-0.6.0.jar ../../src/integratedAdvertisement/*.java ../../src/simulator/*.java

java -Xmx120000M -Dcom.sun.management.jmxremote.port=9999 \
-Dcom.sun.management.jmxremote.authenticate=false \
-Dcom.sun.management.jmxremote.ssl=false \
 -cp .:../../src:../../../deps/argparse4j-0.6.0.jar  simulator/Simulator ../../formattedData/annotatedCAIDAData.txt ../../formattedData/asTypes.txt ../../results/delete.txt --seed 2 --sim 5


