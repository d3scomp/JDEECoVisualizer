package cz.filipekt.jdcv.gui_logic;

import cz.filipekt.jdcv.MapScene;
import cz.filipekt.jdcv.Visualizer;
import javafx.event.Event;
import javafx.event.EventHandler;

/**
 * Handler for the event that the user changes the zoom level.
 * Makes sure that the visualization is zoomed in/out properly.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class ZoomingHandler implements EventHandler<Event> {
	
	/**
	 * Factor by which the current zoom will be multiplied.
	 */
	private final double factor;
	
	/**
	 * Context in which this handler is called
	 */
	private final Visualizer visualizer;

	/**
	 * @param factor Factor by which the current zoom will be multiplied.
	 * @param visualizer Context in which this handler is called
	 */
	public ZoomingHandler(double factor, Visualizer visualizer) {
		this.factor = factor;
		this.visualizer = visualizer;
	}

	/**
	 * Called whenever the user changes the zoom level.
	 * Makes sure that the visualization is zoomed in/out properly.
	 */
	@Override
	public void handle(Event arg0) {
		MapScene scene = visualizer.getScene();
		if (scene != null){
			scene.changeZoom(factor);
		}
	}
}