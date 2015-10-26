package cz.filipekt.jdcv.checkpoints;

/**
 * Marks a relevant event on the timeline. Can hold three types of information -
 * that a person entered a vehicle, left a vehicle, or is at a specified place at specified 
 * time with the car.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class CheckPoint {	
	
	/**
	 * A {@link CheckPoint} can bear various types of information - the types
	 * are specified here 
	 */
	public static enum Type{
		
		/**
		 * Person entered a car
		 */
		PERSON_ENTERS, 
		
		/**
		 * Person left a car
		 */
		PERSON_LEAVES, 
		
		/**
		 * Person enters a link
		 */
		LINK_ENTERED,
		
		/**
		 * Person leaves a link
		 */
		LINK_LEFT;
	}
	
	/**
	 * Specifies the type of information this {@link CheckPoint} bears.
	 */
	private final CheckPoint.Type type;
	
	/**
	 * @return The type of information this {@link CheckPoint} bears.
	 * @see {@link CheckPoint.Type}
	 */
	public CheckPoint.Type getType() {
		return type;
	}
	
	/**
	 * ID of the link
	 */
	private final String linkID;
	
	/**
	 * @return ID of the link
	 */
	public String getLinkID() {
		return linkID;
	}

	/**
	 * A point in time
	 */
	private final double time;
	
	/**
	 * Who is driving
	 */
	private final String person;
	
	/**
	 * @return Who is driving
	 * @see {@link CheckPoint#person}
	 */
	public String getPerson() {
		return person;
	}
	
	/**
	 * Vehicle used
	 */
	private final String vehicle;

	/**
	 * @return Vehicle used
	 * @see {@link CheckPoint#vehicle}
	 */
	public String getVehicle() {
		return vehicle;
	}

	/**
	 * @return A point in time
	 * @see {@link CheckPoint#time}
	 */
	public double getTime() {
		return time;
	}

	/**
	 * @param linkID ID of the link
	 * @param time A point in time
	 * @param person Who is driving/moving
	 * @param ID of the vehicle driven
	 * @param type The type of information this {@link CheckPoint} bears.
	 */
	public CheckPoint(String linkID, double time, String person, String vehicle, Type type) {
		this.linkID = linkID;
		this.time = time;
		this.person = person;
		this.vehicle = vehicle;
		this.type = type;
	}
	
	/**
	 * Equal to calling {@code CheckPoint(0,0,time,person,type)}. Useful when the type
	 * is one of {@code PERSON_ENTERS} , {@code PERSON_LEAVES}
	 * @param time
	 * @param person
	 * @param type
	 */
	public CheckPoint(double time, String person, String vehicle, Type type) {
		this(null, time, person, vehicle, type);
	}
	
	
}