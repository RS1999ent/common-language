/**
 * file: UpdateDependency.java
 * @author John
 *
 */

/**
 * This is a simple container class that describes a
 * conditionally incomplete update
 */
public class UpdateDependency {
	RootCause update;
	RootCause dependsOn;
	
	/**
	 * Constructor for describing a conditionally incomplete update
	 * @param u The update that is cond. incomplete
	 * @param d The update that this depends on
	 */
	public UpdateDependency(RootCause u, RootCause d) {
		update = u;
		dependsOn = d;
	}
	
	public String toString() {
		return "" + update + "|" + dependsOn;
	}
}
