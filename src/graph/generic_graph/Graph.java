package graph.generic_graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Graph{
	
	private Map<String, Node> nodes;
	private Map<Node, List<Edge>> graph;
	private EdgeFactory edgeFactory;
	
	public Graph(){
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
	
	public Set<String> getNodeIdentifiers(){
		return nodes.keySet();
	}
	
	public List<Edge> getEdgesFrom(Node node){
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
		
		if(graph.containsKey(node)){

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
			
			if(removeNeighbors){		
				for (Node neighbor : neighbors) {
					if(graph.get(neighbor).size() == 0){
						nodes.remove(neighbor.getIdentifier());
						graph.remove(neighbor);				
					}
				}
			}
		}
	}
	
	public void removeNode(Node node){
		removeNode(node, true);		
	}
	
	public void resetNeighbors(Node node, List<Edge> newNeighbors){
		graph.put(node, newNeighbors);
	}

	public void addEdge(Edge edge) {
		Node fst = edge.getNode1();
		graph.get(fst).add(edge);
	}
	
	public void addUndirectedEdge(Node node1, Node node2){
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
	
	public void removeNodeWithNoNeighbor(Node node){
		nodes.remove(node.getIdentifier());
		graph.remove(node);
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
	
	public void resetVisited(){
		for(Node node : getNodes()){
			node.resetVisited();
		}
	}
	
	public Graph getSubgraph(Set<Node> nodes){
		Graph subgraph = new Graph(edgeFactory);
		for(Node node : nodes){
			subgraph.addNode(node);
		}
		for(Node node : nodes){
			for(Edge edge : getEdgesFrom(node)){
				if(nodes.contains(edge.getNode2())){
					subgraph.addEdge(edge);
				}
			}
		}
		
		return subgraph;
	}
}