package draft_one;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DataManager {
	private List<String> sentences;
	private List<SemanticRole> semanticRoles;
	private List<Triplet> triplets;
	private List<Triplet> openIETriplets;
	private List<Triplet> corefTriplets;
	private Map<Keyword, List<Triplet>> keywordsMap;
	private Set<Keyword> keywordSet;
	
	private Set<String> stopWords;
	private Set<Triplet> tripletSet;
	
	private NaturalLanguageUnderstandingClient nluClient;
	
	public DataManager() {
		sentences = new ArrayList<String>();
		semanticRoles = new ArrayList<SemanticRole>();
		triplets = new ArrayList<Triplet>();
		openIETriplets = new ArrayList<Triplet>();
		corefTriplets = new ArrayList<Triplet>();
		keywordsMap = new HashMap<Keyword, List<Triplet>>();
		keywordSet = new HashSet<Keyword>();
		
		nluClient = new NaturalLanguageUnderstandingClient();
		
		stopWords = new HashSet<String>();
		stopWords.add("It");
		stopWords.add("He");
		stopWords.add("1");
		stopWords.add("it");
		tripletSet = new HashSet<Triplet>();
	}
	
	public List<SemanticRole> getSemanticRoles() {
		return semanticRoles;
	}
	public void setSemanticRoles(List<SemanticRole> semanticRoles) {
		this.semanticRoles = semanticRoles;
	}
	public List<Triplet> getTriplets() {
		return triplets;
	}
	public void setTriplets(List<Triplet> triplets) {
		this.triplets = triplets;
	}
	public List<Triplet> getOpenIETriplets() {
		return openIETriplets;
	}
	public void setOpenIETriplets(List<Triplet> openIETriplets) {
		this.openIETriplets = openIETriplets;
	}
	public List<Triplet> getCorefTriplets() {
		return corefTriplets;
	}
	public void setCorefTriplets(List<Triplet> corefTriplets) {
		this.corefTriplets = corefTriplets;
	}
	
	public Map<Keyword, List<Triplet>> getKeywordsMap() {
		return keywordsMap;
	}

	public void setKeywordsMap(Map<Keyword, List<Triplet>> keywordsMap) {
		this.keywordsMap = keywordsMap;
	}

	public void gatherTripletsIntoMainSetOfTriplets() {
		triplets.addAll(extractTripletsAndSentenceFromSemanticRoles());
		triplets.addAll(getOpenIETripletsFromSentences());
		triplets.addAll(getCorefTripletsFromSentences());
	}
	
	public void gatherTripletsIntoMainTripletSet() {
		tripletSet.addAll(extractTripletsAndSentenceFromSemanticRoles());
		tripletSet.addAll(getOpenIETripletsFromSentences());
		tripletSet.addAll(getCorefTripletsFromSentences());
	}
	
	private List<Triplet>  extractTripletsAndSentenceFromSemanticRoles() {
		List<Triplet> extractedTriplets = new ArrayList<Triplet>();
		for(SemanticRole semanticRole: semanticRoles) {
			extractedTriplets.add(semanticRole.getTriplet());
			sentences.add(semanticRole.getSentence());
		}
		return extractedTriplets;
	}
	
	private List<Triplet> getOpenIETripletsFromSentences() {
		OpenIEClient openIEClient = new OpenIEClient();
		for(String sentence: sentences) {
			openIETriplets.addAll(openIEClient.annotateSentence(sentence));
		}
		return openIETriplets;
	}
	
	private List<Triplet> getCorefTripletsFromSentences() {
		CorefClient corefClient = new CorefClient();
		for(String sentence: sentences) {
			corefTriplets.addAll(corefClient.annotateSentence(sentence));
		}
		System.out.println("Coref triplets\n");
		printSpecificTriplets(corefTriplets);
		return corefTriplets;
	}
	
	public void addKeyword(Keyword keyword) {
		keywordsMap.putIfAbsent(keyword, new ArrayList<Triplet>());
	}
	
	public void addMultipleKeywords(List<Keyword> keywords) {
		for(Keyword keyword: keywords) {
			addKeyword(keyword);
		}
	}
	
	public List<Triplet> accessKeyword(Keyword keyword) {
		return keywordsMap.get(keyword);
	}
	
	public boolean containsKeyword(Keyword keyword) {
		return keywordsMap.containsKey(keyword);
	}
	
	public void addTripletToKeyword(Keyword keyword, Triplet triplet) {
		accessKeyword(keyword).add(triplet);
	}
	
	public void putTripletIntoKeywordsMap(Keyword keyword, Triplet triplet) {
		if(!containsKeyword(keyword)) {
			addKeyword(keyword);
		}
		
		addTripletToKeyword(keyword, triplet);
	}
	
	public void addKeywordIntoSet(Keyword keyword) {
		keywordSet.add(keyword);
	}
	
	public void enrichTriplets() {
		for(Triplet triplet: tripletSet) {
			System.out.println(String.format("Enriching triplet: \n%s\n", triplet));
			nluClient.enrichRichNode(triplet.getSubjectTry());
			nluClient.enrichRichNode(triplet.getObjectTry());
		}
	}
	
	public void processSemanticTriplesIntoMap() {
		for(SemanticRole semanticRole: semanticRoles) {
			for(Keyword keyword: semanticRole.getKeywordsOfJsonObject("subject")) {
				putTripletIntoKeywordsMap(keyword, semanticRole.getTriplet());
			}
		}
	}
	
	public void processTripletsIntoSet() {
		for(Triplet triplet: tripletSet) {
			keywordSet.addAll(extractKeywordFromTriplet(triplet));
		}
	}
	
	public List<Keyword> extractKeywordFromTriplet(Triplet triplet){
		List<Keyword> listOfKeywords = new ArrayList<Keyword>();
		listOfKeywords.addAll(triplet.getSubjectTry().getKeywords());
		listOfKeywords.addAll(triplet.getObjectTry().getKeywords());
		return listOfKeywords;
	}
	
	public void processTriplesIntoMap() {
		for(Triplet triplet: triplets) {
			for(Keyword keyword: triplet.getSubjectTry().getKeywords()) {
				putTripletIntoKeywordsMap(keyword, triplet);
			}
		}
	}
	
	public void tripletListToSet() {
		tripletSet.addAll(triplets);
	}
	
	public void cleanseTriplets() {
		List<Triplet> toBeDeleted = new ArrayList<Triplet>();
		for(Triplet triplet: tripletSet) {
			if(subjectIsStopWord(triplet)) {
				toBeDeleted.add(triplet);
			}
		}
		tripletSet.removeAll(toBeDeleted);
	}
	
	public boolean subjectIsStopWord(Triplet triplet) {
		return stopWords.contains(triplet.getSubjectTry().getSentence());
	}
	
	public Set<Keyword> getKeywordSet() {
		return keywordSet;
	}

	public void setKeywordSet(Set<Keyword> keywordSet) {
		this.keywordSet = keywordSet;
	}

	public Set<Triplet> getTripletSet() {
		return tripletSet;
	}

	public void setTripletSet(Set<Triplet> tripletSet) {
		this.tripletSet = tripletSet;
	}

	public void printKeywordsMap() {
		String string = "";
		for(Keyword keyword: keywordsMap.keySet()) {
			string += keyword.keyword + "\n";
			for(Triplet triplet: keywordsMap.get(keyword)) {
				string += triplet.toString();
				string += "\n";
			}
			
		}
		System.out.println(String.format("Keywords\t\tTriplets\n"
				+ "%s\n"
				+ "", string));
	}
	
	public void printAllTriplets() {
		String tripletString = "";
		for(Triplet triplet: tripletSet) {
			tripletString += triplet + "\n";
		}
		System.out.println(tripletString);
	}
	
	public void printSpecificTriplets(List<Triplet> listOfTriplets) {
		String tripletString = "";
		for(Triplet triplet: listOfTriplets) {
			tripletString += triplet + "\n";
		}
		System.out.println(tripletString);
	}
	
}
