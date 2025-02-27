import argparse
import random

parser = argparse.ArgumentParser(description='generates file of AS stubs arranged by number of neighbors')
parser.add_argument('annotatedIplaneData', metavar='annotatedIplane', nargs = 1, help = 'annotated iplane data, generated from annotateIplaneData.py')
parser.add_argument('outFile', metavar='outputFile', nargs=1, help = 'file to output astypes')

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
stubASes = []

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

    def __lt__(self, other):
        return len(self.neighborMap) < len(other.neighborMap)
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
                
                  
#fills the dictoinary with the ASes that stubs (have no customers)                
def fillStubs(largestCC):
    for element in largestCC:
        candAS = asMap[element]
        if len(candAS.customers) == 0:
            stubASes.append(candAS)

#writes list of ASes to file with number of neighbors
def putToOutput(ASList):
    for element in ASList:
        outFile.write(str(element.asn) + ' ' + str(len(element.neighborMap)) + '\n')

#returns the asn of the largest stub as with the most neighbors
#def largestStub():
#    largestSoFar = 0 #largest stub found so far #probably more elegant way to do this
#    for element in stubASes:
#        if largestSoFar == 0:
#            largestSoFar = element        
#        elif len(stubASes[element].neighborMap) > len(stubASes[largestSoFar].neighborMap):
#            largestSoFar = element
#    return largestSoFar
    
parseIplane()
#print '[debug] number of ases: ', len(asMap)
largestConnectedComponent = largestConnectedComponent()
fillStubs(largestConnectedComponent)
stubASes.sort(reverse=True)
putToOutput(stubASes)
#print '[debug] number of stub ases: ', len(stubASes)
#print '[debug] largest stub as: ', largestStub()

