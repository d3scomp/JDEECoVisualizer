package cz.cuni.mff.d3s.jdeeco.visualizer.robotsExample;

import cz.cuni.mff.d3s.jdeeco.visualizer.extensions.DynamicEventHandler;
import cz.cuni.mff.d3s.jdeeco.visualizer.extensions.MapSceneExtensionPoint;
import cz.cuni.mff.d3s.jdeeco.visualizer.extensions.VisualizerPlugin;

/**
 * Extends the functionality of the Visualizer according to the needs of the
 * "cleaning robots" scenario.
 * 
 * @author Ilias Gerostathopoulos <iliasg@d3s.mff.cuni.cz>
 */
public class CleaningRobotsPlugin implements VisualizerPlugin {

	@Override
	public DynamicEventHandler getDynamicEventHandler(Double startAt, Double endAt) {
		return new DirtinessEventHandler(startAt, endAt);
	}

	@Override
	public MapSceneExtensionPoint getMapSceneExtensionPoint() {
		return new DirtinessMapSceneExtension();
	}

}
