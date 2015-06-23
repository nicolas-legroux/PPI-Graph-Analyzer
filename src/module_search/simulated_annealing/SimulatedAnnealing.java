package module_search.simulated_annealing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import graph.generic_graph.Edge;
import graph.generic_graph.Graph;
import graph.generic_graph.Node;
import graph.protein_graph.Protein;

public class SimulatedAnnealing {
	
	Graph originalGraph;
	Graph workingGraph;
	
	//Use this for constant time random access to the proteins
	ArrayList<Node> nodesInOriginalGraph;	
	
	//Parameters
	int N;
	double Tstart;
	double Tend;
	
	Random random;
	
	double currentScore;
	double currentT;
	
	ScoreUpdater scoreUpdater;
	
	public SimulatedAnnealing(Graph graph, int N, double Tstart, double Tend){
		this.originalGraph = graph;
		this.workingGraph = null;
		this.nodesInOriginalGraph = new ArrayList<Node>();
		for(Node node : graph.getNodes()){
			nodesInOriginalGraph.add(node);
		}
		random = new Random(0);
		this.N = N;
		this.Tstart = Tstart;
		this.Tend = Tend;
		this.currentT = Tstart;
	}
	
	private void addNodeToWorkingGraph(Node node){
		workingGraph.addNode(node);		
		for(Edge edge : originalGraph.getEdgesFrom(node)){
			if(workingGraph.contains(edge.getNode2())){
				workingGraph.addUndirectedEdge(node, edge.getNode2());
			}
		}		
	} 
	
	private void removeNodeFromWorkingGraph(Node node){		
		workingGraph.removeNode(node, false);
	}
	
	public void setRandomNodeScores(){	
		for(int i=0; i<nodesInOriginalGraph.size(); i++){
			nodesInOriginalGraph.get(i).setScore(-1.0);			
		}	
	}
	
	public void initializeWorkingGraph(){
		
		Set<Node> initialNodes = new HashSet<Node>();
		
		for(Node node : nodesInOriginalGraph){
			if(random.nextBoolean()){
				initialNodes.add(node);
			}
		}
		
		workingGraph = originalGraph.getSubgraph(initialNodes);	
		scoreUpdater = new ScoreUpdater(workingGraph);
		currentScore = scoreUpdater.buildConnectedComponents();		
	}
	
	public void iterate(){	
		int i = random.nextInt(nodesInOriginalGraph.size());
		Node randomNode = nodesInOriginalGraph.get(i);
		
		if(workingGraph.contains(randomNode)){
			removeNodeFromWorkingGraph(randomNode);
			double nextScore = scoreUpdater.removeNode(randomNode);
			if(nextScore < currentScore){
				double p = Math.exp((nextScore-currentScore)/currentT);
				if(random.nextDouble()>p){
					addNodeToWorkingGraph(randomNode);
					scoreUpdater.addNode(randomNode);					
				}
				else{					
					currentScore = nextScore;
				}
			}
			else{				
				currentScore = nextScore;				
			}
		}
		
		else{
			addNodeToWorkingGraph(randomNode);
			double nextScore = scoreUpdater.addNode(randomNode);
			if(nextScore < currentScore){
				double p = Math.exp((nextScore-currentScore)/currentT);
				if(random.nextDouble()>p){
					removeNodeFromWorkingGraph(randomNode);
					scoreUpdater.removeNode(randomNode);					
				}
				else{					
					currentScore = nextScore;
				}
			}
			else{				
				currentScore = nextScore;				
			}			
		}
	}
	
	public void executeSimulatedAnnealing(){	
		setRandomNodeScores();
		initializeWorkingGraph();
		
		for(int i=0; i<N; i++){			
			iterate();
			if(i%1000 == 0){
				System.out.println("Pass " + i + ", T=" + currentT + ", currentScore=" + currentScore);
			}
			currentT = currentT * Math.pow(Tend/Tstart, 1/(double)N);
		}
	}
}