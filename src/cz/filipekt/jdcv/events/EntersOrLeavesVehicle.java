package cz.filipekt.jdcv.events;

/**
 * Models an event of type "PersonEntersVehicle" or "PersonLeavesVehicle", which appears in the MATSIM event log
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class EntersOrLeavesVehicle implements MatsimEvent {
	
	/**
	 * Time at which this event occurred
	 */
	private final double time;
	
	/**
	 * Specifies whether the event type is "PersonEntersVehicle" or "PersonLeavesVehicle"
	 */
	private final EventType type;

	/**
	 * {@inheritDoc}
	 * @see {@link EntersOrLeavesVehicle#type}
	 */
	@Override
	public EventType getType() {
		return type;
	}

	/**
	 * {@inheritDoc}
	 * @see {@link EntersOrLeavesVehicle#time}
	 */
	@Override
	public double getTime() {
		return time;
	}
	
	/**
	 * ID of the person that entered (or left) the vehicle.
	 */
	private final String person;

	/**
	 * @return ID of the person that entered (or left) the vehicle.
	 * @see {@link EntersOrLeavesVehicle#person}
	 */
	@Override
	public String getPerson() {
		return person;
	}
	
	/**
	 * The vehicle that has been entered (or left).
	 */
	private final String vehicleId;

	/**
	 * @return The vehicle that has been entered (or left).
	 * @see {@link EntersOrLeavesVehicle#vehicleId}
	 */
	public String getVehicleId() {
		return vehicleId;
	}

	/**
	 * @param entered If true, the event type is "PersonEntersVehicle". If false, the event type is "PersonLeavesVehicle".
	 * @param time Time at which this event occurred
	 * @param person ID of the person that entered (or left) the vehicle.
	 * @param vehicleId The vehicle that has been entered (or left).
	 */
	public EntersOrLeavesVehicle(boolean entered, double time, String person, String vehicleId) {
		if (entered){
			type = EventType.PERSON_ENTERS_VEHICLE;
		} else {
			type = EventType.PERSON_LEAVES_VEHICLE;
		}
		this.time = time;
		this.person = person;
		this.vehicleId = vehicleId;
	}

}
