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
GETDIFFERENCE = 0

parser = argparse.ArgumentParser(description='Creates a pdf graph from experimentoutput')
parser.add_argument('experimentOutput', metavar='expOutput', nargs ="+", help = 'experimental output')
#parser.add_argument('pdfName', metavar='nameOfPdf', nargs=1, help = 'The name of the pdf')
parser.add_argument('--xlabel', metavar='x axis label', help = 'label for the x axis')
parser.add_argument('--ylabel', metavar='y axis label', help = 'label for the y axis')
parser.add_argument('--title', metavar = 'graph title', help = 'title for the graph')
parser.add_argument('--scale', metavar = 'scale y axis', help = 'value to scale down by', default=1)
parser.add_argument('--metric', metavar = 'use rib or fib sum', help = 'use the rib metric or not', default='FIB')
parser.add_argument('--legendLoc', metavar = 'legend location', help = 'location to put legend (see pylab doc)', default = 2)
parser.add_argument('--primary', metavar = 'protocol to extract', help = 'the primary protocol')
parser.add_argument('--raw', metavar = 'raw y axis', help = 'whether to break y', default=0)
parser.add_argument('--usePercentage', metavar = 'makePercentage', help = 'have the y asxis scale as a percentage of ideal', default = 0)

#open files based on arguments
args = parser.parse_args()
inputDataList = args.experimentOutput
pdfName = args.title + '.pdf'
xlabel = args.xlabel
ylabel = args.ylabel
title = args.title
scalingFactor = args.scale
doPercentage = int(args.usePercentage)
metric = args.metric
legendLoc = args.legendLoc
primary = args.primary
raw = int(args.raw)
rawData = [] #list of tuples (xydictionary, legendname)
X = []
Y = []
ymax = 0

def parseInput(regex, monitorFrom):
    for entry in inputDataList:
        expOutput = open(entry, 'r')
        metricRE = re.compile(metric + '.*END' + metric)
        p = re.compile(regex)
        legendRE = re.compile('LEGEND.*ENDLEGEND')
        legendName = ' '
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
                splitLine = line.split(' ', 1)
                if DEBUG:
                    print 'splitline: ', splitLine
                splitLine = splitLine[1].rsplit(' ', 1)[0]
                if DEBUG:
                    print 'splitline: ', splitLine
                legendName = splitLine
                legendName += ', ' + monitorFrom
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
        x.append(float(key) * 100)
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

#takesin two lists of y (having the same ordering) and gives an
#percentage of the biggest difference
def biggestDifference(y1, y2):
    biggestDifference = 0
    atI = 0;
    for i in range(len(y1)):
        tmpdifference = y1[i] - y2[i]
        if tmpdifference > biggestDifference:
            atI = i
            biggestDifference = tmpdifference
    return (y1[atI] - y2[atI]) / y2[atI], atI
        
#given x and y values, returns a list of slopes
#slope between 0,1; 1,2, etc.
def getslopes(x, y):
    slopes = []
    for i in range(len(y)):
        if i != len(y)-1:
            y2 = y[i+1]
            y1 = y[i]
            x2 = x[i+1]
            x1 = x[i]
            slopes.append((y2 - y1) / (x2 - x1))
    return slopes

#returns the x coordinate of the slope lists where y2 slope
#is greater than y1
def getCrossoverPoint(y1, y2):
    for i in range(len(y1)):
        if y1[i] < y2[i]:
            return i

    
#returns error bars (95% confidence interval) for particular list
def getErrorBars(xyDict, yvals):
    errorBars = []
    for key in sorted(xyDict):
        std = numpy.std(arr(xyDict[key], dtype=numpy.float32), dtype=numpy.float64)
        stdError = std/math.sqrt(10) #so that last value has no std, because it is solidly that number
        marginOError = 2 * stdError
        errorList = [marginOError, marginOError]
        errorBars.append(errorList)
    return errorBars

#find the minimum y in the raw data
#returns it
def minY(rawData):
    minY = 99999999999
    maxY = 0
    for tuple in rawData:
        xyDict = tuple[0]
        y, betterY = getY(xyDict)
#        print y
        tmpMin = min(y)
        tmpMax = max(y)
 #       print 'tmpmax: ',tmpMax
        if tmpMin < minY:
            minY = tmpMin
        if tmpMax > maxY:
            maxY = tmpMax
    return minY, maxY
    
if primary == 'wiser':
    parseInput('WISER_FIB_GRAPH.*END', 'monitored from upgraded ASes')
#    parseInput('ALLWISER_FIB_GRAPH.*END', 'monitored from all ASes')
#    parseInput('GULF_BWREPLACEMENT_TRUECOST_FIB.*END', 'monitored from gulf ASes')

if primary == 'bw':
    parseInput('BW_FIB_GRAPH.*END', 'monitored from upgraded ASes')
    parseInput('BW_FIB_BGP_GRAPH.*END', 'baseline')
#    parseInput('ALLBW_FIB_GRAPH.*END', 'monitored from all ASes')
#    parseInput('GULF_WISERREPLACEMENT_TRUECOST_FIB.*END', 'monitored from gulf ASes')
if primary == 'replacement':
    parseInput('REPLACEMENT_STUB_FIB_GRAPH.*END', 'monitored from upgraded stub ASes')
    
#parseInput()
#fillXY()
        
pp = PdfPages(pdfName)
arr = numpy.asarray



fig = plt.figure(figsize=(9.6,7.5))
smallestY, maxY = minY(rawData)
#print rawData
#print 'ys: ', smallestY, maxY
ylim = [smallestY - .06*smallestY, maxY + .035*maxY]
ylim2 = [0, .05*smallestY]
ylimratio = (ylim[1]-ylim[0])/(ylim2[1]-ylim2[0]+ylim[1]-ylim[0])
ylim2ratio = (ylim2[1]-ylim2[0])/(ylim2[1]-ylim2[0]+ylim[1]-ylim[0])
gs = gridspec.GridSpec(2, 1, height_ratios=[ylimratio, ylim2ratio])
#plt.errorbar(x, y, yerr=0)
legendHandles = []
bestY = None
baselineY = None
minY = None
Ys = []
if(not raw):
    print 'here'
    ax = fig.add_subplot(gs[0])
    ax2 = fig.add_subplot(gs[1])
else:
    ax = fig.add_subplot(1,1,1)
#f, (ax, ax2) = plt.subplots(2, 1, sharex=True)
x = []
for tuple in rawData:
    xyDict = tuple[0]
    legendName = tuple[1]
    splitLegend = legendName.split()
    style = '-'
#    print 'splitgenend: ', splitLegend[0]
    if splitLegend[0] == 'contiguous':
        style = '--'
    
    if DEBUG:
        print 'legendname: ', legendName
    print 'legendname: ', legendName
    y, betterY = getY(xyDict)
    Ys.append(y)
    minY = min(y)
    if not math.isnan(betterY):
        bestY = betterY
    if(doPercentage):
        y = scaleY(y, bestY)
    if DEBUG:
        print 'scaley: ', y
    x = getX(xyDict)
    print x
    print y
    baselineY = y[0]
    error = getErrorBars(xyDict, y)
    ax.errorbar(arr(x),arr(y), color='k', linestyle=style, yerr=arr(error).T, label=legendName)
    #ax.plot(arr(x),arr(y), label=legendName, linestyle=style)
#    if(not raw):
 #       ax2.errorbar(arr(x),arr(y), label=legendName, linestyle=style)

ax.legend(fontsize=10, loc=legendLoc)
if(not raw):
    ax2.set_ylim(ylim2)
    ax.set_ylim(ylim)
else:
        ax.set_ylim(0)
plt.subplots_adjust(hspace = .09)


#adds best case and status quo line
#ax.plot((0, 100), (baselineY, baselineY), 'k-')
#ax.text(.8,baselineY, 'Status Quo')
#ax.plot((0,100), (bestY, bestY), 'k-')
#ax.text(0, bestY, 'Best Case')


ax.spines['bottom'].set_visible(False)
if(not raw):
    ax2.spines['top'].set_visible(False)
ax.xaxis.tick_top()

ax.tick_params(labeltop='off')  # don't put tick labels at the top
ax.yaxis.set_ticks(numpy.arange(smallestY, maxY, (maxY - smallestY) * .15))
if(not raw):
    ax2.xaxis.tick_bottom()
    ax2.yaxis.set_ticks(numpy.arange(0, .1*smallestY, .1*smallestY - 1))

if(not raw ):
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

if(not raw):
    ax2.set_xlabel(xlabel)
    ax2.set_ylabel(ylabel)
    ax2.yaxis.set_label_coords(0.05, 0.5, transform=fig.transFigure)
if(raw):
    plt.ylim(ymin=0)
    ax.xaxis.tick_bottom()
    ax.set_xlabel(xlabel)
    ax.set_ylabel(ylabel)
    ax.yaxis.set_label_coords(0.05, 0.5, transform=fig.transFigure)
if GETDIFFERENCE:
    print 'biggestdfference: ', biggestDifference(Ys[0], Ys[1])
    slope1 = getslopes(x, Ys[0])
    slope2 = getslopes(x, Ys[1])
    print slope1
    print slope2
    print 'corssoverpoint: ', getCrossoverPoint(slope1,slope2)
#plt.axis([0,1, 0, 1520000])
#plt.ylabel(ylabel)
#plt.xlabel(xlabel)
plt.xticks(numpy.arange(10, 101, 10))
#plt.yticks(numpy.arange(-.3, 1.01, .1))
#plt.grid(which='major', axis = 'both')
#pylab.ylim(ymin=-.3, ymax=1)
pp.savefig()
pp.close()
plt.show()

#pylab.errorbar(x, y, yerr=c)
#pylab.show()
