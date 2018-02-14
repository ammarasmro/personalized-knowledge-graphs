package draft_one;

import java.util.Comparator;

public class ObjectLengthComparator implements Comparator<Triplet> {

	@Override
	public int compare(Triplet o1, Triplet o2) {
		// TODO Auto-generated method stub
		return  o2.getObjectTry().getSentence().length() - o1.getObjectTry().getSentence().length();
	}

}
