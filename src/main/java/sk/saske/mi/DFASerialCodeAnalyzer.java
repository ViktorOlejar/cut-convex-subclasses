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

/**
 * 
 * Class implementing the custom automaton code parser used in computations. The
 * encoding tries to describe a DFA in a single line assuming a specific
 * assignment of nonnegative integers to states.
 * 
 * Example:
 * 
 * The encoding of a minimal DFA over a binary alphabet that recognizes the
 * language where the first symbol has an odd occurrence in every string of the
 * accepted language is given by:
 * 
 * 1001FT
 * 
 * Such DFA has two states. The part "FT" indicates that the first state is
 * non-final (False) while the second is final (True). The part of the encoding
 * "1001" indicates that the transitions are given as (0,"first_symbol",1),
 * (0,"second_symbol",0), (1,"first_symbol",0), (1,"second_symbol",1). The
 * initial state is always 0 for this encoding.
 *
 */

public class DFASerialCodeAnalyzer {
	// currently maximum alphabet size
	private static final int MAX_ALPHABET_SIZE = 26;

	// parsed automaton
	private SimpleDFA automaton;
	// corresponding automaton encoding
	private String automatonCode;

	/**
	 * Constructor for the DFA encoder implementation.
	 * 
	 * @param numOfStates
	 * @param alphabetSize
	 */
	public DFASerialCodeAnalyzer(int numOfStates, int alphabetSize) {
		automaton = new SimpleDFA();
		automaton.setAlphabetSize(alphabetSize);
		automaton.setNumberOfStates(numOfStates);
	}

	/**
	 * Parses an input automaton encoding and creates a corresponding SimpleDFA
	 * (saving it to variable automaton)
	 * 
	 * @param code - automaton encoding
	 */
	public void parse(String code) {
		try {
			if (code.length() < 1)
				throw new InvalidDFASerialCodeException("Incomplete automaton code.");

			code = code.trim();

			automatonCode = code;

			String codeSuffix = code.substring(code.length() - automaton.getNumberOfStates());
			String codeTransitions = code.substring(0, code.length() - automaton.getNumberOfStates());

			initializeFinalityArray(codeSuffix);
			initializeTransitionMatrix(codeTransitions);
		} catch (InvalidDFASerialCodeException e) {
			throw e;
		} catch (Exception e) {
			throw new InvalidDFASerialCodeException("Check validity of input DFA code.");
		}

	}

	/**
	 * Support method for parsing an automaton code and creating a transition
	 * function from it.
	 * 
	 * @param codeTransitions - part of the automaton encoding responsible for
	 *                        encoding transitions
	 */
	private void initializeTransitionMatrix(String codeTransitions) {

		if (automaton.getAlphabetSize() > MAX_ALPHABET_SIZE)
			throw new InvalidDFASerialCodeException(
					"Alphabet size too large (greater than " + MAX_ALPHABET_SIZE + ").");

		int[][] transitionMatrix = new int[automaton.getNumberOfStates()][automaton.getAlphabetSize()];

		int currState = 0;
		for (int i = 0; i < codeTransitions.length(); i++) {

			if (Character.getNumericValue(codeTransitions.charAt(i)) < 0
					|| Character.getNumericValue(codeTransitions.charAt(i)) >= automaton.getNumberOfStates()) {
				throw new InvalidDFASerialCodeException(
						"Invalid transition code at index " + codeTransitions.charAt(i) + " .");
			} else {
				transitionMatrix[currState][i % automaton.getAlphabetSize()] = Character
						.getNumericValue(codeTransitions.charAt(i));
				if (i % automaton.getAlphabetSize() == automaton.getAlphabetSize() - 1)
					currState++;
			}
		}
		automaton.setTransitionMatrix(transitionMatrix);
	}

	/**
	 * Support method for parsing an automaton code and identifying final states
	 * from it.
	 * 
	 * @param codeSuffix - part of the automaton encoding responsible for encoding
	 *                   final states
	 */
	private void initializeFinalityArray(String codeSuffix) {
		codeSuffix = codeSuffix.toLowerCase();
		boolean[] finalityArray = new boolean[codeSuffix.length()];
		for (int i = 0; i < codeSuffix.length(); i++) {
			switch (codeSuffix.charAt(i)) {
			case 'f':
				finalityArray[i] = false;
				break;

			case 't':
				finalityArray[i] = true;
				break;

			default:
				throw new InvalidDFASerialCodeException(
						"Invalid state finality sequence. Unknown finality character: " + codeSuffix.charAt(i));
			}
		}
		automaton.setFinalityArray(finalityArray);
	}

	/**
	 * Getters.
	 */
	public SimpleDFA getParsedAutomaton() {
		if (automaton.getFinalityArray() == null) {
			throw new RuntimeException("No automaton parsed.");
		}
		return automaton;
	}

	public String getParsedAutomatonCode() {
		if (automatonCode == null) {
			throw new RuntimeException("No automaton parsed.");
		}
		return automatonCode;
	}

}
