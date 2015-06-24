package test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import graph.generic_graph.GraphCleaner;
import graph.protein_graph.Protein;
import graph.protein_graph.ProteinGraph;
import graph.protein_graph.ProteinGraphAnalyzer;
import graph.protein_graph.ProteinInteractionFactory;
import mapping.ProteinMapper;
import module_search.simulated_annealing.*;

public class Main {

	public static void main(String[] args) {

		Map<String, String> entrez2HGNCBijection = ProteinMapper
				.buildEntrezToHGNCBijection("data/protein_mappers/tcga.txt");
		Set<String> HGNCIdentifiers = new HashSet<String>();
		for (String s : entrez2HGNCBijection.values()) {
			HGNCIdentifiers.add(s);
		}

		Map<String, String> ensembl2EntrezBijection = ProteinMapper
				.buildEnsemblToEntrezBijection("data/protein_mappers/entrez_gene_id.vs.string.v10.28042015.tsv");

		System.out.println(ensembl2EntrezBijection.size());
		Map<String, String> ensembl2HGNC = new HashMap<String, String>();
		for (Entry<String, String> entry : ensembl2EntrezBijection.entrySet()) {
			String ensemblID = entry.getKey();
			String entrezID = entry.getValue();
			String HGNCSymbol = entrez2HGNCBijection.get(entrezID);
			if (HGNCSymbol != null) {
				ensembl2HGNC.put(ensemblID, HGNCSymbol);
			}
		}

		Set<String> ensemblIDsToKeep = ensembl2HGNC.keySet();

		ProteinMapper pmb = new ProteinMapper();
		pmb.buildFromEnsemblFile("data/protein_mappers/biomart-v59.txt");
		pmb.buildFromStringMapper("data/protein_mappers/entrez_gene_id.vs.string.v10.28042015.tsv");
		pmb.buildFromHgncFile("data/protein_mappers/HGNC.txt");
		pmb.buildFromTCGAFile("data/protein_mappers/tcga.txt");

		Map<String, Protein> hgnc2Protein = pmb.getHgnc2Protein();
		Map<String, Protein> ensembl2Protein = pmb.getEnsembl2Protein();
		Map<String, Protein> entrez2Protein = pmb.getEntrez2Protein();

		ProteinInteractionFactory proteinInteractionFactory = new ProteinInteractionFactory();
		// ProteinGraph graph = ProteinGraph.buildFromBiogridDatabase(
		// "data/protein_interactions/biogrid-all-interactions.txt",
		// proteinInteractionFactory, hgnc2Protein);
		ProteinGraph graph = ProteinGraph.buildFromStringDatabase(
				"data/protein_interactions/9606.protein.actions.v10.txt",
				proteinInteractionFactory, ensembl2Protein);
		// ProteinGraph graph =
		// ProteinGraph.buildFromStringDatabase("data/protein_interactions/protein-action-2.tsv",
		// ensembl2Protein);

		ProteinGraphAnalyzer graphAnalyzer = new ProteinGraphAnalyzer(graph);
		graphAnalyzer.printGraphSize();
		graphAnalyzer.printMappedProteins();
		graphAnalyzer.printSelfEdges();
		graphAnalyzer.printEdgeType();
		graphAnalyzer.printDoubleEdges();
		graphAnalyzer.printNodeWithMaxDegree();
//		graphAnalyzer.printDegreeDistribution();
//		graphAnalyzer.printconnectedComponents();
		//
		graph.keepNodes(ensemblIDsToKeep);
	
		GraphCleaner graphCleaner = new GraphCleaner(graph);
		graphCleaner.deleteDoubleEdges();
		graphCleaner.deleteSelfEdges();
		graphCleaner.keepLargestComponent();
		graphAnalyzer.printGraphSize();
		graphAnalyzer.printEdgeType();
		graphAnalyzer.printDoubleEdges();
		
		graph.printToFile("string-edges.txt", "string-nodes.txt", ensembl2HGNC);
//		 graphAnalyzer.printMappedProteins();
//		 graphAnalyzer.printNodeWithMaxDegree();
		//
		// graph.printToFile("biogrid-edges.txt", "biogrid-nodes.txt");
	}
}