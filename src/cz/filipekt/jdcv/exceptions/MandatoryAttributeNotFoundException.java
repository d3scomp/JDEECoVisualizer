package cz.filipekt.jdcv.exceptions;

/**
 * Thrown when a mandatory attribute is not present in a processed element. 
 */
@SuppressWarnings("serial")
public class MandatoryAttributeNotFoundException extends Exception {

	/**
	 * @param message Detailed message about the cause of the exception
	 */
	public MandatoryAttributeNotFoundException(String message) {
		super(message);
	}

}
