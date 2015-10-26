package cz.filipekt.jdcv.network;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Each zone/activity combination can hold a variable amount of opening 
 * times describing on what times the facility is open and therefore 
 * accessible. The opening times are specified on a daily basis for maximum 
 * week periods.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class MyOpenTime {

	/**
	 * A schedule can be set for a single day, for all weekdays, the weekend 
	 * or for the whole week. Schedules for single days always override 
	 * schedules for time ranges.
	 */
	private final MyDayValue day;
	
	/**
	 * Defines when a facility opens
	 */
	private final Date startTime;
	
	/**
	 * Defines when a facility closes
	 */
	private final Date endTime;
	
	/**
	 * Format of the {@link MyOpenTime#startTime} and {@link MyOpenTime#endTime} elements
	 * in the source XML file. Should depend on the locale defined in xml:lang attribute.
	 * @see http://www.matsim.org/files/dtd/facilities_v1.dtd.
	 */
	public static final DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.MEDIUM, new Locale("cs", "CZ"));

	/**
	 * Builds a basic representation of the "opentime" XML element, 
	 * using the mandatory attribute values.
	 * @param day which days this schedule affects
	 * @param startTime when a facility opens
	 * @param endTime when a facility closes
	 */
	public MyOpenTime(MyDayValue day, Date startTime, Date endTime) {
		this.day = day;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	/**
	 * @return Which days this schedule affects
	 * @see {@link MyOpenTime#day}
	 */
	public MyDayValue getDay() {
		return day;
	}

	/**
	 * @return When the facility opens
	 * @see {@link MyOpenTime#startTime}
	 */
	public Date getStartTime() {
		return startTime;
	}

	/**
	 * @return When the facility closes
	 * @see {@link MyOpenTime#endTime}
	 */
	public Date getEndTime() {
		return endTime;
	}
	
	
	
}
