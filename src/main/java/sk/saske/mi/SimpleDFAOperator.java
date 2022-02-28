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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

/**
 * 
 * Class providing some basic operations on DFAs and MNFAs.
 *
 */

public class SimpleDFAOperator {

	// auxiliary variables for saving possible DFA state renumberings
	private List<int[]> stateNumberPermutation = new ArrayList<>();
	private List<SimpleDFA> automatonStatePermutations = new ArrayList<>();

	/**
	 * Modify input DFA, so that it accepts the complement of the input language.
	 * 
	 * @param dfa
	 * @return SimpleDFA
	 */
	public SimpleDFA complement(SimpleDFA dfa) {
		boolean[] newFinality = new boolean[dfa.getNumberOfStates()];
		for (int i = 0; i < newFinality.length; i++) {
			newFinality[i] = !dfa.getFinalityArray()[i];
		}
		return new SimpleDFA(dfa.getNumberOfStates(), dfa.getAlphabetSize(), dfa.getTransitionMatrix(), newFinality);
	}

	/**
	 * Modify input DFA, so that it accepts the reverse of the input language.
	 * 
	 * @param dfa
	 * @return SimpleMNFA
	 */
	public SimpleMNFA reverse(SimpleDFA dfa) {
		ArrayList<ArrayList<HashSet<Integer>>> mnfaTransitions = reverseTransitions(dfa);
		boolean[] initialStates = dfa.getFinalityArray();
		boolean[] finalStates = new boolean[dfa.getNumberOfStates()];
		finalStates[0] = true;

		return new SimpleMNFA(dfa.getNumberOfStates(), dfa.getAlphabetSize(), mnfaTransitions, initialStates,
				finalStates, false);
	}

	/**
	 * Support method for computing the reversed transitions for the reversal
	 * operation.
	 * 
	 * @param dfa
	 * @return transition function for the reversed MNFA
	 */
	private ArrayList<ArrayList<HashSet<Integer>>> reverseTransitions(SimpleDFA dfa) {
		ArrayList<ArrayList<HashSet<Integer>>> revTransitFunc = new ArrayList<>();

		for (int state = 0; state < dfa.getNumberOfStates(); state++) {
			revTransitFunc.add(new ArrayList<>());
			for (int symbol = 0; symbol < dfa.getAlphabetSize(); symbol++) {
				revTransitFunc.get(state).add(new HashSet<>());
			}
		}

		int sourceState = -1;
		for (int targetStates = 0; targetStates < dfa.getNumberOfStates(); targetStates++) {
			for (int symbol = 0; symbol < dfa.getAlphabetSize(); symbol++) {
				sourceState = dfa.applySingleInput(targetStates, symbol);
				revTransitFunc.get(sourceState).get(symbol).add(targetStates);
			}
		}
		return revTransitFunc;
	}

	/**
	 * Returns whether the input DFA accepts the empty language.
	 * 
	 * @param dfa
	 * @return boolean
	 */
	public boolean isEmptyLanguage(SimpleDFA dfa) {
		SimpleDFA dfaMin = minimize(dfa);

		if (dfaMin.getNumberOfStates() != 1)
			return false;

		if (dfaMin.getFinalityArray()[0])
			return false;

		for (int symbol = 0; symbol < dfaMin.getAlphabetSize(); symbol++) {
			if (dfaMin.applySingleInput(0, symbol) != 0)
				return false;
		}

		return true;
	}

	/**
	 * Returns the DFA accepting the intersection of languages accepted by the two
	 * input DFAs.
	 * 
	 * @param dfa1
	 * @param dfa2
	 * @return SimpleDFA
	 */
	public SimpleDFA intersection(SimpleDFA dfa1, SimpleDFA dfa2) {
		int numOfStates = dfa1.getNumberOfStates() * dfa2.getNumberOfStates();
		int minNumOfStates = Math.min(dfa1.getNumberOfStates(), dfa2.getNumberOfStates());
		if (dfa1.getAlphabetSize() != dfa2.getAlphabetSize()) {
			System.err.println("Unequal alphabet size for intersection.");
			return null;
		}
		int alphabetSize = dfa1.getAlphabetSize();
		int[][] transitionMatrix;
		boolean[] finalityArray;

		finalityArray = new boolean[numOfStates];
		for (int dfa1State = 0; dfa1State < dfa1.getNumberOfStates(); dfa1State++) {
			for (int dfa2State = 0; dfa2State < dfa2.getNumberOfStates(); dfa2State++) {
				if (dfa1.getFinalityArray()[dfa1State] && dfa2.getFinalityArray()[dfa2State]) {
					finalityArray[calculateIntersectionIndex(minNumOfStates, dfa1State, dfa2State)] = true;
				}

			}
		}

		transitionMatrix = new int[numOfStates][alphabetSize];
		for (int dfa1State = 0; dfa1State < dfa1.getNumberOfStates(); dfa1State++) {
			for (int dfa2State = 0; dfa2State < dfa2.getNumberOfStates(); dfa2State++) {
				int sourceState = calculateIntersectionIndex(minNumOfStates, dfa1State, dfa2State);
				for (int symbol = 0; symbol < alphabetSize; symbol++) {
					int targetState = calculateIntersectionIndex(minNumOfStates,
							dfa1.applySingleInput(dfa1State, symbol), dfa2.applySingleInput(dfa2State, symbol));
					transitionMatrix[sourceState][symbol] = targetState;
				}

			}
		}

		return new SimpleDFA(numOfStates, alphabetSize, transitionMatrix, finalityArray);
	}

	/**
	 * Support method for the intersection operation to calculate correct transition
	 * matrix indexes.
	 * 
	 * @param numOfStates
	 * @param state1Index
	 * @param state2Index
	 * @return int
	 */
	private int calculateIntersectionIndex(int numOfStates, int state1Index, int state2Index) {
		return (numOfStates * state1Index) + state2Index;
	}

	/**
	 * Subset construction for converting an input SimpleMNFA to SimpleDFA.
	 * 
	 * @param mnfa
	 * @return SimpleDFA
	 */
	public SimpleDFA determinize(SimpleMNFA mnfa) {
		int numOfStates;
		int alphabetSize = mnfa.getAlphabetSize();
		int[][] transitionMatrix;
		boolean[] finalityArray;

		List<int[]> transitionMatrixBuild = new ArrayList<>();
		List<Boolean> finalityArrayBuild = new ArrayList<>();

		HashSet<HashSet<Integer>> powerSet = new HashSet<>();
		HashMap<HashSet<Integer>, Integer> toResultDFAStateMorphism = new HashMap<>();
		Queue<HashSet<Integer>> queue = new LinkedList<>();
		int stateCounter = 1;

		SimpleMNFA completeMNFA = makeComplete(mnfa);

		HashSet<Integer> init = new HashSet<>();
		for (int i = 0; i < completeMNFA.getNumberOfStates(); i++) {
			if (completeMNFA.getInitialityArray()[i])
				init.add(i);
		}

		toResultDFAStateMorphism.put(init, 0);
		powerSet.add(init);
		queue.add(init);

		boolean finalStateFlag;

		while (!queue.isEmpty()) {
			HashSet<Integer> stateToParse = queue.poll();
			HashSet<Integer> targetState;
			transitionMatrixBuild.add(new int[completeMNFA.getAlphabetSize()]);
			finalStateFlag = false;

			for (int symbol = 0; symbol < completeMNFA.getAlphabetSize(); symbol++) {
				targetState = new HashSet<>();
				// compute transitions
				for (Integer integer : stateToParse) {
					targetState.addAll(completeMNFA.applySingleInput(integer, symbol));
					if (completeMNFA.getFinalityArray()[integer])
						finalStateFlag = true;
				}

				if (!powerSet.contains(targetState)) {
					queue.add(targetState);
					powerSet.add(targetState);
					toResultDFAStateMorphism.put(targetState, stateCounter);
					transitionMatrixBuild
							.get(toResultDFAStateMorphism.get(stateToParse))[symbol] = toResultDFAStateMorphism
									.get(targetState);
					stateCounter++;
				} else {
					transitionMatrixBuild
							.get(toResultDFAStateMorphism.get(stateToParse))[symbol] = toResultDFAStateMorphism
									.get(targetState);
				}
			}
			finalityArrayBuild.add(finalStateFlag);
		}

		numOfStates = transitionMatrixBuild.size();
		transitionMatrix = new int[numOfStates][alphabetSize];
		finalityArray = new boolean[numOfStates];

		for (int state = 0; state < numOfStates; state++) {
			for (int symbol = 0; symbol < alphabetSize; symbol++) {
				transitionMatrix[state][symbol] = transitionMatrixBuild.get(state)[symbol];
			}
			finalityArray[state] = finalityArrayBuild.get(state);
		}

		return new SimpleDFA(numOfStates, alphabetSize, transitionMatrix, finalityArray);
	}

	/**
	 * Completes the transition function of a SimpleMNFA.
	 * 
	 * @param mnfa
	 * @return SimpleMNFA
	 */
	public SimpleMNFA makeComplete(SimpleMNFA mnfa) {
		int numberOfStates = mnfa.getNumberOfStates() + 1;
		int alphabetSize = mnfa.getAlphabetSize();
		ArrayList<ArrayList<HashSet<Integer>>> transitionMatrix = new ArrayList<>();

		boolean[] initialityArray = new boolean[numberOfStates];
		boolean[] finalityArray = new boolean[numberOfStates];
		boolean complete = true;
		int deadState = numberOfStates - 1;

		for (int state = 0; state < mnfa.getNumberOfStates(); state++) {
			transitionMatrix.add(new ArrayList<>());
			for (int symbol = 0; symbol < alphabetSize; symbol++) {
				transitionMatrix.get(state).add(new HashSet<>());
				if (mnfa.applySingleInput(state, symbol).isEmpty()) {
					transitionMatrix.get(state).get(symbol).add(deadState);
				} else {
					transitionMatrix.get(state).get(symbol).addAll(mnfa.applySingleInput(state, symbol));
				}

			}
			finalityArray[state] = mnfa.getFinalityArray()[state];
			initialityArray[state] = mnfa.getInitialityArray()[state];
		}

		// add dead state transitions for completeness
		transitionMatrix.add(new ArrayList<>());
		for (int symbol = 0; symbol < alphabetSize; symbol++) {
			transitionMatrix.get(deadState).add(new HashSet<>());
			transitionMatrix.get(deadState).get(symbol).add(deadState);
		}

		return new SimpleMNFA(numberOfStates, alphabetSize, transitionMatrix, initialityArray, finalityArray, complete);
	}

	/**
	 * Perform Hopcroft minimization on input SimpleDFA.
	 * 
	 * @param automaton
	 * @return SimpleDFA which is minimal
	 */
	public SimpleDFA minimize(SimpleDFA automaton) {
		automaton = removeUnreachableStates(automaton);

		List<SimpleAutomatonStatePartition> partitionIterations = new ArrayList<>();

		SimpleAutomatonStatePartition firstPartition = new SimpleAutomatonStatePartition();
		firstPartition.addNewClass(automaton.getAlphabetSize(), false);
		firstPartition.addNewClass(automaton.getAlphabetSize(), true);

		for (int state = 0; state < automaton.getNumberOfStates(); state++) {
			if (automaton.getFinalityArray()[state]) {
				firstPartition.getEquivalenceClassInPartition(1).addState(state);
			} else {
				firstPartition.getEquivalenceClassInPartition(0).addState(state);
			}
		}
		partitionIterations.add(firstPartition);

		SimpleAutomatonStatePartition secondPartition = new SimpleAutomatonStatePartition();
		for (SimpleAutomatonEquivalenceClass cl : partitionIterations.get(0).getEquivalenceClassesOfPartition()) {

			for (Integer stateInteger : cl.getStatesInClass()) {

				int state = stateInteger.intValue();
				SimpleAutomatonEquivalenceClass originClass = partitionIterations.get(0).getPartitionOfState(state);
				int[] stateClassTransitions = new int[automaton.getAlphabetSize()];

				for (int i = 0; i < stateClassTransitions.length; i++) {
					int targetClass = partitionIterations.get(0)
							.getPartitionIndexOfState(automaton.applySingleInput(state, i));
					stateClassTransitions[i] = targetClass;
				}

				int belongsToClassIndex = secondPartition.containsStateClassTransition(state, stateClassTransitions,
						originClass);
				if (belongsToClassIndex == -1) {
					secondPartition.addNewClass(state, stateClassTransitions.length, stateClassTransitions,
							automaton.getFinalityArray()[state]);
				} else {
					secondPartition.getEquivalenceClassInPartition(belongsToClassIndex).addState(state,
							automaton.getFinalityArray()[state]);
				}
			}
		}
		partitionIterations.add(secondPartition);

		int partitionRefinementCounter = 1;
		while (!stopMinimization(partitionIterations.get(partitionRefinementCounter - 1),
				partitionIterations.get(partitionRefinementCounter))) {

			SimpleAutomatonStatePartition newPartition = new SimpleAutomatonStatePartition();
			for (SimpleAutomatonEquivalenceClass cl : partitionIterations.get(partitionRefinementCounter)
					.getEquivalenceClassesOfPartition()) {

				for (Integer stateInteger : cl.getStatesInClass()) {

					int state = stateInteger.intValue();
					SimpleAutomatonEquivalenceClass originClass = partitionIterations.get(partitionRefinementCounter)
							.getPartitionOfState(state);
					int[] stateClassTransitions = new int[automaton.getAlphabetSize()];

					for (int i = 0; i < stateClassTransitions.length; i++) {
						int targetClass = partitionIterations.get(partitionRefinementCounter)
								.getPartitionIndexOfState(automaton.applySingleInput(state, i));
						stateClassTransitions[i] = targetClass;
					}

					int belongsToClassIndex = newPartition.containsStateClassTransition(state, stateClassTransitions,
							originClass);
					if (belongsToClassIndex == -1) {
						newPartition.addNewClass(state, stateClassTransitions.length, stateClassTransitions,
								automaton.getFinalityArray()[state]);
					} else {
						newPartition.getEquivalenceClassInPartition(belongsToClassIndex).addState(state,
								automaton.getFinalityArray()[state]);
					}
				}
			}
			partitionIterations.add(newPartition);
			partitionRefinementCounter++;
		}

		return createAutomatonFromPartition(partitionIterations.get(partitionIterations.size() - 1));
	}

	/**
	 * Support method for removing unreachable states from input SimpleDFA.
	 * 
	 * @param automaton
	 * @return SimpleDFA with no unreachable states
	 */
	private SimpleDFA removeUnreachableStates(SimpleDFA automaton) {
		boolean[] reachableStates = new boolean[automaton.getNumberOfStates()];

		LinkedList<Integer> queue = new LinkedList<Integer>();
		HashMap<Integer, Integer> stateMapping = new HashMap<>();

		reachableStates[0] = true;
		queue.add(0);

		while (!queue.isEmpty()) {
			int state = queue.poll();
			Stack<Integer> neigh = getSubsequentNeighbourStates(automaton, state);

			while (!neigh.isEmpty()) {
				int n = neigh.pop();
				if (!reachableStates[n]) {
					queue.add(n);
					reachableStates[n] = true;
				}
			}
		}

		int numberOfStates = 0;
		int alphabetSize;
		int[][] transitionMatrix;
		boolean[] finalityArray;

		for (int i = 0; i < reachableStates.length; i++) {
			if (reachableStates[i]) {
				stateMapping.put(i, numberOfStates);
				numberOfStates++;
			}
		}
		if (numberOfStates == automaton.getNumberOfStates())
			return automaton;

		alphabetSize = automaton.getAlphabetSize();

		transitionMatrix = new int[numberOfStates][alphabetSize];
		finalityArray = new boolean[numberOfStates];

		int newStateCounter = 0;
		boolean reachedFlag = false;
		for (int i = 0; i < reachableStates.length; i++) {
			reachedFlag = false;
			for (int symbol = 0; symbol < alphabetSize; symbol++) {

				if (reachableStates[i]) {
					reachedFlag = true;
					transitionMatrix[newStateCounter][symbol] = stateMapping
							.get(automaton.getTransitionMatrix()[i][symbol]);
					if (automaton.getFinalityArray()[i])
						finalityArray[newStateCounter] = true;
				}
			}
			if (reachedFlag)
				newStateCounter++;

		}

		return new SimpleDFA(numberOfStates, alphabetSize, transitionMatrix, finalityArray);
	}

	/**
	 * Support method for retrieving every state reachable on a single alphabet
	 * symbol from a given state in a given SimpleDFA.
	 * 
	 * @param automaton
	 * @param state
	 * @return stack of one symbol reachable states
	 */
	public Stack<Integer> getSubsequentNeighbourStates(SimpleDFA automaton, int state) {
		Stack<Integer> result = new Stack<>();
		for (int symbol = 0; symbol < automaton.getAlphabetSize(); symbol++) {
			if (!(automaton.getTransitionMatrix()[state][symbol] == state))
				result.add(automaton.getTransitionMatrix()[state][symbol]);
		}
		return result;
	}

	/**
	 * Support method for DFA minimization indicating partition refinement halt. The
	 * inputs represent two partitions in subsequent iterations of the Hopcroft
	 * minimization algorithm.
	 * 
	 * @param p
	 * @param k
	 * @return boolean
	 */
	private boolean stopMinimization(SimpleAutomatonStatePartition p, SimpleAutomatonStatePartition k) {
		PartitionComparator pComp = new PartitionComparator();
		if (pComp.compare(p, k) == 0)
			if (pComp.isSameContent())
				return true;
		return false;
	}

	/**
	 * Support method for creating a SimpleDFA from the final state partition of the
	 * Hopcroft minimization algorithm.
	 * 
	 * @param partition
	 * @return SimpleDFA
	 */
	private SimpleDFA createAutomatonFromPartition(SimpleAutomatonStatePartition partition) {
		int numOfStates;
		int alphabetSize;
		int[][] transitionMatrix;
		boolean[] finalityArray;

		numOfStates = partition.getPartitionSize();
		alphabetSize = partition.getEquivalenceClassInPartition(0).getAlphabetSize();

		transitionMatrix = new int[numOfStates][alphabetSize];
		finalityArray = new boolean[numOfStates];

		for (int state = 0; state < numOfStates; state++) {
			for (int symbol = 0; symbol < alphabetSize; symbol++) {
				transitionMatrix[state][symbol] = partition.getEquivalenceClassInPartition(state)
						.getPartitionTransition()[symbol];
				if (partition.getEquivalenceClassInPartition(state).isFinal()) {
					finalityArray[state] = true;
				} else {
					finalityArray[state] = false;
				}
			}
		}

		int initStatePartition = partition.getInitPartitionNumber();

		SimpleDFA automaton = new SimpleDFA(numOfStates, alphabetSize, transitionMatrix, finalityArray);
		changeInitialState(initStatePartition, automaton);

		return automaton;
	}

	/**
	 * Support method for changing the initial state of an input SimpleDFA.
	 * 
	 * @param newInit
	 * @param automaton
	 */
	private void changeInitialState(int newInit, SimpleDFA automaton) {

		boolean newInitFinality = automaton.getFinalityArray()[newInit];
		boolean oldInitFinality = automaton.getFinalityArray()[0];

		automaton.getFinalityArray()[0] = newInitFinality;
		automaton.getFinalityArray()[newInit] = oldInitFinality;

		int[] newInitTrans = automaton.getTransitionMatrix()[newInit];
		int[] oldInitTrans = automaton.getTransitionMatrix()[0];

		automaton.getTransitionMatrix()[newInit] = oldInitTrans;
		automaton.getTransitionMatrix()[0] = newInitTrans;

		boolean swap = false;
		int target;
		for (int state = 0; state < automaton.getNumberOfStates(); state++) {
			for (int symbol = 0; symbol < automaton.getAlphabetSize(); symbol++) {
				swap = false;
				target = automaton.getTransitionMatrix()[state][symbol];

				if (target == 0) {
					automaton.getTransitionMatrix()[state][symbol] = newInit;
					swap = true;
				}

				if (!swap && target == newInit)
					automaton.getTransitionMatrix()[state][symbol] = 0;

			}
		}

	}

	/**
	 * Returns every possible state renumbering of a given input SimpleDFA
	 * 
	 * @param automaton
	 * @return an array of renumbered automata
	 */
	public SimpleDFA[] getAutomatonStatePermutations(SimpleDFA automaton) {

		int[] permBaseElements = new int[automaton.getNumberOfStates() - 1];
		for (int i = 1; i <= permBaseElements.length; i++) {
			permBaseElements[i - 1] = i;
		}
		returnAllRecursive(permBaseElements.length, permBaseElements, automaton.copy());

		SimpleDFA[] permutedDFAStates = new SimpleDFA[automatonStatePermutations.size()];

		permutedDFAStates = automatonStatePermutations.toArray(permutedDFAStates);

		automatonStatePermutations.clear();
		stateNumberPermutation.clear();
		return permutedDFAStates;
	}

	/**
	 * Support method for recursively enumerating every possible automaton state
	 * renumbering.
	 * 
	 * @param n        - permutation length
	 * @param elements - elements to permute
	 * @param dfa      - automaton to perform renumbering on
	 */
	private void returnAllRecursive(int n, int[] elements, SimpleDFA dfa) {

		if (n == 1) {
			stateNumberPermutation.add(elements.clone());
			automatonStatePermutations.add(dfa.copy());
		} else {

			for (int i = 0; i < n; i++) {
				returnAllRecursive(n - 1, elements, dfa);
				if (i < n - 1)
					if (n % 2 == 0)
						swap(elements, i, n - 1, dfa);
					else
						swap(elements, 0, n - 1, dfa);

			}

		}
	}

	/**
	 * Support method for performing the renumbering when two state indexes are
	 * swapped.
	 * 
	 * @param input - elements to permute
	 * @param a     - state index for swapping
	 * @param b     - state index for swapping
	 * @param dfa   - automaton to perform renumbering on
	 */
	private void swap(int[] input, int a, int b, SimpleDFA dfa) {
		renumberThisAutomaton(dfa, input[a], input[b]);

		int tmp = input[a];
		input[a] = input[b];
		input[b] = tmp;

	}

	/**
	 * Support method for directly exchanging state number indexes.
	 * 
	 * @param dfa
	 * @param stateA
	 * @param stateB
	 */
	private void renumberThisAutomaton(SimpleDFA dfa, int stateA, int stateB) {
		HashMap<Integer, Integer> stateMap = new HashMap<>();
		stateMap.put(stateA, stateB);
		stateMap.put(stateB, stateA);

		int[] aTrans = Arrays.copyOf(dfa.getTransitionMatrix()[stateA], dfa.getAlphabetSize());
		int[] bTrans = Arrays.copyOf(dfa.getTransitionMatrix()[stateB], dfa.getAlphabetSize());

		boolean aFinal = dfa.getFinalityArray()[stateA];
		boolean bFinal = dfa.getFinalityArray()[stateB];

		dfa.getTransitionMatrix()[stateA] = Arrays.copyOf(bTrans, bTrans.length);
		dfa.getTransitionMatrix()[stateB] = Arrays.copyOf(aTrans, aTrans.length);

		for (int state = 0; state < dfa.getNumberOfStates(); state++) {
			for (int symbol = 0; symbol < dfa.getAlphabetSize(); symbol++) {
				if (dfa.getTransitionMatrix()[state][symbol] == stateA
						|| dfa.getTransitionMatrix()[state][symbol] == stateB)
					dfa.getTransitionMatrix()[state][symbol] = stateMap.get(dfa.getTransitionMatrix()[state][symbol]);
			}
		}

		dfa.getFinalityArray()[stateA] = bFinal;
		dfa.getFinalityArray()[stateB] = aFinal;

	}

	/**
	 * Returns a DFA with newly mapped transition symbols based on input mapping.
	 * 
	 * @param dfa
	 * @param alphabetMapping
	 * @return SimpleDFA
	 */
	public SimpleDFA homomorphicImage(SimpleDFA dfa, int[] alphabetMapping) {
		if (alphabetMapping.length != dfa.getAlphabetSize()) {
			System.err.println("Inconsistent mapping with alphabet size.");
			return null;
		}

		int numOfStates = dfa.getNumberOfStates();
		int alphabetSize = dfa.getAlphabetSize();
		boolean[] finalityArray = Arrays.copyOf(dfa.getFinalityArray(), dfa.getFinalityArray().length);

		int[][] transitionMatrix = new int[numOfStates][alphabetSize];
		for (int symbol = 0; symbol < alphabetSize; symbol++) {
			for (int state = 0; state < numOfStates; state++) {
				transitionMatrix[state][symbol] = dfa.getTransitionMatrix()[state][alphabetMapping[symbol]];
			}
		}
		return new SimpleDFA(numOfStates, alphabetSize, transitionMatrix, finalityArray);
	}

}
