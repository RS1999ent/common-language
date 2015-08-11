#!/usr/bin/gawk -f

BEGIN {
    # Read single-homed stub ASes
    while(getline line < "seed-parents-2013.txt") {
	ases[line] = 1;
    };
}

{
    printit = 1;
    for(a in ases) {
    	if($1 == a || $2 == a) {
    	    printit = 0;
	    break;
    	}
    }

    if(printit == 1) {
    	print;
    }
}

# END {
#     for(a in ases) {
#     	print a;
#     }
# }
