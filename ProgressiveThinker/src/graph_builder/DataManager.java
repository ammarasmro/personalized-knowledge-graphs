package graph_builder;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import trie_ds.WordTrie;

import static java.nio.file.StandardOpenOption.*;
import java.io.*;


public class DataManager {
	private List<String> sentences;
	private List<SemanticRole> semanticRoles;
	private List<Triplet> triplets;
	private List<Triplet> semanticRolesTriplets;
	private List<Triplet> openIETriplets;
	private List<Triplet> corefTriplets;
	private List<Triplet> wordToWordTriplets;
	private Map<Keyword, List<Triplet>> keywordsMap;
	private Set<Keyword> keywordSet;
	
	private Set<String> stopWords;
	private Set<Triplet> tripletSet;
	
	private NaturalLanguageUnderstandingClient nluClient;
	
	public DataManager() {
		sentences = new ArrayList<String>();
		semanticRoles = new ArrayList<SemanticRole>();
		triplets = new ArrayList<Triplet>();
		semanticRolesTriplets = new ArrayList<Triplet>();
		openIETriplets = new ArrayList<Triplet>();
		corefTriplets = new ArrayList<Triplet>();
		wordToWordTriplets = new ArrayList<Triplet>();
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
	
	public List<Triplet> getWordToWordTriplets() {
		return wordToWordTriplets;
	}

	public void setWordToWordTriplets(List<Triplet> wordToWordTriplets) {
		this.wordToWordTriplets = wordToWordTriplets;
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
		semanticRolesTriplets.addAll(extractTripletsAndSentenceFromSemanticRoles());
		openIETriplets.addAll(getOpenIETripletsFromSentences());
		enrichTriplets(openIETriplets);
		tripletSet.addAll(openIETriplets);
		corefTriplets.addAll(getCorefTripletsFromSentences());
		enrichTriplets(corefTriplets);
		tripletSet.addAll(corefTriplets);
	}
	
	public void extractWordToWordTriplesFromMainSet() {
		int subjectLength;
		int objectLength;
		for(Triplet triplet: tripletSet) {
			subjectLength = triplet.getSubjectTry().getSentence().split(" ").length;
			objectLength = triplet.getObjectTry().getSentence().split(" ").length;
			if(subjectLength == 1 &&  objectLength == 1) 
				wordToWordTriplets.add(triplet);
		}
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
	
	public void enrichKeywords() {
		nluClient.initializeForCategoriesExtraction();
		for(Keyword keyword: keywordSet) {
			nluClient.enrichKeyword(keyword);
		}
	}
	
	public void enrichTriplets(Collection<Triplet> collectionOfTriplets) {
		nluClient.initializeForKeywordsExtraction();
		for(Triplet triplet: collectionOfTriplets) {
			System.out.println(String.format("Enriching triplet: \n%s\n", triplet));
			try {
			nluClient.enrichRichNode(triplet.getSubjectTry());
			nluClient.enrichRichNode(triplet.getObjectTry());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void processSemanticTriplesIntoMap() {
		for(SemanticRole semanticRole: semanticRoles) {
			for(Keyword keyword: semanticRole.getKeywordsOfJsonObject("subject")) {
				putTripletIntoKeywordsMap(keyword, semanticRole.getTriplet());
			}
		}
	}
	
	public void extractKeywordsIntoKeywordsSet() {
		for(Triplet triplet: tripletSet) {
			keywordSet.addAll(extractKeywordsFromTriplet(triplet));
		}
	}
	
	public List<Keyword> extractKeywordsFromTriplet(Triplet triplet){
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
		toBeDeleted.addAll(processTripletsToTrie());
		tripletSet.removeAll(toBeDeleted);
	}
	
	public void cleanseShortTriples() {
		List<Triplet> toBeDeleted = new ArrayList<Triplet>();
		for(Triplet triplet: tripletSet) {
			if(tripletIsTooShort(triplet)) {
				toBeDeleted.add(triplet);
			}
		}
		tripletSet.removeAll(toBeDeleted);
	}
	
	public List<Triplet> processTripletsToTrie() {
		List<Triplet> tripletListToBeSorted = new ArrayList<Triplet>(tripletSet);
		List<Triplet> toBeDeleted = new ArrayList<Triplet>();
		Collections.sort(tripletListToBeSorted, new ObjectLengthComparator());
		Collections.sort(tripletListToBeSorted, new SubjectStringComparator());
		WordTrie trie = new WordTrie();
		pushSubjectsIntoTrie(trie, tripletListToBeSorted);
		for(Triplet triplet: tripletListToBeSorted) {
			if(trie.isPrefix(triplet.getObjectTry().getSentence(), triplet.getSubjectTry().getSentence()))
				toBeDeleted.add(triplet);
			else
				trie.processSentence(triplet.getObjectTry().getSentence(), triplet.getSubjectTry().getSentence());
		}
		return toBeDeleted;
	}
	
	public void pushSubjectsIntoTrie(WordTrie trie, List<Triplet> listOfTriplets) {
		for(Triplet triplet: listOfTriplets) {
			trie.addRoot(triplet.getSubjectTry().getSentence());
		}
	}
	
	public boolean tripletIsTooShort(Triplet triplet) {
		int subjectLength = triplet.getSubjectTry().getSentence().split(" ").length;
		int objectLength = triplet.getObjectTry().getSentence().split(" ").length;
		if(subjectLength + objectLength < 4) 
			return true;
		return false;
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
	
	public void logTriplets() {
		writeContentToFile(prepareTripletListForLogging(semanticRolesTriplets), "SemanticRoles");
		writeContentToFile(prepareTripletListForLogging(openIETriplets), "OpenIETriplets");
		writeContentToFile(prepareTripletListForLogging(corefTriplets), "CorefTriplets");
		writeContentToFile(prepareTripletListForLogging(tripletSet), "Triplets");
	}
	
	public String prepareTripletListForLogging(Collection<Triplet> listOfTriples) {
		StringBuilder sb = new StringBuilder();
		for(Triplet triplet: listOfTriples) {
			sb.append(triplet.toString());
		}
		return sb.toString();
	}
	
	public void writeContentToFile(String content, String filename) {
		
		byte data[] = content.getBytes();
		Path file = Paths.get(filename + ".txt");
		try (OutputStream out = new BufferedOutputStream(
			      Files.newOutputStream(file, CREATE, APPEND))) {
			      out.write(data, 0, data.length);
			    } catch (IOException x) {
			      System.err.println(x);
			    }
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
