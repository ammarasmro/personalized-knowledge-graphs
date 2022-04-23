package graph_builder;

import com.google.gson.JsonObject;

public class Concept {
	private String concept;
	private String linkToDbPedia;
	
	public Concept(JsonObject jsonObject) {
		processJsonObjectIntoConcept(jsonObject);
	}
	
	public Concept(String concept, String linkToDbPedia) {
		this.setConcept(concept);
		this.setLinkToDbPedia(linkToDbPedia);
	}

	public String getConcept() {
		return concept;
	}

	public void setConcept(String concept) {
		this.concept = concept;
	}

	public String getLinkToDbPedia() {
		return linkToDbPedia;
	}

	public void setLinkToDbPedia(String linkToDbPedia) {
		this.linkToDbPedia = linkToDbPedia;
	}
	
	public void processJsonObjectIntoConcept(JsonObject jsonObject) {
		concept = jsonObject.get("text").toString();
		linkToDbPedia = jsonObject.get("dbpedia_resource").toString();
	}
	
	@Override
	public String toString() {
		return String.format("[%s] -> %s", concept, linkToDbPedia);
	}

}
