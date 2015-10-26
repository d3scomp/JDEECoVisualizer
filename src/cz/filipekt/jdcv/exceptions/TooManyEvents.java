package cz.filipekt.jdcv.exceptions;

/**
 * Thrown when the part of the MATSim event log file which should be
 * visualized contains too many elements 
 */
@SuppressWarnings("serial")
public class TooManyEvents extends Exception {

	/**
	 * @param message Detailed message about the cause of the exception
	 */
	public TooManyEvents(String message) {
		super(message);
	}
	
}
