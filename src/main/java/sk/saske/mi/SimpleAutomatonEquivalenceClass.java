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

import java.util.HashSet;

public class SimpleAutomatonEquivalenceClass {

	private int alphabetSize;
	private HashSet<Integer> statesInClass;
	private int[] classTransition;
	private boolean isFinal;

	public SimpleAutomatonEquivalenceClass(int alphabetSize) {
		this.alphabetSize = alphabetSize;

		this.statesInClass = new HashSet<>();
		this.classTransition = new int[alphabetSize];
	}
	
	public SimpleAutomatonEquivalenceClass(int alphabetSize, boolean finality) {
		this.alphabetSize = alphabetSize;

		this.statesInClass = new HashSet<>();
		this.classTransition = new int[alphabetSize];
		this.isFinal = finality;
	}
	
	public SimpleAutomatonEquivalenceClass(int alphabetSize, int state, boolean finality) {
		this.alphabetSize = alphabetSize;

		this.statesInClass = new HashSet<>();
		this.statesInClass.add(state);
		this.isFinal = true;
		this.classTransition = new int[alphabetSize];
		
	}

	public SimpleAutomatonEquivalenceClass(int alphabetSize, int[] characteristicTransitions) {
		this.alphabetSize = alphabetSize;

		this.statesInClass = new HashSet<>();
		this.classTransition = new int[alphabetSize];
		if (alphabetSize != characteristicTransitions.length)
			throw new RuntimeException("Wrong transitions in equavalence class.");
		for (int i = 0; i < characteristicTransitions.length; i++) {
			classTransition[i] = characteristicTransitions[i];
		}
	}
	public SimpleAutomatonEquivalenceClass(int state, int alphabetSize, int[] characteristicTransitions) {
		this.alphabetSize = alphabetSize;

		this.statesInClass = new HashSet<>();
		this.statesInClass.add(state);
		this.classTransition = new int[alphabetSize];
		if (alphabetSize != characteristicTransitions.length)
			throw new RuntimeException("Wrong transitions in equavalence class.");
		for (int i = 0; i < characteristicTransitions.length; i++) {
			this.classTransition[i] = characteristicTransitions[i];
		}
	}
	
	public SimpleAutomatonEquivalenceClass(int state, int alphabetSize, int[] characteristicTransitions, boolean finality) {
		this.alphabetSize = alphabetSize;

		this.statesInClass = new HashSet<>();
		this.statesInClass.add(state);
		this.isFinal = finality;
		this.classTransition = new int[alphabetSize];
		if (alphabetSize != characteristicTransitions.length)
			throw new RuntimeException("Wrong transitions in equavalence class.");
		for (int i = 0; i < characteristicTransitions.length; i++) {
			this.classTransition[i] = characteristicTransitions[i];
		}
	}


	public void addState(int state) {
		statesInClass.add(state);
	}
	
	public void addState(int state, boolean finality) {
		statesInClass.add(state);
		this.isFinal = finality;
	}
	
	public int getAlphabetSize() {
		return alphabetSize;
	}

	public void setAlphabetSize(int alphabetSize) {
		this.alphabetSize = alphabetSize;
	}

	public HashSet<Integer> getStatesInClass() {
		return statesInClass;
	}

	public boolean containsState(int state) {
		return statesInClass.contains(state);
	}

	public void setStatesInClass(HashSet<Integer> statesInClass) {
		this.statesInClass = statesInClass;
	}

	public int[] getPartitionTransition() {
		return classTransition;
	}

	public void setPartitionTransition(int[] partitionTransition) {
		this.classTransition = partitionTransition;
	}

	public int getClassSize() {
		return statesInClass.size();
	}
	
	public boolean isFinal() {
		return isFinal;
	}
	
	public int[] getStatesInClassAsList() {
		int[] result = new int[statesInClass.size()];
		int counter = 0;
		for (Integer i : statesInClass) {
			result[counter] = i.intValue();
			counter++;
		}
		return result;
	}

}
