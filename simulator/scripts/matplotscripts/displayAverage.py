import numpy
import pylab
import matplotlib.pyplot as plt
import matplotlib.gridspec as gridspec
from matplotlib.backends.backend_pdf import PdfPages
import argparse
import random
import math
import re

DEBUG = 1

parser = argparse.ArgumentParser(description='grabs the average from the inputed regular expression')
parser.add_argument('experimentOutput', metavar='expOutput', nargs ="+", help = 'experimental output, relevent info should have GRAPH and LEGEND ENDLEGEND')
#parser.add_argument('pdfName', metavar='nameOfPdf', nargs=1, help = 'The name of the pdf')
parser.add_argument('--regex', metavar='average to show', help = 'average to show (regular expresion)')

#open files based on arguments
args = parser.parse_args()
inputDataList = args.experimentOutput
regex = args.regex

rawData = [] #list of tuples (xydictionary, legendname)
average = []
X = []
Y = []
ymax = 0

def parseInput():
    for entry in inputDataList:
        expOutput = open(entry, 'r')
        ourRegex = re.compile(regex)
        xyDict = {}
        for line in expOutput:
            if ourRegex.search(line):
                m = ourRegex.search(line)
                line = m.group()
                splitLine = line.split()
                average.append(splitLine[4])
                
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

print getAverage(average)
