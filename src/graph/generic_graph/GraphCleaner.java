package graph.generic_graph;

import graph.connected_components.ConnectedComponentsFinder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;


public class GraphCleaner {
	
	Graph graph;
	
	public GraphCleaner(Graph graph){
		this.graph = graph;
	}
	
	public void deleteDoubleEdges() {
		for (Node node : graph.getNodes()) {
			List<Edge> newNeighbors = new LinkedList<Edge>();
			Set<Node> neighbors = new HashSet<Node>();
			for (Edge edge : graph.getEdgesFrom(node)) {
				if (!neighbors.contains(edge.getNode2())) {
					neighbors.add(edge.getNode2());
					newNeighbors.add(edge);
				}
			}
			graph.resetNeighbors(node, newNeighbors);
		}
	}
	
	public void deleteSelfEdges() {
		Set<Node> nodesToDelete = new HashSet<Node>();
		for (Node node : graph.getNodes()) {
			List<Edge> newNeighbors = new LinkedList<Edge>();
			for (Edge edge : graph.getEdgesFrom(node)) {
				if (!edge.getNode2().equals(node)) {
					newNeighbors.add(edge);
				}
			}
			if (newNeighbors.size() > 0) {
				graph.resetNeighbors(node, newNeighbors);
			} else {
				nodesToDelete.add(node);
			}
		}

		for (Node node : nodesToDelete) {
			graph.removeNodeWithNoNeighbor(node);
		}
	}
	
	public void keepLargestComponent() {
		System.out.println("\n############## Keeping largest component ##################");
		
		ConnectedComponentsFinder unionFind = new ConnectedComponentsFinder(graph);
		Set<Node> connectedComponents = unionFind.getConnectedComponents();

		Map<Node, Integer> componentSize = new HashMap<Node, Integer>();

		for (Node node : connectedComponents) {
			componentSize.put(node, 0);
		}

		Map<Node, Node> mapping = unionFind
				.getMappingToConnectedComponents();

		for (Entry<Node, Node> entry : mapping.entrySet()) {
			Node node = entry.getValue();
			componentSize.put(node, componentSize.get(node) + 1);
		}

		int largestComponentSize = 0;
		Node largestComponentRepresentative = null;
		
		for (Entry<Node, Integer> entry : componentSize.entrySet()) {
			if(entry.getValue()>largestComponentSize){
				largestComponentSize = entry.getValue();
				largestComponentRepresentative = entry.getKey();
			}
		}	
		
		Set<Node> toRemove = new HashSet<Node>();
		
		for(Node node : graph.getNodes()){
			if(!mapping.get(node).equals(largestComponentRepresentative)){
				toRemove.add(node);
			}
		}
		
		for(Node node : toRemove){
			graph.removeNode(node);
		}
		
		System.out.println("The graph now has " + graph.getGraphSize() + " nodes.");
		System.out.println("###########################################################");
	}
} 