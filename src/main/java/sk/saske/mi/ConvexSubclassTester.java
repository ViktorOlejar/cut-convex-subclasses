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
 * Class that handles tests, whether a given SimpleDFA accepts a language of the
 * queried subclass of convex languages. Currently available subclass tests are:
 * 
 * LID  - left ideal 
 * RID  - right ideal 
 * TSID - two-sided ideal 
 * ASID - all-sided ideal 
 * PF   - prefix-free 
 * SF   - suffix-free 
 * FF   - factor-free 
 * SwF  - subword free
 * PC   - prefix-closed 
 * SC   - suffix-closed 
 * FC   - factor-closed 
 * SwC  - subword-closed
 * 
 * The subword freeness test is incomplete! There exist DFAs accepting subword
 * free languages that are not captured. The improvement of the test is left for
 * future work.
 */
public class ConvexSubclassTester extends SimpleDFAOperator {

	/**
	 * Method testing subclass membership of a language given by a SimpleDFA.
	 * 
	 * @param subclass - Subclass abbreviation to test
	 * @param dfa      - Deterministic finite automaton (accepted language to be
	 *                 tested)
	 * @return boolean - True means membership to given subclass, False otherwise
	 */
	public boolean testSubclass(String subclass, SimpleDFA dfa) {
		switch (subclass) {
		case "LID":
			return isLeftIdeal(dfa);
		case "RID":

			return isRightIdeal(dfa);
		case "TSID":

			return isTwoSidedIdeal(dfa);
		case "ASID":

			return isAllSidedIdeal(dfa);
		case "PF":

			return isPrefixFree(dfa);
		case "SF":

			return isSuffixFree(dfa);
		case "FF":

			return isFactorFree(dfa);
		case "SwF":

			return isSubwordFree(dfa);
		case "PC":

			return isPrefixClosed(dfa);
		case "SC":

			return isSuffixClosed(dfa);
		case "FC":

			return isFactorClosed(dfa);
		case "SwC":

			return isSubwordClosed(dfa);
		default:
			System.err.println("Undefined subclass.");
			return false;
		}

	}

	/**
	 * Returns whether given SimpleDFA accepts a prefix free language.
	 * 
	 * @param dfa - Deterministic finite automaton (accepted language to be tested)
	 * @return boolean
	 */
	public boolean isPrefixFree(SimpleDFA dfa) {
		return isNonExiting(dfa);

	}

	/**
	 * Returns whether given SimpleDFA accepts a suffix free language.
	 * 
	 * @param dfa - Deterministic finite automaton (accepted language to be tested)
	 * @return boolean
	 */
	public boolean isSuffixFree(SimpleDFA dfa) {
		if (isNonReturning(dfa))
			return isPrefixFree(minimize(determinize(reverse(dfa))));
		return false;
	}

	/**
	 * Returns whether given SimpleDFA has the non-returning property.
	 * 
	 * @param dfa - Deterministic finite automaton (accepted language to be tested)
	 * @return boolean
	 */
	private boolean isNonReturning(SimpleDFA dfa) {
		for (int state = 0; state < dfa.getNumberOfStates(); state++) {
			for (int symbol = 0; symbol < dfa.getAlphabetSize(); symbol++) {
				if (dfa.applySingleInput(state, symbol) == 0)
					return false;
			}
		}
		return true;
	}

	/**
	 * Returns whether given SimpleDFA has the non-exiting property.
	 * 
	 * @param dfa - Deterministic finite automaton (accepted language to be tested)
	 * @return boolean
	 */
	private boolean isNonExiting(SimpleDFA dfa) {
		// find the unique final state
		int uniqueFinalStateIndex = -1;
		for (int state = 0; state < dfa.getFinalityArray().length; state++) {
			if (dfa.getFinalityArray()[state] && uniqueFinalStateIndex == -1)
				uniqueFinalStateIndex = state;
			else if (dfa.getFinalityArray()[state] && uniqueFinalStateIndex != -1)
				return false;

		}

		// check whether every transition from the unique final state goes to the
		// non-final sink state (assuming minimality)
		Stack<Integer> nStatesOfFinal = getSubsequentNeighbourStates(dfa, uniqueFinalStateIndex);
		int sinkStateIndex = -1;
		if (nStatesOfFinal.size() < dfa.getAlphabetSize())
			return false;

		while (!nStatesOfFinal.empty()) {
			int neigh = nStatesOfFinal.pop().intValue();
			if (sinkStateIndex == -1)
				sinkStateIndex = neigh;
			if (sinkStateIndex != neigh)
				return false;
		}

		if (sinkStateIndex == -1)
			return false;

		for (int symbol = 0; symbol < dfa.getAlphabetSize(); symbol++) {
			if (dfa.getTransitionMatrix()[sinkStateIndex][symbol] != sinkStateIndex)
				return false;
		}
		return true;
	}

	/**
	 * Returns the state indexes of the unique final state and non-final sink state
	 * in a given SimpleDFA. Returns default int array if the DFA does not have
	 * non-exiting property.
	 * 
	 * @param dfa - Deterministic finite automaton (accepted language to be tested)
	 * @return int[]
	 */
	private int[] getNonExitingFinalSinkState(SimpleDFA dfa) {
		int uniqueFinalStateIndex = -1;
		for (int state = 0; state < dfa.getFinalityArray().length; state++) {
			if (dfa.getFinalityArray()[state] && uniqueFinalStateIndex == -1)
				uniqueFinalStateIndex = state;
			else if (dfa.getFinalityArray()[state] && uniqueFinalStateIndex != -1)
				return new int[0];
		}

		Stack<Integer> nStatesOfFinal = getSubsequentNeighbourStates(dfa, uniqueFinalStateIndex);
		int sinkStateIndex = -1;
		if (nStatesOfFinal.size() < dfa.getAlphabetSize())
			return new int[0];

		while (!nStatesOfFinal.empty()) {
			int neigh = nStatesOfFinal.pop().intValue();
			if (sinkStateIndex == -1)
				sinkStateIndex = neigh;
			if (sinkStateIndex != neigh)
				return new int[0];
		}

		if (sinkStateIndex == -1)
			return new int[0];

		for (int symbol = 0; symbol < dfa.getAlphabetSize(); symbol++)
			if (dfa.getTransitionMatrix()[sinkStateIndex][symbol] != sinkStateIndex)
				return new int[0];

		int[] result = { uniqueFinalStateIndex, sinkStateIndex };
		return result;
	}

	/**
	 * Returns whether given SimpleDFA accepts a factor free language. Algorithm
	 * implementation based on the publication:
	 * 
	 * Yo-subhan, & Yajunwang, & Derickwood,. (2011). INFIX-FREE REGULAR EXPRESSIONS
	 * AND LANGUAGES. International Journal of Foundations of Computer Science. 17.
	 * 10.1142/S0129054106003887.
	 * 
	 * @param dfa - Deterministic finite automaton (accepted language to be tested)
	 * @return boolean
	 */
	public boolean isFactorFree(SimpleDFA inputDfa) {

		// test necessary properties
		if (!isNonReturning(inputDfa) || !isNonExiting(inputDfa))
			return false;
		// algorithm is sensitive to improper DFA state indexing - we renumber DFA
		// states to find the optimal indexing
		permutationLoop: for (SimpleDFA dfa : getAutomatonStatePermutations(inputDfa)) {

			int[] correctNumbering = getNonExitingFinalSinkState(dfa);

			if (!(inputDfa.getNumberOfStates() - 1 == correctNumbering[1]
					&& inputDfa.getNumberOfStates() - 2 == correctNumbering[0]))
				continue permutationLoop;

			StatePairGraph spg = new StatePairGraph(dfa);

			for (int v = 1; v <= correctNumbering[0]; v++)
				for (int j = 0; j <= correctNumbering[0]; j++)
					if (spg.dfsPairsReachable(0, v, correctNumbering[0], j))
						continue permutationLoop;

			for (int j = 1; j < correctNumbering[0]; j++)
				if (spg.dfsPairsReachable(0, 0, correctNumbering[0], j))
					continue permutationLoop;

			return true;
		}
		return false;
	}

	/**
	 * Returns whether given SimpleDFA accepts a subword free language. TEST
	 * INCOMPLETE - left for future work
	 * 
	 * @param dfa - Deterministic finite automaton (accepted language to be tested)
	 * @return boolean
	 */
	public boolean isSubwordFree(SimpleDFA inputDfa) {

		permutationLoop: for (SimpleDFA dfa : getAutomatonStatePermutations(inputDfa)) {

			boolean finalStateFlag = false;

			int sinkState = -1;
			for (int state = 0; state < dfa.getNumberOfStates(); state++) {
				Stack<Integer> followers = getSubsequentNeighbourStates(dfa, state);

				if (dfa.getFinalityArray()[state]) {
					if (!finalStateFlag)
						finalStateFlag = true;
					else
						continue permutationLoop;
				}

				if (followers.empty() && !dfa.getFinalityArray()[state])
					sinkState = state;
				if (followers.size() < dfa.getAlphabetSize() && sinkState != state)
					continue permutationLoop;

			}
			if (sinkState == -1)
				continue permutationLoop;

			for (int i = 0; i < dfa.getNumberOfStates(); i++) {
				if (i == sinkState)
					continue;
				for (int j = 0; j < dfa.getNumberOfStates(); j++) {
					if (j == sinkState)
						continue;
					for (int symbol = 0; symbol < dfa.getAlphabetSize(); symbol++) {
						if (dfa.applySingleInput(i, symbol) == sinkState
								|| dfa.applySingleInput(j, symbol) == sinkState)
							continue;
						if (dfa.applySingleInput(i, symbol) <= i)
							continue permutationLoop;
						if (i < j && dfa.applySingleInput(i, symbol) >= dfa.applySingleInput(j, symbol))
							continue permutationLoop;
					}
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Returns whether given SimpleDFA accepts a prefix closed language.
	 * 
	 * @param dfa - Deterministic finite automaton (accepted language to be tested)
	 * @return boolean
	 */
	public boolean isPrefixClosed(SimpleDFA dfa) {
		// complement of PC is RID
		return isRightIdeal(complement(dfa));
	}

	/**
	 * Returns whether given SimpleDFA accepts a suffix closed language.
	 * 
	 * @param dfa - Deterministic finite automaton (accepted language to be tested)
	 * @return boolean
	 */
	public boolean isSuffixClosed(SimpleDFA dfa) {
		// complement of SC is LID
		return isLeftIdeal(complement(dfa));
	}

	/**
	 * Returns whether given SimpleDFA accepts a factor closed language.
	 * 
	 * @param dfa - Deterministic finite automaton (accepted language to be tested)
	 * @return boolean
	 */
	public boolean isFactorClosed(SimpleDFA dfa) {
		// complement of FC is 2ID
		return isTwoSidedIdeal(complement(dfa));
	}

	/**
	 * Returns whether given SimpleDFA accepts a subword closed language.
	 * 
	 * @param dfa - Deterministic finite automaton (accepted language to be tested)
	 * @return boolean
	 */
	public boolean isSubwordClosed(SimpleDFA dfa) {
		// complement of SwC is AllID
		return isAllSidedIdeal(complement(dfa));
	}

	/**
	 * Returns whether given SimpleDFA accepts a right ideal language.
	 * 
	 * @param dfa - Deterministic finite automaton (accepted language to be tested)
	 * @return boolean
	 */
	public boolean isRightIdeal(SimpleDFA dfa) {
		// identify the unique final sink state
		int undeadCandidate = -1;
		for (int state = 0; state < dfa.getNumberOfStates(); state++) {
			if (dfa.getFinalityArray()[state] && undeadCandidate == -1) {
				undeadCandidate = state;
			} else if (dfa.getFinalityArray()[state] && undeadCandidate != -1) {
				return false;
			}
		}

		for (int symbol = 0; symbol < dfa.getAlphabetSize(); symbol++) {
			if (dfa.applySingleInput(undeadCandidate, symbol) != undeadCandidate)
				return false;
		}

		return true;
	}

	/**
	 * Returns whether given SimpleDFA accepts a left ideal language.
	 * 
	 * @param dfa - Deterministic finite automaton (accepted language to be tested)
	 * @return boolean
	 */
	public boolean isLeftIdeal(SimpleDFA dfa) {
		return isRightIdeal(minimize(determinize(reverse(dfa))));
	}

	/**
	 * Returns whether given SimpleDFA accepts a two-sided ideal language.
	 * 
	 * @param dfa - Deterministic finite automaton (accepted language to be tested)
	 * @return boolean
	 */
	public boolean isTwoSidedIdeal(SimpleDFA dfa) {
		return isRightIdeal(dfa) && isLeftIdeal(dfa);
	}

	/**
	 * Returns whether given SimpleDFA accepts an all-sided ideal language.
	 * 
	 * @param dfa - Deterministic finite automaton (accepted language to be tested)
	 * @return boolean
	 */
	public boolean isAllSidedIdeal(SimpleDFA dfa) {
		if (!isTwoSidedIdeal(dfa))
			return false;

		// add Sigma loops and chceck equivalence
		SimpleMNFA mnfa = new SimpleMNFA(dfa);

		for (int state = 0; state < mnfa.getNumberOfStates(); state++) {
			for (int symbol = 0; symbol < mnfa.getAlphabetSize(); symbol++) {
				mnfa.getTransitionMatrix().get(state).get(symbol).add(state);
			}
		}

		SimpleDFA determinizedMNFA = minimize(determinize(mnfa));

		SimpleDFA dfaComplement = complement(dfa);
		SimpleDFA detMNFAComplement = complement(determinizedMNFA);

		SimpleDFA inter1 = intersection(dfa, detMNFAComplement);
		SimpleDFA inter2 = intersection(determinizedMNFA, dfaComplement);

		inter1 = minimize(inter1);
		inter2 = minimize(inter2);

		return isEmptyLanguage(inter1) && isEmptyLanguage(inter2);
	}

}
