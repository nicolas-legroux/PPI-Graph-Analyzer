package graph.protein_graph;

import graph.generic_graph.EdgeFactory;
import graph.generic_graph.Graph;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

public class ProteinGraph extends Graph{

	private Map<String, Protein> proteinMapping;

	public ProteinGraph(EdgeFactory edgeFactory, Map<String, Protein> proteinMapping) {
		super(edgeFactory);
		this.proteinMapping = proteinMapping;
	}
	
	public Map<String, Protein> getProteinMapping(){
		return proteinMapping;
	}
	
	public boolean inMapping(String s){
		return proteinMapping.keySet().contains(s);
	}
	
	public static ProteinGraph buildFromStringDatabase(String filename, EdgeFactory edgeFactory,
			Map<String, Protein> mappedProteins) {

		ProteinGraph proteinGraph = new ProteinGraph(edgeFactory, mappedProteins);
		BufferedReader br = null;

		try {
			br = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
		}

		if (br != null) {
			String line = null;
			try {
				//Discar fist line
				line = br.readLine();
				line = br.readLine();
			} catch (IOException e) {				
				e.printStackTrace();
			}

			while (line != null) {

				String[] lineData = line.split("\t");

				try {
					line = br.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}

				String name1 = lineData[0].trim();
				if (name1.indexOf('.') != -1) {
					name1 = name1.substring(name1.indexOf('.') + 1);
				}

				String name2 = lineData[1].trim();
				if (name2.indexOf('.') != -1) {
					name2 = name2.substring(name2.indexOf('.') + 1);
				}

				Protein p1 = mappedProteins.get(name1);
				Protein p2 = mappedProteins.get(name2);

				boolean p1IsMapped = (p1 != null);
				boolean p2IsMapped = (p2 != null);

				if (proteinGraph.findNode(name1) == null) {
					if (!p1IsMapped) {
						p1 = new Protein(name1);
						p1.addEnsemblID(name1);
					}
					proteinGraph.addNode(p1);
				} else {
					if (!p1IsMapped) {
						p1 = (Protein)proteinGraph.findNode(name1);
					}
				}

				if (proteinGraph.findNode(name2) == null) {
					if (!p2IsMapped) {
						p2 = new Protein(name2);
						p1.addEnsemblID(name2);
					}
					proteinGraph.addNode(p2);
				} else {
					if (!p2IsMapped) {
						p2 = (Protein)proteinGraph.findNode(name2);
					}
				}

				ProteinInteraction proteinInteraction = new ProteinInteraction(
						p1, p2, lineData[2],
						Integer.parseInt(lineData[lineData.length - 1]));
				proteinGraph.addEdge(proteinInteraction);
			}
		}

		try {
			br.close();
		} catch (IOException e) {		
			e.printStackTrace();
		}

		return proteinGraph;
	}

	public static ProteinGraph buildFromBiogridDatabase(String filename, EdgeFactory edgeFactory,
			Map<String, Protein> mappedProteins) {

		ProteinGraph proteinGraph = new ProteinGraph(edgeFactory, mappedProteins);
		BufferedReader br = null;

		try {
			br = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		if (br != null) {
			String line = null;
			try {
				line = br.readLine();
				line = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}

			while (line != null) {
				String[] lineData = line.split("\t", -1);

				// Keep Only Human Interactions
				if (lineData[9].equals("9606") && lineData[10].equals("9606")) {

					String name1 = lineData[2].trim().toUpperCase();
					String name2 = lineData[3].trim().toUpperCase();

					Protein p1 = mappedProteins.get(name1);
					Protein p2 = mappedProteins.get(name2);

					boolean p1IsMapped = (p1 != null);
					boolean p2IsMapped = (p2 != null);

					if (proteinGraph.findNode(name1) == null) {
						if (!p1IsMapped) {
							//!:èèSystem.out.println(name1 + " --> " + line);
							p1 = new Protein(name1);
							p1.addHGNCSymbol(name1);
						}
						proteinGraph.addNode(p1);
					} else {
						if (!p1IsMapped) {
							p1 = (Protein) proteinGraph.findNode(name1);
						}
					}

					if (proteinGraph.findNode(name2) == null) {
						if (!p2IsMapped) {
							//System.out.println(name2 + " --> " + line);
							p2 = new Protein(name2);
							p2.addHGNCSymbol(name2);
						}
						proteinGraph.addNode(p2);
					} else {
						if (!p2IsMapped) {
							p2 = (Protein) proteinGraph.findNode(name2);
						}
					}

					ProteinInteraction proteinInteraction1 = new ProteinInteraction(
							p1, p2, "", 1);
					proteinGraph.addEdge(proteinInteraction1);

					ProteinInteraction proteinInteraction2 = new ProteinInteraction(
							p2, p1, "", 1);
					proteinGraph.addEdge(proteinInteraction2);
				}

				try {
					line = br.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		

		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return proteinGraph;
	}
}