package cz.filipekt.jdcv.network;

/**
 * Represents either a single day of week, or a certain subset of days in week. 
 * @see {@link MyOpenTime}
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public enum MyDayValue {
	
	/**
	 * Working days, i.e., Monday through Friday
	 */
	WKDAY, 
	
	/**
	 * Weekend days, i.e., Saturday and Sunday
	 */
	WKEND, 
	
	/**
	 * All days of week
	 */
	WK,
	
	/**
	 * Monday
	 */
	MON, 
	
	/**
	 * Tuesday
	 */
	TUE,
	
	/**
	 * Wednesday
	 */
	WED,
	
	/**
	 * Thursday
	 */
	THU, 
	
	/**
	 * Friday
	 */
	FRI, 
	
	/**
	 * Saturday
	 */
	SAT, 
	
	/**
	 * Sunday
	 */
	SUN;
}