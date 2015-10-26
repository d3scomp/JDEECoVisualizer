package cz.filipekt.jdcv.plugins.filter.filters;

import java.util.Map;

import cz.filipekt.jdcv.plugins.filter.VisibilityFilter;
import cz.filipekt.jdcv.prefs.LinkPrefs;

/**
 * A filter which manages to filter out the links with 
 * "to node" not equal to the one given in constructor parameters.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class LinkToFilter extends VisibilityFilter {
	
	/**
	 * Only the link with this "to node" will be shown after the filter
	 * is applied.
	 */
	private final String toNode;
	
	/**
	 * Database of the preferences objects for all links
	 */
	private final Map<String,LinkPrefs> linkPrefs;

	/**
	 * @param toNode Only the link with this "to node" will be shown after 
	 * the filter is applied.
	 * @param linkPrefs Database of the preferences objects for all links
	 */
	public LinkToFilter(String toNode, Map<String, LinkPrefs> linkPrefs) {
		this.toNode = toNode;
		this.linkPrefs = linkPrefs;
	}

	/**
	 * @return A short description of what this filter does
	 */
	@Override
	public String toString() {
		return "Links to node " + toNode;
	}
	
	/**
	 * Notes the links which will be affected by the filter
	 */
	@Override
	public void initializeSelection() {
		for (String linkID : linkPrefs.keySet()){
			LinkPrefs prefs = linkPrefs.get(linkID);
			if (!prefs.getToNode().equals(toNode)){
				affectedNodes.add(prefs);
			}
		}
	}
}