package cz.filipekt.jdcv.events;

import cz.filipekt.jdcv.network.MyLink;

/**
 * Models an event of type "departure" or "arrival", which appears in the MATSIM event log
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class ArrivalOrDeparture implements MatsimEvent {
	
	public static enum LegMode {
		CAR, TRANSIT_WALK;
		
		private static final String carName = "car";
		private static final String transitName = "transit_walk";
		
		public static LegMode from(String value){
			if (value == null){
				return null;
			}
			switch (value) {
				case carName:
					return CAR;
				case transitName:
					return TRANSIT_WALK;
				default:
					return null;					
				}
		}
	}
	
	/**
	 * Specifies whether this event is an arrival or a departure.
	 */
	private final String type;
	
	/**
	 * Time at which the event occurred.
	 */
	private final double time;
	
	/**
	 * ID of the person that arrived (or departed).
	 */
	private String person;
	
	/**
	 * The link through which the person arrived (or departed).
	 */
	private MyLink link;
	
	/**
	 * Mode of the leg that started (or ended) with this departure (or arrival).
	 */
	private String legMode;

	public void setPerson(String person) {
		this.person = person;
	}

	public void setLink(MyLink link) {
		this.link = link;
	}

	public void setLegMode(String legMode) {
		this.legMode = legMode;
	}
	
	/**
	 * {@inheritDoc}
	 * @see {@link ArrivalOrDeparture#type}
	 */
	@Override
	public String getType() {
		return type;
	}

	/**
	 * {@inheritDoc}
	 * @see {@link ArrivalOrDeparture#time}
	 */
	@Override
	public double getTime() {
		return time;
	}

	/**
	 * @return ID of the person that arrived (or departed).
	 * @see {@link ArrivalOrDeparture#person}
	 */
	@Override
	public String getPerson() {
		return person;
	}

	/**
	 * @return The link through which the person arrived (or departed).
	 * @see {@link ArrivalOrDeparture#link}
	 */
	public MyLink getLink() {
		return link;
	}

	/**
	 * @return Mode of the leg that started (or ended) with this departure (or arrival).
	 * @see {@link ArrivalOrDeparture#legMode}
	 */
	public String getLegMode() {
		return legMode;
	}

	/**
	 * @param departure If true, the event is of type "departure". If false, the event is of type "arrival". 
	 * @param time Time at which the event occurred.
	 * @param person ID of the person that arrived (or departed).
	 * @param link The link through which the person arrived (or departed).
	 * @param legMode Mode of the leg that started (or ended) with this departure (or arrival).
	 */
	public ArrivalOrDeparture(boolean departure, double time, String person, MyLink link, String legMode) {
		if (departure){
			type = EventType.DEPARTURE;
		} else {
			type = EventType.ARRIVAL;
		}		
		this.time = time;
		this.person = person;
		this.link = link;
		this.legMode = legMode;
	}

	/**
	 * @param departure If true, the event is of type "departure". If false, the event is of type "arrival". 
	 * @param time Time at which the event occurred.
	 */
	public ArrivalOrDeparture(boolean departure, double time) {
		if (departure) {
			type = EventType.DEPARTURE;
		} else {
			type = EventType.ARRIVAL;
		}
		this.time = time;
	}

}
