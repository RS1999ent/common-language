package CommonLanguageAdvertisement;

import static org.junit.Assert.*;

import org.junit.Test;

public class ClassTest {

	public static long UNIQUEID = 234245;
	@Test
	public void testClassLong() {
		Class aClass = new Class(UNIQUEID);
		assert(aClass.getUniqueID() == UNIQUEID);
	}

	@Test
	public void testClassProtoClass() {
		fail("Not yet implemented");
	}

	@Test
	public void testToProtoClass() {
		fail("Not yet implemented");
	}

}
