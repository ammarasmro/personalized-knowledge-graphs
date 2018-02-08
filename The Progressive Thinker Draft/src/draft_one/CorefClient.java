package draft_one;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.Mention;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class CorefClient {
	
	private Properties props;
	private StanfordCoreNLP pipeline;
	
	private NaturalLanguageUnderstandingClient nluClient;
	
	public CorefClient() {
		props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,mention,coref");
	    props.setProperty("coref.algorithm", "neural");
	    
	    pipeline = new StanfordCoreNLP(props);
	    
	    nluClient = new NaturalLanguageUnderstandingClient();
	}
	
	public List<Triplet> annotateSentence(String highlight) {
		List<Triplet> listOfTriplets = new ArrayList<Triplet>();
		Annotation document = new Annotation(highlight);
		
		System.out.println("Sentence: "+ document.toString());
    	
	    pipeline.annotate(document);
//	    System.out.println("---");
//	    System.out.println("coref chains");
	    for (CorefChain cc : document.get(CorefCoreAnnotations.CorefChainAnnotation.class).values()) {
//	      System.out.println("\t" + cc);
//	      System.out.println("\t" + cc.getMentionMap());
	    }
	    Map<String, List<String>> dependingVerbToMentionsMap = new HashMap<String,List<String>>();
	    String verb = "";
	    for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
//			System.out.println("---");
//			System.out.println("mentions");
			for (Mention m : sentence.get(CorefCoreAnnotations.CorefMentionsAnnotation.class)) {
//		    	  	System.out.println("\t" + m);
		        
//		        System.out.println("dependingVerb\t" + m.dependingVerb);
		        if(m.dependingVerb != null)
		        		verb = m.dependingVerb.word();
		        else verb = "is";
		        dependingVerbToMentionsMap.putIfAbsent(verb, new ArrayList<String>());
		        dependingVerbToMentionsMap.get(verb).add(m.toString());
			}
	    }
	    
	    System.out.println(dependingVerbToMentionsMap);
	    
	    return mapToListOfTriplets(dependingVerbToMentionsMap);
		
	}
	
	public List<Triplet> mapToListOfTriplets(Map<String,List<String>> mapOfTriplets){
		List<Triplet> listOfTriplets = new ArrayList<Triplet>();
		for(Entry<String,List<String>> entry: mapOfTriplets.entrySet()) {
			try {
				listOfTriplets.add(new Triplet(entry.getValue().get(0), entry.getKey(), entry.getValue().get(1)));

			} catch(IndexOutOfBoundsException e) {
				System.out.println(String.format("Entry: %s only has one part\n", entry));
			}
		}
//		enrichTriplets(listOfTriplets);
		return listOfTriplets;
	}
	
	public void enrichTriplets(List<Triplet> listOfTriplets) {
		for(Triplet triplet: listOfTriplets) {
			System.out.println(String.format("Enriching triplet: \n%s\n", triplet));
			nluClient.enrichRichNode(triplet.getSubjectTry());
			nluClient.enrichRichNode(triplet.getObjectTry());
		}
	}

	public static void main(String[] args) {
		String testString = "Obama was born in Hawaii. He is our president.";
		CorefClient corefClient = new CorefClient();
		System.out.println(corefClient.annotateSentence(testString));
		
		
		System.out.println("\n\nDone!");

	}

}
