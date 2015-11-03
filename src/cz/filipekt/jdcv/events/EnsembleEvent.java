package cz.filipekt.jdcv.events;

/**
 * Models an event of type "ensemble", which appears in the JDEECo ensemble event log
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class EnsembleEvent implements Event{
	
	/**
	 * The coordinator of the ensemble
	 */
	private String coordinator;
	
	/**
	 * A member of the ensemble
	 */
	private String member;
	
	/**
	 * If true, {@link EnsembleEvent#member} is a member of {@link EnsembleEvent#ensemble} where
	 * {@link EnsembleEvent#coordinator} is the coordinator.
	 */
	private boolean membership;

	/**
	 * Name of the ensemble
	 */
	private String ensemble;
	
	/**
	 * A point in time when {@link EnsembleEvent#membership} holds
	 */
	private final double time;

	/**
	 * @return The coordinator of the ensemble
	 */
	public String getCoordinator() {
		return coordinator;
	}

	/**
	 * @return A member of the ensemble
	 */
	public String getMember() {
		return member;
	}

	/**
	 * @return If true, {@link EnsembleEvent#member} is a member of {@link EnsembleEvent#ensemble} where
	 * {@link EnsembleEvent#coordinator} is the coordinator.
	 */
	public boolean getMembership() {
		return membership;
	}

	/**
	 * @return Name of the ensemble
	 */
	public String getEnsemble() {
		return ensemble;
	}

	/**
	 * @return Type of this event, i.e. {@link EventType#ENSEMBLE}
	 * @see {@link EventType}
	 */
	@Override
	public String getType() {
		return EventType.ENSEMBLE;
	}

	/**
	 * @return A point in time when {@link EnsembleEvent#membership} holds
	 * @see {@link EnsembleEvent#time}
	 */
	@Override
	public double getTime() {
		return time;
	}

	/**
	 * @param coordinator The coordinator of the ensemble
	 * @param member A member of the ensemble
	 * @param membership If true, the specified 'member' is a member of the specified 'ensemble' with
	 * the specified coordinator
	 * @param ensemble Name of the ensemble
	 * @param time A point in time when 'membership' holds
	 */
	public EnsembleEvent(String coordinator, String member, boolean membership,
			String ensemble, double time) {
		this.coordinator = coordinator;
		this.member = member;
		this.membership = membership;
		this.ensemble = ensemble;
		this.time = time;
	}

	public EnsembleEvent(double time) {
		this.time = time;
	}
	
	public void setMember(String member) {
		this.member = member;
	}
	
	public void setCoordinator(String coordinator) {
		this.coordinator = coordinator;
	}

	public void setMembership(boolean membership) {
		this.membership = membership;
	}

	public void setEnsemble(String ensemble) {
		this.ensemble = ensemble;
	}

	@Override
	public String toString() {
		return "EnsembleEvent [coordinator=" + coordinator + ", member="
				+ member + ", membership=" + membership + ", ensemble="
				+ ensemble + ", time=" + time + "]";
	}

}
