from sets import Set
import sys

f = open(sys.argv[1], 'r')

nodes = Set()
providers = {}
peers = {}
numProviders = {}
numCustomers = {}
numPeers = {}

def addNode(n):
    nodes.add(n)
    if n not in providers:
        providers[n] = []
    if n not in peers:
        peers[n] = []
    if n not in numProviders:
        numProviders[n] = 0
    if n not in numCustomers:
        numCustomers[n] = 0
    if n not in numPeers:
        numPeers[n] = 0

for line in f:
    l = line.split()
    rel = int(l[2])
    addNode(l[0])
    addNode(l[1])
    # nodes.add(l[0])

    if rel == -1:
        providers[l[0]].append(l[1])
        numProviders[l[0]] += 1
        numCustomers[l[1]] += 1

    if rel == 0:
        peers[l[0]].append(l[1])
        peers[l[1]].append(l[0])
        numPeers[l[0]] += 1
        numPeers[l[1]] += 1

    if rel == 1:
        providers[l[1]].append(l[0])
        numProviders[l[1]] += 1
        numCustomers[l[0]] += 1

    if rel < -1 or rel > 2:
        print "Out of range:", rel
        sys.exit(1)

#    print l[0], l[1], rel

for n in nodes:
    # Find all multi-homed stub ASes
    if numProviders[n] > 1 and numCustomers[n] == 0 and numPeers[n] == 0:
        # List links to their parents
        for p in providers[n]:
            print n, p, "-1"
