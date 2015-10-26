package cz.filipekt.jdcv.gui_logic;

import cz.filipekt.jdcv.Visualizer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Listener for the event that user clicks the "show graphics panel" option in the menu.
 * Makes sure that the graphics panel is shown/hidden appropriately.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class GraphicsPanelHandler implements EventHandler<ActionEvent> {
	
	/**
	 * Marks whether the graphics panel is currently shown.
	 */
	private boolean panelShown = true;
	
	/**
	 * The item in the "view" menu allowing for showing/hiding the graphics panel
	 */
	private final CheckMenuItem graphicsPanel;
	
	/**
	 * Context in which this handler is called
	 */
	private final Visualizer visualizer;

	/**
	 * @param graphicsPanel The item in the "view" menu allowing for showing/hiding the graphics panel
	 * @param visualizer Context in which this handler is called
	 */
	public GraphicsPanelHandler(CheckMenuItem graphicsPanel, Visualizer visualizer) {
		this.graphicsPanel = graphicsPanel;
		this.visualizer = visualizer;
	}

	/**
	 * Called whenever the user clicks the "show graphics panel" option in the menu. 
	 * Makes sure that the graphics panel is shown/hidden appropriately.
	 */
	@Override
	public void handle(ActionEvent arg0) {
		VBox graphicsColumn = visualizer.getGraphicsColumn();
		HBox middleRow = visualizer.getMiddleRow();
		if (panelShown){
			if (middleRow.getChildren().contains(graphicsColumn)){
				middleRow.getChildren().remove(graphicsColumn);
				panelShown = false;
				graphicsPanel.setSelected(false);
			}
		} else {
			if (!middleRow.getChildren().contains(graphicsColumn)){
				middleRow.getChildren().add(0, graphicsColumn);
				panelShown = true;
				graphicsPanel.setSelected(true);
			}
		}
	}
}