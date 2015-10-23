package cz.cuni.mff.d3s.jdeeco.visualizer.network;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * The {@link Network} represents a Matsim network.
 * 
 * <p>
 * The network describes nodes and links of a given region. It is spit up into
 * three different networks: streets, pedestrians and publictransport.
 * </p>
 * <p>
 * NOTE: At the moment (October 2004), Matsim only handles street networks
 * (motorized individual transport). The others are included just for
 * completeness. (They will be used in future.)
 * </p>
 * 
 * @see <a href="http://www.matsim.org/files/dtd/network_v1.dtd">Matsim network
 *      DTD</a>
 * 
 * @author Dominik Skoda <skoda@d3s.mff.cuni.cz>
 */
public class Network {

	/**
	 * Holds all the links of the network.
	 * 
	 * @author Dominik Skoda <skoda@d3s.mff.cuni.cz>
	 */
	private class Links {

		/**
		 * The set of {@link Link}s.
		 */
		public Set<Link> links;

		/**
		 * It defines the time period over which the capacity of the given links
		 * has been measured.
		 * <p>
		 * NOTE: It is "01:00:00" per default, i.e. 3600.0 sec
		 * </p>
		 * <p>
		 * format := "hh:mm:ss"
		 * </p>
		 * <p>
		 * REQUIRED
		 * </p>
		 */
		public Duration capPeriod;

		/**
		 * It defines the effective length of the cells in meters. The default
		 * value is 7.5
		 * <p>
		 * format := double
		 * </p>
		 * <p>
		 * REQUIRED
		 * </p>
		 */
		public double effectiveCellSize;

		/**
		 * It defines the effective width of a lane in meters. The default value
		 * is 3.75
		 * <p>
		 * format := double
		 * </p>
		 * <p>
		 * REQUIRED
		 * </p>
		 */
		public double effectiveLaneWidth;
		
		/**
		 * Create a new set of {@link Link}s.
		 */
		public Links(){
			links = new HashSet<>();
			capPeriod = Duration.ofHours(1);
			effectiveCellSize = 7.5;
			effectiveLaneWidth = 3.75;
		}
		
		/**
		 * Returns the Matsim XML representation of the links in the network.
		 */
		@Override
		public String toString() {
			// Get seconds in the capPeriod
			long capPeriodS = capPeriod.getSeconds();
			// Extract hours
			long capPeriodH = capPeriodS / 3600;
			// Extract remaining minutes
			long capPeriodM = (capPeriodS % 3600) / 60;
			// Extract remaining seconds
			capPeriodS = capPeriodS % 60;
			
			StringBuilder builder = new StringBuilder();
			
			builder.append("\t<links ")
				.append("capperiod=\"").append(String.format("%02d:%02d:%02d",
						capPeriodH, capPeriodM, capPeriodS)).append("\" ")
				.append("effectivecellsize=\"").append(effectiveCellSize).append("\" ")
				.append("effectivelanewidth=\"").append(effectiveLaneWidth).append("\" ")
				.append(">").append('\n');
			
			for(Link link : links){
				builder.append("\t\t").append(link.toString()).append('\n');
			}
			
			builder.append("\t</links>");
			
			return builder.toString();
		}
	}

	/**
	 * The set of nodes in the network.
	 */
	private Map<Integer,Node> nodes;

	/**
	 * The set of links in the network.
	 */
	private Links linksWrap;

	/**
	 * It is always a good idea to give a network a descriptive name for better
	 * identification. Normally the name should describe the region which is
	 * described by the network (i.e. Switzerland, Berlin, ...).
	 * <p>
	 * format := string
	 * </p>
	 * <p>
	 * IMPLIED
	 * </p>
	 */
	private String name;

	/**
	 * description of the type of the network (i.e. motorized individual
	 * transport, public transport, etc.)
	 * <p>
	 * format := string
	 * </p>
	 * <p>
	 * IMPLIED
	 * </p>
	 */
	private String type;

	/**
	 * Defines the language settings (see xml spec).
	 * 
	 */
	private Locale language;
	
	/**
	 * Create a new graph instance. New graph has no nodes and no links.
	 */
	public Network(){
		nodes = new HashMap<>();
		linksWrap = new Links();
		name = null;
		type = null;
		language = Locale.ENGLISH;
	}
	
	/**
	 * Add the node to the graph.
	 * @param node The node to be added.
	 * @return True if the node has been added.
	 * 		False if the node has already been present.
	 * @throws IllegalArgumentException Thrown if the node argument is null.
	 */
	public boolean addNode(Node node){
		if(node == null){
			throw new IllegalArgumentException(String.format(
					"The argument \"%s\" cannot be null.", "node"));
		}
		if(!nodes.containsValue(node)){
			nodes.put(node.getId(),node);
			return true;
		}
		return false;
	}
	
	/**
	 * Remove the node from the graph.
	 * @param node The node to be removed.
	 * @return True if the node has been removed.
	 * 		False if the node has not been found in the graph.
	 * @throws IllegalArgumentException Thrown if the node argument is null.
	 */
	public boolean removeNode(Node node){
		if(node == null){
			throw new IllegalArgumentException(String.format(
					"The argument \"%s\" cannot be null.", "node"));
		}
		if(nodes.containsValue(node)){
			nodes.remove(node);
			linksWrap.links.removeAll(getIncidentLinks(node));
			return true;
		}
		return false;
	}

	/**
	 * Return all the nodes in the graph.
	 * @return All the nodes in the graph.
	 */
	public Map<Integer,Node> getNodes(){
		return Collections.unmodifiableMap(nodes);
	}
	
	/**
	 * @return The node with given id.
	 */
	public Node getNode(int id){
		return nodes.get(id);
	}
	
	/**
	 * Return all the nodes in the graph to which a link from the given node
	 * leads.
	 * @param node The node which successor nodes are required.
	 * @return The nodes that are connected by a link leading from the given node.
	 * @throws IllegalArgumentException Thrown if the node argument is null.
	 */
	public Set<Node> getSuccessors(Node node){
		if(node == null){
			throw new IllegalArgumentException(String.format(
					"The argument \"%s\" cannot be null.", "node"));
		}
		
		Set<Node> successors = new HashSet<>();
		for(Link link : getLinksFrom(node)){
			successors.add(link.getTo());
		}
		
		return successors;
	}
	
	/**
	 * Add the given link to the graph.
	 * @param link The link to be added.
	 * @return True if the link has been added.
	 * 		False if the link was already in the graph.
	 * @throws IllegalArgumentException Thrown if the link argument is null.
	 */
	public boolean addLink(Link link){
		if(link == null){
			throw new IllegalArgumentException(String.format(
					"The argument \"%s\" cannot be null.", "link"));
		}
		
		if(!linksWrap.links.contains(link)){
			linksWrap.links.add(link);
			return true;
		}
		return false;
	}
	
	/**
	 * Remove the given link from the graph.
	 * @param link The link to be removed.
	 * @return True if the link has been removed.
	 * 		False if the link has not been found in the graph.
	 * @throws IllegalArgumentException Thrown if the link argument is null.
	 */
	public boolean removeLink(Link link){
		if(link == null){
			throw new IllegalArgumentException(String.format(
					"The argument \"%s\" cannot be null.", "link"));
		}

		if(linksWrap.links.contains(link)){
			linksWrap.links.remove(link);
			return true;
		}
		return false;
	}
	
	/**
	 * Return all the links in the graph.
	 * @return All the links in the graph.
	 */
	public Set<Link> getLinks(){
		return Collections.unmodifiableSet(linksWrap.links);
	}
	
	/**
	 * Get all the links that are incident with the given node.
	 * <p>link.getFrom() == node || link.getTo() == node</p>
	 * @param node The node incident with the required links.
	 * @return The links that are incidet with the given node.
	 * @throws IllegalArgumentException Thrown if the node argument is null.
	 */
	public Set<Link> getIncidentLinks(Node node){
		if(node == null){
			throw new IllegalArgumentException(String.format(
					"The argument \"%s\" cannot be null.", "node"));
		}
		
		Set<Link> incidentLinks = new HashSet<>();
		for(Link link : linksWrap.links){
			if(link.getFrom() == node || link.getTo() == node){
				incidentLinks.add(link);
			}
		}
		return incidentLinks;
	}
	
	/**
	 * Get all the links going from the given node.
	 * @param node The node from which the required links go.
	 * @return All the links going from the given node.
	 * @throws IllegalArgumentException Thrown if the node argument is null.
	 */
	public Set<Link> getLinksFrom(Node node){
		if(node == null){
			throw new IllegalArgumentException(String.format(
					"The argument \"%s\" cannot be null.", "node"));
		}
		
		Set<Link> fromLinks = new HashSet<>();
		for(Link link : linksWrap.links){
			if(link.getFrom() == node){
				fromLinks.add(link);
			}
		}
		return fromLinks;
	}
	
	/**
	 * Get all the links going to the given node.
	 * @param node The node to which the required links go.
	 * @return All the links going to the given node.
	 * @throws IllegalArgumentException Thrown if the node argument is null.
	 */
	public Set<Link> getLinksTo(Node node){
		if(node == null){
			throw new IllegalArgumentException(String.format(
					"The argument \"%s\" cannot be null.", "node"));
		}
		
		Set<Link> toLinks = new HashSet<>();
		for(Link link : linksWrap.links){
			if(link.getTo() == node){
				toLinks.add(link);
			}
		}
		return toLinks;
	}

	/**
	 * It is always a good idea to give a network a descriptive name for better
	 * identification. Normally the name should describe the region which is
	 * described by the network (i.e. Switzerland, Berlin, ...).
	 * <p>
	 * Setting this property is not required. Can be set to null.
	 * </p>
	 * @param name The name of the network.
	 * @throws IllegalArgumentException Thrown if the name parameter is empty string.
	 */
	public void setName(String name){
		if(name != null && name.isEmpty()){
			throw new IllegalArgumentException(String.format(
					"The argument \"%s\" is empty.", "name"));
		}
		this.name = name;
	}
	
	/**
	 * Get the name of the network.
	 * @return The name of the network.
	 */
	public String getName(){
		return name;
	}

	/**
	 * description of the type of the network (i.e. motorized individual
	 * transport, public transport, etc.)
	 * <p>
	 * Setting this property is not required. Can be set to null.
	 * </p>
	 * @param type The type of the network.
	 * @throws IllegalArgumentException Thrown if the type parameter is empty string.
	 */
	public void setType(String type){
		if(type != null && type.isEmpty()){
			throw new IllegalArgumentException(String.format(
					"The argument \"%s\" is empty.", "type"));
		}
		this.type = type;
	}

	/**
	 * Get the type of the network.
	 * @return The type of the network.
	 */
	public String getType(){
		return type;
	}

	/**
	 * Defines the language settings (see xml spec).
	 * <p>
	 * Setting this property is not required. Can be set to null.
	 * </p>
	 */
	public void setLanguage(Locale language){
		this.language = language;
	}
	
	/**
	 * Get the language if the network.
	 * @return The language of the network.
	 */
	public Locale getLanguage(){
		return language;
	}

	/**
	 * It defines the time period over which the capacity of the given links
	 * has been measured.
	 * <p>
	 * NOTE: It is "01:00:00" per default, i.e. 3600.0 sec
	 * </p>
	 * <p>
	 * This property is required.
	 * </p>
	 * @param capPeriod the period over which the capacity has been measured.
	 * @throws IllegalArgumentException Thrown if the capPeriod argument is null
	 * 		or non-positive or zero number.
	 */	
	public void setCapPeriod(Duration capPeriod){
		if(capPeriod == null) throw new IllegalArgumentException(String.format(
				"The argument \"%s\" cannot be null.", "capPeriod"));
		if(capPeriod.isNegative() || capPeriod.isZero())
			throw new IllegalArgumentException(String.format(
					"The argument \"%s\" has to be positive.", "capPeriod"));
		linksWrap.capPeriod = capPeriod;
	}
	
	/**
	 * Get the period over which the capacity has been measured.
	 * @return The period over which the capacity has been measured.
	 */
	public Duration getCapPeriod(){
		return linksWrap.capPeriod;
	}

	/**
	 * It defines the effective length of the cells in meters. The default
	 * value is 7.5
	 * <p>
	 * This property is required.
	 * </p>
	 * @param effectiveCellSize The effective length of the cells in meters.
	 * @throws IllegalArgumentException Thrown if the effectiveCellSize argument
	 * 		is not a non-negative non-zero number.
	 */
	public void setEffectiveCellSize(double effectiveCellSize){
		if(!Double.isFinite(effectiveCellSize)) throw new IllegalArgumentException(
				String.format("The \"%s\" argument is not finite.", "effectiveCellSize"));
		if(effectiveCellSize <= 0) throw new IllegalArgumentException(String.format(
				"The \"%s\" argument has to be greater than 0.", "effectiveCellSize"));
		linksWrap.effectiveCellSize = effectiveCellSize;
	}
	
	/**
	 * Get the length of the cells in meters.
	 * @return The length of the cells in meters.
	 */
	public double getEffectiveCellSize(){
		return linksWrap.effectiveCellSize;
	}

	/**
	 * It defines the effective width of a lane in meters. The default value
	 * is 3.75
	 * <p>
	 * This property is required.
	 * </p>
	 * @param effectiveLaneWidth The effective width of a lane in meters.
	 * @throws IllegalArgumentException Thrown if the effectiveLaneWidth argument
	 * 		is not a positive non-zero number.
	 */
	public void setEffectiveLaneWidth(double effectiveLaneWidth){
		if(!Double.isFinite(effectiveLaneWidth)) throw new IllegalArgumentException(
				String.format("The \"%s\" argument is not finite.", "effectiveLaneWidth"));
		if(effectiveLaneWidth <= 0) throw new IllegalArgumentException(String.format(
				"The \"%s\" argument has to be greater than 0.", "effectiveLaneWidth"));
		linksWrap.effectiveLaneWidth = effectiveLaneWidth;
	}
	
	/**
	 * Get the width of a lane in meters.
	 * @return The width of a lane in meters.
	 */
	public double getEffectiveLaneWidth(){
		return linksWrap.effectiveLaneWidth;
	}
	
	/**
	 * Returns the string Matsim XML representation of the network.
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		builder.append("<!DOCTYPE network SYSTEM \"http://www.matsim.org/files/dtd/network_v1.dtd\">\n");
		
		builder.append("<network ");
		if(name != null){
			builder.append("name=\"").append(name).append("\" ");
		}
		if(type != null){
			builder.append("type=\"").append(type).append("\" ");
		}
		if(language != null){
			builder.append("xml:lang=\"").append(language.toLanguageTag()).append("\" ");
		}
		builder.append(">\n");
		
		builder.append("\t<nodes>\n");
		for(Node node : nodes.values()){
			builder.append("\t\t").append(node.toString()).append('\n');
		}
		builder.append("\t</nodes>\n");
		
		builder.append(linksWrap.toString()).append('\n');
		
		builder.append("</network>");
		
		return builder.toString();
	}
}
