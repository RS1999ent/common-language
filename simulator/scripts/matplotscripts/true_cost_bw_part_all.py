import numpy
import pylab
import matplotlib.pyplot as plt
from matplotlib.backends.backend_pdf import PdfPages
pp = PdfPages('truecost_bw_part_all.pdf')
arr = numpy.asarray

x = arr([0, .1, .2, .3, .4, .5, .6, .7, .8, .9, 1])
y = arr([[83208],
        [82797.93],
        [82980.01763],
        [85500.05813],
        [91007.57086],
        [97449.19363],
        [105826.1463],
        [112847.1445],
        [127224.3243],
        [148194.396],
        [172771.58]]).flatten()

c= arr([[ 49.81, 34.67], #.1
        [65.74, 35.85], #.2
        [32.56, 42.57], #.3
        [40.29, 38.37], #.4
        [35.07, 26.19], #.5
        [32.9, 15.24], #.6
        [13.71, 15.22],   #.7
        [20.77, 48.8],  #.8
        [5.78, 12.43],  #.9
        [0, 0]]).T     #1


#plt.figure()
plt.errorbar(x, y, yerr=0)
plt.axis([0,1, 0, 174000])
plt.ylabel('Average bottleneck bandwidth sum of paths received at all ASes')
plt.xlabel('Percentage of transit ASes participating')
plt.title('Average bottleneck bandwidth of paths received vs number of ASes participating in new protocol')
pp.savefig()
pp.close()
plt.show()

#pylab.errorbar(x, y, yerr=c)
#pylab.show()
