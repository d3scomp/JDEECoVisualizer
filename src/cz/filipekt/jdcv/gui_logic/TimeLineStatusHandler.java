package cz.filipekt.jdcv.gui_logic;

import javafx.animation.Timeline;
import javafx.animation.Animation.Status;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

/**
 * Listener for the event that the status of the {@link Timeline} has changed.
 * When fired, the buttons in the controls panel are enabled/disabled accordingly.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class TimeLineStatusHandler implements ChangeListener<Status> {
	
	/**
	 * The "play/pause visualization" button
	 */
	private final Button playButton;
	
	/**
	 * The "stop visualization" button
	 */
	private final Button stopButton;
	
	/**
	 * The image containing the universal "play" symbol
	 */
	private final ImageView playImage;
	
	/**
	 * The image containing the universal "pause" symbol
	 */
	private final ImageView pauseImage;

	/**
	 * @param playButton The "play/pause visualization" button
	 * @param stopButton The "stop visualization" button
	 * @param playImage The image containing the universal "play" symbol
	 * @param pauseImage The image containing the universal "pause" symbol
	 */
	public TimeLineStatusHandler(Button playButton, Button stopButton, ImageView playImage, ImageView pauseImage) {
		this.playButton = playButton;
		this.stopButton = stopButton;
		this.playImage = playImage;
		this.pauseImage = pauseImage;
	}

	/**
	 * Called whenever the status of the {@link Timeline} changes. 
	 * When fired, the buttons in the controls panel are enabled/disabled accordingly.
	 */
	@Override
	public void changed(ObservableValue<? extends Status> arg0, Status oldStatus, Status newStatus) {
		if (newStatus == Status.RUNNING){
			playButton.setGraphic(pauseImage);
		} else {
			playButton.setGraphic(playImage);
		}
		if (newStatus == Status.STOPPED){
			stopButton.setDisable(true);
		} else {
			stopButton.setDisable(false);
		}
	}
}