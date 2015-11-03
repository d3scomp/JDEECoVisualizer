package cz.filipekt.jdcv.events;

/**
 * Defines the possible values of the "type" attribute of the "event" element 
 * appearing in the simulation output file.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public interface EventType {
	
	/**
	 * The actual value of the type attribute that is represented by the 
	 * {@link EventType#PERSON_ENTERS_VEHICLE} value.
	 */
	public static final String PERSON_ENTERS_VEHICLE = "PersonEntersVehicle";
	
	/**
	 * The actual value of the type attribute that is represented by the 
	 * {@link EventType#PERSON_LEAVES_VEHICLE} value.
	 */
	public static final String PERSON_LEAVES_VEHICLE = "PersonLeavesVehicle";
	
	/**
	 * The actual value of the type attribute that is represented by the 
	 * {@link EventType#ENTERED_LINK} value.
	 */
	public static final String ENTERED_LINK = "entered link";
	
	/**
	 * The actual value of the type attribute that is represented by the 
	 * {@link EventType#LEFT_LINK} value.
	 */
	public static final String LEFT_LINK = "left link";
	
	/**
	 * The actual value of the type attribute that is represented by the 
	 * {@link EventType#DEPARTURE} value.
	 */
	public static final String DEPARTURE = "departure";
	
	/**
	 * The actual value of the type attribute that is represented by the 
	 * {@link EventType#ARRIVAL} value.
	 */
	public static final String ARRIVAL = "arrival";
	
	/**
	 * The actual value of the type attribute that is represented by the 
	 * {@link EventType#ACT_START} value.
	 */
	public static final String ACT_START = "actstart";
	
	/**
	 * The actual value of the type attribute that is represented by the 
	 * {@link EventType#ACT_END} value.
	 */
	public static final String ACT_END = "actend";
	
	/**
	 * The actual value of the type attribute that is represented by the 
	 * {@link EventType#WAIT_2_LINK} value.
	 */
	public static final String wait2linkValue = "wait2link";
	
	/**
	 * The actual value of the type attribute that is represented by the 
	 * {@link EventType#STUCK_AND_ABORT} value.
	 */
	public static final String stuckAndAbortValue = "stuckAndAbort";
	
	/**
	 * The actual value of the type attribute that is represented by the 
	 * {@link EventType#TRAVELLED} value.
	 */
	public static final String travelledValue = "travelled";
	
	/**
	 * The actual value of the type attribute that is represented by the 
	 * {@link EventType#ENSEMBLE} value. 
	 */
	public static final String ENSEMBLE = "ensemble";

}
