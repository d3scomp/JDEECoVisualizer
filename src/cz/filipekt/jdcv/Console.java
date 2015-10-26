package cz.filipekt.jdcv;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import cz.filipekt.jdcv.prefs.GlobalPrefs;
import cz.filipekt.jdcv.prefs.LinkPrefs;
import cz.filipekt.jdcv.prefs.MembershipPrefs;
import cz.filipekt.jdcv.prefs.NodePrefs;
import cz.filipekt.jdcv.util.CharsetNames;
import cz.filipekt.jdcv.util.Dialog;
import cz.filipekt.jdcv.util.GUIUtils;
import cz.filipekt.jdcv.util.Dialog.Type;
import cz.filipekt.jdcv.util.Resources;

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
	 * The amount of horizontal space between the buttons
	 */
	private final double buttonsSpacing = 10;
	
	/**
	 * Width and height of the icons placed inside the control buttons
	 */
	private final int buttonIconSize = 20;
	
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
		try (InputStream initJS = Resources.getResourceInputStream(initScript)){			
			Reader initJsReader = new InputStreamReader(initJS, Charset.forName(initScriptEncoding));
			if (engine != null){
				engine.eval(initJsReader);
			}
		} catch (ScriptException e) {
			System.out.println("Engine environment hasn't been properly initialized.");
			System.out.println(e.getMessage());
		} catch (IOException ex) {}
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
	 * Returns the maximal values from those given as parameters
	 * @param values Values where the maximum will be sought 
	 * @return Maximal value from those given as parameters
	 */
	private final double max(double... values){
		double max = Double.MIN_VALUE;
		for (double val : values){
			if (val > max){
				max = val;
			}
		}
		return max;
	}
	
	/**
	 * The image placed inside the "import script" button
	 */
	private final ImageView importImage = Resources.getImageView("import.png", buttonIconSize);
	
	/**
	 * The text inside the "import script" button
	 */
	private final String importText = "Import File";
	
	/**
	 * The image placed inside the "run" button
	 */
	private final ImageView runImage = Resources.getImageView("run.png", buttonIconSize);
	
	/**
	 * The text inside the "run" button
	 */
	private final String runText = "Run!";
	
	/**
	 * The image placed inside the "clear" button
	 */
	private final ImageView clearImage = Resources.getImageView("clear.png", buttonIconSize);
	
	/**
	 * The text inside the "clear" button
	 */
	private final String clearText = "Clear";
	
	/**
	 * The image placed inside the "help" button
	 */
	private final ImageView helpImage = Resources.getImageView("help.png", buttonIconSize);
	
	/**
	 * The text inside the "help" button
	 */
	private final String helpText = "Help";
	
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
		importButton.setText(importText);
		importButton.setOnAction(new ImportButtonHandler(Charset.defaultCharset().name(), stage, inputArea));
		double importWidth = GUIUtils.getIdealWidth(importButton);
		Button runButton = new Button();
		runButton.setGraphic(runImage);
		runButton.setText(runText);
		double runWidth = GUIUtils.getIdealWidth(runButton);
		MenuItem clearInputItem = new MenuItem("Clear Input");
		MenuItem clearOutputItem = new MenuItem("Clear Output");
		SplitMenuButton clearButton = new SplitMenuButton(clearInputItem, clearOutputItem);
		clearButton.setGraphic(clearImage);
		clearButton.setText(clearText);
		clearButton.setAlignment(Pos.CENTER);
		double clearWidth = GUIUtils.getIdealWidth(clearButton);
		Button helpButton = new Button();
		helpButton.setGraphic(helpImage);
		helpButton.setText(helpText);
		helpButton.setOnAction(new HelpButtonHandler());
		double helpWidth = GUIUtils.getIdealWidth(helpButton);
		List<Control> buttons = Arrays.<Control>asList(importButton, runButton, clearButton, helpButton);
		double buttonWidth = max(importWidth, runWidth, clearWidth, helpWidth);
		for (Control button : buttons){
			button.setPrefWidth(buttonWidth);
		}
		buttonsBox.getChildren().addAll(buttons);
		buttonsBox.setAlignment(Pos.TOP_CENTER);
		buttonsBox.setSpacing(buttonsSpacing);
		runButton.setOnMouseClicked(new RunButtonHandler(visualizer, inputArea, outputArea));
		clearButton.setOnAction(new ClearButtonHandler(outputArea));
		clearInputItem.setOnAction(new ClearButtonHandler(inputArea));
		clearOutputItem.setOnAction(new ClearButtonHandler(outputArea));	
	}
	
	/**
	 * Marks whether a console window is now open
	 */
	private boolean isOpen = false;
	
	/**
	 * Opens a console providing the user with the option to change some of 
	 * the settings and parameters of the visualization with an ECMA script.
	 * @param visualizer The parent application
	 */
	public void showScriptingConsole(Visualizer visualizer){
		if (isOpen){
			return;
		}
		if (visualizer == null){
			return;
		}
		Stage stage = new Stage();
		VBox pane = new VBox();
		pane.setFillWidth(true);
		pane.setPrefWidth(800);
		pane.setPrefHeight(600);
		Scene scene = new Scene(pane);
		HBox buttonsBox = new HBox();
		Label inputLabel = new Label("INPUT:");
		TextArea inputArea = new TextArea();
		ScrollPane inputAreaPane = new ScrollPane();
		inputAreaPane.setContent(inputArea);
		inputAreaPane.setFitToWidth(true);
		inputAreaPane.setFitToHeight(true);
		Label outputLabel = new Label("OUTPUT:");
		TextArea outputArea = new TextArea();
		ScrollPane outputAreaPane = new ScrollPane();
		outputAreaPane.setContent(outputArea);
		outputAreaPane.setFitToWidth(true);
		outputAreaPane.setFitToHeight(true);
		outputArea.setText(writer.toString());
		outputArea.setEditable(false);
		fillButtonsBox(buttonsBox, inputArea, outputArea, visualizer, stage);
		VBox.setVgrow(buttonsBox, Priority.NEVER);
		VBox.setVgrow(inputLabel, Priority.NEVER);
		VBox.setVgrow(inputAreaPane, Priority.ALWAYS);
		VBox.setVgrow(outputLabel, Priority.NEVER);
		VBox.setVgrow(outputAreaPane, Priority.ALWAYS);
		pane.getChildren().addAll(buttonsBox, inputLabel, inputAreaPane, outputLabel, outputAreaPane);
		stage.setScene(scene);		
		decorateConsoleWindow(stage);
		clearWriterBuffer();
		stage.setOnHidden(new EventHandler<WindowEvent>() {
			
			@Override
			public void handle(WindowEvent arg0) {
				isOpen = false;
			}
		});
		isOpen = true;
		stage.show();
	}
	
	/**
	 * Decorates the scripting console window
	 * @param stage The main {@link Stage} of the scripting console
	 */
	private void decorateConsoleWindow(Stage stage){
		if (stage != null){
			stage.initModality(Modality.NONE);
			stage.initStyle(StageStyle.DECORATED);
			stage.sizeToScene();
			stage.setTitle("Scripting Console");
			try (InputStream consoleIconStream = Resources.getResourceInputStream("console.png")){
				Image consoleIcon = new Image(consoleIconStream);
				stage.getIcons().add(consoleIcon);
			} catch (IOException ex) {}
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
		 * Contents of the HTML help file
		 */
		private final String helpFileContents;
		
		/**
		 * Encoding of the help html file
		 */
		private final Charset charset = Charset.forName("UTF-8");
		
		/**
		 * The icon for the help window
		 */
		private final Image windowIcon;
		
		/**
		 * Title of the help window
		 */
		private final String windowTitle = "Manual Pages";
		
		/**
		 * The integrated browser used for viewing the help file
		 */
		private final WebView browser = new WebView();
		
		/**
		 * Loads the help file contents. Loads the help window icon.
		 */
		public HelpButtonHandler() {
			String helpFileContents;						
			try (InputStream helpStream = Resources.getResourceInputStream(helpFileName)){
				helpFileContents = readTextFrom(helpStream);
			} catch (IOException ex) {
				helpFileContents = null;
			}
			this.helpFileContents = helpFileContents;
			Image windowIcon;
			try (InputStream iconStream = Resources.getResourceInputStream("help.png")){
				windowIcon = new Image(iconStream);
			} catch (IOException ex) {
				windowIcon = null;
			}
			this.windowIcon = windowIcon;
			if (helpFileContents != null){
				WebEngine engine = browser.getEngine();
				engine.loadContent(helpFileContents);
			}
		}
		
		/**
		 * Reads the whole input stream and returns its contents as a text encoded in 
		 * {@link HelpButtonHandler#charset}
		 * @param input The input stream to be read
		 * @return Contents of the given input stream encoded as text
		 * @throws IOException When an error occured reading from the input stream
		 */
		private String readTextFrom(InputStream input) throws IOException{
			int bufferSize = 4096;
			byte[] buffer = new byte[bufferSize];
			int count;
			StringBuilder sb = new StringBuilder();
			while ((count=input.read(buffer)) > 0){
				String value = new String(buffer, 0, count, charset);				
				sb.append(value);
			}
			return sb.toString();
		}

		/**
		 * Makes sure that when clicked, the platform default browser is 
		 * started and it shows the manual page.
		 */
		@Override
		public void handle(ActionEvent arg0) {
			if (helpFileContents == null){
				Dialog.show(Type.ERROR, "Help file contents could not be loaded.");
			} else {
				Stage stage = new Stage();
				StackPane pane = new StackPane();
				pane.getChildren().addAll(browser);
				Scene scene = new Scene(pane);
				stage.setScene(scene);
				if (windowIcon != null){
					stage.getIcons().add(windowIcon);
				}
				stage.setTitle(windowTitle);
				stage.initStyle(StageStyle.DECORATED);
				stage.show();
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
			Map<String,NodePrefs> nodePrefs;
			Map<String,LinkPrefs> linkPrefs;
			Set<MembershipPrefs> membershipPrefs;
			GlobalPrefs generalPrefs = new GlobalPrefs(scene, writer);
			if (scene == null){
				nodePrefs = new HashMap<>();
				linkPrefs = new HashMap<>();
				membershipPrefs = new HashSet<>();
			} else {
				nodePrefs = scene.getPreferences().nodePrefs(Console.getInstance().getWriter());
				linkPrefs = scene.getPreferences().linkPrefs(Console.getInstance().getWriter());
				membershipPrefs = scene.getPreferences().membershipPrefs(Console.getInstance().getWriter());
			}
			engine.put("out", System.out);
			engine.put("nodes", nodePrefs);
			engine.put("links", linkPrefs);
			engine.put("memberships", membershipPrefs);
			engine.put("general", generalPrefs);
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
