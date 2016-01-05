import numpy
import pylab
import matplotlib.pyplot as plt
from matplotlib.backends.backend_pdf import PdfPages
pp = PdfPages('incBenefit_partTransits.pdf')
arr = numpy.asarray

x = arr([.1, .2, .3, .4, .5, .6, .7, .8, .9, 1])
y = arr([[696.84],
         [1113.24],
         [1546.66],
         [2045.92],
         [2383.23],
         [2622.93],
         [2932.65],
         [3261.46],
         [3635.21],
         [4108.06]]).flatten()
         

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
plt.axis([0,1, 0, 4300])
plt.ylabel('Avereage wiser cost of paths received at all participating ASes')
plt.xlabel('Percentage of transit ASes participating')
plt.title('sum of costs received')
pp.savefig()
pp.close()
plt.show()

#pylab.errorbar(x, y, yerr=c)
#pylab.show()
