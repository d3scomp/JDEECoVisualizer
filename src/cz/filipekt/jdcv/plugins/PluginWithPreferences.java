package cz.filipekt.jdcv.plugins;

import cz.filipekt.jdcv.prefs.GlobalPrefs;
import cz.filipekt.jdcv.prefs.PreferencesBuilder;

/**
 * Base class for all plugins that need to access some of the visualizations preferences and parameters
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public abstract class PluginWithPreferences implements Plugin {
	
	/**
	 * Generates the preferences objects for visualization elements
	 */
	private PreferencesBuilder preferences;
	
	/**
	 * Allows for setting the global preferences of the visualization
	 */
	private GlobalPrefs globalPrefs;
	
	/**
	 * Sets the preferences wrapper object
	 * @param preferences Generates the preferences objects for visualization elements
	 */
	public final void setPreferences(PreferencesBuilder preferences) {
		this.preferences = preferences;
	}

	/**
	 * @return Generates the preferences objects for visualization elements
	 */
	public final PreferencesBuilder getPreferences() {
		return preferences;
	}
	
	/**
	 * Sets the global preferences object
	 * @param globalPrefs Allows for setting the global preferences of the visualization
	 */
	public final void setGlobalPrefs(GlobalPrefs globalPrefs) {
		this.globalPrefs = globalPrefs;
	}
	
	/**
	 * @return Allows for setting the global preferences of the visualization
	 */
	public final GlobalPrefs getGeneralPrefs() {
		return globalPrefs;
	}
	
	
}
