package cz.filipekt.jdcv;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.CountDownLatch;

import cz.cuni.mff.d3s.jdeeco.visualizer.extensions.VisualizerPlugin;
import cz.filipekt.jdcv.gui_logic.BackgroundColorHandler;
import cz.filipekt.jdcv.gui_logic.CloseSceneHandler;
import cz.filipekt.jdcv.gui_logic.ConfigFileLoader;
import cz.filipekt.jdcv.gui_logic.ControlsBarItemHandler;
import cz.filipekt.jdcv.gui_logic.FileChooserButton;
import cz.filipekt.jdcv.gui_logic.GraphicsPanelHandler;
import cz.filipekt.jdcv.gui_logic.ImportSceneHandler;
import cz.filipekt.jdcv.gui_logic.PlayButtonHandler;
import cz.filipekt.jdcv.gui_logic.PluginsPanelHandler;
import cz.filipekt.jdcv.gui_logic.RecordingHandler;
import cz.filipekt.jdcv.gui_logic.ScreenShotHandler;
import cz.filipekt.jdcv.gui_logic.ShowNodesHandler;
import cz.filipekt.jdcv.gui_logic.StopButtonAction;
import cz.filipekt.jdcv.gui_logic.TimeLineRateChanged;
import cz.filipekt.jdcv.gui_logic.TimeLineRateListener;
import cz.filipekt.jdcv.gui_logic.TimeLineStatusHandler;
import cz.filipekt.jdcv.gui_logic.ZoomingHandler;
import cz.filipekt.jdcv.measuring.MeasureInputProcessing;
import cz.filipekt.jdcv.plugins.InfoPanel;
import cz.filipekt.jdcv.plugins.Plugin;
import cz.filipekt.jdcv.plugins.PluginWithPreferences;
import cz.filipekt.jdcv.prefs.GlobalPrefs;
import cz.filipekt.jdcv.util.CharsetNames;
import cz.filipekt.jdcv.util.GUIUtils;
import cz.filipekt.jdcv.util.Resources;
import javafx.animation.Animation.Status;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Main class of the application. Run it to show the visualized simulation data.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class Visualizer extends Application {
	
	/**
	 * Preferred width of the map view, in pixels.
	 */
	private final double mapWidth = 1000.0;

	/**
	 * Preferred height of the map view, in pixels.
	 */
	private final double mapHeight = 800.0;
	
	/**
	 * Well prepared simulation data and settings, i.e. this is what will be visualized
	 */
	private MapScene scene;
	
	/**
	 * This method is used to specify what will be visualized. The {@link MapScene} instance
	 * given in the parameter is either null, which means that the user wants to close a
	 * scene, or non-null. In that case, it contains a well prepared simulation data and 
	 * settings. Further, this method takes care of some basic GUI operations associated 
	 * with the above actions - such as making the relevant GUI parts accessible or 
	 * setting up the slider at the bottom. 
	 * @param newScene Well prepared simulation data and settings
	 * @param matsimEventsPresent Marks whether we are visualizing any MATSIM events, 
	 * i.e. moving cars/persons. If false, the visualization only shows the map.
	 */
	public void setScene(MapScene newScene, boolean matsimEventsPresent) {
		if (newScene == null){
			showNoMap();
			controlsBar.setDisable(true);
			graphicsColumn.setDisable(true);
			switchablePanel.setDisable(true);
			timelineSlider.setDisable(true);
			if ((scene != null) && (scene.getTimeLine() != null)){
				scene.getTimeLine().stop();
				if (timelineToSliderListener != null){
					scene.getTimeLine().currentTimeProperty().removeListener(timelineToSliderListener);
					timelineToSliderListener = null;
				}
				if (sliderToTimelineListener != null){
					timelineSlider.valueProperty().removeListener(sliderToTimelineListener);
					sliderToTimelineListener = null;
				}
			}
		} else {
			setNontrivialScene(newScene, matsimEventsPresent);
		}
		this.scene = newScene;
		providePreferencesToPlugins();
	}
	
	/**
	 * When a non-null scene is handed to {@link Visualizer#setScene(MapScene, boolean)}, 
	 * this method takes care of loading the scene.
	 * @see {@link Visualizer#setScene(MapScene, boolean)}
	 * @param newScene Well prepared simulation data and settings
	 * @param matsimEventsPresent Marks whether we are visualizing any MATSIM events, 
	 * i.e. moving cars/persons. If false, the visualization only shows the map.
	 */
	private void setNontrivialScene(final MapScene newScene, boolean matsimEventsPresent){
		ScrollPane mapScrollPane = newScene.getMapPane();
		mapScrollPane.setPrefHeight(mapHeight);
		mapScrollPane.setPrefWidth(mapWidth);
		mapPane.getChildren().clear();
		mapPane.getChildren().add(mapScrollPane);											
		graphicsColumn.setDisable(false);
		switchablePanel.setDisable(false);
		if (matsimEventsPresent){
			controlsBar.setDisable(false);
			timelineSlider.setDisable(false);
			setSliderParameters(newScene.getMinTime(), newScene.getMaxTime());
			timelineToSliderListener = new ChangeListener<Duration>() {

				@Override
				public void changed(ObservableValue<? extends Duration> arg0,
						Duration oldValue, Duration newValue) {
					double millis = newValue.toMillis();
					timelineSlider.setValue(newScene.convertToSimulationTime(millis));
				}
			};
			newScene.getTimeLine().currentTimeProperty().addListener(timelineToSliderListener);
			sliderToTimelineListener = new ChangeListener<Number>() {

				@Override
				public void changed(ObservableValue<? extends Number> arg0,
						Number oldValue, Number newValue) {
					Duration time = new Duration(
							newScene.convertToVisualizationTime(newValue.doubleValue()));
					newScene.getTimeLine().jumpTo(time);
				}
			};
			timelineSlider.valueProperty().addListener(sliderToTimelineListener);
		}
	}
	
	/**
	 * Provides the preferences objects to all available plugins that 
	 * extend {@link PluginWithPreferences} 
	 */
	private void providePreferencesToPlugins(){
		for (Plugin plugin : plugins){
			if (plugin instanceof PluginWithPreferences){
				PluginWithPreferences plugin2 = (PluginWithPreferences)plugin;
				if (scene == null){
					plugin2.setPreferences(null);
					plugin2.setGlobalPrefs(null);
				} else {
					plugin2.setPreferences(scene.getPreferences());					
					plugin2.setGlobalPrefs(new GlobalPrefs(scene, null));
				}
			}
		}
	}
	
	/**
	 * Sets the parameters of the slider appearing in the bottom of the application window.
	 * @param min Starting simulation time
	 * @param max Ending simulation time
	 */
	private void setSliderParameters(double min, double max){
		double diff = max - min;
		timelineSlider.setMin(min);
		timelineSlider.setMax(max);
		timelineSlider.setValue(0);
		timelineSlider.setShowTickLabels(true);
		timelineSlider.setShowTickMarks(true);
		timelineSlider.setMajorTickUnit(diff/4);
		timelineSlider.setMinorTickCount(6);
		timelineSlider.setBlockIncrement(diff/10);
	}
	
	/**
	 * Makes sure the changes in visualization timeline current time are projected to
	 * the position of the slider in the main window
	 */
	private ChangeListener<Duration> timelineToSliderListener;
	
	/**
	 * Makes sure that the changes in the value of the slider (in the main window) are
	 * projected to the value of the current time of the visualization timeline
	 */
	private ChangeListener<Number> sliderToTimelineListener;
	
	/**
	 * @return The map that is being visualized, coupled with some view parameters.
	 * @see {@link Visualizer#scene}
	 */
	public MapScene getScene(){
		return scene;
	}

	/**
	 * Main entry point of the JDEECoVisualizer application.
	 * @param args Program arguments. Ignored by the application.
	 */
	public static void main(String[] args){
		launch(args);
	}
	
	/**
	 * Width and height of the play/pause icon shown inside the corresponding button 
	 * in the controls tool bar.
	 */
	private final int playIconSize = 20;
	
	/**
	 * Sets all the controls contained in the graphics columns to to the default values.
	 * The references to those {@link Node} instances that are affected are stored inside 
	 * this implementation, which prevents the need to store the references in separate 
	 * class variables, which would make the class messy.
	 */
	private Runnable graphicsColumnDefaults;
	
	/**
	 * Sets all the controls contained in the graphics columns to to the default values.
	 * The references to those {@link Node} instances that are affected are stored inside 
	 * this implementation, which prevents the need to store the references in separate 
	 * class variables, which would make the class messy.
	 * @see {@link Visualizer#graphicsColumnDefaults}
	 */
	public void setGraphicsColumnDefaults() {
		graphicsColumnDefaults.run();
	}
	
	/**
	 * Handler for the event that the user clicks the "open console" button in the 
	 * application menu. When run, the scripting console is opened in a new window.
	 */
	private final EventHandler<ActionEvent> scriptingWindowButton = new EventHandler<ActionEvent>() {

		@Override
		public void handle(ActionEvent event) {
			Console.getInstance().showScriptingConsole(Visualizer.this);
		}
	};
	
	/**
	 * An item in the "Scenes" menu; when clicked, shows the "import scene" page
	 */
	private final MenuItem importSceneItem = new MenuItem("Import Scene");
	
	/**
	 * An item in the "Scenes" menu; when clicked, closes the current scene
	 */
	private final MenuItem closeThisSceneItem = new MenuItem("Close This Scene");

	/**
	 * Constructs the main menu bar of the application.
	 */
	private void createMenuBar() {
		Menu fileMenu = new Menu("Scenes");
		importSceneItem.setDisable(false);
		closeThisSceneItem.setDisable(true);
		importSceneItem.setOnAction(new ImportSceneHandler(importSceneItem, closeThisSceneItem, this));
		closeThisSceneItem.setOnAction(new CloseSceneHandler(importSceneItem, closeThisSceneItem, this));
		fileMenu.getItems().addAll(importSceneItem, closeThisSceneItem);
		Menu optionsMenu = new Menu("Options");
		MenuItem scriptingWindow = new MenuItem("Open Console");
		scriptingWindow.setOnAction(scriptingWindowButton);
		optionsMenu.getItems().addAll(scriptingWindow);
		Menu viewMenu = new Menu("View");
		CheckMenuItem controlsPanel = new CheckMenuItem("Controls Panel");
		controlsPanel.setSelected(true);
		controlsPanel.setOnAction(new ControlsBarItemHandler(controlsPanel, this));
		CheckMenuItem graphicsPanel = new CheckMenuItem("Graphics Panel");	
		graphicsPanel.setSelected(true);
		graphicsPanel.setOnAction(new GraphicsPanelHandler(graphicsPanel, this));
		CheckMenuItem pluginPanel = new CheckMenuItem("Plugins Panel");
		pluginPanel.setSelected(true);
		pluginPanel.setOnAction(new PluginsPanelHandler(pluginPanel, this));
		viewMenu.getItems().addAll(controlsPanel, graphicsPanel, pluginPanel);
		menuBar.getMenus().addAll(fileMenu, optionsMenu, viewMenu);
	}
	
	/**
	 * The {@link Node} that is shown whenever no map scene is open.
	 */
	private final Pane noMapNode = new VBox();

	/**
	 * Contains the controls where users can specify input XML files for the visualization. 
	 */
	private final GridPane importSceneGrid = new GridPane();

	/**
	 * Container for map view, or if no map is currently view, for a dialog for loading a map.
	 */
	private final Pane mapPane = new StackPane();

	/**
	 * @return Preferred height of the map view, in pixels.
	 * @see {@link Visualizer#mapHeight}
	 */
	double getMapHeight() {
		return mapHeight;
	}
	
	/**
	 * @return Preferred width of the map view, in pixels.
	 * @see {@link Visualizer#mapWidth}
	 */
	public double getMapWidth() {
		return mapWidth;
	}
	
	/**
	 * Shows the "import scene" menu in the central part of the application window
	 * @see {@link Visualizer#importSceneGrid}
	 */
	public void showImportScene(){
		mapPane.getChildren().clear();
		mapPane.getChildren().add(importSceneGrid);
	}
	
	/**
	 * Shows the "empty" panel (which actually contains a few basic instructions) in the
	 * central part of the application window
	 */
	public void showNoMap(){
		mapPane.getChildren().clear();
		mapPane.getChildren().add(noMapNode);
	}
	
	/**
	 * Sets up the drag&drop functionality for the input fields where user defines the input files
	 * @param fields The fields where user defines the input files
	 */
	private void setUpDragNDrop(List<TextField> fields){
		for (final TextField field : fields){
			field.setOnDragOver(new EventHandler<DragEvent>() {
	
				@Override
				public void handle(DragEvent event) {
					event.acceptTransferModes(TransferMode.COPY);
				}
			});
			field.setOnDragDropped(new EventHandler<DragEvent>() {
	
				@Override
				public void handle(DragEvent event) {
					Dragboard dragBoard = event.getDragboard();
					if (dragBoard.hasFiles()){
						for (File file : dragBoard.getFiles()){
							field.setText(file.getAbsolutePath().toString());
						}
						event.setDropCompleted(true);
						event.consume();
					}
				}
			});
		}
	}
	
	/**
	 * This encoding is the default selection in the combo-boxes on the scene-import page.
	 */
	private final String preferredEncoding = "UTF-8";
	
	/**
	 * The text inside of the buttons used for opening a file selection dialog
	 */
	private final String selectButtonText = "Select..";
	
	/**
	 * Text inside the load button, which loads and processes an input config file
	 */
	private final String loadButtonText = "Load!";
	
	/**
	 * The input text fields for entering the paths to the input files.
	 * Located in the "import scene" page.
	 */
	private final List<TextField> fields = new ArrayList<>();
	
	/**
	 * All the combo-boxes used for selecting the text encoding for the input files.
	 * Located in the "import scene" page.
	 */
	private final List<ComboBox<String>> charsets = new ArrayList<>();
	
	/**
	 * Input field specifying at which simulation time should the visualization start.
	 * Located in the "import scene" page.
	 */
	private final TextField startAtField = new TextField();
	
	/**
	 * Input field specifying at which simulation time should the visualization end
	 * Located in the "import scene" page.
	 */
	private final TextField endAtField = new TextField();
	
	/**
	 * The OK button located in the "import scene" page. When fired, the input files
	 * are processed and the visual output is opened.
	 */
	private final Button okButton = new Button("OK");
	
	/**
	 * The input field for specifying the desired duration of the visualization
	 */
	private final TextField durationField = new TextField();
	
	/**
	 * The checkbox for specifying whether only the injected JDEECo
	 * components should be visualized
	 */
	private final CheckBox onlyComponentsBox = new CheckBox();

	/**
	 * The checkbox for specifying whether only the event log expects matsim
	 * events or jDEECo events
	 */
	private final CheckBox matsimLogBox = new CheckBox();

	/**
	 * The checkbox specifying if just the links will be shown upon initialization
	 */
	private final CheckBox showLinksBox = new CheckBox("Show Links");
	
	/**
	 * Path to the acmescripts that can be used to customize the visualization
	 * graphics at startup
	 */
	private String scriptsFilePath;

	public String getScriptsFilePath() {
		return scriptsFilePath;
	}

	public void setScriptsFilePath(String scriptsFilePath) {
		this.scriptsFilePath = scriptsFilePath;
	}

	/**
	 * Prepares and initializes the contents of @link Visualizer#importSceneGrid}.
	 */
	private void createImportSceneGrid(){
		//The preferred width of the combo-boxes for selecting the input file encoding
		double encodingBoxWidth = GUIUtils.getEncodingNameMaxLength() + 60.0;
		//The preferred width of the buttons used for opening a file selection dialog
		double selectButtonWidth = GUIUtils.computeTextLength(selectButtonText) + 40.0;
		//The preferred width of the load button, which loads and processes an input config file
		double loadButtonWidth = GUIUtils.computeTextLength(loadButtonText) + 40.0;
		List<Label> labels = new ArrayList<>();
		labels.add(new Label("Map/network definition:"));
		labels.add(new Label("Event log:"));
		labels.add(new Label("Ensemble event log:"));
		List<Button> chooserButtons = new ArrayList<>();				
		int row = prepareInputFilesControls(importSceneGrid, fields, labels, charsets, 
				chooserButtons, encodingBoxWidth, selectButtonWidth);	
		row = prepareOtherControls(importSceneGrid, row, durationField, onlyComponentsBox, matsimLogBox,
				startAtField, endAtField);
		row += 1;
		String line = "----------";
		Label orLabel = new Label(line + " OR " + line);
		importSceneGrid.add(orLabel, 0, row, 2, 1);
		GridPane.setHalignment(orLabel, HPos.CENTER);
		row += 2;		
		prepareConfigLoaderRow(importSceneGrid, row, fields, charsets, durationField, 
				encodingBoxWidth, selectButtonWidth, loadButtonWidth, onlyComponentsBox,
				startAtField, endAtField, showLinksBox);
		row += 2;
		okButton.setOnAction(new SceneImportHandler(fields, okButton, onlyComponentsBox, matsimLogBox, importSceneGrid,
				Visualizer.this, durationField, timeLineStatus, timeLineRate, startAtField, endAtField, charsets));
		importSceneGrid.add(okButton, 1, row);
		importSceneGrid.setAlignment(Pos.CENTER);
		importSceneGrid.setHgap(importSceneGridHGap);
		importSceneGrid.setVgap(importSceneGridVGap);
	}
	
	/**
	 * Builds and prepares various controls contained in the "import scene" page
	 * @param pane Base container for the controls in the "import scene" page
	 * @param row Number of the row in the pane, to which the controls will be added
	 * @param durationField The input field for specifying the desired duration of the visualization
	 * @param onlyComponentsBox The checkbox for specifying whether only the injected JDEECo
	 * components should be visualized
	 * @param matsimLogBox
	 * @param startAtField Input field specifying at which simulation time should the visualization start
	 * @param endAtField Input field specifying at which simulation time should the visualization end
	 * @return The number of the current row, as the "import scene" page is built one row at a time
	 */
	private int prepareOtherControls(GridPane pane, int row, TextField durationField, 
			CheckBox onlyComponentsBox, CheckBox matsimLogBox, TextField startAtField, TextField endAtField){
		Label matsimLogLabel = new Label("Expect matsim logs:");
		matsimLogBox.setSelected(false);
		matsimLogBox.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				
				CheckBox source = (CheckBox) event.getSource();
				for (TextField f : fields){
					String id = f.getId();
					if ((id != null) && (id.equals("EnsembleEventsTextField"))) {
						f.setDisable(!source.isSelected());
					}
				}
				
			}
		});	
		pane.add(matsimLogLabel, 0, row);
		pane.add(matsimLogBox, 1, row);
		row += 1;		
		Label durationLabel = new Label("Target duration (seconds):");
		pane.add(durationLabel, 0, row);
		pane.add(durationField, 1, row);		
		row += 1;		
		Label onlyComponentsLabel = new Label("Show just JDEECo components:");
		onlyComponentsBox.setSelected(true);
		pane.add(onlyComponentsLabel, 0, row);
		pane.add(onlyComponentsBox, 1, row);
		row += 1;		
		Label startAtLabel = new Label("Start at time (seconds):");
		pane.add(startAtLabel, 0, row);
		pane.add(startAtField, 1, row);
		row += 1;		
		Label endAtLabel = new Label("End at time (seconds):");
		pane.add(endAtLabel, 0, row);
		pane.add(endAtField, 1, row);
		row += 1;
		return row;
	}
	
	/**
	 * Builds and prepares the controls that allow the user to specify the input files.
	 * @param pane Base container for the controls in the "import scene" page
	 * @param fields The input text fields for entering the paths to the input files
	 * @param labels Labels which identify the input fields for input file specification  
	 * @param charsets All the combo-boxes used for selecting the text encoding for the input files
	 * @param chooserButtons Buttons used for opening the file selection dialog
	 * @param encodingBoxWidth The preferred width of the combo-boxes for selecting the input file encoding
	 * @param selectButtonWidth The preferred width of the buttons used for opening a file selection dialog
	 * @return The number of the current row, as the "import scene" page is built one row at a time
	 */
	private int prepareInputFilesControls(GridPane pane, List<TextField> fields, List<Label> labels,
			List<ComboBox<String>> charsets, List<Button> chooserButtons, double encodingBoxWidth,
			double selectButtonWidth){
		for (int i = 0; i < labels.size(); i++){
			TextField f = new TextField();
			if (labels.get(i).getText().equals("Ensemble event log:")) {
				f.setDisable(true);
				f.setId("EnsembleEventsTextField");
			}
			fields.add(f);
			Button chooserButton = new Button(selectButtonText);
			chooserButton.setPrefWidth(selectButtonWidth);
			chooserButtons.add(chooserButton);
			ComboBox<String> charsetBox = new ComboBox<String>();
			charsetBox.getItems().addAll(CharsetNames.get());
			charsetBox.getSelectionModel().select(preferredEncoding);
			charsetBox.setPrefWidth(encodingBoxWidth);
			charsets.add(charsetBox);
		}
		for (TextField field : fields){
			field.setPrefWidth(inputFieldsWidth);
		}
		setUpDragNDrop(fields);
		for (int i = 0; i < chooserButtons.size(); i++){
			Button button = chooserButtons.get(i);
			TextField field = fields.get(i);
			button.setOnMouseClicked(new FileChooserButton(stage, field, "Select XML file"));
		}
		int row = 0;
		for (int i = 0; i < labels.size(); i++){
			Label label = labels.get(i);
			TextField field = fields.get(i);
			ComboBox<String> charsetBox = charsets.get(i);
			Button button = chooserButtons.get(i);
			pane.add(label, 0, row);
			pane.add(field, 1, row);
			pane.add(charsetBox, 2, row);
			pane.add(button, 3, row);
			row += 1;
		}
		return row;
	}
	
	/**
	 * Builds and prepares the controls that deal with loading configuration from an external
	 * file. These controls are located in a single row of the "import scene" page.
	 * @param pane Base container for the controls in the "import scene" page
	 * @param row Number of the row in the pane, to which the controls will be added
	 * @param fields The input text fields for entering the paths to the input files
	 * @param charsets All the combo-boxes used for selecting the text encoding for the input files
	 * @param durationField The input field for specifying the desired duration of the visualization 
	 * @param encodingBoxWidth The preferred width of the combo-boxes for selecting the input file encoding
	 * @param selectButtonWidth The preferred width of the buttons used for opening a file selection dialog
	 * @param loadButtonWidth The preferred width of the load button, 
	 * which loads and processes an input config file
	 * @param onlyComponentsBox The checkbox specifying if just the injected JDEECo agents shall be visualized
	 * @param startAtField The field specifying the where in the event log should the visualization begin
	 * @param endAtField The field specifying the where in the event log should the visualization end
	 * @param showLinks 
	 */
	private void prepareConfigLoaderRow(GridPane pane, int row, List<TextField> fields, 
			List<ComboBox<String>> charsets, TextField durationField, double encodingBoxWidth,
			double selectButtonWidth, double loadButtonWidth, CheckBox onlyComponentsBox,
			TextField startAtField, TextField endAtField, CheckBox showLinksBox){
		Label configFileLabel = new Label("Specify Configuration File:");
		TextField configFileField = new TextField();
		configFileField.setPrefWidth(inputFieldsWidth);
		setUpDragNDrop(Arrays.asList(configFileField));
		ComboBox<String> configFileCharsets = new ComboBox<String>();
		configFileCharsets.getItems().addAll(CharsetNames.get());
		configFileCharsets.getSelectionModel().select(preferredEncoding);
		configFileCharsets.setPrefWidth(encodingBoxWidth);
		Button configFileSelect = new Button("Select..");
		configFileSelect.setPrefWidth(selectButtonWidth);
		configFileSelect.setOnMouseClicked(new FileChooserButton(
				stage, configFileField, "Select Configuration File"));
		Button configFileLoad = new Button(loadButtonText);
		configFileLoad.setPrefWidth(loadButtonWidth);
		ConfigFileLoader configLoader = new ConfigFileLoader(
				configFileField, configFileCharsets, fields, charsets, durationField, onlyComponentsBox,
				startAtField, endAtField, showLinksBox); 
		configFileLoad.setOnAction(configLoader);
		pane.add(configFileLabel, 0, row);
		pane.add(configFileField, 1, row);
		pane.add(configFileCharsets, 2, row);
		pane.add(configFileSelect, 3, row);
		pane.add(configFileLoad, 4, row);		
	}
	
	/**
	 * Called whenever the visualization is started, paused or stopped
	 */
	private ChangeListener<Status> timeLineStatus;
	
	/**
	 * Value for the HGap parameter of {@link Visualizer#importSceneGrid}
	 */
	private final double importSceneGridHGap = 20;
	
	/**
	 * Value for the VGap parameter of {@link Visualizer#importSceneGrid}
	 */
	private final double importSceneGridVGap = 10;
	
	/**
	 * Width of the input fields used in {@link Visualizer#importSceneGrid}
	 */
	private final double inputFieldsWidth = 300;
	
	/**
	 * Difference by which the {@link Timeline#rateProperty()} will be changed when
	 * the fast forward or rewind buttons are clicked.
	 */
	private final double timeLineRateStep = 0.5;
	
	/**
	 * @return Difference by which the {@link Timeline#rateProperty()} will be changed when
	 * the fast forward or rewind buttons are clicked.
	 * @see {@link Visualizer#timeLineRateStep}
	 */
	public double getTimeLineRateStep() {
		return timeLineRateStep;
	}

	/**
	 * Called whenever the visualization is sped up or down
	 */
	private ChangeListener<Number> timeLineRate;
	
	/**
	 * Constructs the tool bar for zooming, pausing, forwarding etc. the simulation visualization.
	 * It is shown at the bottom of the main window.
	 */
	private void createControlsBar() {
		ImageView pauseImage = Resources.getImageView("video-pause.png", playIconSize);
		ImageView playImage = Resources.getImageView("video-play.png", playIconSize);
		Button playButton = new Button();
		final Button stopButton = new Button();
		playButton.setGraphic(playImage);
		playButton.setOnMouseClicked(new PlayButtonHandler(this));
		timeLineStatus = new TimeLineStatusHandler(playButton, stopButton, playImage, pauseImage);
		Label speedLabel = new Label("Speed: 1.0x");
		timeLineRate = new TimeLineRateListener(speedLabel);
		ImageView ffdImage = Resources.getImageView("fast-forward.png", playIconSize);
		Button ffdButton = new Button("Speed up", ffdImage);
		ImageView rwImage = Resources.getImageView("rewind.png", playIconSize);
		Button rwButton = new Button("Speed down", rwImage);
		ffdButton.setOnMouseClicked(new TimeLineRateChanged(true, this));
		rwButton.setOnMouseClicked(new TimeLineRateChanged(false, this));
		ImageView zoomInImage = Resources.getImageView("zoom-in.png", playIconSize);
		Button zoomInButton = new Button("Zoom IN", zoomInImage);
		zoomInButton.setOnMouseClicked(new ZoomingHandler(1.2, this));
		ImageView zoomOutImage = Resources.getImageView("zoom-out.png", playIconSize);
		Button zoomOutButton = new Button("Zoom OUT", zoomOutImage);
		zoomOutButton.setOnMouseClicked(new ZoomingHandler(1/1.2, this));
		ImageView stopImage = Resources.getImageView("stop-black.png", playIconSize);
		stopButton.setDisable(true);
		stopButton.setGraphic(stopImage);
		stopButton.setOnMouseClicked(new StopButtonAction(Visualizer.this, stopButton, recordingHandler));
		controlsBar.getChildren().addAll(speedLabel, rwButton, playButton, stopButton, ffdButton, 
				zoomInButton, zoomOutButton);
		controlsBar.setSpacing(10);
		controlsBar.setAlignment(Pos.CENTER_RIGHT);
	}
	
	/**
	 * The stage used by this application.
	 */
	private Stage stage;
	
	/**
	 * @return The stage used by this application.
	 * @see {@link Visualizer#stage}
	 */
	public Stage getStage() {
		return stage;
	}

	/**
	 * The tool bar containing the various zooming, pausing, forwarding etc. options
	 */
	private final HBox controlsBar;
	
	/**
	 * The tool bar shown on the top of the application, allowing various operations such as
	 * loading a scene, setting the visibility of other tool bars, etc.
	 */
	private final MenuBar menuBar;
	
	/**
	 * @return The tool bar containing the various zooming, pausing, forwarding etc. options
	 * @see {@link Visualizer#controlsBar}
	 */
	public HBox getControlsBar() {
		return controlsBar;
	}

	/**
	 * The root element of the main {@link Scene} of the application.
	 */
	private final VBox rootPane = new VBox();
	
	/**
	 * @return The root element of the main {@link Scene} of the application.
	 * @see {@link Visualizer#rootPane}
	 */
	public VBox getRootPane(){
		return rootPane;
	}
	
	/**
	 * The default color of the map background
	 */
	private static final String defaultBackround = "#f2f2f2";
	
	/**
	 * @return The default color of the map background
	 */
	public static String getDefaultbackround() {
		return defaultBackround;
	}

	/**
	 * The CSS field which sets the background color of a JavaFX Node
	 */
	private static final String backColorCSSField = "-fx-background";
	
	/**
	 * @return The CSS field which sets the background color of a JavaFX Node
	 */
	public static String getBackColorCSSField() {
		return backColorCSSField;
	}
	
	/**
	 * The color-picker used to select the background color of the visual output
	 */
	private Node backgroundColorPicker;

	/**
	 * @return The color-picker used to select the background color of 
	 * the visual output
	 */
	public Node getBackgroundColorPicker() {
		return backgroundColorPicker;
	}

	/**
	 * Constructs the graphics column, shown on the left side of the map, 
	 * containing various graphics options
	 */
	private void createGraphicsColumn() {
		graphicsColumn.setDisable(true);
		graphicsColumn.setAlignment(Pos.TOP_LEFT);
		final CheckBox showNodesBox = new CheckBox("Show nodes");
		showNodesBox.setSelected(true);
		showNodesBox.setOnAction(new ShowNodesHandler(this, showNodesBox));
		graphicsColumn.getChildren().add(showNodesBox);
		showLinksBox.setSelected(true);
		showLinksBox.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent arg0) {
				if (scene != null){
					scene.setLinksVisible(showLinksBox.isSelected());
				}
			}
		});
		graphicsColumn.getChildren().add(showLinksBox);
		Label backColorLabel = new Label("Background color:");
		graphicsColumn.getChildren().add(backColorLabel);
		ColorPicker backColor = new ColorPicker(Color.web(defaultBackround));
		backColor.setOnAction(new BackgroundColorHandler(this, backColor));
		graphicsColumn.getChildren().add(backColor);
		backgroundColorPicker = backColor;
		ImageView snapShotImage = Resources.getImageView("screenshot.png", playIconSize);
		Button screenShotButton = new Button("Snapshot", snapShotImage);
		screenShotButton.setOnMouseClicked(new ScreenShotHandler(this));
		graphicsColumn.getChildren().add(screenShotButton);		
		ImageView recordStartImage = Resources.getImageView("record.png", playIconSize);
		ImageView recordStopImage = Resources.getImageView("stop.png", playIconSize);
		Button recordButton = new Button("Record", recordStartImage);
		recordingHandler = new RecordingHandler(recordButton, recordStartImage, recordStopImage, this); 
		recordButton.setOnMouseClicked(recordingHandler);
		graphicsColumn.getChildren().add(recordButton);
		for (Node node : graphicsColumn.getChildren()){
			VBox.setMargin(node, new Insets(graphicsItemsMargin, 0, graphicsItemsMargin, 
					2 * graphicsItemsMargin));
		}
		VBox.setMargin(backColorLabel, new Insets(graphicsItemsMargin, 0, 0, 2 * graphicsItemsMargin));
		VBox.setMargin(backColor, new Insets(0, 0, graphicsItemsMargin, 2 * graphicsItemsMargin));
		graphicsColumnDefaults = new Runnable() {
			
			@Override
			public void run() {
				showNodesBox.setSelected(true);
				showLinksBox.setSelected(true);
			}
		};
	}
	
	/**
	 * Handler for the event that the user clicks the "start/stop recording" button 
	 */
	private RecordingHandler recordingHandler;
	
	/**
	 * The stripe of the application view that contains the {@link Visualizer#graphicsColumn},
	 * {@link MapScene#mapPane} and others
	 */
	private final HBox middleRow = new HBox();
	
	/**
	 * @return The stripe of the application view that contains the {@link Visualizer#graphicsColumn},
	 * {@link MapScene#mapPane} and others
	 * @see {@link Visualizer#middleRow}
	 */
	public HBox getMiddleRow() {
		return middleRow;
	}

	/**
	 * The column, shown on the left side of the map, containing various graphics options
	 */
	private final VBox graphicsColumn;
	
	/**
	 * @return The column, shown on the left side of the map, containing various graphics options
	 * @see {@link Visualizer#graphicsColumn}
	 */
	public VBox getGraphicsColumn(){
		return graphicsColumn;
	}
	
	/**
	 * Preferred width of the {@link Visualizer#graphicsColumn}
	 */
	private final double graphicsColumnWidth = 200.0;
	
	/**
	 * Margin of the elements inside the {@link Visualizer#graphicsColumn}
	 */
	private final double graphicsItemsMargin = 10;
	
	/**
	 * Shows the current position on a timeline, like any slider in most media players.
	 */
	private final Slider timelineSlider = new Slider();
	
	/**
	 * Parent container for {@link Visualizer#timelineSlider}
	 */
	private final Pane sliderWrapper;
	
	/**
	 * Constructs the slider wrapper, the parent container for {@link Visualizer#timelineSlider}
	 */
	private void createSliderWrapper(){
		sliderWrapper.getChildren().addAll(timelineSlider);
		HBox.setHgrow(timelineSlider, Priority.ALWAYS);
		HBox.setMargin(timelineSlider, new Insets(0, 50, 0, 50));
		sliderWrapper.setPrefHeight(50);
	}
	
	/**
	 * The panel on the right side of the window, allowing various plugins to be viewed.
	 * The panel which has been selected by clicking the corresponding button is viewed.
	 */
	private final VBox switchablePanel;
	
	/**
	 * @return The panel on the right side of the window, allowing various plugins to be viewed.
	 * The panel which has been selected by clicking the corresponding button is viewed.
	 */
	public Region getSwitchablePanel() {
		return switchablePanel;
	}

	/**
	 * Preferred width of the side panel
	 */
	private final double sidePanelWidth = 300.0;
	
	/**
	 * Loads plugins from the plugins directory. In addition, it also loads
	 * the plugin {@link InfoPanel}, which does not have to be loaded from a jar
	 */
	private void loadPlugins(){
		ServiceLoader<Plugin> loader = ServiceLoader.load(Plugin.class);
		for (Plugin plugin : loader){
			plugins.add(plugin);
		}
		plugins.add(InfoPanel.getInstance());
	}
	
	/**
	 * Constructs the panel on the right side of the window, 
	 * allowing various plugins to be viewed. 
	 */
	private void createSwitchablePanel(){	
		loadPlugins();
		final Pane mainPanel = new StackPane();
		ToolBar toolBar = new ToolBar();
		toolBar.setPrefWidth(sidePanelWidth);
		mainPanel.setPrefWidth(sidePanelWidth);
		switchablePanel.getChildren().addAll(toolBar, mainPanel);
		VBox.setVgrow(toolBar, Priority.NEVER);
		VBox.setVgrow(mainPanel, Priority.ALWAYS);
		switchablePanel.setFillWidth(true);
		plugins2.clear();
		for (Plugin plugin : plugins){
			Button button = createPluginButton(plugin);
			plugins2.put(button, plugin.getPanel());
		}
		for (final Button button : plugins2.keySet()){
			button.setStyle("-fx-background-color: white");
			button.setOnAction(new EventHandler<ActionEvent>() {
				
				@Override
				public void handle(ActionEvent arg0) {
					for (Button button : plugins2.keySet()){
						button.setStyle("-fx-background-color: white");
					}
					button.setStyle("-fx-background-color: grey");
					mainPanel.getChildren().clear();
					mainPanel.getChildren().add(plugins2.get(button));
				}
			});			
			toolBar.getItems().add(button);
		}
	}
	
	/**
	 * Width (and height) of the images shown inside the panel-switching buttons
	 */
	private final double pluginButtonImageWidth = 20;
	
	/**
	 * For the given plugin, creates the corresponding button used for switching the plugin on.
	 * @param plugin Plugin to be turned on/off by switching the returned button
	 * @return Button for switching on/off the specified plugin
	 */
	private Button createPluginButton(Plugin plugin){
		ImageView thumbNail;		
		try (InputStream imageStream = plugin.getThumbnail()){
			Image image = new Image(imageStream, pluginButtonImageWidth, pluginButtonImageWidth, false, false);
			thumbNail = new ImageView(image);
		} catch (Exception ex){
			thumbNail = new ImageView();
		}
		Button res = new Button(plugin.getName(), thumbNail);		
		return res;
	}
	
	/**
	 * The plugins available for the application
	 */
	private final Collection<Plugin> plugins = new HashSet<>();
	
	/**
	 * Each button is associated with a plugin (contained in {@link Visualizer#plugins}).
	 * The button is mapped here to the main panel of the associated plugin.
	 */
	private final Map<Button,Node> plugins2 = new HashMap<>();
	
	/**
	 * Selects the info-panel in the panel switching menu on the right side of the 
	 * application window
	 */
	private void selectInfoPanel(){
		for (Button button : plugins2.keySet()){
			if (plugins2.get(button) == InfoPanel.getInstance().getPanel()){
				button.fire();
				break;
			}
		}
	}
	
	/**
	 * Builds the GUI, should only be called by the JavaFX runtime.
	 */
	@Override
	public void start(Stage stage) throws IOException {	
		this.stage = stage;
		createImportSceneGrid();
		selectInfoPanel();
		timelineSlider.setDisable(true);
		noMapNode.setPrefSize(mapWidth, mapHeight);
		importSceneGrid.setPrefSize(mapWidth, mapHeight);
		controlsBar.setDisable(true);
		switchablePanel.setDisable(true);
		switchablePanel.setMinWidth(sidePanelWidth);
		switchablePanel.setMinWidth(sidePanelWidth);
		GridPane helpLabelWrapper = new GridPane();
		helpLabelWrapper.setPadding(new Insets(10, 10, 10, 10));
		Label helpLabel = new Label("To import new simulation data, click File -> Import Scene");
		helpLabelWrapper.add(helpLabel, 0, 0);
		noMapNode.getChildren().add(helpLabelWrapper);	
		mapPane.getChildren().add(noMapNode);
		graphicsColumn.setPrefWidth(graphicsColumnWidth);
		graphicsColumn.setMinWidth(graphicsColumnWidth);
		VBox.setVgrow(menuBar, Priority.NEVER);
		VBox.setVgrow(middleRow, Priority.ALWAYS);
		VBox.setVgrow(sliderWrapper, Priority.NEVER);
		VBox.setVgrow(controlsBar, Priority.NEVER);
		HBox.setHgrow(graphicsColumn, Priority.NEVER);
		HBox.setHgrow(mapPane, Priority.ALWAYS);
		HBox.setHgrow(switchablePanel, Priority.NEVER);
		middleRow.setFillHeight(true);
		middleRow.getChildren().addAll(graphicsColumn, mapPane, switchablePanel);
		rootPane.getChildren().clear();
		rootPane.getChildren().addAll(menuBar, middleRow, sliderWrapper, controlsBar);
		Scene fxScene = new Scene(rootPane, Color.WHITE);
	    stage.setScene(fxScene);
	    stage.setTitle("JDEECo Visualizer");
	    InputStream iconStream = Resources.getResourceInputStream("cup.png");
	    if (iconStream != null){
	    	Image icon = new Image(iconStream);
	    	stage.getIcons().add(icon);
	    }
	    String styleURL = Resources.getResourceAsURI("style.css").toURL().toExternalForm();
	    fxScene.getStylesheets().add(styleURL);
	    stage.show();
	    MeasureInputProcessing.getInstance().register();
	}

	/**
	 * Initializes some of the panels and tool bars
	 */
	public Visualizer() {
		menuBar = new MenuBar();
		controlsBar = new HBox();
		graphicsColumn = new VBox();
		sliderWrapper = new HBox();
		switchablePanel = new VBox();
		setInstance(this);
	}

	/**
	 * Make sure that the various panels and toolbars contain the required
	 * contents, such as button, controls and fields.
	 */
	@Override
	public void init() throws Exception {
		createMenuBar();
		createControlsBar();
		createGraphicsColumn();
		createSliderWrapper();
		createSwitchablePanel();
	}	
	
	/**
	 * An instance of this class. Although in theory there may be multiple
	 * instances of {@link Visualizer} out there, under normal circumstances
	 * this class exists in a single instance, which is saved here
	 */
	private static Visualizer instance = null;

	/**
	 * Setter for {@link Visualizer#instance}.
	 */
	private void setInstance(Visualizer visualizer){
		Visualizer.instance = visualizer;
	}
	
	/**
	 * @return An instance of this class. Although in theory there may be multiple
	 * instances of {@link Visualizer} out there, under normal circumstances
	 * this class exists in a single instance, which is saved here
	 */
	public static Visualizer getInstance(){
		return instance;
	}
	
	/**
	 * The parameters for this application. They are used only when the application
	 * is run for performance measuring. 
	 */
	private cz.filipekt.jdcv.measuring.Parameters params;
	
	/**
	 * Sets the parameters for this application. They are used only when the application
	 * is run for performance measuring.
	 */
	public void setParams(cz.filipekt.jdcv.measuring.Parameters params){
		this.params = params;
	}
	
	/**
	 * This method is used only when the application is run for performance
	 * measuring purposes. It obtains the parameters from {@link Visualizer#params},
	 * applies them and starts the processing of the input.
	 */
	public void processParameters(){
		try {
			importSceneItem.fire();
			fields.get(0).setText(params.getMapPath());
			charsets.get(0).getSelectionModel().select(params.getMapEncoding());
			fields.get(1).setText(params.getEventPath());
			charsets.get(1).getSelectionModel().select(params.getEventEncoding());
			fields.get(2).setText(params.getEnsemblePath());
			charsets.get(2).getSelectionModel().select(params.getEnsembleEncoding());
			startAtField.setText(params.getStartAt());
			endAtField.setText(params.getEndAt());
			onlyComponentsBox.setSelected(params.isJustAgents());
			durationField.setText(params.getDuration());
			okButton.fire();
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * A synchronization primitive used to inform the performance measuring
	 * class that the input processing has finished
	 */
	private CountDownLatch latch;
	
	/**
	 * Contains the time at which the last executed input processing started
	 */
	private long timeStarted;
	
	/**
	 * Contains the time at which the last executed input processing ended
	 */
	private long timeEnded;
	
	/**
	 * Resets the {@link Visualizer#latch} so that it is again at its initial value.
	 * It is then used by the subseqent runs of performance measuring.
	 */
	public void renewLatch(){
		latch = new CountDownLatch(1);
	}
	
	/**
	 * Called when the input processing is starting. It saves the current
	 * time so that we can later compute the elapsed time.
	 */
	public void setStarted(){
		timeStarted = System.currentTimeMillis();
	}
	
	/**
	 * Called when the input processing just ended. It saves the current
	 * time so that we can compute the elapsed time. It also counts down
	 * the latch which is how we inform the measuring class that the results
	 * are ready.
	 */
	public void setEnded(){
		timeEnded = System.currentTimeMillis();
		if (latch != null){
			latch.countDown();
		}
	}
	
	/**
	 * Usually called by the external performance measuring class.
	 * This method blocks until the measuring results are ready, and
	 * then returns the elapsed time.
	 */
	public long getMeasuredTime() throws InterruptedException{
		if (latch != null){
			latch.await();
			return timeEnded - timeStarted;
		} else {
			return -1;
		}
	}
	
	/**
	 * Equivalent to the event that the user clicks the "close current scene" button
	 */
	public void clickCloseScene(){
		closeThisSceneItem.fire();
	}
	
	/**
	 * Hook to be able to toggle the visibility of Links, according to the value
	 * of the show links checkbox. To be used upon initialization for
	 * showing/hiding the links.
	 * 
	 * @param isLinksVisible
	 */
	public void toggleLinksVisibility() {
		scene.setLinksVisible(showLinksBox.isSelected());
	}

	/**
	 * Holds the plugins used to extend the functionality of the visualizer
	 * towards application-specific needs.
	 */
	private List<VisualizerPlugin> visualizerPlugins = new ArrayList<>();
	 
	public void addVisualizerPlugin(VisualizerPlugin visualizerPlugin) {
		visualizerPlugins.add(visualizerPlugin); 
	}
	
	public List<VisualizerPlugin> getVisualizerPlugins() {
		return visualizerPlugins; 
	}
}
