package cz.filipekt.jdcv.plugins.filter.filters;

import java.util.Set;

import cz.filipekt.jdcv.plugins.filter.VisibilityFilter;
import cz.filipekt.jdcv.prefs.MembershipPrefs;

/**
 * A filter which manages to filter out the ensemble memberships with 
 * coordinator not equal to the one given in constructor parameters.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class EnsembleCoordFilter extends VisibilityFilter {
	
	/**
	 * Only ensembles with this coordinator will be shown after applying the filter
	 */
	private final String coordinatorID;
	
	/**
	 * The preferences objects for all ensemble memberships
	 */
	private final Set<MembershipPrefs> ensemblePrefs;
	
	/**
	 * @param coordinatorID Only ensembles with this coordinator will be shown after 
	 * applying the filter
	 * @param ensemblePrefs The preferences objects for all ensemble memberships
	 */
	public EnsembleCoordFilter(String coordinatorID, Set<MembershipPrefs> ensemblePrefs) {
		this.coordinatorID = coordinatorID;
		this.ensemblePrefs = ensemblePrefs;
	}

	/**
	 * @return A short description of what this filter does
	 */
	@Override
	public String toString() {
		return "Ensembles with coordinator " + coordinatorID;
	}
	
	/**
	 * Notes the ensemble memberships which will be affected by this filter
	 */
	@Override
	public void initializeSelection() {
		affectedNodes.clear();
		for (MembershipPrefs prefs : ensemblePrefs){
			if (!prefs.getCoordinator().equals(coordinatorID)){
				affectedNodes.add(prefs);
			}
		}
	}
}