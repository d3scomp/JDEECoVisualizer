package cz.filipekt.jdcv.ensembles;

/**
 * Represents a pair of type (ensemble,agent). Given one such pair (E,A) and one 
 * additional agent B, we can use the ensemble membership predicate to tell 
 * whether the agent B is in the ensemble E with A as the coordinator.
 * Main usage is when determining the colors of the ensemble membership lines
 * in the visualization. 
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class CoordinatorRelation {
	
	/**
	 * Name of an ensemble
	 */
	private final String ensembleName;
	
	/**
	 * @return Name of an ensemble
	 * @see {@link CoordinatorRelation#ensembleName}
	 */
	public String getEnsembleName() {
		return ensembleName;
	}

	/**
	 * ID of a coordinator
	 */
	private final String coordinator;
	
	/**
	 * @return ID of a coordinator
	 * @see {@link CoordinatorRelation#coordinator}
	 */
	public String getCoordinator() {
		return coordinator;
	}

	/**
	 * @param ensembleName Name of an ensemble
	 * @param coordinator ID of a coordinator
	 */
	public CoordinatorRelation(String ensembleName, String coordinator) {
		this.ensembleName = ensembleName;
		this.coordinator = coordinator;
	}

	/**
	 * Depends precisely on the following two fields:
	 * {@link CoordinatorRelation#ensembleName} , {@link CoordinatorRelation#coordinator}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((coordinator == null) ? 0 : coordinator.hashCode());
		result = prime * result
				+ ((ensembleName == null) ? 0 : ensembleName.hashCode());
		return result;
	}

	/**
	 * Equality holds precisely when following fields are equal amid instances:
	 * {@link CoordinatorRelation#ensembleName} , {@link CoordinatorRelation#coordinator}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof CoordinatorRelation)) {
			return false;
		}
		CoordinatorRelation other = (CoordinatorRelation) obj;
		if (coordinator == null) {
			if (other.coordinator != null) {
				return false;
			}
		} else if (!coordinator.equals(other.coordinator)) {
			return false;
		}
		if (ensembleName == null) {
			if (other.ensembleName != null) {
				return false;
			}
		} else if (!ensembleName.equals(other.ensembleName)) {
			return false;
		}
		return true;
	}
}
