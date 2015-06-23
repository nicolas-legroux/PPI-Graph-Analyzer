package graph.protein_graph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import graph.generic_graph.GraphAnalyzer;
import graph.generic_graph.Node;

public class ProteinGraphAnalyzer extends GraphAnalyzer {
	
	private ProteinGraph graph;

	public ProteinGraphAnalyzer(ProteinGraph graph) {
		super(graph);
		this.graph = graph;
	}
	
	public void printMappedProteins() {
		int mapped = 0;
		int notMapped = 0;

		for(String s : graph.getNodeIdentifiers()) {
			if (graph.inMapping(s)) {
				mapped++;
			} else {
				notMapped++;
			}
		}

		System.out.println("\n######### Protein Mapping Statistics #########");
		float percentageMapped = 100 * (float) mapped
				/ ((float) mapped + (float) notMapped);

		System.out.println("There are " + mapped + " mapped proteins and "
				+ notMapped + " unmapped proteins in the graph ("
				+ percentageMapped + "% of the proteins are mapped).");
		System.out.println("##############################################");
	}
	
	public void compareToEntrezRefereceSet(Collection<String> referenceProteins){
		
		
		System.out.println("\n\n##################################");
		
		Set<String> foundProteins = new HashSet<String>();
		int alreadyInSet = 0;
		for(Node node : graph.getNodes()){
			Protein p = (Protein)node;
			for(String entrezID : p.getEntrezIDs()){
				if(referenceProteins.contains(entrezID)){
					if(!foundProteins.add(entrezID)){
						alreadyInSet++;
					}
				}
			}
		}
		
		System.out.println("There are " + referenceProteins.size() + " proteins in the reference file.");
		System.out.println(foundProteins.size() + " matching proteins were found in the graph");
		System.out.println("Nombre de doublons : " + alreadyInSet);
		
		System.out.println("##################################\n");
	}
	
	public void compareToHGNCReferenceSet(Collection<String> referenceProteins){
		
		
		System.out.println("\n\n##################################");
		
		Set<String> foundProteins = new HashSet<String>();
		int alreadyInSet = 0;
		for(Node node : graph.getNodes()){
			Protein p = (Protein)node;
			for(String hgncSymbol : p.getHGNCSymbols()){
				if(referenceProteins.contains(hgncSymbol)){
					if(!foundProteins.add(hgncSymbol)){
						alreadyInSet++;
					}
				}
			}
		}
		
		System.out.println("There are " + referenceProteins.size() + " proteins in the reference file.");
		System.out.println(foundProteins.size() + " matching proteins were found in the graph");
		System.out.println("Nombre de doublons : " + alreadyInSet);
		
		System.out.println("##################################\n");
	}
}