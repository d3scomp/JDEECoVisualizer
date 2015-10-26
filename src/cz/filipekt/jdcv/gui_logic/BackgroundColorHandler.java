package cz.filipekt.jdcv.gui_logic;

import cz.filipekt.jdcv.MapScene;
import cz.filipekt.jdcv.Visualizer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;

/**
 * Handler for the event that the user selects a new color in the color-picker
 * on the left side of the application window.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class BackgroundColorHandler implements EventHandler<ActionEvent>{
	
	/**
	 * The environment in which we run the color picking
	 */
	private final Visualizer visualizer;
	
	/**
	 * The color-picking controls
	 */
	private final ColorPicker backColor;

	/**
	 * @param visualizer The environment in which we run the color picking
	 * @param backColor The color-picking controls
	 */
	public BackgroundColorHandler(Visualizer visualizer, ColorPicker backColor) {
		this.visualizer = visualizer;
		this.backColor = backColor;
	}

	/**
	 * Run whenever a new color is picked by {@link BackgroundColorHandler#backColor}.
	 * Sets the map background color to the newly selected one.
	 */
	@Override
	public void handle(ActionEvent event) {
		MapScene scene = visualizer.getScene();
		if (scene != null){
			Node map = scene.getMapPane();
			Color color = backColor.getValue();
			String style = Visualizer.getBackColorCSSField() + ": rgb(" + 
					Math.round(color.getRed() * 256) + "," + 
					Math.round(color.getGreen() * 256) + "," + 
					Math.round(color.getBlue() * 256) + ");";
			map.setStyle(style);
		}
	}
	
}