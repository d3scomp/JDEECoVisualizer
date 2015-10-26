package cz.filipekt.jdcv.gui_logic;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javafx.animation.Animation.Status;
import javafx.animation.Timeline;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import javax.imageio.ImageIO;

import cz.filipekt.jdcv.MapScene;
import cz.filipekt.jdcv.Visualizer;
import cz.filipekt.jdcv.util.Dialog;

/**
 * Listener for the {@link Event} that user clicks the "screenshot" button.
 * Makes sure that the screenshot is made and saved correctly.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class ScreenShotHandler implements EventHandler<Event>{
	
	/**
	 * Context in which this handler is called
	 */
	private final Visualizer visualizer;

	/**
	 * @param visualizer Context in which this handler is called
	 */
	public ScreenShotHandler(Visualizer visualizer) {
		this.visualizer = visualizer;
	}

	/**
	 * Called whenever the user clicks the "screenshot" button.
	 * Makes sure that the screenshot is made and saved correctly.
	 */
	@Override
	public void handle(Event arg0) {
		MapScene scene = visualizer.getScene();		
		if (scene != null){
			Timeline timeLine = scene.getTimeLine();
			boolean paused = false;
			if (timeLine.getStatus() == Status.RUNNING){
				timeLine.pause();
				paused = true;
			}
			WritableImage image = scene.getSnap();
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Specify a PNG file");
			FileChooser.ExtensionFilter filter = new ExtensionFilter("Just PNG files", "png");
			fileChooser.getExtensionFilters().add(filter);
			Stage stage = visualizer.getStage();
			File file = fileChooser.showSaveDialog(stage);
			if (file != null){
				BufferedImage bim = SwingFXUtils.fromFXImage(image, null);
				boolean success = false;
				try {
					success = ImageIO.write(bim, "png", file);															
				} catch (IOException ex) {}
				if (success){
					Dialog.show(Dialog.Type.SUCCESS, "The snapshot has been saved to", 
							file.getAbsoluteFile().toString());
				} else {
					Dialog.show(Dialog.Type.ERROR, "The snapshot could not be saved.");
				}
			}
			if (paused){
				timeLine.play();
			}
		}
	}
}