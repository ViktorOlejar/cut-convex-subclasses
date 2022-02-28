/*
 * Copyright (C) 2016 Viktor Olejár
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sk.saske.mi;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Graph implementation by adjacency matrix for factor free subclass test.
 * 
 */
public class AdjMatrixGraph {

	private int numOfVertices;
	private int[][] adjMatrix;

	/**
	 * Constructor
	 * 
	 * @param numOfVertices
	 */
	public AdjMatrixGraph(int numOfVertices) {
		this.numOfVertices = numOfVertices;
		this.adjMatrix = new int[numOfVertices][numOfVertices];

	}

	/**
	 * Sets directed edge value from source node index u to index v.
	 * 
	 * @param u
	 * @param v
	 * @param value
	 */
	public void setDirectedEdgeValue(int u, int v, int value) {
		adjMatrix[u][v] = value;
	}

	/**
	 * Sets undirected edge value for edge u·==·v.
	 * 
	 * @param u
	 * @param v
	 * @param value
	 */
	public void setUndirectedEdgeValue(int u, int v, int value) {
		adjMatrix[u][v] = value;
		adjMatrix[v][u] = value;
	}

	/**
	 * Returns directed edge value for edge with source node index u and destination
	 * node index v.
	 * 
	 * @param u
	 * @param v
	 * @return int
	 */
	public int getEdgeValue(int u, int v) {
		return adjMatrix[u][v];
	}

	/**
	 * Returns number of graph vertices.
	 *
	 * @return int
	 */
	public int getNumOfVertices() {
		return numOfVertices;
	}

	/**
	 * Returns neighbour indexes of a given node.
	 * 
	 * @param vertex
	 * @return int[]
	 */
	public int[] getNeighbours(int vertex) {
		List<Integer> neigh = new ArrayList<>();

		for (int i = 0; i < numOfVertices; i++) {
			if (getEdgeValue(vertex, i) != 0)
				neigh.add(i);
		}

		return neigh.stream().mapToInt(i -> i).toArray();
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
