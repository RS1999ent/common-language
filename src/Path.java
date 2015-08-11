/**
 * file: Path.java
 * @author John
 */

import java.util.*;

/**
 * Stores an AS path
 */
public class Path {

	LinkedList<Integer> path = new LinkedList<Integer>();
	RootCause rc;
	
	/**
	 * Default constructor. Creates an empty path
	 *
	 */
	public Path( RootCause rootCause ) {
		rc = rootCause;
	}
	
	/**
	 * Constructor which creates a path from a collection of as numbers
	 * @param p The collection of <short> that form the AS-Path
	 */
	public Path(Collection<Integer> p, RootCause rootCause) {
		path.addAll(p);
		rc = rootCause;
	}
	
	/** 
	 * Prepends an AS to the current path
	 * @param as The AS that is to be added to the beginning of the path
	 */
	public void prepend(int as) {
		path.addFirst(as);
	}
	
	/**
	 * Get the first hop in the as-path
	 * @return the first hop in the path
	 */
	public int getFirstHop() {
		return path.getFirst();
	}
	
	/**
	 * Get the last hop in the path, which is the destination
	 * @return The destination of this path
	 */
	public int getDest() {
		if(path == null) { // withdrawal message
			return rc.dest;
		}
		return path.getLast();
	}
	
	public boolean contains(int asn) {
		if(path == null)
			return false;
		
		return path.contains(asn);
	}
	
	public void setRootCause(RootCause r) {
		rc = r;
	}
}
