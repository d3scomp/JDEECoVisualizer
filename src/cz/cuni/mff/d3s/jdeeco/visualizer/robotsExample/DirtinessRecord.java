package cz.cuni.mff.d3s.jdeeco.visualizer.robotsExample;

import java.util.HashMap;

import cz.cuni.mff.d3s.deeco.runtimelog.RuntimeLogRecord;
import cz.cuni.mff.d3s.jdeeco.visualizer.network.Node;

/**
 * 
 * @author Dominik Skoda <skoda@d3s.mff.cuni.cz>
 *
 */
public class DirtinessRecord extends RuntimeLogRecord {

	public DirtinessRecord(String id) {
		super(id, new HashMap<>());
	}
	
	public void setIntensity(double intensity){
		recordValues.put("intensity", intensity);
	}
	
	public void setNode(Node node){
		recordValues.put("node", node.getId());
	}
	
}
