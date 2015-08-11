#!/usr/bin/gawk -f

{
    nodes[$1] = 1;
    nodes[$2] = 1;
}

$3 == -1 {
    customers[$1"|"$2] = 1;
}

$3 == 0 {
    peers[$1"|"$2] = 1;
}

$3 == 1 {
    providers[$1"|"$2] = 1;
}

$3 < -1 || $3 > 2 {
    print "Out of range: ", $3;
    exit(1);
}

END {
    # print "Processing...";

    for(i in customers) {
	split(i, f, "|");
	# if(f[2] == 40 || f[1] == 40) {
	#     printf("Testing %d customer of %d\n", f[1], f[2]);
	# }
	if(providers[f[2]"|"f[1]] == 1) {
	    symmetrical++;
	} else {
	    asymmetrical++;
	}

	if(customers[f[2]"|"f[1]] == 1) {
	    illegal++;
	}
    }

    for(n in nodes) {
	numnodes++;
    }

    printf("Nodes = %u, Customer/Provider: Symmetrical = %d, Asymmetrical = %d, Illegal = %d\n", numnodes, symmetrical, asymmetrical, illegal);

    symmetrical = 0;
    asymmetrical = 0;

    for(i in peers) {
	split(i, f, "|");
	if(peers[f[2]"|"f[1]] == 1) {
	    symmetrical++;
	} else {
	    asymmetrical++;
	}
    }

    printf("Peers: Symmetrical = %d, Asymmetrical = %d\n", symmetrical, asymmetrical);
}
