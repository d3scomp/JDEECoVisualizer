package cz.filipekt.jdcv.plugins.filter;

import java.util.Collection;
import java.util.HashSet;

import cz.filipekt.jdcv.prefs.VisibilityChangeable;

/**
 * Base class for filters that change visibility of the affected object when applied
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public abstract class VisibilityFilter implements Filter {
	
	/**
	 * The visualization elements that will be affected by this filter
	 */
	protected final Collection<VisibilityChangeable> affectedNodes = new HashSet<>();							
	
	/**
	 * Fills {@link VisibilityFilter#affectedNodes} with relevant visualization elements
	 */
	protected abstract void initializeSelection();
	
	/**
	 * The outcome of this method is used in the GUI to identify the filter, 
	 * thus it is explicitly required to override it.
	 * @return Short description of what this filter does
	 */
	public abstract String toString();
	
	/**
	 * Reverts the application of this filter
	 */
	@Override
	public void unapply() {
		for (VisibilityChangeable prefs : affectedNodes){
			if (!prefs.isVisible()){
				prefs.setVisible(true);
			}
		}
	}
	
	/**
	 * Applies the filter, i.e. filters out some of the visualization elements
	 */
	@Override
	public void apply() {
		initializeSelection();
		for (VisibilityChangeable prefs : affectedNodes){
			if (prefs.isVisible()){
				prefs.setVisible(false);
			}
		}
	}
			
}