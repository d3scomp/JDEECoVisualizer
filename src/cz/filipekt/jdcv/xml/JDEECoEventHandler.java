package cz.filipekt.jdcv.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import cz.cuni.mff.d3s.deeco.logging.Log;
import cz.cuni.mff.d3s.jdeeco.visualizer.network.EnteredLinkRecord;
import cz.cuni.mff.d3s.jdeeco.visualizer.network.LeftLinkRecord;
import cz.filipekt.jdcv.events.EnteredOrLeftLink;
import cz.filipekt.jdcv.events.MatsimEvent;
import cz.filipekt.jdcv.exceptions.InvalidAttributeValueException;
import cz.filipekt.jdcv.exceptions.LinkNotFoundException;
import cz.filipekt.jdcv.exceptions.TooManyEvents;
import cz.filipekt.jdcv.network.MyLink;

/**
 * SAX handler used to parse the XML file containing the jDEECo events.
 * Collects the "event" elements.
 *   
 * @author Ilias Gerostathopoulos <iliasg@d3s.mff.cuni.cz>
 */
public class JDEECoEventHandler extends DefaultHandler {

	/**
	 * Local name of the event element
	 */
	private final String eventName = "event";
	
	/**
	 * Name of the type attribute of the event element
	 */
	private final String typeName = "eventType";

	/**
	 * The first event type expected by the this handler. 
	 */
	private final String enteredLinkTypeValue = EnteredLinkRecord.class.getCanonicalName();
	
	/**
	 * The second event type expected by the this handler. 
	 */
	private final String leftLinkTypeValue = LeftLinkRecord.class.getCanonicalName();
	
	/**
	 * The parsed event elements from the source file
	 */
	private final List<MatsimEvent> events = new ArrayList<>();
	
	/**
	 * Stack to keep reference to the parent event element
	 */
	private Stack<EnteredOrLeftLink> enteredOrLeftLinkStack = new Stack<>();
	
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
	 * Name of the person sub-element of the event element
	 */
	private final String personName = "person";
	
	/**
	 * Name of the link sub-element of the event element
	 */
	private final String linkName = "link";
	
	/**
	 * Collection of parsed link elements, as extracted from a network source file.
	 */
	private final Map<String,MyLink> links;
	
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
	 * @param links
	 *            Collection of parsed link elements, as extracted from a
	 *            network source file.
	 * @param onlyComponents
	 *            If true, only the events of the people corresponding to the
	 *            injected JDEECo components will be taken into account. If
	 *            false, all of the events will be parsed.
	 * @param startAt
	 *            Only events starting from this time on are taken into account.
	 *            If null, no such constraint is applied.
	 * @param endAt
	 *            Only the events ending before this time are taken into
	 *            account. If null, no such constraint is applied.
	 */
	public JDEECoEventHandler(Map<String, MyLink> links, boolean onlyComponents, Double startAt, Double endAt) {
		this.links = links;
		if (startAt == null) {
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
	 * Number of event elements encountered
	 */
	private long count = 0;
	
	/**
	 * Maximal allowed number of event elements in the selection
	 */
	private final long countLimit = 600_000L;

	/**
	 * Flag to denote parsing an enteredLink or a leftLink sub-element
	 */
	private boolean visitingEnteredOrLeftLinkNode = false;

	/**
	 * Flag to denote parsing a link sub-element
	 */
	private boolean visitingLinkNameNode = false;

	/**
	 * Flag to denote parsing a person sub-element
	 */
	private boolean visitingPersonNameNode = false;
	
	/**
	 * Makes sure that when an event element is encountered, correct parsing is carried out.
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
			} catch (NumberFormatException ex) {
				throw new SAXException(new InvalidAttributeValueException(
						"Time attribute of the event element must be in the \"double precision\" format"));
			}
			if (startAtConstraint && (startAtLimit > time)) {
				return;
			}
			if (endAtConstraint && (endAtLimit < time)) {
				return;
			}
			String typeVal = attributes.getValue(typeName);
			if (typeVal.equals(enteredLinkTypeValue)) {

				visitingEnteredOrLeftLinkNode = true;
				EnteredOrLeftLink eev = new EnteredOrLeftLink(true, time, "vehicle");
				enteredOrLeftLinkStack.push(eev);

			} else if (typeVal.equals(leftLinkTypeValue)) {

				visitingEnteredOrLeftLinkNode = true;
				EnteredOrLeftLink eev = new EnteredOrLeftLink(false, time, "vehicle");
				enteredOrLeftLinkStack.push(eev);

			} else {

				visitingEnteredOrLeftLinkNode = false;
				return;
			}
		} else {
			if (visitingEnteredOrLeftLinkNode) {

				switch (qName) {

				case linkName:
					visitingLinkNameNode = true;
					break;

				case personName:
					visitingPersonNameNode = true;
					break;

				default:
					Log.i("Encountered unexpected sub-element of enteredLink or leftLink event with qName: " + qName);
				}
			}
		}
	}

	/**
	 * When a closing event element (of the expected type) is encountered, the
	 * corresponding event element is popped from the stack and stored in the
	 * parsed form in the {@link JDEECoEventHandler#events} storage.
	 */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {

		if (visitingEnteredOrLeftLinkNode && qName.equals(eventName)) {
			events.add(enteredOrLeftLinkStack.pop());
			visitingEnteredOrLeftLinkNode = false;
		}
	}

	/**
	 * Checks the flags to find what is being parsed, parses the (String)
	 * content of the XML element, and sets the corresponding attribute of the
	 * already generated event object. Also resets the flags.
	 */
	@Override
	public void characters(char ch[], int start, int length) throws SAXException {

		if ((visitingEnteredOrLeftLinkNode) && ((visitingLinkNameNode) || (visitingPersonNameNode))) {

			EnteredOrLeftLink parent = enteredOrLeftLinkStack.peek();
			String val = new String(ch, start, length);

			if (visitingLinkNameNode) {
				Utils.ensureNonNullAndNonEmptyAttr("\"entered or left link\" event", linkName, val);
				MyLink link = links.get(val);
				if (link == null) {
					throw new SAXException(new LinkNotFoundException());
				}
				parent.setLink(link);
				visitingLinkNameNode = false;
				return;
			}

			if (visitingPersonNameNode) {
				Utils.ensureNonNullAndNonEmptyAttr(eventName, personName, val);
				parent.setPerson(val);
				visitingPersonNameNode = false;
				return;
			}

		}
	}
	
}
