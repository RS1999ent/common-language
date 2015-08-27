package CommonLanguageAdvertisement;

import static org.junit.Assert.*;
import java.util.Random;
import org.junit.Test;

public class ClassTest {

	public static long UNIQUEID = 234245;
	@Test
	public void testClassLong() {
		
		Class aClass = new Class(UNIQUEID);
		assert(aClass.getUniqueID() == UNIQUEID);
		Random random = new Random();
		long newUniqueID = random.nextLong();
		aClass.setUniqueID(newUniqueID);
		assert(aClass.getUniqueID() == newUniqueID);
	}

	@Test
	public void testClassProtoClass() {
		Class aClass = new Class(UNIQUEID);
		Class protoClass = new Class(aClass.toProtoClass());
		assert(aClass.getUniqueID() == protoClass.getUniqueID());
	}

//	@Test
//	public void testToProtoClass() {
//		fail("Not yet implemented");
//	}

}
