package draft_one;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class DiscoveryDocument {
	
	private List<Keyword> keywords;
	private List<String> categories;
	private List<Concept> concepts;
	private List<SemanticRole> semanticRoles;
	
	public DiscoveryDocument(JsonObject jsonObject) {
		processJsonObjectToSemanticRoles(jsonObject);
		processJsonObjectToConcepts(jsonObject);
		processJsonObjectToKeywords(jsonObject);
		processJsonObjectToCategories(jsonObject);
	}
	
	private void processJsonObjectToCategories(JsonObject jsonObject) {
		JsonArray arrayOfCategories = extractCategories(jsonObject);
		List<JsonObject> listOfSingleCategories = extractSingleCategories(arrayOfCategories);
		setCategories(putCategoriesIntoDocuments(listOfSingleCategories));
	}

	private JsonArray extractCategories(JsonObject jsonObject) {
 		return jsonObject.get("enriched_text").getAsJsonObject().get("categories").getAsJsonArray();
	}
	
	private List<JsonObject> extractSingleCategories(JsonArray jsonArray) {
		List<JsonObject> jsonObjects = new ArrayList<JsonObject>();
		for(int i = 0; i < 3; i++) {
			jsonObjects.add(jsonArray.get(i).getAsJsonObject());
		}
		
		return jsonObjects;
	}
	
	private List<String> putCategoriesIntoDocuments(List<JsonObject> jsonObjects) {
		List<String> listOfCategories = new ArrayList<String>();
		for(JsonObject jsonObject: jsonObjects) {
			listOfCategories.add(jsonObject.get("label").toString());
		}
		
		return listOfCategories;
	}
	
	private void processJsonObjectToKeywords(JsonObject jsonObject) {
		JsonArray arrayOfKeywords = extractKeywords(jsonObject);
		List<JsonObject> listOfSingleKeywords = extractSingleKeywords(arrayOfKeywords);
		setKeywords(putKeywordsIntoDocuments(listOfSingleKeywords));
	}

	private JsonArray extractKeywords(JsonObject jsonObject) {
 		return jsonObject.get("enriched_text").getAsJsonObject().get("keywords").getAsJsonArray();
	}
	
	private List<JsonObject> extractSingleKeywords(JsonArray jsonArray) {
		List<JsonObject> jsonObjects = new ArrayList<JsonObject>();
		for(int i = 0; i < 10; i++) {
			jsonObjects.add(jsonArray.get(i).getAsJsonObject());
		}
		
		return jsonObjects;
	}
	
	private List<Keyword> putKeywordsIntoDocuments(List<JsonObject> jsonObjects) {
		List<Keyword> listOfKeywords = new ArrayList<Keyword>();
		for(JsonObject jsonObject: jsonObjects) {
			listOfKeywords.add(new Keyword(jsonObject.get("text").toString()));
		}
		
		return listOfKeywords;
	}

	private void processJsonObjectToConcepts(JsonObject jsonObject) {
		JsonArray arrayOfConcepts = extractConcepts(jsonObject);
		List<JsonObject> listOfSingleConcepts = extractSingleConcepts(arrayOfConcepts);
		setConcepts(putConceptsIntoDocuments(listOfSingleConcepts));
	}

	public JsonArray extractConcepts(JsonObject jObject) {
 		return jObject.get("enriched_text").getAsJsonObject().get("concepts").getAsJsonArray();
	}
	
	private List<JsonObject> extractSingleConcepts(JsonArray jsonArray) {
		List<JsonObject> jsonObjects = new ArrayList<JsonObject>();
		for(int i = 0; i < 3; i++) {
			jsonObjects.add(jsonArray.get(i).getAsJsonObject());
		}
		
		return jsonObjects;
	}

	private List<Concept> putConceptsIntoDocuments(List<JsonObject> jsonObjects) {
		List<Concept> listOfConcepts = new ArrayList<Concept>();
		for(JsonObject jsonObject: jsonObjects) {
			listOfConcepts.add(new Concept(jsonObject));
		}
		
		return listOfConcepts;
	}

	private void processJsonObjectToSemanticRoles(JsonObject jsonObject) {
		JsonArray arrayOfSemanticRoles = extractSemanticRoles(jsonObject);
		List<JsonObject> listOfSingleSemanticRoles = extractSingleSemanticRoles(arrayOfSemanticRoles);
		setSemanticRoles(putSemanticRolesIntoDocuments(listOfSingleSemanticRoles));
	}
	
	public JsonArray extractSemanticRoles(JsonObject jObject) {
 		return jObject.get("enriched_text").getAsJsonObject().get("semantic_roles").getAsJsonArray();
	}
	
	public List<SemanticRole> putSemanticRolesIntoDocuments(List<JsonObject> jsonObjects) {
		List<SemanticRole> listOfSemanticRoles = new ArrayList<SemanticRole>();
		for(JsonObject jsonObject: jsonObjects) {
			listOfSemanticRoles.add(new SemanticRole(jsonObject));
		}
		
		return listOfSemanticRoles;
	}
	
	public List<JsonObject> extractSingleSemanticRoles(JsonArray jsonArray) {
		List<JsonObject> jsonObjects = new ArrayList<JsonObject>();
		for(int i = 0; i < 3; i++) {
			jsonObjects.add(jsonArray.get(i).getAsJsonObject());
		}
		
		return jsonObjects;
	}

	public List<Keyword> getKeywords() {
		return keywords;
	}

	public void setKeywords(List<Keyword> keywords) {
		this.keywords = keywords;
	}

	public List<String> getCategories() {
		return categories;
	}

	public void setCategories(List<String> categories) {
		this.categories = categories;
	}

	public List<Concept> getConcepts() {
		return concepts;
	}

	public void setConcepts(List<Concept> concepts) {
		this.concepts = concepts;
	}

	public List<SemanticRole> getSemanticRoles() {
		return semanticRoles;
	}

	public void setSemanticRoles(List<SemanticRole> semanticRoles) {
		this.semanticRoles = semanticRoles;
	}
	
	@Override
	public String toString() {
		String objectsOfRoles = "";
		String semanticRolesTriplets = "";
		for(SemanticRole sem: semanticRoles) {
			objectsOfRoles += sem.getKeywordsOfObject() + "\t";
			semanticRolesTriplets += sem.getTriplet() + "\n";
		}
		return String.format("Semantic Roles: \n"
				+ "%s\n"
				+ "Objects from semantic roles: \n"
				+ "%s\n"
				+ "Keywords: \n"
				+ "%s\n"
				+ "Concepts: \n"
				+ "%s\n"
				+ "Categories: \n"
				+ "%s\n", semanticRolesTriplets, objectsOfRoles, keywords.toString(), concepts.toString(), categories.toString());
	}

	public static void main(String[] args) {

	}

}
