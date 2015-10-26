package cz.filipekt.jdcv.corridors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import cz.filipekt.jdcv.network.MyLinkImg;
import javafx.geometry.Point2D;

/**
 * A customized visual representation of links. Allows to specify a custom image
 * to be shown instead of the (default) plain line. Allows to specify a custom
 * path through the image along which the people will move. 
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class Corridor {
	
	/**
	 * Unique identification of the corridor
	 */
	private final String id;
	
	/**
	 * The links which use this corridor as their visual representation.
	 */
	private final Collection<String> links = new HashSet<>();
	
	/**
	 * Specification of the image which represents this link
	 */
	private final MyLinkImg linkImage;
	
	/**
	 * Points specifying the path along which the persons move through the 
	 * link visualization. Coordinates are taken from the image raster.
	 */
	private final List<Point2D> linkPath = new ArrayList<>();
	
	/**
	 * If true, the points in {@link Corridor#linkPath} are given in 
	 * coordinates of the visualization output
	 */
	private final boolean absolutePath;
	
	/**
	 * @param id Unique identification of the corridor
	 * @param links The links which use this corridor as their visual representation.
	 * @param linkImage Specification of the image which represents this link
	 * @param linkPath Points specifying the path along which the persons move through the 
	 * link visualization. Coordinates are taken from the image raster.
	 * @param absolutePath If true, the points in {@link Corridor#linkPath} are given in 
	 * coordinates of the visualization output
	 */
	public Corridor(String id, Collection<String> links, MyLinkImg linkImage, 
			List<Point2D> linkPath,  boolean absolutePath) {
		this.id = id;
		this.links.addAll(links);
		this.linkImage = linkImage;
		this.linkPath.addAll(linkPath);
		this.absolutePath = absolutePath;
	}
	
	/**
	 * @return Unique identification of the corridor
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * @return The links which use this corridor as their visual representation.
	 */
	public Collection<String> getLinks() {
		return links;
	}
	
	/**
	 * @return Specification of the image which represents this link
	 */
	public MyLinkImg getLinkImage() {
		return linkImage;
	}
	
	/**
	 * @return Points specifying the path along which the persons move through the 
	 * link visualization. Coordinates are taken from the image raster.
	 */
	public List<Point2D> getLinkPath() {
		return linkPath;
	}

	/**
	 * @return If true, the points in {@link Corridor#linkPath} are given in 
	 * coordinates of the visualization output
	 */
	public boolean isAbsolutePath() {
		return absolutePath;
	}
}
