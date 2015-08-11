import java.util.ArrayList;

/**
 * file: UWMessage.java
 * @author John
 *
 */

/**
 * This class defines an Update/Withdrawal message. Since
 * both are processed similarly, a common superclass is declared.
 */
public class UWMessage extends Message {
	Integer[] prefixes;
	Path asPath = null;
	
	public static Integer[] toArray( ArrayList<Integer> a ) {
		int size = a.size();
		Integer[] array = new Integer[size];
		for(int i=0; i<size; i++) {
			array[i] = a.get(i);
		}
		return array;
	}
}
