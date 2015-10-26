package cz.filipekt.jdcv.exceptions;

/**
 * Thrown when the value of a mandatory attribute does not contain the required 
 * data type (according to the DTD definition).
 */
@SuppressWarnings("serial")
public class InvalidAttributeValueException extends Exception {

	/**
	 * @param message Detailed message about the cause of the exception
	 */
	public InvalidAttributeValueException(String message) {
		super(message);
	}
	
}