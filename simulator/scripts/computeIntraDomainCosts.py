import argparse
import Queue
import random

parser = argparse.ArgumentParser(description='Creates cache of intradomain costs for ASes')
parser.add_argument('intraASinterPoP', metavar='intra as latencies', nargs = 1, help = 'intradomain latenceis for ases')
parser.add_argument('outFile', metavar='outputFile', nargs=1, help = 'file ot output this data')

#open files based on arguments
args = parser.parse_args()
intraASFile = open(args.intraASinterPoP[0], 'r')
outFile = open(args.outFile[0], 'w')

#temporary hold for each lines adjacency list (call makeadjacency on each line)
adjacencyList = {}
asNum = ''
#computes adjacency list for a line in the intradomain file
def makeAdjacency(line):
    ASPops = line.split('|')
    asNum = ASPops[0]
    pops = ASPops[1].split(':')

    for element in pops:
        poppair = element.split(' ')
        pop1 = poppair[0]
        pop2 = poppair[1]
        latency = poppair[2]

        if pop1 not in adjacencyList:
            pairLatency = {}
            adjacencyList[pop1] = pairLatency
        if pop2 not in adjacencyList:
            pairLatency = {}
            adjacencyList[pop2] = pairLatency

        adjacencyList[pop1][pop2] = latency
        adjacencyList[pop2][pop1] = latency

#computes dijkstra between two pops
def dijkstra (pop1, pop2):
    unsettledNodes = Queue.PriorityQueue()
    settledNodes = []
    distance = {}

    if pop1 not in adjacencyList:
        return 0
    if pop2 not in adjacencyList:
        return 0

    for key in adjacencyList:
        if key != pop1:
            distance[key] = 9999
            unsettledNodes.put((9999, key))
        
    distance[pop1] = 0
    unsettledNodes.put((0, pop1))

    while not unsettledNodes.empty():
        evalNode = unsettledNodes.get()
        neighbors = adjacencyList[evalNode[1]]
        if evalNode[1] not in settledNodes:
            settledNodes.append(evalNode[1])
        for key in neighbors:
            if ket not in settledNodes:
                potentialCost = neighbors[key] + evalNode[0]
                if potentialCost < distance[key]:
                    distance[key] = potentialCost
                    unsettledNodes.put((potentialCost, key))

    if distance[pop2] == 9999:
        return 0

    return distance[pop2]

#writes a line the file for poitns of presence, does dijsstra for each possible pair
def writePoPLatencies(ASnum):
    outLine = ASnum +'|'

    for node in adjacencyList:
        for otherNode in adjacencyList:
            if node != otherNode:
                intradomainCost = dijkstra(node, otherNode)
                outLine += str(node) + ' ' + str(otherNode) + ' ' + str(intradomainCost) + ':'
    outLine += '\n'
    outFile.write(outLine)



for line in intraASFile:
    makeAdjacency(line)
    writePoPLatencies(asNum)
    
                                
                            
    
