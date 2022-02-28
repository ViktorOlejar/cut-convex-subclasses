/*
 * Copyright (C) 2022 Viktor Olejár
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

import java.util.Stack;

/**
 * 
 * State pair graph implementation for factor free subclass test as described in
 * 
 * Yo-subhan, & Yajunwang, & Derickwood,. (2011). INFIX-FREE REGULAR EXPRESSIONS
 * AND LANGUAGES. International Journal of Foundations of Computer Science. 17.
 * 10.1142/S0129054106003887.
 * 
 * Similar to a product automaton.
 */

public class StatePairGraph {

	// automaton to create state pair graph from
	private SimpleDFA dfa;
	// implementation of the state pair graph
	private AdjMatrixGraph graphOfDFA;

	/**
	 * Constructor for state pair graph implementation.
	 * 
	 * @param dfa
	 */
	public StatePairGraph(SimpleDFA dfa) {
		this.dfa = dfa;
		this.graphOfDFA = new AdjMatrixGraph(dfa.getNumberOfStates() * dfa.getNumberOfStates());

		for (int lftStateI = 0; lftStateI < dfa.getNumberOfStates(); lftStateI++)
			for (int rgtStateJ = 0; rgtStateJ < dfa.getNumberOfStates(); rgtStateJ++)
				for (int symbol = 0; symbol < dfa.getAlphabetSize(); symbol++)
					graphOfDFA.setDirectedEdgeValue(graphOfDfaIndex(lftStateI, rgtStateJ), graphOfDfaIndex(
							dfa.applySingleInput(lftStateI, symbol), dfa.applySingleInput(rgtStateJ, symbol)), 1);
	}

	/**
	 * Returns whether the given target node (described by coordinates lftTarget and
	 * rgtTarget) is reachable from the given source node (described by coordinates
	 * lftInitial and rgtInitial) in the state pair graph.
	 * 
	 * @param lftInitial - source node first coordinate
	 * @param rgtInitial - source node second coordinate
	 * @param lftTarget  - target node first coordinate
	 * @param rgtTarget  - target node second coordinate
	 * @return boolean - whether the target node is reachable from the source node
	 */
	public boolean dfsPairsReachable(int lftInitial, int rgtInitial, int lftTarget, int rgtTarget) {
		int initIndex = graphOfDfaIndex(lftInitial, rgtInitial);
		int targetIndex = graphOfDfaIndex(lftTarget, rgtTarget);

		Stack<Integer> stack = new Stack<>();
		boolean[] isVisited = new boolean[graphOfDFA.getNumOfVertices()];

		stack.push(initIndex);

		while (!stack.isEmpty()) {
			int current = stack.pop();
			isVisited[current] = true;

			if (current == targetIndex)
				return true;

			for (int dest : graphOfDFA.getNeighbours(current))
				if (!isVisited[dest])
					stack.push(dest);

		}

		return false;
	}

	/**
	 * Support method that computes the real node index in a state pair graph
	 * implementation based on its "2D" coordinates.
	 * 
	 * @param lftState - first coordinate
	 * @param rgtState - second coordinate
	 * @return int
	 */
	private int graphOfDfaIndex(int lftState, int rgtState) {
		return ((dfa.getNumberOfStates() * lftState) + rgtState);
	}

	/**
	 * Getters.
	 */

	public SimpleDFA getDfa() {
		return dfa;
	}

	public AdjMatrixGraph getGraphOfDFA() {
		return graphOfDFA;
	}
}
