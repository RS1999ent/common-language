package integratedAdvertisement;
/**
 * file: IA.java
 * @author John
 */

import java.util.*;

/**
 * Stores an AS Integrated advertisement 
 */
public class IA {

	private LinkedList<Integer> legacyPath = new LinkedList<Integer>(); //used for returning a default path for legacy support with the rest of sim 
	
	private HashMap<Integer, LinkedList<Integer>> paths = new HashMap<Integer, LinkedList<Integer>>(); //hash map of paths. should be keyed on the pathToKey method in this class
	
	private HashMap<Integer, Values> pathValues = new HashMap<Integer, Values>(); //stores path attributes. should be keyed on pathToKey method
	
	private RootCause rc;
	
	/**
	 * Default constructor. Creates an empty path
	 *
	 */
	public IA( RootCause rootCause ) {
		setRootCause(rootCause);
	}
	
	/**
	 * Constructor which creates a path from a collection of as numbers
	 * @param p The collection of <short> that form the AS-Path
	 */
	public IA(Collection<Integer> p, RootCause rootCause) {
		getPath().addAll(p);
		setRootCause(rootCause);
		paths.put(pathToKey(legacyPath), legacyPath);
	}
	
	/** 
	 * Prepends an AS to the current path
	 * @param as The AS that is to be added to the beginning of the path
	 */
	public void prepend(int as) {
		Integer key = pathToKey(legacyPath);
		getPath().addFirst(as);
		paths.remove(key);
		paths.put(pathToKey(legacyPath), legacyPath);
		
	}
	
	/**
	 * Get the first hop in the as-path
	 * @return the first hop in the path
	 */
	public int getFirstHop() {
		return getPath().getFirst();
	}
	
	/**
	 * Get the last hop in the path, which is the destination
	 * @return The destination of this path
	 */
	public int getDest() {
		if(getPath() == null) { // withdrawal message
			return getRootCause().getDest();
		}
		return getPath().getLast();
	}
	
	public boolean contains(int asn) {
		if(getPath() == null)
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

	public LinkedList<Integer> getPath(Integer key)
	{
		return paths.get(key);
	}

	/**
	 * @param path the path to set
	 */
	public void setPath_Legacy(LinkedList<Integer> path) {
		this.legacyPath = path;		
	}
	
	public void setPath(LinkedList<Integer> path)
	{
		paths.put(pathToKey(path), path);
	}
	
	public void removePath(Integer pathKey)
	{
		paths.remove(pathKey);
	}
	
	public Set<Integer> getPathKeys()
	{
		return paths.keySet();
	}
	

	
	public static Integer pathToKey(LinkedList<Integer> p)
	{
		String key = "";
		for(Iterator<Integer> iter = p.iterator();  iter.hasNext(); )
		{
			key += String.valueOf(iter.next());
			key += " ";
		}
		return key.hashCode();
		
	}
	
	public Values getPathAttributes(LinkedList<Integer> path)
	{
		return pathValues.get(pathToKey(path));
	}
	
	public Values getPathAttributes(Integer key)
	{
		return pathValues.get(key);
	}
	
	public void setPathAttributes(Values value, LinkedList<Integer> path)
	{
		pathValues.put(pathToKey(path), value);
	}
	
	public void setProtocolPathAttribute(byte[] setBytes, Protocol protocol, LinkedList<Integer> path){
		Values pathAttributes = pathValues.get(pathToKey(path));
		pathAttributes.putValue(protocol, setBytes);
		pathValues.put(pathToKey(path), pathAttributes); //is this necessary? Probably not since java passes back references, just to be safe
	}
	
	public byte[] getProtocolPathAttribute(Protocol protocol, LinkedList<Integer> path)
	{
		return pathValues.get(pathToKey(path)).getValue(protocol);
	}
	
	
	
	
}
