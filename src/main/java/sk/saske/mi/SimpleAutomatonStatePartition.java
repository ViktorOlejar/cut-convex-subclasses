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

import java.util.ArrayList;
import java.util.List;

public class SimpleAutomatonStatePartition {
	
	private List<SimpleAutomatonEquivalenceClass> partition; 
	
	public SimpleAutomatonStatePartition() {
		this.partition = new ArrayList<>();
	}
	
	public int getPartitionOfTransitionForState(int state, int symbol , SimpleDFA automaton) {
		int targetState = automaton.applySingleInput(state, symbol);
		return getPartitionIndexOfState(targetState);
	}
	
	public int getPartitionIndexOfState(int state) {
		for (int i = 0; i < partition.size(); i++) {
			if (partition.get(i).containsState(state)) {
				return i;
			}
		}
		return -1;
	}
	
	public SimpleAutomatonEquivalenceClass getPartitionOfState(int state) {
		int index = getPartitionIndexOfState(state);
		return partition.get(index);
	}
	
	public int getPartitionSize() {
		return partition.size();
	}
	
	public int getInitPartitionNumber () {
		return getPartitionIndexOfState(0);
	}
	
	public List<SimpleAutomatonEquivalenceClass> getEquivalenceClassesOfPartition() {
		return partition;
	}

	public SimpleAutomatonEquivalenceClass getEquivalenceClassInPartition(int index) {
		return partition.get(index);
	}
	
	public void addNewClass(int state, int[] characteristicTransitions) {
		SimpleAutomatonEquivalenceClass newClass = new SimpleAutomatonEquivalenceClass(state, characteristicTransitions.length, characteristicTransitions);;
		partition.add(newClass);
	}
	
	public void addNewClass(int alphabetSize) {
		SimpleAutomatonEquivalenceClass newClass = new SimpleAutomatonEquivalenceClass(alphabetSize);
		partition.add(newClass);
	}
	
	public void addNewClass(int alphabetSize, int state, boolean finality) {
		SimpleAutomatonEquivalenceClass newClass = new SimpleAutomatonEquivalenceClass(alphabetSize, state, finality);
		partition.add(newClass);
	}
	
	public void addNewClass(int state, int alphabetSize, int[] characteristicTransitions, boolean finality) {
		SimpleAutomatonEquivalenceClass newClass = new SimpleAutomatonEquivalenceClass(state, alphabetSize, characteristicTransitions, finality);
		partition.add(newClass);
	}
	
	public void addNewClass(int alphabetSize, boolean finality) {
		SimpleAutomatonEquivalenceClass newClass = new SimpleAutomatonEquivalenceClass(alphabetSize, finality);
		partition.add(newClass);
	}
	
	public int containsStateClassTransition(int state, int[] scTransition, SimpleAutomatonEquivalenceClass originClass) {
		for (SimpleAutomatonEquivalenceClass cl: partition) {
			boolean found = false;
			for (int i = 0; i < cl.getPartitionTransition().length; i++) {
				if(cl.getPartitionTransition()[i] != scTransition[i])
					break;
				if(cl.getPartitionTransition().length - 1 == i)
					found = true;
			}
			if(found) {
				int[] statesInClass = cl.getStatesInClassAsList();
				if (originClass.containsState(statesInClass[0]))
					return partition.indexOf(cl);
			}
				
		}
		return -1;
	}
	
}
