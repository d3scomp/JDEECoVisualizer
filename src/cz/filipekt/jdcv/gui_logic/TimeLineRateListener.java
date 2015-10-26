package cz.filipekt.jdcv.gui_logic;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;

/**
 * Listener for the event that the rate of the visualization timeline has changed.
 * The new rate is written to the given label.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class TimeLineRateListener implements ChangeListener<Number>{
	
	/**
	 * The label where the change in timeline rate will be recorded
	 */
	private final Label speedLabel;

	/**
	 * @param speedLabel The label where the change in timeline rate will be recorded
	 */
	public TimeLineRateListener(Label speedLabel) {
		this.speedLabel = speedLabel;
	}

	/**
	 * Writes the new timeline rate to the given label
	 */
	@Override
	public void changed(ObservableValue<? extends Number> observable, Number oldValue, 
			Number newValue) {
		speedLabel.setText("Speed: " + newValue.doubleValue() + "x");
	}
	
}