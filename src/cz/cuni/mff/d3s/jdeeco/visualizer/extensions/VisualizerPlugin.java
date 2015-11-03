package cz.cuni.mff.d3s.jdeeco.visualizer.extensions;

/**
 * Interface to be implemented by each class that extends the functinality of
 * the visualizer towards application-specific needs. Provides access to (i) a
 * custom dynamic event handler (to handle new events that have a start and end
 * time) and (ii) a map scene extension to visualize the new events.
 * 
 * @author Ilias Gerostathopoulos <iliasg@d3s.mff.cuni.cz>
 */
public interface VisualizerPlugin {

	public DynamicEventHandler getDynamicEventHandler(Double startAt, Double endAt);
	
	public MapSceneExtensionPoint getMapSceneExtensionPoint();
	
}
