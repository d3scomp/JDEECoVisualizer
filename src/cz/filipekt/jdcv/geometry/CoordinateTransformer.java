package cz.filipekt.jdcv.geometry;

/**
 * Converter of coordinates from system A to system B 
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public interface CoordinateTransformer {
	
	/**
	 * @param x x-coordinate value in system A
	 * @return x-coordinate value in system B
	 */
	double transformX(double x);
	
	/**
	 * @param y y-coordinate value in system A
	 * @return y-coordinate value in system B
	 */
	double transformY(double y);
		
}