package trie_ds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WordTrie {
	private TrieNode root;
	
	public WordTrie() {
        root = new TrieNode();
    }
	
	public void insertWords(List<String> words) {
		TrieNode current = root;
		for(String word: words) {
			if(current.getChildren().containsKey(word))
				current = current.getChildren().get(word);
			else {
				current.getChildren().put(word, new TrieNode());
				current = current.getChildren().get(word);
			}
		}
	}
	
	public void processSentence(String sentence, String rootChild) {
		List<String> words = new ArrayList<String>();
		words.add(rootChild);
		words.addAll(Arrays.asList(sentence.replaceAll("[,.]", "").split(" ")));
		insertWords(words);
	}
	
	public void addRoot(String rootChild) {
		root.getChildren().putIfAbsent(rootChild, new TrieNode());
	}
	
	public void buildTrie() {
		
	}
	
	public boolean isPrefix(String sentence, String rootChild) {
		sentence.replaceAll("[.,]", "");
		TrieNode current = root.getChildren().get(rootChild);
		for(String word: sentence.split(" "))
			if(current.getChildren().containsKey(word))
				current = current.getChildren().get(word);
			else {
				return false;
			}
		return true;
	}
	
	public static void main(String[] args) {
		System.out.println("Started!");
		WordTrie trie = new WordTrie();
//		System.out.println(Arrays.toString("Hi,. my name.. is".replaceAll("[.,]", "").split(" ")));
		trie.addRoot("name");
		trie.processSentence("Hi, my name is Ammar", "name");
		trie.processSentence("Hi, my name bla bla", "name");
		
	}

}
