package cz.filipekt.jdcv;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javafx.animation.Animation.Status;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import cz.filipekt.jdcv.gui_logic.CloseSceneHandler;
import cz.filipekt.jdcv.gui_logic.ControlsBarItemHandler;
import cz.filipekt.jdcv.gui_logic.GraphicsPanelHandler;
import cz.filipekt.jdcv.gui_logic.ImportSceneHandler;
import cz.filipekt.jdcv.gui_logic.PlayButtonHandler;
import cz.filipekt.jdcv.gui_logic.RecordingHandler;
import cz.filipekt.jdcv.gui_logic.ScreenShotHandler;
import cz.filipekt.jdcv.gui_logic.TimeLineRateChanged;
import cz.filipekt.jdcv.gui_logic.TimeLineStatusHandler;
import cz.filipekt.jdcv.gui_logic.ZoomingHandler;

/**
 * Main class of the application. Run it to show the simulation data visualization.
 */
public class Visualizer extends Application {
	
	/**
	 * Marks whether the application runs in debugging mode
	 */
	private final boolean debug = false;
	
	/**
	 * Preferred width of the map view, in pixels.
	 */
	private final double mapWidth = 800.0;
	
	/**
	 * Preferred height of the map view, in pixels.
	 */
	private final double mapHeight = 600.0;
	
	/**
	 * The map that is being visualized, coupled with some view parameters.
	 */
	private MapScene scene;
	
	/**
	 * Sets the map that will be visualized by this application
	 * @param scene The map that will be visualized by this application
	 */
	public void setScene(MapScene scene) {
		this.scene = scene;
	}
	
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
	 * Width and height of the check mark shown next to the items in the View menu.
	 */
	private final double checkBoxSize = 18.0;
	
	/**
	 * Width and height of the play/pause icon shown inside the corresponding button 
	 * in the controls tool bar.
	 */
	private final double playIconSize = 20.0;
	
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
	 * @param resourceName A resource to be loaded
	 * @return An {@link InputStream} instance reading from the specified resource
	 * @throws IOException When the specified resource could not be found or opened
	 */
	private InputStream getResourceInputStream(String resourceName) throws IOException{
		if (debug){
			return Files.newInputStream(Paths.get("C:/diplomka/JDEECoVisualizer/resources", resourceName));
		} else {
			return getClass().getResourceAsStream("/resources/" + resourceName);
		}
	}
	
	/**
	 * @param resourceFileName Filename of the desired image
	 * @param size Preferred width and height of the {@link ImageView} 
	 * @return The desired image wrapped in a {@link ImageView}
	 */
	ImageView getImageView(String resourceFileName, double size){
		try {
			if (resourceFileName == null){
				throw new NullPointerException();
			}
			InputStream stream = getResourceInputStream(resourceFileName);
			return new ImageView(new Image(stream, size, size, true, true));
		} catch (Exception ex) { // if the specified image is not found
			return new ImageView();
		}
	}
	
	/**
	 * Constructs the main menu bar of the application.
	 * @return The main menu bar of the application.
	 * @throws IOException Unless the application source folder contents have been changed in 
	 * any way by the user, this exception will never be thrown.
	 */
	private MenuBar createMenuBar() throws IOException {
		MenuBar menuBar = new MenuBar();
		Menu fileMenu = new Menu("File");
		MenuItem importSceneItem = new MenuItem("Import Scene"); 
		importSceneItem.setDisable(false);
		MenuItem closeThisSceneItem = new MenuItem("Close This Scene");
		closeThisSceneItem.setDisable(true);
		importSceneItem.setOnAction(new ImportSceneHandler(importSceneItem, closeThisSceneItem, this));
		closeThisSceneItem.setOnAction(new CloseSceneHandler(importSceneItem, closeThisSceneItem, this));
		fileMenu.getItems().addAll(importSceneItem, closeThisSceneItem);
		Menu editMenu = new Menu("Options");
		Menu viewMenu = new Menu("View");
		MenuItem controlsPanel = new MenuItem("Controls Panel");
		ImageView checkBoxImage = getImageView("checkmark.png", checkBoxSize);
		controlsPanel.setGraphic(checkBoxImage);	
		controlsPanel.setOnAction(new ControlsBarItemHandler(controlsPanel, checkBoxImage, this));
		final MenuItem graphicsPanel = new MenuItem("Graphics Panel");	
		final ImageView checkBoxImage2 = getImageView("checkmark.png", checkBoxSize);
		graphicsPanel.setGraphic(checkBoxImage2);
		graphicsPanel.setOnAction(new GraphicsPanelHandler(graphicsPanel, checkBoxImage2, this));
		viewMenu.getItems().addAll(controlsPanel, graphicsPanel);
		menuBar.getMenus().addAll(fileMenu, editMenu, viewMenu);
		return menuBar;
	}
	
	/**
	 * The {@link Node} that is shown whenever no map scene is open.
	 */
	private final Pane noMapNode = new VBox();
	
	/**
	 * @return The {@link Node} that is shown whenever no map scene is open.
	 * @see {@link Visualizer#noMapNode}
	 */
	public Pane getNoMapNode() {
		return noMapNode;
	}

	/**
	 * Contains the controls where users can specify input XML files for the visualization. 
	 */
	private final GridPane importSceneGrid;

	/**
	 * @return Grid that contains the controls where users can specify input XML files for the visualization.
	 * @see {@link Visualizer#importSceneGrid}
	 */
	public GridPane getImportSceneGrid() {
		return importSceneGrid;
	}

	/**
	 * Container for map view, or if no map is currently view, for a dialog for loading a map.
	 */
	private final Pane mapPane = new StackPane();
	
	/** 
	 * @return Preferred width of the map view, in pixels.
	 * @see {@link Visualizer#mapWidth}
	 */
	double getMapWidth() {
		return mapWidth;
	}

	/**
	 * @return Preferred height of the map view, in pixels.
	 * @see {@link Visualizer#mapHeight}
	 */
	double getMapHeight() {
		return mapHeight;
	}

	/**
	 * @return Container for map view, or if no map is currently view, for a dialog for loading a map.
	 * @see {@link Visualizer#mapPane}
	 */
	public Pane getMapPane() {
		return mapPane;
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
	 * Builds the {@link Visualizer#importSceneGrid}.
	 * @param timeLineStatus Called whenever the visualization is started, paused or stopped
	 * @return The {@link GridPane} to be used for importing new scenes.
	 */
	private GridPane createImportSceneGrid(ChangeListener<Status> timeLineStatus, ChangeListener<Number> timeLineRate){
		GridPane pane = new GridPane();
		List<Label> labels = new ArrayList<>();
		labels.add(new Label("Location of the network definition XML:"));
		labels.add(new Label("Location of the event log:"));
		labels.add(new Label("Location of the ensemble event log:"));
		final List<TextField> fields = new ArrayList<>();
		List<Button> chooserButtons = new ArrayList<>();
		for (int i = 0; i < labels.size(); i++){
			fields.add(new TextField());
			chooserButtons.add(new Button("Select.."));
		}
		for (TextField field : fields){
			field.setPrefWidth(inputFieldsWidth);
		}
		if (debug){
			fields.get(0).setText("C:/diplomka/example_output/network.xml");
			fields.get(1).setText("C:/diplomka/example_output/events.xml");
			fields.get(2).setText("C:/diplomka/example_output/ensembles.xml");
		}
		setUpDragNDrop(fields);
		for (int i = 0; i < chooserButtons.size(); i++){
			Button button = chooserButtons.get(i);
			TextField field = fields.get(i);
			button.setOnMouseClicked(new ButtonXmlChooserHandler(stage, field));
		}
		int row = 0;
		for (int i = 0; i < labels.size(); i++){
			Label label = labels.get(i);
			TextField field = fields.get(i);
			Button button = chooserButtons.get(i);
			pane.add(label, 0, row);
			pane.add(field, 1, row);
			pane.add(button, 2, row);
			row += 1;
		}
		
		Label durationLabel = new Label("Target duration (seconds):");
		ComboBox<Integer> durationsBox = new ComboBox<>();
		durationsBox.setEditable(false);
		durationsBox.getItems().addAll(10, 30, 60, 120, 300, 3600);
		durationsBox.setValue(60);
		pane.add(durationLabel, 0, row);
		pane.add(durationsBox, 1, row);		
		row += 1;
		
		Label onlyComponentsLabel = new Label("Show only the injected JDEECo components:");
		CheckBox onlyComponentsBox = new CheckBox();
		onlyComponentsBox.setSelected(true);
		pane.add(onlyComponentsLabel, 0, row);
		pane.add(onlyComponentsBox, 1, row);
		row += 1;
		
		Button okButton = new Button("OK");
		okButton.setOnMouseClicked(new SceneBuilder(fields, okButton, onlyComponentsBox, pane, 
				Visualizer.this, durationsBox, timeLineStatus, timeLineRate));
		pane.add(okButton, 1, row);
		pane.setAlignment(Pos.CENTER);
		pane.setHgap(importSceneGridHGap);
		pane.setVgap(importSceneGridVGap);
		return pane;
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
	 * @return The tool bar containing the various zooming, pausing, forwarding etc. options
	 * @throws IOException Unless the application source folder contents have been changed in 
	 * any way by the user, this exception will never be thrown.
	 */
	private HBox createControlsBar() throws IOException{
		ImageView pauseImage = getImageView("video-pause.png", playIconSize);
		ImageView playImage = getImageView("video-play.png", playIconSize);
		Button playButton = new Button();
		final Button stopButton = new Button();
		playButton.setGraphic(playImage);
		playButton.setOnMouseClicked(new PlayButtonHandler(this));
		timeLineStatus = new TimeLineStatusHandler(playButton, stopButton, playImage, pauseImage);
		final Label speedLabel = new Label("Speed: 1.0x");
		timeLineRate = new ChangeListener<Number>() {
			
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1,
					Number arg2) {
				speedLabel.setText("Speed: " + arg2.doubleValue() + "x");
			}
		};
		ImageView ffdImage = getImageView("fast-forward.png", playIconSize);
		Button ffdButton = new Button("Speed up", ffdImage);
		ImageView rwImage = getImageView("rewind.png", playIconSize);
		Button rwButton = new Button("Speed down", rwImage);
		ffdButton.setOnMouseClicked(new TimeLineRateChanged(true, this));
		rwButton.setOnMouseClicked(new TimeLineRateChanged(false, this));
		ImageView zoomInImage = getImageView("zoom-in.png", playIconSize);
		Button zoomInButton = new Button("Zoom IN", zoomInImage);
		zoomInButton.setOnMouseClicked(new ZoomingHandler(1.2, this));
		ImageView zoomOutImage = getImageView("zoom-out.png", playIconSize);
		Button zoomOutButton = new Button("Zoom OUT", zoomOutImage);
		zoomOutButton.setOnMouseClicked(new ZoomingHandler(1/1.2, this));
		ImageView stopImage = getImageView("stop-black.png", playIconSize);
		stopButton.setDisable(true);
		stopButton.setGraphic(stopImage);
		stopButton.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event arg0) {
				Timeline timeline = scene.getTimeLine();
				if ((timeline.getStatus() == Status.RUNNING) || (timeline.getStatus() == Status.PAUSED)){
					timeline.stop();
					stopButton.setDisable(true);
				}
			}
		});
		
		HBox hbox = new HBox(speedLabel, rwButton, playButton, stopButton, ffdButton, zoomInButton, zoomOutButton); 	
		hbox.setSpacing(10);
		hbox.setAlignment(Pos.CENTER_RIGHT);
		return hbox;
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
	private final VBox vbox = new VBox();
	
	/**
	 * @return The root element of the main {@link Scene} of the application.
	 * @see {@link Visualizer#vbox}
	 */
	public VBox getVBox(){
		return vbox;
	}
	
	/**
	 * @return Builds and returns the column, shown next to the map on the left side, containing various graphics options.
	 * @throws IOException Unless the application source folder contents have been changed in 
	 * any way by the user, this exception will never be thrown. 
	 */
	private VBox createGraphicsColumn() throws IOException{
		VBox panel = new VBox();
		panel.setDisable(true);
		panel.setAlignment(Pos.TOP_LEFT);
		final CheckBox showNodesBox = new CheckBox("Show nodes");
		showNodesBox.setSelected(true);
		showNodesBox.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				if (scene != null){
					scene.setNodesVisible(showNodesBox.isSelected());
				}
			}
		});
		panel.getChildren().add(showNodesBox);
		final CheckBox showLinksBox = new CheckBox("Show links");
		showLinksBox.setSelected(true);
		showLinksBox.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent arg0) {
				if (scene != null){
					scene.setLinksVisible(showLinksBox.isSelected());
				}
			}
		});
		panel.getChildren().add(showLinksBox);
		ImageView snapShotImage = getImageView("screenshot.png", playIconSize);
		Button screenShotButton = new Button("Snapshot", snapShotImage);
		screenShotButton.setOnMouseClicked(new ScreenShotHandler(this));
		panel.getChildren().add(screenShotButton);		
		ImageView recordStartImage = getImageView("record.png", playIconSize);
		ImageView recordStopImage = getImageView("stop.png", playIconSize);
		Button recordButton = new Button("Record", recordStartImage);
		recordButton.setOnMouseClicked(new RecordingHandler(recordButton, recordStartImage, recordStopImage, this));
		panel.getChildren().add(recordButton);
		for (Node node : panel.getChildren()){
			VBox.setMargin(node, new Insets(graphicsItemsMargin, 0, graphicsItemsMargin, 2 * graphicsItemsMargin));
		}
		graphicsColumnDefaults = new Runnable() {
			
			@Override
			public void run() {
				showNodesBox.setSelected(true);
				showLinksBox.setSelected(true);
			}
		};
		return panel;
	}
	
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
	 * Builds the GUI, should only be called by the JavaFX runtime.
	 */
	@Override
	public void start(Stage stage) throws IOException {	
		this.stage = stage;
		noMapNode.setPrefSize(mapWidth, mapHeight);
		controlsBar.setDisable(true);
		Label helpLabel = new Label("To import new simulation data, click File -> Import Scene");
		helpLabel.setPadding(new Insets(10, 10, 10, 10));
		noMapNode.getChildren().add(helpLabel);	
		mapPane.getChildren().add(noMapNode);
		graphicsColumn.setPrefWidth(graphicsColumnWidth);
		graphicsColumn.setMinWidth(graphicsColumnWidth);
		middleRow.getChildren().addAll(graphicsColumn, mapPane);		
		vbox.getChildren().clear();
		vbox.getChildren().addAll(menuBar, middleRow, controlsBar);
		Scene fxScene = new Scene(vbox, Color.WHITE);
		fxScene.heightProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> arg0,
					Number arg1, Number arg2) {
				double diff = arg2.doubleValue() - arg1.doubleValue();
				mapPane.setPrefHeight(mapPane.getPrefHeight() + diff);			
			}
		});
		fxScene.widthProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> arg0,
					Number arg1, Number arg2) {
				double diff = arg2.doubleValue() - arg1.doubleValue();
				mapPane.setPrefWidth(mapPane.getPrefWidth() + diff);
			}
		});
		mapPane.heightProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> arg0,
					Number arg1, Number arg2) {								
				Node child = mapPane.getChildren().get(0);
				if (child instanceof Region){
					Region reg = (Region)child;
					reg.setPrefHeight(arg2.doubleValue());
				} 
			}
		});			
		mapPane.widthProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> arg0,
					Number arg1, Number arg2) {
				Node child = mapPane.getChildren().get(0);
				if (child instanceof Region){
					Region reg = (Region)child;
					reg.setPrefWidth(arg2.doubleValue());
				}
			}
		});
	    stage.setScene(fxScene);
	    stage.setTitle("Map Visualizer");
	    stage.show();
	}

	/**
	 * Initializes some of the panels and tool bars.
	 * @throws IOException Unless the application source folder contents have been changed in 
	 * any way by the user, this exception will never be thrown.
	 */
	public Visualizer() throws IOException {				
		menuBar = createMenuBar();	
		controlsBar = createControlsBar();
		graphicsColumn = createGraphicsColumn();
		importSceneGrid = createImportSceneGrid(timeLineStatus, timeLineRate);
	}			
	
}
