package cz.filipekt.jdcv.gui_logic;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import cz.filipekt.jdcv.Visualizer;
import cz.filipekt.jdcv.util.CharsetNames;
import cz.filipekt.jdcv.util.Dialog;
import cz.filipekt.jdcv.util.Dialog.Type;

/**
 * Loads the configuration of a new scene from a configuration file.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class ConfigFileLoader implements EventHandler<ActionEvent> {
	
	/**
	 * The text field containing the config file path
	 */
	private final TextField configFileField;
	
	/**
	 * The combo-box for selecting the text encoding of the config file
	 */
	private final ComboBox<String> configFileCharsets;
	
	/**
	 * The text field for specifying the path to the network definition file
	 */
	private final TextField networkField;
	
	/**
	 * The combo-box for selecting the text encoding of the network definition file
	 */
	private final ComboBox<String> networkCharsets;
	
	/**
	 * The text field for specifying the path to the event log file
	 */
	private final TextField eventField;
	
	/**
	 * The combo-box for selecting the text encoding of the event log file
	 */
	private final ComboBox<String> eventCharsets;
	
	/**
	 * The text field for specifying the path to the ensembke event log file
	 */
	private final TextField ensembleField;
	
	/**
	 * The combo-box for selecting the text encoding of the ensemble event log file
	 */
	private final ComboBox<String> ensembleCharsets;
	
	/**
	 * The field specifying the duration of the visualization
	 */
	private final TextField durationField;
	
	/**
	 * The checkbox specifying if just the injected JDEECo agents shall be visualized
	 */
	private final CheckBox justAgentsBox;
	
	/**
	 * The field specifying the where in the event log should the visualization begin
	 */
	private final TextField startAtField;
	
	/**
	 * The field specifying the where in the event log should the visualization end
	 */
	private final TextField endAtField;

	/**
	 * @param configFileField The text field containing the config file path
	 * @param configFileCharsets The combo-box for selecting the text encoding of the config file
	 * @param fields The text fields where the paths to the input XML files will be filled in
	 * @param charsetBoxes The combo-boxes for selecting the text encoding of the input XML files
	 * @param durationField The editable combo-box for specifying the duration of the visualization
	 * @param justAgentsBox The checkbox specifying if just the injected JDEECo agents shall be visualized
	 * @param startAtField The field specifying the where in the event log should the visualization begin
	 * @param endAtField The field specifying the where in the event log should the visualization end
	 */
	public ConfigFileLoader(TextField configFileField, ComboBox<String> configFileCharsets, 
			List<TextField> fields, List<ComboBox<String>> charsetBoxes, TextField durationField,
			CheckBox justAgentsBox, TextField startAtField, TextField endAtField) {
		this.configFileField = configFileField;
		this.configFileCharsets = configFileCharsets;
		this.durationField = durationField;
		this.networkField = fields.get(0);
		this.networkCharsets = charsetBoxes.get(0);
		this.eventField = fields.get(1);
		this.eventCharsets = charsetBoxes.get(1);
		this.ensembleField = fields.get(2);
		this.ensembleCharsets = charsetBoxes.get(2);
		this.justAgentsBox = justAgentsBox;
		this.startAtField = startAtField;
		this.endAtField = endAtField;
	}

	/**
	 * Delimiter of the line blocks in the config file
	 */
	private final String delimiter = ";";
	
	/**
	 * Contents of the first block of the line that specifies the network definition 
	 * file, in the config file
	 */
	private final String networkPreamble = "network";
	
	/**
	 * Contents of the first block of the line that specifies the event log 
	 * file, in the config file
	 */
	private final String eventPreamble = "events";
	
	/**
	 * Contents of the first block of the line that specifies the ensemble event 
	 * log file, in the config file
	 */
	private final String ensemblePreamble = "ensembles";
	
	/**
	 * Contents of the first block of the line that specifies the visualization
	 * duration, in the config file
	 */
	private final String durationPreamble = "target_duration";
	
	/**
	 * Contents of the first block of the line that specifies the "view only
	 * JDEECo agents" mode in the config file
	 */
	private final String agentsPreamble = "just_agents";
	
	/**
	 * First block of the line which specifies where in the event logs 
	 * the visualization should start
	 */
	private final String startAtPreamble = "start_at";
	
	/**
	 * First block of the line which specifies where in the event logs 
	 * the visualization should end
	 */
	private final String endAtPreamble = "end_at";

	/**
	 * First block of the line which specifies the scripts file,  
	 * in the config file
	 */
	private final String scriptPreamble = "scripts";
	
	/**
	 * Fired when user clicks the "load" button next to the config file text field.
	 * Makes sure that the config file exists, is opened and is properly processed.
	 */
	@Override
	public void handle(ActionEvent arg0) {
		String pathValue = configFileField.getText();
		if ((pathValue == null) || (pathValue.isEmpty())){
			Dialog.show(Type.INFO, "Path to the configuration file hasn't been specified.",
					"Fill in the path and try again.");
		} else {
			clearInputFields();
			Path path = Paths.get(pathValue);
			if (Files.exists(path)){
				Charset charset = Charset.forName(configFileCharsets.getSelectionModel().getSelectedItem());
				try {
					List<String> lines = Files.readAllLines(path, charset);
					processLines(lines);
					configFileField.setText("");
				} catch (IOException ex) {
					Dialog.show(Type.ERROR, "Could not read from the specified config file.");
				} catch (ConfigFileLoader.ConfigFileFormatException ex) {
					Dialog.show(Type.ERROR, "The config file contains a syntax error:",
							ex.getMessage(),
							"Provide a different config file and try again.");
				}
			} else {
				Dialog.show(Type.ERROR, "The specified configuration file doesn't exist.",
						"Enter a different file and try again.");
			}
		}
	}
	
	/**
	 * Clears all the input fields on the "import new scene" page.  
	 */
	private void clearInputFields(){
		networkField.setText("");
		eventField.setText("");
		ensembleField.setText("");
		durationField.setText("");
		justAgentsBox.setSelected(false);
		startAtField.setText("");
		endAtField.setText("");
	}
	
	/**
	 * Thrown when the config file does not have a valid structure
	 */
	@SuppressWarnings("serial")
	private static class ConfigFileFormatException extends Exception {

		public ConfigFileFormatException(String message) {
			super(message);
		}
		
	}
	
	/**
	 * Given all the lines of the config file, this method makes sure that the lines
	 * are properly processed one by one. The processing is delegated to specialized
	 * method, according to the type of the info the line holds. 
	 * @param lines The lines of the config file 
	 * @throws ConfigFileLoader.ConfigFileFormatException When the config file does not have a valid structure
	 */
	private void processLines(List<String> lines) throws ConfigFileLoader.ConfigFileFormatException{
		List<String> errorMessages = new ArrayList<>();
		for (int lineNo = 0; lineNo < lines.size(); lineNo++){
			String line = lines.get(lineNo);
			if ((line != null) && (!line.isEmpty())){
				try {
					String[] blocks = line.split(delimiter);
					if ((blocks != null) && (blocks.length > 0) && (blocks[0] != null)){
						switch (blocks[0]){
							case networkPreamble:
								processPathDef(blocks, networkField, networkCharsets, lineNo);
								break;
							case eventPreamble:
								processPathDef(blocks, eventField, eventCharsets, lineNo);
								break;
							case ensemblePreamble:
								processPathDef(blocks, ensembleField, ensembleCharsets, lineNo);
								break;
							case durationPreamble:
								processNumberDef(blocks, durationField, lineNo);
								break;
							case agentsPreamble:
								processBoolean(blocks, justAgentsBox, lineNo);
								break;
							case startAtPreamble:
								processNumberDef(blocks, startAtField, lineNo);
								break;
							case endAtPreamble:
								processNumberDef(blocks, endAtField, lineNo);
								break;
							case scriptPreamble:
								processPathDef(blocks, lineNo);
								break;
							default:
								break;
						}
					}
				} catch (ConfigFileLoader.ConfigFileFormatException ex){
					errorMessages.add(ex.getMessage());
				}
			}
		}
		if (errorMessages.size() > 0){
			StringBuilder sb = new StringBuilder();
			for (String error : errorMessages){
				sb.append(error);
				sb.append("\n");					
			}
			throw new ConfigFileFormatException(sb.toString());
		}
	}
	
	private void processPathDef(String[] blocks, int lineNo) throws ConfigFileLoader.ConfigFileFormatException{
		if ((blocks != null) && (blocks.length >= 1)){
			if (blocks.length > 2){
				throw new ConfigFileFormatException("[Line " + lineNo + 
						"]: contains too many blocks delimited by \"" + delimiter + "\"");
			}
			Visualizer.getInstance().setScriptsFilePath(blocks[1]);
		} else {
			throw new ConfigFileFormatException("[Line " + (lineNo+1) + 
					"]: contains too few blocks delimited by \"" + delimiter + "\"");
		}
		
	}

	/**
	 * Processes those lines of the config file that specify paths to the input XML files
	 * @param blocks A line from the config file, parsed by the delimiter {@link ConfigFileLoader#delimiter}
	 * @param field The text field to which the path will be written to 
	 * @param encodingBox The combo-box where the loaded text encoding will be recorded to
	 * @param lineNo Number of the line, whose contents are given in the first parameter
	 * @throws ConfigFileLoader.ConfigFileFormatException When the line does not have a valid structure
	 */
	private void processPathDef(String[] blocks, TextField field, ComboBox<String> encodingBox, 
			int lineNo) throws ConfigFileLoader.ConfigFileFormatException{
		if ((blocks != null) && (blocks.length >= 2)){
			if (blocks.length > 3){
				throw new ConfigFileFormatException("[Line " + lineNo + 
						"]: contains too many blocks delimited by \"" + delimiter + "\"");
			}
			field.setText(blocks[1]);
			if ((encodingBox != null) && (blocks.length >= 3) && (blocks[2] != null) && 
					(CharsetNames.get().contains(blocks[2]))){
				encodingBox.getSelectionModel().select(blocks[2]);
			}
		} else {
			throw new ConfigFileFormatException("[Line " + (lineNo+1) + 
					"]: contains too few blocks delimited by \"" + delimiter + "\"");
		}
	}
	
	/**
	 * Processes the lines of the config file that specify numeric aspects of the visualization, 
	 * i.e. its duration or starting time
	 * @param blocks Line from the config file, parsed by the delimiter {@link ConfigFileLoader#delimiter}
	 * @param timeBox The field where the loaded number will be recorded to
	 * @param lineNo Number of the line, whose contents are given in the first parameter
	 * @throws ConfigFileLoader.ConfigFileFormatException When the line does not have a valid structure
	 */
	private void processNumberDef(String[] blocks, TextField timeField, 
			int lineNo) throws ConfigFileLoader.ConfigFileFormatException{
		if ((blocks != null) && (blocks.length == 2) && (timeField != null)){
			try {
				Integer value = Integer.valueOf(blocks[1]);
				timeField.setText(value.toString());
			} catch (NumberFormatException ex){
				throw new ConfigFileFormatException("[Line " + (lineNo+1) + "]: the duration definition " + 
						"line must contain precisely two blocks delimeted by \"" + "\"");
			}
		}
	}
	
	/**
	 * Processes the lines of the config file that specify boolean input, such as the
	 * "just agents" input.
	 * @param blocks Line from the config file, parsed by the delimiter {@link ConfigFileLoader#delimiter}
	 * @param booleanBox The checkbox where the "just agents" input is marked
	 * @param lineNo Number of the line, whose contents are given in the first parameter
	 * @throws ConfigFileLoader.ConfigFileFormatException When the line does not have a valid structure
	 */
	private void processBoolean(String[] blocks, CheckBox booleanBox, int lineNo) 
			throws ConfigFileFormatException{
		if ((blocks != null) && (blocks.length == 2) && (booleanBox != null)){
			try {
				Boolean value = Boolean.valueOf(blocks[1]);
				booleanBox.setSelected(value);
			} catch (NumberFormatException ex){
				throw new ConfigFileFormatException("[Line " + (lineNo+1) + 
						"]: the \"just agents\" definition ");
			}
		}
	}
}
