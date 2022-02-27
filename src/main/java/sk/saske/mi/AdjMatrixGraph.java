package sk.saske.mi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class AdjMatrixGraph {

	private int numOfVertices;
	private int[][] adjMatrix;
	
	
	public AdjMatrixGraph(int numOfVertices) {
		super();
		this.numOfVertices = numOfVertices;
		this.adjMatrix = new int[numOfVertices][numOfVertices];
		
	}
	
	public void setDirectedEdgeValue(int u, int v, int value) {
		adjMatrix[u][v] = value;
	}
	
	public void setUndirectedEdgeValue(int u, int v, int value) {
		adjMatrix[u][v] = value;
		adjMatrix[v][u] = value;
	}
	
	public int getEdgeValue(int u, int v) {
		return adjMatrix[u][v];
	}

	public int getNumOfVertices() {
		return numOfVertices;
	}


	public int[] getNeighbours(int vertex) {
		List<Integer> neigh = new ArrayList<>();
		
		for (int i = 0; i < numOfVertices; i++) {
			if(getEdgeValue(vertex, i) != 0)
				neigh.add(i);
		}
		
		return neigh.stream().mapToInt(i->i).toArray();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < numOfVertices; i++) {
			for (int j = 0; j < numOfVertices; j++) {
				sb.append(adjMatrix[i][j] + "\t");
			}
			sb.append(System.lineSeparator());
		}
		
		return sb.toString();
	}
	
	
	
}
