package cz.filipekt.jdcv.ensembles;


/**
 * Represents a triplet of type (ensemble,agent,agent).
 * The ensemble membership predicate is defined precisely
 * on these triplets, so this class provides means to work
 * with that predicate.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class MembershipRelation extends CoordinatorRelation {
	
	/**
	 * A member of ensemble
	 */
	private final String member;

	/**
	 * @return A member of ensemble
	 * @see {@link MembershipRelation#member}
	 */
	public String getMember() {
		return member;
	}

	/**
	 * @param ensembleName Name of an ensemble
	 * @param coordinator ID of a coordinator
	 * @param member A member of ensemble
	 */
	public MembershipRelation(String ensembleName, String coordinator, String member) {
		super(ensembleName, coordinator);
		this.member = member;
	}

	/**
	 * Depends precisely on the following three fields:
	 * {@link CoordinatorRelation#ensembleName} , 
	 * {@link CoordinatorRelation#coordinator} ,
	 * {@link MembershipRelation#member}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((member == null) ? 0 : member.hashCode());
		return result;
	}

	/**
	 * Equality holds precisely when following fields are equal amid instances:
	 * {@link CoordinatorRelation#ensembleName} , 
	 * {@link CoordinatorRelation#coordinator} ,
	 * {@link MembershipRelation#member}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof MembershipRelation)) {
			return false;
		}
		MembershipRelation other = (MembershipRelation) obj;
		if (member == null) {
			if (other.member != null) {
				return false;
			}
		} else if (!member.equals(other.member)) {
			return false;
		}
		return true;
	}
}
