import numpy
import pylab
import matplotlib.pyplot as plt
from matplotlib.backends.backend_pdf import PdfPages
pp = PdfPages('alltransits_allases.pdf')
arr = numpy.asarray

x = arr([.1, .2, .3, .4, .5, .6, .7, .8, .9, 1])
y = arr([[181.35], #.1
        [151.33], #.2
        [120.01], #.3
        [89.09], #.4
        [73.81], #.5
        [74.18], #.6
        [39.99], #.7
        [44.02], #.8
        [21.30], #.9
        [12.01]]).flatten() #1

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
plt.errorbar(x, y, yerr=c)
plt.axis([0,1, 0, 250])
plt.ylabel('Avereage cost of paths received at all participating transit ASes')
plt.xlabel('Percentage of stub ASes and transit ASes participating')
plt.title('Average cost of paths received vs number of ASes participating in new protocol')
pp.savefig()
pp.close()
plt.show()

#pylab.errorbar(x, y, yerr=c)
#pylab.show()
