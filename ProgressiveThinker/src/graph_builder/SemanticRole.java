package graph_builder;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class SemanticRole {
	private String sentence;
	private String subject;
	private String verb;
	private String object;
	private Triplet triplet;
	private JsonObject jsonObject;
	
	private RichTextNode richSubject;
	private RichTextNode richObject;
	
	public SemanticRole(JsonObject jsonObject) {
		this.jsonObject = jsonObject;
		processJsonElementToTriplet(jsonObject);
	}
	
	public SemanticRole(String sentence, String subject, String verb, String object) {
		
		this.sentence = sentence;
		this.subject = subject;
		this.verb = verb;
		this.object = object;
	}
	
	public void processJsonElementToTriplet(JsonObject jsonObject) {
		sentence = jsonObject.get("sentence").getAsString();
		
		subject = jsonObject.get("subject").getAsJsonObject().get("text").getAsString();
		verb = jsonObject.get("action").getAsJsonObject().get("text").getAsString();
		object = jsonObject.get("object").getAsJsonObject().get("text").getAsString();
//		triplet = new Triplet(subject, verb, object);
		
		richSubject = new RichTextNode(subject);
		richSubject.addAllKeywords(getKeywordsOfJsonObject("subject"));
		
		richObject = new RichTextNode(object);
		richObject.addAllKeywords(getKeywordsOfJsonObject("object"));
		triplet = new Triplet(richSubject, verb, richObject);
	}
	
	public List<String> getKeywordsOfObject(){
		List<String> keywordsWithTheObject = new ArrayList<String>();
		if(jsonObject.get("object").getAsJsonObject().has("keywords")) {
			JsonArray keywordsJsonArray =  jsonObject.get("object").getAsJsonObject().get("keywords").getAsJsonArray();
			for(JsonElement keyword: keywordsJsonArray) {
				keywordsWithTheObject.add(keyword.getAsJsonObject().get("text").toString());
			}
		}
		return keywordsWithTheObject;
	}
	
	public List<Keyword> getKeywordsOfJsonObject(String posToBeProcessed){
		List<Keyword> keywordsWithTheObject = new ArrayList<Keyword>();
		if(jsonObject.get(posToBeProcessed).getAsJsonObject().has("keywords")) {
			JsonArray keywordsJsonArray =  jsonObject.get(posToBeProcessed).getAsJsonObject().get("keywords").getAsJsonArray();
			for(JsonElement keyword: keywordsJsonArray) {
				keywordsWithTheObject.add(new Keyword(keyword.getAsJsonObject().get("text").toString()));
			}
		}
		return keywordsWithTheObject;
	}
	
	public String getSentence() {
		return sentence;
	}
	
	public String getSubject() {
		return triplet.getSubject();
	}
	
	public String getVerb() {
		return triplet.getVerb();
	}
	
	public String getObject() {
		return triplet.getObject();
	}
	
	public Triplet getTriplet() {
		return triplet;
	}

	public void setTriplet(Triplet triplet) {
		this.triplet = triplet;
	}

	public String prettyPrintDocument() {
		return String.format("Sentence: %s\nSubject: %s\nVerb: %s\nObject: %s\n", sentence, subject, verb, object);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
