package graph.protein_graph;

import graph.generic_graph.Edge;

public class ProteinInteraction implements Edge {
	
	Protein fst;
	Protein snd;
	String interactionType;
	int interactionValue;
	
	boolean isVisited;
	
	public ProteinInteraction(Protein _fst, Protein _snd, String _interactionType, int _interactionValue){
		this.fst = _fst;
		this.snd = _snd;
		this.interactionType = _interactionType;
		this.interactionValue = _interactionValue;
		this.isVisited = false;
	}
	
	public ProteinInteraction(Protein fst, Protein snd){
		this(fst, snd, "", 0);
	}
	
	public Protein getNode1(){
		return fst;
	}
	
	public Protein getNode2(){
		return snd;
	}
	
	String getInteractionType(){
		return interactionType;
	}
	
	int getInteractionValue(){
		return interactionValue;
	}
	
	@Override
	public String toString(){
		String s = "{" + fst.toString() + "->" + snd.toString() + ", interactionType=" + interactionType + ", interactionValue=" + interactionValue + "}";
		return s;
	}

	@Override
	public void setVisited() {
		isVisited = true;
		
	}

	@Override
	public void resetVisited() {
		isVisited = false;		
	}

	@Override
	public boolean isVisited() {
		return isVisited;
	}
}