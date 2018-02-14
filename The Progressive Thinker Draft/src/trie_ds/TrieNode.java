package trie_ds;

import java.util.HashMap;
import java.util.Map;

public class TrieNode {
	private Map<String, TrieNode> children;
	private String content;
	
	public TrieNode() {
		children = new HashMap<String, TrieNode>();
	}
	
	public TrieNode(String content) {
		children = new HashMap<String, TrieNode>();
		this.setContent(content);
	}
	
	public Map<String, TrieNode> getChildren() {
        return children;
    }
	
	public void setChildren(Map<String, TrieNode> children) {
        this.children = children;
    }

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
