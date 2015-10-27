package cz.filipekt.jdcv.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import cz.cuni.mff.d3s.deeco.logging.Log;
import cz.cuni.mff.d3s.deeco.runtimelog.EnsembleLogRecord;
import cz.filipekt.jdcv.events.EnsembleEvent;
import cz.filipekt.jdcv.exceptions.InvalidAttributeValueException;
import cz.filipekt.jdcv.exceptions.TooManyEvents;

/**
 * SAX handler used to parse the XML file containing the ensemble events.
 * Collects the "event" elements.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 * @author Ilias Gerostathopoulos <iliasg@d3s.mff.cuni.cz>
 */
public class EnsembleHandler extends DefaultHandler {

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
	 * Expected value of hte type attribute of the event element. Events of
	 * different types should be ignored by this handler.
	 */
	private final String expectedTypeValue = EnsembleLogRecord.class.getCanonicalName();

	/**
	 * Name of the time attribute of the event element
	 */
	private final String timeName = "time";

	/**
	 * Name of the coordinator sub-element of the event element
	 */
	private final String coordinatorName = "coordinatorID";

	/**
	 * Name of the member sub-element of the event element
	 */
	private final String memberName = "memberID";

	/**
	 * Name of the ensemble sub-element of the event element
	 */
	private final String ensembleName = "ensembleName";

	/**
	 * Name of the membership sub-element of the event element
	 */
	private final String membershipName = "membership";

	/**
	 * Flag to denote parsing a node element that is of the expected type
	 */
	private boolean visitingEnsembleNode = false;

	/**
	 * Flag to denote parsing a coordinator sub-element
	 */
	private boolean visitingCoordinatorNameNode = false;

	/**
	 * Flag to denote parsing a member sub-element
	 */
	private boolean visitingMemberNameNode = false;

	/**
	 * Flag to denote parsing an ensemble sub-element
	 */
	private boolean visitingEnsembleNameNode = false;

	/**
	 * Flag to denote parsing a membership sub-element
	 */
	private boolean visitingMembershipNameNode = false;

	/**
	 * Storage for the parsed event elements
	 */
	private final List<EnsembleEvent> events = new ArrayList<>();

	/**
	 * Stack to keep reference to the parent event element
	 */
	private Stack<EnsembleEvent> ensembleEventsStack = new Stack<EnsembleEvent>();

	/**
	 * @return The parsed event elements
	 * @see {@link EnsembleHandler#events}
	 */
	public List<EnsembleEvent> getEvents() {
		return events;
	}

	/**
	 * If true, only the events starting after time
	 * {@link EnsembleHandler#startAtLimit} are taken into account
	 */
	private final boolean startAtConstraint;

	/**
	 * If {@link EnsembleHandler#startAtConstraint} holds, only events starting
	 * from this time on are taken into account
	 */
	private final double startAtLimit;

	/**
	 * If true, only the events ending before time
	 * {@link EnsembleHandler#endAtLimit} are taken into account
	 */
	private final boolean endAtConstraint;

	/**
	 * If {@link EnsembleHandler#endAtConstraint} holds, only the events ending
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
	public EnsembleHandler(Double startAt, Double endAt) {
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
	 * When an ensemble event element is encountered, it creates a corresponding
	 * object and pushes it to the stack. Also sets the flags according to the
	 * element that is encountered. contains all the required attributes with
	 * values in correct format.
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
				visitingEnsembleNode = true;
			} else {
				visitingEnsembleNode = false;
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

			EnsembleEvent eev = new EnsembleEvent(time);
			ensembleEventsStack.push(eev);
		} else {

			if (visitingEnsembleNode) {

				switch (qName) {

				case coordinatorName:
					visitingCoordinatorNameNode = true;
					break;

				case memberName:
					visitingMemberNameNode = true;
					break;

				case membershipName:
					visitingMembershipNameNode = true;
					break;

				case ensembleName:
					visitingEnsembleNameNode = true;
					break;

				case eventsContainer:
					break;

				default:
					Log.i("Encountered unexpected sub-element of ensemble event with qName: " + qName);
				}
			}
		}
	}

	/**
	 * When a closing event element (of the expected type) is encountered, the
	 * corresponding event element is popped from the stack and stored in the
	 * parsed form in the {@link EnsembleHandler#events} storage.
	 */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {

		if (visitingEnsembleNode && qName.equals(eventName)) {
			events.add(ensembleEventsStack.pop());
			visitingEnsembleNode = false;
		}
	}

	/**
	 * Checks the flags to find what is being parsed, parses the (String)
	 * content of the XML element, and sets the corresponding attribute of the
	 * already generated event object. Also resets the flags.
	 */
	@Override
	public void characters(char ch[], int start, int length) throws SAXException {

		if (visitingEnsembleNode) {

			EnsembleEvent parent = ensembleEventsStack.peek();
			String val = new String(ch, start, length);

			if (visitingCoordinatorNameNode) {
				Utils.ensureNonNullAndNonEmptyAttr(eventName, coordinatorName, val);
				parent.setCoordinator(val);
				visitingCoordinatorNameNode = false;
				return;
			}

			if (visitingMemberNameNode) {
				Utils.ensureNonNullAndNonEmptyAttr(eventName, memberName, val);
				parent.setMember(val);
				visitingMemberNameNode = false;
				return;
			}

			if (visitingEnsembleNameNode) {
				Utils.ensureNonNullAndNonEmptyAttr(eventName, ensembleName, val);
				parent.setEnsemble(val);
				visitingEnsembleNameNode = false;
				return;
			}

			if (visitingMembershipNameNode) {
				Utils.ensureNonNullAndNonEmptyAttr(eventName, membershipName, val);
				boolean membership;
				switch (val) {
				case "true":
					membership = true;
					break;
				case "false":
					membership = false;
					break;
				default:
					throw new SAXException(new InvalidAttributeValueException(
							"Membership attribute in the ensemble event element has only two allowed values: true, false."));
				}
				parent.setMembership(membership);
				visitingMembershipNameNode = false;
				return;
			}
		}
	}

}
