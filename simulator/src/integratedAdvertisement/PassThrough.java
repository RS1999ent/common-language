/**
 * 
 */
package integratedAdvertisement;

import java.util.HashMap;
import java.util.LinkedList;

import simulator.AS.PoPTuple;

/**
 * Class that holds passthrough information and attaches passthrough information
 * to advertisements
 * 
 * @author David
 *
 */
public class PassThrough {

	// keyed on pathTokey. links to aggregated values that were received. IA
	// value contains info for a single path
	HashMap<String, IAInfo> passThroughDatabase = new HashMap<String, IAInfo>();

	public PassThrough() {

	}
	
	public void clear(){
		passThroughDatabase.clear();
	}

	// attach passthrough information based on an advertisement that is about to
	// go out
	/**
	 * method that attaches passthrough information based on an advertisment
	 * that is about to out that is, that is, the path is not going to change
	 * 
	 * @param advertisement
	 *            the advertisement to attach passthrough information to (this
	 *            object is mutated)
	 * @return advertisement with passthrough information attached, this is
	 *         redundant given how java does references
	 */
	public IA attachPassthrough(IA advertisement, PoPTuple chosenTuple) {
		// for each path, attach values from passthroughdatabase
		for (String pathKey : advertisement.getPathKeys()) {
			// grab the path, and attach passthrough informatin based on next
			// hop. this should only be called when
			// you have a fully formed path ready to be advertised
			LinkedList<Integer> path = (LinkedList<Integer>) advertisement
					.getPath(pathKey).clone();
			path.remove();
			String passThroughPathKey = IA.pathToKey(path);
			// merge pasthrough information into advertisement if there is
			// somethign in database
			// only does it for path attributes, can be extended to do edge and
			// as descriptors
			if (passThroughDatabase.containsKey(passThroughPathKey)) {
				IAInfo passThroughInfo = passThroughDatabase
						.get(passThroughPathKey);
				Values val1 = advertisement.getPathAttributes(chosenTuple, pathKey);
				// if there was no path attribute values set for the
				// advertisement, then make a new values
				if (val1 == null) {
					val1 = new Values();
				}
				Values val2 = passThroughInfo
						.getPathAttributes(passThroughPathKey);
				Values mergedVal = mergeValues(val1, val2);

				advertisement.setPathAttributes(chosenTuple, mergedVal,
						advertisement.getPath(pathKey));

			}
		}
		return advertisement; // REDUNDANT, advertisement is changed directly,
								// may change later
	}

	/**
	 * method that adds a received advertisement to the passthroughdatabase
	 * merges the information in this advertisement with that already contained
	 * in the database (if there is one there) we overwrite exhisting values in
	 * the database from this advertisement if there is some
	 * 
	 * @param receivedAdvert
	 *            the advertismeent recieved from a neighbor
	 */
	public void addToDatabase(IA receivedAdvert) {
		for (String key : receivedAdvert.getPathKeys()) {
			IA toDatabase = passThroughDatabase.containsKey(key) ? passThroughDatabase
					.get(key) : new IA(receivedAdvert); // if the passthrogh
			// database already has an
			// entry for this path, then
			// use that as base,
			// otherwise craete new
			// entry
			Values val1 = toDatabase.getPathAttributes(key);
			Values val2 = receivedAdvert.getPathAttributes(key);
			// if either val1 or val2 is null, give it a fresh blank vlaues.
			val1 = val1 == null ? new Values() : val1;
			val2 = val2 == null ? new Values() : val2;
			toDatabase.setPathAttributes(mergeValues(val2, val1),
					toDatabase.getPath()); // merge val2 first because we want
											// to overwrite old received values
											// in advert alrady in database

			passThroughDatabase.put(key, toDatabase);
		}

	}

	// removes path from database. Used with path is withdrawn by peer. should
	// use key formed by pathToKey
	/**
	 * method that removes a path and associated passthrough information (based
	 * on path key generaated by IA.pathToKey(path)) from passthroug database
	 * 
	 * @param string
	 *            key of path to be removed
	 */
	public void removeFromDatabase(String string) {
		passThroughDatabase.remove(string);
	}

	// merges two Values together. val1 is considered the base (i.e. the fields
	// set in there will not be changed in merge.)
	// only protocol information not contained in val1 will be merged from val2.
	// Val1 has precedence
	/**
	 * helper method that merges to sets of values together, the preemininte
	 * value is val1. val1 cannot be null, otherwise will crash
	 * 
	 * @param val1
	 *            the values in this won't be overwritten, only new values from
	 *            val2 will be put in with it, cannot be null
	 * @param val2
	 *            the values to merge with val1, will not overwrite existing
	 *            values in val1
	 * @return returns a set of merged values, redundant because of the way that
	 *         java handles references
	 */
	private Values mergeValues(Values val1, Values val2) {
		// Values mergedValue = new Values();
		for (Long protocol : val2.getKeySet()) {
			if (!val1.getKeySet().contains(protocol)) {
				val1.putValue(new Protocol(protocol),
						val2.getValue(new Protocol(protocol)));
			}
		}
		return val1; // again, know this return is redundant as val1 reference
						// is directly changed.
	}

}
