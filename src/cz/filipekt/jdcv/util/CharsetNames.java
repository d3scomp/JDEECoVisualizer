package cz.filipekt.jdcv.util;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Holds the names of charsets that are available on the computer running 
 * this application. Contains the six charsets required by the Java platform 
 * plus the platform default encoding.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class CharsetNames {
	
	/**
	 * Names of the available charsets
	 */
	private static final Set<String> charsets = initCharsetNames();
	
	/**
	 * Initializer for {@link CharsetNames#charsets}.
	 * @return Names of charsets available at the platform.
	 */
	private static Set<String> initCharsetNames(){
		Set<String> res = new HashSet<>();
		String defaultCharset = Charset.defaultCharset().name();
		List<String> mandatoryCharsets = Arrays.asList(
				"US-ASCII", "ISO-8859-1", "UTF-8", "UTF-16BE", "UTF-16LE", "UTF-16");
		res.add(defaultCharset);
		res.addAll(mandatoryCharsets);
		return Collections.<String>unmodifiableSet(res);
	}
	
	/**
	 * @return Names of charsets available at the computer running this application.
	 * These charsets include the six charsets mandated by the Java specification
	 * and the platform default encoding. 
	 */
	public static Set<String> get(){
		return charsets;
	}
}
