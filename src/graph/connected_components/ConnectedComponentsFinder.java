package graph.connected_components;

import graph.generic_graph.Graph;
import graph.generic_graph.Node;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class ConnectedComponentsFinder {

	private class UnionFind {
		private Map<Node, Node> previous;

		public UnionFind(Collection<Node> initialSet) {
			previous = new HashMap<Node, Node>();
			for (Node node : initialSet) {
				makeSet(node);
			}
		}
		
		public void makeSet(Node node){
			previous.put(node, null);
		}

		public Node find(Node node) {

			Node prev = previous.get(node);
			while (prev != null) {
				
				//Path compression
				if (previous.get(prev) != null) {
					previous.put(node, previous.get(prev));
				}
				
				node = prev;
				prev = previous.get(prev);
			}

			return node;
		}

		public void union(Node node1, Node node2) {
			Node f1 = find(node1);
			Node f2 = find(node2);
			if (!f1.equals(f2)) {
				previous.put(f1, f2);
			}
		}
	}

	private Graph graph;
	private Map<Node, Node> mappingToConnectedComponents;
	private Map<Node, Set<Node>> connectedComponents;
	
	UnionFind unionFind;

	public ConnectedComponentsFinder(Graph graph) {
		this.graph = graph;
		unionFind = new UnionFind(graph.getNodes());
		mappingToConnectedComponents = null;
		connectedComponents = null;
	}
	
	private void makeMappings(){
		connectedComponents = new HashMap<Node, Set<Node>>();
		mappingToConnectedComponents = new HashMap<Node, Node>();

		for (Node node : graph.getNodes()) {
			
			Node connectedComponentRepresentative = unionFind.find(node);
			
			mappingToConnectedComponents.put(node, connectedComponentRepresentative);
			
			if(connectedComponents.get(connectedComponentRepresentative)==null){
				connectedComponents.put(connectedComponentRepresentative, new HashSet<Node>());
			}
			
			connectedComponents.get(connectedComponentRepresentative).add(node);			
		}		
	}

	private void buildConnectedComponents() {

		System.out.println("Doing the unions...");
		for (Node node1 : graph.getNodes()) {
			for (Node node2 : graph.getNeighbors(node1)) {
				unionFind.union(node1, node2);
			}
		}
		System.out.println("Done.");

		makeMappings();
	}
	
	public Set<Node> getConnectedComponents() {
		if(connectedComponents == null){
			buildConnectedComponents();
		}
		return connectedComponents.keySet();
	}

	public Map<Node, Node> getMappingToConnectedComponents() {
		if(connectedComponents == null){
			buildConnectedComponents();
		}
		return mappingToConnectedComponents;		
	}	
}