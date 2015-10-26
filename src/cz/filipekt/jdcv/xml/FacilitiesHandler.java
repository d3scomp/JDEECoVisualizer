package cz.filipekt.jdcv.xml;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import cz.filipekt.jdcv.exceptions.InvalidAttributeValueException;
import cz.filipekt.jdcv.network.MyActivity;
import cz.filipekt.jdcv.network.MyDayValue;
import cz.filipekt.jdcv.network.MyFacility;
import cz.filipekt.jdcv.network.MyOpenTime;

/**
 * SAX handler used to parse the XML file containing the facilities description.
 * Collects the "facility" XML elements.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class FacilitiesHandler extends DefaultHandler {
	
	/**
	 * Local name of the "facility" XML element
	 */
	private final String facilityName = "facility";
	
	/**
	 * Local name of the "activity" XML element
	 */
	private final String activityName = "activity";
	
	/**
	 * Name of the "type" attribute of the "activity" XML element
	 */
	private final String activityTypeName = "type";
	
	/**
	 * Local name of the "capacity" XML element
	 */
	private final String capacityName = "capacity";
	
	/**
	 * Local name of the "value" attribute of the "capacity" XML element
	 */
	private final String capacityValueName = "value";
	
	/**
	 * Local name of the "opentime" XML element
	 */
	private final String openTimeName = "opentime";
	
	/**
	 * Name of the "day" attribute of the "opentime" XML element
	 */
	private final String dayName = "day";
	
	/**
	 * Name of the "startTime" attribute of the "opentime" XML element
	 */
	private final String startTimeName = "start_time";
	
	/**
	 * Name of the "endTime" attribute of the "opentime" XML element
	 */
	private final String endTimeName = "end_time";
	
	/**
	 * Name of the "id" attribute of the "facility" XML element
	 */
	private final String facilityIdName = "id";
	
	/**
	 * Name of the "x" attribute of the "facility" XML element
	 */
	private final String facilityXName = "x";
	
	/**
	 * Name of the "y" attribute of the "facility" XML element
	 */
	private final String facilityYName = "y";
	
	/**
	 * Holds the id of the currently parsed facility element.
	 */
	private String facilityIdVal;
	
	/**
	 * Holds the x-coordinate of the currently parsed facility element.
	 */
	private double facilityXVal;
	
	/**
	 * Holds the y-coordinate of the currently parsed facility element.
	 */
	private double facilityYVal;
	
	/**
	 * Holds the "type" attribute value of the currently parsed activity element.
	 */
	private String activityTypeVal;
	
	/**
	 * Holds the capacity of the currently parsed activity element.
	 */
	private long activityCapacity = Long.MAX_VALUE;
	
	/**
	 * Storage of the "opentime" child elements of the currently parsed activity element.
	 */
	private List<MyOpenTime> activityOpenTimes = new ArrayList<>();
	
	/**
	 * Storage of the "activity" child elements of the currently parsed facility element.
	 */
	private List<MyActivity> facilityActivities = new ArrayList<>();
	
	/**
	 * Storage of the already parsed facility elements.
	 */
	private final Map<String,MyFacility> facilities = new HashMap<>();

	/**
	 * @return The parsed facility elements.
	 * @see {@link FacilitiesHandler#facilities}
	 */
	public Map<String, MyFacility> getFacilities() {
		return facilities;
	}


	/**
	 * Called by the {@link XMLReader} during the SAX parsing, when an element is entered.
	 * Makes sure that "facility" elements and their children are properly processed.
	 * @throws SAXException When a mandatory attribute is missing or has an invalid value.
	 */
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (qName.equals(facilityName)){
			facilityIdVal = attributes.getValue(facilityIdName);
			Utils.ensureNonNullAndNonEmptyAttr(facilityName, facilityIdName, facilityIdVal);
			String x = attributes.getValue(facilityXName);
			Utils.ensureNonNullAndNonEmptyAttr(facilityName, facilityXName, x);
			String y = attributes.getValue(facilityYName);
			Utils.ensureNonNullAndNonEmptyAttr(facilityName, facilityYName, y);
			try {
				facilityXVal = Double.parseDouble(x);
				facilityYVal = Double.parseDouble(y);
			} catch (NumberFormatException ex){
				throw new SAXException(new InvalidAttributeValueException(
						"Numeric attributes of the " + facilityName + " element must be in the \"double precision\" format."));
			}
		}
		if (qName.equals(activityName)){
			activityTypeVal = attributes.getValue(activityTypeName);
			Utils.ensureNonNullAndNonEmptyAttr(activityName, activityTypeName, activityTypeVal);
		}
		if (qName.equals(capacityName)){
			String capVal = attributes.getValue(capacityValueName);
			Utils.ensureNonNullAndNonEmptyAttr(capacityName, capacityValueName, capVal);
			try {
				activityCapacity = Long.parseLong(capVal.split("\\.")[0]);
			} catch (NumberFormatException ex){
				throw new SAXException(new InvalidAttributeValueException(
						capacityValueName + " attribute of the " + capacityName + " element must be in the long integer format."));
			}
		}
		if (qName.equals(openTimeName)){		
			String day = attributes.getValue(dayName);
			Utils.ensureNonNullAndNonEmptyAttr(openTimeName, dayName, day);
			String startTime = attributes.getValue(startTimeName);
			Utils.ensureNonNullAndNonEmptyAttr(openTimeName, startTimeName, startTime);
			String endTime = attributes.getValue(endTimeName);
			Utils.ensureNonNullAndNonEmptyAttr(openTimeName, endTimeName, endTime);
			try {
				MyOpenTime ot = parseOpenTimeFrom(day, startTime, endTime);
				activityOpenTimes.add(ot);
			} catch (InvalidAttributeValueException ex){
				throw new SAXException(ex);
			}
		}
	}
	
	
	/**
	 * Called by the {@link XMLReader} during the SAX parsing, after an element has been left.
	 * Makes sure that "facility" elements and their children are properly processed.
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (qName.equals(activityName)){
			MyActivity act = new MyActivity(activityTypeVal, activityCapacity, activityOpenTimes);
			facilityActivities.add(act);
			activityCapacity = Long.MAX_VALUE;
			activityOpenTimes = new ArrayList<>();
		}
		if (qName.equals(facilityName)){
			MyFacility fac = new MyFacility(facilityIdVal, facilityXVal, facilityYVal, facilityActivities);
			facilities.put(facilityIdVal, fac);
			facilityActivities = new ArrayList<>();
		}
	}
	
	/**
	 * Given the String values of the attributes in the "opentime: XML element, this 
	 * method constructs a new {@link MyOpenTime} object representing the element.
	 * @param day Value of the "day" attribute of the "opentime" XML element
	 * @param start Value of the "start" attribute of the "opentime" XML element
	 * @param end Value of the "end" attribute of the "opentime" XML element
	 * @return {@link MyOpenTime} representation the the "opentime" XML element specified by its attribute values
	 * @throws InvalidAttributeValueException if some of the parameters does not contain a value in a valid format
	 */
	private MyOpenTime parseOpenTimeFrom(String day, String start, String end) throws InvalidAttributeValueException {
		try {
			MyDayValue dayVal = MyDayValue.valueOf(day.toUpperCase());
			Date startDate = MyOpenTime.dateFormat.parse(start);
			Date endDate = MyOpenTime.dateFormat.parse(end);
			return new MyOpenTime(dayVal, startDate, endDate);
		} catch (IllegalArgumentException | ParseException | NullPointerException ex){
			throw new InvalidAttributeValueException(
					openTimeName + " element attributes are in an invalid format");
		}
	}
	
}
