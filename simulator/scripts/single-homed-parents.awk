#!/usr/bin/gawk -f
#
# Outputs two streams on stdout and stderr, respectively.
# stdout contains the list of parents that have only stub children and the count of those children.
# stderr contains the list of stub ASes.
# This is useful to cut down simulation time. It is an optimization.

{
    nodes[$1] = 1;
    nodes[$2] = 1;
}

$3 == -1 {
    customers[$1"|"$2] = 1;
    provider[$1] = $2;
    numProviders[$1]++;
    numCustomers[$2]++;
}

$3 == 0 {
    peers[$1"|"$2] = 1;
    numPeers[$1]++;
    numPeers[$2]++;
}

$3 == 1 {
    customers[$2"|"$1] = 1;
    provider[$2] = $1;
    numProviders[$2]++;
    numCustomers[$1]++;
}

$3 < -1 || $3 > 2 {
    print "Out of range: ", $3;
    exit(1);
}

END {
    for(n in nodes) {
	if(numCustomers[n] == 0 && numPeers[n] == 0 && numProviders[n] == 1) {
	    printf("%s\n", n) > "/dev/stderr";
	    children[provider[n]]++;
	}
    }

    for(n in children) {
	printf("%s %d\n", n, children[n]);
    }
}
