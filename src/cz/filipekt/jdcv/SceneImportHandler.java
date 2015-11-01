package cz.filipekt.jdcv;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import cz.filipekt.jdcv.checkpoints.CheckPoint;
import cz.filipekt.jdcv.checkpoints.CheckPoint.Type;
import cz.filipekt.jdcv.checkpoints.CheckPointDatabase;
import cz.filipekt.jdcv.events.EnsembleEvent;
import cz.filipekt.jdcv.events.EnteredOrLeftLink;
import cz.filipekt.jdcv.events.EntersOrLeavesVehicle;
import cz.filipekt.jdcv.events.EventType;
import cz.filipekt.jdcv.events.MatsimEvent;
import cz.filipekt.jdcv.network.MyLink;
import cz.filipekt.jdcv.util.BigFilesSearch;
import cz.filipekt.jdcv.util.BigFilesSearch.ElementTooLargeException;
import cz.filipekt.jdcv.util.BigFilesSearch.SelectionTooBigException;
import cz.filipekt.jdcv.util.Dialog;
import cz.filipekt.jdcv.util.Resources;
import cz.filipekt.jdcv.xml.BackgroundHandler;
import cz.filipekt.jdcv.xml.CorridorHandler;
import cz.filipekt.jdcv.xml.EnsembleHandler;
import cz.filipekt.jdcv.xml.LinkHandler;
import cz.filipekt.jdcv.xml.MatsimEventHandler;
import cz.filipekt.jdcv.xml.JDEECoEventHandler;
import cz.filipekt.jdcv.xml.NodeHandler;
import cz.filipekt.jdcv.xml.XMLextractor;
import javafx.animation.Animation.Status;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

/**
 * Listener for the event that the user requests a visualization scene to be
 * created. Makes sure that when the OK button (on the import-scene page) is 
 * clicked, the input data is processed and the requested visualization is shown.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
class SceneImportHandler implements EventHandler<ActionEvent>{
	
	/**
	 * The three fields containing the paths to the source XML files.
	 * The first one holds paths to the network definition, the second 
	 * to the event log, the third to the ensemble event log.
	 */
	private final List<TextField> pathFields;
	
	/**
	 * The {@link Button} with which this {@link EventHandler} is associated. 
	 */
	private final Button okButton;
	
	/**
	 * The {@link CheckBox} that allows the user to select whether to show just those
	 * elements in the map that correspond to the injected JDEECo agents 
	 */
	private final CheckBox onlyAgentsBox;

	/**
	 * The {@link CheckBox} for specifying whether only the event log expects
	 * matsim events or jDEECo events
	 */
	private final CheckBox matsimLog;
	
	/**
	 * Shown when a new scene is being loaded.
	 */
	private final ProgressIndicator progIndicator = new ProgressIndicator(-1);
	
	/**
	 * The {@link GridPane} which contains {@link SceneImportHandler#progIndicator} 
	 * and {@link SceneImportHandler#okButton}.
	 */
	private final GridPane pane;
	
	/**
	 * The {@link Visualizer} that will show the network that has been submitted by 
	 * clicking the OK button.
	 */
	private final Visualizer visualizer;
	
	/**
	 * Called whenever the visualization is started, paused or stopped
	 */
	private final ChangeListener<Status> timeLineStatus;
	
	/**
	 * Called whenever the visualization is sped up or down
	 */
	private final ChangeListener<Number> timeLineRate;
	
	/**
	 * The combo boxes selecting the character encoding of the input text files
	 */
	private final List<ComboBox<String>> charsetBoxes;

	/**
	 * @param pathFields
	 *            Text fields containing the paths to the source XML files.
	 * @param okButton
	 *            The button with which this {@link EventHandler} is associated.
	 * @param onlyAgents
	 *            The {@link CheckBox} that allows the user to select whether to
	 *            show just those elements in the map that correspond to the
	 *            injected JDEECo agents
	 * @param matsimLog
	 *            The {@link CheckBox} for specifying whether only the event log
	 *            expects matsim events or jDEECo events
	 * @param pane
	 *            The {@link GridPane} which contains
	 *            {@link SceneImportHandler#progIndicator} and
	 *            {@link SceneImportHandler#okButton}.
	 * @param visualizer
	 *            The {@link Visualizer} that will show the network that has
	 *            been submitted by clicking the OK button.
	 * @param durationField
	 *            The field specifying the duration of the visualization
	 * @param timeLineStatus
	 *            Called whenever the visualization is started, paused or
	 *            stopped
	 * @param timeLineRate
	 *            Called whenever the visualization is sped up or down
	 * @param startAtField
	 *            The field specifying the time (simulation time) at which the
	 *            visualization should begin.
	 * @param endAtField
	 *            The field specifying the time (simulation time) at which the
	 *            visualization should end.
	 * @param charsetBoxes
	 *            The combo boxes selecting the character encoding of the input
	 *            text files
	 * @throws NullPointerException
	 *             When any of the parameters if null
	 */
	public SceneImportHandler(List<TextField> pathFields, Button okButton, CheckBox onlyAgents, CheckBox matsimLog,
			GridPane pane, Visualizer visualizer, TextField durationField, ChangeListener<Status> timeLineStatus,
			ChangeListener<Number> timeLineRate, TextField startAtField, TextField endAtField,
			List<ComboBox<String>> charsetBoxes) throws NullPointerException {
		if ((pathFields == null) || (okButton == null) || (onlyAgents == null) || (pane == null) ||
				(visualizer == null) || (durationField == null) || (timeLineStatus == null) ||
				(timeLineRate == null) || (startAtField == null) || (endAtField == null)){
			throw new NullPointerException();
		}
		this.pathFields = pathFields;
		this.okButton = okButton;
		this.onlyAgentsBox = onlyAgents;
		this.matsimLog = matsimLog;
		this.pane = pane;
		this.visualizer = visualizer;
		this.durationField = durationField;
		this.timeLineStatus = timeLineStatus;
		this.timeLineRate = timeLineRate;
		this.startAtField = startAtField;
		this.endAtField = endAtField;
		this.charsetBoxes = charsetBoxes;
	}
	
	/**
	 * The {@link TextField} specifying the time (simulation time) at which the 
	 * visualization should begin.
	 */
	private final TextField startAtField;
	
	/**
	 * The {@link TextField} specifying the time (simulation time) at which the 
	 * visualization should end.
	 */
	private final TextField endAtField;
	
	/**
	 * The field specifying the duration of the visualization
	 */
	private final TextField durationField;

	/**
	 * Adds {@link SceneImportHandler#progIndicator} to the {@link SceneImportHandler#pane} container.
	 */
	private void openProgressIndicator(){
		int column = GridPane.getColumnIndex(okButton);
		int row = GridPane.getRowIndex(okButton);				
		pane.add(progIndicator, column, row);	
	}
	
	/**
	 * Removes {@link SceneImportHandler#progIndicator} from the 
	 * {@link SceneImportHandler#pane} container.
	 */
	private void closeProgressIndiciator(){
		pane.getChildren().remove(progIndicator);
	}
	
	/**
	 * Reports any problems encountered by the {@link SceneImportHandler#handle} method and then tries to
	 * create a new {@link MapScene} by calling {@link SceneImportHandler#prepareNewScene}. If any problem
	 * is encountered, details are shown to the user and the application is terminated.
	 * @param problems Problems encountered by {@link SceneImportHandler#handle}
	 * @param onlyAgents Value of the checkbox specifying whether only JDEECo agents should be shown
	 * @param startAt Value of the field specifying the simulation time at which visualization should start
	 * @param endAt Value of the field specifying the simulation time at which visualization should end
	 * @param duration Value of the field specifying the total duration of the visualization
	 */
	private void reportProblemsForScene(List<String> problems, final boolean onlyAgents, 
			final Double startAt, final Double endAt, final int duration){
		if (problems.size() > 0){
			StringBuilder sb = new StringBuilder();
			for (String problem : problems){
				sb.append(problem);
				sb.append("\n");
			}
			Dialog.show(cz.filipekt.jdcv.util.Dialog.Type.ERROR, "Some problems were encountered:", 
					sb.toString());
		} else {
			new Thread(){

				@Override
				public void run() {
					try {	
						Platform.runLater(new Runnable() {
							
							@Override
							public void run() {								
								openProgressIndicator();
							}
						});							
						try {
							prepareNewScene(onlyAgents, startAt, endAt, duration);
						} finally {
							Platform.runLater(new Runnable() {
								
								@Override
								public void run() {
									closeProgressIndiciator();
									// we have prepared the scene, now we change the graphics
									Console.getInstance().executeStartupScripts(visualizer);
									// we changed the graphics, now we set the default values to the scene
									Visualizer.getInstance().toggleLinksVisibility();
								} 
							});
							Platform.runLater(new Runnable() {
								
								@Override
								public void run() {
									visualizer.setEnded();
								}
							});
						}
					} catch (IOException ex){						
						reportError("Could not read from one of the input files:", ex.getMessage());
					} catch (ParserConfigurationException ex) {						
						reportError("A problem with XML parser configuration has been encountered:",
								ex.getMessage());
					} catch (SAXException ex) {
						if (ex.getException() == null){
							String message = ex.getMessage();
							reportError("A problem in syntax of one of the XML input files has been encountered:", 
									message);
						} else {
							if ((ex.getException().getMessage() != null) && 
									(!ex.getException().getMessage().isEmpty())){
								String message = ex.getException().getMessage();
								String[] messages = message.split("\n");
								String[] messages2 = new String[messages.length+1];
								messages2[0] = "A problem in syntax of one of the XML input files has been encountered:";
								for (int i = 0; i < messages.length; i++){
									messages2[i+1] = messages[i];
								}
								reportError(messages2);
							} else {
								String message = ex.getException().toString();
								reportError("A problem in syntax of one of the XML input files has been encountered:", 
										message);
							}
						}
					} catch (SelectionTooBigException e) {
						reportError("The selected time interval is too large to handle.");
					} catch (ElementTooLargeException e) {
						reportError("An event element in the Matsim event log is too large.",
								"Contact the application developer.");
					}
				}
				
			}.start();
		}
	}
	
	/**
	 * Shows the error dialog using {@link Dialog} in the JavaFX application thread
	 * @param messages The error messages to show
	 */
	private void reportError(final String... messages){
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				Dialog.show(Dialog.Type.ERROR, messages);
			}
		});
	}
	
	/**
	 * Called when {@link SceneImportHandler#okButton} is clicked. Makes sure that the input fields 
	 * contain data. Further processing is delegated to {@link SceneImportHandler#reportProblemsForScene}
	 */
	@Override
	public void handle(ActionEvent event) {
		Visualizer.getInstance().setStarted();
		TextField networkField = pathFields.get(0);
		if ((networkField == null) || (networkField.getText() == null) || (networkField.getText().isEmpty())){
			Dialog.show(cz.filipekt.jdcv.util.Dialog.Type.INFO, 
					"Path to the network definition XML file must be specified.");
			return;
		}			
		determineSpecifiedFiles();
		boolean onlyAgents = onlyAgentsBox.isSelected();
		List<String> problems = new ArrayList<>();
		String startAtText = startAtField.getText();
		Double startAt = null;
		try {
			if ((startAtText != null) && (!startAtText.isEmpty())){
				startAt = Double.valueOf(startAtText);
			}

		} catch (NumberFormatException ex){
			problems.add("The \"Start at\" field may only contain an integer number or nothing.");
		}
		String endAtText = endAtField.getText();
		Double endAt = null;
		try {
			if ((endAtText != null) && (!endAtText.isEmpty())){
				endAt = Double.valueOf(endAtText);
			}
		} catch (NumberFormatException ex) {
			problems.add("The \"End at\" field may only contain an integer number or nothing.");
		}
		String durationText = durationField.getText();
		int duration = 60;
		if (eventsPresent){
			try {
				if ((durationText != null) && (!durationText.isEmpty())){
					duration = Integer.parseInt(durationText);
				}
			} catch (NumberFormatException ex){
				problems.add("The \"Target duration\" field must contain an integer number.");
			}
		}
		reportProblemsForScene(problems, onlyAgents, startAt, endAt, duration);
	}
	
	/**
	 * Checks which input fields have been filled in, out of the three specifying the XML input files.
	 * The (un)availability is recorded to {@link SceneImportHandler#eventsFilePresent} and
	 * {@link SceneImportHandler#ensembleFilePresent}
	 */
	private void determineSpecifiedFiles(){
		TextField eventField = pathFields.get(1);
		if ((eventField == null) || (eventField.getText() == null) || eventField.getText().isEmpty()){
			eventsFilePresent = false;
		} else {
			eventsFilePresent = true;
		}
		TextField ensembleField = pathFields.get(2);
		if ((!eventsFilePresent) || (ensembleField == null) || (ensembleField.getText() == null) || 
				ensembleField.getText().isEmpty()){
			ensembleFilePresent = false;
		} else {
			ensembleFilePresent = true;
		}	
	}
	
	/**
	 * If true, a file containing the MATSIM or the jDEECo event log has been specified by the user.
	 */
	private boolean eventsFilePresent;

	/**
	 * If true, a file containing the ensemble event log has been specified by the user.
	 */
	private boolean ensembleFilePresent;
	
	/**
	 * If true, there are MATSIM or jDEECo events 
	 */
	private boolean eventsPresent;
	
	/**
	 * If true, there are ensemble events
	 */
	private boolean ensembleEventsPresent;
	
	/**
	 * The ration by which the visualized scene is shrunken so that the scroll-pane
	 * is not in the scrolling mode by default  
	 */
	private final double mapScrollMargin = 0.95;
	
	/**
	 * Minimum time value shown at the application timeline. It is computed as the
	 * maximum of values (minimal time in the event logs, "from time" input value)
	 */
	private double minTime;
	
	/**
	 * Maximum time value shown at the application timeline. It is computed as the
	 * minimum of values (maximal time in the event logs, "to time" input value)
	 */
	private double maxTime;
	
	/**
	 * The partially parsed data from the event logs
	 */
	private CheckPointDatabase cdb;
	
	/**
	 * Parsed event elements from the ensemble event log
	 */
	private List<EnsembleEvent> ensembleEvents;
	
	/** 
	 * If true, then links are visualized upon creation of the new scene 
	 */
	private boolean isLinksVisible;
	
	public void setIsLinksVisible(boolean isLinksVisible) {
		this.isLinksVisible = isLinksVisible;
	}
	
	/**
	 * Creates a {@link MapScene} representation of the data provided by the input
	 * files. The instance is then given to the {@link Visualizer} instance which
	 * takes care of the actual visualization.
	 * @param onlyAgents Value of the checkbox specifying whether only JDEECo agents should be shown
	 * @param startAt Value of the field specifying the simulation time at which visualization should start
	 * @param endAt Value of the field specifying the simulation time at which visualization should end
	 * @param duration Value of the field specifying the total duration of the visualization
	 * @throws ParserConfigurationException Should never happen
	 * @throws SAXException When there is any problem when parsing the XML document. 
	 * It is generally used as a wrapper for other kinds of exceptions.
	 * @throws IOException If the source XML file, specified by a method parameter, 
	 * does not exist or is inaccessible.
	 * @throws ElementTooLargeException If an event element in the Matsim event log is too large
	 * @throws SelectionTooBigException If the selected time interval is too large to handle 
	 */
	private void prepareNewScene(boolean onlyAgents, Double startAt, Double endAt, int duration) 
			throws ParserConfigurationException, SAXException, IOException, SelectionTooBigException, 
			ElementTooLargeException {
		TextField networkField = pathFields.get(0);
		TextField eventField = pathFields.get(1);
		TextField ensembleField = pathFields.get(2);	
		Path networkFile = Paths.get(networkField.getText());
		String networkFileEncoding = charsetBoxes.get(0).getSelectionModel().getSelectedItem();
		NodeHandler nodeHandler = new NodeHandler();
		XMLextractor.run(networkFile, networkFileEncoding, nodeHandler);
		LinkHandler linkHandler = new LinkHandler(nodeHandler.getNodes());
		XMLextractor.run(networkFile, networkFileEncoding, linkHandler);
		CorridorHandler corridorHandler = new CorridorHandler(linkHandler.getLinks());
		XMLextractor.run(networkFile, networkFileEncoding, corridorHandler);
		BackgroundHandler backgroundHandler = new BackgroundHandler();
		XMLextractor.run(networkFile, networkFileEncoding, backgroundHandler);
		retrieveEventsData(onlyAgents, startAt, endAt, eventField, ensembleField, linkHandler.getLinks());
		ShapeProvider circleProvider = new CircleProvider(personCircleRadius, personCircleColor);
		MapSceneBuilder sceneBuilder = new MapSceneBuilder();
		sceneBuilder.setNodes(nodeHandler.getNodes());
		sceneBuilder.setLinks(linkHandler.getLinks());
		sceneBuilder.setMapWidth(visualizer.getMapWidth() * mapScrollMargin);
		sceneBuilder.setMapHeight(visualizer.getMapHeight() * mapScrollMargin);
		sceneBuilder.setTimeLineStatus(timeLineStatus);
		sceneBuilder.setTimeLineRate(timeLineRate);
		sceneBuilder.setMinTime(minTime);
		sceneBuilder.setMaxTime(maxTime);
		sceneBuilder.setDuration(duration);
		sceneBuilder.setCheckpointDb(cdb);
		sceneBuilder.setEnsembleEvents(ensembleEvents);
		sceneBuilder.setControlsBar(visualizer.getControlsBar());
		sceneBuilder.setMatsimEventsPresent(eventsPresent);
		sceneBuilder.setEnsembleEventsPresent(ensembleEventsPresent);
		sceneBuilder.setPersonImageWidth(8 * personCircleRadius);
		sceneBuilder.setCircleProvider(circleProvider);
		sceneBuilder.setBackground(backgroundHandler.getResult());
		sceneBuilder.setBackgroundColorPicker(visualizer.getBackgroundColorPicker());
		final MapScene scene = sceneBuilder.build();
		scene.update(circleProvider, false, null);
		Platform.runLater(new Runnable() {

			@Override
			public void run() {			
				visualizer.setScene(scene, eventsPresent);					
			}
		});		
	}
	
	/**
	 * Parses and retrieves the data from the Matsim and ensembles event logs
	 * @param onlyAgents Value of the checkbox specifying whether only JDEECo agents should be shown
	 * @param startAt Value of the field specifying the simulation time at which visualization should start
	 * @param endAt Value of the field specifying the simulation time at which visualization should end
	 * @param eventField The GUI input field for entering the Matsim event log file path
	 * @param ensembleField The GUI input field for entering the ensemble event log file path
	 * @param links The parsed link elements, indexed by their link IDs
	 * @throws IOException If the source XML file, specified by a method parameter, 
	 * does not exist or is inaccessible.
	 * @throws SelectionTooBigException If the selected time interval is too large to handle 
	 * @throws ElementTooLargeException If an event element in the Matsim event log is too large
	 * @throws ParserConfigurationException Should never happen
	 * @throws SAXException When there is any problem when parsing the XML document. 
	 * It is generally used as a wrapper for other kinds of exceptions.
	 */
	private void retrieveEventsData(boolean onlyAgents, Double startAt, Double endAt, 
			TextField eventField, TextField ensembleField, Map<String,MyLink> links) 
					throws IOException, SelectionTooBigException, ElementTooLargeException, 
					ParserConfigurationException, SAXException{
		if(eventsFilePresent){
			Path eventsFile = Paths.get(eventField.getText());
			String eventsFileEncoding = charsetBoxes.get(1).getSelectionModel().getSelectedItem();
			List<MatsimEvent> events;
			InputStream eventsStream = getEventLogStream(eventsFile, eventsFileEncoding, startAt, endAt);
			
			if (matsimLog.isSelected()) { // matsim events expected, ensembles in separate file 
				
				if (ensembleFilePresent){ 
					Path ensembleFile = Paths.get(ensembleField.getText());
					String ensembleFileEncoding = charsetBoxes.get(2).getSelectionModel().getSelectedItem();
					EnsembleHandler ensembleHandler = new EnsembleHandler(startAt, endAt);
					XMLextractor.run(ensembleFile, ensembleFileEncoding, ensembleHandler);
					ensembleEvents = ensembleHandler.getEvents();
					ensembleEventsPresent = !ensembleEvents.isEmpty();
				} else {
					ensembleEventsPresent = false;
				}
				
				MatsimEventHandler matsimEventHandler = new MatsimEventHandler(links, onlyAgents, startAt, endAt);
				XMLextractor.run(eventsStream, eventsFileEncoding, matsimEventHandler);
				events = matsimEventHandler.getEvents();
				
			} else { // a single file containing jDEECo events and ensemble events is expected
				
				Path ensembleFile = Paths.get(eventField.getText());
				String ensembleFileEncoding = charsetBoxes.get(1).getSelectionModel().getSelectedItem();
				EnsembleHandler ensembleHandler = new EnsembleHandler(startAt, endAt);
				XMLextractor.run(ensembleFile, ensembleFileEncoding, ensembleHandler);
				ensembleEvents = ensembleHandler.getEvents();
				ensembleEventsPresent = !ensembleEvents.isEmpty();
								
				JDEECoEventHandler jDEECoEventHandler = new JDEECoEventHandler(links, onlyAgents, startAt, endAt);
				XMLextractor.run(eventsStream, eventsFileEncoding, jDEECoEventHandler);
				events = jDEECoEventHandler.getEvents();
			}
			
			cdb = buildCheckPointDatabase(events);
			eventsPresent = !events.isEmpty();
			
			if (startAt == null){
				minTime = cdb.getMinTime();
			} else {
				minTime = Math.max(startAt * 1.0, cdb.getMinTime());
			}
			if (endAt == null){
				maxTime = cdb.getMaxTime();
			} else {
				maxTime = Math.min(endAt * 1.0, cdb.getMaxTime());
			}
			
		} else {
			ensembleEvents = null;
			minTime = 0;
			maxTime = 0;
			cdb = null;
		}
	}
	
	/**
	 * Radius of the circle representing a person in the visualization
	 */
	private final int personCircleRadius = 3;
	
	/**
	 * Color of the circle representing a person in the visualization
	 */
	private final Paint personCircleColor = Color.LIME;
	
	/**
	 * If the event log file is at most this big, it is parsed as a whole.
	 * If the file is larger, intelligent searching is used.
	 */
	private final long eventLogFileThreashold = 10L * 1024L * 1024L;
	
	/**
	 * Returns an input stream opened on a possibly modified version of the XML document given in 
	 * the first parameter. If the XML file is small enough, the returned stream is simply opened 
	 * on the file. If the XML file is larger than the threshold {@link SceneImportHandler#eventLogFileThreashold}, 
	 * the stream is opened on a modified version of the XML document. In this modified version, most of the 
	 * event elements that do not belong to the time interval specified in the parameters are discarded, 
	 * i.e. the resulting document may be much smaller than the whole original document.
	 * For more info about what "most of the elements" means, see {@link BigFilesSearch}
	 * @see {@link BigFilesSearch#getSectionWellFormed(double, double)} 
	 * @param eventLog The Matsim event log file
	 * @param encoding Character encoding set used by the Matsim event log file
	 * @param fromTime If not null, specifies the lower bound of the desired time interval. If null,
	 * no lower bound is given.
	 * @param toTime If not null, specifies the upper bound of the desired time interval. If null,
	 * no upper bound is given
	 * @return Stream opened on a possibly modified version of the Matsim event log file
	 * @throws IOException If it is impossible to read from the Matsim event log file
	 * @throws ElementTooLargeException If some event element is too large
	 * @throws SelectionTooBigException If the specified time interval is too large
	 */
	private InputStream getEventLogStream(Path eventLog, String encoding, Double fromTime, Double toTime) 
			throws IOException, SelectionTooBigException, ElementTooLargeException{	
		if (Files.exists(eventLog)){
			if (Files.size(eventLog) <= eventLogFileThreashold){
				return Files.newInputStream(eventLog);
			} else {
				Charset charset = Charset.forName(encoding);
				BigFilesSearch bfs = new BigFilesSearch(eventLog, charset);
				String document = bfs.getSectionWellFormed(fromTime, toTime);
				byte[] docBytes = document.getBytes(encoding);
				return new ByteArrayInputStream(docBytes);
			}
		} else {
			throw new IOException("Event log file does not exist.");
		}
	}
	
	/**
	 * Producer of {@link Node} instances, later used for visualizing persons or cars 
	 */
	public static interface ShapeProvider {
		
		/**
		 * @return A new instance of {@link Node}, 
		 * later used for visualizing persons or cars 
		 * @throws IOException When the shape could not be loaded for any reason
		 */
		Node getNewShape() throws IOException;
	}
	
	/**
	 * Producer of {@link ImageView} instances,
	 * later used for visualizing persons or cars 
	 */
	static class ImageProvider implements ShapeProvider {
		
		/**
		 * Name of the resource file containing the image
		 */
		private final String image;
		
		/**
		 * Width of the provided image
		 */
		private final int imageWidth;
		
		/**
		 * Height of the provided image
		 */
		private final int imageHeight;
		
		/**
		 * If true, the specified image is looked for in the application resources.
		 * Otherwise the image is looked for in the filesystem. 
		 */
		private final boolean isResource;

		/**
		 * @param isResource If true, the image specified by the second parameter, is looked 
		 * for in the application resources. 
		 * @param image If the first parameter holds, this parameter specifies the resource name. Otherwise,
		 * this parameter contains a full path to the specified image.
		 * @param imageWidth Width (a also height) of the provided image
		 * @throws FileNotFoundException When the image could not be found
		 */
		public ImageProvider(boolean isResource, String image, int imageWidth, int imageHeight) throws FileNotFoundException {
			this.image = image;
			this.imageWidth = imageWidth;
			this.imageHeight = imageHeight;
			this.isResource = isResource;
			try {
				getNewShape();
			} catch (IOException ex){
				throw new FileNotFoundException();
			}
		}

		/**
		 * @return The specified image, or null if the image was not found.
		 * @throws IOException When the shape could not be loaded for any reason
		 */
		@Override
		public Node getNewShape() throws IOException {
			final ImageView res;
			if (isResource){
				res = Resources.getImageView(image, imageWidth);
			} else {
				InputStream stream = Files.newInputStream(Paths.get(image));
				Image image = new Image(stream, imageWidth, imageHeight, true, false);
				res = new ImageView(image);
			}
			res.setLayoutX(-(imageWidth/2));
			res.setLayoutY(-(imageHeight/2));
			res.setOnMouseEntered(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent arg0) {
					res.setScaleX(1.5);
					res.setScaleY(1.5);
				}
			});
			res.setOnMouseExited(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent arg0) {
					res.setScaleX(1);
					res.setScaleY(1);
				}
			});
			return res;
		}
		
	}
	
	/**
	 * Producer of {@link Circle} instances,
	 * later used for visualizing persons or cars 
	 */
	static class CircleProvider implements ShapeProvider {
		
		/**
		 * Radius of the circle
		 */
		private final double radius;
		
		/**
		 * Color of the circle
		 */
		private final Paint color;

		/**
		 * @param radius Radius of the circle
		 * @param color Color of the circle
		 */
		public CircleProvider(double radius, Paint color) {
			this.radius = radius;
			this.color = color;
		}
		
		/**
		 * @return A new instance of {@link Circle}, 
		 * later used for visualizing persons or cars 
		 */
		@Override
		public Node getNewShape() {
			final Circle circle = new Circle(0, 0, radius, color);
			circle.setOnMouseEntered(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent arg0) {
					circle.setRadius(radius * 2);
				}
			});
			circle.setOnMouseExited(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent arg0) {
					circle.setRadius(radius);
				}
			});
			return circle;
		}
		
	}
	
	/**
	 * Given the {@link MatsimEvent} instances parsed from the event log, this method extracts the
	 * information about the positions of people on the map at specified times. This information
	 * is then returned packaged in the {@link CheckPointDatabase} instance.
	 * @param events The {@link MatsimEvent} instances parsed from the event log.
	 * @return {@link CheckPointDatabase} instance containing information about the positions of
	 * people on the map at specified times.
	 */
	private CheckPointDatabase buildCheckPointDatabase(List<MatsimEvent> events){
		CheckPointDatabase db = new CheckPointDatabase();
		for (MatsimEvent event : events){
			String personID = event.getPerson();
			double time = event.getTime();
			if (event.getType() == EventType.PERSON_ENTERS_VEHICLE){	
				EntersOrLeavesVehicle elv = (EntersOrLeavesVehicle)event;
				String vehicleID = elv.getVehicleId();
				CheckPoint cp = new CheckPoint(time, personID, vehicleID, Type.PERSON_ENTERS);
				db.add(personID, cp);
				db.setInVehicle(personID, vehicleID);
			} else if (event.getType() == EventType.PERSON_LEAVES_VEHICLE){
				EntersOrLeavesVehicle elv = (EntersOrLeavesVehicle)event;
				String vehicleID = elv.getVehicleId();
				CheckPoint cp = new CheckPoint(time, personID, vehicleID, Type.PERSON_LEAVES);
				db.add(personID, cp);
				db.setInVehicle(personID, null);
			} else if ((event.getType() == EventType.ENTERED_LINK) || 
					(event.getType() == EventType.LEFT_LINK)){
				Type type;
				if (event.getType() == EventType.ENTERED_LINK){
					type = Type.LINK_ENTERED;
				} else {
					type = Type.LINK_LEFT;
				}
				String vehicleID = db.getInVehicle(personID);
				EnteredOrLeftLink ell = (EnteredOrLeftLink)event;					
				MyLink link = ell.getLink();
				boolean justDeparted = db.getJustDeparted(personID);
				if (justDeparted && (vehicleID == null)){
					vehicleID = ell.getVehicleId();
					CheckPoint cp = new CheckPoint(time, personID, vehicleID, Type.PERSON_ENTERS);
					db.add(personID, cp);
					db.setInVehicle(personID, vehicleID);
				}
				CheckPoint cp = new CheckPoint(link.getId(), time, personID, vehicleID, type);
				db.add(personID, cp);
				db.setJustDeparted(personID, false);
			} else if (event.getType() == EventType.DEPARTURE){
				db.setJustDeparted(personID, true);
			} else if (event.getType() == EventType.ARRIVAL){
				CheckPoint cp = new CheckPoint(time, personID, null, Type.PERSON_LEAVES);
				db.add(personID, cp);
			}
		}
		return db;
	}
		
}
