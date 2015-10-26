package cz.filipekt.jdcv.gui_logic;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import cz.filipekt.jdcv.checkpoints.CheckPoint;
import cz.filipekt.jdcv.checkpoints.CheckPointDatabase;
import cz.filipekt.jdcv.plugins.InfoPanel;

/**
 * Handler for the event that the user clicks on a person visualization.
 * It shows detailed info about that person in the info-panel.
 */
public class InfoPanelSetter implements EventHandler<MouseEvent>{
	
	/**
	 * Pieces of information to be shown in the info-panel.
	 */
	private final Map<String,String> info;
	
	/**
	 * The checkpoints (position of people) as encountered when parsing the input XML 
	 * files. Contains positions of people on the map at specified times.
	 */
	private final CheckPointDatabase checkpointDb;

	/**
	 * @param personID ID of the person whose info will be shown
	 */
	public InfoPanelSetter(String personID, CheckPointDatabase checkpointDb) {	
		this.checkpointDb = checkpointDb;
		if ((checkpointDb != null) && (checkpointDb.getList(personID) != null)){				
			Map<String,String> personInfo = getInfoForPerson(personID);
			if (personInfo == null){
				throw new NullPointerException();
			} else {
				this.info = personInfo;				
			}
		} else {
			throw new NullPointerException();
		}				
	}
	
	/**
	 * Given a person ID, this method creates a collection of key-value pairs for 
	 * use by the info side-panel, with each pair containing a piece of relevant 
	 * information about the person.
	 * @param personID ID of the person
	 * @return Collection of key-value pairs for use by the info side-panel
	 */
	private Map<String,String> getInfoForPerson(String personID){
		List<CheckPoint> checkPoints = checkpointDb.getList(personID);
		Map<String,String> res = new LinkedHashMap<>();
		res.put("Person ID", personID);
		for (CheckPoint cp : checkPoints){
			String key = "Time " + cp.getTime();
			String value = null;
			switch (cp.getType()){
				case PERSON_ENTERS:
					value = "person enters vehicle";
					break;
				case PERSON_LEAVES:
					value = "persons leaves vehicle";
					break;
				case LINK_ENTERED:
					value = "enters link " + cp.getLinkID();
					break;
				case LINK_LEFT:
					value = "leaves link " + cp.getLinkID();
					break;
			}
			if (value != null){
				res.put(key, value);
			}
		}
		return res;
	}

	/**
	 * Called when the user clicks on a person visualization.
	 */
	@Override
	public void handle(MouseEvent arg0) {
		InfoPanel.getInstance().setInfo("Person/car selected:", info);
	}
}