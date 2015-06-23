package graph.generic_graph;

public interface Edge {
	
	Node getNode1();
	Node getNode2();
	
	void setVisited();
	void resetVisited();
	boolean isVisited();
}