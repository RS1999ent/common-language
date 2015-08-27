#!/usr/bin/gawk -f

{
    nodes[$1] = 1;
    nodes[$2] = 1;
}

$3 == -1 {
    customers[$1"|"$2] = 1;
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
    numProviders[$2]++;
    numCustomers[$1]++;
}

$3 < -1 || $3 > 2 {
    print "Out of range: ", $3;
    exit(1);
}

END {
    for(n in nodes) {
	# Get multi-homed stub ASes
	if(numProviders[n] > 1 && numChildren[n] == 0 && numPeers[n] == 0) {
	    
	    printf("%s\n", n);
	    children[provider[n]]++;
	}
    }
}
