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
import java.util.List;

/**
 * 
 * Class for implementing state partitions for Hopcroft minimization.
 *
 */

public class SimpleAutomatonStatePartition {

	// variable for saving the partition of states
	private List<SimpleAutomatonEquivalenceClass> partition;

	/**
	 * Constructor for state partition implementation.
	 */
	public SimpleAutomatonStatePartition() {
		this.partition = new ArrayList<>();
	}

	/**
	 * Returns the equivalence class index of the target state, for which the given
	 * input state transitions on the given input symbol for a given automaton.
	 * 
	 * @param state
	 * @param symbol
	 * @param automaton
	 * @return int - index of equivalence class in this partition
	 */
	public int getClassOfTransitionForState(int state, int symbol, SimpleDFA automaton) {
		int targetState = automaton.applySingleInput(state, symbol);
		return getClassIndexOfState(targetState);
	}

	/**
	 * Returns the equivalence class index in this partition to which the given
	 * state belongs.
	 * 
	 * @param state
	 * @return int - returns -1 if the state index is not present in partition
	 */
	public int getClassIndexOfState(int state) {
		for (int i = 0; i < partition.size(); i++)
			if (partition.get(i).containsState(state))
				return i;

		return -1;
	}

	/**
	 * Returns the equivalence class object in this partition to which the given
	 * state belongs.
	 * 
	 * @param state
	 * @return SimpleAutomatonEquivalenceClass
	 */
	public SimpleAutomatonEquivalenceClass getClassOfState(int state) {
		int index = getClassIndexOfState(state);
		return partition.get(index);
	}

	/**
	 * Returns the number of equivalence classes in partition.
	 * 
	 * @return int
	 */
	public int getPartitionSize() {
		return partition.size();
	}

	/**
	 * Returns the equivalence class index with the initial state
	 * 
	 * @return int
	 */
	public int getInitClassNumber() {
		return getClassIndexOfState(0);
	}

	public List<SimpleAutomatonEquivalenceClass> getEquivalenceClassesOfPartition() {
		return partition;
	}

	/**
	 * Returns the equivalence class in the partition list with the given index.
	 * 
	 * @param index
	 * @return SimpleAutomatonEquivalenceClass
	 */
	public SimpleAutomatonEquivalenceClass getEquivalenceClassInPartition(int index) {
		return partition.get(index);
	}

	/**
	 * Add new equivalence class to partition based on given parameters.
	 * 
	 * @param alphabetSize - size of Sigma
	 * @param finality     - whether the class contains final states
	 */
	public void addNewClass(int alphabetSize, boolean finality) {
		SimpleAutomatonEquivalenceClass newClass = new SimpleAutomatonEquivalenceClass(alphabetSize, finality);
		partition.add(newClass);
	}

	/**
	 * Add new equivalence class to partition based on given parameters.
	 * 
	 * @param state                     - representing state in class
	 * @param alphabetSize              - size of Sigma
	 * @param characteristicTransitions - transitions of class
	 * @param finality                  - whether the class contains final states
	 */
	public void addNewClass(int state, int alphabetSize, int[] characteristicTransitions, boolean finality) {
		SimpleAutomatonEquivalenceClass newClass = new SimpleAutomatonEquivalenceClass(state, alphabetSize,
				characteristicTransitions, finality);
		partition.add(newClass);
	}

	public int containsStateClassTransition(int[] scTransition, SimpleAutomatonEquivalenceClass originClass) {
		for (SimpleAutomatonEquivalenceClass cl : partition) {
			boolean found = false;
			for (int i = 0; i < cl.getPartitionTransition().length; i++) {
				if (cl.getPartitionTransition()[i] != scTransition[i])
					break;
				if (cl.getPartitionTransition().length - 1 == i)
					found = true;
			}
			if (found) {
				int[] statesInClass = cl.getStatesInClassAsList();
				if (originClass.containsState(statesInClass[0]))
					return partition.indexOf(cl);
			}
		}
		return -1;
	}

}
