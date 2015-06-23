package graph.generic_graph;

import graph.connected_components.ConnectedComponentsFinder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

public class GraphAnalyzer {

	protected Graph graph;

	public GraphAnalyzer(Graph graph) {
		this.graph = graph;
	}

	public void printGraphSize() {
		System.out.println("\n######### Printing Graph Size #########");
		System.out.println("Number of nodes : " + graph.getGraphSize());
		System.out.println("Number of edges : " + graph.getNumberOfEdges());
		System.out.println("#######################################");
	}

	public int printNodeWithMaxDegree() {
		Node nodeWithMaxDegree = null;
		int maxNeighbors = 0;
		for (Node node : graph.getNodes()) {
			if (graph.getDegree(node) > maxNeighbors) {
				nodeWithMaxDegree = node;
				maxNeighbors = graph.getDegree(node);
			}
		}
		System.out
				.println("\n######### Printing Node with Max Degree #########");
		System.out.println("The node " + nodeWithMaxDegree.toString() + " has "
				+ maxNeighbors + " neighbors.");
		System.out
				.println("####################################################");
		return maxNeighbors;
	}

	public void printDegreeDistribution() {
		SortedMap<Integer, Integer> map = new TreeMap<Integer, Integer>();
		int numberOfNodes = graph.getGraphSize();
		int count = 0;

		for (Node node : graph.getNodes()) {
			int degree = graph.getDegree(node);

			if (map.get(degree) == null) { // First time we encounter this
											// degree
				map.put(degree, 1);
			} else {
				// Increment
				map.put(degree, map.get(degree) + 1);
			}
		}

		System.out
				.println("\n######### Printing Degree Distribution #########");

		for (Entry<Integer, Integer> entry : map.entrySet()) {
			count += entry.getValue();
			float p = 100*(float)count/(float)numberOfNodes;
			System.out.println("Degree " + entry.getKey() + " --> "
					+ entry.getValue() + " (" + p + "%)");
		}

		System.out.println("################################################");
	}

	public void printEdgeType() {
		int directed = 0;
		int undirected = 0;
		for (Node node1 : graph.getNodes()) {
			for (Edge edge : graph.getEdgesFrom(node1)) {
				if (!node1.equals(edge.getNode2())) {
					if (!graph.existsEdgeBetween(edge.getNode2(), node1)) {
						directed++;
					} else {
						undirected++;
					}
				}
			}
		}
		System.out
				.println("\n############## Printing Edge Types ##################");
		System.out.println("Found " + directed + " directed edges.");
		System.out.println("Found " + undirected / 2 + " undirected edges.");
		System.out
				.println("#####################################################");
	}

	public void printDoubleEdges() {
		int count = 0;
		for (Node node1 : graph.getNodes()) {
			Set<Node> neighbors = new HashSet<Node>();
			for (Edge edge : graph.getEdgesFrom(node1)) {
				if (!neighbors.contains(edge.getNode2())) {
					neighbors.add(edge.getNode2());
				} else {
					count++;
				}
			}
		}

		System.out
				.println("\n############## Printing Double Edges ##################");
		System.out.println("Found " + count + " double edges.");
		System.out
				.println("#######################################################");
	}

	public void printSelfEdges() {
		int i = 0;
		int unique = 0;

		for (Node node1 : graph.getNodes()) {
			boolean alreadyFound = false;
			for (Edge edge : graph.getEdgesFrom(node1)) {
				if (node1.equals(edge.getNode2())) {
					i++;
					if (!alreadyFound) {
						alreadyFound = true;
						unique++;
					}
				}
			}
		}

		System.out
				.println("\n############## Printing Self Edges ##################");
		System.out.println("Found " + i + " self edges.");
		System.out.println("Found " + unique + " nodes that have self edges.");
		System.out
				.println("#####################################################");
	}

	public void printconnectedComponents() {
		System.out
				.println("\n############## Connected Components ##################");
		ConnectedComponentsFinder connnectedComponentsFinder = new ConnectedComponentsFinder(
				graph);
		Set<Node> connectedComponents = connnectedComponentsFinder
				.getConnectedComponents();

		System.out.println("Found " + connectedComponents.size()
				+ " connected components in the graph.");

		Map<Node, Integer> componentSize = new HashMap<Node, Integer>();

		for (Node node : connectedComponents) {
			componentSize.put(node, 0);
		}

		Map<Node, Node> mapping = connnectedComponentsFinder
				.getMappingToConnectedComponents();

		for (Entry<Node, Node> entry : mapping.entrySet()) {
			Node node2 = entry.getValue();
			componentSize.put(node2, componentSize.get(node2) + 1);
		}

		for (Entry<Node, Integer> entry : componentSize.entrySet()) {
			System.out.println("Component " + entry.getKey().toString()
					+ " --> size " + entry.getValue());
		}

		System.out
				.println("######################################################");
	}
}