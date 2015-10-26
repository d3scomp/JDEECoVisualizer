package cz.filipekt.jdcv.network;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a facility, which allows certain activities.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class MyFacility {

	/**
	 * Identification of the facility
	 */
	private final String id;
	
	/**
	 * X-coordinate of the facility
	 */
	private final double x;
	
	/**
	 * Y-coordinate of the facility
	 */
	private final double y;

	/**
	 * Activities that can be carried out in this facility.
	 */
	private final List<MyActivity> activities;

	/**
	 * Builds a representation of a "facility" XML element. The first three parameters
	 * correspond to the mandatory attributes, the fourth contains the "activity" 
	 * child elements.
	 * @param id Identification of the facility
	 * @param x X-coordinate of the facility
	 * @param y Y-coordinate of the facility
	 * @param activities Activities that can be carried out in this facility.
	 */
	public MyFacility(String id, double x, double y, List<MyActivity> activities) {
		this.id = id;
		this.x = x;
		this.y = y;
		if (activities == null){
			this.activities = new ArrayList<>();
		} else {
			this.activities = activities;
		}
	}

	/**
	 * @return The identification of the facility
	 * @see {@link MyFacility#id}
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return The x-coordinate of the facility
	 * @see {@link MyFacility#x}
	 */
	public double getX() {
		return x;
	}

	/**
	 * @return The y-coordinate of the facility
	 * @see {@link MyFacility#y}
	 */
	public double getY() {
		return y;
	}

	/**
	 * @return The activities that can be carried out in this facility.
	 * @see {@link MyFacility#activities}
	 */
	public List<MyActivity> getActivities() {
		return activities;
	}
}
