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

import java.util.Comparator;

public class PartitionComparator implements Comparator<SimpleAutomatonStatePartition> {

	private boolean sameContentFlag = true;
	
	@Override
	public int compare(SimpleAutomatonStatePartition arg0, SimpleAutomatonStatePartition arg1) {
		sameContentFlag = true;
		if (arg0.getPartitionSize() < arg1.getPartitionSize())
			return -1;
		if (arg0.getPartitionSize() > arg1.getPartitionSize())
			return 1;
		
		for (int i = 0; i < arg0.getPartitionSize(); i++) {
			if (arg0.getEquivalenceClassInPartition(i).getClassSize() < arg1.getEquivalenceClassInPartition(i).getClassSize())
				return -1;
			if (arg0.getEquivalenceClassInPartition(i).getClassSize() > arg1.getEquivalenceClassInPartition(i).getClassSize())
				return 1;
			
			for (int j = 0; j < arg0.getEquivalenceClassInPartition(i).getClassSize(); j++) {
				if (!arg0.getEquivalenceClassInPartition(i).getStatesInClass().equals(arg1.getEquivalenceClassInPartition(i).getStatesInClass()))
					sameContentFlag = false;
			}
			
		}
		return 0;
	}
	
	public boolean isSameContent() {
		return sameContentFlag;
	}

}
