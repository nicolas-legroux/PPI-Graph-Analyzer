package graph.generic_graph;

public interface Node {
	
	String getIdentifier();
	
	void setVisited();
	void resetVisited();
	boolean isVisited();	
	
	double getScore();
	void setScore(double score);	
	
	boolean equals(Object o);
	String toString();
	int hashCode();
}
