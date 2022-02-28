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

import java.util.Arrays;

public class SimpleDFA {

	private int numberOfStates;
	private int alphabetSize;
	private int[][] transitionMatrix;
	private boolean[] finalityArray;
	
	public SimpleDFA(int numberOfStates, int alphabetSize, int[][] transitionMatrix, boolean[] finalityArray) {
		this.numberOfStates = numberOfStates;
		
		if (numberOfStates < 1)
			throw new RuntimeException("Invalid number of states (must be non-negatve)");
		
		
		this.alphabetSize = alphabetSize;
		
		if (alphabetSize < 1 || alphabetSize > 26)
			throw new RuntimeException("Invalid alphabet size");
		
		this.transitionMatrix = transitionMatrix;
		this.finalityArray = finalityArray;
	}
	
	public SimpleDFA() {
		super();
	}

	
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

	public int[][] getTransitionMatrix() {
		return transitionMatrix;
	}

	public void setTransitionMatrix(int[][] transitionMatrix) {
		this.transitionMatrix = transitionMatrix;
	}

	public boolean[] getFinalityArray() {
		return finalityArray;
	}

	public void setFinalityArray(boolean[] finalityArray) {
		this.finalityArray = finalityArray;
	}
	
	public int applySingleInput(int state, int symbol) {
		if (symbol >= alphabetSize) {
			throw new RuntimeException("Invalid symbol - exceeding alphabet size for transition!");
		}
		if (state >= numberOfStates) {
			throw new RuntimeException("Invalid state - exceeding number of states for transition!");
		}
		
		return transitionMatrix[state][symbol];
	}
	
	@Override
	public String toString() {
		return "SimpleDFA [numberOfStates=" + numberOfStates + ", alphabetSize=" + alphabetSize + ", transitionMatrix="
				+ Arrays.toString(transitionMatrix) + ", finalityArray=" + Arrays.toString(finalityArray) + "]";
	}

	public SimpleDFA copy() {
		int[][] tm = new int[numberOfStates][alphabetSize];
		boolean[] fa = new boolean[numberOfStates];
		
		for (int i = 0; i < tm.length; i++) {
			tm[i] = Arrays.copyOf(transitionMatrix[i], alphabetSize);
		}

		fa = Arrays.copyOf(finalityArray, fa.length);
		
		return new SimpleDFA(numberOfStates, alphabetSize, tm, fa);
	}
	
	
}
