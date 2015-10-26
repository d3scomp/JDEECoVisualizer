package cz.filipekt.jdcv.plugins;

import java.io.IOException;
import java.io.InputStream;

import javafx.scene.Node;

/**
 * A plugin is shown on the right side of the application window. When there are multiple
 * plugins available, user can select which one to view by clicking the corresponding
 * button on the top-right side.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public interface Plugin {	
	
	/**
	 * @return Short name of this plugin
	 */
	String getName();
	
	/**
	 * @return Input stream opened on a file containing the small image representing this plugin
	 * @throws IOException If the image couldn't be found.
	 */
	InputStream getThumbnail() throws IOException;
	
	/**
	 * @return The main panel of this plugin, as shown on the right side of the 
	 * application window.
	 */
	Node getPanel();
}