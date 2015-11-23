package cz.cuni.mff.d3s.jdeeco.visualizer.records;

import java.util.HashMap;

import cz.cuni.mff.d3s.deeco.runtimelog.RuntimeLogRecord;

public abstract class VehicleRecord extends RuntimeLogRecord {

	public VehicleRecord(String id) {
		super(id, new HashMap<String, Object>());
	}
	
	public void setVehicle(String vehicle){
		recordValues.put("vehicle", vehicle);
	}
	
	public void setPerson(String person){
		recordValues.put("person", person);
	}

}
