package cz.filipekt.jdcv.events;

/**
 * Defines the possible values of the "type" attribute of the "event" element 
 * appearing in the simulation output file.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public enum EventType {
	
	PERSON_ENTERS_VEHICLE,
	PERSON_LEAVES_VEHICLE,
	ENTERED_LINK,
	LEFT_LINK,
	DEPARTURE,
	ARRIVAL,
	ACT_START,
	ACT_END,
	WAIT_2_LINK,
	STUCK_AND_ABORT,
	TRAVELLED,
	ENSEMBLE,
	DIRTINESS;
	
	/**
	 * The actual value of the type attribute that is represented by the 
	 * {@link EventType#PERSON_ENTERS_VEHICLE} value.
	 */
	private static final String personEntersVehicleValue = "PersonEntersVehicle";
	
	/**
	 * The actual value of the type attribute that is represented by the 
	 * {@link EventType#PERSON_LEAVES_VEHICLE} value.
	 */
	private static final String personLeavesVehicleValue = "PersonLeavesVehicle";
	
	/**
	 * The actual value of the type attribute that is represented by the 
	 * {@link EventType#ENTERED_LINK} value.
	 */
	private static final String enteredLinkValue = "entered link";
	
	/**
	 * The actual value of the type attribute that is represented by the 
	 * {@link EventType#LEFT_LINK} value.
	 */
	private static final String leftLinkValue = "left link";
	
	/**
	 * The actual value of the type attribute that is represented by the 
	 * {@link EventType#DEPARTURE} value.
	 */
	private static final String departureValue = "departure";
	
	/**
	 * The actual value of the type attribute that is represented by the 
	 * {@link EventType#ARRIVAL} value.
	 */
	private static final String arrivalValue = "arrival";
	
	/**
	 * The actual value of the type attribute that is represented by the 
	 * {@link EventType#ACT_START} value.
	 */
	private static final String actStartValue = "actstart";
	
	/**
	 * The actual value of the type attribute that is represented by the 
	 * {@link EventType#ACT_END} value.
	 */
	private static final String actEndValue = "actend";
	
	/**
	 * The actual value of the type attribute that is represented by the 
	 * {@link EventType#WAIT_2_LINK} value.
	 */
	private static final String wait2linkValue = "wait2link";
	
	/**
	 * The actual value of the type attribute that is represented by the 
	 * {@link EventType#STUCK_AND_ABORT} value.
	 */
	private static final String stuckAndAbortValue = "stuckAndAbort";
	
	/**
	 * The actual value of the type attribute that is represented by the 
	 * {@link EventType#TRAVELLED} value.
	 */
	private static final String travelledValue = "travelled";
	
	/**
	 * The actual value of the type attribute that is represented by the 
	 * {@link EventType#ENSEMBLE} value. 
	 */
	private static final String ensembleValue = "ensemble";
	
	/**
	 * @param text Value of the type attribute in an event element
	 * @return The enum representation of the value of the type attribute. 
	 * If the attribute value is unrecognized, null is returned.
	 */
	public static EventType from(String text){
		switch (text){
			case EventType.personEntersVehicleValue:
				return EventType.PERSON_ENTERS_VEHICLE;
			case EventType.personLeavesVehicleValue:
				return EventType.PERSON_LEAVES_VEHICLE;
			case EventType.enteredLinkValue:
				return EventType.ENTERED_LINK;
			case EventType.leftLinkValue:
				return EventType.LEFT_LINK;
			case EventType.departureValue:
				return EventType.DEPARTURE;
			case EventType.arrivalValue:
				return EventType.ARRIVAL;
			case EventType.actStartValue:
				return EventType.ACT_START;
			case EventType.actEndValue:
				return EventType.ACT_END;
			case EventType.wait2linkValue:
				return EventType.WAIT_2_LINK;
			case EventType.stuckAndAbortValue:
				return EventType.STUCK_AND_ABORT;
			case EventType.travelledValue:
				return EventType.TRAVELLED;
			case EventType.ensembleValue:
				return EventType.ENSEMBLE;
			default:
				return null;
		}
	}
}
