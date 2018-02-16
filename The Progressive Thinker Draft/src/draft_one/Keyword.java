package draft_one;

import java.util.ArrayList;
import java.util.List;

public class Keyword {
	String keyword;
	List<Triplet> childrenTriplets;
	List<Category> categories;
	
	public Keyword(String keyword) {
		this.keyword = keyword;
		childrenTriplets = new ArrayList<Triplet>();
		categories = new ArrayList<Category>();
	}
	
	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public List<Triplet> getChildrenTriplets() {
		return childrenTriplets;
	}

	public void setChildrenTriplets(List<Triplet> childrenTriplets) {
		this.childrenTriplets = childrenTriplets;
	}
	
	public void addChildTriplet(Triplet triplet) {
		childrenTriplets.add(triplet);
	}
	
	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

	@Override
	public String toString() {
		return keyword;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((childrenTriplets == null) ? 0 : childrenTriplets.hashCode());
		result = prime * result + ((keyword == null) ? 0 : keyword.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Keyword other = (Keyword) obj;
		if (keyword == null) {
			if (other.keyword != null)
				return false;
		} else if (!keyword.equals(other.keyword))
			return false;
		return true;
	}
	
	

}
