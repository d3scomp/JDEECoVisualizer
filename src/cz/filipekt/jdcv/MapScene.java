package cz.filipekt.jdcv;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;

import cz.cuni.mff.d3s.jdeeco.visualizer.extensions.MapSceneExtensionPoint;
import cz.filipekt.jdcv.SceneImportHandler.ImageProvider;
import cz.filipekt.jdcv.SceneImportHandler.ShapeProvider;
import cz.filipekt.jdcv.checkpoints.CheckPoint;
import cz.filipekt.jdcv.checkpoints.CheckPoint.Type;
import cz.filipekt.jdcv.checkpoints.CheckPointDatabase;
import cz.filipekt.jdcv.corridors.Background;
import cz.filipekt.jdcv.corridors.CorridorLoader;
import cz.filipekt.jdcv.corridors.LinkCorridor;
import cz.filipekt.jdcv.ensembles.CoordinatorRelation;
import cz.filipekt.jdcv.ensembles.EnsembleDatabase;
import cz.filipekt.jdcv.ensembles.MembershipRelation;
import cz.filipekt.jdcv.events.EnsembleEvent;
import cz.filipekt.jdcv.events.Event;
import cz.filipekt.jdcv.geometry.MatsimToVisualCoordinates;
import cz.filipekt.jdcv.geometry.PointTransformer;
import cz.filipekt.jdcv.gui_logic.InfoPanelSetter;
import cz.filipekt.jdcv.network.MyLink;
import cz.filipekt.jdcv.network.MyNode;
import cz.filipekt.jdcv.plugins.InfoPanel;
import cz.filipekt.jdcv.prefs.PreferencesBuilder;
import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

/**
 * The scene that the {@link Visualizer} makes a graphic view of. 
 * It contains the map, view parameters, event log etc.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class MapScene {
	
	/**
	 * Width of the image that represents a node in the visualization
	 */
	public final int NODE_IMAGE_WIDTH = 48;
	
	/**
	 * Height of the image that represents a node in the visualization
	 */
	public final int NODE_IMAGE_HEIGHT = 42;
	
	/**
	 * Contains the network nodes. Keys = node IDs, values = {@link MyNode} node representations.
	 */
	private Map<String,MyNode> nodes;
	
	public Map<String,MyNode> getNodes() {
		return nodes;
	}
	
	/**
	 * Contains the network links. Keys = link IDs, values = {@link MyLink} link representations.
	 */
	private final Map<String,MyLink> links = new HashMap<>();
	
	/**
	 * Maps visual representations of nodes to the corresponding parsed node XML elements.
	 */
	private final Map<Node,MyNode> circles = new HashMap<>();
	
	/**
	 * Factor by which the x-coordinate scale (from MATSIM map) must be multiplied so 
	 * that the resulting network view fits nicely into the window of preferred width 
	 */
	private double widthFactor;
	
	/**
	 * Factor by which the y-coordinate scale (from MATSIM map) must be multiplied so 
	 * that the resulting network view fits nicely into the window of preferred height 
	 */
	private double heightFactor;

	/**
	 * Radius (in pixels) of the {@link Shape} objects representing the network nodes.
	 */
	private final double nodeRadius = 4.0;
	
	/**
	 * Color of the {@link Shape} instances representing the network nodes.
	 */
	private final Paint nodeColor = Color.FIREBRICK;
	
	/**
	 * Zoom factor used to view the map, relative to the preferred size defined 
	 * by {@link MapScene#preferredMapWidth} and {@link MapScene#preferredMapHeight}.
	 */
	private double zoom = 1.0;

	/**
	 * @return Scrollable container for the network(map) components such as nodes and links.
	 * @see {@link MapScene#mapPane}
	 */
	public ScrollPane getMapPane() {
		return mapPane;
	}	
	
	/**
	 * Interval (in miliseconds) at which a screenshot is taken when recording the visualization
	 */
	private final double recordingFreqency = 200;
	
	/**
	 * Directory to which the recorded images will be stored
	 */
	private File recordingDirectory;
	
	/**
	 * @param recordingDirectory Directory to which the recorded images will be stored
	 * @see {@link MapScene#recordingDirectory}
	 */
	public void setRecordingDirectory(File recordingDirectory) {
		this.recordingDirectory = recordingDirectory;
	}
	
	/**
	 * If true, the visualization is currently being recorded. If false, it is not.
	 */
	private boolean recordingInProgress = false;

	/**
	 * @param recordingInProgress If true, the visualization will from now be recorded. If false, it will not.
	 * @see {@link MapScene#recordingInProgress}
	 */
	public void setRecordingInProgress(boolean recordingInProgress) {
		this.recordingInProgress = recordingInProgress;
	}

	/**
	 * @return If true, the visualization is currently being recorded. If false, it is not.
	 */
	public boolean isRecordingInProgress() {
		return recordingInProgress;
	}
	
	/**
	 * Holds the path to the folder with the additional resources (e.g. pngs)
	 * used in custom visualization of persons and nodes
	 */
	private String additionalResourcesPath;
	
	public void setAdditionalResourcesPath(String additionalResourcesPath) {
		this.additionalResourcesPath = additionalResourcesPath;
	}
	
	public String getAdditionalResourcesPath() {
		return additionalResourcesPath;
	}
	
	/**
	 * Makes sure that the {@link KeyFrame} instances allowing for recording of
	 * snapshots are added to the keyframe list of {@link MapScene#timeLine}.
	 */
	private void addRecordingFrames(){
		if (matsimEventsPresent && !recordingFramesAdded){			
			List<KeyFrame> frames = createRecordingFrames();
			timeLine.getKeyFrames().addAll(frames);
			recordingFramesAdded = true;
		}
	}
	
	/**
	 * Flushes the specified number of recorded snapshots (stored in 
	 * {@link MapScene#recordedFrames}) to disk.
	 * A new separate thread is used for this purpose. These threads are synchronized,
	 * i.e. the caller does not have to care about threading.
	 * @param framesCount Number of screenshots to flush
	 */
	public void flushRecordedFrames(final int framesCount){
		if (recordedFrames.size() > 0){
			showProgress();
			Task<Void> task = new Task<Void>() {
				
				@Override
				public Void call() {											
					synchronized(recordedFrames){
						Iterator<ImageWriter> it = ImageIO.getImageWritersByFormatName("png");
						ImageWriter writer = null;
						if (it.hasNext()){
							writer = it.next();
						}
						if (writer != null){
							try {						
								DateFormat df = new SimpleDateFormat("yyyy-MMM-dd-HH-mm-ss");
								String datePrefix = df.format(new Date());
								int i = 0;
								long limit = Math.min(recordedFrames.size(), framesCount);
								Iterator<WritableImage> iter = recordedFrames.iterator();
								while (iter.hasNext() && (i < framesCount)){								
									WritableImage wi = iter.next();
									RenderedImage recordedImage = SwingFXUtils.fromFXImage(wi, null);
									File file = new File(recordingDirectory, datePrefix + "_" + i + ".png");							
									try (FileImageOutputStream outStream = new FileImageOutputStream(file)){									
										writer.setOutput(outStream);
										writer.write(recordedImage);	
										outStream.flush();
										updateProgress(i+1, limit);
									} catch (IOException ex) {									
									} finally {
										iter.remove();
										i++;
									}
								}
							} finally {
								writer.dispose();
							}
						}						
					}
					return null;
				}
			};
			EventHandler<WorkerStateEvent> flushWindowCloser = new EventHandler<WorkerStateEvent>() {
				
				@Override
				public void handle(WorkerStateEvent arg0) {
					if (flushWindow != null){
						flushWindow.close();
					}
					if (timeLine.getStatus() == Status.PAUSED){
						timeLine.play();
					}
					controlsBar.setDisable(false);
				}
			};
			task.setOnSucceeded(flushWindowCloser);
			task.setOnFailed(flushWindowCloser);
			task.setOnCancelled(flushWindowCloser);
			flushProgress.progressProperty().bind(task.progressProperty());
			new Thread(task).start();
		}
	}
	 
	/**
	 * Depicts the progress of flushing the recorded video 
	 * frames to disc
	 */
	private final ProgressBar flushProgress = new ProgressBar();
	
	/**
	 * Window depicting the progress of flushing the recorded video 
	 * frames to disc
	 */
	private Stage flushWindow;
	
	/**
	 * Shows a small window depicting the progress of flushing the recorded video 
	 * frames to disc
	 */
	private void showProgress(){
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {	
				controlsBar.setDisable(true);
				flushWindow = new Stage();
				flushWindow.setOnCloseRequest(new EventHandler<WindowEvent>() {
					
					@Override
					public void handle(WindowEvent event) {
						event.consume();
					}
				});
				flushProgress.setPrefWidth(200);
				flushWindow.initStyle(StageStyle.UTILITY);
				flushWindow.setTitle("Flushing to Disc");
				HBox pane = new HBox();
				pane.getChildren().addAll(flushProgress);
				HBox.setHgrow(flushProgress, Priority.ALWAYS);
				Scene scene = new Scene(pane);
				flushWindow.setScene(scene);				
				flushWindow.show();
			}
		});
	}
	
	/**
	 * Temporary storage for recorded snapshots when recording is underway.
	 */
	private final List<WritableImage> recordedFrames = new ArrayList<>();
	
	/**
	 * If true, the {@link KeyFrame} instances produced by {@link MapScene#createRecordingFrames()}
	 * have already been added to the list of keyframes of {@link MapScene#timeLine}.
	 */
	private boolean recordingFramesAdded = false;
	
	/**
	 * When recording is on, every time this number of frames is recorded, 
	 * they are flushed to the disc.
	 */
	private final int recordingFlushInterval = 20;
	
	/**
	 * @return {@link KeyFrame} instances that are later inserted into the {@link MapScene#timeLine} 
	 * keyframes, to allow possible recording requests.
	 */
	private List<KeyFrame> createRecordingFrames(){
		double totalTime = timeLine.getTotalDuration().toMillis();
		List<KeyFrame> res = new ArrayList<>();
		long count = (long)Math.floor(totalTime / recordingFreqency);
		for (long i = 0; i<count; i++){
			double time = recordingFreqency * i;
			Duration timeVal = new Duration(time);
			KeyFrame frame = new KeyFrame(timeVal, new RecordingAction());
			res.add(frame);
			if ((i % recordingFlushInterval) == 0){
				frame = new KeyFrame(timeVal, new EventHandler<ActionEvent>(){

					@Override
					public void handle(ActionEvent arg0) {	
						if (recordingInProgress){
							timeLine.pause();
							flushRecordedFrames(recordingFlushInterval);
						}
					}
					
				});
				res.add(frame);
			}
		}		
		return res;
	}
	
	/**
	 * Called whenever the visualization is being recorded and the {@link MapScene#recordingFreqency}
	 * interval just passed. Saves a current snapshot into {@link MapScene#recordedFrames}.
	 */
	private class RecordingAction implements EventHandler<ActionEvent>{

		@Override
		public void handle(ActionEvent event) {
			if (recordingInProgress){
				WritableImage image = mapContainer.snapshot(null, null);
				synchronized(recordedFrames){
					recordedFrames.add(image);
				}
			}
		}
		
	}

	/**
	 * Determines the minimal and maximal x,y coordinates across all of the map nodes.
	 * @return Array consisting of (minimal x, minimal y, maximal x, maximal y) coordinates of map nodes.
	 */
	private double[] getMapBorders(){
		double minx = Double.MAX_VALUE;
		double miny = Double.MAX_VALUE;
		double maxx = 0.0;
		double maxy = 0.0;
		for (MyNode node : nodes.values()){
			if (node.getX() < minx){
				minx = node.getX();
			}
			if (node.getX() > maxx){
				maxx = node.getX();
			}
			if (node.getY() < miny){
				miny = node.getY();
			}
			if (node.getY() > maxy){
				maxy = node.getY();
			}
		}
		return new double[]{minx, miny, maxx, maxy};
	}
	
	/**
	 * Constructs the visualizations of the map nodes. Makes sure that proper
	 * magnification and metric is used.
	 * @return The visualizations of the map nodes mapped to corresponding node 
	 * element representations.
	 */
	private Map<Shape,MyNode> generateCircles(){
		Map<Shape,MyNode> res = new HashMap<>();
		for (MyNode node : nodes.values()){
			double x = matsimToVisual.transformX(node.getX());
			double y = matsimToVisual.transformY(node.getY());
			final Circle circle = new Circle(x, y, nodeRadius, nodeColor);
			circle.setEffect(new BoxBlur());
			res.put(circle, node);			
			final Map<String,String> data = new LinkedHashMap<>();
			data.put("Node ID", node.getId());
			data.put("x-coordinate", node.getX() + "");
			data.put("y-coordinate", node.getY() + "");
			circle.setOnMouseClicked(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent arg0) {
					InfoPanel.getInstance().setInfo("Node selected:", data);
				}
			});
			circle.setOnMouseEntered(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent arg0) {
					circle.setRadius(nodeRadius * 2);
				}
			});
			circle.setOnMouseExited(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent arg0) {
					circle.setRadius(nodeRadius);
				}
			});
		}
		return res;
	}
	
	/**
	 * Generated the visualization of the nodes according to their background images. 
	 * @see generateCircles()
	 */
	private Map<Node, MyNode> generateNodesWithBackgroundImage(ShapeProvider provider, String[] selectedNodes) throws IOException {
		Map<Node,MyNode> res = new HashMap<>();
		for (MyNode node : nodes.values()){
			
			double x = matsimToVisual.transformX(node.getX());
			double y = matsimToVisual.transformY(node.getY());
			Node shape = provider.getNewShape();
			
			if (shape != null){
				shape.setTranslateX(x);
				shape.setTranslateY(y);
			}
			res.put(shape, node);		
			
			final Map<String,String> data = new LinkedHashMap<>();
			data.put("Node ID", node.getId());
			data.put("x-coordinate", node.getX() + "");
			data.put("y-coordinate", node.getY() + "");
			
			shape.setOnMouseEntered(null);
			shape.setOnMouseExited(null);
			shape.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent arg0) {
					InfoPanel.getInstance().setInfo("Node selected:", data);
				}
			});
		}
		return res;
	}
	
	/**
	 * Makes sure that when the user clicks on the visualization of the link,
	 * detailed info about the link is shown in the info-panel.
	 * @param link Detailed info about this link will be shown
	 * @param visual Visualization of the link given in the first parameter
	 */
	private void connectLinkWithInfoPanel(MyLink link, Node visual){
		final Map<String,String> data = new LinkedHashMap<>();
		data.put("Link ID", link.getId());
		data.put("From Node", link.getFrom().getId());
		data.put("From x-coordinate", Double.toString(link.getFrom().getX()));
		data.put("From y-coordinate", Double.toString(link.getFrom().getY()));
		data.put("To Node", link.getTo().getId());
		data.put("To x-coordinate", Double.toString(link.getTo().getX()));
		data.put("To y-coordinate", Double.toString(link.getTo().getY()));
		visual.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				InfoPanel.getInstance().setInfo("Link Selected:", data);
			}
		});
	}
	
	/**
	 * Creates the visualizations of all links. 
	 * @return Mapping of link IDs to their corresponding visualizations
	 */
	private Map<String,LinkCorridor> generateLinkCorridors(){
		Map<String,LinkCorridor> res = new HashMap<>();
		for (MyLink link : links.values()){			
			Point2D fromPoint = new Point2D(matsimToVisual.transformX(link.getFrom().getX()), 
					matsimToVisual.transformY(link.getFrom().getY()));
			Point2D toPoint = new Point2D(matsimToVisual.transformX(link.getTo().getX()), 
					matsimToVisual.transformY(link.getTo().getY()));
			CorridorLoader cl = new CorridorLoader(link, fromPoint, toPoint, matsimToVisual);
			LinkCorridor corridor = cl.build();
			connectLinkWithInfoPanel(link, corridor.getVisualization());
			res.put(link.getId(), corridor);
		}
		return res;
	}		
	
	/**
	 * Double of the width of the white margin that is added on each side of the map.
	 */
	private final double constantMargin = 25.0;
		
	/**
	 * Converter from the coordinates used in the MATSIM simulation map to the coordinates
	 * used in the visualization, i.e. as used on the screen
	 */
	private PointTransformer matsimToVisual;
	
	public PointTransformer getMatsimToVisual() {
		return matsimToVisual;
	}
	
	/**
	 * The simulation time at which we start the visualization
	 */
	private double minTime;
	
	/**
	 * The simulation time at which we end the visualization
	 */
	private double maxTime;
	
	/**
	 * @return The simulation time at which we start the visualization
	 * @see {@link MapScene#minTime}
	 */
	public double getMinTime() {
		return minTime;
	}

	/**
	 * @return The simulation time at which we end the visualization
	 * @see {@link MapScene#maxTime}
	 */
	public double getMaxTime() {
		return maxTime;
	}

	/**
	 * The actual intended duration of the visualization (i.e. in visualization time)
	 */
	private int duration;
	
	/**
	 * Converter from visualization to simulation time
	 * @param visualizationTime A time in visualization time format
	 * @return The time in simulation time format 
	 */
	public double convertToSimulationTime(double visualizationTime){
		double ratio = duration * 1000 / (maxTime - minTime);
		return (visualizationTime / ratio) + minTime;
	}
	
	/**
	 * Converter from simulation to visualization time
	 * @param simulationTime A time in simulation time format
	 * @return The time in visualization time format
	 */
	public double convertToVisualizationTime(double simulationTime){
		double ratio = duration * 1000 / (maxTime - minTime);
		double diff = simulationTime - minTime;
		return diff * ratio;
	}
	
	/**
	 * Maps each person's ID to the person's graphical representation
	 */
	private final Map<String,Node> personShapes = new HashMap<>();
	
	/**
	 * Timeline used for animation of the simulation output 
	 */
	private final Timeline timeLine = new Timeline();

	/**
	 * @return Timeline used for animation of the simulation output
	 * @see {@link Visualizer#timeLine}
	 */
	public Timeline getTimeLine() {
		return timeLine;
	}
	
	/**
	 * Maps each link ID to the corresponding link visualization
	 */
	private final Map<String,LinkCorridor> linkCorridors = new HashMap<>();

	/**
	 * Holds the provider of people's shapes in order to be preserved while invoking updateNodes().
	 */
	private ShapeProvider peopleShapeProvider;
	
	/**
	 * Updates the graphics of the nodes: instead of nodes being visualized as
	 * circles, the nodes' background images are visualized.
	 * @see update()
	 */
	public void updateNodes(ShapeProvider peopleShapeProvider, String[] selectedPeople, ShapeProvider nodesShapeProvider, String[] selectedNodes) throws IOException{
		timeLine.stop();
		timeLine.getKeyFrames().clear();
		mapContainer.getChildren().clear();
		
		Map<Node,MyNode> newNodes = generateNodesWithBackgroundImage(nodesShapeProvider,selectedNodes);
		circles.clear();
		circles.putAll(newNodes);
		Map<String,LinkCorridor> newCorridors = generateLinkCorridors();
		linkCorridors.clear();
		linkCorridors.putAll(newCorridors); 
		for (LinkCorridor corridor : linkCorridors.values()){
			mapContainer.getChildren().add(corridor.getVisualization());
		}
		if (peopleShapeProvider == null) {
			peopleShapeProvider = this.peopleShapeProvider;
		}
		produceShapes(peopleShapeProvider, selectedPeople);
		addRecordingFrames();
		setBackground();
		mapContainer.getChildren().addAll(circles.keySet());
		List<Node> sortedList = getSortedListofOtherShapes();
		mapContainer.getChildren().addAll(sortedList);
		mapContainer.getChildren().addAll(personShapes.values());
		mapContainer.getChildren().addAll(ensembleShapes.values());
//		moveShapesToFront();
	}
	
	/**
	 * Updates the collections of node instances that represent the map elements,
	 * both mobile (agents, ensemble memberships) and immobile (nodes,links).
	 * Also, the {@link MapScene#mapContainer} holding these {@link Shape} instances for
	 * visualizing purposes is updated with the new values.
	 * 
	 * @param peopleShapeProvider Used for generating the visualizations of people
	 * @param justMovables If true, only the moveable objects (people,ensembles) will be updated 
	 * @param selectedPeople People whose visualizations will be updated
	 * @throws IOException  When a person shape could not be loaded for any reason
	 */
	public void update(ShapeProvider peopleShapeProvider, boolean justMovables, String[] selectedPeople) throws IOException{
		timeLine.stop();
		timeLine.getKeyFrames().clear();
		mapContainer.getChildren().clear();
		if (!justMovables){
			Map<Shape,MyNode> newCircles = generateCircles();
			circles.clear();
			circles.putAll(newCircles);
			Map<String,LinkCorridor> newCorridors = generateLinkCorridors();
			linkCorridors.clear();
			linkCorridors.putAll(newCorridors);
		}
		for (LinkCorridor corridor : linkCorridors.values()){
			mapContainer.getChildren().add(corridor.getVisualization());
		}
		this.peopleShapeProvider = peopleShapeProvider;
		produceShapes(peopleShapeProvider, selectedPeople);
		addRecordingFrames();
		setBackground();
		mapContainer.getChildren().addAll(circles.keySet());
		List<Node> sortedList = getSortedListofOtherShapes();
		mapContainer.getChildren().addAll(sortedList);
		mapContainer.getChildren().addAll(personShapes.values());
		mapContainer.getChildren().addAll(ensembleShapes.values());
//		moveShapesToFront();
	}

	/**
	 * 
	 * @return a list of shapes coming from the visualizer's extensions sorted
	 *         according their timestamps
	 */
	private List<Node> getSortedListofOtherShapes() {
		
		List<OtherShapeMetaData> metadataList = new ArrayList<>(otherShapes.keySet());
		
		Collections.sort(metadataList, new Comparator(){

			@Override
			public int compare(Object o1, Object o2) {
				OtherShapeMetaData s1 = (OtherShapeMetaData) o1;
				OtherShapeMetaData s2 = (OtherShapeMetaData) o2;
				return (int) (s1.getEventTime() - s2.getEventTime());
			}});
		
		List<Node> sortedList = new ArrayList<>();
		for (OtherShapeMetaData metadata: metadataList) {
			sortedList.add(otherShapes.get(metadata));
		}
		return sortedList;
	}
	
	/**
	 * Sets the map background using the parsed "background" XML element
	 */
	private void setBackground(){
		if (background == null){
			String style = Visualizer.getBackColorCSSField() + ": " + 
					Visualizer.getDefaultbackround() + ";";
			mapPane.setStyle(style);
		} else {
			if (background.getImage() == null){
				Color color = background.getColor();
				if (color != null){
					StringBuilder style = new StringBuilder();
					style.append(Visualizer.getBackColorCSSField());
					style.append(": ");
					style.append("rgb(");
					String red = Long.toString(Math.round(color.getRed() * 255));
					style.append(red);
					style.append(",");
					String green = Long.toString(Math.round(color.getGreen() * 255));
					style.append(green);
					style.append(",");
					String blue = Long.toString(Math.round(color.getBlue() * 255));
					style.append(blue);
					style.append(");");
					mapPane.setStyle(style.toString());
				}
			} else {
				String imagePath = background.getImage();
				Point2D leftTopMatsim = new Point2D(background.getLeftTopX(), background.getLeftTopY());
				Point2D leftTop = matsimToVisual.transform(leftTopMatsim);
				Point2D rightBottomMatsim = new Point2D(background.getRightBottomX(), 
						background.getRightBottomY());
				Point2D rightBottom = matsimToVisual.transform(rightBottomMatsim);
				long ltx = Math.round(leftTop.getX());
				long lty = Math.round(leftTop.getY());
				long rbx = Math.round(rightBottom.getX());
				long rby = Math.round(rightBottom.getY());
				StringBuilder style = new StringBuilder();
				style.append("-fx-background-image: url(\"");
				style.append(imagePath);
				style.append("\");");
				style.append("-fx-background-position: ");
				style.append(ltx);
				style.append(" ");
				style.append(lty);
				style.append(";");
				style.append("-fx-background-repeat: stretch;");
				style.append("-fx-background-size: ");
				style.append(rbx-ltx);
				style.append(" ");
				style.append(rby-lty);
				style.append(";");
				mapPane.setStyle(style.toString());	
				if (backgroundColorPicker != null){
					backgroundColorPicker.setDisable(true);
				}
			}
		}
		
	}
	
	/**
	 * Sets the visibility of the {@link Shape} instances representing the map nodes,
	 * depending on the value of the parameter.
	 * @param visible If true, it makes the {@link Shape} instances representing the map nodes visible.
	 * Otherwise it makes them invisible.
	 */
	public void setNodesVisible(boolean visible){
		for (Node node : circles.keySet()){
			node.setVisible(visible);
		}
	}
	
	/**
	 * Sets the visibility of the {@link Shape} instances representing the map links,
	 * depending on the value of the parameter.
	 * @param visible If true, it makes the {@link Shape} instances representing the map links visible.
	 * Otherwise it makes them invisible.
	 */
	public void setLinksVisible(boolean visible){
		for (LinkCorridor corridor : linkCorridors.values()){
			corridor.getVisualization().setVisible(visible);
		}
	}
	
	/**
	 * Ensures that the {@link Shape} instances making up the map are drawn in the correct
	 * background-foreground manner, for example that there is no incorrect overlapping 
	 */
	private void moveShapesToFront(){
		for (Node node : circles.keySet()){	
			node.toFront();
		}
		for (Node person : personShapes.values()){
			person.toFront();
		}
	}
	
	/**
	 * Zooms in or zooms out the map view.
	 * @param factor By this value the current zoom factor {@link MapScene#zoom} will be multiplied.
	 */
	public void changeZoom(double factor){
		setZoom(zoom * factor);
	}
	
	/**
	 * @return Zoom factor used to view the map
	 * @see {@link MapScene#zoom}
	 */
	public double getZoom(){
		return zoom;
	}
	
	/**
	 * Sets the value of the current zoom factor. Zooms in/out, appropriately, 
	 * the container holding the scene map.
	 * @param zoom New value of the zoom factor
	 */
	public void setZoom(double zoom){
		this.zoom = zoom;
		mapContainer.setPrefHeight(originalMapHeight * zoom);
		mapContainer.setPrefWidth(originalMapWidth * zoom);		
		mapContainer.getTransforms().clear();
		Scale scale = new Scale(zoom, zoom, 0, 0);
		mapContainer.getTransforms().add(scale);
	}
	
	/**
	 * @return Snapshot of the container holding the scene map.
	 */
	public WritableImage getSnap(){
		mapContainer.setPrefHeight(originalMapHeight);
		mapContainer.setPrefWidth(originalMapWidth);
		WritableImage res = mapContainer.snapshot(null, null);
		mapContainer.setPrefHeight(originalMapHeight * zoom);
		mapContainer.setPrefWidth(originalMapWidth * zoom);
		return res;
	}
	
	/**
	 * Container for the {@link Shape} instances that represent the map elements, 
	 * such as links, nodes, vehicles.
	 */
	private final Pane mapContainer = new Pane();
	
	/**
	 * @return Container for the {@link Shape} instances that represent the map elements, 
	 * such as links, nodes, vehicles.
	 * @see {@link MapScene#mapContainer}
	 */
	public Pane getMapContainer() {
		return mapContainer;
	}
	
	/**
	 * Scrollable container for {@link MapScene#mapContainer}
	 */
	private final ScrollPane mapPane = new ScrollPane();
	
	/**
	 * Maps each ensemble membership relation to the graphical representation of this relation.
	 */
	private final Map<MembershipRelation,Node> ensembleShapes = new HashMap<>();

	public static class OtherShapeMetaData {
		
		private String eventType;
		private String nodeId;
		private double eventTime;
		
		public OtherShapeMetaData(String eventType, String nodeId, double eventTime) {
			this.eventType = eventType;
			this.nodeId = nodeId;
			this.eventTime = eventTime;
		}
		
		public String getEventType() {
			return eventType;
		}

		public String getNodeId() {
			return nodeId;
		}

		public double getEventTime() {
			return eventTime;
		}
		
		public String toString() {
			return "OtherShapeMetaData[ eventType: " + eventType + " | nodeId: " + nodeId + " | eventTime: " + eventTime + " ]";
		}
		
	}
	/**
	 * Additional shapes generated by the extensions of the map scene The key in
	 * the map holds the type of the event, in order for each extension to
	 * change only its own shapes
	 */
	private final Map<OtherShapeMetaData,Node> otherShapes = new HashMap<>();	
	
	public Map<OtherShapeMetaData,Node> getOtherShapes() {
		return otherShapes;
	}
	
	/**
	 * Fills the timeline with keyframes that enable the right movements of 
	 * people and ensembles visualizations. On top of that, it updates the 
	 * collections containing the visualizations of the map elements
	 * @param shapeProvider Used for generating the visualizations of people
	 * @param selectedPeople People whose visualizations will be updated
	 * @throws IOException When a person shape could not be loaded for any reason
	 */
	private void produceShapes(ShapeProvider shapeProvider, String[] selectedPeople) throws IOException{
		if (matsimEventsPresent){
			Collection<KeyFrame> keyFrames = buildFramesForPeople(shapeProvider, selectedPeople);
			timeLine.getKeyFrames().addAll(keyFrames);
			if (ensembleEventsPresent){
				Collection<KeyFrame> keyFrames2 = buildFramesForEnsembles();			
				timeLine.getKeyFrames().addAll(keyFrames2);
			}
			
			for (MapSceneExtensionPoint extension: mapSceneExtensions) {
				Collection<KeyFrame> keyFramesExt = extension.buildFrames(otherEvents, this);
				timeLine.getKeyFrames().addAll(keyFramesExt);
			}
		}
	}	
	
	/**
	 * Using the {@link MapScene#checkpointDb}, this method converts its contents into the format
	 * specified by JavaFX {@link Timeline} animation model. The {@link MapScene#personShapes} is 
	 * filled with the individual nodes that represent the persons. Additionally, 
	 * {@link MapScene#keyFramesForPeople} is filled with keyframes mapped to the corresponding
	 * people.  
	 * @param shapeProvider Used for generating the visualizations of people
	 * @param selectedPeople People whose visualizations will be updated
	 * @return {@link KeyFrame} instances describing the movements of people on the map.
	 * @throws IOException When a person shape could not be loaded for any reason
	 */
	private Collection<KeyFrame> buildFramesForPeople(SceneImportHandler.ShapeProvider shapeProvider, 
			String[] selectedPeople) throws IOException{
		Collection<KeyFrame> frames = new ArrayList<>();
		Collection<String> selectedPeopleCol = null;
		if ((selectedPeople != null) && (selectedPeople.length != 0)){
			selectedPeopleCol = Arrays.<String>asList(selectedPeople);
		}
		for (String personID : checkpointDb.getKeys()){
			if ((selectedPeople == null) || (selectedPeople.length == 0) || 
					(selectedPeopleCol.contains(personID))){				
				List<CheckPoint> positionCheckpoints = checkpointDb.getPositionsList(personID);
				if ((positionCheckpoints != null) && (!positionCheckpoints.isEmpty())){
					try {
						Node personShape = buildPersonShape(positionCheckpoints, shapeProvider);
						personShape.setOnMouseClicked(new InfoPanelSetter(personID, checkpointDb));
						personShape.setVisible(false);
						Collection<KeyFrame> personFrames = new ArrayList<>();
						prepareInitialFrame(personFrames, personShape);
						for (int i = 0; i < positionCheckpoints.size(); i++){
							CheckPoint cp = positionCheckpoints.get(i);
							CheckPoint nextCp;
							if ((i+1) < positionCheckpoints.size()){
								nextCp = positionCheckpoints.get(i+1);
							} else {
								nextCp = null;
							}
							processPositionCheckPoint(cp, nextCp, personFrames, personShape);
						}
						List<CheckPoint> otherCheckpoints = checkpointDb.getOthersList(personID);
						for (CheckPoint cp : otherCheckpoints){
							processOtherCheckPoint(cp, personFrames, personShape);
						}
						personShapes.put(personID, personShape);
						frames.addAll(personFrames);
						keyFramesForPeople.put(personID, personFrames);
					} catch (InitialPositionNotFoundException | IllegalArgumentException ex){
//						should not happen; prevented by the "if" statement just before the "try" block
					}
				}
			} else {
				frames.addAll(keyFramesForPeople.get(personID));
			}
		}
		return frames;
	}
	
	/**
	 * Creates the keyframes that describe the initial position and visbility of the
	 * person visualization given in the second parameter. The keyframes are added
	 * to the collection provided in the first parameter.
	 * @param personFrames Keyframes associated with a person
	 * @param personShape Visualization of the (same) person 
	 */
	private void prepareInitialFrame(Collection<KeyFrame> personFrames, Node personShape){
		DoubleProperty xProperty = personShape.translateXProperty();
		DoubleProperty yProperty = personShape.translateYProperty();
		BooleanProperty visibleProperty = personShape.visibleProperty();
		KeyValue initX = new KeyValue(xProperty, xProperty.get());
		KeyValue initY = new KeyValue(yProperty, yProperty.get());
		KeyValue initVis = new KeyValue(visibleProperty, false);
		personFrames.add(new KeyFrame(Duration.ZERO, initX, initY, initVis));
	}
	
	/**
	 * Given a checkpoint which determines a person's position, this method creates
	 * corresponding keyframes (used by the JavaFX timeline) and adds them to the provided 
	 * collection of keyframes.
	 * @param cp Checkpoint to be processed. Determines a person's position.
	 * @param nextCp The following checkpoint, presumably to be processed in the next call to this method.
	 * @param personFrames Keyframes associated with the person to whom the checkpoint belongs to
	 * @param personShape Visualization of the person to whom the checkpoint belongs to
	 */
	private void processPositionCheckPoint(CheckPoint cp, CheckPoint nextCp, 
			Collection<KeyFrame> personFrames, Node personShape){
		DoubleProperty xProperty = personShape.translateXProperty();
		DoubleProperty yProperty = personShape.translateYProperty();
		BooleanProperty visibleProperty = personShape.visibleProperty();
		Duration actualTime = new Duration(convertToVisualizationTime(cp.getTime()));
		LinkCorridor corridor = linkCorridors.get(cp.getLinkID());
		Point2D point;
		if (cp.getType() == Type.LINK_ENTERED){
			point = corridor.getFromPoint();
		} else {
			point = corridor.getToPoint();
		}
		KeyValue xVal = new KeyValue(xProperty, point.getX());
		KeyValue yVal = new KeyValue(yProperty, point.getY());
		KeyValue visibleVal = new KeyValue(visibleProperty, true);
		KeyFrame frame = new KeyFrame(actualTime, xVal, yVal, visibleVal);
		personFrames.add(frame);
		if ((nextCp != null) && (cp.getType() == Type.LINK_ENTERED)){
			Duration nextCpTime = new Duration(convertToVisualizationTime(nextCp.getTime()));
			double difference = nextCpTime.subtract(actualTime).toMillis();
			double[] relativeDistances = corridor.getRelativeDistances();
			List<Duration> intermediateTimes = new ArrayList<>();
			for (int i = 0; i < relativeDistances.length; i++){
				double time = (relativeDistances[i] * difference) + actualTime.toMillis();
				intermediateTimes.add(new Duration(time));
			}
			List<Point2D> pathPoints = corridor.getPathPoints();
			for (int i = 1; i < (pathPoints.size() - 1); i++){	// do not include start/end points
				double x = pathPoints.get(i).getX();
				xVal = new KeyValue(xProperty, x);
				double y = pathPoints.get(i).getY();
				yVal = new KeyValue(yProperty, y);
				actualTime = intermediateTimes.get(i);
				frame = new KeyFrame(actualTime, xVal, yVal);
				personFrames.add(frame);
			}
		}
	}
	
	/**
	 * Given a checkpoint which does not determine a person's position, this method creates
	 * corresponding keyframes (used by the JavaFX timeline) and adds them to the provided 
	 * collection of keyframes.
	 * @param cp Checkpoint to be processed. Does not determine a person's position.
	 * @param personFrames Keyframes associated with the person to whom the checkpoint belongs to
	 * @param personShape Visualization of the person to whom the checkpoint belongs to
	 */
	private void processOtherCheckPoint(CheckPoint cp, Collection<KeyFrame> personFrames,
			Node personShape){
		BooleanProperty visibleProperty = personShape.visibleProperty();
		Duration actualTime = new Duration(convertToVisualizationTime(cp.getTime()));
		KeyFrame frame;
		if (cp.getType() == Type.PERSON_ENTERS){
			KeyValue visibleVal = new KeyValue(visibleProperty, true);
			frame = new KeyFrame(actualTime, visibleVal);
		} else if (cp.getType() == Type.PERSON_LEAVES){
			KeyValue visibleVal = new KeyValue(visibleProperty, false);
			frame = new KeyFrame(actualTime, visibleVal);
		} else {
			throw new UnsupportedOperationException();
		}
		personFrames.add(frame);
	}
	
	/**
	 * For each person ID, stores a collection of all the key frames that capture the
	 * movements of the person visualization
	 */
	private final Map<String,Collection<KeyFrame>> keyFramesForPeople = new HashMap<>();
	
	/**
	 * It is thrown when the initial position of a person could not be determined
	 */
	@SuppressWarnings("serial")
	private static class InitialPositionNotFoundException extends Exception {}

	/**
	 * Builds a {@link Node} that represents a moving person/vehicle on the map.
	 * @param positionCheckPoints Checkpoints of the person/vehicle, defining his/its position
	 * @param provider Used for generating the visualizations of people
	 * @return A {@link Shape} that represents a moving person/vehicle on the map.
	 * @throws IOException When the shape could not be loaded for any reason
	 * @throws InitialPositionNotFoundException When the checkpoints provided by the first parameter
	 * are an empty collection
	 * @throws IllegalArgumentException When either of the method parameters are null
	 */
	private Node buildPersonShape(List<CheckPoint> positionCheckPoints, SceneImportHandler.ShapeProvider provider) 
			throws IOException, InitialPositionNotFoundException, IllegalArgumentException {
		if (provider == null){
			throw new IllegalArgumentException("Non-null ShapeProvider must be specified.");
		}
		Point2D initialPosition = getInitialPosition(positionCheckPoints);
		double x = initialPosition.getX();
		double y = initialPosition.getY();
		Node shape = provider.getNewShape();
		if (shape != null){
			shape.setTranslateX(matsimToVisual.transformX(x));
			shape.setTranslateY(matsimToVisual.transformY(y));
		}
		return shape;
	}
	
	/**
	 * Given a list of checkpoints defining the position of a person, this method
	 * returns the initial position of the person.
	 * @param positionCheckPoints Checkpoints defining the position of a person
	 * @return The initial position of the person
	 * @throws InitialPositionNotFoundException When the list provided as a parameter is empty
	 */
	private Point2D getInitialPosition(List<CheckPoint> positionCheckPoints) 
			throws InitialPositionNotFoundException{
		if (positionCheckPoints == null){
			throw new IllegalArgumentException("Non-null list of checkpoints must be supplied");
		} else {
			CheckPoint cp = positionCheckPoints.get(0);
			if (cp == null){
				throw new InitialPositionNotFoundException();
			} else {
				MyLink link = links.get(cp.getLinkID());
				MyNode node;
				if (cp.getType() == Type.LINK_ENTERED){
					node = link.getFrom();
				} else {
					node = link.getTo();
				}
				return new Point2D(node.getX(), node.getY());
			}
		}
	}
	
	/**
	 * Contains methods to retrieve preferences objects for various elements of simulated situation
	 * @see {@link PreferencesBuilder}
	 * @see {@link MapScene#getPreferences()}
	 */
	private final PreferencesBuilder preferences = new PreferencesBuilder(
			circles, links, linkCorridors, ensembleShapes);
	
	/**
	 * @return A valid instance of the object providing access to the preferences objects.
	 * Contains methods to retrieve preferences objects for various elements of simulated situation.
	 */
	public PreferencesBuilder getPreferences(){
		return preferences;
	}
	
	/**
	 * Holds the colors used for ensemble memberships
	 */
	private final Map<CoordinatorRelation,Paint> ensembleColors = new HashMap<>();

	/**
	 * Given all the ensemble events, this method creates their graphical representations in the form of 
	 * JavaFX nodes and prepares the correct movements of these nodes by binding them in the right way
	 * to the movements of the corresponding coordinators and members.  
	 * @return These key frames capture the varying visibility of the ensemble membership representations,
	 * as they disappear whenever the membership condition ceases to hold and vice versa.
	 */
	private Collection<KeyFrame> buildFramesForEnsembles(){
		Collection<KeyFrame> res = new ArrayList<>();
		EnsembleDatabase edb = new EnsembleDatabase(ensembleColors);
		for (EnsembleEvent eev : ensembleEvents){
			double timeVal = convertToVisualizationTime(eev.getTime());
			Duration time = new Duration(timeVal);
			final String coord = eev.getCoordinator();
			Node coordShape = personShapes.get(coord);
			final String member = eev.getMember();
			Node memberShape = personShapes.get(member);
			if ((coordShape != null) && (memberShape != null)){  						
				Node ensembleShape = edb.getEnsembleShape(eev.getEnsemble(), coord, member, coordShape, memberShape);				
				KeyValue kv = new KeyValue(ensembleShape.visibleProperty(), eev.getMembership());
				KeyFrame kf = new KeyFrame(time, kv);
				res.add(kf);
			} else {
				/*
				Some of the agents had no corresponding event in the interval of matsim event log which 
				we loaded so we don't know where the agent is on the map and we move on.
				*/				 
			}
		}
		ensembleShapes.clear();
		ensembleShapes.putAll(edb.getEnsembleShapes());
		for (Node line : ensembleShapes.values()){
			KeyValue kv = new KeyValue(line.visibleProperty(), Boolean.FALSE);
			KeyFrame kf = new KeyFrame(Duration.ZERO, kv);
			res.add(kf);
		}
		return res;
	}

	/**
	 * The checkpoints (position of people) as encountered when parsing the input XML 
	 * files. Contains positions of people on the map at specified times.
	 */
	private CheckPointDatabase checkpointDb;
	
	/**
	 * The ensemble events as parsed from the ensemble event log file.
	 */
	private List<EnsembleEvent> ensembleEvents;
	
	/**
	 * Changes the image which represents each person in the visualization.
	 * IMPORTANT: To be invoked before the changeNodeImage(), as it reverts the nodes to circles.
	 * @param imageName Name of the new image, or (if the next parameter is false) a path to the image. 
	 * @param isResource If true, the previous parameter specifies a resource name, else it specifies
	 * a path to a file.
	 * @param selectedPeople People whose visualizations will be updated
	 * @throws IOException When the specified image couldn't be found or read from
	 */
	public void changePeopleImage(String imageName, boolean isResource, String[] selectedPeople) throws IOException{
		if (timeLine != null){
			Duration time = timeLine.getCurrentTime();
			Status status = timeLine.getStatus();
			timeLine.stop();
			ShapeProvider provider;
			if (imageName == null){
				provider = circleProvider;
			} else {
				provider = new ImageProvider(isResource, additionalResourcesPath + imageName, null, personImageWidth, personImageWidth, 1);
			}
			update(provider, false, selectedPeople);
			if (status == Status.RUNNING){
				timeLine.playFrom(time);
			} else if (status == Status.PAUSED){
				timeLine.playFrom(time);
				timeLine.pause();
			} else {
				timeLine.stop();			
			}
		}
	}
	
	/**
	 * Changes the image which represents each node in the visualization.
	 * IMPORTANT: To be invoked after the changePersonImage().  
	 * @param imageName Name of the new image, or (if the next parameter is false) a path to the image. 
	 * @param isResource If true, the previous parameter specifies a resource name, else it specifies
	 * a path to a file.
	 * @param selectedPeople Nodes whose visualizations will be updated
	 * @throws IOException When the specified image couldn't be found or read from
	 */
	public void changeNodeImage(String imageName, boolean isResource, String[] selectedNodes) throws IOException {
		if (timeLine != null){
			Duration time = timeLine.getCurrentTime();
			Status status = timeLine.getStatus();
			timeLine.stop();
			ShapeProvider provider = null;
			if (imageName == null){
				provider = circleProvider;
			} else {
				provider = new ImageProvider(isResource, additionalResourcesPath + imageName, null, NODE_IMAGE_WIDTH, NODE_IMAGE_HEIGHT, 1);
			}
			updateNodes(null, null, provider,selectedNodes);
			if (status == Status.RUNNING){
				timeLine.playFrom(time);
			} else if (status == Status.PAUSED){
				timeLine.playFrom(time);
				timeLine.pause();
			} else {
				timeLine.stop();			
			}
		}
	}
	
	/**
	 * Preferred map width at the beginning, before any zooming takes place
	 */
	private double originalMapWidth;
	
	/**
	 * Preferred map height at the beginning, before any zooming takes place
	 */
	private double originalMapHeight;
	
	/**
	 * The tool bar containing the various zooming, pausing, forwarding etc. options
	 */
	private HBox controlsBar;
	
	/**
	 * Marks whether we are visualizing any MATSIM events, i.e. moving cars/persons.
	 * If false, the visualization only shows the map.
	 */
	private boolean matsimEventsPresent;
	
	/**
	 * Marks whether we are visualizing any ensemble membership events. If false, 
	 * just the map is shown plus, if {@link MapScene#matsimEventsPresent} is 
	 * true, the moving cars/persons.
	 */
	private boolean ensembleEventsPresent;
	
	/**
	 * Both width and height of the image that represents a person/car in the visualization
	 */
	private int personImageWidth;
	
	/**
	 * Generates the plain circles for cars/people representation
	 */
	private ShapeProvider circleProvider;
	
	/**
	 * The background of the map
	 */
	private Background background;
	
	/**
	 * The color-picker used to select the background color of the visual output
	 */
	private Node backgroundColorPicker;

	private void init(Map<String, MyNode> nodes, Map<String, MyLink> links, double mapWidth, double mapHeight,
			ChangeListener<? super Status> timeLineStatus, ChangeListener<? super Number> timeLineRate, double minTime,
			double maxTime, int duration, CheckPointDatabase checkpointDb, List<EnsembleEvent> ensembleEvents,
			HBox controlsBar, boolean matsimEventsPresent, boolean ensembleEventsPresent, int personImageWidth,
			ShapeProvider circleProvider, Background background, Node backgroundColorPicker) {
		mapPane.setContent(mapContainer);
		this.checkpointDb = checkpointDb;
		this.ensembleEvents = ensembleEvents;
		this.minTime = minTime;
		this.maxTime = maxTime;
		this.duration = duration;
		this.nodes = nodes;
		this.links.putAll(links);
		double[] borders = getMapBorders();
		double minx,miny,maxx,maxy;
		if ((background == null) || (background.getImage() == null)){
			minx = borders[0];
			miny = borders[1];
			maxx = borders[2];
			maxy = borders[3];
		} else {
			minx = Math.min(borders[0], background.getLeftTopX());
			miny = Math.min(borders[1], background.getLeftTopY());
			maxx = Math.max(borders[2], background.getRightBottomX());
			maxy = Math.max(borders[3], background.getRightBottomY());
		}
		this.originalMapWidth = mapWidth;
		this.originalMapHeight = mapHeight;
		this.controlsBar = controlsBar;
		widthFactor = (mapWidth - (constantMargin)) / (maxx - minx);
		heightFactor = (mapHeight - (constantMargin)) / (maxy - miny);
		mapContainer.setPrefSize(mapWidth, mapHeight);
		mapContainer.setId("mapContainer");
		timeLine.statusProperty().addListener(timeLineStatus);
		timeLine.rateProperty().addListener(timeLineRate);
		matsimToVisual = new MatsimToVisualCoordinates(minx, miny, widthFactor, heightFactor, constantMargin/2);
		this.matsimEventsPresent = matsimEventsPresent;
		this.ensembleEventsPresent = ensembleEventsPresent;
		this.personImageWidth = personImageWidth;
		this.circleProvider = circleProvider;
		this.background = background;
		this.backgroundColorPicker = backgroundColorPicker;
	}
	
	/**
	 * @param nodes The network nodes. Keys = node IDs, values = {@link MyNode} node representations.
	 * @param links The network links. Keys = link IDs, values = {@link MyLink} link representations.
	 * @param mapWidth Preferred width of the map view, in pixels
	 * @param mapHeight Preferred height of the map view, in pixels
	 * @param timeLineStatus Called whenever the visualization is started, paused or stopped
	 * @param timeLineRate Called whenever the visualization is sped up or down
	 * @param minTime The simulation time at which we start the visualization
	 * @param maxTime The simulation time at which we end the visualization
	 * @param duration The actual intended duration of the visualization (i.e. in visualization time)
	 * @param checkpointDb The checkpoints (positions of people) as encountered when 
	 * parsing the input XML files. Contains positions of people on the map at specified times.
	 * @param ensembleEvents The ensemble events as parsed from the ensemble event log file.
	 * @param controlsBar The tool bar containing the various zooming, pausing, forwarding etc. options
	 * @param matsimEventsPresent Marks whether we are visualizing any MATSIM events, i.e. moving cars/persons.
	 * If false, the visualization only shows the map.
	 * @param ensembleEventsPresent Marks whether we are visualizing any ensemble membership events. If false, 
	 * just the map is shown plus, if {@link MapScene#matsimEventsPresent} is 
	 * true, the moving cars/persons.
	 * @param personImageWidth Both width and height of the image that represents a person/car in 
	 * the visualization
	 * @param circleProvider Generates the plain circles for cars/people representation
	 * @param background The background of the map
	 */
	MapScene(Map<String,MyNode> nodes, Map<String,MyLink> links, double mapWidth, double mapHeight,  
			ChangeListener<? super Status> timeLineStatus, ChangeListener<? super Number> timeLineRate,
			double minTime, double maxTime, int duration, CheckPointDatabase checkpointDb, 
			List<EnsembleEvent> ensembleEvents, HBox controlsBar, boolean matsimEventsPresent,
			boolean ensembleEventsPresent, int personImageWidth, ShapeProvider circleProvider,
			Background background, Node backgroundColorPicker) {		
		init(nodes, links, mapWidth, mapHeight, timeLineStatus, timeLineRate, minTime, maxTime, duration, checkpointDb,
				ensembleEvents, controlsBar, matsimEventsPresent, ensembleEventsPresent, personImageWidth,
				circleProvider, background, backgroundColorPicker);
	}

	/**
	 * Events that can be used to extend what can be visualized in the scene
	 */
	private Map<String, List<Event>> otherEvents;

	/**
	 * Holds the extensions that allow the map scene to visualize the {@link otherEvents}
	 */
	private List<MapSceneExtensionPoint> mapSceneExtensions;
	 
	public MapScene(Map<String, MyNode> nodes, Map<String, MyLink> links, double mapWidth, double mapHeight,
			ChangeListener<? super Status> timeLineStatus, ChangeListener<? super Number> timeLineRate, double minTime,
			double maxTime, int duration, CheckPointDatabase checkpointDb, List<EnsembleEvent> ensembleEvents,
			HBox controlsBar, boolean matsimEventsPresent, boolean ensembleEventsPresent, int personImageWidth,
			ShapeProvider circleProvider, Background background, Node backgroundColorPicker,
			Map<String, List<Event>> otherEvents, List<MapSceneExtensionPoint> mapSceneExtensions) {
		init(nodes, links, mapWidth, mapHeight, timeLineStatus, timeLineRate, minTime, 
				maxTime, duration, checkpointDb, ensembleEvents, controlsBar, matsimEventsPresent, 
				ensembleEventsPresent, personImageWidth, circleProvider, background, backgroundColorPicker);
		this.otherEvents = otherEvents; 
		this.mapSceneExtensions = mapSceneExtensions;
	}
 
}
