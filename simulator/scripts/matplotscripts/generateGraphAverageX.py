import numpy
import pylab
import matplotlib.pyplot as plt
import matplotlib.gridspec as gridspec
from matplotlib.backends.backend_pdf import PdfPages
import argparse
import random
import math
import re

DEBUG = 0

parser = argparse.ArgumentParser(description='Creates a pdf graph from experimentoutput')
parser.add_argument('experimentOutput', metavar='expOutput', nargs ="+", help = 'experimental output, relevent info should have GRAPH and LEGEND ENDLEGEND')
#parser.add_argument('pdfName', metavar='nameOfPdf', nargs=1, help = 'The name of the pdf')
parser.add_argument('--xlabel', metavar='x axis label', help = 'label for the x axis')
parser.add_argument('--ylabel', metavar='y axis label', help = 'label for the y axis')
parser.add_argument('--title', metavar = 'graph title', help = 'title for the graph')
parser.add_argument('--scale', metavar = 'scale y axis', help = 'value to scale down by', default=1)
parser.add_argument('--metric', metavar = 'use rib or fib sum', help = 'use the rib metric or not', default='FIB')
parser.add_argument('--legendLoc', metavar = 'legend location', help = 'location to put legend (see pylab doc)', default = 2)

#open files based on arguments
args = parser.parse_args()
inputDataList = args.experimentOutput
pdfName = args.title + '.pdf'
xlabel = args.xlabel
ylabel = args.ylabel
title = args.title
scalingFactor = args.scale
metric = args.metric
legendLoc = args.legendLoc
rawData = [] #list of tuples (xydictionary, legendname)
X = []
Y = []
ymax = 0

def parseInput():
    for entry in inputDataList:
        expOutput = open(entry, 'r')
        metricRE = re.compile(metric + '.*END' + metric)
        p = re.compile('GRAPH.*ENDGRAPH')
        legendRE = re.compile('LEGEND.*ENDLEGEND')
        legendName = ' '
        xyDict = {}
        for line in expOutput:
            if metricRE.search(line):
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
                splitLine = line.split(' ', 1)
                if DEBUG:
                    print 'splitline: ', splitLine
                splitLine = splitLine[1].rsplit(' ', 1)[0]
                if DEBUG:
                    print 'splitline: ', splitLine
                legendName = splitLine
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

#return list of y values and the best y value (the last one)
#input dictionary of xyvalues
def getY(xyDict):
    y = []
    for key in sorted(xyDict):
        xAverage = getAverage(xyDict[key])
        y.append(xAverage/float(scalingFactor))
    if DEBUG:
        print "y: ", y
    return y, y[-1]

#get x values associated with this dictionary
def getX(xyDict):
    x = []
    for key in sorted(xyDict):
        x.append(key)
    if DEBUG:
        print 'x: ', x
    return x

#scale y list down to be a percentage
#return the list
def scaleY(y, scaler):
    zeroith = y[0] #bgp status quo, what we scale by
    dataRange = zeroith -  scaler
    newY = []
    for element in y:
        distance = zeroith - element
        percentInto = distance/dataRange
        newY.append(percentInto)
    return newY

#find the minimum y in the raw data
#returns it
def minY(rawData):
    minY = 99999999999
    maxY = 0
    for tuple in rawData:
        xyDict = tuple[0]
        y, betterY = getY(xyDict)
        tmpMin = min(y)
        tmpMax = max(y)
        if tmpMin < minY:
            minY = tmpMin
        if tmpMax > maxY:
            maxY = tmpMax
    return minY, maxY
    

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


fig = plt.figure()
smallestY, maxY = minY(rawData)
ylim = [smallestY - .02*smallestY, maxY + .02*maxY]
ylim2 = [0, .1*smallestY]
ylimratio = (ylim[1]-ylim[0])/(ylim2[1]-ylim2[0]+ylim[1]-ylim[0])
ylim2ratio = (ylim2[1]-ylim2[0])/(ylim2[1]-ylim2[0]+ylim[1]-ylim[0])
gs = gridspec.GridSpec(2, 1, height_ratios=[ylimratio, ylim2ratio])
#plt.errorbar(x, y, yerr=0)
legendHandles = []
bestY = None
baselineY = None
minY = None
ax = fig.add_subplot(gs[0])
ax2 = fig.add_subplot(gs[1])
#f, (ax, ax2) = plt.subplots(2, 1, sharex=True)
for tuple in rawData:
    xyDict = tuple[0]
    legendName = tuple[1]
    splitLegend = legendName.split()
    style = '-'
    print 'splitgenend: ', splitLegend[0]
    if splitLegend[0] == 'contiguous':
        style = '--'
    
    if DEBUG:
        print 'legendname: ', legendName
    y, betterY = getY(xyDict)
    minY = min(y)
    if not math.isnan(betterY):
        bestY = betterY
  #  y = scaleY(y, bestY)
    if DEBUG:
        print 'scaley: ', y
    x = getX(xyDict)
    baselineY = y[0]
    ax.plot(arr(x),arr(y), label=legendName, linestyle=style)
    ax2.plot(arr(x),arr(y), label=legendName, linestyle=style)

ax.legend(fontsize=10, loc=legendLoc)
ax2.set_ylim(ylim2)
plt.subplots_adjust(hspace = .09)
ax.set_ylim(ylim)

ax.plot((0, 1), (baselineY, baselineY), 'k-')
ax.text(.8,baselineY, 'Status quo')
ax.plot((0,1), (bestY, bestY), 'k-')
ax.text(0, bestY, 'Full adoption')


ax.spines['bottom'].set_visible(False)
ax2.spines['top'].set_visible(False)
ax.xaxis.tick_top()

ax.tick_params(labeltop='off')  # don't put tick labels at the top
ax.yaxis.set_ticks(numpy.arange(smallestY, maxY, (maxY - smallestY) * .15))
ax2.xaxis.tick_bottom()
ax2.yaxis.set_ticks(numpy.arange(0, .1*smallestY, .1*smallestY - 1))


kwargs = dict(color='k', clip_on=False)
xlim = ax.get_xlim()
dx = .01*(xlim[1]-xlim[0])
dy = .01*(ylim[1]-ylim[0])/ylimratio
ax.plot((xlim[0]-dx,xlim[0]+dx), (ylim[0]-dy,ylim[0]+dy), **kwargs)
ax.plot((xlim[1]-dx,xlim[1]+dx), (ylim[0]-dy,ylim[0]+dy), **kwargs)
dy = .01*(ylim2[1]-ylim2[0])/ylim2ratio
ax2.plot((xlim[0]-dx,xlim[0]+dx), (ylim2[1]-dy,ylim2[1]+dy), **kwargs)
ax2.plot((xlim[1]-dx,xlim[1]+dx), (ylim2[1]-dy,ylim2[1]+dy), **kwargs)
ax.set_xlim(xlim)
ax2.set_xlim(xlim)


#d = .015  # how big to make the diagonal lines in axes coordinates
# arguments to pass plot, just so we don't keep repeating them
#kwargs = dict(transform=ax.transAxes, color='k', clip_on=False)
#ax.plot((-d, +d), (-d, +d), **kwargs)        # top-left diagonal
#ax.plot((1 - d, 1 + d), (-d, +d), **kwargs)  # top-right diagonal
#
#kwargs.update(transform=ax2.transAxes)  # switch to the bottom axes
#ax2.plot((-d, +d), (1 - d, 1 + d), **kwargs)  # bottom-left diagonal
#ax2.plot((1 - d, 1 + d), (1 - d, 1 + d), **kwargs)  # bottom-right diagona

ax2.set_xlabel(xlabel)
ax2.set_ylabel(ylabel)
ax2.yaxis.set_label_coords(0.05, 0.5, transform=fig.transFigure)

#plt.axis([0,1, 0, 1520000])
#plt.ylabel(ylabel)
#plt.xlabel(xlabel)
plt.xticks(numpy.arange(0, 1.1, .1))
#plt.yticks(numpy.arange(-.3, 1.01, .1))
#plt.grid(which='major', axis = 'both')
#pylab.ylim(ymin=-.3, ymax=1)
pp.savefig()
pp.close()
plt.show()

#pylab.errorbar(x, y, yerr=c)
#pylab.show()
