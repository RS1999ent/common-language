package integratedAdvertisement;

import java.util.HashMap;
import java.util.LinkedList;

public class IAInfo {
	@Override
	public String toString() {
		return "IAInfo [pathValues=" + pathValues + ", nodeDescriptor="
				+ nodeDescriptor + "]";
	}



	// stores path attributes. should be keyed on pathToKey method
	public HashMap<String, Values> pathValues = new HashMap<String, Values>();

	// stores the node descriptors for ASes on the path
	public HashMap<String, Values> nodeDescriptor = new HashMap<String, Values>();
	
	public IAInfo()
	{
		
	}
	
	public IAInfo(IAInfo toCopy)
	{
		pathValues = (HashMap<String, Values>)toCopy.pathValues.clone();
		nodeDescriptor = (HashMap<String, Values>)toCopy.nodeDescriptor.clone();
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
		Values pathAttributes = pathValues.get(IA.pathToKey(path));
		if (pathAttributes == null) {
			pathAttributes = new Values();
		}
		pathAttributes.putValue(protocol, setBytes.clone());
		pathValues.put(IA.pathToKey(path), pathAttributes);
	}
	
	

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

	

}
