package cz.cuni.mff.d3s.jdeeco.visualizer.records;

public abstract class VehicleRecord extends PersonRecord {

	public VehicleRecord(String id) {
		super(id);
	}
	
	public void setVehicle(String vehicle){
		recordValues.put("vehicle", vehicle);
	}

}
