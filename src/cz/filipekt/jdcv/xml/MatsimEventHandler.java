package cz.filipekt.jdcv.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import cz.filipekt.jdcv.events.ActStartOrEnd;
import cz.filipekt.jdcv.events.ArrivalOrDeparture;
import cz.filipekt.jdcv.events.EnteredOrLeftLink;
import cz.filipekt.jdcv.events.EntersOrLeavesVehicle;
import cz.filipekt.jdcv.events.MatsimEvent;
import cz.filipekt.jdcv.events.EventType;
import cz.filipekt.jdcv.exceptions.InvalidAttributeValueException;
import cz.filipekt.jdcv.exceptions.LinkNotFoundException;
import cz.filipekt.jdcv.exceptions.TooManyEvents;
import cz.filipekt.jdcv.network.MyLink;

/**
 * SAX handler used to parse the XML file containing the events.
 * Collects the "event" elements. 
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class MatsimEventHandler extends DefaultHandler {
	
	/**
	 * Local name of the event element
	 */
	private final String eventName = "event";
	
	/**
	 * Name of the type attribute of the event element
	 */
	private final String typeName = "type";
	
	/**
	 * The parsed event elements from the source file
	 */
	private final List<MatsimEvent> events = new ArrayList<>();
	
	/**
	 * @return The parsed event elements from the source file
	 * @see {@link MatsimEventHandler#events}
	 */
	public List<MatsimEvent> getEvents() {
		return events;
	}

	/**
	 * Name of the time attribute of the event element
	 */
	private final String timeName = "time";
	
	/**
	 * Name of the person attribute of the event element
	 */
	private final String personName = "person";
	
	/**
	 * Name of the vehicle attribute of the event element
	 */
	private final String vehicleName = "vehicle";
	
	/**
	 * Name of the link attribute of the event element
	 */
	private final String linkName = "link";
	
	/**
	 * Name of the legMode attribute of the event element
	 */
	private final String legModeName = "legMode";
	
	/**
	 * Name of the facility attribute of the event element
	 */
	private final String facilityName = "facility";
	
	/**
	 * Name of the actType attribute of the event element
	 */
	private final String actTypeName = "actType";
	
	/**
	 * Collection of parsed link elements, as extracted from a network source file.
	 */
	private final Map<String,MyLink> links;
	
	/**
	 * If true, only the events of the people corresponding to the injected JDEECo components will be
	 * taken into account. If false, all of the events will be parsed.
	 */
	private final boolean onlyComponents;
	
	/**
	 * If true, only the events starting after time {@link EnsembleHandler#startAtLimit}
	 * are taken into account 
	 */
	private final boolean startAtConstraint;
	
	/**
	 * If {@link EnsembleHandler#startAtConstraint} holds, only events starting from
	 * this time on are taken into account
	 */
	private final double startAtLimit;
	
	/**
	 * If true, only the events ending before time {@link EnsembleHandler#endAtLimit}
	 * are taken into account
	 */
	private final boolean endAtConstraint;
	
	/**
	 * If {@link EnsembleHandler#endAtConstraint} holds, only the events ending before this
	 * time are taken into account
	 */
	private final double endAtLimit;

	/**
	 * @param links Collection of parsed link elements, as extracted from a network source file.
	 * @param onlyComponents If true, only the events of the people corresponding to the injected 
	 * JDEECo components will be taken into account. If false, all of the events will be parsed.
	 * @param startAt Only events starting from this time on are taken into account. If null,
	 * no such constraint is applied.
	 * @param endAt Only the events ending before this time are taken into account. If null,
	 * no such constraint is applied.
	 */
	public MatsimEventHandler(Map<String, MyLink> links, boolean onlyComponents, Double startAt, 
			Double endAt) {		
		this.links = links;
		this.onlyComponents = onlyComponents;
		if (startAt == null){
			startAtConstraint = false;
			startAtLimit = -1;
		} else {
			startAtConstraint = true;
			startAtLimit = startAt;
		}
		if (endAt == null){
			endAtConstraint = false;
			endAtLimit = -1;
		} else {
			endAtConstraint = true;
			endAtLimit = endAt;
		}
	}
	
	/**
	 * @param personID An ID of a person that appears in the MATSIM event log
	 * @return True if the person is an injected JDEECo component, false if it is
	 * a regular MATSIM person.
	 */
	private boolean isInjectedComponent(String personID){
		return personID.startsWith("V");
	}
	
	/**
	 * Number of event elements encountered
	 */
	private long count = 0;
	
	/**
	 * Maximal allowed number of event elements in the selection
	 */
	private final long countLimit = 600_000L;

	/**
	 * Makes sure that when an event element is encountered, correct parsing is carried out.
	 * For each event type, the parsing work is done by specialized methods.
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equals(eventName)){
			count += 1;
			if (count > countLimit){
				String message = "The selection contains too many <event> elements. \n" + 
						"Please specify a selection of the log file which contains at most " +
						countLimit + " elements.";
				throw new SAXException(new TooManyEvents(message));
			}
			String timeVal = attributes.getValue(timeName);
			Utils.ensureNonNullAndNonEmptyAttr(eventName, timeName, timeVal);
			double time;
			try {
				time = Double.parseDouble(timeVal);
			} catch (NumberFormatException ex){
				throw new SAXException(new InvalidAttributeValueException(
						"Time attribute of the event element must be in the \"double precision\" format"));
			}
			if (startAtConstraint && (startAtLimit > time)){
				return;
			}
			if (endAtConstraint && (endAtLimit < time)){
				return;
			}
			String typeVal = attributes.getValue(typeName);
			EventType type = EventType.from(typeVal);
			if (type != null){
				String personVal = attributes.getValue(personName);
				Utils.ensureNonNullAndNonEmptyAttr(eventName, personName, personVal);
				if (!onlyComponents || isInjectedComponent(personVal)){
					switch(type){
						case PERSON_ENTERS_VEHICLE:
							processEnteredOrLVehicle(attributes, time, personVal, true);						
							break;
						case PERSON_LEAVES_VEHICLE:
							processEnteredOrLVehicle(attributes, time, personVal, false);
							break;
						case ENTERED_LINK:
							processEorLLink(attributes, time, personVal, true);
							break;
						case LEFT_LINK:
							processEorLLink(attributes, time, personVal, false);
							break;
						case ARRIVAL:
							processArrivalDeparture(attributes, time, personVal, false);
							break;
						case DEPARTURE:
							processArrivalDeparture(attributes, time, personVal, true);
							break;
						case ACT_START:
							processActStartEnd(attributes, time, personVal, true);
							break;
						case ACT_END:
							processActStartEnd(attributes, time, personVal, false);
							break;
						default:
							return;
					}
				}
			}
		}
	}
	
	/**
	 * Parses the PersonEntersVehicle and PersonLeavesVehicle events.
	 * @param attributes Attributes of the event element. Usually provided by the SAX engine.
	 * @param time Simulation time at which the processed event occurred
	 * @param personVal This person is associated with the processed event
	 * @param entered When true, the event type is PersonEntersVehicle. 
	 * When false, the event type is PersonLeavesVehicle.
	 * @throws SAXException When a mandatory attribute is missing or has an invalid value.
	 */
	private void processEnteredOrLVehicle(Attributes attributes, double time, String personVal, boolean entered) 
			throws SAXException{
		String vehicleVal = attributes.getValue(vehicleName);
		Utils.ensureNonNullAndNonEmptyAttr("\"entered or left vehicle\" event", vehicleName, vehicleVal);
		EntersOrLeavesVehicle elv = new EntersOrLeavesVehicle(entered, time, personVal, vehicleVal);
		events.add(elv);
	}
	
	/**
	 * Parses the "entered link" and "left link" events.
	 * @param attributes Attributes of the event element. Usually provided by the SAX engine.
	 * @param time Simulation time at which the processed event occurred
	 * @param personVal This person is associated with the processed event
	 * @param entered When true, the event type is "entered link".
	 * When false, the event type is "left link".
	 * @throws SAXException When a mandatory attribute is missing or has an invalid value.
	 */
	private void processEorLLink(Attributes attributes, double time, String personVal, boolean entered) 
			throws SAXException{
		String linkVal = attributes.getValue(linkName);
		Utils.ensureNonNullAndNonEmptyAttr("\"entered or left link\" event", linkName, linkVal);
		String vehicleVal = attributes.getValue(vehicleName);
		MyLink link = links.get(linkVal);
		if (link == null){
			throw new SAXException(new LinkNotFoundException());
		}			
		EnteredOrLeftLink ell = new EnteredOrLeftLink(entered, time, personVal, link, vehicleVal);
		events.add(ell);
	}
	
	/**
	 * Parses the arrival and departure events.
	 * @param attributes Attributes of the event element. Usually provided by the SAX engine.
	 * @param time Simulation time at which the processed event occurred
	 * @param personVal This person is associated with the processed event
	 * @param departure When true, the event type is departure. When false, the event type is arrival.
	 * @throws SAXException When a mandatory attribute is missing or has an invalid value.
	 */
	private void processArrivalDeparture(Attributes attributes, double time, String personVal, boolean departure) 
			throws SAXException{
		String linkVal = attributes.getValue(linkName);
		Utils.ensureNonNullAndNonEmptyAttr("\"arrival or departure\" event", linkName, linkVal);
		String legModeVal = attributes.getValue(legModeName);
		if (!Utils.checkNonNullAndNonEmpty(legModeVal)){
			legModeVal = null;
		}
		MyLink link = links.get(linkVal);
		if (link == null){
			throw new SAXException(new LinkNotFoundException());
		}
		ArrivalOrDeparture aod = new ArrivalOrDeparture(departure, time, personVal, link, legModeVal);
		events.add(aod);
	}
	
	/**
	 * Parses the actstart and actend events.
	 * @param attributes Attributes of the event element. Usually provided by the SAX engine.
	 * @param time Simulation time at which the processed event occurred
	 * @param personVal This person is associated with the processed event
	 * @param start When true, the event type is actstart. When false, the event type is actend.
	 * @throws SAXException When a mandatory attribute is missing or has an invalid value.
	 */
	private void processActStartEnd(Attributes attributes, double time, String personVal, boolean start) 
			throws SAXException{
		String linkVal = attributes.getValue(linkName);
		Utils.ensureNonNullAndNonEmptyAttr("\"actstart or actend\" event", linkName, linkVal);
		String facilityVal = attributes.getValue(facilityName);
		String actTypeVal = attributes.getValue(actTypeName);
		MyLink link = links.get(linkVal);
		if (link == null){
			throw new SAXException(new LinkNotFoundException());
		}
		ActStartOrEnd ase = new ActStartOrEnd(start, time, personVal, link, facilityVal, actTypeVal);
		events.add(ase);
	}
	
}
