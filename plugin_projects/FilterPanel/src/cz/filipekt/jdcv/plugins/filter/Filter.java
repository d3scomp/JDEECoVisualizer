package cz.filipekt.jdcv.plugins.filter;

/**
 * A simple filter as used by {@link FilterPanel}.
 * When applied, it filters out some of the visualization elements.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public interface Filter {
	
	/**
	 * Applies the filter, i.e. filters out some of the visualization elements
	 */
	void apply();
	
	/**
	 * Reverts the application of this filter
	 */
	void unapply();
}
