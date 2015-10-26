package cz.filipekt.jdcv.corridors;

import javafx.scene.paint.Color;

/**
 * Background definition taken from the map definition XML file.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class Background {
	
	/**
	 * Image to used as background of the map
	 */
	private final String image;
	
	/**
	 * Color to be used as background of the map 
	 */
	private final Color color;
	
	/**
	 * x-coordinate of the point where the left-top corner of the 
	 * background image will be placed in the visualization 
	 */
	private final double leftTopX;
	
	/**
	 * y-coordinate of the point where the left-top corner of the 
	 * background image will be placed in the visualization
	 */
	private final double leftTopY;
	
	/**
	 * x-coordinate of the point where the right-bottom corner of the 
	 * background image will be placed in the visualization
	 */
	private final double rightBottomX;
	
	/**
	 * y-coordinate of the point where the right-bottom corner of the 
	 * background image will be placed in the visualization
	 */
	private final double rightBottomY;
	
	/**
	 * @return Image to used as background of the map
	 */
	public String getImage() {
		return image;
	}
	
	/**
	 * @return Color to be used as background of the map
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * @return x-coordinate of the point where the left-top corner of the 
	 * background image will be placed in the visualization
	 */
	public double getLeftTopX() {
		return leftTopX;
	}

	/**
	 * @return y-coordinate of the point where the left-top corner of the 
	 * background image will be placed in the visualization
	 */
	public double getLeftTopY() {
		return leftTopY;
	}

	/**
	 * @return x-coordinate of the point where the right-bottom corner of the 
	 * background image will be placed in the visualization
	 */
	public double getRightBottomX() {
		return rightBottomX;
	}

	/**
	 * @return y-coordinate of the point where the right-bottom corner of the 
	 * background image will be placed in the visualization
	 */
	public double getRightBottomY() {
		return rightBottomY;
	}

	/**
	 * @param image Image to used as background of the map
	 * @param color Color to be used as background of the map
	 * @param leftTopX x-coordinate of the point where the left-top corner of the 
	 * background image will be placed in the visualization
	 * @param leftTopY y-coordinate of the point where the left-top corner of the 
	 * background image will be placed in the visualization
	 * @param rightBottomX x-coordinate of the point where the right-bottom corner of the 
	 * background image will be placed in the visualization
	 * @param rightBottomY y-coordinate of the point where the right-bottom corner of the 
	 * background image will be placed in the visualization
	 */
	private Background(String image, Color color, double leftTopX, double leftTopY, 
			double rightBottomX, double rightBottomY) {
		this.image = image;
		this.color = color;
		this.leftTopX = leftTopX;
		this.leftTopY = leftTopY;
		this.rightBottomX = rightBottomX;
		this.rightBottomY = rightBottomY;
	}
	
	/**
	 * @param color Color to be used as background of the map
	 */
	public Background(Color color){
		this(null, color, 0, 0, 0, 0);
	}
	
	/**
	 * @param image Image to used as background of the map
	 * @param leftTopX x-coordinate of the point where the left-top corner of the 
	 * background image will be placed in the visualization
	 * @param leftTopY y-coordinate of the point where the left-top corner of the 
	 * background image will be placed in the visualization
	 * @param rightBottomX x-coordinate of the point where the right-bottom corner of the 
	 * background image will be placed in the visualization
	 * @param rightBottomY y-coordinate of the point where the right-bottom corner of the 
	 * background image will be placed in the visualization
	 */
	public Background(String image, double leftTopX, double leftTopY, double rightBottomX, 
			double rightBottomY) {
		this(image, null, leftTopX, leftTopY, rightBottomX, rightBottomY);
	}
}
