package cz.filipekt.jdcv.gui_logic;

import cz.filipekt.jdcv.MapScene;
import cz.filipekt.jdcv.Visualizer;
import javafx.animation.Timeline;
import javafx.animation.Animation.Status;
import javafx.event.Event;
import javafx.event.EventHandler;

/**
 * Listener for the {@link Event} that user clicks the "play/pause visualization" button in the controls panel.
 * Makes sure that the visualization is started/paused accordingly.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class PlayButtonHandler implements EventHandler<Event> {
	
	/**
	 * Context in which this handler is called
	 */
	private final Visualizer visualizer;

	/**
	 * @param visualizer Context in which this handler is called
	 */
	public PlayButtonHandler(Visualizer visualizer) {
		this.visualizer = visualizer;
	}

	/**
	 * Called whenever the user clicks the "play/pause visualization" button in the controls panel.
	 * Makes sure that the visualization is started/paused accordingly.
	 */
	@Override
	public void handle(Event arg0) {
		MapScene scene = visualizer.getScene();
		if (scene != null){
			Timeline timeLine = scene.getTimeLine();
			if (timeLine.getStatus() == Status.RUNNING){
				timeLine.pause();
			} else if (timeLine.getStatus() == Status.STOPPED){
				timeLine.playFromStart();
			} else {
				timeLine.play();
			}
		}
	}
	
}