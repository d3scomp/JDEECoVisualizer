package cz.filipekt.jdcv;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import cz.filipekt.jdcv.prefs.LinkPrefs;
import cz.filipekt.jdcv.prefs.MembershipPrefs;
import cz.filipekt.jdcv.util.CharsetNames;
import cz.filipekt.jdcv.util.Dialog;
import cz.filipekt.jdcv.util.Resources;
import cz.filipekt.jdcv.util.Dialog.Type;

/**
 * A console providing the user with the option to change some of the
 * settings and visualization parameters with an ECMA script.
 * This is a singleton class. To retrieve the only instance, call
 * {@link Console#getInstance()}.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>, in Bad Hofgastein 2015
 * 
 */
public class Console {
	
	/**
	 * Width (in pixels) of the buttons provided in the console window
	 */
	private final double buttonWidth = 130;
	
	/**
	 * The amount of horizontal space between the buttons
	 */
	private final double buttonsSpacing = 10;
	
	/**
	 * Width and height of the icons placed inside the control buttons
	 */
	private final double buttonIconSize = 20.0;
	
	/**
	 * The standard output of the JavaScript code is redirected here. 
	 */
	private final StringWriter writer = new StringWriter();
	
	/**
	 * @return The writer that consumes the standard output of the scripts
	 * @see {@link Console#writer}
	 */
	public Writer getWriter(){
		return writer;
	}
	
	/**
	 * Clears the underlying {@link StringBuffer} of the {@link Console#writer}
	 */
	private void clearWriterBuffer(){
		if (writer != null){
			writer.getBuffer().setLength(0);
		}
	}
	
	/**
	 * This engine is used to run the ECMA script code entered by the user.
	 */
	private final ScriptEngine engine;
	
	/**
	 * The only instance of this singleton class.
	 * @see {@link Console#getInstance()}
	 */
	private static final Console INSTANCE = new Console();
	
	/**
	 * @return The only instance of this singleton class.
	 * @see {@link Console#INSTANCE}
	 */
	public static Console getInstance(){
		return INSTANCE;
	}
	
	/**
	 * This is a singleton class. 
	 * The only instance can be retrieved by {@link Console#getInstance()}.
	 */
	private Console(){
		ScriptEngineManager manager = new ScriptEngineManager();
		engine = manager.getEngineByName("ecmascript");
		engine.getContext().setWriter(writer);
		initializeEngineEnvironment();
	}
	
	/**
	 * Name of the resource file that contains a script that is run
	 * before the console is ready for user input.
	 */
	private final String initScript = "init.js";
	
	/**
	 * Text encoding of the script file specified by {@link Console#initScript}
	 */
	private final String initScriptEncoding = "UTF-8";
	
	/**
	 * Runs the initialization scripts in the scripting environment
	 */
	private void initializeEngineEnvironment(){
		try {
			InputStream initJS = Resources.getResourceInputStream(initScript);
			if (initJS != null){
				Reader initJsReader = new InputStreamReader(initJS, Charset.forName(initScriptEncoding));
				if (engine != null){
					engine.eval(initJsReader);
				}
			}
		} catch (ScriptException e) {
			System.out.println("Engine environment hasn't been properly initialized.");
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Handler for the event that the user clicks one of the "import" buttons 
	 * in the scripting console window. It shows a file chooser window and
	 * loads the selected file in the text encoding as specified by one of
	 * the constructor parameters.
	 */
	private static class ImportButtonHandler implements EventHandler<ActionEvent>{
		
		/**
		 * The encoding used to read the script file
		 */
		private final Charset encoding;
		
		/**
		 * The main stage of the application
		 */
		private final Stage stage;
		
		/**
		 * The imported script is put inside this {@link TextArea} 
		 */
		private final TextArea inputArea;

		/**
		 * @param encoding The encoding used to read the script file
		 * @param stage The main stage of the application
		 * @param inputArea The imported script is put inside this {@link TextArea}
		 */
		public ImportButtonHandler(String encoding, Stage stage,
				TextArea inputArea) {
			this.encoding = Charset.forName(encoding);
			this.stage = stage;
			this.inputArea = inputArea;
		}

		/**
		 * Shows a file chooser window and loads the selected file in the text 
		 * encoding as specified by {@link ImportButtonHandler#encoding}.
		 */
		@Override
		public void handle(ActionEvent arg0) {
			FileChooser chooser = new FileChooser();
			chooser.setTitle("Select a Script File");
			File file = chooser.showOpenDialog(stage);
			if (file != null){
				try {
					byte[] bytes = Files.readAllBytes(file.toPath());
					String script = new String(bytes, encoding);
					inputArea.appendText(script);
				} catch (IOException e) {
					Dialog.show(Type.ERROR, "The file " + file.getAbsolutePath() + 
							" could not be imported.");
				}
			}
		}
		
	}
	
	/**
	 * Fills the "buttonsBox" with the buttons that provide options to import a script
	 * file, run the script, delete the input/output area, view help file.
	 * @param buttonsBox The box to be filled with buttons
	 * @param inputArea The area where the user writes the scripts
	 * @param outputArea The area where the script output is viewed
	 * @param visualizer The parent application
	 * @param stage The main stage of the console window
	 */
	private void fillButtonsBox(HBox buttonsBox, TextArea inputArea, TextArea outputArea, 
			Visualizer visualizer, Stage stage){
		ImageView importImage = Resources.getImageView("import.png", buttonIconSize);
		Set<MenuItem> importButtons = new HashSet<>();
		for (String encoding : CharsetNames.get()){
			MenuItem item = new MenuItem();
			item.setText(encoding);
			item.setOnAction(new ImportButtonHandler(encoding, stage, inputArea));
			importButtons.add(item);
		}
		SplitMenuButton importButton = new SplitMenuButton();
		importButton.getItems().addAll(importButtons);
		importButton.setGraphic(importImage);
		importButton.setText("Import File");
		importButton.setOnAction(new ImportButtonHandler(Charset.defaultCharset().name(), stage, inputArea));
		ImageView runImage = Resources.getImageView("run.png", buttonIconSize);
		Button runButton = new Button("Run!", runImage);
		ImageView clearImage = Resources.getImageView("clear.png", buttonIconSize);
		MenuItem clearInputItem = new MenuItem("Clear Input");
		MenuItem clearOutputItem = new MenuItem("Clear Output");
		SplitMenuButton clearButton = new SplitMenuButton(clearInputItem, clearOutputItem);
		clearButton.setGraphic(clearImage);
		clearButton.setText("Clear Input");
		ImageView helpImage = Resources.getImageView("help.png", buttonIconSize);
		Button helpButton = new Button("Help", helpImage);
		helpButton.setOnAction(new HelpButtonHandler());
		List<Control> buttons = Arrays.<Control>asList(importButton, runButton, clearButton, helpButton);
		for (Control button : buttons){
			button.setPrefWidth(buttonWidth);
		}
		buttonsBox.getChildren().addAll(buttons);
		buttonsBox.setAlignment(Pos.TOP_CENTER);
		buttonsBox.setSpacing(buttonsSpacing);
		runButton.setOnMouseClicked(new RunButtonHandler(visualizer, inputArea, outputArea));
		clearButton.setOnAction(new ClearButtonHandler(inputArea));
		clearInputItem.setOnAction(new ClearButtonHandler(inputArea));
		clearOutputItem.setOnAction(new ClearButtonHandler(outputArea));	
	}
	
	/**
	 * Opens a console providing the user with the option to change some of 
	 * the settings and parameters of the visualization with an ECMA script.
	 * @param visualizer The parent application
	 */
	public void showScriptingConsole(final Visualizer visualizer){
		if (visualizer == null){
			return;
		}
		Stage stage = new Stage();
		VBox pane = new VBox();
		Scene scene = new Scene(pane);
		HBox buttonsBox = new HBox();
		Label inputLabel = new Label("INPUT:");
		TextArea inputArea = new TextArea();
		ScrollPane inputAreaPane = new ScrollPane(inputArea);
		inputAreaPane.setFitToWidth(true);
		Label outputLabel = new Label("OUTPUT:");
		TextArea outputArea = new TextArea();
		ScrollPane outputAreaPane = new ScrollPane(outputArea);
		outputAreaPane.setFitToWidth(true);
		outputArea.setText(writer.toString());
		outputArea.setEditable(false);
		fillButtonsBox(buttonsBox, inputArea, outputArea, visualizer, stage);
		pane.getChildren().addAll(buttonsBox, inputLabel, inputAreaPane, outputLabel, outputAreaPane);
		stage.setScene(scene);
		stage.initModality(Modality.NONE);
		decorateConsoleWindow(stage);
		clearWriterBuffer();
		stage.show();
	}
	
	/**
	 * Decorates the scripting console window
	 * @param stage The main {@link Stage} of the scripting console
	 */
	private void decorateConsoleWindow(Stage stage){
		stage.initStyle(StageStyle.DECORATED);
		stage.sizeToScene();
		stage.setTitle("Scripting Console");
		InputStream consoleIconStream = Resources.getResourceInputStream("console.png");
		if (consoleIconStream != null){
			Image consoleIcon = new Image(consoleIconStream);
			stage.getIcons().add(consoleIcon);
		}
	}
	
	/**
	 * Handler for the event that the user clicks the "help" button in
	 * the scripting console window. Makes sure that when clicked, the 
	 * platform default browser is started and it shows the manual page.
	 */
	private static class HelpButtonHandler implements EventHandler<ActionEvent>{
		
		/**
		 * Name of the resource containing the manual page.
		 */
		private final String helpFileName = "scriptingHelp.html";
		
		/**
		 * The temporary file that the manual page has been copied to.
		 * This file is created so that the manual page is contained in a regular file
		 * present in the local filesystem - while the resources bundled with the
		 * applications are not, as they are packed inside a jar file. 
		 */
		private final File tempHelpFile;
		
		/**
		 * Creates the new temporary file to be stored at {@link HelpButtonHandler#tempHelpFile}
		 * @return A reference to the newly created temporary file
		 * @see {@link HelpButtonHandler#tempHelpFile}
		 */
		private File initializeHelpFile() {
			try {
				File temp = File.createTempFile("ManPage", ".html");
				InputStream fromStream = Resources.getResourceInputStream(helpFileName);
				OutputStream toStream = new FileOutputStream(temp);
				try {
					copy(fromStream, toStream);
				} finally {
					if (fromStream != null){
						fromStream.close();
					}
					if (toStream != null){
						toStream.close();
					}
				}
				temp.deleteOnExit();
				return temp;
			} catch (IOException ex){
				return null;
			}
		}
		
		/**
		 * Copies all the bytes from the source input stream to the target output stream
		 * using a 4kB buffer.
		 * @param fromStream The source input stream
		 * @param toStream The target output stream
		 * @throws IOException Thrown when the copying went wrong
		 */
		private void copy(InputStream fromStream, OutputStream toStream) throws IOException{
			int bufferSize = 4096;
			byte[] buffer = new byte[bufferSize];
			int count;
			while ((count=fromStream.read(buffer)) > 0){
				toStream.write(buffer, 0, count);
			}
		}
		
		/**
		 * Just initializes the {@link HelpButtonHandler#tempHelpFile}
		 */
		public HelpButtonHandler() {
			tempHelpFile = initializeHelpFile();
		}
		
		/**
		 * When thrown, it means that the platform default web browser could not
		 * be found or started.
		 */
		@SuppressWarnings("serial") 
		private static class BrowserNotAvailableException extends Exception {}
		

		/**
		 * Makes sure that when clicked, the platform default browser is 
		 * started and it shows the manual page.
		 */
		@Override
		public void handle(ActionEvent arg0) {
			try {
				if ((tempHelpFile != null) && (tempHelpFile.exists())){
					if (!Desktop.isDesktopSupported()){
						throw new BrowserNotAvailableException();
					}
					Desktop desktop = Desktop.getDesktop();
					if ((desktop == null) || (!desktop.isSupported(Action.BROWSE))){
						throw new BrowserNotAvailableException();
					}
					desktop.browse(tempHelpFile.toURI());	
				}
			} catch (IOException | BrowserNotAvailableException ex){
				Dialog.show(Type.ERROR, "Browser could not be started.", 
						"You can find the manual pages on the GitHub repository of this application.");
			}
		}
	}
	
	/**
	 * Handler for the event that the user clicks the "run" button in the
	 * scripting console window. Makes sure that the objects representing
	 * the elements of the visualization are properly imported and the
	 * script is executed.
	 */
	private class RunButtonHandler implements EventHandler<Event>{
		
		/**
		 * The parent application
		 */
		private final Visualizer visualizer;
		
		/**
		 * The users write the scripts into this area.
		 */
		private final TextArea inputArea;
		
		/**
		 * The output from the scripts is shown here
		 */
		private final TextArea outputArea;

		/**
		 * @param visualizer The parent application
		 * @param inputArea The users write the scripts into this area.
		 * @param outputArea The output from the scripts is shown here
		 */
		public RunButtonHandler(Visualizer visualizer, TextArea inputArea, TextArea outputArea) {
			this.visualizer = visualizer;
			this.inputArea = inputArea;
			this.outputArea = outputArea;
		}

		/**
		 * Makes sure that the objects representing the elements of the 
		 * visualization are properly imported and the script is executed.
		 */
		@Override
		public void handle(Event arg0) {
			MapScene scene = visualizer.getScene();
			Map<String,LinkPrefs> linkPrefs;
			Set<MembershipPrefs> membershipPrefs;
			if (scene == null){
				linkPrefs = new HashMap<>();
				membershipPrefs = new HashSet<>();
			} else {
				linkPrefs = scene.getLinkPrefs();
				membershipPrefs = scene.getMembershipPrefs();
			}
			engine.put("links", linkPrefs);
			engine.put("memberships", membershipPrefs);
			try {
				engine.eval(inputArea.getText());
			} catch (ScriptException e) {
				writer.append("Exception occured:\n");
				writer.append(e.getLocalizedMessage() + "\n");
			} finally {
				String output = writer.toString();
				clearWriterBuffer();
				outputArea.appendText(output);
			}
		}
		
	}
	
	/**
	 * Handler for the event that the user clicks one of the "clear" buttons 
	 * in the scripting console window. Makes sure that the input/output area,
	 * as specified by the constructor parameter, is cleared.
	 */
	private static class ClearButtonHandler implements EventHandler<ActionEvent>{
		
		/**
		 * The content of this {@link TextArea} will be cleared. 
		 */
		private final TextArea textArea;

		/**
		 * @param textArea The content of this {@link TextArea} will be cleared.
		 */
		public ClearButtonHandler(TextArea textArea) {
			this.textArea = textArea;
		}

		/**
		 * Makes sure that the input/output area, as specified by 
		 * {@link ClearButtonHandler#textArea}, is cleared.
		 */
		@Override
		public void handle(ActionEvent arg0) {
			textArea.setText("");
		}
	}
	
}
