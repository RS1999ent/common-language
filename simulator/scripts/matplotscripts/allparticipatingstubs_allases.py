import numpy
import pylab
import matplotlib.pyplot as plt

arr = numpy.asarray

x = arr([.1, .2, .3, .4, .5, .6, .7, .8, .9, 1])
y = arr([[80.85], #.1
        [66.32], #.2
        [59.89], #.3
        [48.52], #.4
        [39.41], #.5
        [37.64], #.6
        [24.68], #.7
        [21.97], #.8
        [13.74], #.9
        [8.01]]).flatten() #1

c= arr([[ 13.53, 14.6], #.1
        [22.03, 9.44], #.2
        [17.03, 15.01], #.3
        [17.88, 21.18], #.4
        [15.06, 13.14], #.5
        [12.72, 13.34], #.6
        [7.04, 3.91],   #.7
        [6.44, 16.71],  #.8
        [3, 4.48],  #.9
        [0, 0]]).T     #1


#plt.figure()
plt.errorbar(x, y, yerr=c)
plt.axis([0,1, 0, 140])
plt.ylabel('Avereage cost of paths received at all participating stub ASes')
plt.xlabel('Percentage of stub ASes and transit ASes participating')
plt.title('Average cost of paths received vs number of ASes participating in new protocol')
plt.show()

#pylab.errorbar(x, y, yerr=c)
#pylab.show()
