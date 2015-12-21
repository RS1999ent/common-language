import numpy
import pylab
import matplotlib.pyplot as plt
from matplotlib.backends.backend_pdf import PdfPages
arr = numpy.asarray
pp = PdfPages('allparticipating_allases.pdf')
x = arr([.1, .2, .3, .4, .5, .6, .7, .8, .9, 1])
y = arr([[108.16], #.1
        [88.92], #.2
        [77.98], #.3
        [61.96], #.4
        [50.55], #.5
        [48.83], #.6
        [23.93], #.7
        [28.62], #.8
        [16.96], #.9
        [9.82]]).flatten() #1

c= arr([[ 21.03, 19.8], #.1
        [31.73, 13.23], #.2
        [22.76, 21.46], #.3
        [23.95, 26.97], #.4
        [20.35, 16.28], #.5
        [17.67, 15.49], #.6
        [2.32, 12.1],   #.7
        [9.57, 24.05],  #.8
        [3.77, 6.35],  #.9
        [0, 0]]).T     #1


#plt.figure()
plt.errorbar(x, y, yerr=c)
plt.axis([0,1, 0, 140])
plt.ylabel('Avereage cost of paths received at all participating ASes')
plt.xlabel('Percentage of stub ASes and transit ASes participating')
plt.title('Average cost of paths received vs number of ASes participating in new protocol')
pp.savefig()
pp.close()
plt.show()

#pylab.errorbar(x, y, yerr=c)
#pylab.show()
