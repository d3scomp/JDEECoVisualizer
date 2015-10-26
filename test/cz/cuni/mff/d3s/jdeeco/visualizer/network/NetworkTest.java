package cz.cuni.mff.d3s.jdeeco.visualizer.network;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class NetworkTest {

	protected Network network;
	private static final String PATH_PREFIX = "target" + File.separator + "test-classes" + File.separator;

	@Test
	public void testSmallSquareNetworkCreation() throws IOException, ParserConfigurationException {
		network = new Network();
		int width = 5;
		int height = 5;

		Node.resetIdReference();
		createAndAttachNodes(width, height);
		createAndAttachLinks(width, height);

		String pathToCreatedFile = writeToFile("smallSquareNetwork.xml");
		assertTrue(validateWithDTD(pathToCreatedFile));
	}

	@Test
	public void testMediumSquareNetworkCreation() throws IOException, InterruptedException, ParserConfigurationException {
		network = new Network();
		int width = 10;
		int height = 10;

		Node.resetIdReference();
		createAndAttachNodes(width, height);
		createAndAttachLinks(width, height);

		String pathToCreatedFile = writeToFile("mediumSquareNetwork.xml");
		assertTrue(validateWithDTD(pathToCreatedFile));
	}

	@Test
	public void testRectangleNetworkCreation() throws IOException, ParserConfigurationException {
		network = new Network();
		int width = 10;
		int height = 5;

		Node.resetIdReference();
		createAndAttachNodes(width, height);
		createAndAttachLinks(width, height);

		String pathToCreatedFile = writeToFile("mediumRectangleNetwork.xml");
		assertTrue(validateWithDTD(pathToCreatedFile));
	}

	protected void createAndAttachNodes(int width, int height) {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Node n = new Node(i, j);
				network.addNode(n);
			}
		}
	}

	protected void createAndAttachLinks(int width, int height) {
		for (int id = 0; id < width * height - 1; id++) {
			// add vertical links
			if (id % height != height - 1) {
				Node upper = getNode(id);
				Node lower = getNode(id + 1);
				network.addLink(new Link(upper, lower));
				// add diagonal links
				if (id < width * height - height) {
					// add upper left to lower right links
					Node upperLeft = getNode(id);
					Node lowerRight = getNode(id + height + 1);
					network.addLink(new Link(upperLeft, lowerRight));
					// add lower left to upper right links
					Node lowerLeft = getNode(id + 1);
					Node upperRight = getNode(id + height);
					network.addLink(new Link(lowerLeft, upperRight));
				}
			}
			// add horizontal links
			if (id < width * height - height) {
				Node left = getNode(id);
				Node right = getNode(id + height);
				network.addLink(new Link(left, right));
			}
		}
	}

	private Node getNode(int id) {
		for (Node n : network.getNodes()) {
			if (n.getId() == id) {
				return n;
			}
		}
		return null;
	}

	private String writeToFile(String name) {
		PrintWriter writer = null;
		String path = PATH_PREFIX  + name;
		try {
			writer = new PrintWriter(path, "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		writer.println(network.toString());
		writer.close();
		return path;

	}

	private static boolean validateWithDTD(String xml) throws ParserConfigurationException, IOException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(true);
			factory.setNamespaceAware(true);

			DocumentBuilder builder = factory.newDocumentBuilder();

			builder.setErrorHandler(new ErrorHandler() {
				public void warning(SAXParseException e) throws SAXException {
					System.out.println("WARNING : " + e.getMessage()); // do
				}

				public void error(SAXParseException e) throws SAXException {
					System.out.println("ERROR : " + e.getMessage());
					throw e;
				}

				public void fatalError(SAXParseException e) throws SAXException {
					System.out.println("FATAL : " + e.getMessage());
					throw e;
				}
			});
			builder.parse(new InputSource(xml));
			return true;
		} catch (ParserConfigurationException pce) {
			throw pce;
		} catch (IOException io) {
			throw io;
		} catch (SAXException se) {
			return false;
		}
	}

}
