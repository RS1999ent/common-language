import numpy
import pylab
import matplotlib.pyplot as plt
from matplotlib.backends.backend_pdf import PdfPages
pp = PdfPages('truecost_bw_part_transits.pdf')
arr = numpy.asarray

x = arr([0, .1, .2, .3, .4, .5, .6, .7, .8, .9, 1])
y = arr([[83208],
         [81178.5754],
         [82376.968],
         [85953.09938],
         [92011.63914],
         [97559.11013],
         [106075.3614],
         [112864.2485],
         [127155.3367],
         [148247.947],
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
plt.ylabel('Average bottleneck bandwidth sum of paths received at transit ASes')
plt.xlabel('Percentage of transit ASes participating')
plt.title('Average bottleneck bandwidth of paths received vs number of ASes participating in new protocol')
pp.savefig()
pp.close()
plt.show()

#pylab.errorbar(x, y, yerr=c)
#pylab.show()
