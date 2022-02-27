package sk.saske.mi;

import java.util.Stack;

public class ConvexSubclassTester extends SimpleDFAOperator {

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
			System.err.println("UNKNOWN SUBCLASS!!!");
			return false;
		}

	}

	// Freeness
	public boolean isPrefixFree(SimpleDFA dfa) {
		return isNonExiting(dfa);

	}

	public boolean isPrefixFree(SimpleMNFA mmnfa) {
		return isPrefixFree(minimize(determinize(mmnfa)));

	}

	public boolean isSuffixFree(SimpleDFA dfa) {
		if (isNonReturning(dfa))
			return isPrefixFree(reverse(dfa));
		return false;
	}

	private boolean isNonReturning(SimpleDFA dfa) {
		for (int state = 0; state < dfa.getNumberOfStates(); state++) {
			for (int symbol = 0; symbol < dfa.getAlphabetSize(); symbol++) {
				if (dfa.applySingleInput(state, symbol) == 0)
					return false;
			}
		}
		return true;
	}

	private boolean isNonExiting(SimpleDFA dfa) {
		int uniqueFinalStateIndex = -1;
		for (int state = 0; state < dfa.getFinalityArray().length; state++) {
			if (dfa.getFinalityArray()[state] && uniqueFinalStateIndex == -1) {
				uniqueFinalStateIndex = state;
			} else if (dfa.getFinalityArray()[state] && uniqueFinalStateIndex != -1) {
				return false;
			}
		}

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

	private int[] getNonExitingFinalSinkState(SimpleDFA dfa) {
		int uniqueFinalStateIndex = -1;
		for (int state = 0; state < dfa.getFinalityArray().length; state++) {
			if (dfa.getFinalityArray()[state] && uniqueFinalStateIndex == -1) {
				uniqueFinalStateIndex = state;
			} else if (dfa.getFinalityArray()[state] && uniqueFinalStateIndex != -1) {
				return new int[0];
			}
		}

		Stack<Integer> nStatesOfFinal = getSubsequentNeighbourStates(dfa, uniqueFinalStateIndex);
		int sinkStateIndex = -1;
		if (nStatesOfFinal.size() < dfa.getAlphabetSize())
			return new int[0];
		;

		while (!nStatesOfFinal.empty()) {
			int neigh = nStatesOfFinal.pop().intValue();
			if (sinkStateIndex == -1)
				sinkStateIndex = neigh;
			if (sinkStateIndex != neigh)
				return new int[0];
		}

		if (sinkStateIndex == -1)
			return new int[0];

		for (int symbol = 0; symbol < dfa.getAlphabetSize(); symbol++) {
			if (dfa.getTransitionMatrix()[sinkStateIndex][symbol] != sinkStateIndex)
				return new int[0];
		}
		int[] result = { uniqueFinalStateIndex, sinkStateIndex };
		return result;
	}

	public boolean isFactorFree(SimpleDFA inputDfa) {

		if (!isNonReturning(inputDfa) || !isNonExiting(inputDfa))
			return false;

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

	private boolean isSubword(String u, String v) {
		int i = 0, j = 0;
		while (i < u.length() && j < v.length()) {
			if (u.charAt(i) == v.charAt(j)) {
				i++;
			}
			j++;
		}
		return i == u.length();
	}

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

	// Closedness
	public boolean isPrefixClosed(SimpleDFA dfa) {
		// compl PC is RID
		return isRightIdeal(complement(dfa));
	}

	public boolean isSuffixClosed(SimpleDFA dfa) {
		// compl SC is LID
		return isLeftIdeal(complement(dfa));
	}

	public boolean isFactorClosed(SimpleDFA dfa) {
		// compl FC is 2ID
		return isTwoSidedIdeal(complement(dfa));
	}

	public boolean isSubwordClosed(SimpleDFA dfa) {
		// compl SwC is AllID
		return isAllSidedIdeal(complement(dfa));
	}

	// Ideality
	public boolean isRightIdeal(SimpleDFA dfa) {
		// undead -> is sink, but final

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

	private boolean isRightIdeal(SimpleMNFA mnfa) {
		return isRightIdeal(minimize(determinize(mnfa)));
	}

	public boolean isLeftIdeal(SimpleDFA dfa) {
		return isRightIdeal(reverse(dfa));
	}

	public boolean isTwoSidedIdeal(SimpleDFA dfa) {
		return isRightIdeal(dfa) && isLeftIdeal(dfa);
	}

	public boolean isAllSidedIdeal(SimpleDFA dfa) {
		if (!isTwoSidedIdeal(dfa))
			return false;

		SimpleMNFA mnfa = new SimpleMNFA(dfa);

		// add Sigma loops
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
