package cz.filipekt.jdcv.events;

import cz.filipekt.jdcv.network.MyLink;

/**
 * Models an event of type "entered link" or "left link", which appears in the MATSIM event log
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class EnteredOrLeftLink implements MatsimEvent {
	
	/**
	 * Time at which the event occurred.
	 */
	private final double time;
	
	/**
	 * ID of the person that entered (or left) the link.
	 */
	private final String person;
	
	/**
	 * The link that has been entered (or left).
	 */
	private final MyLink link;
	
	/**
	 * The vehicle used to enter (or leave) the link.
	 */
	private final String vehicleId;
	
	/**
	 * Specifies whether the event is of type "entered link" or "left link".
	 */
	private final EventType type;

	/**
	 * @return ID of the person that entered (or left) the link.
	 * @see {@link EnteredOrLeftLink#person}
	 */
	@Override
	public String getPerson() {
		return person;
	}

	/**
	 * @return The link that has been entered (or left).
	 * @see {@link EnteredOrLeftLink#link}
	 */
	public MyLink getLink() {
		return link;
	}

	/**
	 * @return The vehicle used to enter (or leave) the link.
	 * @see {@link EnteredOrLeftLink#vehicleId}
	 */
	public String getVehicleId() {
		return vehicleId;
	}

	/**
	 * {@inheritDoc}
	 * @return Specifies whether the event is of type "entered link" or "left link".
	 * @see {@link EnteredOrLeftLink#type}
	 */
	@Override
	public EventType getType() {
		return type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getTime() {
		return time;
	}

	/**
	 * @param entered If true, the event type is "entered link". If false, the event type is "left link".
	 * @param time Time at which the event occurred.
	 * @param person ID of the person that entered (or left) the link.
	 * @param link The link that has been entered (or left).
	 * @param vehicleId The vehicle used to enter (or leave) the link.
	 */
	public EnteredOrLeftLink(boolean entered, double time, String person, MyLink link, String vehicleId) {
		if (entered){
			type = EventType.ENTERED_LINK;
		} else {
			type = EventType.LEFT_LINK;
		}
		this.time = time;
		this.person = person;
		this.link = link;
		this.vehicleId = vehicleId;
	}

}
