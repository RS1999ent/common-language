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

	// used for returning a default path for legacy support with the rest of sim
	private LinkedList<Integer> legacyPath = new LinkedList<Integer>();

	// hash map of paths. should be keyed on the pathToKey method in this class
	private HashMap<String, LinkedList<Integer>> paths = new HashMap<String, LinkedList<Integer>>();

	// stores path attributes. should be keyed on pathToKey method
	private HashMap<String, Values> pathValues = new HashMap<String, Values>();

	private RootCause rc; // stores root cause of this integrated advertisement

	//true cost of path
	long trueCost;
	
	public long getTrueCost() {
		return trueCost;
	}

	public void setTrueCost(long trueCost) {
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

	public IA(IA toCopy) {
		legacyPath = (LinkedList<Integer>) toCopy.legacyPath.clone();
		paths = (HashMap<String, LinkedList<Integer>>) toCopy.paths.clone();
		this.trueCost = toCopy.trueCost;
	//	this.popTuple = toCopy.popTuple;
		// copy the path attributes, if Values implenets interface "cloneable",
		// then
		// we might be able to just call pathValues.clone(). Since it doesn't,
		// just
		// use Values copy constructor
		for (String pathValuesKey : toCopy.pathValues.keySet()) {
			Values copyValues = new Values(toCopy.pathValues.get(pathValuesKey));
			pathValues.put(pathValuesKey, copyValues);
		}
		// pathValues = (HashMap<String, Values>) toCopy.pathValues.clone();
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
		getPath().addFirst(as);
		paths.remove(key);
		pathValues.remove(key);
		paths.put(pathToKey(legacyPath), legacyPath);

	}

	/**
	 * Get the first hop in the as-path
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
	 * @param path
	 *            the path to set
	 */
	public void setPath_Legacy(LinkedList<Integer> path) {
		// remove old legacy path from hash table and its corresponding
		// attributes
		removePathAttributes(legacyPath);
		this.legacyPath = path;

	}

	/**
	 * method that sets a path in IA. Does not affect legacy path, used for
	 * setting paths in hash table
	 * 
	 * @param path
	 *            - the path being replaced
	 */
	public void setPath(LinkedList<Integer> path) {
		paths.put(pathToKey(path), path);
	}

	/**
	 * method reomves element from hash table based on the paseed in key
	 * 
	 * @param pathKey
	 *            , the key of element to be removed from hash table
	 */
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
	public Values getPathAttributes(LinkedList<Integer> path) {
		return pathValues.get(pathToKey(path));
	}

	// returns null if a path has no path attributes yet
	/**
	 * method that returns all path attributes for a given path key
	 * 
	 * @param key
	 *            the key of the path to get the values for
	 * @return the path values for a given path key (null if none)
	 */
	public Values getPathAttributes(String key) {
		return pathValues.get(key);
	}

	/**
	 * method that sets the path attributes of a path
	 * 
	 * @param value
	 *            the set of all path attributes to be assocaited with a path
	 * @param path
	 *            the path for the values to be associated with
	 */
	public void setPathAttributes(Values value, LinkedList<Integer> path) {
		pathValues.put(pathToKey(path), value);
	}

	/**
	 * method that sets the path attribute for a particular protocol with the
	 * associated path
	 * 
	 * @param setBytes
	 *            the path attribute associated with the protocol
	 * @param protocol
	 *            the protocol these attributes are associated with
	 * @param path
	 *            the path the path attributes are associated with
	 */
	public void setProtocolPathAttribute(byte[] setBytes, Protocol protocol,
			LinkedList<Integer> path) {
		// get path attributes based on key of path, if null, create one for the
		// protocol.
		Values pathAttributes = pathValues.get(pathToKey(path));
		if (pathAttributes == null) {
			pathAttributes = new Values();
		}
		pathAttributes.putValue(protocol, setBytes.clone());
		pathValues.put(pathToKey(path), pathAttributes);
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
		pathValues.remove(pathToKey(path));
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
	public byte[] getProtocolPathAttribute(Protocol protocol,
			LinkedList<Integer> path) {
		if (pathValues.containsKey(pathToKey(path)))
			return pathValues.get(pathToKey(path)).getValue(protocol);
		else {
			byte arr[] = new byte[1];
			arr[0] = (byte) 0xFF;
			return arr;
		}

	}

}
