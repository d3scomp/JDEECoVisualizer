package cz.filipekt.jdcv.events;

import cz.filipekt.jdcv.network.MyLink;

/**
 * Represents an event element from the simulation output. Moreover, 
 * the event provides information about the link it happened on.  
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public interface EventWithLink extends Event {
	
	/**
	 * @return ID of the link the event occurred on.
	 */
	MyLink getLink();
}
