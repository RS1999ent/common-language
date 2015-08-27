package CommonLanguageAdvertisement;

import protobuf.AdvertisementProtos.ProtoValues;
import protobuf.AdvertisementProtos.ProtoValues.Builder;
import protobuf.AdvertisementProtos.ProtoValues.valueFieldEntry;

import java.util.Hashtable;
import java.util.Set;

import com.google.protobuf.ByteString;

//class that holds the values associated with a particular key
//or edge in the graph.  Meant so that multiple algorithms can
//use the same key but still have some control over their information.

public class Values {
	
	private Hashtable<Long, byte[]> classValues = 
			new Hashtable<Long, byte[]>(); //keyed in by classid, value is 
										   //is just raw bytes. more flexible
	
	public Values(){
		
	}
	
	//takes in a protobuf values and extracts relavent fields
	public Values(ProtoValues protoValues)
	{
		for(int i = 0; i < protoValues.getClassValuesCount(); i++)
		{
			valueFieldEntry entry = protoValues.getClassValues(i);
			classValues.put(entry.getClassID(), entry.getClassInfo().toByteArray());
		}
	}
	
	//returns null if no associated info with classID
	//grabs the value associated with the class.
	public byte[] getValue(Class associatedClass){
		return 	classValues.get(associatedClass.getUniqueID());
	}
	
	//takes in a class, uses the uniqueid to key on the hash table and
	//inserts the value.
	public void putValue(Class associatedClass, byte[] value)
	{		
		classValues.put(associatedClass.getUniqueID(), value);
	}
	
	//grabs the keyset of the classValues.  makes
	//it possible for algorithsm to reference values used
	//by other classes.
	public Set<Long> getKeySet(){
		return classValues.keySet();
	}
	
	//converts the values to the protobuf format for sending on wire
	public ProtoValues toProtoValues()
	{
		Set<Long> keys = classValues.keySet();
		Builder values = ProtoValues.newBuilder();
		int i = 0;
		for(Long key : keys)
		{
			valueFieldEntry keyValueEntry = valueFieldEntry.newBuilder()
					.setClassID(key.intValue())
					.setClassInfo(ByteString.copyFrom(classValues.get(key)))
					.build();
			values.addClassValues(keyValueEntry);
			i++;
		}
		return values.build();
		
	}
	
	public void fromProtoValues(ProtoValues protoValues) {
		for (int i = 0; i < protoValues.getClassValuesCount(); i++) {
			valueFieldEntry entry = protoValues.getClassValues(i);
			classValues.put(entry.getClassID(), entry.getClassInfo()
					.toByteArray());
		}
	}
	
	public String toString()
	{
		return classValues.toString();
	}
	
	
}


