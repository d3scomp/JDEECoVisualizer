package cz.filipekt.jdcv.prefs;

import java.io.IOException;
import java.io.Writer;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;

/**
 * Preferences object associated with a given link element
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class LinkPrefs implements VisibilityChangeable {
	
	/**
	 * Identification of the link
	 */
	private final String id;
	
	/**
	 * @return Identification of the link
	 * @see {@link LinkPrefs#id}
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * ID of the node where this link starts
	 */
	private final String fromNode;
	
	/**
	 * @return ID of the node where this link starts
	 * @see {@link LinkPrefs#fromNode}
	 */
	public String getFromNode() {
		return fromNode;
	}
	
	/**
	 * ID of the node where this link ends
	 */
	private final String toNode;
	
	/**
	 * @return ID of the node where this link ends
	 * @see {@link LinkPrefs#toNode}
	 */
	public String getToNode() {
		return toNode;
	}
	
	/**
	 * The geometric shape that represents the link in the visualization
	 */
	private final Shape line;
	
	/**
	 * Used for logging of the carried out operations
	 */
	private final Writer writer;
	
	/**
	 * @param id Identification of the link
	 * @param fromNode ID of the node where this link starts
	 * @param toNode ID of the node where this link ends
	 * @param line The geometric shape that represents the link in the visualization
	 * @param writer Used for logging of the carried out operations 
	 */
	public LinkPrefs(String id, String fromNode, String toNode, Shape line, Writer writer) {
		this.id = id;
		this.fromNode = fromNode;
		this.toNode = toNode;
		this.line = line;
		this.writer = writer;
	}
	
	/**
	 * @return The current color of the link visualization
	 */
	public Paint getColor(){
		if (line == null){
			return null;
		} else {
			return line.getStroke();
		}
	}
	
	/**
	 * Sets the color of the link visualization
	 * @param paint The new color
	 */
	public void setColor(Paint paint){
		if (line != null){
			line.setStroke(paint);
			log("Color of the link " + id + " set to " + paint);
		}
	}
	
	/**
	 * @return The current width of the link visualization
	 */
	public double getWidth(){
		if (line == null){
			return 0;
		} else {
			return line.getStrokeWidth();
		}
	}
	
	/**
	 * Sets the width of the link visualization
	 * @param val The new width
	 */
	public void setWidth(double val){
		if (line != null){
			line.setStrokeWidth(val);
			log("Width of the link " + id + " set to " + val);
		}
	}
	
	/**
	 * Logs the specified text, using {@link LinkPrefs#writer}
	 * @param text The text to be logged
	 */
	private void log(String text){
		if (writer != null){
			try {
				writer.append(text);
				writer.append("\n");
				writer.flush();
			} catch (IOException ex) {}
		}
	}

	/**
	 * Allows to set the visibility of the link
	 */
	@Override
	public void setVisible(boolean visible) {
		if (line != null){
			line.setVisible(visible);
			log("Visibility of link " + id + " set to " + visible);
		}
	}

	/**
	 * A check for visibility of the link 
	 */
	@Override
	public boolean isVisible() {
		if (line == null){
			return false;
		} else {
			return line.isVisible();
		}
	}
}