package integratedAdvertisement;

/*import protobuf.AdvertisementProtos.ProtoValues;
 import protobuf.AdvertisementProtos.ProtoValues.Builder;
 import protobuf.AdvertisementProtos.ProtoValues.valueFieldEntry;*/

import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.Set;

//import com.google.protobuf.ByteString;

//class that holds the values associated with a particular key
//or edge in the graph.  Meant so that multiple algorithms can
//use the same key but still have some control over their information.

public class Values {

	// keyed in by protocolId, value is
	// is just raw bytes. more flexible
	private Hashtable<Long, byte[]> c_protocolValues = new Hashtable<Long, byte[]>();

	public Values() {

	}

	// copy constructor for values
	Values(Values toCopy) {
		c_protocolValues = (Hashtable<Long, byte[]>) toCopy.c_protocolValues
				.clone();
	}

	// returns null if no associated info with classID
	// grabs the value associated with the class.
	/**
	 * 
	 * @param associatedProtocol
	 *            the protocol to get the associatd values
	 * @return the raw bytes that are stored associatd with the passed in
	 *         protocol, returns null if there no bytes assocaited with the
	 *         protocol
	 */
	public byte[] getValue(Protocol associatedProtocol) {
		return c_protocolValues.get(associatedProtocol.getUniqueID());
	}

	// takes in a class, uses the uniqueid to key on the hash table and
	// inserts the value.
	/**
	 * @param associatedProtocol
	 *            the protocol to associated the passed in bytes with
	 * @param value
	 *            the bytes to be associated with the passed in protocl
	 */
	public void putValue(Protocol associatedProtocol, byte[] value) {
		c_protocolValues.put(associatedProtocol.getUniqueID(), value);
	}

	// grabs the keyset of the c_protocolValues. makes
	// it possible for algorithsm to reference values used
	// by other classes.
	public Set<Long> getKeySet() {
		return c_protocolValues.keySet();
	}

	public String toString() {
		String returnString = "";
		for(Long key : c_protocolValues.keySet())
		{
			try {
				returnString += String.valueOf(key) + " " + new String(c_protocolValues.get(key), "UTF-8") + "| ";
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return returnString;
	}

	// OLD PROTOBUF STUFF, may be useful later

	// public void fromProtoValues(ProtoValues protoValues) {
	// for (int i = 0; i < protoValues.getClassValuesCount(); i++) {
	// valueFieldEntry entry = protoValues.getClassValues(i);
	// c_protocolValues.put(entry.getClassID(), entry.getClassInfo()
	// .toByteArray());
	// }
	// }

	// converts the values to the protobuf format for sending on wire
	// public ProtoValues toProtoValues()
	// {
	// Set<Long> keys = c_protocolValues.keySet();
	// Builder values = ProtoValues.newBuilder();
	// int i = 0;
	// for(Long key : keys)
	// {
	// valueFieldEntry keyValueEntry = valueFieldEntry.newBuilder()
	// .setClassID(key.intValue())
	// .setClassInfo(ByteString.copyFrom(c_protocolValues.get(key)))
	// .build();
	// values.addClassValues(keyValueEntry);
	// i++;
	// }
	// return values.build();
	//
	// }

	// takes in a protobuf values and extracts relavent fields
	// public Values(ProtoValues protoValues)
	// {
	// for(int i = 0; i < protoValues.getClassValuesCount(); i++)
	// {
	// valueFieldEntry entry = protoValues.getClassValues(i);
	// c_protocolValues.put(entry.getClassID(),
	// entry.getClassInfo().toByteArray());
	// }
	// }
	//
}
