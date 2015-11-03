package cz.cuni.mff.d3s.jdeeco.visualizer.extensions;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import cz.filipekt.jdcv.MapScene;
import cz.filipekt.jdcv.events.Event;
import cz.filipekt.jdcv.events.EventType;
import javafx.animation.KeyFrame;

/**
 * Models an extension of the map scene, that can provide more KeyFrames to the
 * simulation in order to extend the capability of the visualizer to visualize
 * events other than the core events.
 * 
 * @author Ilias Gerostathopoulos <iliasg@d3s.mff.cuni.cz>
 */
public interface MapSceneExtensionPoint {

	Collection<KeyFrame> buildFrames(Map<String, List<Event>> otherEvents, MapScene mapScene) throws IOException;
	
}
