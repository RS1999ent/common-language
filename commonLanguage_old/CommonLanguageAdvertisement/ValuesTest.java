package CommonLanguageAdvertisement;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Random;
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ValuesTest {

	public Values filledValues = new Values();
	public Hashtable<Long, byte[]> enteredKeyValues = 
			new Hashtable<Long, byte[]>();
	@Before
	public void setUpBefore() throws Exception {
		filledValues = new Values();
		enteredKeyValues.clear();
		Random random = new Random();
		for(int i = 0; i < 200; i++)
		{
			byte[] arr = new byte[100];
			random.nextBytes(arr);
			filledValues.putValue(new Class(i), arr);
			enteredKeyValues.put((long) i, arr);
		}
		for(Long key : filledValues.getKeySet())
		{
			assert(Arrays.equals(enteredKeyValues.get(key),
					filledValues.getValue(new Class(key))));
		}
	}

	@Test
	public void testValuesProtoValues() {
		Values newValue = new Values(filledValues.toProtoValues());
		for(Long key : filledValues.getKeySet())
		{
			assert(Arrays.equals(newValue.getValue(new Class(key)),
					filledValues.getValue(new Class(key))));
		}
	}

	@Test
	public void testFromProtoValues() {
		Values newValue = new Values();
		for(Long key : filledValues.getKeySet())
		{
			assert(Arrays.equals(newValue.getValue(new Class(key)),
					filledValues.getValue(new Class(key))));
		}
	}

}
