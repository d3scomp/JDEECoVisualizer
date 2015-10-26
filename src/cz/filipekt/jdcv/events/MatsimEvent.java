package cz.filipekt.jdcv.events;


/**
 * Represents an event element from the MATSIM output.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public interface MatsimEvent extends Event{
	
	/**
	 * @return ID of the person involved in the event
	 */
	String getPerson();
}
