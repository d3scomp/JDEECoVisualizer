package cz.filipekt.jdcv;

import java.util.List;
import java.util.Map;

import javafx.animation.Animation.Status;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import cz.filipekt.jdcv.SceneImportHandler.ShapeProvider;
import cz.filipekt.jdcv.checkpoints.CheckPointDatabase;
import cz.filipekt.jdcv.corridors.Background;
import cz.filipekt.jdcv.events.EnsembleEvent;
import cz.filipekt.jdcv.network.MyLink;
import cz.filipekt.jdcv.network.MyNode;

/**
 * Builder object for {@link MapScene}, as in "builder object" design pattern.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
class MapSceneBuilder {
	
	/**
	 * The network nodes. Keys = node IDs, values = {@link MyNode} node representations.
	 */
	private Map<String,MyNode> nodes;
	
	/**
	 * @param nodes The network nodes. Keys = node IDs, values = {@link MyNode} node representations.
	 * @see {@link MapSceneBuilder#nodes}
	 */
	public void setNodes(Map<String, MyNode> nodes) {
		this.nodes = nodes;
	}
	
	/**
	 * The network links. Keys = link IDs, values = {@link MyLink} link representations.
	 */
	private Map<String,MyLink> links;
	
	/**
	 * @param links The network links. Keys = link IDs, values = {@link MyLink} link representations.
	 * @see {@link MapSceneBuilder#links}
	 */
	public void setLinks(Map<String, MyLink> links) {
		this.links = links;
	}
	
	/**
	 * Preferred width of the map view, in pixels
	 */
	private double mapWidth;
	
	/**
	 * @param mapWidth Preferred width of the map view, in pixels
	 * @see {@link MapSceneBuilder#mapWidth}
	 */
	public void setMapWidth(double mapWidth) {
		this.mapWidth = mapWidth;
	}
	
	/**
	 * Preferred height of the map view, in pixels
	 */
	private double mapHeight;
	
	/**
	 * @param mapHeight Preferred height of the map view, in pixels
	 * @see {@link MapSceneBuilder#mapHeight}
	 */
	public void setMapHeight(double mapHeight) {
		this.mapHeight = mapHeight;
	}
	
	/**
	 * Called whenever the visualization is started, paused or stopped
	 */
	private ChangeListener<? super Status> timeLineStatus;
	
	/**
	 * @param timeLineStatus Called whenever the visualization is started, paused or stopped
	 * @see {@link MapSceneBuilder#timeLineStatus}
	 */
	public void setTimeLineStatus(ChangeListener<? super Status> timeLineStatus) {
		this.timeLineStatus = timeLineStatus;
	}
	
	/**
	 * Called whenever the visualization is sped up or down
	 */
	private ChangeListener<? super Number> timeLineRate;
	
	/**
	 * @param timeLineRate Called whenever the visualization is sped up or down
	 * @see {@link MapSceneBuilder#timeLineRate}
	 */
	public void setTimeLineRate(ChangeListener<? super Number> timeLineRate) {
		this.timeLineRate = timeLineRate;
	}
	
	/**
	 * The simulation time at which we start the visualization
	 */
	private double minTime;
	
	/**
	 * @param minTime The simulation time at which we start the visualization
	 * @see {@link MapSceneBuilder#minTime}
	 */
	public void setMinTime(double minTime) {
		this.minTime = minTime;
	}
	
	/**
	 * The simulation time at which we end the visualization
	 */
	private double maxTime;
	
	/**
	 * @param maxTime The simulation time at which we end the visualization
	 * @see {@link MapSceneBuilder#maxTime}
	 */
	public void setMaxTime(double maxTime) {
		this.maxTime = maxTime;
	}
	
	/**
	 * The actual intended duration of the visualization (i.e. in visualization time)
	 */
	private int duration;
	
	/**
	 * @param duration The actual intended duration of the visualization (i.e. in visualization time)
	 * @see {@link MapSceneBuilder#duration}
	 */
	public void setDuration(int duration) {
		this.duration = duration;
	}
	
	/**
	 * The checkpoints (positions of people) as encountered when 
	 * parsing the input XML files. Contains positions of people on the map at specified times.
	 */
	private CheckPointDatabase checkpointDb;
	
	/**
	 * @param checkpointDb The checkpoints (positions of people) as encountered when 
	 * parsing the input XML files. Contains positions of people on the map at specified times.
	 * @see {@link MapSceneBuilder#checkpointDb}
	 */
	public void setCheckpointDb(CheckPointDatabase checkpointDb) {
		this.checkpointDb = checkpointDb;
	}
	
	/**
	 * The ensemble events as parsed from the ensemble event log file.
	 */
	private List<EnsembleEvent> ensembleEvents;
	
	/**
	 * @param ensembleEvents The ensemble events as parsed from the ensemble event log file.
	 * @see {@link MapSceneBuilder#ensembleEvents}
	 */
	public void setEnsembleEvents(List<EnsembleEvent> ensembleEvents) {
		this.ensembleEvents = ensembleEvents;
	}
	
	/**
	 * The tool bar containing the various zooming, pausing, forwarding etc. options
	 */
	private HBox controlsBar;
	
	/**
	 * @param controlsBar The tool bar containing the various zooming, pausing, forwarding etc. options
	 * @see {@link MapSceneBuilder#controlsBar}
	 */
	public void setControlsBar(HBox controlsBar) {
		this.controlsBar = controlsBar;
	}
	
	/**
	 * Marks whether we are visualizing any MATSIM events, i.e. moving cars/persons.
	 */
	private boolean matsimEventsPresent;
	
	/**
	 * @param matsimEventsPresent Marks whether we are visualizing any MATSIM events, i.e. moving cars/persons.
	 * @see {@link MapSceneBuilder#matsimEventsPresent}
	 */
	public void setMatsimEventsPresent(boolean matsimEventsPresent) {
		this.matsimEventsPresent = matsimEventsPresent;
	}
	
	/**
	 * Marks whether we are visualizing any ensemble membership events. If false, 
	 * just the map is shown plus, if {@link MapScene#matsimEventsPresent} is 
	 * true, the moving cars/persons.
	 */
	private boolean ensembleEventsPresent;
	
	/**
	 * @param ensembleEventsPresent Marks whether we are visualizing any ensemble membership events. 
	 * If false, just the map is shown plus, if {@link MapScene#matsimEventsPresent} is true, 
	 * the moving cars/persons.
	 * @see {@link MapSceneBuilder#ensembleEventsPresent}
	 */
	public void setEnsembleEventsPresent(boolean ensembleEventsPresent) {
		this.ensembleEventsPresent = ensembleEventsPresent;
	}
	
	/**
	 * Both width and height of the image that represents a person/car in 
	 * the visualization
	 */
	private int personImageWidth;
	
	/**
	 * @param personImageWidth Both width and height of the image that represents a person/car in 
	 * the visualization
	 * @see {@link MapSceneBuilder#personImageWidth}
	 */
	public void setPersonImageWidth(int personImageWidth) {
		this.personImageWidth = personImageWidth;
	}
	
	/**
	 * Generates the plain circles for cars/people representation
	 */
	private ShapeProvider circleProvider;
	
	/**
	 * @param circleProvider Generates the plain circles for cars/people representation
	 * @see {@link MapSceneBuilder#circleProvider}
	 */
	public void setCircleProvider(ShapeProvider circleProvider) {
		this.circleProvider = circleProvider;
	}
	
	/**
	 * The background of the map 
	 */
	private Background background;
	
	/**
	 * @param background The background of the map
	 * @see {@link MapSceneBuilder#background}
	 */
	public void setBackground(Background background) {
		this.background = background;
	}
	
	/**
	 * The color-picker used to select the background color of the visual output
	 */
	private Node backgroundColorPicker;

	/**
	 * @param backgroundColorPicker The color-picker used to select the background 
	 * color of the visual output
	 * @see {@link MapSceneBuilder#backgroundColorPicker}
	 */
	public void setBackgroundColorPicker(Node backgroundColorPicker) {
		this.backgroundColorPicker = backgroundColorPicker;
	}

	/**
	 * Builds and returns a {@link MapScene} instance using the parameter values collected by this class. 
	 */
	public MapScene build(){
		return new MapScene(nodes, links, mapWidth, mapHeight, timeLineStatus, timeLineRate, minTime, 
				maxTime, duration, checkpointDb, ensembleEvents, controlsBar, matsimEventsPresent, 
				ensembleEventsPresent, personImageWidth, circleProvider, background, backgroundColorPicker);
	}
}
