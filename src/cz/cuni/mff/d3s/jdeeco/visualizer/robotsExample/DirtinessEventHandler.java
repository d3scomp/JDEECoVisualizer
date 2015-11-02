package cz.cuni.mff.d3s.jdeeco.visualizer.robotsExample;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import cz.cuni.mff.d3s.deeco.logging.Log;
import cz.filipekt.jdcv.events.EnsembleEvent;
import cz.filipekt.jdcv.exceptions.InvalidAttributeValueException;
import cz.filipekt.jdcv.exceptions.TooManyEvents;
import cz.filipekt.jdcv.xml.Utils;

/**
 * SAX handler used to parse the XML file containing the dirtiness events.
 * 
 * @author Ilias Gerostathopoulos <iliasg@d3s.mff.cuni.cz>
 */
public class DirtinessEventHandler extends DefaultHandler {

	/**
	 * Container of events elements
	 */
	private final String eventsContainer = "events";

	/**
	 * Name of the event element
	 */
	private final String eventName = "event";

	/**
	 * Name of the type attribute of the event element
	 */
	private final String typeName = "eventType";

	/**
	 * Expected value of the type attribute of the event element. Events of
	 * different types should be ignored by this handler.
	 */
	private final String expectedTypeValue = DirtinessRecord.class.getCanonicalName();

	/**
	 * Name of the time attribute of the event element
	 */
	private final String timeName = "time";

	/**
	 * Name of the coordinator sub-element of the event element
	 */
	private final String intensityName = "intensity";

	/**
	 * Name of the member sub-element of the event element
	 */
	private final String nodeName = "node";

	/**
	 * Flag to denote parsing a node element that is of the expected type
	 */
	private boolean visitingDirtinessNode = false;

	/**
	 * Flag to denote parsing an intensity sub-element
	 */
	private boolean visitingIntensityNode = false;

	/**
	 * Flag to denote parsing a node sub-element
	 */
	private boolean visitingNodeNode = false;

	/**
	 * Storage for the parsed event elements
	 */
	private final List<DirtinessEvent> events = new ArrayList<>();

	/**
	 * Stack to keep reference to the parent event element
	 */
	private Stack<DirtinessEvent> dirtinessEventsStack = new Stack<>();

	/**
	 * @return The parsed event elements
	 * @see {@link DirtinessEventHandler#events}
	 */
	public List<DirtinessEvent> getEvents() {
		return events;
	}

	/**
	 * If true, only the events starting after time
	 * {@link DirtinessEventHandler#startAtLimit} are taken into account
	 */
	private final boolean startAtConstraint;

	/**
	 * If {@link DirtinessEventHandler#startAtConstraint} holds, only events starting
	 * from this time on are taken into account
	 */
	private final double startAtLimit;

	/**
	 * If true, only the events ending before time
	 * {@link DirtinessEventHandler#endAtLimit} are taken into account
	 */
	private final boolean endAtConstraint;

	/**
	 * If {@link DirtinessEventHandler#endAtConstraint} holds, only the events ending
	 * before this time are taken into account
	 */
	private final double endAtLimit;

	/**
	 * @param startAt
	 *            Only events starting from this time on are taken into account.
	 *            If null, no such constraint is applied.
	 * @param endAt
	 *            Only the events ending before this time are taken into
	 *            account. If null, no such constraint is applied.
	 */
	public DirtinessEventHandler(Double startAt, Double endAt) {
		if (startAt == null) {
			startAtConstraint = false;
			startAtLimit = -1;
		} else {
			startAtConstraint = true;
			startAtLimit = startAt;
		}
		if (endAt == null) {
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
	 * When a dirtiness event element is encountered, it creates a corresponding
	 * object and pushes it to the stack. Also sets the flags according to the
	 * element that is encountered.
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		if (qName.equals(eventName)) {

			count += 1;
			if (count > countLimit) {
				String message = "The selection contains too many <event> elements. "
						+ "Please specify a selection of the log file which contains at most " + countLimit
						+ " elements.";
				throw new SAXException(new TooManyEvents(message));
			}

			String typeVal = attributes.getValue(typeName);
			if (typeVal.equals(expectedTypeValue)) {
				visitingDirtinessNode = true;
			} else {
				visitingDirtinessNode = false;
				return;
			}

			String timeVal = attributes.getValue(timeName);
			Utils.ensureNonNullAndNonEmptyAttr(eventName, timeName, timeVal);
			double time;
			try {
				time = Double.parseDouble(timeVal);
			} catch (NumberFormatException ex) {
				throw new SAXException(new InvalidAttributeValueException(
						"Time attribute of the ensemble event must be in the \"double precision\" format."));
			}
			if (startAtConstraint && (startAtLimit > time)) {
				return;
			}
			if (endAtConstraint && (endAtLimit < time)) {
				return;
			}

			DirtinessEvent eev = new DirtinessEvent(time);
			dirtinessEventsStack.push(eev);
		} else {

			if (visitingDirtinessNode) {

				switch (qName) {

				case intensityName:
					visitingIntensityNode = true;
					break;

				case nodeName:
					visitingNodeNode = true;
					break;

				default:
					Log.i("Encountered unexpected sub-element of dirtiness event with qName: " + qName);
				}
			}
		}
	}

	/**
	 * When a closing event element (of the expected type) is encountered, the
	 * corresponding event element is popped from the stack and stored in the
	 * parsed form in the {@link DirtinessEventHandler#events} storage.
	 */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {

		if (visitingDirtinessNode && qName.equals(eventName)) {
			events.add(dirtinessEventsStack.pop());
			visitingDirtinessNode = false;
		}
	}

	/**
	 * Checks the flags to find what is being parsed, parses the (String)
	 * content of the XML element, and sets the corresponding attribute of the
	 * already generated event object. Also resets the flags.
	 */
	@Override
	public void characters(char ch[], int start, int length) throws SAXException {

		if (visitingDirtinessNode) {

			DirtinessEvent parent = dirtinessEventsStack.peek();
			String val = new String(ch, start, length);

			if (visitingIntensityNode) {
				Utils.ensureNonNullAndNonEmptyAttr(eventName, intensityName, val);
				double intensity;
				try {
					intensity = Double.parseDouble(val);
				} catch (NumberFormatException ex) {
					throw new SAXException(new InvalidAttributeValueException(
							"Time attribute of the ensemble event must be in the \"double precision\" format."));
				}
				parent.setIntensity(intensity);
				visitingIntensityNode = false;
				return;
			}

			if (visitingNodeNode) {
				Utils.ensureNonNullAndNonEmptyAttr(eventName, nodeName, val);
				parent.setNode(val);
				visitingNodeNode = false;
				return;
			}

		}
	}

}
