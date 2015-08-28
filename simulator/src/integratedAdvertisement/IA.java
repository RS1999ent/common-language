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
	
	private HashMap<String, LinkedList<Integer>> paths = new HashMap<String, LinkedList<Integer>>(); //hash map of paths. should be keyed on the pathToKey method in this class
	
	private HashMap<String, Values> pathValues = new HashMap<String, Values>(); //stores path attributes. should be keyed on pathToKey method
	
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
//	public IA(Collection<Integer> p, RootCause rootCause) {
//		getPath().addAll(p);
//		setRootCause(rootCause);
//		paths.put(pathToKey(legacyPath), legacyPath);
//	}
	
	public IA(IA toCopy)
	{
		legacyPath = (LinkedList<Integer>) toCopy.legacyPath.clone();
		paths = (HashMap<String, LinkedList<Integer>>) toCopy.paths.clone();
		//clone into pathvalues
		for(String pathValuesKey : toCopy.pathValues.keySet())
		{
			Values copyValues = new Values(toCopy.pathValues.get(pathValuesKey));
			pathValues.put(pathValuesKey, copyValues);
		}
//		pathValues = (HashMap<String, Values>) toCopy.pathValues.clone();
		rc = new RootCause(toCopy.rc.rcAsn, toCopy.rc.updateNum, toCopy.rc.getDest());
	}
	
	/** 
	 * Prepends an AS to the current path
	 * @param as The AS that is to be added to the beginning of the path
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

	public LinkedList<Integer> getPath(String key)
	{
		return paths.get(key);
	}

	/**
	 * @param path the path to set
	 */
	public void setPath_Legacy(LinkedList<Integer> path) {
		//remove old legacy path from hash table and its corresponding attributes
		removePathAttributes(legacyPath);
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
	
	public Set<String> getPathKeys()
	{
		return paths.keySet();
	}
	

	
	public static String pathToKey(LinkedList<Integer> p)
	{
		String key = "";
		for(Iterator<Integer> iter = p.iterator();  iter.hasNext(); )
		{
			key += String.valueOf(iter.next());
			key += " ";
		}
		return key;//.hashCode();
		
	}
	
	public Values getPathAttributes(LinkedList<Integer> path)
	{
		return pathValues.get(pathToKey(path));
	}
	
	//returns null if a path has no path attributes yet
	public Values getPathAttributes(String key)
	{
		return pathValues.get(key);
	}
	
	public void setPathAttributes(Values value, LinkedList<Integer> path)
	{
		pathValues.put(pathToKey(path), value);
	}
	
	public void setProtocolPathAttribute(byte[] setBytes, Protocol protocol, LinkedList<Integer> path){
		//get path attributes based on key of path, if null, create one for the protocol.
		Values pathAttributes = pathValues.get(pathToKey(path)); 
		if (pathAttributes == null)
		{
			pathAttributes = new Values();
		}
		pathAttributes.putValue(protocol, setBytes.clone()); 
		pathValues.put(pathToKey(path), pathAttributes);
	}
	
	//removes the path attributes of the passed in path
	public void removePathAttributes(LinkedList<Integer> path)
	{
		//remove pathValues entrie based on the corresponding key of the path (with pathToKey)
		pathValues.remove(pathToKey(path));
	}
	
	public byte[] getProtocolPathAttribute(Protocol protocol, LinkedList<Integer> path)
	{
		if (pathValues.containsKey(pathToKey(path)))
			return pathValues.get(pathToKey(path)).getValue(protocol);
		else
		{
			byte arr[] = new byte[1];
			arr[0] =  (byte) 0xFF;
			return  arr;
		}
			
		
	}
	
	
	
	
}
