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

public class CutAutomaton {

	private SimpleDFA automaton;

	private int automatonANumberOfStates;
	private int automatonBNumberOfStates;

	private boolean isTheInitialOfAFinal;

	public CutAutomaton(ModifiedCevorovaCodeAnalyzer a, ModifiedCevorovaCodeAnalyzer b) {
		cut(a.getParsedAutomaton().getTransitionMatrix(), b.getParsedAutomaton().getTransitionMatrix(),
				a.getParsedAutomaton().getFinalityArray(), b.getParsedAutomaton().getFinalityArray());
	}

	public CutAutomaton(SimpleDFA a, SimpleDFA b) {
		cut(a.getTransitionMatrix(), b.getTransitionMatrix(), a.getFinalityArray(), b.getFinalityArray());
	}

	private void cut(int[][] transitionMatrixA, int[][] transitionMatrixB, boolean[] finalityArrayA,
			boolean[] finalityArrayB) {
		automatonANumberOfStates = transitionMatrixA.length;
		automatonBNumberOfStates = transitionMatrixB.length;
		int numberOfStates;
		int alphabetSize = transitionMatrixA[0].length;
		int[][] transitionMatrix;
		boolean[] finalityArray;

		if (finalityArrayA[0]) {
			isTheInitialOfAFinal = true;
			numberOfStates = automatonANumberOfStates * automatonBNumberOfStates;
			transitionMatrix = new int[numberOfStates][alphabetSize];
			finalityArray = new boolean[numberOfStates];

			for (int sourceState = 0; sourceState < numberOfStates; sourceState++) {
				for (int symbol = 0; symbol < alphabetSize; symbol++) {
					if (!finalityArrayA[transitionMatrixA[sourceState % automatonANumberOfStates][symbol]]) {
						transitionMatrix[sourceState][symbol] = (transitionMatrixB[sourceState
								/ automatonANumberOfStates][symbol] * automatonANumberOfStates)
								+ transitionMatrixA[sourceState % automatonANumberOfStates][symbol];
					} else {
						transitionMatrix[sourceState][symbol] = transitionMatrixA[sourceState
								% automatonANumberOfStates][symbol];
					}
				}
			}

			for (int i = 0; i < finalityArrayB.length; i++) {
				if (finalityArrayB[i]) {
					for (int state = 0; state < automatonANumberOfStates; state++) {
						finalityArray[i * automatonANumberOfStates + state] = true;
					}
				}
			}
		} else {
			isTheInitialOfAFinal = false;
			numberOfStates = automatonANumberOfStates + (automatonANumberOfStates * automatonBNumberOfStates);
			transitionMatrix = new int[numberOfStates][alphabetSize];
			finalityArray = new boolean[numberOfStates];

			for (int sourceState = 0; sourceState < automatonANumberOfStates; sourceState++) {
				for (int symbol = 0; symbol < alphabetSize; symbol++) {
					if (!finalityArrayA[transitionMatrixA[sourceState][symbol]]) {
						transitionMatrix[sourceState][symbol] = transitionMatrixA[sourceState][symbol];
					} else {
						transitionMatrix[sourceState][symbol] = transitionMatrixA[sourceState][symbol]
								+ automatonANumberOfStates;
					}
				}
			}

			for (int sourceState = automatonANumberOfStates; sourceState < numberOfStates; sourceState++) {
				for (int symbol = 0; symbol < alphabetSize; symbol++) {
					if (!finalityArrayA[transitionMatrixA[sourceState % automatonANumberOfStates][symbol]]) {
						transitionMatrix[sourceState][symbol] = (transitionMatrixB[(sourceState
								- automatonANumberOfStates) / automatonANumberOfStates][symbol]
								* automatonANumberOfStates) + automatonANumberOfStates
								+ transitionMatrixA[sourceState % automatonANumberOfStates][symbol];
					} else {
						transitionMatrix[sourceState][symbol] = transitionMatrixA[sourceState
								% automatonANumberOfStates][symbol] + automatonANumberOfStates;
					}
				}
			}

			for (int i = 0; i < finalityArrayB.length; i++) {
				if (finalityArrayB[i]) {
					for (int state = 0; state < automatonANumberOfStates; state++) {
						finalityArray[(i + 1) * automatonANumberOfStates + state] = true;
					}
				}
			}
		}

		automaton = new SimpleDFA(numberOfStates, alphabetSize, transitionMatrix, finalityArray);

	}

	public SimpleDFA getAutomaton() {
		return automaton;
	}
}
