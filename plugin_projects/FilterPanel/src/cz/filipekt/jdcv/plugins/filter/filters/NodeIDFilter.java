package cz.filipekt.jdcv.plugins.filter.filters;

import java.util.Map;

import cz.filipekt.jdcv.plugins.filter.VisibilityFilter;
import cz.filipekt.jdcv.prefs.NodePrefs;

/**
 * A filter which manages to filter out the nodes with 
 * id not equal to the one given in constructor parameters.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class NodeIDFilter extends VisibilityFilter {
	
	/**
	 * Only this node will be shown after the filter is applied
	 */
	private final String node;
	
	/**
	 * Database of the preferences objects for all nodes
	 */
	private final Map<String,NodePrefs> nodePrefs;

	/**
	 * @param node Only this node will be shown after the filter is applied
	 * @param nodePrefs Database of the preferences objects for all nodes
	 */
	public NodeIDFilter(String node, Map<String, NodePrefs> nodePrefs) {
		this.node = node;
		this.nodePrefs = nodePrefs;
	}

	/**
	 * Notes the nodes which will be affected by the filter
	 */
	@Override
	public void initializeSelection() {
		for (String nodeID : nodePrefs.keySet()){
			if (!nodeID.equals(node)){
				NodePrefs prefs = nodePrefs.get(nodeID);
				affectedNodes.add(prefs);
			}
		}
	}
	
	/**
	 * @return A short description of what this filter does
	 */
	@Override
	public String toString(){
		return "Nodes with ID equal to " + node;
	}
}