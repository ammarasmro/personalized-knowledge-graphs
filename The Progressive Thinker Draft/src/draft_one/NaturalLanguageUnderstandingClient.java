package draft_one;

import java.util.ArrayList;
import java.util.List;

import com.ibm.watson.developer_cloud.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalyzeOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.Features;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.KeywordsOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.KeywordsResult;

public class NaturalLanguageUnderstandingClient {
	
	NaturalLanguageUnderstanding service;
	
	CriticalInfo criticalInfo = new CriticalInfo();
	
	KeywordsOptions keywords;
	Features features;
	AnalyzeOptions parameters;
	AnalysisResults response;
	
	
	public NaturalLanguageUnderstandingClient() {
		service = new NaturalLanguageUnderstanding(
				  NaturalLanguageUnderstanding.VERSION_DATE_2017_02_27,
				  criticalInfo.NLU_USERNAME,
				  criticalInfo.NLU_PASSWORD
				);
		
		init();
	}
	
	public void init() {
		keywords= new KeywordsOptions.Builder()
				  .limit(3)
				  .build();
		features = new Features.Builder()
				  .keywords(keywords)
				  .build();
	}
	
	public List<KeywordsResult> getKeywordsResults(String sentence) {
		 parameters = new AnalyzeOptions.Builder()
				 .text(sentence)
				  .features(features)
				  .language("en")
				  .build();
		 response = service
				  .analyze(parameters)
				  .execute();
		 return response.getKeywords();
	}
	
	public List<Keyword> analyzeSentence(String sentence){
		List<Keyword> listOfKeywords = new ArrayList<Keyword>();
		for(KeywordsResult keywordsResult: getKeywordsResults(sentence)) {
			listOfKeywords.add(new Keyword(keywordsResult.getText()));
		}
		return listOfKeywords;
	}
	
	public void enrichRichNode(RichTextNode richTextNode) {
		richTextNode.addAllKeywords(analyzeSentence(richTextNode.getSentence()));
	}
	
	public static void main(String[] args) {
		NaturalLanguageUnderstandingClient testClient = new NaturalLanguageUnderstandingClient();
		System.out.println(testClient.analyzeSentence("Ammar is back"));
	}
}
