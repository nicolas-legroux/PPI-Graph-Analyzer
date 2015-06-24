package mapping;


import graph.protein_graph.Protein;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class ProteinMapper {

	private Map<String, Protein> ensembl2Protein;
	private Map<String, Protein> entrez2Protein;
	private Map<String, Protein> hgnc2Protein;

	public ProteinMapper() {
		this.ensembl2Protein = new HashMap<String, Protein>();
		this.entrez2Protein = new HashMap<String, Protein>();
		this.hgnc2Protein = new HashMap<String, Protein>();
	}

	public Map<String, Protein> getEnsembl2Protein() {
		return ensembl2Protein;
	}

	public Map<String, Protein> getEntrez2Protein() {
		return entrez2Protein;
	}

	public Map<String, Protein> getHgnc2Protein() {
		return hgnc2Protein;
	}

	public void printMapping(Map<String, Protein> mapping) {
		for (Protein p : mapping.values()) {
			System.out.println(p.toFullString());
		}
		System.out.println("Mapped " + mapping.values().size() + " proteins.");
	}

	private void addMapping(String ensemblID, String entrezID, String HGNCSymbol) {

		if (!ensemblID.isEmpty()) {
			if (!ensembl2Protein.containsKey(ensemblID)
					&& (!entrezID.isEmpty() || !HGNCSymbol.isEmpty())) {
				ensembl2Protein.put(ensemblID, new Protein(ensemblID));
				ensembl2Protein.get(ensemblID).addEnsemblID(ensemblID);

			}

			if (!entrezID.isEmpty()) {
				ensembl2Protein.get(ensemblID).addEntrezID(entrezID);
			}

			if (!HGNCSymbol.isEmpty()) {
				ensembl2Protein.get(ensemblID).addHGNCSymbol(HGNCSymbol);
			}
		}

		if (!entrezID.isEmpty()) {
			if (!entrez2Protein.containsKey(entrezID)
					&& (!ensemblID.isEmpty() || !HGNCSymbol.isEmpty())) {
				entrez2Protein.put(entrezID, new Protein(entrezID));
				entrez2Protein.get(entrezID).addEntrezID(entrezID);
				;
			}

			if (!ensemblID.isEmpty()) {
				entrez2Protein.get(entrezID).addEnsemblID(ensemblID);
			}

			if (!HGNCSymbol.isEmpty()) {
				entrez2Protein.get(entrezID).addHGNCSymbol(HGNCSymbol);
			}
		}

		if (!HGNCSymbol.isEmpty()) {
			if (!hgnc2Protein.containsKey(HGNCSymbol)
					&& (!ensemblID.isEmpty() || !entrezID.isEmpty())) {
				hgnc2Protein.put(HGNCSymbol, new Protein(HGNCSymbol));
				hgnc2Protein.get(HGNCSymbol).addHGNCSymbol(HGNCSymbol);

			}

			if (!ensemblID.isEmpty()) {
				hgnc2Protein.get(HGNCSymbol).addEnsemblID(ensemblID);
			}

			if (!entrezID.isEmpty()) {
				hgnc2Protein.get(HGNCSymbol).addEntrezID(entrezID);
			}
		}
	}

	public void buildFromEnsemblFile(String filename) {
		
		System.out.println("\nMapping from Ensembl Database...");

		BufferedReader br = null;

		try {
			br = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException e) {
			System.out.println("File does not exist.");
			return;
		}

		int countEnsemblEntries = ensembl2Protein.size();
		int countEntrezEntries = entrez2Protein.size();
		int countHgncEntries = hgnc2Protein.size();

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

				String[] lineData = line.split("\t", -1);
				String entrezID = lineData[2].trim();
				String HGNCSymbol = lineData[3].trim().toUpperCase();
				String ensemblID = lineData[1].trim();				

				addMapping(ensemblID, entrezID, HGNCSymbol);

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

		System.out.println("Done. Created "
				+ (ensembl2Protein.size() - countEnsemblEntries)
				+ " new Ensembl entries, "
				+ (entrez2Protein.size() - countEntrezEntries)
				+ " new Entrez entries, " + (hgnc2Protein.size() - countHgncEntries) + " new HGNC entries.");
	}
	
public void buildFromStringMapper(String filename) {
		
		System.out.println("\nMapping from STRINGv10 Mapping file...");

		BufferedReader br = null;

		try {
			br = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException e) {
			System.out.println("File does not exist.");
			return;
		}

		int countEnsemblEntries = ensembl2Protein.size();
		int countEntrezEntries = entrez2Protein.size();
		int countHgncEntries = hgnc2Protein.size();

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

				String[] lineData = line.split("\t", -1);
				String entrezID = lineData[0].trim();				
				String ensemblID = lineData[1].trim();	
				
				if (ensemblID.indexOf('.') != -1) {
					ensemblID = ensemblID.substring(ensemblID.indexOf('.') + 1);
				}

				addMapping(ensemblID, entrezID, "");

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

		System.out.println("Done. Created "
				+ (ensembl2Protein.size() - countEnsemblEntries)
				+ " new Ensembl entries, "
				+ (entrez2Protein.size() - countEntrezEntries)
				+ " new Entrez entries, " + (hgnc2Protein.size() - countHgncEntries) + " new HGNC entries.");
	}

	public void buildFromEntrezFile(String filename) {
		
		System.out.println("\nMapping from Entrez Database...");

		BufferedReader br = null;

		try {
			br = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException e) {
			System.out.println("File does not exist.");
			return;
		}
		
		int countEnsemblEntries = ensembl2Protein.size();
		int countEntrezEntries = entrez2Protein.size();
		int countHgncEntries = hgnc2Protein.size();

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

				if (line.contains("ENSP000")) {
					String[] lineData = line.split("\t", -1);

					String entrezID = lineData[1].trim();
					String ensemblID = lineData[6].trim();

					addMapping(ensemblID, entrezID, "");
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
		
		System.out.println("Done. Created "
				+ (ensembl2Protein.size() - countEnsemblEntries)
				+ " new Ensembl entries, "
				+ (entrez2Protein.size() - countEntrezEntries)
				+ " new Entrez entries, " + (hgnc2Protein.size() - countHgncEntries) + " new HGNC entries.");
	}

	public void buildFromTCGAFile(String filename) {
		
		System.out.println("\nMapping from TCGA File...");
		
		BufferedReader br = null;

		try {
			br = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException e) {
			System.out.println("File does not exist.");
			return;
		}
		
		int countEnsemblEntries = ensembl2Protein.size();
		int countEntrezEntries = entrez2Protein.size();
		int countHgncEntries = hgnc2Protein.size();
		
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
				String HGNCSymbol = lineData[0].substring(0, lineData[0].indexOf('|')).toUpperCase();
				String entrezID = lineData[0].substring(lineData[0].indexOf('|')+1);
				
				if(HGNCSymbol.equals("?")){
					HGNCSymbol = "";
				}
				
				addMapping("", entrezID, HGNCSymbol);
				
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
		
		System.out.println("Done. Created "
				+ (ensembl2Protein.size() - countEnsemblEntries)
				+ " new Ensembl entries, "
				+ (entrez2Protein.size() - countEntrezEntries)
				+ " new Entrez entries, " + (hgnc2Protein.size() - countHgncEntries) + " new HGNC entries.");
	}
	
	public void buildFromHgncFile(String filename){
		System.out.println("\nMapping from HGNC File...");
		
		BufferedReader br = null;

		try {
			br = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException e) {
			System.out.println("File does not exist.");
			return;
		}
		
		int countEnsemblEntries = ensembl2Protein.size();
		int countEntrezEntries = entrez2Protein.size();
		int countHgncEntries = hgnc2Protein.size();
		
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

				String[] lineData = line.split("\t", -1);	
				String HGNCSymbol = lineData[1].toUpperCase().trim();				
				String entrezID = lineData[10].trim();	
				String entrezID2 = lineData[8].trim();
				
				addMapping("", entrezID, HGNCSymbol);
				addMapping("", entrezID2, HGNCSymbol);
				
				String[] previousNames = lineData[4].split(",", -1);
				for(String previousName : previousNames){
					previousName = previousName.toUpperCase().trim();
					addMapping("", entrezID, previousName);
					addMapping("", entrezID2, previousName);
				}	
				
				String[] synonyms = lineData[5].split(",", -1);
				for(String synonym: synonyms){
					synonym = synonym.toUpperCase().trim();
					addMapping("", entrezID, synonym);
					addMapping("", entrezID2, synonym);
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
		
		System.out.println("Done. Created "
				+ (ensembl2Protein.size() - countEnsemblEntries)
				+ " new Ensembl entries, "
				+ (entrez2Protein.size() - countEntrezEntries)
				+ " new Entrez entries, " + (hgnc2Protein.size() - countHgncEntries) + " new HGNC entries.");
	}
	
	public void buildFromNCBIFile(String filename){
		System.out.println("\nMapping from NCBI File...");
		
		BufferedReader br = null;

		try {
			br = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException e) {
			System.out.println("File does not exist.");
			return;
		}
		
		int countEnsemblEntries = ensembl2Protein.size();
		int countEntrezEntries = entrez2Protein.size();
		int countHgncEntries = hgnc2Protein.size();
		
		if (br != null) {
			String line = null;
			try {				
				line = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}

			while (line != null) {

				String[] lineData = line.split("\t", -1);	
				String HGNCSymbol = lineData[2].toUpperCase().trim();				
				String entrezID = lineData[1].trim();			
				
				addMapping("", entrezID, HGNCSymbol);	
				
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
		
		System.out.println("Done. Created "
				+ (ensembl2Protein.size() - countEnsemblEntries)
				+ " new Ensembl entries, "
				+ (entrez2Protein.size() - countEntrezEntries)
				+ " new Entrez entries, " + (hgnc2Protein.size() - countHgncEntries) + " new HGNC entries.");
	}
	
	public void build(String ensemblFileName, String hgncFileName,  String tcgaFileName, String stringFileName){
		System.out.println("################# Protein Mapper ###############");
		buildFromEnsemblFile(ensemblFileName);			
		buildFromHgncFile(hgncFileName);
		buildFromTCGAFile(tcgaFileName);
		buildFromStringMapper(stringFileName);
		System.out.println("################################################");
	}
	
	public static Map<String, String> buildEnsemblToEntrezBijection(String stringMapperFilename){
		
		Map<String, String> map = new HashMap<String, String>();
		BufferedReader br = null;

		try {
			br = new BufferedReader(new FileReader(stringMapperFilename));
		} catch (FileNotFoundException e) {
			System.out.println("File does not exist.");
			return null;
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

				String[] lineData = line.split("\t", -1);
				String entrezID = lineData[0].trim();				
				String ensemblID = lineData[1].trim();	
				
				if (ensemblID.indexOf('.') != -1) {
					ensemblID = ensemblID.substring(ensemblID.indexOf('.') + 1);
				}
				
				map.put(ensemblID, entrezID);

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
		
		return map;
	}
	
	public static Map<String, String> buildEntrezToHGNCBijection(String tcgaFilename){
		Map<String, String> map = new HashMap<String, String>();

		BufferedReader br = null;

		try {
			br = new BufferedReader(new FileReader(tcgaFilename));
		} catch (FileNotFoundException e) {
			System.out.println("File does not exist.");
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
				String HGNCSymbol = lineData[0].substring(0, lineData[0].indexOf('|')).toUpperCase();
				String entrezID = lineData[0].substring(lineData[0].indexOf('|')+1);
				
				if(!HGNCSymbol.equals("?")){
					map.put(entrezID, HGNCSymbol);
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
		
		return map;
	}
}