package cz.filipekt.jdcv.xml;

import org.xml.sax.SAXException;

import cz.filipekt.jdcv.exceptions.MandatoryAttributeNotFoundException;

/**
 * Contains static utility methods for use inside the package.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class Utils {
	
	/**
	 * Ensures that the given attribute value is non-null and non-empty.
	 * @param element Element which holds the attribute
	 * @param attrName Name of the attribute
	 * @param attrValue Value of the attribute
	 * @throws SAXException When the attribute value is either null or empty.
	 */
	static void ensureNonNullAndNonEmptyAttr(String element, String attrName, String attrValue) throws SAXException{
		if ((attrValue == null) || attrValue.isEmpty()){
			throw new SAXException(new MandatoryAttributeNotFoundException("The " + attrName + " attribute of the " + 
					element + " element must always be present."));
		}
	}
	
	/**
	 * Checks whether the given String is not null and not empty.
	 * @param value The String to be examined
	 */
	static boolean checkNonNullAndNonEmpty(String value) {
		if ((value == null) || value.isEmpty()){
			return false;
		} else {
			return true;
		}
	}
	
}