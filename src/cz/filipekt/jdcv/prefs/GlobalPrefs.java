package cz.filipekt.jdcv.prefs;

import java.io.IOException;
import java.io.Writer;

import cz.filipekt.jdcv.MapScene;

/**
 * Provides the option to change some of the global preferences of the
 * application or visualization.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class GlobalPrefs {
	
	/**
	 * The simulated situation 
	 */
	private final MapScene scene;
	
	/**
	 * Used for logging of the carried out operations
	 */
	private final Writer outputWriter;

	/**
	 * Holds the path to the folder with the additional resources (e.g. pngs)
	 * used in custom visualization of persons and nodes
	 */
	private String additionalResourcesPath;
	
	/**
	 * @param scene The simulated situation 
	 * @param writer Used for logging of the carried out operations
	 */
	public GlobalPrefs(MapScene scene, Writer writer) {
		this.scene = scene;
		this.outputWriter = writer;
	}

	public void setAdditionalResourcesPath(String additionalResourcesPath) {
		this.additionalResourcesPath = additionalResourcesPath;
	}
	
	/**
	 * Given a path to an image and an array of person IDs, each of the persons will be 
	 * represented by the image in the visualization.
	 * @param path The image to use for visualizing people
	 * @param selectedPeople People whose visualizations will be updated
	 */
	public void setPersonImage(String path, String... selectedPeople) {
		if (scene == null){
			printNoOp();
		} else {
			try {
				scene.changePeopleImage(additionalResourcesPath + path, false, selectedPeople);
				write("Image for persons has been successfully changed.");
			} catch (Exception ex){
				write("Image for persons couldn't be changed.");
			}
		}
	}
	
	/**
	 * Given a path to an image and an array of node IDs, each of the nodes will be 
	 * represented by the image in the visualization.
	 * @param path The image to use for visualizing nodes
	 * @param selectedNodes Nodes whose visualizations will be updated
	 */
	public void setNodeImage(String path, String... selectedNodes) {
		if (scene == null){
			printNoOp();
		} else { 
			try {
				scene.changeNodeImage(additionalResourcesPath + path, false, selectedNodes);
				write("Image for nodes has been successfully changed.");
			} catch (Exception ex){
				write("Image for nodes couldn't be changed.");
				write("Exception: " + ex.toString());
			}
		}
	}
	
	/**
	 * Prints a "no operation" log to the output
	 */
	private void printNoOp() {
		write("No simulation scene has been specified.");
	}
	
	/**
	 * Logs the specified text, using {@link GlobalPrefs#outputWriter}
	 * @param text The text to be logged
	 */
	private void write(String text){
		if ((outputWriter != null) && (text != null)){
			try {
				outputWriter.append(text);
				outputWriter.append("\n");
				outputWriter.flush();			
			} catch (IOException ex) {}
		}
	}
}
