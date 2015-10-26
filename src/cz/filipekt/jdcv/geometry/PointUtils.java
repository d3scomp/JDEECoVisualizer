package cz.filipekt.jdcv.geometry;

import javafx.geometry.Point2D;

/**
 * Contains some of the basic utility methods on instances of {@link Point2D}, 
 * which are not contained in the standard JavaFX 2.2 library.
 * Most of these have been added to the JavaFX library since the version 8..
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class PointUtils {
	
	/**
	 * The point with all coordinates being zero.
	 */
	public static final Point2D ZERO = new Point2D(0, 0);
	
	/**
	 * The point with all coordinates being 1.
	 */
	public static final Point2D ONE = new Point2D(1, 1);
	
	/**
	 * Subtracts the point b from the point a - the result is
	 * returned as a new instance of {@link Point2D}, as the class
	 * is immutable.
	 * @param a The minuend
	 * @param b The subtrahend
	 * @return A new point instance equal to (a-b) 
	 */
	public static Point2D subtract(Point2D a, Point2D b){
		return new Point2D(a.getX()-b.getX(), a.getY()-b.getY());
	}
	
	/**
	 * Adds the point a to the point b - the result is returned
	 * as a new instance of {@link Point2D}, as the class is
	 * immutable.
	 * @param a First summand
	 * @param b Second summand
	 * @return A new point instance equal to (a+b)
	 */
	public static Point2D add(Point2D a, Point2D b){
		return new Point2D(a.getX()+b.getX(), a.getY()+b.getY());
	}
	
	/**
	 * Multiplies the coordinates of the point by the given number.
	 * The result is returned as a new instance of {@link Point2D},
	 * as the class is immutable.
	 * @param point The initial point to be multiplied
	 * @param factor By this factor the point coordinates will be multiplied
	 * @return A new point instance equal to point*factor (coordinate-wise).
	 */
	public static Point2D multiply(Point2D point, double factor){
		return new Point2D(point.getX()*factor, point.getY()*factor);
	}
}
