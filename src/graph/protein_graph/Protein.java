package graph.protein_graph;

import graph.generic_graph.Node;

import java.util.HashSet;
import java.util.Set;

public class Protein implements Node{	
	
	String identifier;
	private Set<String> ensemblIDs;
	private Set<String> entrezIDs;
	private Set<String> HGNCSymbols;
	
	//For graph search algorithms
	private boolean visited;
	
	//Activity
	private double score;
	
	public Protein(String identifier){
		this.identifier = identifier;
		this.ensemblIDs = new HashSet<String>();	
		this.entrezIDs = new HashSet<String>();
		this.HGNCSymbols = new HashSet<String>();
		this.score = 0;
		this.visited = false;
	}
	
	public void setVisited(){
		this.visited = true;
	}
	
	public void resetVisited(){
		this.visited = false;
	}
	
	public  boolean isVisited(){
		return visited;
	}
	
	public String getIdentifier(){
		return identifier;
	}
	
	public void setScore(double score){
		this.score = score;
	}
	
	public double getScore(){
		return score;
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(!(o instanceof Protein)) return false;
		Protein protein = (Protein) o;
		return protein.getIdentifier().equals(this.getIdentifier());
	}
	
	@Override
	public int hashCode(){
		return getIdentifier().hashCode();
	}
	
	@Override
	public String toString(){
		return getIdentifier();
	}
	
	public String toFullString(){
		StringBuilder sb = new StringBuilder();
		sb.append("{ Identifier=" + getIdentifier() + ", ensemblIDs=[ ");
		
		for(String s : ensemblIDs){
			sb.append(s + " ");
		}
		
		sb.append("], entrezIDs=[ ");		
		for(String s : entrezIDs){
			sb.append(s + " ");
		}
		
		sb.append("], HGNCSymbols=[ ");
		for(String s : HGNCSymbols){
			sb.append(s + " ");
		}
		
		sb.append("] }");		
		
		return sb.toString();
	}
	
	public void addEnsemblID(String s){
		ensemblIDs.add(s);
	}
	
	public void addEntrezID(String s){
		entrezIDs.add(s);
	}
	
	public void addHGNCSymbol(String s){
		HGNCSymbols.add(s);
	}
	
	public Set<String> getEnsemblIds(){
		return ensemblIDs;
	}
	
	public Set<String> getEntrezIDs(){
		return entrezIDs;
	}
	
	public Set<String> getHGNCSymbols(){
		return HGNCSymbols;
	}
}