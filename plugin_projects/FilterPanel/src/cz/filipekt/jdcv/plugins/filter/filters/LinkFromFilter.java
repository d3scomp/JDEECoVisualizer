package cz.filipekt.jdcv.plugins.filter.filters;

import java.util.Map;

import cz.filipekt.jdcv.plugins.filter.VisibilityFilter;
import cz.filipekt.jdcv.prefs.LinkPrefs;

/**
 * A filter which manages to filter out the links with 
 * "from node" not equal to the one given in constructor parameters.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class LinkFromFilter extends VisibilityFilter {
	
	/**
	 * Only the link with this "from node" will be shown after the filter
	 * is applied.
	 */
	private final String fromNode;
	
	/**
	 * Database of the preferences objects for all links
	 */
	private final Map<String,LinkPrefs> linkPrefs;
	
	/**
	 * @param fromNode Only the link with this "from node" will be shown after 
	 * the filter is applied.
	 * @param linkPrefs Database of the preferences objects for all links
	 */
	public LinkFromFilter(String fromNode, Map<String, LinkPrefs> linkPrefs) {
		this.fromNode = fromNode;
		this.linkPrefs = linkPrefs;
	}

	/**
	 * @return A short description of what this filter does
	 */
	@Override
	public String toString() {
		return "Links from node " + fromNode;
	}
	
	/**
	 * Notes the links which will be affected by the filter
	 */
	@Override
	public void initializeSelection() {
		for (String linkID : linkPrefs.keySet()){
			LinkPrefs prefs = linkPrefs.get(linkID);
			if (!prefs.getFromNode().equals(fromNode)){
				affectedNodes.add(prefs);
			}
		}
	}
}