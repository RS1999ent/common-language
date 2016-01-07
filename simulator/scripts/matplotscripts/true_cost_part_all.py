import numpy
import pylab
import matplotlib.pyplot as plt
from matplotlib.backends.backend_pdf import PdfPages
pp = PdfPages('truecost_part_all.pdf')
arr = numpy.asarray

x = arr([0, .1, .2, .3, .4, .5, .6, .7, .8, .9, 1])
y = arr([[1457270.5],
         [1479158.9],
         [1476073.413],
         [1444782.44],
         [1372953.26],
         [1308710.05],
         [1187560.917],
         [1141159.451],
         [1018126.513],
         [863181.1922],
         [698950.2]]).flatten()

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
plt.axis([0,1, 0, 1520000])
plt.ylabel('Average true cost of paths received at all ASes')
plt.xlabel('Percentage of transit ASes participating')
plt.title('Average cost of paths received vs number of ASes participating in new protocol')
pp.savefig()
pp.close()
plt.show()

#pylab.errorbar(x, y, yerr=c)
#pylab.show()
