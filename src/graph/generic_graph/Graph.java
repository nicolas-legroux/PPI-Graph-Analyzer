package graph.generic_graph;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Graph {

	private Map<String, Node> nodes;
	private Map<Node, List<Edge>> graph;
	private EdgeFactory edgeFactory;

	public Graph() {
		this.nodes = new HashMap<String, Node>();
		this.graph = new HashMap<Node, List<Edge>>();
		this.edgeFactory = null;
	}

	public Graph(EdgeFactory edgeFactory) {
		this.nodes = new HashMap<String, Node>();
		this.graph = new HashMap<Node, List<Edge>>();
		this.edgeFactory = edgeFactory;
	}

	public Node findNode(String nodeIdentifier) {
		return nodes.get(nodeIdentifier);
	}

	public boolean contains(Node node) {
		return findNode(node.getIdentifier()) != null;
	}

	public Collection<Node> getNodes() {
		return nodes.values();
	}

	public Set<String> getNodeIdentifiers() {
		return nodes.keySet();
	}

	public List<Edge> getEdgesFrom(Node node) {
		return graph.get(node);
	}

	public Set<Node> getNeighbors(Node node) {
		Set<Node> neighbors = new HashSet<Node>();
		for (Edge edge : getEdgesFrom(node)) {
			neighbors.add(edge.getNode2());
		}
		return neighbors;
	}

	public void printNeighbors(Node node) {
		System.out.println("Printing neighbors of " + node.toString() + " : ");
		System.out.print("[ ");
		for (Node neighbor : getNeighbors(node)) {
			System.out.print(neighbor.toString() + " ");
		}
		System.out.println("]");
	}

	// Adds a new node to the graph.
	public void addNode(Node node) {
		nodes.put(node.getIdentifier(), node);
		graph.put(node, new LinkedList<Edge>());
	}

	public void removeNode(Node node, boolean removeNeighbors) {
		nodes.remove(node.getIdentifier());

		if (graph.containsKey(node)) {

			Set<Node> neighbors = getNeighbors(node);
			neighbors.remove(node);

			graph.remove(node);

			for (Node neighbor : neighbors) {
				List<Edge> newNeighbors = new LinkedList<Edge>();
				for (Edge edge : getEdgesFrom(neighbor)) {
					if (!edge.getNode2().equals(node)) {
						newNeighbors.add(edge);
					}
				}
				resetNeighbors(neighbor, newNeighbors);
			}

			if (removeNeighbors) {
				for (Node neighbor : neighbors) {
					if (graph.get(neighbor).size() == 0) {
						nodes.remove(neighbor.getIdentifier());
						graph.remove(neighbor);
					}
				}
			}
		}
	}

	public void setVisited(Node node1, Node node2) {
		for (Edge edge : getEdgesFrom(node1)) {
			if (edge.getNode2().equals(node2)) {
				edge.setVisited();
			}
		}

		for (Edge edge : getEdgesFrom(node2)) {
			if (edge.getNode2().equals(node1)) {
				edge.setVisited();
			}
		}
	}

	public void removeNodeWithNoNeighbor(Node node) {
		nodes.remove(node.getIdentifier());
		graph.remove(node);
	}

	public void removeNode(Node node) {
		removeNode(node, true);
	}

	public void resetNeighbors(Node node, List<Edge> newNeighbors) {
		graph.put(node, newNeighbors);
	}

	public void addEdge(Edge edge) {
		Node fst = edge.getNode1();
		graph.get(fst).add(edge);
	}

	public void addUndirectedEdge(Node node1, Node node2) {
		addEdge(edgeFactory.create(node1, node2));
		addEdge(edgeFactory.create(node2, node1));
	}

	public int getDegree(Node node) {
		if (contains(node)) {
			return graph.get(node).size();
		} else
			return -1;
	}

	public int getGraphSize() {
		return graph.size();
	}

	public int getNumberOfEdges() {
		int numberOfEdges = 0;
		for (Node node : getNodes()) {
			numberOfEdges += getDegree(node);
		}
		return numberOfEdges;
	}

	public boolean existsEdgeBetween(Node node1, Node node2) {
		for (Edge edge : getEdgesFrom(node1)) {
			if (node2.equals(edge.getNode2())) {
				return true;
			}
		}

		return false;
	}

	public void resetVisited() {
		for (Node node : getNodes()) {
			node.resetVisited();
			for (Edge edge : getEdgesFrom(node)) {
				edge.resetVisited();
			}
		}
	}

	public Graph getSubgraph(Set<Node> nodes) {
		Graph subgraph = new Graph(edgeFactory);
		for (Node node : nodes) {
			subgraph.addNode(node);
		}
		for (Node node : nodes) {
			for (Edge edge : getEdgesFrom(node)) {
				if (nodes.contains(edge.getNode2())) {
					subgraph.addEdge(edge);
				}
			}
		}

		return subgraph;
	}

	public void keepNodes(Set<String> nodeIdentifiers) {
		Set<Node> toRemove = new HashSet<Node>();
		for (Node node : getNodes()) {
			if (!nodeIdentifiers.contains(node.getIdentifier())) {
				toRemove.add(node);
			}
		}
		for (Node node : toRemove) {
			removeNode(node);
		}
	}

	public void printToFile(String filenameEdges, String filenameNodes) {

		try {
			resetVisited();
			System.out.println("\n################################");
			System.out.println("Writing graph in file...");
			Set<String> nodestoPrint = new HashSet<String>();

			File fileEdges = new File(filenameEdges);
			// if file doesnt exists, then create it
			if (!fileEdges.exists()) {
				fileEdges.createNewFile();
			}

			BufferedWriter bwEdges = new BufferedWriter(new FileWriter(
					fileEdges.getAbsoluteFile()));

			for (Node node : getNodes()) {
				for (Edge edge : getEdgesFrom(node)) {
					if (!edge.isVisited()) {
						nodestoPrint.add(edge.getNode1().getIdentifier());
						nodestoPrint.add(edge.getNode2().getIdentifier());
						bwEdges.write(edge.getNode1().getIdentifier() + " "
								+ edge.getNode2().getIdentifier() + "\n");
						setVisited(edge.getNode1(), edge.getNode2());
					}
				}
			}

			bwEdges.close();

			File fileNodes = new File(filenameNodes);
			// if file doesnt exists, then create it
			if (!fileNodes.exists()) {
				fileNodes.createNewFile();
			}

			BufferedWriter bwNodes = new BufferedWriter(new FileWriter(
					fileNodes.getAbsoluteFile()));

			for (String s : nodestoPrint) {
				bwNodes.write(s + "\n");
			}

			bwNodes.close();

			System.out.println("Done.");
			System.out.println("################################");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void printToFile(String filenameEdges, String filenameNodes,
			Map<String, String> identifierMapping) {

		try {
			resetVisited();
			System.out.println("\n################################");
			System.out.println("Writing graph in file...");
			Set<String> nodestoPrint = new HashSet<String>();

			File fileEdges = new File(filenameEdges);
			// if file doesnt exists, then create it
			if (!fileEdges.exists()) {
				fileEdges.createNewFile();
			}

			BufferedWriter bwEdges = new BufferedWriter(new FileWriter(
					fileEdges.getAbsoluteFile()));

			for (Node node : getNodes()) {
				for (Edge edge : getEdgesFrom(node)) {
					if (!edge.isVisited()
							&& identifierMapping.containsKey(edge.getNode1()
									.getIdentifier())
							&& identifierMapping.containsKey(edge.getNode2()
									.getIdentifier())) {
						nodestoPrint.add(edge.getNode1().getIdentifier());
						nodestoPrint.add(edge.getNode2().getIdentifier());
						bwEdges.write(identifierMapping.get(edge.getNode1()
								.getIdentifier())
								+ " "
								+ identifierMapping.get(edge.getNode2()
										.getIdentifier()) + "\n");
						setVisited(edge.getNode1(), edge.getNode2());
					}
				}
			}

			bwEdges.close();

			File fileNodes = new File(filenameNodes);
			// if file doesnt exists, then create it
			if (!fileNodes.exists()) {
				fileNodes.createNewFile();
			}

			BufferedWriter bwNodes = new BufferedWriter(new FileWriter(
					fileNodes.getAbsoluteFile()));

			for (String s : nodestoPrint) {
				bwNodes.write(s + "\n");
			}

			bwNodes.close();

			System.out.println("Done.");
			System.out.println("################################");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}