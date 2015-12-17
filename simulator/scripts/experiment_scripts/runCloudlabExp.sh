#!/bin/bash
java -Xmx120000M -Dcom.sun.management.jmxremote.port=9999 \
-Dcom.sun.management.jmxremote.authenticate=false \
-Dcom.sun.management.jmxremote.ssl=false \
 -cp .:../../src:/users/tranlam/argparse4j-0.6.0.jar  simulator/Simulator ../../formattedData/annotatedCAIDAData.txt ../../formattedData/asTypes.txt ../../results/delete.txt --seed 2 --sim 5


