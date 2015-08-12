#!/usr/bin/awk -f
BEGIN{

    if(h == 1)
    {
	print "script takes in iplane inter_pop_links and outputs a files"
	print "containing interAS interPoP latency format 'AS1 AS2 Latency pop1 pop2'"
	print "and file containing intraAS interPop latency format 'AS|pop1 pop2 latency:...'"
	exit;
    }
    else{
	if(interASFile == "")
	{
	    print "Need input variable 'interASFile'";
	    exit;
	}
	if(intraASFile == "")
	{
	    print "Need input variable 'intraASFile'";
	    exit;
	}
    }
}


$2 == $4{
    if($2 in intraAS)
	intraAS[$2] = intraAS[$2] $1 " " $3 " " $5 ":";
    else{
	intraAS[$2] = "|" $1 " " $3 " " $5 ":";
    }
}

$2 != $4{
    print $2, $4, $5, $1, $3 > interASFile;
}

END{
    OFS = "";
    for(element in intraAS){
	print element,  intraAS[element] > intraASFile
    }

}
