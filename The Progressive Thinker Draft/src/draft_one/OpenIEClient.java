package draft_one;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class OpenIEClient {
	private Properties props;
	private StanfordCoreNLP pipeline;
	
	private NaturalLanguageUnderstandingClient nluClient;
	
	public OpenIEClient() {
		props = new Properties();
	    props.setProperty("annotators", "tokenize,ssplit,pos,lemma,depparse,natlog,openie,ner,parse,dcoref,mention,coref");
	    props.setProperty("openie.triple.all_nominals", "true");
	    
	    pipeline = new StanfordCoreNLP(props);
	    nluClient = new NaturalLanguageUnderstandingClient();
	}
	
	public List<Triplet> annotateSentence(String highlight) {
		List<Triplet> listOfTriplets = new ArrayList<Triplet>();
		Annotation doc = new Annotation(highlight);
		pipeline.annotate(doc);
		
		// Loop over sentences in the document
	    for (CoreMap sentence : doc.get(CoreAnnotations.SentencesAnnotation.class)) {
		  // Get the OpenIE triples for the sentence
		  Collection<RelationTriple> triples =
				  sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
		  for (RelationTriple triple : triples) {
			  	String subject = triple.subjectGloss();
			  	String verb = triple.relationGloss();
			  	String object = triple.objectGloss();
			  	Triplet triplet = new Triplet(subject, verb, object);
			  	listOfTriplets.add(triplet);
		  }
	    }
	    enrichTriplets(listOfTriplets);
	    return listOfTriplets;
	}
	
	public void enrichTriplets(List<Triplet> listOfTriplets) {
		for(Triplet triplet: listOfTriplets) {
			nluClient.enrichRichNode(triplet.getSubjectTry());
			nluClient.enrichRichNode(triplet.getObjectTry());
		}
	}
	
	public String printTriple(RelationTriple relationTriple) {
		return relationTriple.confidence + "\t" +		    
				relationTriple.subjectGloss() + "\t" +
				relationTriple.relationGloss() + "\t" +
				relationTriple.objectGloss();
	}
	
	
	public static void main(String[] args) throws Exception {
		String testString = "Obama was born in Hawaii. He is our president.";
		OpenIEClient openIEClient = new OpenIEClient();
		openIEClient.annotateSentence(testString);
		
		System.out.println("\n\nDone!");
		
	  }
}
