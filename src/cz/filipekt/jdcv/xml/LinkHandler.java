package cz.filipekt.jdcv.xml;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import cz.filipekt.jdcv.exceptions.InvalidAttributeValueException;
import cz.filipekt.jdcv.exceptions.NodeNotFoundException;
import cz.filipekt.jdcv.network.MyLink;
import cz.filipekt.jdcv.network.MyLinkBuilder;
import cz.filipekt.jdcv.network.MyNode;

/**
 * SAX handler used to parse the XML file containing the network(map) description.
 * Collects the "link" elements. 
 * Can only be used after the "node" elements have been collected.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class LinkHandler extends DefaultHandler {
	
	/**
	 * Local name of the link XML element.
	 */
	private final String linkName = "link";
	
	/**
	 * Name of the link attribute containing the id of the link
	 */
	private final String idName = "id";
	
	/**
	 * Name of the link attribute containing the id of the node where this link starts.
	 */
	private final String fromName = "from";
	
	/**
	 * Name of the link attribute containing the id of the node where this link ends.
	 */
	private final String toName = "to";
	
	/**
	 * Name of the link attribute containing the length of this link.
	 */
	private final String lengthName = "length";
	
	/**
	 * Name of the link attribute containing the allowed maximum speed of the link 
	 * (for street link), the 'fixed' speed of the link (for public transport links), 
	 * the 'typical' speed of the link (for pedestrian links).
	 */
	private final String freespeedName = "freespeed";
	
	/**
	 * Name of the link attribute containing the maximal capacity of this link 
	 * for a given period (specified by "links" element attribute "capperiod").
	 */
	private final String capacityName = "capacity";
	
	/**
	 * Name of the link attribute containing the number of lanes of this link.
	 */
	private final String permlanesName = "permlanes";
	
	/**
	 * Name of the link attribute containing the comma-separated list of 
	 * transportation modes that are allowed on this link.
	 */
	private final String modesName = "modes";
	
	/**
	 * Contains the {@link MyLink} representations of the encountered link elements.
	 */
	private final Map<String,MyLink> links = new HashMap<>();
	
	/**
	 * @return The {@link MyLink} representations of the encountered link elements.
	 * @see {@link LinkHandler#links}
	 */
	public Map<String, MyLink> getLinks() {
		return links;
	}

	/**
	 * Contains the {@link MyNode} representations of all the "node" elements.
	 */
	private final Map<String,MyNode> nodes;

	/**
	 * The link elements of the network XML source contain mandatory attributes "from" and "to",
	 * which refer to ids of some network nodes. When parsing the link elements, these node ids
	 * are immediately dereferenced into {@link MyNode} representations of nodes. Thus the node
	 * collection is required.
	 * @param nodes Presumably the result of parsing all of the "node" elements in the network XML file. 
	 * Maps the node id to the {@link MyNode} representation of the node.
	 */
	public LinkHandler(Map<String, MyNode> nodes) {
		this.nodes = nodes;
	}

	/**
	 * Called by the {@link XMLReader} during the SAX parsing, when an element is entered.
	 */
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (qName.equals(linkName)){
			processLink(attributes);
		}
	}
	
	/**
	 * When a "link" element is encountered, this method creates a {@link MyLink} representation 
	 * of this element and places it in the {@link LinkHandler#links} map.
	 * @param attributes The attributes of the currently encountered link element.
	 * @throws SAXException When a mandatory attribute is missing, empty or has an invalid value.
	 */
	private void processLink(Attributes attributes) throws SAXException {
		String id = attributes.getValue(idName);
		Utils.ensureNonNullAndNonEmptyAttr(linkName, idName, id);
		String from = attributes.getValue(fromName);
		Utils.ensureNonNullAndNonEmptyAttr(linkName, fromName, from);
		String to = attributes.getValue(toName);
		Utils.ensureNonNullAndNonEmptyAttr(linkName, toName, to);		
		String length = attributes.getValue(lengthName);
		if (!Utils.checkNonNullAndNonEmpty(length)){
			length = null;
		}
		String freespeed = attributes.getValue(freespeedName);
		if (!Utils.checkNonNullAndNonEmpty(freespeed)){
			freespeed = null;
		}
		String capacity = attributes.getValue(capacityName);
		if (!Utils.checkNonNullAndNonEmpty(capacity)){
			capacity = null;
		}
		String permlanes = attributes.getValue(permlanesName);
		if (!Utils.checkNonNullAndNonEmpty(permlanes)){
			permlanes = null;
		}
		String modes = attributes.getValue(modesName);
		if (!Utils.checkNonNullAndNonEmpty(modes)){
			modes = null;
		}
		MyNode fromNode = nodes.get(from);
		MyNode toNode = nodes.get(to);
		if ((fromNode==null) || (toNode==null)){
			throw new SAXException(new NodeNotFoundException());
		}
		try {
			MyLinkBuilder linkBuilder = new MyLinkBuilder();
			linkBuilder.setId(id);
			linkBuilder.setFrom(fromNode);
			linkBuilder.setTo(toNode);
			if (length != null){
				BigDecimal lengthDecimal = new BigDecimal(length);
				linkBuilder.setLength(lengthDecimal);
			}
			if (freespeed != null){
				double freespeedDouble = Double.parseDouble(freespeed);
				linkBuilder.setFreespeed(freespeedDouble);
			}
			if (capacity != null){
				double capacityDouble = Double.parseDouble(capacity);
				linkBuilder.setCapacity(capacityDouble);
			}
			if (permlanes != null){
				double permlanesDouble = Double.parseDouble(permlanes);
				linkBuilder.setNumberOfLanes(permlanesDouble);
			}			
			if (modes != null){
				String[] modesArray = modes.split(",");
				linkBuilder.setAllowedModes(modesArray);
			}	
			MyLink link = linkBuilder.build();
			links.put(link.getId(), link);
		} catch (NumberFormatException ex){
			throw new SAXException(new InvalidAttributeValueException(
					"Numeric attributes of the link element must be in the \"double precision\" format."));
		}
	}
	
}