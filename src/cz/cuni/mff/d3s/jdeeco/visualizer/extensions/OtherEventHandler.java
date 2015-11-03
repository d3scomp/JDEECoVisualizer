package cz.cuni.mff.d3s.jdeeco.visualizer.extensions;

import java.util.List;

import org.xml.sax.ContentHandler;

import cz.filipekt.jdcv.events.Event;

/**
 * Models an event handler for events other than the core events (matsim events
 * and ensemble events). Its should be used to extend the capabilities of the
 * visualizer so that it can parse custom events.
 * 
 * @author Ilias Gerostathopoulos <iliasg@d3s.mff.cuni.cz>
 */
public interface OtherEventHandler extends ContentHandler {
	
	public List<Event> getEvents();
	
	public String getEventType();
}
