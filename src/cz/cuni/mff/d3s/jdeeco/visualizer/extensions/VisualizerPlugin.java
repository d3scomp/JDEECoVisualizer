package cz.cuni.mff.d3s.jdeeco.visualizer.extensions;

import java.util.Collection;

/**
 * Interface to be implemented by each class that extends the functionality of
 * the visualizer towards application-specific needs. Provides access to (i) a
 * collection of custom dynamic event handlers (to handle new events that have a
 * start and end time) and (ii) a collection of map scene extensions to
 * visualize the new events.
 * 
 * @author Ilias Gerostathopoulos <iliasg@d3s.mff.cuni.cz>
 */
public interface VisualizerPlugin {

	public Collection<OtherEventHandler> getDynamicEventHandlers(Double startAt, Double endAt);
	
	public Collection<MapSceneExtensionPoint> getMapSceneExtensionPoints();
	
}
