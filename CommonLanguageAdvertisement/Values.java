package CommonLanguageAdvertisement;

import protobuf.AdvertisementProtos;
import protobuf.AdvertisementProtos.ProtoValues;
import protobuf.AdvertisementProtos.ProtoValues.Builder;
import protobuf.AdvertisementProtos.ProtoValues.valueFieldEntry;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Set;

import com.google.protobuf.ByteString;

public class Values {
	private Hashtable<Long, byte[]> classValues = new Hashtable<Long, byte[]>();
	
	public Values(){
		
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
			values.setClassValues(i, keyValueEntry);
			i++;
		}
		return values.build();
		
	}
	
	
}


