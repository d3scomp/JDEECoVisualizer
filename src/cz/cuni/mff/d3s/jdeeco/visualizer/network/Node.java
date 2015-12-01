package cz.cuni.mff.d3s.jdeeco.visualizer.network;

import java.io.Serializable;

/**
 * Each {@link Node} instance represents a node in a Matsim-style network.
 * 
 * @author Dominik Skoda <skoda@d3s.mff.cuni.cz>
 */
public class Node implements Serializable {
	
	/**
	 * Generated UID.
	 */
	private static final long serialVersionUID = 3320094985829503349L;

	/**
	 * The reference to generate next node ID.
	 */
	private static int nodeIdReference = 0;
	
	/** 
	 * Each node HAS to have a unique id!
	 * <p>format := unsigned integer</p>
	 * <p>REQUIRED</p>
	 */
	private final int id;
	
	/**
	 * Each node HAS to have an x coordinate!
	 * <p>format := double</p>
	 * <p>units  := meter</p>
	 * <p>REQUIRED</p>
	 */
	private final double x;
	
	/** 
	 * Each node HAS to have an y coordinate!
	 * <p>format := double</p>
	 * <p>units  := meter</p>
	 * <p>REQUIRED</p>
	 */
	private final double y;
	
	/** 
	 * Typically, networks are created by third party (i.e. GIS users).
	 * So it makes sense (for completeness) to add the type of the node.
	 * Note, sometimes, the type defines in which of the network categories
	 * it belongs (streets, pedestrians, publictransport), so be sure,
	 * that the type does not conflict with the network category.
	 * <p>format := string</p>
	 * <p>IMPLIED</p>
	 */
	private String type;

	/**
	 * This attribute holds the original id for comparison.
	 * <p>format := string</p>
	 * <p>units  := N/A</p>
	 * <p>IMPLIED</p>
	 */
	private String origId;
	
	/**
	 * Create a new instance of {@link Node} with the given coordinates.
	 * @param x The X coordinate of the node.
	 * @param y The Y coordinate of the node.
	 */
	public Node(double x, double y){
		id = Node.nodeIdReference++;
		this.x = x;
		this.y = y;

		type = null;
		origId = null;
	}
	
	/**
	 * Get the ID of the node.
	 * @return The ID of the node.
	 */
	public int getId(){
		return id;
	}
	
	/**
	 * Get the X coordinate of the node.
	 * @return The X coordinate of the node.
	 */
	public double getX(){
		return x;
	}
	
	/**
	 * Get the Y coordinate of the node.
	 * @return The Y coordinate of the node.
	 */
	public double getY(){
		return y;
	}
	

	/** 
	 * Typically, networks are created by third party (i.e. GIS users).
	 * So it makes sense (for completeness) to add the type of the node.
	 * Note, sometimes, the type defines in which of the network categories
	 * it belongs (streets, pedestrians, publictransport), so be sure,
	 * that the type does not conflict with the network category.
	 * <p>This property is not required. Can be set to null.</p>
	 * @param type The type of the node.
	 * @throws IllegalArgumentException Thrown if the type argument is empty string.
	 */
	public void setType(String type){
		if(type != null && type.isEmpty()){
			throw new IllegalArgumentException(String.format(
					"The argument \"%s\" is empty.", "type"));
		}
		this.type = type;
	}
	
	/**
	 * Get the type of the network.
	 * @return
	 */
	public String getType(){
		return type;
	}

	/**
	 * This attribute holds the original id for comparison.
	 * <p>This property is not required. Can be set to null.</p>
	 * @param origId The original ID of the node.
	 * @throws IllegalArgumentException Thrown if the origID argument is empty string.
	 */
	public void setOrigId(String origId){
		if(origId != null && origId.isEmpty()){
			throw new IllegalArgumentException(String.format(
					"The argument \"%s\" is empty.", "origId"));
		}
		this.origId = origId;
	}
	
	/**
	 * Get the original ID of the node.
	 * @return The original ID of the node.
	 */
	public String getOrigId(){
		return origId;
	}
	
	/**
	 * Returns the string Matsim XML representation of the node.
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("<node ")
			.append("id=\"").append(id).append("\" ")
			.append("x=\"").append(x).append("\" ")
			.append("y=\"").append(y).append("\" ");
		if(type != null){
			builder.append("type=\"").append(type).append("\" ");
		}
		if(origId != null){
			builder.append("origid=\"").append(origId).append("\" ");
		}
		builder.append("/>");
		
		return builder.toString();
	}
	
	public static void resetIdReference(){
		nodeIdReference = 0;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Node)){
			return false;
		}
		Node other = (Node) obj;
		return this.id == other.id;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return id;
	}
}
