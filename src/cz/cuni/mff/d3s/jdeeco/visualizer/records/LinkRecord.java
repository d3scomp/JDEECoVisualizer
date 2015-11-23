package cz.cuni.mff.d3s.jdeeco.visualizer.records;

import cz.cuni.mff.d3s.jdeeco.visualizer.network.Link;

public abstract class LinkRecord extends VehicleRecord {

	public LinkRecord(String id) {
		super(id);
	}
	
	public void setLink(Link link){
		recordValues.put("link", link.getId());
	}

}
