package cz.filipekt.jdcv.plugins.filter.filters;

import java.util.Map;

import cz.filipekt.jdcv.plugins.filter.VisibilityFilter;
import cz.filipekt.jdcv.prefs.LinkPrefs;

/**
 * A filter which manages to filter out the links with 
 * id not equal to the one given in constructor parameters.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class LinkIDFilter extends VisibilityFilter {
	
	/**
	 * Only the link with this id will be shown after the filter
	 * is applied.
	 */
	private final String idValue;
	
	/**
	 * Database of the preferences objects for all links
	 */
	private final Map<String,LinkPrefs> linkPrefs;

	/**
	 * @param idValue Only the link with this id will be shown after 
	 * the filter is applied.
	 * @param linkPrefs Database of the preferences objects for all links
	 */
	public LinkIDFilter(String idValue, Map<String, LinkPrefs> linkPrefs) {
		this.idValue = idValue;
		this.linkPrefs = linkPrefs;
	}

	/**
	 * Notes the links which will be affected by the filter
	 */
	@Override
	public void initializeSelection() {
		for (String linkID : linkPrefs.keySet()){
			if (!linkID.equals(idValue)){
				LinkPrefs prefs = linkPrefs.get(linkID);
				affectedNodes.add(prefs);
			}
		}
	}

	/**
	 * @return A short description of what this filter does
	 */
	@Override
	public String toString() {
		return "Links with ID equal to " + idValue;
	}
}