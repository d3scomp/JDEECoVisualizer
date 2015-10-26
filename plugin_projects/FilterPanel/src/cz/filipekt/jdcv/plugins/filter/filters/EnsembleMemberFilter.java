package cz.filipekt.jdcv.plugins.filter.filters;

import java.util.Set;

import cz.filipekt.jdcv.plugins.filter.VisibilityFilter;
import cz.filipekt.jdcv.prefs.MembershipPrefs;

/**
 * A filter which manages to filter out the ensemble memberships with 
 * member not equal to the one given in constructor parameters.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class EnsembleMemberFilter extends VisibilityFilter {
	
	/**
	 * Only ensembles with this member will be shown after applying the filter
	 */
	private final String memberID;
	
	/**
	 * The preferences objects for all ensemble memberships
	 */
	private final Set<MembershipPrefs> ensemblePrefs;
	
	/**
	 * @param memberID Only ensembles with this member will be shown after 
	 * applying the filter
	 * @param ensemblePrefs The preferences objects for all ensemble memberships
	 */
	public EnsembleMemberFilter(String memberID, Set<MembershipPrefs> ensemblePrefs) {
		this.memberID = memberID;
		this.ensemblePrefs = ensemblePrefs;
	}

	/**
	 * @return A short description of what this filter does
	 */
	@Override
	public String toString() {
		return "Ensembles with member " + memberID;
	}
	
	/**
	 * Notes the ensemble memberships which will be affected by this filter
	 */
	@Override
	public void initializeSelection() {
		affectedNodes.clear();
		for (MembershipPrefs prefs : ensemblePrefs){
			if (!prefs.getMember().equals(memberID)){
				affectedNodes.add(prefs);
			}
		}
	}
}