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
