package cz.filipekt.jdcv.xml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import cz.filipekt.jdcv.corridors.Corridor;
import cz.filipekt.jdcv.exceptions.InvalidAttributeValueException;
import cz.filipekt.jdcv.network.MyLink;
import cz.filipekt.jdcv.network.MyLinkImg;
import javafx.geometry.Point2D;

/**
 * SAX handler used to parse the XML file containing the map definition.
 * Collects the corridor elements. 
 * Can only be used after the "link" elements have been collected, as these
 * are required upon construction of this handler instance.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class CorridorHandler extends DefaultHandler {
	
	/**
	 * Local name of the corridor element
	 */
	private final String corridorName = "corridor";
	
	/**
	 * Name of the id attribute of the corridor element
	 */
	private final String corridorIdName = "id";
	
	/**
	 * Name of the links attribute of the corridor element
	 */
	private final String corridorLinksName = "links";
	
	/**
	 * Delimiter of link IDs inside the "links" attribute of "corridor" element
	 */
	private final String linksDelimiter = ",";
	
	/**
	 * Local name of the link_img element
	 */
	private final String linkImgName = "link_img";
	
	/**
	 * Local name of the link_path element
	 */
	private final String linkPathName = "link_path";
	
	/**
	 * Name of the link_path attribute specifying whether the points in the 
	 * link_path are given in coordinates of the visualization output
	 */
	private final String linkPathAbsoluteName = "absolute";
	
	/**
	 * The positive value of the {@link CorridorHandler#linkPathAbsoluteName} 
	 * attribute. Checking is done in a case-insensitive manner.
	 */
	private final String linkPathAbsoluteTrue = "true";
	
	/**
	 * Name of the attribute specifying the path to the image that represents a link
	 */
	private final String linkImgSourceName = "source";
	
	/**
	 * Name of the attribute specifying the x-coordinate of the point in the
	 * link visualization where the link starts.
	 */
	private final String linkImgFromXName = "fromx";
	
	/**
	 * Name of the attribute specifying the y-coordinate of the point in the
	 * link visualization where the link starts.
	 */
	private final String linkImgFromYName = "fromy";
	
	/**
	 * Name of the attribute specifying the x-coordinate of the point in the
	 * link visualization where the link ends.
	 */
	private final String linkImgToXName = "tox";
	
	/**
	 * Name of the attribute specifying the y-coordinate of the point in the
	 * link visualization where the link ends.
	 */
	private final String linkImgToYName = "toy";
	
	/**
	 * When set, the XML parser is currently inside a corridor element
	 */
	private boolean inCorridor = false;
	
	/**
	 * Visual representation of the last encountered corridor
	 */
	private MyLinkImg currentLinkImg;
	
	/**
	 * ID of the last encountered corridor element
	 */
	private String currentCorridorId;
	
	/**
	 * Marks, whether the points in the current link path are given in 
	 * coordinates of the visualization output
	 */
	private boolean currentPathAbsolute;
	
	/**
	 * Link IDs given in the "links" attribute of the last encountered corridor element
	 */
	private final Collection<String> currentCorridorLinks = new HashSet<>();
	
	/**
	 * The points defined inside the link_path element of the last encountered corridor element
	 */
	private final List<Point2D> currentLinkPath = new ArrayList<>();
	
	/**
	 * Local name of the point element (specifying a point in the map)
	 */
	private final String pointName = "point";
	
	/**
	 * Name of the attribute specifying the x-coordinate of a point
	 */
	private final String pointXName = "x";
	
	/**
	 * Name of the attribute specifying the y-coordinate of a point
	 */
	private final String pointYName = "y";
	
	/**
	 * The parsed link elements
	 */
	private final Map<String,MyLink> links;

	/**
	 * @param links The parsed link elements
	 */
	public CorridorHandler(Map<String, MyLink> links) {
		this.links = links;
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equals(corridorName)){
			Corridor newCorridor = new Corridor(currentCorridorId, currentCorridorLinks, 
					currentLinkImg, currentLinkPath, currentPathAbsolute);
			Collection<MyLink> parsedLinks = getParsedLinksFor(currentCorridorLinks);
			for (MyLink link : parsedLinks){
				link.setCorridor(newCorridor);
			}
			inCorridor = false;
		}
	}
	
	/**
	 * @param linkIDs ID values of some link elements
	 * @return The parsed link elements defined by the ID values given in the parameter
	 */
	private Collection<MyLink> getParsedLinksFor(Collection<String> linkIDs){
		Collection<MyLink> res = new HashSet<>();
		if ((links != null) && !links.isEmpty() && 
				(linkIDs != null) && !linkIDs.isEmpty()){
			for (String id : linkIDs){
				MyLink parsed = links.get(id);
				if (parsed != null){
					res.add(parsed);
				}
			}
		}
		return res;
	}

	/**
	 * Called by the SAX engine whenever one of the corridor-related elements is 
	 * entered. The actual processing work is delegated to specialized methods,
	 * depending on the element name.
	 */
	@Override
	public void startElement(String uri, String localName, String qName, 
			Attributes attributes) throws SAXException {
		if (qName.equals(corridorName)){
			processCorridor(attributes);
		} else if (inCorridor){
			if (qName.equals(linkImgName)){
				processLinkImg(attributes);
			} else if (qName.equals(pointName)){
				processPoint(attributes);
			} else if (qName.equals(linkPathName)){
				processLinkPath(attributes);
			}
		}
	}
	
	/**
	 * Called when a link_path element is encountered. Collects the "absolute"
	 * attribute and saves its value.
	 * @param attributes The attributes of the link_path element
	 */
	private void processLinkPath(Attributes attributes){
		String absolute = attributes.getValue(linkPathAbsoluteName);
		currentPathAbsolute = Utils.checkNonNullAndNonEmpty(absolute) && 
				absolute.equalsIgnoreCase(linkPathAbsoluteTrue);
	}
	
	/**
	 * Called when a corridor element is encountered. It parses its attributes
	 * and clears the storage for the current values of link_img and link_path
	 * (i.e. the {@link CorridorHandler#currentLinkImg} and 
	 * {@link CorridorHandler#currentLinkPath} 
	 * @param attributes The attributes of the link_img element
	 * @throws SAXException When a mandatory attribute is missing, empty or 
	 * has an invalid value.
	 */
	private void processCorridor(Attributes attributes) throws SAXException{
		String idValue = attributes.getValue(corridorIdName);
		Utils.ensureNonNullAndNonEmptyAttr("corridor", "id", idValue);
		currentCorridorId = idValue;
		String linksValue = attributes.getValue(corridorLinksName);
		Utils.ensureNonNullAndNonEmptyAttr("corridor", "links", linksValue);
		String[] links = linksValue.split(linksDelimiter);
		currentCorridorLinks.clear();
		Collections.addAll(currentCorridorLinks, links);
		currentLinkImg = null;
		currentLinkPath.clear();
		inCorridor = true;
	}
	
	/**
	 * Called when a link_img element is encountered. It parses the element and
	 * puts the parsed form into {@link LinkHandler#currentLinkImg} 
	 * @param attributes The attributes of the link_img element
	 * @throws SAXException When a mandatory attribute is missing, empty or has an invalid value.
	 */
	private void processLinkImg(Attributes attributes) throws SAXException{
		String source = attributes.getValue(linkImgSourceName);
		Utils.ensureNonNullAndNonEmptyAttr(linkImgName, linkImgSourceName, source);
		String fromx = attributes.getValue(linkImgFromXName);
		Utils.ensureNonNullAndNonEmptyAttr(linkImgName, linkImgFromXName, fromx);
		String fromy = attributes.getValue(linkImgFromYName);
		Utils.ensureNonNullAndNonEmptyAttr(linkImgName, linkImgFromYName, fromy);
		String tox = attributes.getValue(linkImgToXName);
		Utils.ensureNonNullAndNonEmptyAttr(linkImgName, linkImgToXName, tox);
		String toy = attributes.getValue(linkImgToYName);
		Utils.ensureNonNullAndNonEmptyAttr(linkImgName, linkImgToYName, toy);
		try {
			int fromXNum = Integer.parseInt(fromx);
			int fromYNum = Integer.parseInt(fromy);
			int toXNum = Integer.parseInt(tox);
			int toYNum = Integer.parseInt(toy);
			currentLinkImg = new MyLinkImg(source, fromXNum, fromYNum, toXNum, toYNum);
		} catch (NumberFormatException ex){
			throw new SAXException(new InvalidAttributeValueException(
					"Numeric attributes of the link_img element must be in the integer format."));
		}
	}
	
	/**
	 * Called when a point element is encountered. It parses the element and
	 * adds the parsed form to {@link CorridorHandler#currentLinkPath}
	 * @param attributes Attributes of the point element.
	 * @throws SAXException When a mandatory attribute is missing, empty or has an invalid value.
	 */
	private void processPoint(Attributes attributes) throws SAXException{
		String xStr = attributes.getValue(pointXName);
		Utils.ensureNonNullAndNonEmptyAttr(pointName, pointXName, xStr);
		String yStr = attributes.getValue(pointYName);
		Utils.ensureNonNullAndNonEmptyAttr(pointName, pointYName, yStr);
		try {
			double x = Double.parseDouble(xStr);
			double y = Double.parseDouble(yStr);
			Point2D point = new Point2D(x, y);
			currentLinkPath.add(point);
		} catch (NumberFormatException ex){
			throw new SAXException(new InvalidAttributeValueException(
					"Numeric attributes of the point element must be in the double precision format."));
		}
	}
}
