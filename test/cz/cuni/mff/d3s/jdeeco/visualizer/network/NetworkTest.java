package cz.cuni.mff.d3s.jdeeco.visualizer.network;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import junitx.framework.FileAssert;

public class NetworkTest {

	protected File tempFile;
	protected Network network;
	
	@Before
	public void setUp() throws Exception {
		tempFile = Files.createTempFile(null, ".xml").toFile();
	}

	@After
	public void tearDown() throws IOException {
		Files.deleteIfExists(tempFile.toPath());
	}
	
	/*
	 * Acceptance tests. 
	 * The produced XML-serialized models (saved in a temporary file) 
	 * are compared to their pre-generated expected outputs.
	 */
	
	@Test 
	public void testSmallSquareNetworkCreation() throws IOException {
		network = new Network();
		int width = 5;
		int height = 5;
		
		Node.resetIdReference();
		createAndAttachNodes(width, height);
		createAndAttachLinks(width, height);

//		writeToFile("experiment.xml");
		writeToTempFile();
		File expected = getExpectedFile("smallSquareNetwork.xml");
		FileAssert.assertEquals(expected, tempFile);
	}
	
//	@Test @Ignore
//	public void testMediumSquareNetworkCreation() throws IOException, InterruptedException {
//		network = new Network();
//		int width = 10;
//		int height = 10;
//		
//		Node.resetIdReference();
//		createAndAttachNodes(width, height);
//		createAndAttachLinks(width, height);
//
////		writeToFile("mediumSquareNetwork.xml");
//		writeToTempFile();
//		File expected = getExpectedFile("mediumSquareNetwork.xml");
//		Thread.sleep(1000);
//
//		FileAssert.assertEquals(expected, tempFile);
//	}
//
//	@Test @Ignore 
//	public void testRectangleNetworkCreation() throws IOException {
//		network = new Network();
//		int width = 5;
//		int height = 3;
//		
//		Node.resetIdReference();
//		createAndAttachNodes(width, height);
//		createAndAttachLinks(width, height);
//
////		writeToFile("mediumRectangleNetwork.xml");
//		writeToTempFile();
//		File expected = getExpectedFile("mediumRectangleNetwork.xml");
//		FileAssert.assertEquals(expected, tempFile);
//	}
	
	protected void createAndAttachNodes(int width, int height) {
		for (int i=0; i<width; i++) {
			for (int j=0; j<height; j++) {
				Node n = new Node(i,j);
				network.addNode(n);
			}
		}
	}
	
	protected void createAndAttachLinks(int width, int height) {
		for (int id=0; id<width*height-1; id++) {
			// add vertical links
			if (id % height != height - 1) {
				Node upper = getNode(id);
				Node lower = getNode(id+1);
				network.addLink(new Link(upper,lower));
				// add diagonal links
				if (id < width*height - height) {
					// add upper left to lower right links	
					Node upperLeft = getNode(id);
					Node lowerRight = getNode(id+height+1);
					network.addLink(new Link(upperLeft,lowerRight));
					// add lower left to upper right links
					Node lowerLeft = getNode(id+1);
					Node upperRight = getNode(id+height);
					network.addLink(new Link(lowerLeft,upperRight)); 
				}
			}
			// add horizontal links
			if (id < width*height - height) {
				Node left = getNode(id);
				Node right = getNode(id+height);
				network.addLink(new Link(left,right)); 
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
	
	private void writeToTempFile() {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(tempFile.getPath(), "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writer.println(network.toString());
		writer.close();
	}
	
	private void writeToFile(String name) {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(name, "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writer.println(network.toString());
		writer.close();
	}
	
	private File getExpectedFile(String name) {
		String outF = "target" + File.separator + "test-classes" + File.separator;
		String path = outF + name;
		return new File(path);
	}
}
