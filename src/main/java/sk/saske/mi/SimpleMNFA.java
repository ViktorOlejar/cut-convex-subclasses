package sk.saske.mi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class SimpleMNFA {

	private int numberOfStates;
	private int alphabetSize;
	private ArrayList<ArrayList<HashSet<Integer>>> transitionMatrix;
	private boolean[] initialityArray;
	private boolean[] finalityArray;
	private boolean complete;

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

	public SimpleMNFA(int numberOfStates, int alphabetSize, ArrayList<ArrayList<HashSet<Integer>>> transitionMatrix,
			boolean[] initialityArray, boolean[] finalityArray, boolean complete) {
		super();
		this.numberOfStates = numberOfStates;
		this.alphabetSize = alphabetSize;
		this.transitionMatrix = transitionMatrix;
		this.initialityArray = initialityArray;
		this.finalityArray = finalityArray;
		this.complete = complete;
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

	public boolean isInitial(int state) {
		if (state >= numberOfStates) {
			throw new RuntimeException("Invalid state - exceeding number of states for initiality array!");
		}
		return initialityArray[state];
	}

	public void setInitialityArray(boolean[] initialityArray) {
		this.initialityArray = initialityArray;
	}

	public HashSet<Integer> applySingleInput(int state, int symbol) {
		if (symbol >= alphabetSize) {
			throw new RuntimeException("Invalid symbol - exceeding alphabet size for transition!");
		}
		if (state >= numberOfStates) {
			throw new RuntimeException("Invalid state - exceeding number of states for transition!");
		}

		return transitionMatrix.get(state).get(symbol);
	}

	@Override
	public String toString() {
		return "SimpleMNFA [numberOfStates=" + numberOfStates + ", alphabetSize=" + alphabetSize + ", transitionMatrix="
				+ transitionMatrix + ", initialityArray=" + Arrays.toString(initialityArray) + ", finalityArray="
				+ Arrays.toString(finalityArray) + "]";
	}
	
	public boolean isComplete() {
		return complete;
	}

}
