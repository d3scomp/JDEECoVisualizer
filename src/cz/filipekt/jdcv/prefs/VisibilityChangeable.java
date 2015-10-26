package cz.filipekt.jdcv.prefs;

/**
 * Marks those preferences objects that allow to make the affected object (in)visible
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public interface VisibilityChangeable {
	
	/**
	 * Allows to set the affected object (in)visible
	 * @param visible
	 */
	void setVisible(boolean visible);
	
	/**
	 * @return True iff the object is currently visible
	 */
	boolean isVisible();
}
