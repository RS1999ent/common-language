package CommonLanguageAdvertisement;

import protobuf.AdvertisementProtos.ProtoValues;
import protobuf.AdvertisementProtos.ProtoValues.Builder;
import protobuf.AdvertisementProtos.ProtoValues.valueFieldEntry;

import java.util.Hashtable;
import java.util.Set;

import com.google.protobuf.ByteString;

public class Values {
	private Hashtable<Long, byte[]> classValues = new Hashtable<Long, byte[]>();
	
	public Values(){
		
	}
	
	public Values(ProtoValues protoValues)
	{
		for(int i = 0; i < protoValues.getClassValuesCount(); i++)
		{
			valueFieldEntry entry = protoValues.getClassValues(i);
			classValues.put(entry.getClassID(), entry.getClassInfo().toByteArray());
		}
	}
	
	//returns null if no associated info with classID
	public byte[] getValue(Class associatedClass){
		return 	classValues.get(associatedClass.getUniqueID());
	}
	
	public void putValue(Class associatedClass, byte[] value)
	{
		classValues.put(associatedClass.getUniqueID(), value);
	}
	
	public Set<Long> getKeySet(){
		return classValues.keySet();
	}
	
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


