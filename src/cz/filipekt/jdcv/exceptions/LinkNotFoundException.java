package cz.filipekt.jdcv.exceptions;


/**
 * Thrown when an XML element refers to a "link" element that is not present in the network XML definition file
 */
@SuppressWarnings("serial")
public class LinkNotFoundException extends Exception {}