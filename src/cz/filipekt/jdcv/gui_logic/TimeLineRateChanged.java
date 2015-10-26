package cz.filipekt.jdcv.gui_logic;

import cz.filipekt.jdcv.MapScene;
import cz.filipekt.jdcv.Visualizer;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.event.EventHandler;

/**
 * Listener for the {@link Event} that the user clicks a fast forward or
 * rewind button. Makes sure that the visualization is accordingly
 * sped up or down.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class TimeLineRateChanged implements EventHandler<Event>{

	/**
	 * If true, the rate changes to a higher value.
	 * If false, the rate changes to a lower value.
	 */
	private final boolean speedUp;
	
	/**
	 * Context in which this handler is called
	 */
	private final Visualizer visualizer;
	
	/**
	 * Makes sure that the visualization is accordingly sped up or down.
	 */
	@Override
	public void handle(Event arg0) {
		MapScene scene = visualizer.getScene();
		double timeLineRateStep = visualizer.getTimeLineRateStep();
		if (scene != null){
			Timeline timeLine = scene.getTimeLine();
			double old = timeLine.getRate();
			double diff = speedUp ? timeLineRateStep : (-1*timeLineRateStep);
			timeLine.setRate(old + diff);
		}
	}

	/**
	 * @param speedUp If true, the rate changes to a higher value.
	 * If false, the rate changes to a lower value.
	 * @param visualizer Context in which this handler is called
	 */
	public TimeLineRateChanged(boolean speedUp, Visualizer visualizer) {
		this.speedUp = speedUp;
		this.visualizer = visualizer;
	}
}