package cz.filipekt.jdcv.corridors;

import java.util.List;

import javafx.geometry.Point2D;
import javafx.scene.Node;

/**
 * Visual representation of a link. 
 * Contains information both about the actual visualization (i.e. the {@link Node} instance)
 * as well as description of the path through which the persons/cars will be moving
 * when passing through the link. This path is described as an ordered list of points.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class LinkCorridor {
	
	/**
	 * ID of the link that is represented by this class
	 */
	private final String linkID;
	
	/**
	 * @return ID of the link that is represented by this class
	 * @see {@link LinkCorridor#linkID}
	 */
	public String getLinkID() {
		return linkID;
	}

	/**
	 * Visual representation of the link
	 */
	private final Node visualization;
	
	/**
	 * @return Visual representation of the link
	 * @see {@link LinkCorridor#visualization}
	 */
	public Node getVisualization() {
		return visualization;
	}

	/**
	 * Ordered list of points that define the path along which people/cars will 
	 * be moving when going through this link. Note that this path does not formally
	 * depend on {@link LinkCorridor#visualization}, even though they should probably
	 * go roughly the same way in a reasonable visualization.
	 */
	private final List<Point2D> pathPoints;
	
	/**
	 * @return Ordered list of points that define the path along which people/cars will 
	 * be moving when going through this link.
	 * @see {@link LinkCorridor#pathPoints}
	 */
	public List<Point2D> getPathPoints() {
		return pathPoints;
	}

	/**
	 * i-th element determines the distance between i-th and (i+1)-th elements
	 * of {@link LinkCorridor#pathPoints}
	 */
	private final double[] pathDistances;
	
	/**
	 * i-th element marks the relative distance of i-th point between the first and 
	 * last points. Unit of measurement - unit-less number from [0,1]
	 */
	private final double[] relativeDistances;
	
	/**
	 * @return i-th element marks the relative distance of i-th point between the 
	 * first and last points. Unit of measurement - unit-less number from [0,1]
	 */
	public double[] getRelativeDistances() {
		return relativeDistances;
	}

	/**
	 * @return Coordinates (in the visualization) of the point where this link starts
	 */
	public Point2D getFromPoint(){
		return pathPoints.get(0);
	}
	
	/**
	 * @return Coordinates (in the visualization) of the point where this link ends
	 */
	public Point2D getToPoint(){
		return pathPoints.get(pathPoints.size()-1);
	}
	
	/**
	 * @param linkID ID of the link that is represented by this class
	 * @param visual Visual representation of the link
	 * @param pathPoints Ordered list of points that define the path along which 
	 * people/cars will be moving when going through this link.
	 */
	public LinkCorridor(String linkID, Node visual, List<Point2D> pathPoints) {
		if (linkID == null){
			throw new IllegalArgumentException("The construction parameters for " + 
					getClass().getName() + " cannot be null.");
		} else {
			this.linkID = linkID;
		}
		if (visual == null){
			throw new IllegalArgumentException("The construction parameters for " + 
					getClass().getName() + " cannot be null.");
		} else {
			this.visualization = visual;
		}
		if (pathPoints == null){
			throw new IllegalArgumentException("The construction parameters for " + 
					getClass().getName() + " cannot be null.");
		} else {
			if (pathPoints.size() < 2){
				throw new IllegalArgumentException("The path points must contain at least " + 
						"two points, i.e. at least the initial and the end point.");
			} else {
				this.pathPoints = pathPoints;
				pathDistances = new double[pathPoints.size() - 1];
				for (int i = 0; i < (pathPoints.size() - 1); i++){
					Point2D thisPoint = pathPoints.get(i);
					Point2D nextPoint = pathPoints.get(i+1);
					double dist = thisPoint.distance(nextPoint);
					pathDistances[i] = dist;
				}
				double totalDistance = 0;
				for (double dist : pathDistances){
					totalDistance += dist;
				}
				double[] cumulativeDistances = new double[pathPoints.size()];
				cumulativeDistances[0] = 0;
				for (int i = 1; i < pathPoints.size(); i++){
					cumulativeDistances[i] = cumulativeDistances[i-1] + pathDistances[i-1];
				}
				double[] relatives = new double[pathPoints.size()];
				for (int i = 0; i < pathPoints.size(); i++){
					relatives[i] = cumulativeDistances[i] / totalDistance;
				}
				this.relativeDistances = relatives;
			}
		}
		
	}
}
