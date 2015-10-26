package cz.filipekt.jdcv.gui_logic;

import java.io.File;
import java.io.IOException;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Listener for the {@link Event} that the user clicks a button that allows to choose a
 * file from the local filesystem. Shows the standard {@link FileChooser} and records the
 * selected file into a {@link TextField} specified by {@link FileChooserButton#field}. 
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class FileChooserButton implements EventHandler<Event>{

	/**
	 * The main {@link Stage} of the application
	 */
	private final Stage stage;
	
	/**
	 * After the file selection process is completed, the absolute path to the
	 * selected file is put inside this {@link TextField}.
	 */
	private final TextField field;
	
	/**
	 * Directory containing the file selected the last time, 
	 * in any instance of this class
	 */
	private static File lastDirectory = new File(".");
	
	/**
	 * The title of the file choosing window
	 */
	private final String windowTitle;
	
	/**
	 * @param stage The main {@link Stage} of the application
	 * @param field After the file selection process is completed, the absolute path to the
	 * selected file is put inside this {@link TextField}.
	 * @param windowTitle The title of the file choosing window
	 */
	public FileChooserButton(Stage stage, TextField field, String windowTitle) {
		this.stage = stage;
		this.field = field;
		this.windowTitle = windowTitle;
	}

	/**
	 * When the button, to which this handler is appointed, is clicked, this method is run.
	 * Shows the standard JavaFX {@link FileChooser} allowing for selection of a single file. 
	 */
	@Override
	public void handle(Event arg0) {
		FileChooser fileChooser = new FileChooser();
		try {
			fileChooser.setInitialDirectory(FileChooserButton.lastDirectory.getCanonicalFile());
		} catch (IOException e) {}
		fileChooser.setTitle(windowTitle);
		File res = fileChooser.showOpenDialog(stage);
		if (res != null){
			field.setText(res.toPath().toAbsolutePath().toString());
			File dir = res.getAbsoluteFile().getParentFile();
			FileChooserButton.lastDirectory = dir.getAbsoluteFile();
		}
		
	}
	
}