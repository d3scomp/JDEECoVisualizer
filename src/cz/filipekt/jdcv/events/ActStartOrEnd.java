package cz.filipekt.jdcv.events;

import cz.filipekt.jdcv.network.MyLink;

/**
 * Models an event of type "actstart" or "actend", which appears in the MATSIM event log
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class ActStartOrEnd implements MatsimEvent {
	
	/**
	 * The time at which the event occurred.
	 */
	private final double time;
	
	/**
	 * Specifies whether the event type is "actstart" or "actend".
	 */
	private final EventType type;
	
	/**
	 * ID of the person that started (or ended) with the activity.
	 */
	private final String person;
	
	/**
	 * The link at which the activity started (or ended).
	 */
	private final MyLink link;
	
	/**
	 * ID of the facility at which the activity started (or ended).
	 */
	private final String facility;
	
	/**
	 * The type of the activity that started (or ended).
	 */
	private final String actType;

	/**
	 * @return ID of the person that started (or ended) with the activity.
	 * @see {@link ActStartOrEnd#person}
	 */
	@Override
	public String getPerson() {
		return person;
	}

	/**
	 * @return The link at which the activity started (or ended).
	 * @see {@link ActStartOrEnd#link}
	 */
	public MyLink getLink() {
		return link;
	}

	/**
	 * @return ID of the facility at which the activity started (or ended).
	 * @see {@link ActStartOrEnd#facility}
	 */
	public String getFacility() {
		return facility;
	}

	/**
	 * @return The type of the activity that started (or ended).
	 * @see {@link ActStartOrEnd#actType}
	 */
	public String getActType() {
		return actType;
	}

	/**
	 * {@inheritDoc}
	 * @see {@link ActStartOrEnd#type}
	 */
	@Override
	public EventType getType() {
		return type;
	}

	/**
	 * {@inheritDoc}
	 * @see {@link ActStartOrEnd#time}
	 */
	@Override
	public double getTime() {
		return time;
	}

	/**
	 * @param start If true, the event type is "actstart". If false, the event type is "actend".
	 * @param time The time at which the event occurred.
	 * @param person ID of the person that started (or ended) with the activity.
	 * @param link The link at which the activity started (or ended).
	 * @param facility ID of the facility at which the activity started (or ended).
	 * @param actType The type of the activity that started (or ended).
	 */
	public ActStartOrEnd(boolean start, double time, String person, MyLink link,
			String facility, String actType) {
		if (start){
			type = EventType.ACT_START;
		} else {
			type = EventType.ACT_END;
		}
		this.time = time;
		this.person = person;
		this.link = link;
		this.facility = facility;
		this.actType = actType;
	}

}
