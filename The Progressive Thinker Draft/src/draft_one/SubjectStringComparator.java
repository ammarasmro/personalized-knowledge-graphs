package draft_one;

import java.util.Comparator;

public class SubjectStringComparator implements Comparator<Triplet>{

	@Override
	public int compare(Triplet o1, Triplet o2) {
		// TODO Auto-generated method stub
		return o1.getSubjectTry().getSentence().compareTo(o2.getSubjectTry().getSentence());
	}

}
