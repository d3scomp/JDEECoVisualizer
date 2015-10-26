package cz.filipekt.jdcv.xml;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import cz.filipekt.jdcv.exceptions.InvalidAttributeValueException;
import cz.filipekt.jdcv.network.MyNode;

/**
 * SAX handler used to parse the XML file containing the network(map) description.
 * Collects the "node" elements.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class NodeHandler extends DefaultHandler {
	
	/**
	 * Local name of the node element.
	 */
	private final String nodeName = "node";
	
	/**
	 * Name of the attribute containing the id of the node.
	 */
	private final String idName = "id";
	
	/**
	 * Name of the attribute containing the x coordinate of the node.
	 */
	private final String xName = "x";
	
	/**
	 * Name of the attribute containing the y coordinate of the node.
	 */
	private final String yName = "y";
	
	/**
	 * Contains the {@link MyNode} representations of the encountered "node" elements.
	 */
	private final Map<String,MyNode> nodes = new HashMap<>();

	/**
	 * @return the {@link MyNode} representations of the encountered "node" elements.
	 * @see {@link NodeHandler#nodes}
	 */
	public Map<String, MyNode> getNodes() {
		return nodes;
	}

	/**
	 * Called by the {@link XMLReader} during the SAX parsing, when an element is entered.
	 * Makes sure that when the name of the entered element is "node", proper actions are taken.
	 * When a "node" elements is encountered, this method creates a {@link MyNode} representation 
	 * of this element and places it in the {@link NodeHandler#nodes} map.
	 * @throws SAXException When a mandatory attribute is missing or has an invalid value.
	 */
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (qName.equals(nodeName)){
			String id = attributes.getValue(idName);
			Utils.ensureNonNullAndNonEmptyAttr(nodeName, idName, id);
			String x = attributes.getValue(xName);
			Utils.ensureNonNullAndNonEmptyAttr(nodeName, xName, x);
			String y = attributes.getValue(yName);
			Utils.ensureNonNullAndNonEmptyAttr(nodeName, yName, y);
			try {
				double xd = Double.parseDouble(x);
				double yd = Double.parseDouble(y);
				MyNode node = new MyNode(id, xd, yd);
				nodes.put(node.getId(), node);
			} catch (NumberFormatException ex){
				throw new SAXException(new InvalidAttributeValueException(
						"x and y attributes of the node element must be in the \"double precision\" format"));
			}
		}
	}
	
}
