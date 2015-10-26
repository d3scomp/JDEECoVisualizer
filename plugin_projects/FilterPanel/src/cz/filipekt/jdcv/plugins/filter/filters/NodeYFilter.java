package cz.filipekt.jdcv.plugins.filter.filters;

import java.util.Map;

import cz.filipekt.jdcv.plugins.filter.VisibilityFilter;
import cz.filipekt.jdcv.prefs.NodePrefs;

/**
 * A filter which manages to filter out the nodes with 
 * y-coordinate not equal to the one given in constructor parameters.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class NodeYFilter extends VisibilityFilter {
	
	/**
	 * Only nodes with this y-coordinate will be shown after 
	 * the filter is applied
	 */
	private final double yCoord;
	
	/**
	 * Database of the preferences objects for all nodes
	 */
	private final Map<String,NodePrefs> nodePrefs;
	
	/**
	 * @param yCoord Only nodes with this y-coordinate will be shown after 
	 * the filter is applied
	 * @param nodePrefs Database of the preferences objects for all nodes
	 */
	public NodeYFilter(double yCoord, Map<String, NodePrefs> nodePrefs) {
		this.yCoord = yCoord;
		this.nodePrefs = nodePrefs;
	}

	/**
	 * Notes the nodes which will be affected by the filter
	 */
	@Override
	public void initializeSelection() {
		for (String nodeID : nodePrefs.keySet()){
			NodePrefs prefs = nodePrefs.get(nodeID);												
			if (prefs.getY() != yCoord){
				affectedNodes.add(prefs);
			}
		}
	}

	/**
	 * @return A short description of what this filter does
	 */
	@Override
	public String toString() {
		return "Nodes with x-coord equal to " + yCoord; 
	}	
}