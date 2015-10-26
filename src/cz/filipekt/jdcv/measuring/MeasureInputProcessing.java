package cz.filipekt.jdcv.measuring;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import cz.filipekt.jdcv.Visualizer;
import javafx.application.Application;
import javafx.application.Platform;

/**
 * Measures the performance of the visualizing application in the situation
 * when the input is being processed. Provides a self-contained measuring
 * package - the test files are first automatically generated and after the
 * measurements are done, they are deleted.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class MeasureInputProcessing {
	
	/**
	 * When counted down to zero, it means that the JavaFX platform has
	 * been initialized.
	 */
	private final CountDownLatch latch = new CountDownLatch(1);
	
	/**
	 * Used by the {@link Visualizer} class to let this class know, that
	 * it has successfully started
	 */
	public void register(){
		latch.countDown();
	}
	
	/**
	 * Intended for use only by the initializer statement of the singleton instance.
	 * Prepares the input files.
	 */
	private MeasureInputProcessing() {
		fillParams();
	}
	
	/**
	 * The singleton instance of this class.
	 */
	private static MeasureInputProcessing instance = new MeasureInputProcessing();
	
	/**
	 * @return The singleton instance of this class.
	 */
	public static MeasureInputProcessing getInstance(){
		return MeasureInputProcessing.instance;
	}
	
	/**
	 * Contains a complete description of each of the input file sets
	 */
	private final List<Parameters> params = new ArrayList<>();
	
	/**
	 * Determines how many times will a single input tested, 
	 * with the warm-up already finished
	 */
	private final int individualLimit = 10;
	
	/**
	 * After the total time spent during a warm-up grows over this value,
	 * the warm-up is terminated
	 */
	private final long warmupLimit = 10_000L;
	
	/**
	 * Warms-up the JVM on the input specified by the "par" parameter
	 */
	private void warmup(final Visualizer visualizer, final Parameters par) throws InterruptedException{
		long totalTime = 0L;
		while(true){
			visualizer.renewLatch();
			Platform.runLater(new Runnable() {
				
				@Override
				public void run() {
					visualizer.setParams(par);
					visualizer.processParameters();
				}
			});
			totalTime += visualizer.getMeasuredTime();
			Platform.runLater(new Runnable() {
				
				@Override
				public void run() {
					visualizer.clickCloseScene();
				}
			});
			if (totalTime > warmupLimit){
				break;
			}
		}
		System.out.println("Warmed up");
	}
	
	/**
	 * Carries out the performance testing of the input file processing
	 * functionality. The input is created already during the class construction,
	 * here the actual measuring is done. The input which was generated only for
	 * the testing purposes will be deleted upon application exit.
	 */
	private void measure() throws InterruptedException{
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Application.launch(Visualizer.class);
			}
		}).start();
		latch.await();
		final Visualizer visualizer = Visualizer.getInstance();
		System.gc();
		int count = 1;
		for (final Parameters par : params){
			if (par == null){
				System.out.println("Input #" + count++ + " error");
			} else {
				warmup(visualizer, par);
				List<Long> results = new ArrayList<>();
				for (int i = 0; i<individualLimit; i++){
					System.gc();
					visualizer.renewLatch();
					Platform.runLater(new Runnable() {
						
						@Override
						public void run() {
							visualizer.setParams(par);
							visualizer.processParameters();
						}
					});
					results.add(visualizer.getMeasuredTime());
					Platform.runLater(new Runnable() {
						
						@Override
						public void run() {
							visualizer.clickCloseScene();
						}
					});
					
				}
				Collections.sort(results);
				System.out.println("Input #" + count++ + " elapsed: " + 
						results.get(results.size()/2) + "ms (" + individualLimit + " iterations)");
			}
		}
		Platform.exit();
	}
	
	/**
	 * Carries out the performance testing of the input file processing
	 * functionality. The input is created already during the class construction,
	 * here the actual measuring is done. The input which was generated only for
	 * the testing purposes will be deleted upon application exit.
	 * @see {@link MeasureInputProcessing#measure()}
	 */
	public static void main(String[] args) throws InterruptedException{
		MeasureInputProcessing.instance.measure();
	}
	
	/**
	 * Prepares the input which will be used for performance testing.
	 * That includes the generation of those inputs which are not 
	 * packaged in the source files.
	 */
	private void fillParams() {
		Parameters d3sExample = new Parameters(
				"example_data/network.xml", "US-ASCII",
				"example_data/events.xml", "US-ASCII",
				"example_data/ensembles.xml", "US-ASCII",
				true, "56", "", "");
		params.add(d3sExample);
		try {
			Parameters par2a = prepareInput2(3_000);
			params.add(par2a);
		} catch (IOException ex) {
			ex.printStackTrace();
			params.add(null);
		}
		try {
			Parameters par2b = prepareInput2(30_000);
			params.add(par2b);
		} catch (IOException ex) {
			ex.printStackTrace();
			params.add(null);
		}
		try {
			Parameters par3a = prepareInput3(5_000);
			params.add(par3a);
		} catch (IOException ex) {
			ex.printStackTrace();
			params.add(null);
		}
		try {
			Parameters par3b = prepareInput3(50_000);
			params.add(par3b);
		} catch (IOException ex) {
			ex.printStackTrace();
			params.add(null);
		}
	}
	
	/**
	 * XML header used in those testing input files which are generated by this class 
	 */
	private final String xmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
	
	/**
	 * Generates the input #3 for the performance testing purposes. The input contains
	 * customizable number of ensembles (specified by the method parameter)
	 */
	private Parameters prepareInput3(int ensemblesCount) throws IOException{
		File networkFile = File.createTempFile("JDEECo", "set3.xml");
		networkFile.deleteOnExit();
		String network = getNetworkSet3();
		try (Writer writer = Files.newBufferedWriter(networkFile.toPath(), StandardCharsets.UTF_8)){
			writer.append(network);
		}
		File eventsFile = File.createTempFile("JDEECo", "set3.xml");
		eventsFile.deleteOnExit();
		String events = getEventsSet3();
		try (Writer writer = Files.newBufferedWriter(eventsFile.toPath(), StandardCharsets.UTF_8)){
			writer.append(events);
		}
		File ensemblesFile = File.createTempFile("JDEECo", "set3.xml");
		ensemblesFile.deleteOnExit();
		String ensembles = getEnsemblesSet3(ensemblesCount);
		try (Writer writer = Files.newBufferedWriter(ensemblesFile.toPath(), StandardCharsets.UTF_8)){
			writer.append(ensembles);
		}
		Parameters par = new Parameters(networkFile.toString(), "UTF-8", 
				eventsFile.toString(), "UTF-8", 
				ensemblesFile.toString(), "UTF-8", 
				false, "600", "", "");
		return par;
	}
	
	/**
	 * Generates and returns the network definition for input #3
	 */
	private String getNetworkSet3(){
		return getNetworkSet2();
	}
	
	/**
	 * Number of DEECo elements mentioned in the event log file of input #3
	 */
	private final int set3elementCount = 100;
	
	/**
	 * Generates and returns the MATSim event log file of input #3
	 */
	private String getEventsSet3(){
		StringBuilder sb = new StringBuilder();
		sb.append(xmlHeader);
		sb.append("<events>\n");
		int time = 0;
		for (int i = 0; i<set3elementCount; i++){
			String personID = Integer.toString(i);
			sb.append("<event time=\"");
			sb.append(time);
			sb.append("\" type=\"PersonEntersVehicle\" person=\"");
			sb.append(personID);
			sb.append("\" vehicle=\"V");
			sb.append(personID);
			sb.append("\" />\n");
			
			sb.append("<event time=\"");
			sb.append(time);
			sb.append("\" type=\"departure\" person=\"");
			sb.append(personID);
			sb.append("\" link=\"4\" />\n");
			
			sb.append("<event time=\"");
			sb.append(time);
			sb.append("\" type=\"left link\" person=\"");
			sb.append(personID);
			sb.append("\" link=\"4\" />\n");
			
			sb.append("<event time=\"");
			sb.append(time);
			sb.append("\" type=\"entered link\" person=\"");
			sb.append(personID);
			sb.append("\" link=\"1\" />\n");
			
			time++;
		}
		time += set3elementCount;
		for (int i = 0; i<set3elementCount; i++){
			String personID = Integer.toString(i);
			sb.append("<event time=\"");
			sb.append(time);
			sb.append("\" type=\"left link\" person=\"");
			sb.append(personID);
			sb.append("\" link=\"1\" />\n");
			
			sb.append("<event time=\"");
			sb.append(time);
			sb.append("\" type=\"entered link\" person=\"");
			sb.append(personID);
			sb.append("\" link=\"2\" />\n");
			
			time++;
		}
		time += set3elementCount;
		for (int i = 0; i<set3elementCount; i++){
			String personID = Integer.toString(i);
			sb.append("<event time=\"");
			sb.append(time);
			sb.append("\" type=\"left link\" person=\"");
			sb.append(personID);
			sb.append("\" link=\"2\" />\n");
			
			sb.append("<event time=\"");
			sb.append(time);
			sb.append("\" type=\"entered link\" person=\"");
			sb.append(personID);
			sb.append("\" link=\"3\" />\n");
			
			time++;
		}
		time += set3elementCount;
		for (int i = 0; i<set3elementCount; i++){
			String personID = Integer.toString(i);
			sb.append("<event time=\"");
			sb.append(time);
			sb.append("\" type=\"left link\" person=\"");
			sb.append(personID);
			sb.append("\" link=\"3\" />\n");
			
			sb.append("<event time=\"");
			sb.append(time);
			sb.append("\" type=\"entered link\" person=\"");
			sb.append(personID);
			sb.append("\" link=\"4\" />\n");
			
			time++;
		}
		time += set3elementCount;
		for (int i = 0; i<set3elementCount; i++){
			String personID = Integer.toString(i);
			sb.append("<event time=\"");
			sb.append(time);
			sb.append("\" type=\"left link\" person=\"");
			sb.append(personID);
			sb.append("\" link=\"4\" />\n");
			
			sb.append("<event time=\"");
			sb.append(time);
			sb.append("\" type=\"arrival\" person=\"");
			sb.append(personID);
			sb.append("\" link=\"4\" />\n");
			
			sb.append("<event time=\"");
			sb.append(time);
			sb.append("\" type=\"PersonLeavesVehicle\" person=\"");
			sb.append(personID);
			sb.append("\" vehicle=\"V");
			sb.append(personID);
			sb.append("\" />\n");
			
			time++;
		}
		sb.append("</events>\n");
		return sb.toString();
	}
	
	/**
	 * Generates and returns the ensemble event log file of input #3
	 */
	private String getEnsemblesSet3(int ensemblesCount){
		int totalTime = 8 * set3elementCount;
		double step = (totalTime * 0.5) / ensemblesCount;
		Map<Integer,Set<Integer>> members = new HashMap<>();
		for (int i = 0; i<ensemblesCount; i++){
			int coord = i % set3elementCount;
			int membersOffset = (i / set3elementCount) + 1;
			Set<Integer> locals = new HashSet<>();
			int member1 = (coord + membersOffset) % set3elementCount;
			locals.add(member1);
			int member2 = (member1 + 1) % set3elementCount;
			locals.add(member2);
			int member3 = (member2 + 1) % set3elementCount;
			locals.add(member3);
			members.put(i, locals);
		}
		double time = set3elementCount;
		StringBuilder sb = new StringBuilder();
		sb.append(xmlHeader);
		sb.append("<events>\n");
		for (int i = 0; i<ensemblesCount; i++){
			int coord = i % set3elementCount;
			Set<Integer> mem = members.get(i);
			for (int member : mem){
				sb.append("<event time=\"");
				sb.append(time);
				sb.append("\" type=\"ensemble\" ensemble=\"");
				sb.append(i);
				sb.append("\" coordinator=\"");
				sb.append(coord);
				sb.append("\" member=\"");
				sb.append(member);
				sb.append("\" membership=\"true\" />\n");
			}
			time +=  (step * 0.5);
			for (int member : mem){
				sb.append("<event time=\"");
				sb.append(time);
				sb.append("\" type=\"ensemble\" ensemble=\"");
				sb.append(i);
				sb.append("\" coordinator=\"");
				sb.append(coord);
				sb.append("\" member=\"");
				sb.append(member);
				sb.append("\" membership=\"false\" />\n");
			}
			time += (step * 0.5);
		}
		sb.append("</events>\n");
		return sb.toString();
	}
	
	/**
	 * Generates and return the input #2 for performance testing purposes.
	 * The input contains a customizable number DEECo elements, specified
	 * by the method parameter
	 */
	private Parameters prepareInput2(int carsCount) throws IOException {
		File networkFile = File.createTempFile("JDEECo", "set2.xml");
		networkFile.deleteOnExit();
		String network = getNetworkSet2();
		try (Writer writer = Files.newBufferedWriter(networkFile.toPath(), StandardCharsets.UTF_8)){
			writer.append(network);
		}
		File eventsFile = File.createTempFile("JDEECo", "set2.xml");
		eventsFile.deleteOnExit();
		String events = getEventsSet2(carsCount);
		try (Writer writer = Files.newBufferedWriter(eventsFile.toPath(), StandardCharsets.UTF_8)){
			writer.append(events);
		}
		Parameters par = new Parameters(networkFile.toString(), "UTF-8", 
				eventsFile.toString(), "UTF-8", 
				"", "", false, "600", "", "");
		return par;
	}
	
	/**
	 * Generates and returns the MATSim event log file of input #2
	 */
	private String getEventsSet2(int carsCount){
		StringBuilder sb = new StringBuilder();
		sb.append(xmlHeader);
		sb.append("<events>\n");
		for (int i = 1; i < carsCount; i++){
			String car = circlingCar(i * 10, 10, Integer.toString(i));
			sb.append(car);
		}
		sb.append("</events>\n");
		return sb.toString();
	}
	
	/**
	 * Helper method used during the generation of MATSim event log file of
	 * input #2. Creates a series of event elements, which describe a single
	 * car which circles around the map once.
	 */
	private String circlingCar(double time, int duration, String personID){
		StringBuilder sb = new StringBuilder();
		int step = duration/4;
		sb.append("<event time=\"");
		sb.append(time);
		sb.append("\" type=\"PersonEntersVehicle\" person=\"");
		sb.append(personID);
		sb.append("\" vehicle=\"V");
		sb.append(personID);
		sb.append("\" />\n");
		
		sb.append("<event time=\"");
		sb.append(time);
		sb.append("\" type=\"departure\" person=\"");
		sb.append(personID);
		sb.append("\" link=\"4\" />\n");
		
		sb.append("<event time=\"");
		sb.append(time);
		sb.append("\" type=\"left link\" person=\"");
		sb.append(personID);
		sb.append("\" link=\"4\" />\n");
		
		sb.append("<event time=\"");
		sb.append(time);
		sb.append("\" type=\"entered link\" person=\"");
		sb.append(personID);
		sb.append("\" link=\"1\" />\n");
		
		time += step;
		
		sb.append("<event time=\"");
		sb.append(time);
		sb.append("\" type=\"left link\" person=\"");
		sb.append(personID);
		sb.append("\" link=\"1\" />\n");
		
		sb.append("<event time=\"");
		sb.append(time);
		sb.append("\" type=\"entered link\" person=\"");
		sb.append(personID);
		sb.append("\" link=\"2\" />\n");
		
		time += step;
		
		sb.append("<event time=\"");
		sb.append(time);
		sb.append("\" type=\"left link\" person=\"");
		sb.append(personID);
		sb.append("\" link=\"2\" />\n");
		
		sb.append("<event time=\"");
		sb.append(time);
		sb.append("\" type=\"entered link\" person=\"");
		sb.append(personID);
		sb.append("\" link=\"3\" />\n");
		
		time += step;
		
		sb.append("<event time=\"");
		sb.append(time);
		sb.append("\" type=\"left link\" person=\"");
		sb.append(personID);
		sb.append("\" link=\"3\" />\n");
		
		sb.append("<event time=\"");
		sb.append(time);
		sb.append("\" type=\"entered link\" person=\"");
		sb.append(personID);
		sb.append("\" link=\"4\" />\n");
		
		time += step;
		
		sb.append("<event time=\"");
		sb.append(time);
		sb.append("\" type=\"left link\" person=\"");
		sb.append(personID);
		sb.append("\" link=\"4\" />\n");
		
		sb.append("<event time=\"");
		sb.append(time);
		sb.append("\" type=\"arrival\" person=\"");
		sb.append(personID);
		sb.append("\" link=\"4\" />\n");
		
		sb.append("<event time=\"");
		sb.append(time);
		sb.append("\" type=\"PersonLeavesVehicle\" person=\"");
		sb.append(personID);
		sb.append("\" vehicle=\"V");
		sb.append(personID);
		sb.append("\" />\n");
		
		return sb.toString();
	}
	
	/**
	 * Generates and returns the network definition of input #2
	 */
	private String getNetworkSet2(){
		StringBuilder sb = new StringBuilder();
		sb.append(xmlHeader);
		sb.append("<network name=\"set2\">\n");
		sb.append("<nodes>\n");
		int nodeid = 1;
		sb.append("<node id=\"");
		sb.append(nodeid++);
		sb.append("\" x=\"0\" y=\"0\"/>\n");
		sb.append("<node id=\"");
		sb.append(nodeid++);
		sb.append("\" x=\"10\" y=\"0\"/>\n");
		sb.append("<node id=\"");
		sb.append(nodeid++);
		sb.append("\" x=\"10\" y=\"10\"/>\n");
		sb.append("<node id=\"");
		sb.append(nodeid++);
		sb.append("\" x=\"0\" y=\"10\"/>\n");
		sb.append("</nodes>\n");
		sb.append("<links>\n");
		int linkid = 1;
		sb.append("<link id=\"");
		sb.append(linkid++);
		sb.append("\" from=\"1\" to=\"2\"/>\n");
		sb.append("<link id=\"");
		sb.append(linkid++);
		sb.append("\" from=\"2\" to=\"3\"/>\n");
		sb.append("<link id=\"");
		sb.append(linkid++);
		sb.append("\" from=\"3\" to=\"4\"/>\n");
		sb.append("<link id=\"");
		sb.append(linkid++);
		sb.append("\" from=\"4\" to=\"1\"/>\n");
		sb.append("</links>\n");
		sb.append("</network>\n");
		return sb.toString();
	}
}
