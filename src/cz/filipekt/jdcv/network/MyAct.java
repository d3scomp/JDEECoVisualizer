package cz.filipekt.jdcv.network;

import java.util.Date;

/**
 * Defines an activity of a person (like working, shopping, etc.). 
 * An activity is placed on a defined coordinate/link of the given network
 * NOTE: Either (x,y) or link must be defined, otherwise the activity cannot 
 * be matched on the given network (see network_v1.dtd on matsim.org). 
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class MyAct {

	/**
	 * Each activity has to have a type, like w(ork), l(eisure), h(ome), 
	 * s(hopping), e(ducation), etc.
	 */
	private final String type;
	
	/**
	 * The x coordinate of the activity. If x and y is not defined, 
	 * the link has to be specified.
	 * Value -1 means the coordinate is not specified
	 */
	private final double x;
	
	/**
	 * The y coordinate of the activity. If x and y is not defined, 
	 * the link has to be specified.
	 * Value -1 means the coordinate is not specified
	 */
	private final double y;
	
	/**
	 * The link where this activity will be performed.  If not 
	 * specified, the x and y attributes must be defined.
	 */
	private final MyLink link;
	
	/**
	 * ID of the facility where this activity will be performed.
	 */
	private final String facility;
	
	/**
	 * Defines when the activity should start
	 */
	private final Date startTime;
	
	/**
	 * Defines when the activity should end.  This is a REQUIRED attribute for 
	 * the FIRST activity, and optional for the other ones.
	 */
	private final Date endTime;
	
	/**
	 * Defines the maximum duration of the activity
	 */
	private final Date maxDur;

	/**
	 * Note: If x and y is not defined, the link has to be specified.
	 * @param type Each activity has to have a type, like w(ork), l(eisure) etc.
	 * @param x The x coordinate of the activity.
	 * @param y The y coordinate of the activity.
	 * @param link The link where this activity will be performed.
	 * @param facility ID of the facility where this activity will be performed.
	 * @param startTime when the activity should start
	 * @param endTime when the activity should end
	 * @param maxDur the maximum duration of the activity
	 */
	public MyAct(String type, double x, double y, MyLink link,
			String facility, Date startTime, Date endTime, Date maxDur) {
		this.type = type;
		this.x = x;
		this.y = y;
		this.link = link;
		this.facility = facility;
		this.startTime = startTime;
		this.endTime = endTime;
		this.maxDur = maxDur;
	}

	/**
	 * @return The type of the activity, like w(ork), l(eisure) etc.
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return The x coordinate of the activity.
	 */
	public double getX() {
		return x;
	}

	/**
	 * @return The y coordinate of the activity.
	 */
	public double getY() {
		return y;
	}

	/**
	 * @return The link where this activity will be performed.
	 */
	public MyLink getLink() {
		return link;
	}

	/**
	 * @return ID of the facility where this activity will be performed.
	 */
	public String getFacility() {
		return facility;
	}

	/**
	 * @return The time when the activity should start
	 */
	public Date getStartTime() {
		return startTime;
	}

	/**
	 * @return The time when the activity should end
	 */
	public Date getEndTime() {
		return endTime;
	}

	/**
	 * @return The maximum duration of the activity
	 */
	public Date getMaxDur() {
		return maxDur;
	}
}
