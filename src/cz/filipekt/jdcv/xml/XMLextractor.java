package cz.filipekt.jdcv.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Takes care of loading and parsing the input XML files. 
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class XMLextractor {

	/**
	 * The input XML document
	 */
	private final Path xmlFile;
	
	/**
	 * Stream opened on the input XML document
	 */
	private final InputStream xmlStream;
	
	/**
	 * SAX handler used during the parsing
	 */
	private final ContentHandler handler;
	
	/**
	 * Character encoding of the input XML document
	 */
	private final Charset charset;

	/**
	 * @param xmlFile The input XML document
	 * @param xmlStream Stream opened on the input XML document
	 * @param encoding Character encoding of the input XML document
	 * @param handler SAX handler used during the parsing
	 */
	private XMLextractor(Path xmlFile, InputStream xmlStream,
			String encoding, ContentHandler handler) {
		this.xmlFile = xmlFile;
		this.xmlStream = xmlStream;
		this.handler = handler;
		this.charset = Charset.forName(encoding);
	}

	/**
	 * Runs the SAX parsing of the input file, using {@link XMLextractor#handler} as SAX handler.
	 * @throws SAXException When there is any problem when parsing the XML document.
	 * @throws If the {@link XMLextractor#xmlFile} does not exist or is inaccessible.
	 */
	private void doExtraction() throws ParserConfigurationException, SAXException, IOException{
		SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setNamespaceAware(false);
		SAXParser saxParser = spf.newSAXParser();
		XMLReader xmlReader = saxParser.getXMLReader();
		xmlReader.setContentHandler(handler);
		Reader reader;
		if (xmlStream == null){
			reader = Files.newBufferedReader(xmlFile, charset);
		} else {
			reader = new InputStreamReader(xmlStream, charset);
		}
		InputSource source = new InputSource(reader);
		xmlReader.parse(source);
	}
	
	/**
	 * Given an XML document and a {@link ContentHandler} it parses 
	 * the input file using SAX, with the given handler. 
	 * @param xmlFile Input XML file
	 * @param encoding Character encoding of the input XML document
	 * @param handler SAX handler used during the parsing
	 * @throws IllegalArgumentException If either of the parameters is null.
	 * @throws IOException If it is impossible to read from the input XML file
	 * @throws SAXException If there is any problem when parsing the XML document. 
	 * It is often used as a wrapper for other kinds of exceptions.
	 */
	public static void run(Path xmlFile, String encoding, ContentHandler handler) throws ParserConfigurationException, 
			SAXException, IOException, IllegalArgumentException {
		if ((xmlFile == null) || (handler == null)){
			throw new IllegalArgumentException("Arguments of XMLextractor.run(..) must be non-null.");
		}
		XMLextractor instance = new XMLextractor(xmlFile, null, encoding, handler);
		instance.doExtraction();
	}
	
	/**
	 * Given a stream opened on an XML document and a {@link ContentHandler} it parses 
	 * the input file using SAX, with the given handler. 
	 * @param xmlStream Stream opened on an XML document
	 * @param encoding Character encoding of the input XML document
	 * @param handler SAX handler used for parsing
	 * @throws IllegalArgumentException If either of the parameters is null.
	 * @throws IOException If it is impossible to read from the input stream
	 * @throws SAXException If there is any problem when parsing the XML document. 
	 * It is often used as a wrapper for other kinds of exceptions.
	 */
	public static void run(InputStream xmlStream, String encoding, ContentHandler handler) 
			throws ParserConfigurationException, SAXException, IOException, IllegalArgumentException {
		if ((xmlStream == null) || (handler == null)){
			throw new IllegalArgumentException("Arguments of XMLextractor.run(..) must be non-null.");
		}
		XMLextractor instance = new XMLextractor(null, xmlStream, encoding, handler);
		instance.doExtraction();
	}
}
