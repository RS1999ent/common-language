import numpy
import pylab
import matplotlib.pyplot as plt
from matplotlib.backends.backend_pdf import PdfPages
import argparse
import random
import math
import re

DEBUG = 0

parser = argparse.ArgumentParser(description='Creates a pdf graph from experimentoutput')
parser.add_argument('experimentOutput', metavar='expOutput', nargs = 1, help = 'experimental output, relevent info should have GRAPH ')
#parser.add_argument('pdfName', metavar='nameOfPdf', nargs=1, help = 'The name of the pdf')
parser.add_argument('--xlabel', metavar='x axis label', help = 'label for the x axis')
parser.add_argument('--ylabel', metavar='y axis label', help = 'label for the y axis')
parser.add_argument('--title', metavar = 'graph title', help = 'title for the graph')

#open files based on arguments
args = parser.parse_args()
inputData = open(args.experimentOutput[0], 'r')
pdfName = args.experimentOutput[0] + '.pdf'
xlabel = args.xlabel
ylabel = args.ylabel
title = args.title
xyDict = {}
X = []
Y = []
ymax = 0

def parseInput():
    p = re.compile('GRAPH.*ENDGRAPH')
    for line in inputData:
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
    if DEBUG:
        print "xyDict: ", xyDict

#computes the average from a list of floats
def getAverage(list):
    sum = 0
    for item in list:
        sum += float(item)
    average = sum/len(list)
    if DEBUG:
        print "Average: ",average
    return average
        
def fillXY():
    for key in sorted(xyDict):
        xAverage = getAverage(xyDict[key])
        global ymax
        if xAverage > ymax:
            ymax = xAverage
        X.append(key)
        Y.append(xAverage)
    if DEBUG:
        print "x: ", X
        print "y: ", Y
        

parseInput()
fillXY()
        
pp = PdfPages(pdfName)
arr = numpy.asarray

x = arr(X)
y = arr(Y)
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
plt.plot(x,y)
#plt.axis([0,1, 0, 1520000])
plt.ylabel(ylabel)
plt.xlabel(xlabel)
plt.title(title)
pp.savefig()
pp.close()
#plt.show()

#pylab.errorbar(x, y, yerr=c)
#pylab.show()
