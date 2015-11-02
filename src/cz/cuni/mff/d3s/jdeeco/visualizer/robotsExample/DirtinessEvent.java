package cz.cuni.mff.d3s.jdeeco.visualizer.robotsExample;

import cz.filipekt.jdcv.events.Event;
import cz.filipekt.jdcv.events.EventType;

/**
 * Models an event of type "dirtiness", which appears in the JDEECo
 * event log (runtimeData.xml)
 * 
 * @author Ilias Gerostathopoulos <iliasg@d3s.mff.cuni.cz>
 *
 */
public class DirtinessEvent implements Event {

	/**
	 * A point in time when a node changes dirtiness level (became more
	 * dirty/less dirty/clean)
	 */
	private final double time;

	/**
	 * ID of node which changed dirtiness level (became more dirty/less
	 * dirty/clean)
	 */
	private String node;

	/**
	 * The level of dirtiness captured in the event. Max level is 1.0 (very
	 * dirty), min is 0.0 (clean)
	 */
	private double intensity;

	public DirtinessEvent(double time) {
		this.time = time;
	}

	@Override
	public EventType getType() {
		return EventType.DIRTINESS;
	}

	@Override
	public double getTime() {
		return time;
	}

	public String getNode() {
		return node;
	}

	public double getIntensity() {
		return intensity;
	}
	
	public void setIntensity(double intensity) {
		this.intensity = intensity;
	}

	public void setNode(String node) {
		this.node = node;
	}
	
	@Override
	public String toString(){
		return "Dirtiness at node " + node + " changed to intensity " + intensity + " at time " + time;
	}
}
