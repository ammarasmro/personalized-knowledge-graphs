package draft_one;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
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
	
	boolean useMockData = false;
	
	public DiscoveryClient() {
		
		discovery.setEndPoint("https://gateway.watsonplatform.net/discovery/api/");
		
		discovery.setUsernameAndPassword(criticalInfo.USERNAME, criticalInfo.PASSWORD);
		
		environmentId = criticalInfo.ENVIRONMENT_ID;
		collectionId = criticalInfo.COLLECTION_ID;
	}
	
	public JsonArray queryDispatcher(String query) {
		String jsonRes = "";
		if(useMockData) {
			jsonRes = getMockDataFromFile();
		} else {
			QueryOptions.Builder queryBuilder = new QueryOptions.Builder(environmentId, collectionId);
			queryBuilder.query(query);
			QueryResponse queryResponse = discovery.query(queryBuilder.build()).execute();
			List<QueryResult> results = queryResponse.getResults();
			
		    jsonRes = new Gson().toJson(results);
		    writeMockDataToFile(jsonRes);
		}
	    
		JsonElement jelement = new JsonParser().parse(jsonRes);
	    JsonArray jarray = jelement.getAsJsonArray();
        return jarray;
	}
	
	public String getMockDataFromFile() {
		String jsonRes = "";
		Path file = Paths.get("the-file-name.txt");
		try {
			StringBuilder sb = new StringBuilder();
			for(String line: Files.readAllLines(file, Charset.forName("UTF-8")))
				sb.append(line);
			jsonRes = sb.toString();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return jsonRes;
	}
	
	public void writeMockDataToFile(String jsonRes) {
		Path file = Paths.get("howToDesignACar.txt");
	    List<String> temp = Arrays.asList(jsonRes);
	    try {
			Files.write(file, temp, Charset.forName("UTF-8"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
