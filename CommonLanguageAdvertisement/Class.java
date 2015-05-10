package CommonLanguageAdvertisement;

public class Class {

	private long m_uniqueID;
	
	public Class(long uniqueID){
		m_uniqueID = uniqueID;
	}
	
	public long getUniqueID(){
		return m_uniqueID;
	}
	
	public void setUniqueID(long newUniqueID)
	{
		m_uniqueID = newUniqueID;
	}
}
