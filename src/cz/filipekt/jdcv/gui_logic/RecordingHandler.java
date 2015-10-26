package cz.filipekt.jdcv.gui_logic;

import java.io.File;

import cz.filipekt.jdcv.MapScene;
import cz.filipekt.jdcv.Visualizer;
import cz.filipekt.jdcv.util.Dialog;
import cz.filipekt.jdcv.util.Dialog.Type;
import javafx.animation.Timeline;
import javafx.animation.Animation.Status;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 * Listener for the {@link Event} that user clicks the "record video" button.
 * Makes sure that the recording is started or stopped accordingly.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class RecordingHandler implements EventHandler<Event>{
	
	/**
	 * The "record" button that has been clicked
	 */
	private final Button recordButton;
	
	/**
	 * The graphics that is shown inside the {@link RecordingHandler#recordButton} when
	 * recording is not in progress 
	 */
	private final ImageView recordStartImage;
	
	/**
	 * The graphics that is shown inside the {@link RecordingHandler#recordButton} when
	 * recording is in progress
	 */
	private final ImageView recordStopImage;
	
	/**
	 * Context in which this handler is called
	 */
	private final Visualizer visualizer;

	/**
	 * @param recordButton The "record" button that has been clicked
	 * @param recordStartImage The graphics that is shown inside the {@link RecordingHandler#recordButton} when
	 * recording is not in progress 
	 * @param recordStopImage The graphics that is shown inside the {@link RecordingHandler#recordButton} when
	 * recording is in progress
	 * @param visualizer Context in which this handler is called
	 */
	public RecordingHandler(Button recordButton,
			ImageView recordStartImage, ImageView recordStopImage, Visualizer visualizer) {
		this.recordButton = recordButton;
		this.recordStartImage = recordStartImage;
		this.recordStopImage = recordStopImage;
		this.visualizer = visualizer;
	}

	/**
	 * Called whenever the user clicks the "record video" button.
	 * Makes sure that the recording is started or stopped accordingly.
	 */
	@Override
	public void handle(Event arg0) {
		MapScene scene = visualizer.getScene();		
		if (scene != null){			
			if (scene.isRecordingInProgress()){
				stopRecording(scene);
			} else {
				startRecording(scene);
			}					
		}
	}
	
	/**
	 * Stops the recording
	 * @param scene The scene that is being recorded
	 */
	public void stopRecording(MapScene scene){
		if ((scene != null) && (scene.isRecordingInProgress())){			
			scene.setRecordingInProgress(false);
			scene.flushRecordedFrames(Integer.MAX_VALUE);
			recordButton.setText("Record");
			recordButton.setGraphic(recordStartImage);			
		}
	}
	
	/**
	 * Starts the recording
	 * @param scene The scene to be recorded
	 */
	public void startRecording(MapScene scene){
		if ((scene != null) && (!scene.isRecordingInProgress())){
			Timeline timeLine = scene.getTimeLine();
			boolean paused = false;
			if (timeLine.getStatus() == Status.RUNNING){
				timeLine.pause();
				paused = true;
			}
			DirectoryChooser dirChooser = new DirectoryChooser();
			dirChooser.setTitle("Select a folder");
			Stage stage = visualizer.getStage();
			File dir = dirChooser.showDialog(stage);
			if (dir != null){
				scene.setRecordingDirectory(dir);
				scene.setRecordingInProgress(true);
				recordButton.setText("Stop");
				recordButton.setGraphic(recordStopImage);
				if (scene.getZoom() != 1.0){
					scene.setZoom(1);
					Dialog.show(Type.INFO, "The zoom has been restored.", 
							"Recording only allowed when zoom is not in effect.");
				}
			}
			if (paused){
				timeLine.play();
			}
		}
	}
}