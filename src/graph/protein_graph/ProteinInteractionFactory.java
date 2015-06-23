package graph.protein_graph;

import graph.generic_graph.Edge;
import graph.generic_graph.EdgeFactory;
import graph.generic_graph.Node;

public class ProteinInteractionFactory implements EdgeFactory {

	@Override
	public Edge create(Node node1, Node node2) {
		Protein p1 = (Protein)node1;
		Protein p2 = (Protein)node2;
		return new ProteinInteraction(p1, p2);
	}
}