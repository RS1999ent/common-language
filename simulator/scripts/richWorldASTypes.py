import argparse
import random
import math


DEBUG = 0


parser = argparse.ArgumentParser(description='generates AStypes for initial experiment')
parser.add_argument('annotatedData', metavar='annotatedIplane', nargs = 1, help = 'annotated data, generated from annotateIplaneData.py or annotateCAIDAData.py')
parser.add_argument('outFile', metavar='outputFile', nargs=1, help = 'file to output astypes')
parser.add_argument('--numTransits', metavar='fraction', help = 'what fraction of ases does the primary transit take up', default = .1)
parser.add_argument('--seedTransit', metavar='rgenerator seed',  help = 'seed for random number generator for generating transits', default=1)
parser.add_argument('--seedWiser', metavar='seed', help = 'seed for random number generator for generating wiser', default=2)
parser.add_argument('--sim', metavar='sim', help = 'what is the primary transit type (501=wiser, 504 = sbgp, 505=bw, 507 = replacement)', default=501)
parser.add_argument('--randomMethod', metavar='what random method', help = 'how do pick ases randomly 0 for equally from transits and stubs, 1 weighted by degree, 2 uniform', default = 0)
parser.add_argument('--richworld', metavar = 'use richworld generation', default= 1)

#open files based on arguments
args = parser.parse_args()
annotatedDataFile = open(args.annotatedData[0], 'r')
outFile = open(args.outFile[0], 'w')

#number annotation for node types.  This is wellknown value found in the simulator AS.java file
BGP_NUMBER = 500
WISER_NUMBER = 501
TRANSIT_NUMBER = 502
SBGP_NUMBER = 504
SBGP_TRANSIT = 503
BW_NUMBER = 505
BW_TRANSIT = 506
REPLACEMENT_NUMBER = 507
#relatinship constants
CUSTOMER_PROVIDER = -1
PEER_PEER = 0
PROVIDER_CUSTOMER = 1

#random method
POOLS = 0
WEIGHTED = 1
UNIFORM = 2

distList = [BGP_NUMBER, WISER_NUMBER, BW_NUMBER, REPLACEMENT_NUMBER]

#asMap (integer, AS)
asMap = {}
stubASes = {} #stub ases, (integer, AS)
transitASes = [] #transit ases (integer AS) superset of tier1s and tier2s
tier1s = []
tier2s = []

#seeds
wiserSeed = args.seedWiser
transitSeed = args.seedTransit

#random method
weighted = int(args.randomMethod)

#number of transits
numTransits = float(args.numTransits) #1/round(1/float(args.numTransits))
richworld = int(args.richworld)

#simtype
sim = args.sim;

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
    for line in annotatedDataFile:
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
        if len(tempAS.providers) == 0 and len(tempAS.customers) > 0 :
     #       if len(tempAS.customers) + len(tempAS.peers) > TIER1_THRESHOLD:
            tier1s.append(element)
    return tier1s

#computes tier2s (all transits that aren't tier 1s) returns list of asnums
def computeTier2(largestCC):
    tier2s = []
    for element in largestCC:
        tempAS = asMap[element]
        if len(tempAS.providers) > 0 and len(tempAS.customers) > 0:
     #       if len(tempAS.customers) + len(tempAS.peers) < TIER1_THRESHOLD:
     #           tier2s.append(element)
#        elif len(tempAS.customers) > 0 and len(tempAS.providers) + len(tempAS.peers) > 0:
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
    iter = 0
    for element in ASList:
        outFile.write(str(element) + ' ' + str(asType) + '\n')

#assign the remaining astypes evenly
#remaining ases- list of asnums of remaining unassigned ases
#alreadyassigned - type number of the ases already assigned (so not to assing more)
#distribution - list of type numbers of ASes to assign
def assignRemaining(remainingASes, alreadyAssigned, distribution):
    iter = 0
#    print 'remaingases: ', remainingASes
    for element in remainingASes:
        distElement = None
        while True:
            distElement = iter % len(distribution)
            if distribution[distElement] != alreadyAssigned:
                break
            iter+= 1
        if distribution[distElement] != BGP_NUMBER:
#            print 'writing: ', str(element), ', ', str(distribution[distElement]) 
            outFile.write(str(element) + ' ' + str(distribution[distElement]) + '\n')
        iter+=1
        
            
        
    

#returns the asn of the largest stub as with the most neighbors
def largestStub():
    largestSoFar = 0 #largest stub found so far #probably more elegant way to do this
    for element in stubASes:
        if largestSoFar == 0:
            largestSoFar = element        
        elif len(stubASes[element].neighborMap) > len(stubASes[largestSoFar].neighborMap):
            largestSoFar = element
    return largestSoFar


#returns a list of ases with weight based on their degree
def createDegreeWeightedList(asList):
    totalNeighbors = 0
    weightedList = []
    for element in asList:
        AS = asList[element]
        totalNeighbors += len(AS.neighborMap)
    for element in asList:
        AS = asList[element]
        weightedList.append((element, len(AS.neighborMap)))
    return weightedList
    
        
        

#randomily create a list of ases as a percentage of the choice list
#returns the chosen as list and the modified choiceList
def straightRandom(choiceList, percentToChoose):
    iterations = int(percentToChoose * len(choiceList)) #number of transits to
                                                 #add (easiliy modifiable to be from any tier)
                                                 #print iterations
    transits = []
    for i in range(iterations):
        #generate random number in range of largestcc, if not in transits, add it if it is, generate another and add
        rNum = random.randrange(0, len(choiceList))
        tempAS = choiceList[rNum]
        while tempAS in transits:
            rNum = random.randrange(0, len(transitRange))
            tempAS = transitRange[rNum]
        choiceList.pop(rNum)
        transits.append(tempAS)
    return transits, choiceList

#returns chosen as list given weights
def weightedRandom(choices, percentToChoose):
    iterations = int(percentToChoose * len(choices))
    chosenASes = []
    leftovers = []
    for i in range(iterations):
        total = sum(w for c, w in choices)
        r = random.uniform(0, total)
        upto = 0
        for c, w in choices:
            if upto + w >= r:
                chosenASes.append(c)
                choices.remove((c,w))
                break
            upto += w
    for element in choices:
        leftovers.append(element[0])
    return chosenASes, leftovers

    
###############
###start######
##########
parseIplane()
if DEBUG:
    print '[debug] number of ases: ', len(asMap)
#largestConnectedComponent = asMap.keys()
largestConnectedComponent = largestConnectedComponent()
if DEBUG:
    print '[debug] largestcc: ', len(largestConnectedComponent)
fillStubs(largestConnectedComponent)
fillTransit(largestConnectedComponent)
tier1s = computeTier1(largestConnectedComponent)
tier2s = computeTier2(largestConnectedComponent)
if DEBUG:
    print 'number of stubs: ',len(stubASes)
    print 'num transits: ' , len(transitASes)
    print 'num tier1s: ', len(tier1s)
    print 'num tier2s: ', len(tier2s)

##################
#check
############
i = 0
for element in tier1s:
    if element  in stubASes:
        i+=1
if DEBUG:
    print i        

###########
#endcheck
##########

#print '[debug] number of stub ases: ', len(stubASes)
#print '[debug] largest stub as: ', largestStub()

transits = []
wiserAS = []
random.seed(wiserSeed)

#tempAS = largestStub() #use largest stub as wiser as
#print '[debug] number of neighbors largest stub has', len(asMap[tempAS].neighborMap)
rNum = random.randrange(0, len(stubASes)-1)
tempAS = stubASes.keys()[rNum]
#tempAS = largestConnectedComponent[rNum]
#while tempAS in transits:
#    rNum = random.randrange(0, len(largestConnectedComponent)-1)
#    tempAS = largestConnectedComponent[rNum]

#wiserAS.append(tempAS) # COMMENTED THIS OUT, ANOUNCING FROM ALL STUBS NOW


#going to do percentage
random.seed(transitSeed)

#print len(transitASes)
#print len(stubASes)
#print len(largestConnectedComponent)
#print 'num transits from ', numTransits, 'percent: ', int(numTransits * (len(largestConnectedComponent) - len(wiserAS)))



#stubsChosen = []
stubKeys = stubASes.keys()
iterations = int(numTransits * (len(stubKeys))) 
#iterations = int(1 * (len(stubKeys)-1)) this is if you want all stubs chosen
if DEBUG:
    print "stub interations: ", iterations
random.seed(transitSeed)

transitRange = transitASes
chosenASes = []
leftoverASes = []
if weighted == POOLS:
    transitsChosen, leftoverTransits = straightRandom(transitRange, numTransits)
    stubsChosen, leftoverStubs = straightRandom(stubKeys, numTransits)
    chosenASes = transitsChosen + stubsChosen
    leftoverASes = leftoverTransits + leftoverStubs
elif weighted == WEIGHTED:
    weightedChoices = createDegreeWeightedList(asMap)
    chosen, leftovers = weightedRandom(weightedChoices, numTransits)
    chosenASes = chosen
    leftoverASes = leftovers
elif weighted == UNIFORM:
    chosenASes, leftoverASes = straightRandom(asMap.keys(), numTransits)
    
if int(sim) == WISER_NUMBER:
    putToOutput(chosenASes, WISER_NUMBER)
    if richworld:
        assignRemaining(leftoverASes, WISER_NUMBER, distList)
if SBGP_NUMBER == int(sim):
#    putToOutput(transitsChosen, SBGP_TRANSIT)
    putToOutput(chosenASes, SBGP_NUMBER)
    
if BW_NUMBER == int(sim):
    putToOutput(chosenASes, BW_TRANSIT)
    if richworld:
        assignRemaining(leftoverASes, BW_NUMBER, distList)
if REPLACEMENT_NUMBER == int(sim):
    putToOutput(chosenASes, REPLACEMENT_NUMBER)
    if richworld:
        assignRemaining(leftoverASes, REPLACEMENT_NUMBER, distList)

    
#putToOutput(wiserAS, WISER_NUMBER) #commented this out, we announce from the stubs
#print transits
#print wiserAS
