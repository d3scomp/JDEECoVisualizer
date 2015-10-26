package cz.filipekt.jdcv.util;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.text.Text;

/**
 * Utility methods dealing with JavaFX {@link Node} instances
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class GUIUtils {

	/**
	 * Computes the width (in pixels) of default JavaFX view of the given text.
	 * This method must be called within the JavaFX application thread!
	 * @param text Text whose width is computed
	 * @return Width of a default view of the given text
	 */
	public static double computeTextLength(String text){
		Text textView = new Text(text);
	    return getIdealWidth(textView);
	}
	
	/**
	 * Computes the width of this node before any resizing takes places
	 * This method must be called within the JavaFX application thread! 
	 * @param node The node whose size is computed
	 * @return Width of this node before any resizing takes places
	 */
	public static double getIdealWidth(Node node){
		new Scene(new Group(node)); 
	    node.snapshot(null, null);
	    return node.getLayoutBounds().getWidth();
	}

	/**
	 * Determines which of the available charset names has the maximal length in default 
	 * text view, and returns the length (in pixels). 
	 * This method must be called within the JavaFX application thread!
	 * @return Length of the longest charset name, in pixels, using default view
	 */
	public static double getEncodingNameMaxLength(){
		double maxVal = 0.0;
		for (String encodingName : CharsetNames.get()){
			double length = computeTextLength(encodingName);
			if (length > maxVal){
				maxVal = length;
			}
		}
		return maxVal;
	}

}
