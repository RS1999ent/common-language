package integratedAdvertisement;

/**
 * file: IA.java
 * @author John
 */

import java.util.*;

import simulator.AS;
import simulator.AS.PoPTuple;

/**
 * Stores an AS Integrated advertisement
 */
public class IA {

	public static final String BNBW_KEY = "BNBW";
	
	@Override
	public String toString() {
		return "IA [legacyPath=" + legacyPath + ", paths=" + paths
				+ ", bookKeepingInfo=" + bookKeepingInfo + ", rc=" + rc
				+ ", popCosts=" + popCosts + ", truePoPCosts=" + truePoPCosts
				+ ", trueCost=" + trueCost + ", secure=" + secure + "]";
	}

	// used for returning a default path for legacy support with the rest of sim
	//since ia's only contain one path, this could be thought of as the primary path now, though any changes to it
	//should also be reflcted in the paths
	private LinkedList<Integer> legacyPath = new LinkedList<Integer>();

	// hash map of paths. should be keyed on the pathToKey method in this class
	private HashMap<String, LinkedList<Integer>> paths = new HashMap<String, LinkedList<Integer>>();

	//more extensible way of keeping important value information about this integrated advertisement
	public HashMap<String, Float> bookKeepingInfo = new HashMap<String, Float>();
	
	private RootCause rc; // stores root cause of this integrated advertisement
	
	//used to simulate the information of multiple adverts will contain for different PoPs
	//inforamtion about the intradomain costs for each pop pair needs to be in single advert
	//so we need this
	//should be cleared after use upon receipt, is used for processing only
	//The poptuple ordering is always the PoP of the advertisemetn sender to the PoP of the advertisement receiver.
	public HashMap<AS.PoPTuple, IAInfo> popCosts = new HashMap<AS.PoPTuple, IAInfo>();

	//bookkeepign for true cost of as path. like the popcosts, only all nodes use this for true cost updates
	//should be cleared after use used for local processing only.  This represents the intradomain cost that must be added if 
	//you choose a path going through this pop
	public HashMap<AS.PoPTuple, Integer> truePoPCosts = new HashMap<AS.PoPTuple, Integer>();
	//true cost of path
	float trueCost;
	//true iff the every node on path is secure (i.e. sbgp).  
	public boolean secure;
	
	public float getTrueCost() {
		return trueCost;
	}

	public void setTrueCost(float trueCost) {
		this.trueCost = trueCost;
	}

	//HashMap<AS.PoPTuple, Integer> intraDomainCosts; 
	
	/*public PoPTuple getPoPTuple() {
		return popTuple;
	}

	public void setPoPTuple(AS.PoPTuple newTuple) {
		this.popTuple = newTuple;
	}*/

	/**
	 * Default constructor. Creates an empty path
	 *
	 */
	public IA(RootCause rootCause) {
		setRootCause(rootCause);
	}

	/**
	 * Constructor which creates a path from a collection of as numbers
	 * 
	 * @param p
	 *            The collection of <short> that form the AS-Path
	 */
	// public IA(Collection<Integer> p, RootCause rootCause) {
	// getPath().addAll(p);
	// setRootCause(rootCause);
	// paths.put(pathToKey(legacyPath), legacyPath);
	// } //old constructor, copy constructor used now because of there is more
	// than just the path

	/**
	 * copy constructor
	 * @param toCopy - IA of fields to copy
	 */
	public IA(IA toCopy) {
		legacyPath = (LinkedList<Integer>) toCopy.legacyPath.clone();
		paths = (HashMap<String, LinkedList<Integer>>) toCopy.paths.clone();
		
		this.bookKeepingInfo = (HashMap<String, Float>)toCopy.bookKeepingInfo.clone();
		this.trueCost = toCopy.trueCost;
		this.secure = toCopy.secure;
		//copy poptuples in
		for(AS.PoPTuple tuple : toCopy.popCosts.keySet())
		{
			popCosts.put(new AS.PoPTuple(tuple.pop1,  tuple.pop2), new IAInfo(toCopy.popCosts.get(tuple)));
		}
		//copy truepoptuples in
		for(AS.PoPTuple tuple : toCopy.truePoPCosts.keySet())
		{
			truePoPCosts.put(new AS.PoPTuple(tuple.pop1, tuple.pop2), toCopy.truePoPCosts.get(tuple));
		}
		rc = new RootCause(toCopy.rc.rcAsn, toCopy.rc.updateNum,
				toCopy.rc.getDest());
	}

	/**
	 * Prepends an AS to the current path
	 * 
	 * @param as
	 *            The AS that is to be added to the beginning of the path
	 */
	public void prepend(int as) {
		String key = pathToKey(legacyPath);
		getPath().addFirst(as); //prepends onto legacy path
		paths.remove(key);
		removePathAttributes(legacyPath);//pathValues.remove(key);
		paths.put(pathToKey(legacyPath), legacyPath);

	}

	/**
	 * Get the first hop in the as-path (legacy path)
	 * 
	 * @return the first hop in the path
	 */
	public int getFirstHop() {
		return getPath().getFirst();
	}

	/**
	 * Get the last hop in the path, which is the destination
	 * 
	 * @return The destination of this path
	 */
	public int getDest() {
		if (getPath() == null) { // withdrawal message
			return getRootCause().getDest();
		}
		return getPath().getLast();
	}

	public boolean contains(int asn) {
		if (getPath() == null)
			return false;

		return getPath().contains(asn);
	}

	/**
	 * @return the rc
	 */
	public RootCause getRootCause() {
		return rc;
	}

	public void setRootCause(RootCause r) {
		rc = r;
	}

	/**
	 * @return the path
	 */
	public LinkedList<Integer> getPath() {
		return legacyPath;
	}

	public LinkedList<Integer> getPath(String key) {
		return paths.get(key);
	}

	/**
	 * sets the legacy path and updates the legacy path in the paths.
	 * note: untested changes fixes a bug, but the bug didn't affect the simulator prior to 2-9-16
	 * @param path
	 *            the path to set
	 */
	public void setPath_Legacy(LinkedList<Integer> path) {
		// remove old legacy path from hash table and its corresponding
		// attributes
		paths.remove(pathToKey(legacyPath)); //untested change
		if(path != null){
			paths.put(pathToKey(path), path); //untested change
		}
		removePathAttributes(legacyPath);
		this.legacyPath = path;

	}

	/**
	 * method that sets a path in IA. Does not affect legacy path, used for
	 * setting paths in hash table
	 * 
	 * @param path
	 *            - the path being replaced
	 */ //not called in any of the simulator 2-9-16
	public void setPath(LinkedList<Integer> path) {
		paths.put(pathToKey(path), path);
	}

	/**
	 * method reomves element from hash table based on the paseed in key
	 * 
	 * @param pathKey
	 *            , the key of element to be removed from hash table
	 */ //not called in any of the simulator so far 2-9-16
	public void removePath(String pathKey) {
		paths.remove(pathKey);
	}

	/**
	 * @return the keyset of the paths stored in IA
	 */
	public Set<String> getPathKeys() {
		return paths.keySet();
	}

	/**
	 * method that converts a path to a hash key
	 * 
	 * @param p
	 *            the path that we want to find the corresponding key for
	 * @return the path converted to a string that is a key to be hashed on
	 */
	public static String pathToKey(LinkedList<Integer> p) {
		String key = "";
		for (Iterator<Integer> iter = p.iterator(); iter.hasNext();) {
			key += String.valueOf(iter.next());
			key += " ";
		}
		return key;// .hashCode();

	}

	/**
	 * method that returns all path attributes for a given path
	 * 
	 * @param path
	 *            the path to get all path attributes for
	 * @return the path attributes for that path (null if none exist)
	 */
/*	public Values getPathAttributes(LinkedList<Integer> path) {
		return pathValues.get(pathToKey(path));
	}*/

	// returns null if a path has no path attributes yet
	/**
	 * method that returns all path attributes for a given path key
	 * 
	 * @param key
	 *            the key of the path to get the values for
	 * @return the path values for a given path key (null if none)
	 */
	public Values getPathAttributes(PoPTuple tuple, String key) {
		return popCosts.get(tuple).pathValues.get(key);
	}

	/**
	 * method that sets the path attributes of a path
	 * 
	 * @param value
	 *            the set of all path attributes to be assocaited with a path
	 * @param path
	 *            the path for the values to be associated with
	 */
	public void setPathAttributes(PoPTuple tuple, Values value, LinkedList<Integer> path) {
		popCosts.get(tuple).pathValues.put(pathToKey(path), value);
	}

		// removes the path attributes of the passed in path
	/**
	 * method that removes all path attributes associated with a path
	 * 
	 * @param path
	 *            the path to remove the path attributes of
	 */
	public void removePathAttributes(LinkedList<Integer> path) {
		// remove pathValues entrie based on the corresponding key of the path
		// (with pathToKey)
		
		//removes pathvalues form all points of presence in advertisement
		for(IAInfo values : popCosts.values())
		{
			values.pathValues.remove(IA.pathToKey(path));
		}
		//pathValues.remove(IA.pathToKey(path));
	}
	

	/**
	 * method to get the path attributes of a protocol associated with a path
	 * 
	 * @param protocol
	 *            - the protocol to get the attribtues from
	 * @param path
	 *            the path these attributes are associated with
	 * @return the path attribute associated with the protocol
	 */
	public byte[] getProtocolPathAttribute(PoPTuple forTuple, Protocol protocol,
			LinkedList<Integer> path) {
		if(popCosts.containsKey(forTuple)){
			if (popCosts.get(forTuple).pathValues.containsKey(IA.pathToKey(path))){
				return popCosts.get(forTuple).pathValues.get(IA.pathToKey(path)).getValue(protocol);
			}
			else {
				byte arr[] = new byte[1];
				arr[0] = (byte) 0xFF; //indicates that there is a poptuple that is valid, but that there are no values for the protocol
				return arr;
			}
		}
		return null;
}



}
