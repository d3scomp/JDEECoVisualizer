package cz.filipekt.jdcv.gui_logic;

import cz.filipekt.jdcv.Visualizer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

/**
 * Listener for the event that user clicks the "show plugins panel" option in the menu.
 * Makes sure that the plugins panel is shown/hidden appropriately.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class PluginsPanelHandler implements EventHandler<ActionEvent> {
	
	/**
	 * Marks whether the plugins panel is currently shown.
	 */
	private boolean panelShown = true;
	
	/**
	 * The item in the "view" menu allowing for showing/hiding the plugins panel
	 */
	private final CheckMenuItem menuButton;
	
	/**
	 * Context in which this handler is called
	 */
	private final Visualizer visualizer;

	/**
	 * @param menuButton The item in the "view" menu allowing for showing/hiding 
	 * the plugins panel
	 * @param visualizer Context in which this handler is called
	 */
	public PluginsPanelHandler(CheckMenuItem menuButton, Visualizer visualizer) {
		this.menuButton = menuButton;
		this.visualizer = visualizer;
	}

	/**
	 * Called whenever the user clicks the "show plugins panel" option in the menu. 
	 * Makes sure that the plugins panel is shown/hidden appropriately.
	 */
	@Override
	public void handle(ActionEvent event) {
		Region pluginsColumn = visualizer.getSwitchablePanel();
		HBox middleRow = visualizer.getMiddleRow();
		if (panelShown){
			if (middleRow.getChildren().contains(pluginsColumn)){
				middleRow.getChildren().remove(pluginsColumn);
				panelShown = false;
				menuButton.setSelected(false);
			}
		} else {
			if (!middleRow.getChildren().contains(pluginsColumn)){
				int length = middleRow.getChildren().size();
				middleRow.getChildren().add(length, pluginsColumn);
				panelShown = true;
				menuButton.setSelected(true);
			}
		}
	}
	
}