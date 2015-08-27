package integratedAdvertisement;
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
	
	private int dest;
	
	public RootCause(int asn, int uNum, int dst) {
		rcAsn = asn;
		updateNum = uNum;
		this.setDest(dst);
	}
	
	/**
	 * @return the dest
	 */
	public int getDest() {
		return dest;
	}

	/**
	 * @param dest the dest to set
	 */
	public void setDest(int dest) {
		this.dest = dest;
	}

	public boolean equals(Object o) {
		RootCause r = (RootCause)o;
		if(r.rcAsn == this.rcAsn && r.updateNum == this.updateNum && r.getDest() == this.getDest())
			return true;
		else
			return false;
	}
	
	public String toString() {
		return "" + rcAsn + "." + updateNum;
	}

}
