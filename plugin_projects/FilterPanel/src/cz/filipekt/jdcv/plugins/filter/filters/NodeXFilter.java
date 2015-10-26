package cz.filipekt.jdcv.plugins.filter.filters;

import java.util.Map;

import cz.filipekt.jdcv.plugins.filter.VisibilityFilter;
import cz.filipekt.jdcv.prefs.NodePrefs;

/**
 * A filter which manages to filter out the nodes with 
 * x-coordinate not equal to the one given in constructor parameters.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class NodeXFilter extends VisibilityFilter {
	
	/**
	 * Only nodes with this x-coordinate will be shown after 
	 * the filter is applied
	 */
	private final double xCoord;
	
	/**
	 * Database of the preferences objects for all nodes
	 */
	private final Map<String,NodePrefs> nodePrefs;

	/**
	 * @param xCoord Only nodes with this x-coordinate will be shown after 
	 * the filter is applied
	 * @param nodePrefs Database of the preferences objects for all nodes
	 */
	public NodeXFilter(double xCoord, Map<String, NodePrefs> nodePrefs) {
		this.xCoord = xCoord;
		this.nodePrefs = nodePrefs;
	}

	/**
	 * Notes the nodes which will be affected by the filter
	 */
	@Override
	public void initializeSelection() {
		for (String nodeID : nodePrefs.keySet()){
			NodePrefs prefs = nodePrefs.get(nodeID);												
			if (prefs.getX() != xCoord){
				affectedNodes.add(prefs);
			}
		}
	}

	/**
	 * @return A short description of what this filter does
	 */
	@Override
	public String toString() {
		return "Nodes with x-coord equal to " + xCoord; 
	}	
}