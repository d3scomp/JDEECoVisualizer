package cz.filipekt.jdcv.measuring;

import cz.filipekt.jdcv.Visualizer;

/**
 * Encapsulates the parameters which are provided to the {@link Visualizer} class
 * when it is run for performance measuring purposes.
 * 
 * @author Tomas Filipek <tom.filipek@seznam.cz>
 */
public class Parameters {
	
	/**
	 * Path to the network definition file
	 */
	private final String mapPath;
	
	/**
	 * Text encoding of the network definition file
	 */
	private final String mapEncoding;
	
	/**
	 * Path to the MATSim event log file
	 */
	private final String eventPath;
	
	/**
	 * Text encoding of the MATSim event log file
	 */
	private final String eventEncoding;
	
	/**
	 * Path to the ensemble log file
	 */
	private final String ensemblePath;
	
	/**
	 * Text encoding of the ensemble log file
	 */
	private final String ensembleEncoding;
	
	/**
	 * If true, just JDEECo injected agents will be shown
	 */
	private final boolean justAgents;
	
	/**
	 * Intended duration  of the visualization
	 */
	private final String duration;
	
	/**
	 * Left bound of the time interval we wish to visualize
	 */
	private final String startAt;
	
	/**
	 * Right bound of the time interval we wish to visualize
	 */
	private final String endAt;
	
	/**
	 * @return Path to the network definition file
	 */
	public String getMapPath() {
		return mapPath;
	}
	
	/**
	 * @return Text encoding of the network definition file
	 */
	public String getMapEncoding() {
		return mapEncoding;
	}
	
	/**
	 * @return Path to the MATSim event log file
	 */
	public String getEventPath() {
		return eventPath;
	}
	
	/**
	 * @return Text encoding of the MATSim event log file
	 */
	public String getEventEncoding() {
		return eventEncoding;
	}
	
	/**
	 * @return Left bound of the time interval we wish to visualize
	 */
	public String getStartAt() {
		return startAt;
	}
	
	/**
	 * @return Right bound of the time interval we wish to visualize
	 */
	public String getEndAt() {
		return endAt;
	}
	
	/**
	 * @return Path to the ensemble log file
	 */
	public String getEnsemblePath() {
		return ensemblePath;
	}
	
	/**
	 * @return Text encoding of the ensemble log file
	 */
	public String getEnsembleEncoding() {
		return ensembleEncoding;
	}
	
	/**
	 * @return If true, just JDEECo injected agents will be shown
	 */
	public boolean isJustAgents() {
		return justAgents;
	}
	
	/**
	 * @return Intended duration  of the visualization
	 */
	public String getDuration() {
		return duration;
	}
	
	/**
	 * @param mapPath Path to the network definition file
	 * @param mapEncoding Text encoding of the network definition file
	 * @param eventPath Path to the MATSim event log file
	 * @param eventEncoding Text encoding of the MATSim event log file
	 * @param ensemblePath Path to the ensemble log file
	 * @param ensembleEncoding Text encoding of the ensemble log file
	 * @param justAgents If true, just JDEECo injected agents will be shown
	 * @param duration Intended duration  of the visualization
	 * @param startAt Left bound of the time interval we wish to visualize
	 * @param endAt Right bound of the time interval we wish to visualize
	 */
	public Parameters(String mapPath, String mapEncoding, String eventPath, String eventEncoding, String ensemblePath,
			String ensembleEncoding, boolean justAgents, String duration, String startAt, String endAt) {
		this.mapPath = mapPath;
		this.mapEncoding = mapEncoding;
		this.eventPath = eventPath;
		this.eventEncoding = eventEncoding;
		this.ensemblePath = ensemblePath;
		this.ensembleEncoding = ensembleEncoding;
		this.justAgents = justAgents;
		this.duration = duration;
		this.startAt = startAt;
		this.endAt = endAt;
	}
	
}
