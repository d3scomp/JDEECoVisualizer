package cz.cuni.mff.d3s.jdeeco.visualizer.extensions;

import java.util.List;

import cz.filipekt.jdcv.events.Event;
import cz.filipekt.jdcv.events.EventType;

/**
 * 
 * @author Ilias Gerostathopoulos <iliasg@d3s.mff.cuni.cz>
 */
public abstract class DynamicEventHandler implements OtherEventHandler {

	/**
	 * If true, only the events starting after time
	 * {@link DynamicEventHandler#startAtLimit} are taken into account
	 */
	protected final boolean startAtConstraint;

	/**
	 * If {@link DynamicEventHandler#startAtConstraint} holds, only events starting
	 * from this time on are taken into account
	 */
	protected final double startAtLimit;

	/**
	 * If true, only the events ending before time
	 * {@link DynamicEventHandler#endAtLimit} are taken into account
	 */
	protected final boolean endAtConstraint;

	/**
	 * If {@link DynamicEventHandler#endAtConstraint} holds, only the events ending
	 * before this time are taken into account
	 */
	protected final double endAtLimit;

	/**
	 * @param startAt
	 *            Only events starting from this time on are taken into account.
	 *            If null, no such constraint is applied.
	 * @param endAt
	 *            Only the events ending before this time are taken into
	 *            account. If null, no such constraint is applied.
	 */
	public DynamicEventHandler(Double startAt, Double endAt) {
		if (startAt == null) {
			startAtConstraint = false;
			startAtLimit = -1;
		} else {
			startAtConstraint = true;
			startAtLimit = startAt;
		}
		if (endAt == null) {
			endAtConstraint = false;
			endAtLimit = -1;
		} else {
			endAtConstraint = true;
			endAtLimit = endAt;
		}
	}
	
	public List<Event> getEvents() {
		return null;
	};
	
	public EventType getEventType() {
		return null;
	};
}
