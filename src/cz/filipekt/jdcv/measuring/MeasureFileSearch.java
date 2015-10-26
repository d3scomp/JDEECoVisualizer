package cz.filipekt.jdcv.measuring;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cz.filipekt.jdcv.util.BigFilesSearch;
import cz.filipekt.jdcv.util.BigFilesSearch.ElementTooLargeException;
import cz.filipekt.jdcv.util.BigFilesSearch.SelectionTooBigException;

/**
 * Measures the performance of the big-files search procedure included in
 * {@link BigFilesSearch}. Provides a self-contained measuring package -
 * the test files are dynamically created and after the measurements, they
 * are deleted.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz> 
 */
public class MeasureFileSearch {

	/**
	 * Carries out the whole performance measuring process of the big-files
	 * searching mechanism. The action is delegated to the 
	 * {@link MeasureFileSearch#test()} procedure.
	 */
	public static void main(String[] args) 
			throws IOException, SelectionTooBigException, ElementTooLargeException {
		INSTANCE.test();
	}
	
	/**
	 * The singleton instance of this class
	 */
	private static final MeasureFileSearch INSTANCE = new MeasureFileSearch(); 
	
	/**
	 * After initialization it contains the file characters described in
	 * the thesis. A file character definition consists of the number
	 * of contained event elements, their length and the text encoding.
	 * @see {@link FileCharacter}
	 */
	private final List<FileCharacter> fileCharacters = new ArrayList<>();
	
	/**
	 * Initializes the file characters
	 */
	private MeasureFileSearch() {
		createFillers();
		createCharacters();
	}
	
	/**
	 * Carries out the whole performance measuring process of the big-files
	 * searching mechanism. It creates the input files, measures how long does
	 * the searching take, and deletes them.
	 */
	private void test() throws IOException, SelectionTooBigException, ElementTooLargeException{
		for (int i = 0; i< fileCharacters.size(); i++){
			FileCharacter character = fileCharacters.get(i);
			File file = createFile(character);
			System.out.println("Created: " + file);
			measure(character, file);
			if (file.delete()){
				System.out.println("Deleted: " + file);
			}
			System.out.println();
		}
		System.out.println("Finished");
	}

	/**
	 * Initializes the {@link MeasureFileSearch#fileCharacters} collection with a few
	 * reasonable values (mentioned in the thesis)
	 */
	private void createCharacters(){
		fileCharacters.clear();
		fileCharacters.add(new FileCharacter(100_000L, 30, StandardCharsets.UTF_16));
		fileCharacters.add(new FileCharacter(100_000L, 200, StandardCharsets.UTF_8));
		fileCharacters.add(new FileCharacter(1_000_000L, 30, StandardCharsets.UTF_16));
		fileCharacters.add(new FileCharacter(1_000_000L, 200, StandardCharsets.UTF_8));
		fileCharacters.add(new FileCharacter(10_000_000L, 30, StandardCharsets.UTF_16));
		fileCharacters.add(new FileCharacter(10_000_000L, 200, StandardCharsets.UTF_8));
	}
	
	/**
	 * Creates a temporary XML file which complies with the provided file characterization 
	 * @param fc Specifies some basic properties of the file to be created
	 */
	private File createFile(FileCharacter fc) throws IOException{
		File file = File.createTempFile("jdeeco", "_" + Long.toString(fc.getElementCount()) + ".xml");
		file.deleteOnExit();
		try (Writer writer = Files.newBufferedWriter(file.toPath(), fc.getEncoding())){
			writer.append("<?xml version=\"1.0\" encoding=\"" + fc.getEncoding() + "\"?>\n");
			writer.append("<events>\n");
			String pre = "<event time=\"";
			int prefixLength = pre.length();
			String post = "/>\n";
			int suffixLength = post.length();
			for (long i = 0; i<fc.getElementCount(); i++){
				writer.append(pre);
				writer.append(Long.toString(i));
				writer.append("\"");
				String filler = getFiller(prefixLength, suffixLength, i, fc.getElementSize());
				writer.append(filler);
				writer.append(post);
			}
			writer.append("</events>\n");
		}
		return file;
	}
	
	/**
	 * Maximal supported length of an event element (in characters)
	 */
	private final int maxElementLength = 250;
	
	/**
	 * Stores the fillers returned by {@link MeasureFileSearch#getFiller(int, int, long, int)}
	 * so that the fillers are not computed repeatedly for the same parameters
	 */
	private final String[] fillers = new String[maxElementLength];
	
	/**
	 * Initializes selected positions of {@link MeasureFileSearch#fillers}
	 */
	private void createFillers(){
		for (int i = 0; i<3; i++){
			StringBuilder sb = new StringBuilder();
			for (int j = 0; j<i; j++){
				sb.append(" ");
			}
			fillers[i] = sb.toString();
		}
	}
	
	/**
	 * Prepares a filler for an event element so that the element has a specified
	 * total length.
	 * @param prefixLength Length of the element prefix (which is usually '<event time="')
	 * @param suffixLength Length of the element suffix (which is usually '/>\n')
	 * @param time The time value of the element
	 * @param targetLength The desired total length of the element
	 * @return A filler which after inserting to th element, will make the element
	 * exactly "targetLength" long.
	 */
	private String getFiller(int prefixLength, int suffixLength, long time, int targetLength){
		int count = prefixLength;
		count += getLength(time);
		count += 1;
		count += suffixLength;
		int deficit = targetLength - count;
		if (deficit >= maxElementLength){
			return null;
		}
		if (fillers[deficit] == null){
			StringBuilder sb = new StringBuilder();
			if (deficit < 4){
				for (int i = 0; i< deficit; i++){
					sb.append(" ");
				}
			} else {
				sb.append("a=\"");
				for (int i = 0; i<(deficit-4); i++){
					sb.append("X");
				}
				sb.append("\"");
			}
			fillers[deficit] = sb.toString();
		}
		return fillers[deficit];
	}
	
	/**
	 * Given a non-negative long, the method returns the number of characters
	 * in its decimal string representation
	 * @param number A non-negative number
	 */
	private int getLength(long number){
		if (number < 0){
			return -1;
		}
		int length = 1;
		while ((number/10) >= 1){
			number /= 10;
			length += 1;
		}
		return length;
	}
	
	/**
	 * Measures the running time of {@link BigFilesSearch#getPrecedingLocation(double)}
	 * on the given file.
	 * @param character The characteristics of the file given in the other parameter
	 * @param file The {@link BigFilesSearch} class will access this file
	 */
	private void measure(FileCharacter character, File file) 
			throws IOException, SelectionTooBigException, ElementTooLargeException{
		BigFilesSearch bfs = new BigFilesSearch(file.toPath(), character.getEncoding());
		warmup(bfs);
		System.gc();
		double[] points = generatePoints(character.getElementCount());
		List<Long> results = new ArrayList<>(); 
		for (double point : points){
			for (int i = 0; i<individualCount; i++){
				long before = System.nanoTime();
				bfs.getPrecedingLocation(point);
				long after = System.nanoTime();
				results.add(after-before);
			}
		}
		long median = getMedian(results);
		System.out.println("Count:" + character.getElementCount() + 
				", size: " + character.getElementSize() + 
				", encoding: " + character.getEncoding());
		System.out.println(median);
	}
	
	/**
	 * Determines how many times a single measurement is repeated before
	 * moving on to other measurements
	 */
	private final int individualCount = 100;
	
	/**
	 * Given a list of "long" numbers, this method returns 
	 * the median of the values 
	 * @param numbers The input number sequence
	 * @return Median of the input sequence
	 */
	private long getMedian(List<Long> numbers){
		if ((numbers == null) || (numbers.isEmpty())){
			return 0;
		} else {
			Collections.sort(numbers);
			return numbers.get(numbers.size() / 2);
		}
	}
	
	/**
	 * Given the maximal time value of any event element inside the input
	 * file, this methods returns ten time values at which the search
	 * time will be measured.
	 * @param maxTime Maximal time value of event elements in the input file
	 */
	private double[] generatePoints(long maxTime){
		int count = 10;
		double[] res = new double[count];
		for (int i = 0; i<count; i++){
			res[i] = (maxTime/count) * i;
		}
		return res;
	}
	
	/**
	 * Warms up the JVM before the measuring can be started
	 * @param bfs Used to access the big input file
	 */
	private void warmup(BigFilesSearch bfs) throws IOException, ElementTooLargeException{
		double pointTest = 111_111L;
		long testCount = 20_000L;
		for (long L = 1; L<testCount+1; L++){
			bfs.getPrecedingLocation(pointTest);
		}
		System.out.println("Finished warmup");
	}
}
