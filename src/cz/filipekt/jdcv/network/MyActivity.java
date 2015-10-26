package cz.filipekt.jdcv.network;

import java.util.List;

/**
 * Defines spatial and temporal properties for a specific 
 * activity to be performed in a zone.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class MyActivity {
	
	/**
	 * Activity type, e.g. work, shop, home...
	 * Mandatory attribute.
	 */
	private final String type;
	
	/**
	 * A cell can hold a limited capacity per activity. A capacity is the 
	 * maximum number of something in a given time range, e.g. number of 
	 * costumers shopping at the same time or number of workplaces.
	 * Default value is {@link Long#MAX_VALUE}.
	 */
	private final long capacity;

	/**
	 * A variable amount of opening times describing on what times the facility is 
	 * open and therefore accessible for this type of activity.
	 * Default value: empty {@link List}.
	 */
	private final List<MyOpenTime> openTimes;

	/**
	 * @param type Activity type, e.g. work, shop, home...
	 * @param capacity Maximum number of something in a given time range, e.g. number of 
	 * costumers shopping at the same time
	 * @param openTimes What times the facility is open
	 */
	public MyActivity(String type, long capacity, List<MyOpenTime> openTimes) {
		this.type = type;
		this.capacity = capacity;
		this.openTimes = openTimes;
	}

	/**
	 * @return Activity type, e.g. work, shop, home...
	 * @see {@link MyActivity#type}
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return Maximum number of something in a given time range, e.g. number of 
	 * costumers shopping at the same time
	 * @see {@link MyActivity#capacity}
	 */
	public long getCapacity() {
		return capacity;
	}

	/**
	 * @return What times the facility is open
	 * @see {@link MyActivity#openTimes}
	 */
	public List<MyOpenTime> getOpenTimes() {
		return openTimes;
	}
}
