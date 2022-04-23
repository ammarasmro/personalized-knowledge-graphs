package graph_builder;

import java.util.HashSet;
import java.util.List;

public class RichTextNode {
	String sentence;
	HashSet<Keyword> keywords;
	
	public RichTextNode(String sentence) {
		this.sentence = sentence;
		keywords = new HashSet<Keyword>();
	}
	
	public String getSentence() {
		return sentence;
	}

	public void setSentence(String sentence) {
		this.sentence = sentence;
	}

	public HashSet<Keyword> getKeywords() {
		return keywords;
	}

	public void setKeywords(HashSet<Keyword> keywords) {
		this.keywords = keywords;
	}
	
	public void addKeyword(Keyword keyword) {
		keywords.add(keyword);
	}
	
	public void addAllKeywords(List<Keyword> keywordsList) {
		keywords.addAll(keywordsList);
	}
	
//	public List<Keyword> getListOfKeywords(){
//		keywords.toArray();
//	}
	
	@Override
	public String toString() {
		return sentence;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sentence == null) ? 0 : sentence.hashCode());
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
		RichTextNode other = (RichTextNode) obj;
		if (sentence == null) {
			if (other.sentence != null)
				return false;
		} else if (!sentence.equals(other.sentence))
			return false;
		return true;
	}
	
	
}
