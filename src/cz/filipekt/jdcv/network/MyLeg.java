package cz.filipekt.jdcv.network;

import java.util.Date;
import java.util.List;

/**
 * Defines one part of the trip between two activities. (I.e. a route with the car, 
 * a walk by foot, a change of the mode, a time where it has to wait for the bus, 
 * and so on.)  At the moment, MATSim provides only 'car mode' legs. (Oct. 2004)
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class MyLeg {
	
	/**
	 * Defines the mode of this leg (e.g. car, bus, foot, bike, etc.).
	 */
	private final String mode;
	
	/**
	 * Departure time for this leg.
	 */
	private final Date depTime;
	
	/**
	 * Travel time for this leg.
	 */
	private final Date travTime;
	
	/**
	 * Arrival time for this leg.
	 */
	private final Date arrTime;
	
	/**
	 * List of links through which this leg goes
	 */
	private final List<MyLink> route;

	/**
	 * @param mode Mode of this leg (e.g. car, bus, foot, bike, etc.).
	 * @param depTime Departure time for this leg.
	 * @param travTime Travel time for this leg.
	 * @param arrTime Arrival time for this leg.
	 * @param route List of links through which this leg goes
	 */
	public MyLeg(String mode, Date depTime, Date travTime, Date arrTime,
			List<MyLink> route) {
		this.mode = mode;
		this.depTime = depTime;
		this.travTime = travTime;
		this.arrTime = arrTime;
		this.route = route;
	}

	/**
	 * @return Mode of this leg (e.g. car, bus, foot, bike, etc.).
	 * @see {@link MyLeg#mode}
	 */
	public String getMode() {
		return mode;
	}

	/**
	 * @return Departure time for this leg.
	 * @see {@link MyLeg#depTime}
	 */
	public Date getDepTime() {
		return depTime;
	}

	/**
	 * @return Travel time for this leg.
	 * @see {@link MyLeg#travTime}
	 */
	public Date getTravTime() {
		return travTime;
	}

	/**
	 * @return Arrival time for this leg.
	 * @see {@link MyLeg#arrTime}
	 */
	public Date getArrTime() {
		return arrTime;
	}

	/**
	 * @return List of links through which this leg goes
	 * @see {@link MyLeg#route}
	 */
	public List<MyLink> getRoute() {
		return route;
	}
	
}
