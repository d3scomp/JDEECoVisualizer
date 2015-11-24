/* The authors of this work have released all rights to it and placed it
in the public domain under the Creative Commons CC0 1.0 waiver
(http://creativecommons.org/publicdomain/zero/1.0/).

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

Retrieved from: http://en.literateprograms.org/Dijkstra's_algorithm_(Java)?oldid=15444
 */
//

// 

//

package cz.cuni.mff.d3s.jdeeco.visualizer.network;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Computes shortest path between two {@link Node}s in a {@link Network}.
 */
public class Dijkstra {
	
	/**
	 * {@link DistanceFrom} holds a distance and the last link through which
	 * it was measured.
	 * 
	 * @author Dominik Skoda <skoda@d3s.mff.cuni.cz>
	 */
	private static class DistanceFrom{
		/**
		 * The calculated distance.
		 */
		public double distance;
		
		/**
		 * The last link through which the distance has been calculated.
		 */
		public Link predecessorLink;
		
		/**
		 * A new instance of {@link DistanceFrom} with associated distance
		 * and predecessor {@link Link}.
		 * @param predecessorLink The last link in the path the distance
		 * 			has been calculated through.
		 * @param distance The calculated distance.
		 */
		public DistanceFrom(Link predecessorLink, double distance){
			this.predecessorLink = predecessorLink;
			this.distance = distance;
		}
	}
	
	/**
	 * This helper class is not supposed to create instances.
	 */
	private Dijkstra(){}
	
	/**
	 * Compute the shortest path in the given {@link Network} from the source
	 * {@link Node} to the target {@link Node}.
	 * @param graph The graph to search the path in.
	 * @param source The node to start from.
	 * @param target The node to finish at.
	 * @return The list of links which constitutes the path from source to target.
	 * 		If the path doesn't exist the list is empty.
	 */
	public static List<Link> getShortestPath(Network graph, Node source, Node target){
		final Map<Node, DistanceFrom> distances = getInitialDistances(graph, source);
		final PriorityQueue<Node> unvisited = new PriorityQueue<>(
				new Comparator<Node>(){
			@Override
			public int compare(Node from, Node to) {
				return Double.compare(distances.get(from).distance, distances.get(to).distance);
			}
		});
		unvisited.addAll(graph.getNodes());
		Node current = unvisited.poll();
		
		while(true){
			updateSuccessorsDistances(graph, current, unvisited, distances);
			if(current == target){
				return assemblePath(target, distances);
			}
			current = unvisited.poll();
			if(!Double.isFinite(distances.get(current).distance)){
				return Collections.emptyList();
			}
		}
	}
	
	/**
	 * Initialize the distances in the graph. The source node has initial distance 0,
	 * all the other nodes has initial distance +&infin;.
	 * @param graph The {@link Network} to initialize distances in.
	 * @param source The starting {@link Node}.
	 * @return A map of distances associated with the {@link Node}s from the {@link Network}.
	 */
	private static Map<Node, DistanceFrom> getInitialDistances(Network graph, Node source){
		Map<Node, DistanceFrom> initialDistances = new HashMap<>();
		
		for(Node node : graph.getNodes()){
			if(node == source){
				initialDistances.put(node, new DistanceFrom(null, 0));
				DistanceFrom check = initialDistances.get(node);
			} else {
				initialDistances.put(node, new DistanceFrom(null, Double.POSITIVE_INFINITY));
			}
		}
		
		return initialDistances;
	}
	
	/**
	 * Update distances to successor nodes of the current node.
	 * If the distance in a successor node is greater than the new distance
	 * through the current node, it will be updated. Left otherwise.
	 * @param graph The graph to work in.
	 * @param current The current node used for distance computation.
	 * @param unvisited The set of unvisited nodes.
	 * @param distances The distances of individual nodes.
	 */
	private static void updateSuccessorsDistances(Network graph, Node current, PriorityQueue<Node> unvisited, Map<Node, DistanceFrom> distances){
		Set<Link> linksFrom = graph.getLinksFrom(current);
		for(Link link: linksFrom){
			Node successor = link.getTo();
			DistanceFrom successorDistance = distances.get(successor);
			if(unvisited.contains(successor)){
				double distViaCurrent = distances.get(current).distance + link.getLength();
				if(distViaCurrent < successorDistance.distance){
					successorDistance.distance = distViaCurrent;
					successorDistance.predecessorLink = link;
					unvisited.remove(successor);
					unvisited.add(successor);
				}
			}
		}
	}
	
	/**
	 * Extract the found path from its end to its beginning.
	 * @param target The last node on the path.
	 * @param distances The computed distances with links to previous nodes.
	 * @return The list of links which constitutes the path from source to target.
	 * 		If the path doesn't exist the list is empty.
	 */
	private static List<Link> assemblePath(Node target, Map<Node, DistanceFrom> distances){
		LinkedList<Link> path = new LinkedList<>();

		DistanceFrom head = distances.get(target);
		while(head.predecessorLink != null){
			path.addFirst(head.predecessorLink);
			head = distances.get(head.predecessorLink.getFrom());
		}
	
		return path;
	}
	
}
