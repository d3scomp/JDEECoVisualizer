package cz.filipekt.jdcv.measuring;

import java.nio.charset.Charset;

/**
 * Characterization of a generated log file. Contains information about
 * the total number of present elements, the size of each element and
 * the text encoding used.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class FileCharacter {
	
	/**
	 * Total number of event elements in the file
	 */
	private final long elementCount;
	
	/**
	 * Total number of characters which each event element consists of
	 */
	private final int elementSize;
	
	/**
	 * Text encoding used by the log file
	 */
	private final Charset encoding;

	/**
	 * @return Total number of event elements in the file
	 */
	public long getElementCount() {
		return elementCount;
	}

	/**
	 * @return Total number of characters which each event element consists of
	 */
	public int getElementSize() {
		return elementSize;
	}

	/**
	 * @return Text encoding used by the log file
	 */
	public Charset getEncoding() {
		return encoding;
	}

	/**
	 * @param elementCount Total number of event elements in the file
	 * @param elementSize Total number of characters which each event element consists of
	 * @param encoding Text encoding used by the log file
	 */
	public FileCharacter(long elementCount, int elementSize, Charset encoding) {
		this.elementCount = elementCount;
		this.elementSize = elementSize;
		this.encoding = encoding;
	}
}