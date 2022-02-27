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
