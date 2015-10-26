package cz.filipekt.jdcv.network;

/**
 * Represents a "node" XML element in the network source file.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class MyNode {
	
	/**
	 * The unique id of the node
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
	 * @param id The unique id of the node
	 * @param x x-coordinate of the node
	 * @param y y-coordinate of the node
	 */
	public MyNode(String id, double x, double y) {
		super();
		this.id = id;
		this.x = x;
		this.y = y;
	}

	/**
	 * @return The unique id of the node
	 * @see {@link MyNode#id}
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return x-coordinate of the node
	 * @see {@link MyNode#x}
	 */
	public double getX() {
		return x;
	}

	/**
	 * @return y-coordinate of the node
	 * @see {@link MyNode#y}
	 */
	public double getY() {
		return y;
	}

	/**
	 * Implements the unique identification of the {@link MyNode} object
	 * by {@link MyNode#id}
	 * @see {@link MyNode#hashCode()}
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MyNode){
			MyNode that = (MyNode)obj;
			return this.id.equals(that.id);
		} else {
			return false;
		}
	}

	/**
	 * Implements the unique identification of the {@link MyNode} object
	 * by {@link MyNode#id}
	 * @see {@link MyNode#equals(Object)}
	 */
	@Override
	public int hashCode() {
		return this.id.hashCode();
	}

}
