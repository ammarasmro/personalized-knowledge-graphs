package draft_one;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.watson.developer_cloud.discovery.v1.Discovery;
import com.ibm.watson.developer_cloud.discovery.v1.model.QueryOptions;
import com.ibm.watson.developer_cloud.discovery.v1.model.QueryResponse;
import com.ibm.watson.developer_cloud.discovery.v1.model.QueryResult;

public class DiscoveryClient {

	Discovery discovery = new Discovery("2017-11-07");
	
	CriticalInfo criticalInfo = new CriticalInfo();;
	
	String environmentId;
	String collectionId;
	
	public DiscoveryClient() {
		
		discovery.setEndPoint("https://gateway.watsonplatform.net/discovery/api/");
		
		discovery.setUsernameAndPassword(criticalInfo.USERNAME, criticalInfo.PASSWORD);
		
		environmentId = criticalInfo.ENVIRONMENT_ID;
		collectionId = criticalInfo.COLLECTION_ID;
	}
	
	public JsonArray queryDispatcher(String query) {
		QueryOptions.Builder queryBuilder = new QueryOptions.Builder(environmentId, collectionId);
		queryBuilder.query(query);
		QueryResponse queryResponse = discovery.query(queryBuilder.build()).execute();
		List<QueryResult> results = queryResponse.getResults();
	    String jsonRes = new Gson().toJson(results);
	    JsonElement jelement = new JsonParser().parse(jsonRes);
	    JsonArray jarray = jelement.getAsJsonArray();
        
//	    System.out.println(jarray.get(0).getAsJsonObject().get("enriched_text").getAsJsonObject().get("semantic_roles").getAsJsonArray().toString());
//        System.out.println(jarray.get(0).getAsJsonObject().get("enriched_text").getAsJsonObject().get("semantic_roles").getAsJsonArray().get(0).getAsJsonObject().get("subject").toString());
        return jarray;
	}
	
	public List<DiscoveryDocument> extractDocuments(JsonArray jarray) {
		List<DiscoveryDocument> listOfDocuments = new ArrayList<DiscoveryDocument>();
		for(int i = 0; i < 3; i++) {
			listOfDocuments.add(new DiscoveryDocument(jarray.get(i).getAsJsonObject()));
		}
		
		return listOfDocuments;
	}
	
	public String prettyPrintSemanticRoles(List<JsonObject> jsonObjects) {
		String temp = "";
		for(JsonObject jsonObject: jsonObjects) {
			temp = temp + jsonObject.toString() + "\n";
		}
		return temp;
	}
	
	public String prettyPrintSemanticRolesDocuments(List<SemanticRole> listOfDocuments) {
		String temp = "";
		for(SemanticRole discoveryDocument: listOfDocuments) {
			temp += discoveryDocument.prettyPrintDocument();
		}
		
		return temp;
	}
		
	public static void main(String[] args) {
//		DiscoveryClient tester = new DiscoveryClient();
//		JsonArray jarray = tester.queryDispatcher("car design");
//		tester.queryDispatcher("natural_language_query:car design");
		
		
		
//		List<JsonArray> jsonArrays = tester.extractSemanticRoles(jarray);
//		
//		List<JsonObject> jsonObjects = tester.extractSingleSemanticRoles(jsonArrays);
//		
//		System.out.println(tester.prettyPrintSemanticRoles(jsonObjects));
//		
//		List<SemanticRole> listOfDocuments = tester.putSemanticRolesIntoDocuments(jsonObjects);
//		
//		System.out.println(tester.prettyPrintSemanticRolesDocuments(listOfDocuments));
	}
}
