package cz.cuni.mff.d3s.jdeeco.visualizer.network;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;

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

//		writeToFile("smallSquareNetwork.xml");
		writeToTempFile();
		File expected = getExpectedFile("smallSquareNetwork.xml");
		FileAssert.assertEquals(expected, tempFile);
	}
	
	@Test 
	public void testMediumSquareNetworkCreation() throws IOException, InterruptedException {
		network = new Network();
		int width = 10;
		int height = 10;
		
		Node.resetIdReference();
		createAndAttachNodes(width, height);
		createAndAttachLinks(width, height);

//		writeToFile("mediumSquareNetwork.xml");
		writeToTempFile();
		File expected = getExpectedFile("mediumSquareNetwork.xml");
		Thread.sleep(1000);

		FileAssert.assertEquals(expected, tempFile);
		System.gc();
	}

	@Test 
	public void testRectangleNetworkCreation() throws IOException {
		network = new Network();
		int width = 5;
		int height = 3;
		
		Node.resetIdReference();
		createAndAttachNodes(width, height);
		createAndAttachLinks(width, height);

//		writeToFile("mediumRectangleNetwork.xml");
		writeToTempFile();
		File expected = getExpectedFile("mediumRectangleNetwork.xml");
		FileAssert.assertEquals(expected, tempFile);
		System.gc();
	}
	
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
				Node upper = network.getNode(id);
				Node lower = network.getNode(id+1);
				network.addLink(new Link(upper,lower));
				// add diagonal links
				if (id < width*height - height) {
					// add upper left to lower right links	
					Node upperLeft = network.getNode(id);
					Node lowerRight = network.getNode(id+height+1);
					network.addLink(new Link(upperLeft,lowerRight));
					// add lower left to upper right links
					Node lowerLeft = network.getNode(id+1);
					Node upperRight = network.getNode(id+height);
					network.addLink(new Link(lowerLeft,upperRight)); 
				}
			}
			// add horizontal links
			if (id < width*height - height) {
				Node left = network.getNode(id);
				Node right = network.getNode(id+height);
				network.addLink(new Link(left,right)); 
			}
		}		
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
	
	private void writeToFile(String string) {
		PrintWriter writer = null;
		String outF = "test"
				+ File.separator
				+ this.getClass().getPackage().getName().replace('.', File.separatorChar)
				+ File.separator
				+ "samples" + File.separator;
		String path = outF + string;
		try {
			writer = new PrintWriter(path, "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writer.println(network.toString());
		writer.close();
	}
	
	private File getExpectedFile(String name) {
		String outF = "test"
				+ File.separator
				+ this.getClass().getPackage().getName().replace('.', File.separatorChar)
				+ File.separator
				+ "samples" + File.separator;
		String path = outF + name;
		return new File(path);
	}

}
