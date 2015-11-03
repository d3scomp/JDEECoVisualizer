package cz.filipekt.jdcv.events;

/**
 * Represents an event element, either from the MATSIM output or from ensemble log output.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public interface Event {
	
	/**
	 * @return The type of the event.
	 */
	String getType();
	
	/**
	 * @return The time at which the event occurred.
	 */
	double getTime();
}
