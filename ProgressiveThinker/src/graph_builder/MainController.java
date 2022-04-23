package graph_builder;

import java.util.List;

import com.google.gson.JsonArray;

public class MainController {
	

	public static void main(String[] args) {
		DiscoveryClient discoveryClient = new DiscoveryClient();
		
		GraphDispatcher graphDispatcher = new GraphDispatcher( "bolt://localhost:7687", "ammar", "ammar" );

		JsonArray jarray = discoveryClient.queryDispatcher("car design");
//		System.out.println(jarray);
		DataManager dataManager = new DataManager();
		List<DiscoveryDocument> listOfDiscoveryDocuments = discoveryClient.extractDocuments(jarray);
		for(DiscoveryDocument document: listOfDiscoveryDocuments) {
			System.out.println(document);
			dataManager.getSemanticRoles().addAll(document.getSemanticRoles());
			dataManager.addMultipleKeywords(document.getKeywords());
		}
//		dataManager.gatherTripletsIntoMainSetOfTriplets();
		dataManager.gatherTripletsIntoMainTripletSet();
		String tripletsString = "";
		
		
//		dataManager.processSemanticTriplesIntoMap();
//		dataManager.printKeywordsMap();
		
//		dataManager.tripletListToSet();
		
//		dataManager.logTriplets();
		
		
		
		dataManager.cleanseTriplets();
		
		dataManager.extractWordToWordTriplesFromMainSet();
		
		dataManager.extractKeywordsIntoKeywordsSet();
		
		dataManager.cleanseShortTriples();
		
		
		dataManager.logTriplets();
		
//		dataManager.enrichTriplets();
		
		for(Triplet triplet: dataManager.getTripletSet()) {
			tripletsString += triplet + "\n";
		}
		System.out.println(String.format("Triplets from DataManager: \n"
				+ "%s\n", tripletsString));
		
		for(Triplet triplet: dataManager.getTripletSet()) {
			graphDispatcher.addRichTriplet(triplet);
		}
		
//		for(Keyword keyword: dataManager.getKeywordsMap().keySet()) {
//			graphDispatcher.addKeyword(keyword, "Design");
//			for(Triplet triplet: dataManager.getKeywordsMap().get(keyword)) {
////				graphDispatcher.addRichTriplet(triplet);
//				graphDispatcher.linkListOfKeywordsToSubject(triplet.getSubjectTry().getSentence(), triplet.getSubjectTry().getKeywords());
//				graphDispatcher.linkListOfKeywordsToObject(triplet.getObjectTry().getSentence(), triplet.getObjectTry().getKeywords());
//			}
//		}
		
		
		for(Keyword keyword: dataManager.getKeywordSet()) {
			graphDispatcher.addKeyword(keyword, "Design");
		}
		
		dataManager.enrichKeywords();
		
		for(Keyword keyword: dataManager.getKeywordSet()) {
			
			for(Category category: keyword.getCategories()) {
				String previous = "";
				for(String currentLevel: category.getCategoryStructure()) {
					graphDispatcher.addCategoryLevel(currentLevel, previous);
					previous = currentLevel;
				}
				graphDispatcher.linkKeywordToCategory(keyword.getKeyword(), previous);
			}
		}
		
		for(Triplet triplet: dataManager.getWordToWordTriplets()) {
			graphDispatcher.linkKeywordToDefintion(triplet);
		}
		for(Triplet triplet: dataManager.getTripletSet()) {
			graphDispatcher.linkListOfKeywordsToSubject(triplet.getSubjectTry().getSentence(), triplet.getSubjectTry().getKeywords());
			graphDispatcher.linkListOfKeywordsToObject(triplet.getObjectTry().getSentence(), triplet.getObjectTry().getKeywords());
		}
		
		
				
		
	}

}
