package simulator;
/**
 * file: Event.java
 * @author John
 */

/**
 * This class defines a generic Event. Since this is an event-driven simulator,
 * events are responsible for all actions. The MRAI timer firing is an event,
 * receipt of an update message is an event, and so on.
 * 
 * When an update is received, it is processed 
 * 
 */
public class Event implements Comparable<Event> {
	
	public static final int MRAI_EVENT = 1;
	public static final int MSG_EVENT = 2;
	
	/** The time at which this event is to occur */
	long scheduledTime;
	
	int tieBreaker;
	
	/** The AS for whom this event applies */
	int eventFor;
	
	/** The type of event:
	 * MRAI_EVENT
	 * MSG_EVENT
	 */
	int eventType;
	
	/** This field specifies the peer for whom the MRAI timer expired.
	 * Needless to say, this value makes sense only for MRAI_EVENTs
	 */
	int timerExpiredForPeer;
	
	Message msg;
	
	/**
	 * Constructs an event for MRAI timer expired
	 * @param time The scheduled time of the event
	 * @param evtFor The AS which should receive this event
	 * @param expPeer The peer for whom the timer expired
	 */
	public Event(long time, int evtFor, int expPeer) {
		scheduledTime = time;
		eventFor = evtFor;
		timerExpiredForPeer = expPeer;
		eventType = MRAI_EVENT;
	}
	
	/**
	 * Constructs an event for message received
	 * @param time The scheduled time of the event
	 * @param evtFor The AS which should receive this event
	 * @param msg The message
	 */
	public Event(long time, int evtFor, Message msg) {
		scheduledTime = time;
		eventFor = evtFor;
		this.msg = msg;
		eventType = MSG_EVENT;
	}
	
	public void setTieBreaker(int tb) {
		tieBreaker = tb;
	}
	
	public String toString() {
		String eventStr = "" + scheduledTime + ": For AS" + eventFor + " ";
		if(eventType == MRAI_EVENT) {
			eventStr += "Timer expired for AS" + timerExpiredForPeer;
			return "";
		}
		else {
			eventStr += "Msg = " + msg.toString();
		}
		return eventStr;
	}

	public int compareTo(Event b) {
		if(this.scheduledTime > b.scheduledTime)
			return 1;
		else if(this.scheduledTime < b.scheduledTime)
			return -1;
		else { // use tie breaker -- always distinct.
			if(this.tieBreaker > b.tieBreaker) {
				return 1;
			}
			else {
				return -1;
			}
		}
	}
	


}
