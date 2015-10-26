package cz.filipekt.jdcv.gui_logic;

import cz.filipekt.jdcv.MapScene;
import cz.filipekt.jdcv.Visualizer;
import javafx.animation.Timeline;
import javafx.animation.Animation.Status;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

/**
 * Handles the event that the stop button is clicked. Stops the visualization,
 * makes the necessary GUI changes, and if needed, stops the recording process.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class StopButtonAction implements EventHandler<Event>{
	
	/**
	 * The current visualization
	 */
	private final Visualizer visualizer;
	
	/**
	 * The stop button that has been clicked
	 */
	private final Button stopButton;
	
	/**
	 * Handles recording actions. It is relevant here, because on the stop button
	 * click, any recordings should be stopped.
	 */
	private final RecordingHandler recordingHandler;

	/**
	 * @param visualizer The current visualization
	 * @param stopButton The stop button that has been clicked
	 * @param recordingHandler Handles recording actions. It is relevant here, 
	 * because on the stop button click, any recordings should be stopped.
	 */
	public StopButtonAction(Visualizer visualizer, Button stopButton, RecordingHandler recordingHandler) {
		this.visualizer = visualizer;
		this.stopButton = stopButton;
		this.recordingHandler = recordingHandler;
	}

	/**
	 * Stops the visualization, makes the necessary GUI changes, and if needed, 
	 * stops the recording process.
	 */
	@Override
	public void handle(Event event) {
		MapScene scene = visualizer.getScene();
		if (scene != null){
			Timeline timeline = scene.getTimeLine();
			if ((timeline.getStatus() == Status.RUNNING) || (timeline.getStatus() == Status.PAUSED)){
				timeline.stop();
				stopButton.setDisable(true);
				if ((recordingHandler != null) && (scene != null)){
					recordingHandler.stopRecording(scene);
				}
			}
		}
	}
	
}