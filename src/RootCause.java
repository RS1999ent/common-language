/**
 * file: RootCause.java
 * @author John
 *
 */

/**
 * This class describes the Root Cause of an update
 * An update is identified by its RootCause
 * The RootCause consists of the RootCause-ASN, and the UpdateNum
 * In addition, we also store the destination, for lookup-efficiency
 */
public class RootCause {
	int rcAsn;
	int updateNum;
	
	int dest;
	
	public RootCause(int asn, int uNum, int dst) {
		rcAsn = asn;
		updateNum = uNum;
		this.dest = dst;
	}
	
	public boolean equals(Object o) {
		RootCause r = (RootCause)o;
		if(r.rcAsn == this.rcAsn && r.updateNum == this.updateNum && r.dest == this.dest)
			return true;
		else
			return false;
	}
	
	public String toString() {
		return "" + rcAsn + "." + updateNum;
	}

}
