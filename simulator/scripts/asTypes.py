import argparse
import random
import math

parser = argparse.ArgumentParser(description='generates AStypes for initial experiment')
parser.add_argument('annotatedIplaneData', metavar='annotatedIplane', nargs = 1, help = 'annotated iplane data, generated from annotateIplaneData.py')
parser.add_argument('outFile', metavar='outputFile', nargs=1, help = 'file to output astypes')
parser.add_argument('--numTransits', metavar='numberTransits', help = 'number of transits to specify (random', default = .1)
parser.add_argument('--seedTransit', metavar='rgenerator seed',  help = 'seed for random number generator for generating transits', default=1)
parser.add_argument('--seedWiser', metavar='seed', help = 'seed for random number generator for generating wiser', default=2)

#open files based on arguments
args = parser.parse_args()
annotatedIplaneDataFile = open(args.annotatedIplaneData[0], 'r')
outFile = open(args.outFile[0], 'w')

#number annotation for node types.  This is wellknown value found in the simulator AS.java file
WISER_NUMBER = 501
TRANSIT_NUMBER = 502

#relatinship constants
CUSTOMER_PROVIDER = -1
PEER_PEER = 0
PROVIDER_CUSTOMER = 1

#asMap (integer, AS)
asMap = {}
stubASes = {} #stub ases, (integer, AS)
transitASes = [] #transit ases (integer AS) superset of tier1s and tier2s
tier1s = []
tier2s = []

#seeds
wiserSeed = args.seedWiser
transitSeed = args.seedTransit

#number of transits
numTransits =  float(args.numTransits)

#print numTransits

class AS:
#    peers = []
#    providers = []
#    customers = []
#    neighborMap = {}
#    asn = ''
    def __init__(self, asn):
        self.asn = asn
        self.peers = []
        self.providers = []
        self.customers = []
        self.neighborMap = {}        
        
    def addNeighbor(self, asNum, relationship):
        #if the asnumber is not a known neighbor then add it
        #the add it to the customer provider lists as appropriate
        if asNum not in self.neighborMap:
            self.neighborMap[asNum] = relationship
            if relationship == CUSTOMER_PROVIDER:
                self.providers.append(asNum)
            elif relationship == PEER_PEER:
                self.peers.append(asNum)
            elif relationship == PROVIDER_CUSTOMER:
                self.customers.append(asNum)
      #  else:
       #     print "not adding neighbor"



#function to parse data into an ASmap (integer - AS)
def parseIplane():
    #for each line in the iplane file
    for line in annotatedIplaneDataFile:
        split = line.split()
        AS1Num = int(split[0])
        AS2Num = int(split[1])
        relationship = int(split[2])
        if AS1Num not in asMap:
            #create as class
            AS1 = AS(AS1Num)
        else:
            AS1 = asMap[AS1Num]
        if AS2Num not in asMap:
            #create asclass
            AS2 = AS(AS2Num)
        else:
            AS2 = asMap[AS2Num]

        #add the AS relationships to AS
        if relationship == CUSTOMER_PROVIDER:
            AS2.addNeighbor(AS1Num, PROVIDER_CUSTOMER)
            AS1.addNeighbor(AS2Num, CUSTOMER_PROVIDER)
        if relationship == PEER_PEER:
            AS1.addNeighbor(AS2Num, PEER_PEER)
            AS2.addNeighbor(AS1Num, PEER_PEER)
        if relationship == PROVIDER_CUSTOMER: #included for redundancy sake, not contained in file
            AS1.addNeighbor(AS2Num, PROVIDER_CUSTOMER)
            AS2.addNeighbor(AS1Num, CUSTOMER_PROVIDER)
            print 'SHOULDN\'T BE HERE'

        #add to dictionary
        asMap[AS1Num] = AS1
        asMap[AS2Num] = AS2

#returns list of integers of ases in largest connected component, from asMap
def largestConnectedComponent():
    largestConnectedComponent = []
    verticesSeenSoFar = []
    for asMapKey in asMap:
        if asMapKey not in verticesSeenSoFar:
            connectedComponent = [] #hold tempory cc
            searchQueue = [asMapKey]
            verticesSeenSoFar.append(asMapKey)
            connectedComponent.append(asMapKey)
            #while the serach queue isn't empty, perform breadth first search
            while len(searchQueue) > 0:
                searchEntry = searchQueue.pop()
                searchAS = asMap[searchEntry]
#                print searchAS.neighborMap
            #    print "num neighbros for as: ", len(searchAS.neighborMap)
                for neighborKey in searchAS.neighborMap:
                    if neighborKey not in verticesSeenSoFar:
                        searchQueue.append(neighborKey)
                        verticesSeenSoFar.append(neighborKey)
                        connectedComponent.append(neighborKey)
            if len(connectedComponent) > len(largestConnectedComponent):
                largestConnectedComponent = connectedComponent
                  
        return largestConnectedComponent        

TIER1_THRESHOLD = 0
#computes tier1 ases, returns a list of asnums that are tier 1s
def computeTier1(largestCC):
    tier1s = []
    for element in largestCC:
        tempAS = asMap[element]
        if len(tempAS.providers) == 0:
            if len(tempAS.customers) + len(tempAS.peers) > TIER1_THRESHOLD:
                tier1s.append(element)
    return tier1s

#computes tier2s (all transits that aren't tier 1s) returns list of asnums
def computeTier2(largestCC):
    tier2s = []
    for element in largestCC:
        tempAS = asMap[element]
        if len(tempAS.providers) == 0:
            if len(tempAS.customers) + len(tempAS.peers) < TIER1_THRESHOLD:
                tier2s.append(element)
        elif len(tempAS.customers) > 0 and len(tempAS.providers) + len(tempAS.peers) > 0:
            tier2s.append(element)
    return tier2s
                

                  
#fills the dictoinary with the ASes that stubs (have no customers)                
def fillStubs(largestCC):
    for element in largestCC:
        candAS = asMap[element]
        if len(candAS.customers) == 0:
            stubASes[element] = candAS

#fills the dictionary with ases that are transits (have customers)
def fillTransit(largestCC):
    for element in largestCC:
        candAS = asMap[element]
        if len(candAS.customers) > 0:
            transitASes.append(element)
    
#writes list of ASes to file            
def putToOutput(ASList, asType):
    for element in ASList:
        outFile.write(str(element) + ' ' + str(asType) + '\n')

#returns the asn of the largest stub as with the most neighbors
def largestStub():
    largestSoFar = 0 #largest stub found so far #probably more elegant way to do this
    for element in stubASes:
        if largestSoFar == 0:
            largestSoFar = element        
        elif len(stubASes[element].neighborMap) > len(stubASes[largestSoFar].neighborMap):
            largestSoFar = element
    return largestSoFar
    
parseIplane()
#print '[debug] number of ases: ', len(asMap)
largestConnectedComponent = largestConnectedComponent()
fillStubs(largestConnectedComponent)
fillTransit(largestConnectedComponent)
tier1s = computeTier1(largestConnectedComponent)
tier2s = computeTier2(largestConnectedComponent)
print 'number of stubs: ', len(tier1s), len(tier2s), len(transitASes)
#print '[debug] number of stub ases: ', len(stubASes)
#print '[debug] largest stub as: ', largestStub()

transits = []
wiserAS = []
random.seed(wiserSeed)

tempAS = largestStub() #use largest stub as wiser as
#print '[debug] number of neighbors largest stub has', len(asMap[tempAS].neighborMap)
#rNum = random.randrange(0, len(largestConnectedComponent)-1)
#tempAS = largestConnectedComponent[rNum]
#while tempAS in transits:
#    rNum = random.randrange(0, len(largestConnectedComponent)-1)
#    tempAS = largestConnectedComponent[rNum]

wiserAS.append(tempAS)


#going to do percentage
random.seed(transitSeed)

print len(transitASes)
print len(stubASes)
print len(largestConnectedComponent)
#print 'num transits from ', numTransits, 'percent: ', int(numTransits * (len(largestConnectedComponent) - len(wiserAS)))

transitRange = tier2s #list of transits to grab from, will shrink
iterations = int(numTransits * len(transitRange)) #number of transits to
                                                 #add (easiliy modifiable to be from any tier)
print iterations
for i in range(iterations):
    #generate random number in range of largestcc, if not in transits, add it if it is, generate another and add
    rNum = random.randrange(0, len(transitRange))
    tempAS = transitRange[rNum]
    while tempAS in transits or tempAS in wiserAS:
        rNum = random.randrange(0, len(transitRange))
        tempAS = transitRange[rNum]
    transitRange.pop(rNum)
    transits.append(tempAS)


putToOutput(transits, TRANSIT_NUMBER)
putToOutput(wiserAS, WISER_NUMBER)
#print transits
#print wiserAS
