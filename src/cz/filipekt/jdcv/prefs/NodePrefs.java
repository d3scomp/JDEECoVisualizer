package cz.filipekt.jdcv.prefs;

import java.io.IOException;
import java.io.Writer;

import javafx.scene.Node;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

/**
 * Preferences object associated with a given node element
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class NodePrefs implements VisibilityChangeable {
	
	/**
	 * ID of the node
	 */
	private final String id;
	
	/**
	 * x-coordinate of the node
	 */
	private final double x;
	
	/**
	 * y-coordinate of the node
	 */
	private final double y;
	
	/**
	 * Visual representation of the node
	 */
	private final Node node;
	
	/**
	 * Used for logging of the carried out operations
	 */
	private final Writer writer;

	/**
	 * @return ID of the node
	 * @see {@link NodePrefs#id}
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return x-coordinate of the node
	 * @see {@link NodePrefs#x}
	 */
	public double getX() {
		return x;
	}

	/**
	 * @return y-coordinate of the node
	 * @see {@link NodePrefs#y}
	 */
	public double getY() {
		return y;
	}
	
	/**
	 * Sets the visibility of the visual representation of the node
	 */
	@Override
	public void setVisible(boolean visible){
		if (node != null){
			node.setVisible(visible);
			log("Visibility of node " + getId() + " set to " + visible);
		}
	}
	
	/**
	 * A check for visibility of the node
	 */
	@Override
	public boolean isVisible() {
		if (node == null){
			return false;
		} else {
			return node.isVisible();
		}
	}

	/**
	 * If the visual representation of the node is a plain circle (default option),
	 * then this method sets its color
	 * @param color The new color
	 */
	public void setCircleColor(Paint color){
		if ((node != null) && (node instanceof Circle)){	
			Circle circle = (Circle)node;
			circle.setFill(color);
			log("Color of the node " + getId() + " set to " + color);
		}
	}
	
	/**
	 * @return If the node is represnted by the (default) circle, this method returns
	 * the color of the circle. Otherwise, null is returned. 
	 */
	public Paint getCircleColor(){
		if ((node != null) && (node instanceof Circle)){	
			Circle circle = (Circle)node;
			return circle.getFill();
		} else {
			return null;
		}
	}

	/**
	 * @param id ID of the node
	 * @param x x-coordinate of the node
	 * @param y y-coordinate of the node
	 * @param node Visual representation of the node
	 * @param writer Used for logging of the carried out operations
	 */
	public NodePrefs(String id, double x, double y, Node node, Writer writer) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.node = node;
		this.writer = writer;
	}
	
	/**
	 * Logs the specified text, using {@link NodePrefs#writer}
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

}
