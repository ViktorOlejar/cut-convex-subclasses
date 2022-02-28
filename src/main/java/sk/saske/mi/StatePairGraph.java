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

public class StatePairGraph {

	private SimpleDFA dfa;
	private AdjMatrixGraph graphOfDFA;

	public SimpleDFA getDfa() {
		return dfa;
	}

	public AdjMatrixGraph getGraphOfDFA() {
		return graphOfDFA;
	}

	public StatePairGraph(SimpleDFA dfa) {
		this.dfa = dfa;
		this.graphOfDFA = new AdjMatrixGraph(dfa.getNumberOfStates() * dfa.getNumberOfStates());

		for (int lftStateI = 0; lftStateI < dfa.getNumberOfStates(); lftStateI++)
			for (int rgtStateJ = 0; rgtStateJ < dfa.getNumberOfStates(); rgtStateJ++)
				for (int symbol = 0; symbol < dfa.getAlphabetSize(); symbol++)
					graphOfDFA.setDirectedEdgeValue(graphOfDfaIndex(lftStateI, rgtStateJ), graphOfDfaIndex(
							dfa.applySingleInput(lftStateI, symbol), dfa.applySingleInput(rgtStateJ, symbol)), 1);
	}

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

	private int graphOfDfaIndex(int lftState, int rgtState) {
		return ((dfa.getNumberOfStates() * lftState) + rgtState);
	}
}
