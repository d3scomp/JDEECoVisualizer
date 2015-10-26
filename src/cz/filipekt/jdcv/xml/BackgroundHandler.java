package cz.filipekt.jdcv.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import cz.filipekt.jdcv.corridors.Background;
import javafx.scene.paint.Color;

/**
 * SAX handler used to parse the XML file containing the map definition.
 * Collects the background element. 
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class BackgroundHandler extends DefaultHandler {
	
	/**
	 * Local name of the background element
	 */
	private final String backgroundName = "background";
	
	/**
	 * Name of the "image" attribute of the background element
	 */
	private final String imageAttributeName = "image";
	
	/**
	 * Name of the "color" attribute of the background element
	 */
	private final String colorAttributeName = "color";
	
	/**
	 * Name of the attribute, which contains the x-coordinate of 
	 * the point where the left-top corner of the background image 
	 * will be placed in the visualization
	 */
	private final String leftTopX = "leftTopX";
	
	/**
	 * Name of the attribute, which contains the y-coordinate of 
	 * the point where the left-top corner of the background image 
	 * will be placed in the visualization
	 */
	private final String leftTopY = "leftTopY";
	
	/**
	 * Name of the attribute, which contains the x-coordinate of 
	 * the point where the right-bottom corner of the background image 
	 * will be placed in the visualization
	 */
	private final String rightBottomX = "rightBottomX";
	
	/**
	 * Name of the attribute, which contains the y-coordinate of 
	 * the point where the right-bottom corner of the background image 
	 * will be placed in the visualization
	 */
	private final String rightBottomY = "rightBottomY";
	
	/**
	 * The parsed representation of the background element
	 */
	private Background result;

	/**
	 * @return The parsed representation of the background element
	 */
	public Background getResult() {
		return result;
	}

	@Override
	public void startElement(String uri, String localName, String qName, 
			Attributes attributes) throws SAXException {
		if (qName.equals(backgroundName)){
			String imageAttribute = attributes.getValue(imageAttributeName);
			String colorAttribute = attributes.getValue(colorAttributeName);
			String ltxAttribute = attributes.getValue(leftTopX);
			String ltyAttribute = attributes.getValue(leftTopY);
			String rbxAttribute = attributes.getValue(rightBottomX);
			String rbyAttribute = attributes.getValue(rightBottomY);
			if (Utils.checkNonNullAndNonEmpty(colorAttribute)){
				try {
					Color color = Color.valueOf(colorAttribute);
					result = new Background(color);
				} catch (NullPointerException | IllegalArgumentException ex) {}
			}
			if (Utils.checkNonNullAndNonEmpty(imageAttribute) && 
					Utils.checkNonNullAndNonEmpty(ltxAttribute) &&
					Utils.checkNonNullAndNonEmpty(ltyAttribute) &&
					Utils.checkNonNullAndNonEmpty(rbxAttribute) &&
					Utils.checkNonNullAndNonEmpty(rbyAttribute)
					){
				try {
					double ltx = Double.parseDouble(ltxAttribute);
					double lty = Double.parseDouble(ltyAttribute);
					double rbx = Double.parseDouble(rbxAttribute);
					double rby = Double.parseDouble(rbyAttribute);
					result = new Background(imageAttribute, ltx, lty, rbx, rby);
				} catch (IllegalArgumentException ex) {}
			}
		}
	}
	
}
