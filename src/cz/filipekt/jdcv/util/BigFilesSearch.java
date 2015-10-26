package cz.filipekt.jdcv.util;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Makes it possible to retrieve a certain part of the event log file, according
 * to a specified time interval. It is assumed that the event elements are sorted
 * by the time attribute.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class BigFilesSearch {
	
	/**
	 * The event log file
	 */
	private final Path path;
	
	/**
	 * Text encoding of the event log file
	 */
	private final Charset charset;

	/**
	 * @param path The event log file
	 * @param charset Text encoding of the event log file
	 */
	public BigFilesSearch(Path path, Charset charset) {
		this.charset = charset;
		this.path = path;
	}

	/**
	 * First few characters of an event element
	 */
	private final String eventElementStart = "<event ";
	
	/**
	 * Name of the time attribute
	 */
	private final String timeAttributeName = "time";
	
	/**
	 * Returns a section of the event log file that contains all the event elements 
	 * with time attribute value inside the interval specified by the parameters.
	 * The section starts with a generated XML declaration, and the event elements are 
	 * encapsulated inside a root XML element (<events>), thus making it a well formed
	 * XML document, providing that the event log file is itself a well formed XML. 
	 * The provided section may contain up to {@link BigFilesSearch#step} bytes in
	 * excess on each end, i.e. the first event element with time equal to the lower
	 * bound given as a parameter can be encounter as far as {@link BigFilesSearch#step}
	 * bytes into the selection.  
	 * @param fromTime Start of the desired time interval. If null, 
	 * the interval is unbounded from below.
	 * @param toTime End of the desired time interval. If null, 
	 * the interval is unbounded from above.
	 * @return Section of the event log file that contains all the event elements 
	 * with time attribute value inside the specified interval
	 * @throws IOException If it is impossible to read from the event log file
	 * @throws SelectionTooBigException If the specified time interval is too large
	 * @throws ElementTooLargeException If some event element is too large
	 */
	public String getSectionWellFormed(Double fromTime, Double toTime) 
			throws IOException, SelectionTooBigException, ElementTooLargeException{
		StringBuilder section = new StringBuilder(getSectionRaw(fromTime, toTime));
		alignToElements(section);
		String preamble = "<?xml version=\"1.0\" encoding=\"" + charset.name() +
				"\"?>\n<events version=\"1.0\">\n";
		section.insert(0, preamble);
		section.append("\n</events>");
		return section.toString();
	}
	
	/**
	 * Crops the given section of an XML document so that it begins with an event
	 * element and ends just before an event element.
	 * @param section A section of the event log file 
	 */
	private void alignToElements(StringBuilder section){
		int eventFirstIndex = section.indexOf(eventElementStart);
		if (eventFirstIndex == -1){
			section.setLength(0);
		} else {
			section.delete(0, eventFirstIndex);
		}
		int eventLastIndex = section.lastIndexOf(eventElementStart);
		if (eventLastIndex == -1){
			section.setLength(0);
		} else {
			section.delete(eventLastIndex, section.length());
		}
	}
	
	/**
	 * Returns a section of the event log file that contains all the event elements 
	 * with time attribute value inside the interval specified by the parameters.
	 * The section is not aligned to the event elements, i.e. it can start at a 
	 * completely arbitrary place.
	 * The provided section may contain up to {@link BigFilesSearch#step} bytes in
	 * excess on each end, i.e. the first event element with time equal to the lower
	 * bound given as a parameter can be encounter as far as {@link BigFilesSearch#step}
	 * bytes into the selection.  
	 * @param fromTime Start of the desired time interval. If null, 
	 * the interval is unbounded from below.
	 * @param toTime End of the desired time interval. If null, 
	 * the interval is unbounded from above.
	 * @return Section of the event log file that contains all the event elements 
	 * with time attribute value inside the specified interval
	 * @throws IOException If it is impossible to read from the event log file
	 * @throws SelectionTooBigException If the specified time interval is too large
	 * @throws ElementTooLargeException If some event element is too large
	 */
	private String getSectionRaw(Double fromTime, Double toTime) 
			throws IOException, SelectionTooBigException, ElementTooLargeException{
		long precFrom;
		if (fromTime == null){
			precFrom = 0;
		} else {
			precFrom = getPrecedingLocation(fromTime);
		}
		long afterTo;
		if (toTime == null){
			afterTo = Files.size(path);
		} else {
			long precTo = getPrecedingLocation(toTime);
			afterTo = precTo + step + sampleSize;
		}
		try (RandomAccessFile raf = new RandomAccessFile(path.toFile(), "r")){
			raf.seek(precFrom);
			long maxLength = afterTo-precFrom;
			if ((maxLength > ((long)Integer.MAX_VALUE)) || 
					(maxLength > maxSelectionSize)){
				throw new SelectionTooBigException();
			}
			byte[] data = new byte[(int)maxLength];
			int count = raf.read(data);
			raf.seek(precFrom);
			int sampleCount = raf.read(sampleBuffer);
			int offset = getCorrectOffset(sampleBuffer, sampleCount);
			String text = new String(data, offset, count-offset, charset);
			return text;
		}		
	}
	
	/**
	 * Thrown when the user demands a too large portion of the event log file.
	 * Too large is defined as at least 2^31 - (1 + 2^21), which is cca 2GB 
	 */
	@SuppressWarnings("serial")
	public static class SelectionTooBigException extends Exception {}
	
	/**
	 * Thrown when the correct offset in an encoded text could not be determined because in no
	 * offset configuration the decoded text contains the {@link BigFilesSearch#eventElementStart}
	 */
	@SuppressWarnings("serial")
	public static class ElementTooLargeException extends Exception {}

	/**
	 * Returns the given bytes parsed as a text, using encoding specified by
	 * {@link BigFilesSearch#charset} and using the correct offset - it has to
	 * be determined at which position the first character starts.
	 * @param data The raw data containing encoded text
	 * @param count Length of the raw data
	 * @return The given data decoded as a text
	 * @throws ElementTooLargeException If the correct offset could not be determined because in no
	 * offset configuration the sample text contains the {@link BigFilesSearch#eventElementStart}
	 */
	private String getWithCorrectOffset(byte[] data, int count) throws ElementTooLargeException{
		count = Math.max(0, count-4);
		for (int offset = 0; offset < 4; offset++){
			String text = new String(data, offset, count, charset);
			if (text.contains(eventElementStart)){
				return text;
			}
		}
		throw new ElementTooLargeException();
	}
	
	/**
	 * Determines the correct alignment of the given bytes with regard to the character
	 * encoding given in {@link BigFilesSearch#charset}
	 * @param data The raw data containing encoded text
	 * @param count Length of the raw data
	 * @return Position in the input byte array where the first character starts
	 * @throws ElementTooLargeException If the correct offset could not be determined because in no
	 * offset configuration the sample text contains the {@link BigFilesSearch#eventElementStart}
	 */
	private int getCorrectOffset(byte[] data, int count) throws ElementTooLargeException{
		for (int offset = 0; offset < 4; offset++){
			String text = new String(data, offset, count, charset);
			if (text.contains(eventElementStart)){
				return offset;
			}
		}
		throw new ElementTooLargeException();
	}
	
	/**
	 * Informally, the returned value can be seen as an approximation (from below) of
	 * the position of the first event element with time attribute value as given. <br>
	 * Formally, given a specific time value, this method searches the event log file and
	 * returns a position (measured by bytes from the file beginning) which is at most
	 * {@link BigFilesSearch#step} bytes before the first element with the time
	 * attribute value equal to the value given in the parameter. At the same time,
	 * the first event element that starts after this position has its time attribute
	 * value strictly smaller than the value given in the parameter. <br>
	 * 
	 * @param targetTime We are trying to locate the element with this time attribute value 
	 * @throws IOException If it is impossible to read from the event log file
	 * @throws ElementTooLargeException If some of the event elements is too large
	 */
	public long getPrecedingLocation(double targetTime) throws IOException, ElementTooLargeException {
		long lowerBound = 0;
		long upperBound = Files.size(path);
		long midPoint;
		try (RandomAccessFile raf = new RandomAccessFile(path.toFile(), "r")){			
			while ((upperBound - lowerBound) >= step){
				midPoint = (lowerBound + upperBound) / 2L;
				boolean midPointBefore = isBefore(raf, midPoint, targetTime);
				if (midPointBefore){
					lowerBound = midPoint;
				} else {
					upperBound = midPoint;
				}
			}
			return lowerBound;
		}
	}
	
	/**
	 * Determines, whether the first event element starting after the given position in the
	 * file has its time attribute value smaller than the given value.
	 * @param file File in which the event element will be examined
	 * @param positionInFile A position in the given file from which we will start searching 
	 * for an event element 
	 * @param timeValue The time attribute value used for comparison
	 * @return Returns true if and only if the first event element starting after the given 
	 * position in the given file has its time attribute value smaller than the given value. 
	 * @throws ElementTooLargeException If some of the event elements is too large
	 * @throws IOException If it is impossible to read from the event log file
	 */
	private boolean isBefore(RandomAccessFile file, long positionInFile, double timeValue) throws ElementTooLargeException, IOException{
		file.seek(positionInFile);
		int count = file.read(sampleBuffer);
		String sample = getWithCorrectOffset(sampleBuffer, count);
		int index = sample.indexOf(timeAttributeName + "=\"");
		String sampleFromTime = sample.substring(index + 6);
		if (index >= 0){
			int quotes = sampleFromTime.indexOf("\"");
			String number = sampleFromTime.substring(0, quotes);
			double time = Double.parseDouble(number);
			return time < timeValue;
		} else {
			return false;
		}
	}
	
	/**
	 * Maximum size of the selection in the XML document, in bytes
	 */
	private final long maxSelectionSize = 100L * 1024L * 1024L;
	
	/**
	 * The location of an event element with a specified time value is
	 * approximated with this precision.
	 */
	private final long step = 1024L * 1024L;
	
	/**
	 * At each position where the event log file is probed, this amount of data is examined
	 */
	private final int sampleSize = 2048;
	
	/**
	 * At each position where the event log file is probed, the examined sample of data is put here
	 */
	private final byte[] sampleBuffer = new byte[sampleSize];

}
