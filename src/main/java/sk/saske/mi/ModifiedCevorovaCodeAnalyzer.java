package sk.saske.mi;


public class ModifiedCevorovaCodeAnalyzer {
	private static final int MAX_ALPHABET_SIZE = 26;

	private SimpleDFA automaton;
	private String automatonCode;

	public ModifiedCevorovaCodeAnalyzer(int numOfStates, int alphabetSize) {
		automaton = new SimpleDFA();
		automaton.setAlphabetSize(alphabetSize);
		automaton.setNumberOfStates(numOfStates);
	}

	public void parse(String code) {
		try {
			if (code.length() < 1)
				throw new InvalidCevorovaCodeException("Incomplete automaton code!");

			code = code.trim();

			automatonCode = code;

			String codeSuffix = code.substring(code.length() - automaton.getNumberOfStates());
			String codeTransitions = code.substring(0, code.length() - automaton.getNumberOfStates());

			initializeFinalityArray(codeSuffix);
			initializeTransitionMatrix(codeTransitions);
		} catch (InvalidCevorovaCodeException e) {
			throw e;
		} catch (Exception e) {
			throw new InvalidCevorovaCodeException("Something went wrong, please check input file.");
		}

	}

	private void initializeTransitionMatrix(String codeTransitions) {

		if (automaton.getAlphabetSize() > MAX_ALPHABET_SIZE)
			throw new InvalidCevorovaCodeException("Alphabet size too large (greater than " + MAX_ALPHABET_SIZE + ")!");

		int[][] transitionMatrix = new int[automaton.getNumberOfStates()][automaton.getAlphabetSize()];

		int currState = 0;
		for (int i = 0; i < codeTransitions.length(); i++) {

			if (Character.getNumericValue(codeTransitions.charAt(i)) < 0
					|| Character.getNumericValue(codeTransitions.charAt(i)) >= automaton.getNumberOfStates()) {
				throw new InvalidCevorovaCodeException(
						"Invalid transition code at index " + codeTransitions.charAt(i) + " !");
			} else {
				transitionMatrix[currState][i % automaton.getAlphabetSize()] = Character
						.getNumericValue(codeTransitions.charAt(i));
				if (i % automaton.getAlphabetSize() == automaton.getAlphabetSize() - 1)
					currState++;
			}
		}
		automaton.setTransitionMatrix(transitionMatrix);
	}

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
				throw new InvalidCevorovaCodeException(
						"Invalid state finality sequence! Unknown finality character: " + codeSuffix.charAt(i));
			}
		}
		automaton.setFinalityArray(finalityArray);
	}

	public SimpleDFA getParsedAutomaton() {
		if (automaton.getFinalityArray() == null) {
			throw new RuntimeException("No automaton parsed!!!");
		}
		return automaton;
	}

	public String getParsedAutomatonCode() {
		if (automatonCode == null) {
			throw new RuntimeException("No automaton parsed!!!");
		}
		return automatonCode;
	}

	
}
