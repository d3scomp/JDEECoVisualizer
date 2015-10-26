package cz.filipekt.jdcv.plugins.filter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cz.filipekt.jdcv.plugins.PluginWithPreferences;
import cz.filipekt.jdcv.plugins.filter.filters.EnsembleCoordFilter;
import cz.filipekt.jdcv.plugins.filter.filters.EnsembleMemberFilter;
import cz.filipekt.jdcv.plugins.filter.filters.EnsembleNameFilter;
import cz.filipekt.jdcv.plugins.filter.filters.LinkFromFilter;
import cz.filipekt.jdcv.plugins.filter.filters.LinkIDFilter;
import cz.filipekt.jdcv.plugins.filter.filters.LinkToFilter;
import cz.filipekt.jdcv.plugins.filter.filters.NodeIDFilter;
import cz.filipekt.jdcv.plugins.filter.filters.NodeXFilter;
import cz.filipekt.jdcv.plugins.filter.filters.NodeYFilter;
import cz.filipekt.jdcv.prefs.LinkPrefs;
import cz.filipekt.jdcv.prefs.MembershipPrefs;
import cz.filipekt.jdcv.prefs.NodePrefs;
import cz.filipekt.jdcv.prefs.PreferencesBuilder;
import cz.filipekt.jdcv.util.Resources;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * The side panel allowing the user to filter out some of the visualization 
 * elements. Only simple filters are supported - for more elaborate ones, 
 * use the scripting console. 
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class FilterPanel extends PluginWithPreferences {
	
	/**
	 * Short name of this plugin
	 */
	private final String pluginName = "Filter";

	/**
	 * @return Short name of this plugin
	 */
	@Override
	public String getName() {
		return pluginName;
	}

	/**
	 * @return Input stream opened on a file containing a small image representing this plugin
	 * @throws IOException If the resource couldn't be found
	 */
	@Override
	public InputStream getThumbnail() throws IOException {
		return Resources.getResourceInputStream("filter.png");
	}

	/**
	 * @return The main panel of the plugin
	 * @see {@link FilterPanel#panel}
	 */
	@Override
	public Node getPanel() {
		return panel;
	}
	
	/**
	 * The main panel of the plugin
	 */
	private final Node panel;

	/**
	 * This is a singleton class, so uncontrolled instantiation is forbidden.
	 * Initializes the main panel.
	 */
	public FilterPanel() {
		panel = createPanel();
		initializeAttributes();
	}
	
	/**
	 * The graphical view of the filters that are currently in use
	 */
	private final ListView<Filter> filters = new ListView<>();
	
	/**
	 * Constructs the main panel of the plugin, later held in {@link FilterPanel#panel}
	 * @return The main panel of the plugin
	 */
	private Node createPanel(){
		VBox vGrid = new VBox();
		Button addButton = new Button("Add Filter");
		Button removeButton = new Button("Remove Selected");
		HBox buttons = new HBox();
		buttons.getChildren().addAll(addButton, removeButton);
		VBox.setVgrow(buttons, Priority.NEVER);
		VBox.setVgrow(filters, Priority.ALWAYS);
		filters.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		vGrid.getChildren().addAll(buttons, filters);
		addButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent arg0) {
				showCreateFilter();
			}
		});
		removeButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent arg0) {
				deleteSelectedFilters();
			}
		});
		return vGrid;
	}
	
	/**
	 * Name of the node visualization, as viewed in the "create filter" window.
	 */
	private final String nodeElementName = "Node";
	
	/**
	 * Name of the link visualization, as viewed in the "create filter" window.
	 */
	private final String linkElementName = "Link";
	
	/**
	 * Name of the person visualization, as viewed in the "create filter" window.
	 */
	private final String personElementName = "Person";
	
	/**
	 * Name of the ensemble membership visualization, as viewed in the "create filter" window.
	 */
	private final String ensembleElementName = "Ensemble Membership";
	
	/**
	 * Names of the elements of the visualization that can be filtered
	 */
	private final List<String> elementNames = Arrays.asList(
			nodeElementName, linkElementName, /* personElementName,*/ ensembleElementName
			);
	
	/**
	 * Maps each visualization element to the list of attributes by which we can filter
	 */
	private final Map<String,List<String>> attributesForElement = new HashMap<>();
	
	/**
	 * Attributes associated with nodes
	 */
	private final List<String> attributesForNode = Arrays.asList("ID", "x-coordinate", "y-coordinate");
	
	/**
	 * Attributes associated with links
	 */
	private final List<String> attributesForLink = Arrays.asList("ID", "From ID", "To ID");
	
	/**
	 * Attributes associated with persons
	 */
	private final List<String> attributesForPerson = Arrays.asList("ID");
	
	/**
	 * Attributes associated with ensembles
	 */
	private final List<String> attributesForEnsemble = Arrays.asList("Ensemble Name", "Coordinator ID", "Member ID");
	
	/**
	 * Builds and shows the "create new filter" window
	 */
	private void showCreateFilter(){
		final Stage stage = new Stage();
		GridPane pane = new GridPane();
		Scene scene = new Scene(pane);
		stage.setScene(scene);
		int row = 0;
		Label elementLabel = new Label("Element Type:");
		ComboBox<String> elementBox = new ComboBox<>();
		elementBox.getItems().addAll(elementNames);
		pane.add(elementLabel, 0, row);
		pane.add(elementBox, 1, row);
		row += 1;
		Label attributeLabel = new Label("Attribute");
		final ComboBox<String> attributeBox = new ComboBox<>();
		elementBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0,
					String oldElement, String newElement) {
				provideAttributes(newElement, attributeBox);
			}
		});
		elementBox.getSelectionModel().select(0);
		pane.add(attributeLabel, 0, row);
		pane.add(attributeBox, 1, row);
		row += 1;
		Label valueLabel = new Label("Value:");
		TextField valueField = new TextField();
		pane.add(valueLabel, 0, row);
		pane.add(valueField, 1, row);
		row += 1;
		Button okButton = new Button("OK");
		okButton.setOnAction(new OkButtonHandler(elementBox, attributeBox, valueField, stage));
		Button cancelButton = new Button("Cancel");
		cancelButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent arg0) {
				stage.close();				 
			}
		});
		pane.setHgap(5);
		pane.setVgap(5);
		pane.add(okButton, 0, row);
		pane.add(cancelButton, 1, row);
		row += 1;		
		decorateCreateFilterWindow(stage);
		stage.show();
	}
	
	/**
	 * Handler for the event that the OK button in the "add new filter" window is
	 * clicked. Delegates further actions to specialized methods, according to
	 * the filter type.
	 * 
	 * @author Tomas Filipek <tom.filipek@seznam.cz>
	 */
	private class OkButtonHandler implements EventHandler<ActionEvent>{
		
		/**
		 * The combo-box which contains the output element types, such
		 * as "Node", "Link", "Ensemble"
		 */
		private final ComboBox<String> elementBox;
		
		/**
		 * The combo-box which contains the attributes for the elements selected 
		 * above. For example, when the "Node" element is selected, this box
		 * contains "ID", "x-coord", "y-coord"
		 */
		private final ComboBox<String> attributeBox;
		
		/**
		 * Holds the attribute value according to which we will filter
		 */
		private final TextField valueField;
		
		/**
		 * The main stage in which the application runs
		 */
		private final Stage stage;

		/**
		 * @param elementBox The combo-box which contains the output element types, such
		 * as "Node", "Link", "Ensemble"
		 * @param attributeBox The combo-box which contains the attributes for the elements 
		 * selected above. For example, when the "Node" element is selected, this box 
		 * contains "ID", "x-coord", "y-coord"
		 * @param valueField Holds the attribute value according to which we will filter
		 * @param stage The main stage in which the application runs
		 */
		public OkButtonHandler(ComboBox<String> elementBox, ComboBox<String> attributeBox, TextField valueField,
				Stage stage) {
			this.elementBox = elementBox;
			this.attributeBox = attributeBox;
			this.valueField = valueField;
			this.stage = stage;
		}

		/**
		 * Run when the "OK" button in the "add new filter" window is clicked.
		 * Delegates the action to the respective methods, according to the
		 * filter type.
		 */
		@Override
		public void handle(ActionEvent event) {
			String selected = elementBox.getSelectionModel().getSelectedItem();
			String attribute = attributeBox.getSelectionModel().getSelectedItem();
			switch(selected){
				case nodeElementName:
					processNodeRequest(attribute, valueField.getText());
					break;
				case linkElementName:
					processLinkRequest(attribute, valueField.getText());
					break;
				case personElementName:
					break;
				case ensembleElementName:
					processEnsembleRequest(attribute, valueField.getText());
					break;
				default:
					break;
			}
			stage.close();
		}
		
	}
	
	/**
	 * Decorates the "create new filter" window
	 * @param stage The main {@link Stage} of the window
	 */
	private void decorateCreateFilterWindow(Stage stage){
		if (stage != null){
			stage.sizeToScene();
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.initStyle(StageStyle.DECORATED);
			stage.setResizable(false);
			stage.setTitle("Create New Filter");
			try (InputStream iconStream = Resources.getResourceInputStream("filter.png")){
				Image icon = new Image(iconStream);
				stage.getIcons().add(icon);
			} catch (IOException ex) {}
		}
	}
	
	/**
	 * Given the node attribute and its value, this method builds and applies the filter
	 * which filters out any nodes not having the specified attribute value. 
	 * @param attribute Name of a node attribute, such as "x-coordinate"
	 * @param value Value of the specified attribute
	 */
	private void processNodeRequest(String attribute, String value){
		if ((attribute != null) && (value != null)){
			PreferencesBuilder prefs = getPreferences();
			if (prefs != null){
				Map<String,NodePrefs> nodePrefs = prefs.nodePrefs(null);
				if (nodePrefs != null){
					Filter filter = null;
					switch(attribute){
						case "ID":							
							filter = new NodeIDFilter(value, nodePrefs);
							break;
						case "x-coordinate":
							try {
								double xCoord = Double.parseDouble(value);
								filter = new NodeXFilter(xCoord, nodePrefs);
							} catch (NumberFormatException ex) {}							
							break;
						case "y-coordinate":
							try {
								double yCoord = Double.parseDouble(value);
								filter = new NodeYFilter(yCoord, nodePrefs);
							} catch (NumberFormatException ex) {}	
							break;
						default:
							break;
					}
					if (filter != null){
						filters.getItems().add(filter);
						filter.apply();
					}
				}
			}
		}
	}
	
	/**
	 * Given the link attribute and its value, this method builds and applies the filter
	 * which filters out any links not having the specified attribute value. 
	 * @param attribute Name of a link attribute, such as "From node"
	 * @param value Value of the specified attribute
	 */
	private void processLinkRequest(String attribute, String value){
		if ((attribute != null) && (value != null)){
			PreferencesBuilder prefs = getPreferences();
			if (prefs != null){
				Map<String,LinkPrefs> linkPrefs = prefs.linkPrefs(null);
				if (linkPrefs != null){
					Filter filter = null;
					switch(attribute){
						case "ID":
							filter = new LinkIDFilter(value, linkPrefs);
							break;
						case "From ID":
							filter = new LinkFromFilter(value, linkPrefs);
							break;
						case "To ID":
							filter = new LinkToFilter(value, linkPrefs);
							break;
						default:
							break;
					}
					if (filter != null){
						filters.getItems().add(filter);
						filter.apply();
					}
				}
			}
		}
	}
	
	/**
	 * Given the ensemble membership attribute and its value, this method builds and applies the 
	 * filter which filters out any ensemble memberships not having the specified attribute value. 
	 * @param attribute Name of an ensemble membership attribute, such as "Coordinator"
	 * @param value Value of the specified attribute
	 */
	private void processEnsembleRequest(String attribute, String value){
		if ((attribute != null) && (value != null)){
			PreferencesBuilder prefs = getPreferences();
			if (prefs != null){
				Set<MembershipPrefs> ensemblePrefs = prefs.membershipPrefs(null);
				if (ensemblePrefs != null){
					Filter filter = null;
					switch(attribute){
						case "Ensemble Name":
							filter = new EnsembleNameFilter(value, ensemblePrefs);
							break;
						case "Coordinator ID":
							filter = new EnsembleCoordFilter(value, ensemblePrefs);
							break;
						case "Member ID":
							filter = new EnsembleMemberFilter(value, ensemblePrefs);
							break;
						default:
							break;
					}
					if (filter != null){
						filters.getItems().add(filter);
						filter.apply();
					}
				}
			}
		}
	}
	
	/**
	 * Un-applies the filters selected by the user and removes them from the filter list.
	 */
	private void deleteSelectedFilters(){
		for (Filter filter : filters.getItems()){
			filter.unapply();
		}
		List<Filter> selected = new ArrayList<>(filters.getSelectionModel().getSelectedItems());
		filters.getItems().removeAll(selected);
		for (Filter filter : filters.getItems()){
			filter.apply();
		}
	}
	
	/**
	 * Fills the combobox with attributes corresponding to the specified visualization element.
	 * @param element Element whose attributes will be put into the combobox
	 * @param attributeBox Combobox to be filled with the attributes corresponding to the element
	 */
	private void provideAttributes(String element, ComboBox<String> attributeBox){
		if ((element != null) && (attributeBox != null)){
			List<String> attributes = attributesForElement.get(element);
			attributeBox.getItems().clear();
			if (attributes != null){
				attributeBox.getItems().addAll(attributes);
				attributeBox.getSelectionModel().select(0);
			}
		}
	}
	
	/**
	 * Initializer for {@link FilterPanel#attributesForElement}. It fills the map
	 * with elements mapped to their corresponding attributes. 
	 */
	private void initializeAttributes(){
		attributesForElement.clear();
		for (String element : elementNames){
			switch (element){
				case nodeElementName:
					attributesForElement.put(element, attributesForNode);
					break;
				case linkElementName:
					attributesForElement.put(element, attributesForLink);
					break;
				case personElementName:
					attributesForElement.put(element, attributesForPerson);
					break;
				case ensembleElementName:
					attributesForElement.put(element, attributesForEnsemble);
					break;
			}
		}
	}

}
