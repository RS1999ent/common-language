import numpy
import pylab
import matplotlib.pyplot as plt
from matplotlib.backends.backend_pdf import PdfPages
import argparse
import random
import math
import re

DEBUG = 1

parser = argparse.ArgumentParser(description='Creates a pdf graph from experimentoutput')
parser.add_argument('experimentOutput', metavar='expOutput', nargs ="+", help = 'experimental output, relevent info should have GRAPH and LEGEND ENDLEGEND')
#parser.add_argument('pdfName', metavar='nameOfPdf', nargs=1, help = 'The name of the pdf')
parser.add_argument('--xlabel', metavar='x axis label', help = 'label for the x axis')
parser.add_argument('--ylabel', metavar='y axis label', help = 'label for the y axis')
parser.add_argument('--title', metavar = 'graph title', help = 'title for the graph')
parser.add_argument('--scale', metavar = 'scale y axis', help = 'value to scale down by', default=1)

#open files based on arguments
args = parser.parse_args()
inputDataList = args.experimentOutput
pdfName = args.title + '.pdf'
xlabel = args.xlabel
ylabel = args.ylabel
title = args.title
scalingFactor = args.scale
rawData = [] #list of tuples (xydictionary, legendname)
X = []
Y = []
ymax = 0

def parseInput():
    for entry in inputDataList:
        expOutput = open(entry, 'r')
        p = re.compile('GRAPH.*ENDGRAPH')
        legendRE = re.compile('LEGEND.*ENDLEGEND')
        legendName = ''
        xyDict = {}
        for line in expOutput:
            if p.search(line):
                m = p.search(line)
                line = m.group()
                splitLine = line.split()
                x = splitLine[1]
                y = splitLine[2]
                if DEBUG:
                    print x,y
                if x not in xyDict:
                    xyDict[x] = []
                xyDict[x].append(y)
                
            if legendRE.search(line):
                m = legendRE.search(line)
                line = m.group()
                splitLine = line.split()
                splitLine = splitLine[1:-1]
                legendName = legendName.join(splitLine)
        rawData.append((xyDict, legendName))
        if DEBUG:
            print rawData

#computes the average from a list of floats
def getAverage(list):
    sum = 0
    for item in list:
        sum += float(item)
    average = sum/len(list)
    if DEBUG:
        print "sum: ", sum
        print "listlen: ", len(list)
        print "Average: ",average
    return average

#return list of y values
#input dictionary of xyvalues
def getY(xyDict):
    y = []
    for key in sorted(xyDict):
        xAverage = getAverage(xyDict[key])
        y.append(xAverage/float(scalingFactor))
    if DEBUG:
        print "y: ", y
    return y

#get x values associated with this dictionary
def getX(xyDict):
    x = []
    for key in sorted(xyDict):
        x.append(key)
    if DEBUG:
        print 'x: ', x
    return x

parseInput()
#fillXY()
        
pp = PdfPages(pdfName)
arr = numpy.asarray

#y = arr(Y)
#y = arr([[1457270.5],
#         [1479158.9],
#         [1476073.413],
#         [1444782.44],
#         [1372953.26],
#         [1308710.05],
#         [1187560.917],
#         [1141159.451],
#         [1018126.513],
#         [863181.1922],
#         [698950.2]]).flatten()
#
#c= arr([[ 49.81, 34.67], #.1
#        [65.74, 35.85], #.2
#        [32.56, 42.57], #.3
#        [40.29, 38.37], #.4
#        [35.07, 26.19], #.5
#        [32.9, 15.24], #.6
#        [13.71, 15.22],   #.7
#        [20.77, 48.8],  #.8
#        [5.78, 12.43],  #.9
#        [0, 0]]).T     #1


#plt.figure()
#plt.errorbar(x, y, yerr=0)
legendHandles = []
for tuple in rawData:
    xyDict = tuple[0]
    legendName = tuple[1]
    if DEBUG:
        print 'legendname: ', legendName
    y = getY(xyDict)
    x = getX(xyDict)
    handle, =plt.plot(arr(x),arr(y), label=legendName)
    legendHandles.append(handle)

plt.legend(fontsize='small', loc=2)
    
#plt.axis([0,1, 0, 1520000])
plt.ylabel(ylabel)
plt.xlabel(xlabel)
plt.xticks(numpy.arange(0, 1.1, .1))
pylab.ylim(ymin=0)
pp.savefig()
pp.close()
plt.show()

#pylab.errorbar(x, y, yerr=c)
#pylab.show()
