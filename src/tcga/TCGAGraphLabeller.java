package tcga;

import graph.protein_graph.Protein;
import graph.protein_graph.ProteinGraph;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TCGAGraphLabeller {

	ProteinGraph graph;

	public TCGAGraphLabeller(ProteinGraph graph) {
		this.graph = graph;
	}
	
	private Set<Protein> findProteinInBiogridGraph(String entrezID, Map<String, Protein> entrez2Protein){
		Set<Protein> found = new HashSet<Protein>();
		if(!entrez2Protein.containsKey(entrezID)){
			return found;
		}
		for(String hgncSymbol : entrez2Protein.get(entrezID).getHGNCSymbols()){
			if(graph.findNode(hgncSymbol) != null){
				found.add((Protein) graph.findNode(hgncSymbol));
			}
		}	
		return found;
	}
	
	private Set<Protein> findProteinInStringGraph(String entrezID, Map<String, Protein> entrez2Protein){
		Set<Protein> found = new HashSet<Protein>();
		if(!entrez2Protein.containsKey(entrezID)){
			return found;
		}
		for(String ensemblID : entrez2Protein.get(entrezID).getEnsemblIds()){
			if(graph.findNode(ensemblID) != null){
				found.add((Protein) graph.findNode(ensemblID));
			}
		}	
		return found;
	}

	// Biogrid graph uses HGNC Symbol to identify proteins
	// Use HGNC when possible, otherwise use the Mapping EntezID -> Protein that
	// should be provided
	public void labelBiogridGraph(String filename,
			Map<String, Protein> entrez2Protein) {

		BufferedReader br = null;

		int countInGraph = 0;
		int countNotInGraph = 0;
		Set<Protein> labelled = new HashSet<Protein>();

		try {
			br = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		if (br != null) {
			String line = null;
			try {
				// Discard first line
				line = br.readLine();
				line = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}

			while (line != null) {

				String[] lineData = line.split("\t");
				String entrezID = lineData[0].substring(lineData[0]
						.indexOf('|') + 1);
				String hgncSymbol = lineData[0].substring(0,
						lineData[0].indexOf('|')).toUpperCase();
				
				double expression = Double.parseDouble(lineData[1]);
				
				if(graph.findNode(hgncSymbol) != null){
					countInGraph++;	
					labelled.add((Protein) graph.findNode(hgncSymbol));
				}
				else{
					Set<Protein> found = findProteinInBiogridGraph(entrezID, entrez2Protein);
					if(found.size() == 1){	
						Protein p = found.iterator().next();
						if(!labelled.contains(p)){
							countInGraph++;	
							labelled.add(p);
						}
						else{ 							
							System.out.println("Already labelled !!!");
							System.out.println(p.toFullString());
						}
					}
					else if(found.size()>1){
						
						/*
						System.out.println("----- Investigating ------");
						System.out.println("----- " + entrezID + " ------");
						for(Protein p : found){
							System.out.println(p.toFullString());
						}
						System.out.println("-----------------------");
						*/
					}
					else{
						countNotInGraph++;
					}
				}

				try {
					line = br.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		System.out.println("InGraph = " + countInGraph + ", NotInGraph = "
				+ countNotInGraph);

		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void labelStringGraph(String filename,
			Map<String, Protein> entrez2Protein) {

		BufferedReader br = null;

		int countInGraph = 0;
		int countNotInGraph = 0;
		Set<Protein> labelled = new HashSet<Protein>();

		try {
			br = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		if (br != null) {
			String line = null;
			try {
				// Discard first line
				line = br.readLine();
				line = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}

			while (line != null) {

				String[] lineData = line.split("\t");
				String entrezID = lineData[0].substring(lineData[0]
						.indexOf('|') + 1);	
				String hgncSymbol = lineData[0].substring(0,
						lineData[0].indexOf('|')).toUpperCase();
				
				double expression = Double.parseDouble(lineData[1]);
				
				Set<Protein> found = findProteinInStringGraph(entrezID, entrez2Protein);
				if(found.size() == 1){	
					Protein p = found.iterator().next();
					if(!labelled.contains(p)){
						countInGraph++;	
						labelled.add(p);
					}
					else{ 							
						System.out.println("Already labelled : " + entrezID + " " + hgncSymbol);
						System.out.println(p.toFullString());
					}
				}
				else if(found.size()>1){	
					Protein foundWithHgnc = null;
					boolean foundOne = false;
					
					for(Protein p : found){
						if(p.getHGNCSymbols().size() == 1 && p.getHGNCSymbols().iterator().next().equals(hgncSymbol)){
							if(foundWithHgnc==null){
								foundWithHgnc = p;
								foundOne = true;
							}
							else{
								foundOne = false;
							}
						}
					}
					
					if(foundOne){
						if(!labelled.contains(foundWithHgnc)){
							countInGraph++;	
							labelled.add(foundWithHgnc);
						}
						else{ 							
							System.out.println("Already labelled : " + entrezID + " " + hgncSymbol);
							System.out.println(foundWithHgnc.toFullString());
						}
					}
					else{
						System.out.println("----- Investigating ------");
						System.out.println("----- " + entrezID + " " + hgncSymbol + " ------");
						for(Protein p : found){
							System.out.println(p.toFullString());
						}
						System.out.println("-----------------------");
					}
				}
				else{
					countNotInGraph++;
				}				

				try {
					line = br.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		System.out.println("InGraph = " + countInGraph + ", NotInGraph = "
				+ countNotInGraph);

		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}