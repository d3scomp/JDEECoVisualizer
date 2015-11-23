package cz.cuni.mff.d3s.jdeeco.visualizer.records;

import java.util.HashMap;

import cz.cuni.mff.d3s.deeco.runtimelog.RuntimeLogRecord;
import cz.cuni.mff.d3s.jdeeco.visualizer.network.Link;

public abstract class LinkRecord extends RuntimeLogRecord {

	public LinkRecord(String id) {
		super(id, new HashMap<String, Object>());
	}
	
	public void setLink(Link link){
		recordValues.put("link", link.getId());
	}
	
	public void setPerson(String person){
		recordValues.put("person", person);
	}

}
