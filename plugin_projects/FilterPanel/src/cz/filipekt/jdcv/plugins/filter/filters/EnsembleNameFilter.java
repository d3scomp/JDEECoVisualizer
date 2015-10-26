package cz.filipekt.jdcv.plugins.filter.filters;

import java.util.Set;

import cz.filipekt.jdcv.plugins.filter.VisibilityFilter;
import cz.filipekt.jdcv.prefs.MembershipPrefs;

/**
 * A filter which manages to filter out the ensemble memberships with ensemble
 * name not equal to the one given in constructor parameters.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class EnsembleNameFilter extends VisibilityFilter {
	
	/**
	 * Only ensembles with this name will be shown after applying the filter
	 */
	private final String nameValue;
	
	/**
	 * The preferences objects for all ensemble memberships
	 */
	private final Set<MembershipPrefs> ensemblePrefs;
	
	/**
	 * @param nameValue Only ensembles with this name will be shown after applying 
	 * the filter
	 * @param ensemblePrefs The preferences objects for all ensemble memberships
	 */
	public EnsembleNameFilter(String nameValue, Set<MembershipPrefs> ensemblePrefs) {
		this.nameValue = nameValue;
		this.ensemblePrefs = ensemblePrefs;
	}

	/**
	 * @return A short description of what this filter does
	 */
	@Override
	public String toString() {
		return "Ensembles with name " + nameValue;
	}
	
	/**
	 * Notes the ensemble memberships which will be affected by this filter
	 */
	@Override
	public void initializeSelection() {
		affectedNodes.clear();
		for (MembershipPrefs prefs : ensemblePrefs){
			if (!prefs.getEnsembleName().equals(nameValue)){
				affectedNodes.add(prefs);
			}
		}
	}
}