package cz.filipekt.jdcv.gui_logic;

import cz.filipekt.jdcv.MapScene;
import cz.filipekt.jdcv.Visualizer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;

/**
 * Handler for the event that the user checks/unchecks the "show nodes" check-box.
 * Shows or hides the nodes appropriately.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class ShowNodesHandler implements EventHandler<ActionEvent>{

	/**
	 * The environment in which the handler operates
	 */
	private final Visualizer visualizer;
	
	/**
	 * The check-box for showing/hiding nodes
	 */
	private final CheckBox showNodesBox;
	
	/**
	 * @param visualizer The environment in which the handler operates
	 * @param showNodesBox The check-box for showing/hiding nodes
	 */
	public ShowNodesHandler(Visualizer visualizer, CheckBox showNodesBox) {
		this.visualizer = visualizer;
		this.showNodesBox = showNodesBox;
	}

	/**
	 * Run whenever the {@link ShowNodesHandler#showNodesBox} is checked/unchecked.
	 * Shows or hides the nodes appropriately.
	 */
	@Override
	public void handle(ActionEvent event) {
		MapScene scene = visualizer.getScene();
		if (scene != null){
			scene.setNodesVisible(showNodesBox.isSelected());
		}
	}
	
}