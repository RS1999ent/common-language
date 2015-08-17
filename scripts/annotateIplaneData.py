import argparse

parser = argparse.ArgumentParser(description="Annotate iplane data with AS relationship. Requires interAS file produced by intraAsInterPop script and converted CAIDA file produced by convert_caida_dataset")
parser.add_argument('interAS', metavar='interAS', nargs=1, help = "interAS file")
parser.add_argument('convertedCAIDA', metavar='CAIDA', nargs=1, help = "converted CAIDA file")
parser.add_argument('outFile', metavar = 'outputFile', nargs=1, help = 'file to output annotated data')
parser.add_argument('logFile', metavar = 'logfile', nargs=1, help = 'log file holding unmatched data')

args = parser.parse_args()
interASFile = open(args.interAS[0], 'r')
convertedCAIDAFile = open(args.convertedCAIDA[0], 'r')
outFile = open(args.outFile[0], 'w')
noMatchFile = open(args.logFile[0], 'w')

CAIDAHash = {}


def preProcessCAIDA():
    for line in convertedCAIDAFile:
        split = line.split()
        relationship = int(split[2])
        key = split[0] + ' ' + split[1];
        if key not in CAIDAHash:
            CAIDAHash[key] = [relationship]
            
#        if relationship == 0:
#            peerKey = split[1] + ' ' + split[0]
#            if peerKey not in CAIDAHash:
#                CAIDAHash[peerKey] = [relationship]


preProcessCAIDA()
linksTotal = 0
linksMatched = 0

nodesMatched = []
nodesSeen = []
for line in interASFile:
    split = line.split()
    AS1 = split[0]
    AS2 = split[1]
    pop1 = split[3]
    pop2 = split[4]
    latency = split[2]

    #gather link statistics
    linksTotal += 1
    if AS1 not in nodesSeen:
        nodesSeen.append(AS1)
    if AS2 not in nodesSeen:
        nodesSeen.append(AS1)
        
    key1 = AS1 + ' ' + AS2
    key2 = AS2 + ' ' + AS1

    if key1 in CAIDAHash:
        outFile.write(AS1 + ' ' + AS2 + ' ' + str(CAIDAHash[key1][0]) + ' ' + latency + ' ' + pop1 + ' ' + pop2 + '\n')
        linksMatched+= 1
        if AS1 not in nodesMatched:
            nodesMatched.append(AS1)
        if AS2 not in nodesMatched:
            nodesMatched.append(AS2)
        if not len(CAIDAHash[key1]) > 1:
                   CAIDAHash[key1] = (CAIDAHash[key1][0], True)
#        print AS1, AS2, CAIDAHash[key1], latency, pop1, pop2
    elif key2 in CAIDAHash and key1 not in CAIDAHash:
        outFile.write(AS2 + ' ' + AS1 + ' ' + str(CAIDAHash[key2][0]) + ' ' + latency + ' ' + pop2 + ' ' + pop1 + '\n')
        linksMatched+= 1
        if AS1 not in nodesMatched:
            nodesMatched.append(AS1)
        if AS2 not in nodesMatched:
            nodesMatched.append(AS2)
        if not len(CAIDAHash[key2]) > 1:
                   CAIDAHash[key2] = (CAIDAHash[key2][0], True)
 #       print AS2, AS1, CAIDAHash[key2], latency, pop2, pop1
    else:
        noMatchFile.write('caida > iplane missing: ' +  line)

unMatched = 0        
for element in CAIDAHash:
    if not len(CAIDAHash[element]) > 1:
        noMatchFile.write('iplane > caida missing' + element + ' ' + str(CAIDAHash[element])+ '\n')
        unMatched += 1
    
print "Links matched: ", linksMatched
print "Links total: ", linksTotal, "\n"
print "NodesMatched: ", len(nodesMatched)
print "NodesTotal: ", len(nodesSeen), '\n'
print "link in caida not in iplane: ", unMatched
print "total caida links: ", len(CAIDAHash)
