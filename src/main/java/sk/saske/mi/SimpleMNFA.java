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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * 
 * A class representing a nondeterministic finite automaton with multiple
 * initial states (MNFA). States are indexed by natural numbers. Transitions are
 * modeled by a transition table, where in row x and column y is a set of states
 * to which state x goes on symbol y. Final states are indicated by a finality
 * array and initial states by an initiality array. There is an auxiliary
 * variable indicating whether the given MNFA is complete. In this specific
 * implementation, no epsilon moves are allowed.
 *
 */

public class SimpleMNFA {

	// number of MNFA states
	private int numberOfStates;
	// Sigma size
	private int alphabetSize;
	// transition function
	private ArrayList<ArrayList<HashSet<Integer>>> transitionMatrix;
	// initial states
	private boolean[] initialityArray;
	// final states
	private boolean[] finalityArray;
	// boolean flag, whether the transition function is complete
	private boolean complete;

	/**
	 * Constructor for MNFA implementation.
	 * 
	 * @param numberOfStates
	 * @param alphabetSize
	 */
	public SimpleMNFA(int numberOfStates, int alphabetSize) {
		this.numberOfStates = numberOfStates;
		this.alphabetSize = alphabetSize;

		this.initialityArray = new boolean[numberOfStates];
		this.finalityArray = new boolean[numberOfStates];

		this.transitionMatrix = new ArrayList<>();

		for (int state = 0; state < numberOfStates; state++) {
			this.transitionMatrix.add(new ArrayList<>());
			for (int symbol = 0; symbol < alphabetSize; symbol++) {
				this.transitionMatrix.get(state).add(new HashSet<>());
			}
		}
		this.complete = false;
	}

	/**
	 * Constructor for MNFA implementation.
	 * 
	 * @param dfa
	 */
	public SimpleMNFA(SimpleDFA dfa) {
		this.numberOfStates = dfa.getNumberOfStates();
		this.alphabetSize = dfa.getAlphabetSize();

		this.initialityArray = new boolean[numberOfStates];
		initialityArray[0] = true;
		this.finalityArray = dfa.getFinalityArray();

		this.transitionMatrix = new ArrayList<>();

		for (int state = 0; state < numberOfStates; state++) {
			this.transitionMatrix.add(new ArrayList<>());
			for (int symbol = 0; symbol < alphabetSize; symbol++) {
				this.transitionMatrix.get(state).add(new HashSet<>());
				this.transitionMatrix.get(state).get(symbol).add(dfa.applySingleInput(state, symbol));
			}
		}
		this.complete = true;
	}

	/**
	 * Constructor for MNFA implementation.
	 * 
	 * @param numberOfStates
	 * @param alphabetSize
	 * @param transitionMatrix
	 * @param initialityArray
	 * @param finalityArray
	 * @param complete
	 */
	public SimpleMNFA(int numberOfStates, int alphabetSize, ArrayList<ArrayList<HashSet<Integer>>> transitionMatrix,
			boolean[] initialityArray, boolean[] finalityArray, boolean complete) {
		this.numberOfStates = numberOfStates;
		this.alphabetSize = alphabetSize;
		this.transitionMatrix = transitionMatrix;
		this.initialityArray = initialityArray;
		this.finalityArray = finalityArray;
		this.complete = complete;
	}

	/**
	 * Setters and getters.
	 */

	public int getNumberOfStates() {
		return numberOfStates;
	}

	public void setNumberOfStates(int numberOfStates) {
		this.numberOfStates = numberOfStates;
	}

	public int getAlphabetSize() {
		return alphabetSize;
	}

	public void setAlphabetSize(int alphabetSize) {
		this.alphabetSize = alphabetSize;
	}

	public ArrayList<ArrayList<HashSet<Integer>>> getTransitionMatrix() {
		return transitionMatrix;
	}

	public void setTransitionMatrix(ArrayList<ArrayList<HashSet<Integer>>> transitionMatrix) {
		this.transitionMatrix = transitionMatrix;
	}

	public boolean[] getFinalityArray() {
		return finalityArray;
	}

	public void setFinalityArray(boolean[] finalityArray) {
		this.finalityArray = finalityArray;
	}

	public boolean[] getInitialityArray() {
		return initialityArray;
	}
	
	public void setInitialityArray(boolean[] initialityArray) {
		this.initialityArray = initialityArray;
	}
	
	public boolean isComplete() {
		return complete;
	}

	/**
	 * Returns whether given state index represents an initial state.
	 * 
	 * @param state
	 * @return boolean
	 */
	public boolean isInitial(int state) {
		if (state >= numberOfStates) {
			throw new RuntimeException("Invalid state - exceeding number of states for initiality array.");
		}
		return initialityArray[state];
	}

	/**
	 * Apply transition from state on symbol and return the target state.
	 * 
	 * @param state
	 * @param symbol
	 * @return set of target states
	 */
	public HashSet<Integer> applySingleInput(int state, int symbol) {
		if (symbol >= alphabetSize) {
			throw new RuntimeException("Invalid symbol - exceeding alphabet size for transition.");
		}
		if (state >= numberOfStates) {
			throw new RuntimeException("Invalid state - exceeding number of states for transition.");
		}

		return transitionMatrix.get(state).get(symbol);
	}

	@Override
	public String toString() {
		return "SimpleMNFA [numberOfStates=" + numberOfStates + ", alphabetSize=" + alphabetSize + ", transitionMatrix="
				+ transitionMatrix + ", initialityArray=" + Arrays.toString(initialityArray) + ", finalityArray="
				+ Arrays.toString(finalityArray) + "]";
	}

}
