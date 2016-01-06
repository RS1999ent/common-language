#!/usr/bin/awk -f

BEGIN{
    if(h == 1)
    {
	print "script takes in a AS level brite generate .brite file and outputs an annotated file"
	print "anotated file is of form \"AS1 AS2 rel latency POP1 POP2\" "
    }
#    else{
#	if(briteFile == "")
#	{
#	    print "NEED output 'briteFile'"
#	    exit;
#	}
#    }
}

$9=="E_AS"{
    print $7, $8, "-1", $6, $7, $8
}
END{
}
