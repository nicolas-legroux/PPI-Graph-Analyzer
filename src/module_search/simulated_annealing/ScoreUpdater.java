package module_search.simulated_annealing;

import graph.generic_graph.Graph;
import graph.generic_graph.Node;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


public class ScoreUpdater {

	private class UnionFind {
		private Map<Node, Node> previous;

		public UnionFind(Collection<Node> initialSet) {
			previous = new HashMap<Node, Node>();
			for (Node node : initialSet) {
				makeSet(node);
			}
		}

		public void makeSet(Node node, boolean update) {
			previous.put(node, null);
			if (update) {
				connectedComponents.put(node, new HashSet<Node>());
				connectedComponents.get(node).add(node);
				double score = node.getScore();
				componentScore.put(node, score);
				if(score>maxComponentScore){
					maxComponentScore = score;
				}
			}
		}

		public void makeSet(Node node) {
			makeSet(node, false);
		}

		public Node find(Node node) {

			Node prev = previous.get(node);
			while (prev != null) {

				// Path compression
				if (previous.get(prev) != null) {
					previous.put(node, previous.get(prev));
				}

				node = prev;
				prev = previous.get(prev);
			}

			return node;
		}

		public void union(Node node1, Node node2, boolean update) {
			Node f1 = find(node1);
			Node f2 = find(node2);
			if (!f1.equals(f2)) {
				if (update
						&& connectedComponents.get(f1).size() < connectedComponents
								.get(f2).size()) {
					previous.put(f1, f2);
					double score1 = componentScore.get(f1);
					double score2 = componentScore.get(f2);
					componentScore.put(f2, score1 + score2);
					connectedComponents.get(f2).addAll(
							connectedComponents.get(f1));
					connectedComponents.remove(f1);
					componentScore.remove(f1);
					if (score1 == maxComponentScore
							|| score2 == maxComponentScore) {
						if (score1 + score2 < maxComponentScore) {
							computeMaxScore();
						}
					} else if (score1 + score2 > maxComponentScore) {
						maxComponentScore = score1 + score2;
					}

				} else if (update) {
					previous.put(f2, f1);

					double score1 = componentScore.get(f1);
					double score2 = componentScore.get(f2);
					componentScore.put(f1, score1 + score2);
					connectedComponents.get(f1).addAll(
							connectedComponents.get(f2));
					connectedComponents.remove(f2);
					componentScore.remove(f2);
					if (score1 == maxComponentScore
							|| score2 == maxComponentScore) {
						if (score1 + score2 < maxComponentScore) {
							computeMaxScore();
						}
					} else if (score1 + score2 > maxComponentScore) {
						maxComponentScore = score1 + score2;
					}
				} else {
					previous.put(f1, f2);
				}
			}
		}

		public void union(Node node1, Node node2) {
			union(node1, node2, false);
		}

		// For this to work, all the nodes in the collection must be in the
		// same connected component !
		public void removeConnectedComponent(Collection<Node> nodes) {
			for (Node node : nodes) {
				makeSet(node);
			}
		}

		// So as not to break the algorithm, p MUST be a Singleton !
		public void removeSingleton(Node node) {
			previous.remove(node);
		}
	}

	private Graph graph;
	private Map<Node, Set<Node>> connectedComponents;
	private Map<Node, Double> componentScore;

	private double maxComponentScore;

	UnionFind unionFind;

	public ScoreUpdater(Graph graph) {
		this.graph = graph;
		this.unionFind = new UnionFind(graph.getNodes());
		this.connectedComponents = null;
		this.componentScore = new HashMap<Node, Double>();
	}

	private void computeMaxScore() {
		maxComponentScore = -1000.0;

		for (Entry<Node, Double> entry : componentScore.entrySet()) {			
			if (entry.getValue() > maxComponentScore) {				
				maxComponentScore = entry.getValue();				
			}
		}
	}

	private void makeMappings() {
		connectedComponents = new HashMap<Node, Set<Node>>();

		for (Node node : graph.getNodes()) {
			
			Node connectedComponent = unionFind.find(node);

			if (connectedComponents.get(connectedComponent) == null) {				
				connectedComponents.put(connectedComponent,
						new HashSet<Node>());
				componentScore.put(connectedComponent, 0.0);
			}

			connectedComponents.get(connectedComponent).add(node);
			double score = componentScore.get(connectedComponent) + node.getScore();
			
			componentScore.put(connectedComponent, score);
		}
	}

	public double buildConnectedComponents() {

		if (connectedComponents == null) {
			System.out.println("Doing the unions...");
			for (Node node1 : graph.getNodes()) {
				for (Node node2 : graph.getNeighbors(node1)) {
					unionFind.union(node1, node2);
				}
			}
			System.out.println("Done.");
			System.out.println("Doing the mappings...");
			makeMappings();
			computeMaxScore();			
		}

		return maxComponentScore;
	}

	public double addNode(Node node) {
		unionFind.makeSet(node, true);
		for (Node neighbor : graph.getNeighbors(node)) {
			unionFind.union(node, neighbor, true);
		}

		return maxComponentScore;
	}

	public double removeNode(Node node) {

		// First, get the nodes in the connected component
		Node connectedComponentRepresentative = unionFind.find(node);
		Set<Node> nodesInConnectedComponent = connectedComponents
				.get(connectedComponentRepresentative);

		double score = componentScore.get(connectedComponentRepresentative);
		double oldMaxScore = maxComponentScore;

		// Remove everything from the map data structures
		connectedComponents.remove(connectedComponentRepresentative);
		componentScore.remove(connectedComponentRepresentative);

		nodesInConnectedComponent.remove(node);

		unionFind.removeSingleton(node);
		unionFind.removeConnectedComponent(nodesInConnectedComponent);

		// Assume that the node has been removed from the graph
		for (Node node1 : nodesInConnectedComponent) {
			for (Node node2 : graph.getNeighbors(node1)) {
				unionFind.union(node1, node2);
			}
		}

		for (Node nodeInConnectedComponent : nodesInConnectedComponent) {
			Node newComponentRepresentative = unionFind.find(nodeInConnectedComponent);
			if (connectedComponents.get(newComponentRepresentative) == null) {
				connectedComponents.put(newComponentRepresentative,
						new HashSet<Node>());
				componentScore.put(newComponentRepresentative, 0.0);
			}
			connectedComponents.get(newComponentRepresentative).add(nodeInConnectedComponent);
			double newScore = componentScore.get(newComponentRepresentative)
					+ nodeInConnectedComponent.getScore();
			componentScore.put(newComponentRepresentative, newScore);
			if(newScore > maxComponentScore){
				maxComponentScore = newScore;
			}
		}

		if (score == oldMaxScore) {
			computeMaxScore();
		}

		return maxComponentScore;
	}

	public void printComponentScore() {
		for (Entry<Node, Double> entry : componentScore.entrySet()) {
			System.out.println(entry.getKey().toString() + " : "
					+ entry.getValue());
		}
	}
}