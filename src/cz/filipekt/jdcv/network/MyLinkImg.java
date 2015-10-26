package cz.filipekt.jdcv.network;

/**
 * Represents a link_img element, which is used to specify the image that represents a link
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class MyLinkImg {
	
	/**
	 * Path to the image that represent a link
	 */
	private final String source;
	
	/**
	 * x-coordinate of the point in the specified image that marks 
	 * where the link start is located
	 */
	private final int fromX;
	
	/**
	 * y-coordinate of the point in the specified image that marks 
	 * where the link start is located
	 */
	private final int fromY;
	
	/**
	 * x-coordinate of the point in the specified image that marks 
	 * where the link end is located
	 */
	private final int toX;
	
	/**
	 * y-coordinate of the point in the specified image that marks 
	 * where the link end is located
	 */
	private final int toY;

	/**
	 * @return Path to the image that represent a link
	 * @see {@link MyLinkImg#source}
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @return x-coordinate of the point in the specified image that marks 
	 * where the link start is located
	 * @see {@link MyLinkImg#fromX}
	 */
	public int getFromX() {
		return fromX;
	}

	/**
	 * @return y-coordinate of the point in the specified image that marks 
	 * where the link start is located
	 * @see {@link MyLinkImg#fromY}
	 */
	public int getFromY() {
		return fromY;
	}

	/**
	 * @return x-coordinate of the point in the specified image that marks 
	 * where the link end is located
	 * @see {@link MyLinkImg#toX}
	 */
	public int getToX() {
		return toX;
	}

	/**
	 * @return y-coordinate of the point in the specified image that marks 
	 * where the link end is located
	 * @see {@link MyLinkImg#toY}
	 */
	public int getToY() {
		return toY;
	}

	/**
	 * @param source Path to the image that represent a link
	 * @param fromX x-coordinate of the point in the specified image that marks 
	 * where the link start is located
	 * @param fromY y-coordinate of the point in the specified image that marks 
	 * where the link start is located
	 * @param toX x-coordinate of the point in the specified image that marks 
	 * where the link end is located
	 * @param toY y-coordinate of the point in the specified image that marks 
	 * where the link end is located
	 */
	public MyLinkImg(String source, int fromX, int fromY, int toX, int toY) {
		super();
		this.source = source;
		this.fromX = fromX;
		this.fromY = fromY;
		this.toX = toX;
		this.toY = toY;
	}
}
