package test;
import java.util.Map;

import graph.generic_graph.GraphCleaner;
import graph.protein_graph.Protein;
import graph.protein_graph.ProteinGraph;
import graph.protein_graph.ProteinGraphAnalyzer;
import graph.protein_graph.ProteinInteractionFactory;
import mapping.ProteinMapper;
import module_search.simulated_annealing.*;

public class Main {

	public static void main(String[] args) {	
		
		Map<String, String> tcgaBijection = ProteinMapper.buildEntrezHGNCBijection("data/protein_mappers/tcga.txt");		
		
		ProteinMapper pmb = new ProteinMapper();
		pmb.buildFromEnsemblFile("data/protein_mappers/biomart-v59.txt");
		pmb.buildFromStringMapper("data/protein_mappers/entrez_gene_id.vs.string.v10.28042015.tsv");
		pmb.buildFromHgncFile("data/protein_mappers/HGNC.txt");
		pmb.buildFromTCGAFile("data/protein_mappers/tcga.txt");
		
		Map<String, Protein> hgnc2Protein = pmb.getHgnc2Protein();		
		Map<String, Protein> ensembl2Protein = pmb.getEnsembl2Protein();
		Map<String, Protein> entrez2Protein = pmb.getEntrez2Protein();
		
		ProteinInteractionFactory proteinInteractionFactory = new ProteinInteractionFactory();
		//ProteinGraph graph = ProteinGraph.buildFromBiogridDatabase("data/protein_interactions/BIOGRID-ALL-3.4.125.tab.txt", proteinInteractionFactory, hgnc2Protein);
		ProteinGraph graph = ProteinGraph.buildFromStringDatabase("data/protein_interactions/9606.protein.actions.v10.txt", proteinInteractionFactory, ensembl2Protein);
		//ProteinGraph graph = ProteinGraph.buildFromStringDatabase("data/protein_interactions/protein-action-2.tsv", ensembl2Protein);	
		
		
		ProteinGraphAnalyzer graphAnalyzer = new ProteinGraphAnalyzer(graph);
		
		graphAnalyzer.printGraphSize();	
		graphAnalyzer.printMappedProteins();
//		graphAnalyzer.printSelfEdges();
//		graphAnalyzer.printEdgeType();
//		graphAnalyzer.printDoubleEdges();
//		graphAnalyzer.printNodeWithMaxDegree();
//		graphAnalyzer.printDegreeDistribution();
		graphAnalyzer.compareToEntrezRefereceSet(tcgaBijection.keySet());
		graphAnalyzer.compareToHGNCReferenceSet(tcgaBijection.values());
		//graphAnalyzer.printconnectedComponents();	
		
		GraphCleaner graphCleaner = new GraphCleaner(graph);			
		graphCleaner.deleteDoubleEdges();
		graphCleaner.deleteSelfEdges();
		graphCleaner.keepLargestComponent();			
	}
}