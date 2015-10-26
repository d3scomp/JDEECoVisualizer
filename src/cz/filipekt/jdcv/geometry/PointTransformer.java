package cz.filipekt.jdcv.geometry;

import javafx.geometry.Point2D;

/**
 * Converter of coordinates from system A to system B 
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public abstract class PointTransformer implements CoordinateTransformer {

	/**
	 * Transforms the given point from coordinate system A to system B
	 * @param point The point in coordinates A
	 * @return The point in coordinates B
	 */
	public Point2D transform(Point2D point){
		double x = transformX(point.getX());
		double y = transformY(point.getY());
		return new Point2D(x, y);
	}

}
