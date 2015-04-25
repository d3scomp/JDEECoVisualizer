package cz.filipekt.jdcv;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import cz.filipekt.jdcv.network.MyLink;
import cz.filipekt.jdcv.network.MyLinkImg;

/**
 * Given a link element, this class provides means to construct a corresponding
 * {@link LinkCorridor} instance.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class CorridorLoader {
	
	/**
	 * The image that represents the link
	 */
	private final MyLinkImg linkImage;
	
	/**
	 * Points specifying the path along which the cars/persons move through the 
	 * link visualization. Coordinates are taken from the image raster.
	 */
	private final List<Point2D> path;
	
	/**
	 * Location of the link start, in the visualized map coordinates
	 */
	private final Point2D fromPoint;
	
	/**
	 * Location of the link end, in the visualized map coordinates
	 */
	private final Point2D toPoint;
	
	/**
	 * Location of the link start, in the image coordinates
	 */
	private final Point2D fromPointImage;
	
	/**
	 * Location of the link end, in the image coordinates
	 */
	private final Point2D toPointImage;
	
	/**
	 * The image visualizing the link will be rotated by this angle, so that it fits
	 * the position of the link on the visualized map 
	 */
	private final double angle;
	
	/**
	 * The image visualizing the link will be shrinked in this ratio, so that it fits
	 * the length of the link in the map visualization
	 */
	private final double shrinkRatio;
	
	/**
	 * The desired corddidor visualizing the specified link using the given image, as
	 * specified inside the link element
	 */
	private final LinkCorridor builtCorridor;
	
	/**
	 * Default color of the {@link Line} instances that represent links
	 */
	private final Paint linkDefaultColor = Color.SILVER;
	
	/**
	 * Width of the {@link Line} instances that represent links, in pixels.
	 */
	private final double linkWidth = 2.0;
	
	/**
	 * @param link The link to be visualized
	 * @param fromPoint Location of the link start, in the visualized map coordinates
	 * @param toPoint Location of the link end, in the visualized map coordinates
	 */
	public CorridorLoader(MyLink link, Point2D fromPoint, Point2D toPoint) {
		this.linkImage = link.getLinkImage();
		this.path = link.getPathPoints();
		this.fromPoint = fromPoint;
		this.toPoint = toPoint;
		if (linkImage == null){
			fromPointImage = null;
			toPointImage = null;
			angle = 0;
			shrinkRatio = 1;
		} else {
			fromPointImage = new Point2D(linkImage.getFromX(), linkImage.getFromY());
			toPointImage = new Point2D(linkImage.getToX(), linkImage.getToY());
			Point2D unitInImage = toPointImage.subtract(fromPointImage);
			Point2D unitInVisual = toPoint.subtract(fromPoint);
			angle = angle(unitInImage, unitInVisual);
			double distanceInImage = fromPointImage.distance(toPointImage);
			double distanceInVisual = fromPoint.distance(toPoint);
			shrinkRatio = distanceInVisual / distanceInImage;
		}
		builtCorridor = new LinkCorridor(link.getId(), getVisualCorridor(), getConvertedPath());
	}
	
	/**
	 * If no image is provided for link visualization, this method is used to generate the
	 * simplest form of link visualization - a plain line
	 * @return A line representing the link
	 */
	private Node getSimpleLineCorridor(){
		final Line line = new Line(fromPoint.getX(), fromPoint.getY(), toPoint.getX(), toPoint.getY());
		line.setStroke(linkDefaultColor);
		line.setStrokeWidth(linkWidth);
		line.setOnMouseEntered(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				line.setStrokeWidth(linkWidth * 3);
			}
		});
		line.setOnMouseExited(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				line.setStrokeWidth(linkWidth);
			}
		});
		return line;
	}
	
	/**
	 * Given two vectors (starting in 0), this method calculates the angle between them, 
	 * counter-clockwise, in degrees. I.e. if the vectors are, in order, (1,0) and (0,-1), 
	 * the returned value will be 270.
	 * @param vectorA First vector
	 * @param vectorB Second vector
	 * @return Angle between the vectors, counter-clockwise, in degrees.
	 */
	private double angle(Point2D vectorA, Point2D vectorB){
		double angleA = angle(vectorA);
		double angleB = angle(vectorB);
		return angleB - angleA;
	}
	
	/**
	 * Calculates the angle between the vectors, in order, (1,0) and the vector specified
	 * in the parameter, counter-clockwise, in degrees. I.e. if the specified vector is
	 * (-1,-1), the returned value will be 225.
	 * @param vector The vector determining the angle with the unit vector
	 * @return Angle between the vectors, in order, (1,0) and the vector specified
	 * in the parameter, counter-clockwise, in degrees
	 */
	private double angle(Point2D vector){
		double x = vector.getX();
		double y = vector.getY();
		double length = vector.distance(Point2D.ZERO);
		double angle = Math.toDegrees(Math.acos(x / length));
		if (y < 0){
			angle *= -1;
		}
		return angle;
	}
	
	/**
	 * Given the image visualizing the link as a JavaFX node, this method transforms its
	 * positioning and sizing so that it fits exactly in the right place in the map  
	 * @param node The image visualizing the link, as a JavaFX node
	 */
	private void transform(Node node){					
		double centerX = fromPointImage.getX();
		double centerY = fromPointImage.getY();
		Translate layout = new Translate(-centerX, -centerY);
		node.getTransforms().add(layout);
		Translate translate = new Translate(fromPoint.getX(), fromPoint.getY());
		node.getTransforms().add(translate);
		Scale scale = new Scale(shrinkRatio, shrinkRatio, centerX, centerY);
		node.getTransforms().add(scale);				
		Rotate rotate = new Rotate(angle, centerX, centerY);
		node.getTransforms().add(rotate);		
	}
	
	/**
	 * @return The image visualizing the link, 
	 * with correct positioning and resizing already done.
	 */
	private Node getVisualCorridor(){
		if (linkImage == null){
			return getSimpleLineCorridor();
		} else {
			try (InputStream imageStream = Files.newInputStream(Paths.get(linkImage.getSource()))){
				Image image = new Image(imageStream);
				ImageView imageView = new ImageView(image);
				transform(imageView);
				return imageView;
			} catch (IOException ex) {
				return getSimpleLineCorridor();
			}
		}
	}
	
	/**
	 * Given the points specifying a path in the image, this method transforms them into the
	 * visualized map coordinates, and adds the beginning and end points {@link CorridorLoader#fromPoint},
	 * {@link CorridorLoader#toPoint}. The new points are returned in a new collection. 
	 * @return The path transformed into the visualization coordinates
	 */
	private List<Point2D> getConvertedPath(){
		List<Point2D> res = new ArrayList<>();
		res.add(fromPoint);
		if (path != null){
			for (Point2D point : path){
				point = point.subtract(fromPointImage);
				point = point.multiply(shrinkRatio);
				double angle1 = Math.toDegrees(Math.acos(point.getX() / point.distance(0, 0)));
				if (point.getY() < 0){
					angle1 *= -1;
				}
				angle1 += angle;				
				double newX = Math.cos(Math.toRadians(angle1)) * point.distance(0, 0);
				double newY = Math.sin(Math.toRadians(angle1)) * point.distance(0, 0);
				point = new Point2D(newX, newY);
				point = point.add(fromPoint);
				res.add(point);
			}
		}
		res.add(toPoint);
		return res;
	}
	
	/**
	 * @return Corridor instance representing the link specified in the constructor parameters. 
	 */
	public LinkCorridor build(){
		return builtCorridor;
	}
}