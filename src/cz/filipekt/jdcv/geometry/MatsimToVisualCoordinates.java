package cz.filipekt.jdcv.geometry;

/**
 * Converter from the coordinates used in the MATSIM simulation map to the coordinates
 * used in the visualization, i.e. as used on the screen
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class MatsimToVisualCoordinates extends PointTransformer {
	
	/**
	 * Minimal value of the x-coordinate among all the nodes in the MATSIM map
	 */
	private final double minx;
	
	/**
	 * Minimal value of the y-coordinate among all the nodes in the MATSIM map
	 */
	private final double miny;
	
	/**
	 * Factor by which the x-coordinate scale (from MATSIM map) must be multiplied so 
	 * that the resulting network view fits nicely into the window of preferred width
	 */
	private final double widthFactor;
	
	/**
	 * Factor by which the y-coordinate scale (from MATSIM map) must be multiplied so 
	 * that the resulting network view fits nicely into the window of preferred height
	 */
	private final double heightFactor;
	
	/**
	 * Width of the white margin that is added on each side of the map
	 */
	private final double margin;
	
	/**
	 * @param minx Minimal value of the x-coordinate among all the nodes in the MATSIM map
	 * @param miny Minimal value of the y-coordinate among all the nodes in the MATSIM map
	 * @param widthFactor Factor by which the x-coordinate scale (from MATSIM map) must be multiplied so 
	 * that the resulting network view fits nicely into the window of preferred width
	 * @param heightFactor Factor by which the y-coordinate scale (from MATSIM map) must be multiplied so 
	 * that the resulting network view fits nicely into the window of preferred height
	 * @param margin Width of the white margin that is added on each side of the map
	 */
	public MatsimToVisualCoordinates(double minx, double miny,
			double widthFactor, double heightFactor, double margin) {
		this.minx = minx;
		this.miny = miny;
		this.widthFactor = widthFactor;
		this.heightFactor = heightFactor;
		this.margin = margin;
	}

	/**
	 * @param x An x-coordinate as given in an XML element such event, link, etc. 
	 * @return The x-coordinate converted to the value used in the map visualization, where 
	 * the coordinates correspond to the actual pixels on the screen (before zooming). 
	 */
	@Override
	public double transformX(double x) {
		x -= minx;
		x *= widthFactor;
		x += margin;
		return x;
	}
	
	/**
	 * @param y A y-coordinate as given in an XML element such event, link, etc.
	 * @return The y-coordinate converted to the value used in the map visualization, where 
	 * the coordinates correspond to the actual pixels on the screen (before zooming). 
	 */
	@Override
	public double transformY(double y) {
		y -= miny;
		y *= heightFactor;
		y += margin;
		return y;
	}
}