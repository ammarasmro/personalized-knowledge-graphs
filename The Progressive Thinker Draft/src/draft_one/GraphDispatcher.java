package draft_one;

import static org.neo4j.driver.v1.Values.parameters;

import java.util.List;
import java.util.Set;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.TransactionWork;
//import org.neo4j.driver.v1.exceptions.ServiceUnavailableException;

public class GraphDispatcher implements AutoCloseable {

	private final Driver driver;
	
	private String project;
		
	public GraphDispatcher( String uri, String user, String password ) {
		driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
		init();
	}
	
	public boolean init() {
		deleteEverything();
		project = "Car";
		startNewProject("Design");
//		this.addAspectsToProject("Design");
//		this.addAspectsToProject("Performance");
//		this.addAspectsToProject("Materials");
//		this.addAspectsToProject("Building");
//		this.addAspectsToProject("Tools");
//		this.addAspectsToProject("Parts");
		return true;
	}
	
	
	
	public void startNewProject( final String aspect )
    {
        try ( Session session = driver.session() )
        {
            String greeting = session.writeTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                    StatementResult result = tx.run( "CREATE (car:Project {title:$project, started:timestamp()})\n" + 
                    		"CREATE (design:Aspect {aspectName: $aspect})\n" + 
                    		"CREATE (car)-[:HASASPECT]->(design)",
                            parameters( "project", project, "aspect", aspect ) );
                    result.consume();
                    return "Project " + project + " was created!";
                }
            } );
            System.out.println( greeting );
        }
    }
	
	public void addTriplet( final String subject , final String object, final String verb, final String aspect)
    {
        try ( Session session = driver.session() )
        {
            String greeting = session.writeTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                    StatementResult result = tx.run( "CREATE (subject:Subject {subjectText: $subject}),\n" + 
                    		"	   (object:Object {objectText: $object})\n" + 
                    		"\n" + 
                    		"CREATE (subject)-[:IS]->(object)\n" + 
                    		"\n" + 
                    		"WITH subject, object\n" + 
                    		"MATCH (project:Project)-[:HASASPECT]->(aspect:Aspect {aspectName: $aspect})\n" + 
                    		"WITH aspect, project, subject, object\n" + 
                    		"CREATE (aspect)-[:CONTAINS]->(subject)\n" + 
                    		"\n" + 
                    		"RETURN project, aspect, subject, object",
                            parameters( "subject", subject, "object", object, "verb", verb, "project", project, "aspect", aspect ) );
                    result.consume();
                    return "Triplets added!";
                }
            } );
            System.out.println( greeting );
        }
    }
	
	public void addRichTriplet(Triplet triplet) {
		try ( Session session = driver.session() )
        {
            String greeting = session.writeTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                    StatementResult result = tx.run( "MERGE (subject:Subject {subjectText: $subject})\n" + 
                    		"MERGE (object:Object {objectText: $object})\n" + 
                    		"MERGE (subject)-[:IS {verb: $verb}]->(object)",
                            parameters( "subject", triplet.getSubjectTry().getSentence(), "object", triplet.getObjectTry().getSentence(), "verb", triplet.getVerb() ) );
                    result.consume();
                    return "Triplet added!\n" + triplet;
                }
            } );
            System.out.println( greeting );
            
        }		
	}
	
	public void addKeyword( final Keyword keyword, final String aspect)
    {
        try ( Session session = driver.session() )
        {
            String greeting = session.writeTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                    StatementResult result = tx.run( "MERGE (keyword:Keyword {keywordText: $keyword})\n" + 
                    		"WITH keyword\n" + 
                    		"MATCH (project:Project)-[:HASASPECT]->(aspect:Aspect {aspectName: $aspect})\n" + 
                    		"WITH aspect, project, keyword\n" + 
                    		"MERGE (aspect)-[:HasKeyword]->(keyword)\n" + 
                    		"RETURN project, aspect, keyword\n",
                            parameters( "keyword", keyword.getKeyword(), "project", project, "aspect", aspect ) );
                    result.consume();
                    return String.format("Keyword %s added!", keyword);
                }
            } );
            System.out.println( greeting );
        }
    }
	
	public void linkKeywordToDefintion(Triplet triplet) {
		try ( Session session = driver.session() )
        {
            String greeting = session.writeTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                    StatementResult result = tx.run( "MATCH (keyword:Keyword {keywordText:$subject})\n" +
                    		"WITH keyword\n" +
                    		"MERGE (definition:Definition {definitionText: $object})\n" + 
                    		"MERGE (keyword)-[:IS {verb: $verb}]->(definition)",
                            parameters( "subject", triplet.getSubjectTry().getSentence(), "object", triplet.getObjectTry().getSentence(), "verb", triplet.getVerb() ) );
                    result.consume();
                    return "Keyword relation added!\n" + triplet;
                }
            } );
            System.out.println( greeting );
            
        }		
	}
	
	public void linkListOfKeywordsToSubject(String subject, Set<Keyword> keywords) {
		for(Keyword keyword: keywords) {
			linkKeywordToSubject(subject, keyword);
		}
		System.out.println("Linked all keywords to " + subject);
	}
	
	public void linkKeywordToSubject(String subject, Keyword keyword) {
		try ( Session session = driver.session() )
        {
            String greeting = session.writeTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                    StatementResult result = tx.run( "MATCH (keyword:Keyword {keywordText: $keyword})\n" + 
                    		"MATCH (subject:Subject {subjectText: $subject})\n" + 
                    		"WITH keyword, subject\n" + 
                    		"MERGE (keyword)-[:LinksTo]->(subject)\n" + 
                    		"RETURN keyword, subject",
                            parameters( "keyword", keyword.getKeyword(), "subject", subject ) );
                    result.consume();
                    return String.format("Keyword %s linked to subject %s!", keyword, subject);
                }
            } );
            System.out.println( greeting );
        }
	}
	
	public void linkListOfKeywordsToObject(String object, Set<Keyword> keywords) {
		for(Keyword keyword: keywords) {
			linkObjectToKeyword(object, keyword);
		}
		System.out.println("Linked all keywords to " + object);
	}
	
	public void linkObjectToKeyword(String object, Keyword keyword) {
		try ( Session session = driver.session() )
        {
            String greeting = session.writeTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                    StatementResult result = tx.run( "MATCH (keyword:Keyword {keywordText: $keyword})\n" + 
                    		"MATCH (object:Object {objectText: $object})\n" + 
                    		"WITH keyword, object\n" + 
                    		"MERGE (object)-[:HasDefinition]->(keyword)\n" + 
                    		"RETURN keyword, object\n" + 
                    		"",
                            parameters( "keyword", keyword.getKeyword(), "object", object ) );
                    result.consume();
                    return String.format("Object %s linked to keyword %s!", object, keyword);
                }
            } );
            System.out.println( greeting );
        }
	}
	
	public void getAllData()
    {
        try ( Session session = driver.session() )
        {
            String greeting = session.writeTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                    StatementResult result = tx.run( "MATCH (project:Project)-[:HASASPECT]->(aspect:Aspect)-[:CONTAINS]->(subject:Subject)-[]->(object:Object)\n" + 
                    		"RETURN project, aspect, subject, object",
                            parameters() );
                    return result.single().get(0).get("message").asString();
                }
            } );
            System.out.println( greeting );
        }
    }
	
	public void getAspectsOfProject()
    {
        try ( Session session = driver.session() )
        {
            String greeting = session.writeTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                    StatementResult result = tx.run( "MATCH (project:Project {title: $project})-[:HASASPECT]->(aspect:Aspect)\n" + 
                    		"RETURN project, aspect",
                            parameters( "project", project ) );
                    return result.single().get(0).get("aspect").asString();
                }
            } );
            System.out.println( greeting );
        }
    }
	
	public void getTitlesOfProjects()
    {
        try ( Session session = driver.session() )
        {
            String greeting = session.writeTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                    StatementResult result = tx.run( "MATCH (project:Project)\n" + 
                    		"RETURN project",
                            parameters() );
                    return result.single().get(0).get("title").asString();
                }
            } );
            System.out.println( greeting );
        }
    }
	
	public void getSubjectsFromProjectAndAspect( final String aspect)
    {
        try ( Session session = driver.session() )
        {
            String greeting = session.writeTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                    StatementResult result = tx.run( "MATCH (project:Project {title: $project})-[:HASASPECT]->(aspect:Aspect {aspectName: $aspect})-[:CONTAINS]->(subject:Subject)" + 
                    		"RETURN project, aspect, subject",
                            parameters( "project", project ) );
                    return result.single().get(0).get("aspect").asString();
                }
            } );
            System.out.println( greeting );
        }
    }
	
	public void addAspectsToProject(final String aspect)
    {
        try ( Session session = driver.session() )
        {
            String greeting = session.writeTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                    StatementResult result = tx.run( "MATCH (project:Project {title: $project})\n" + 
                    		"WITH project\n" + 
                    		"CREATE (aspect:Aspect {aspectName: $aspect})\n" + 
                    		"CREATE (project)-[:HASASPECT]->(aspect)",
                            parameters( "project", project, "aspect", aspect ) );
                    result.consume();
                    return "Aspect " + aspect + ", added to project " + project; 
                }
            } );
            System.out.println( greeting );
        }
    }
	
	public void deleteEverything()
    {
        try ( Session session = driver.session() )
        {
            String greeting = session.writeTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                    tx.run( "MATCH (n) DETACH DELETE n",
                            parameters() );
                    return "Database emptied...";
                }
            } );
            System.out.println( greeting );
        }
    }

    public static void main( String... args ) throws Exception
    {
    	
    		GraphDispatcher graphDispatcher = new GraphDispatcher( "bolt://localhost:7687", "ammar", "ammar" );
    		
    		graphDispatcher.addKeyword(new Keyword("Keyword 1"), "Design");
    		graphDispatcher.addKeyword(new Keyword("Keyword 2"), "Design");
    		graphDispatcher.addKeyword(new Keyword("Keyword 3"), "Design");
    		
    		graphDispatcher.addRichTriplet(new Triplet("Subject 1", "Verb 1", "Object 1"));
    		graphDispatcher.linkKeywordToSubject("Subject 1", new Keyword("Keyword 1"));
    		graphDispatcher.linkObjectToKeyword("Object 1", new Keyword("Keyword 3"));
    		
    		graphDispatcher.addRichTriplet(new Triplet("Subject 2", "Verb 2", "Object 2"));
    		graphDispatcher.linkKeywordToSubject("Subject 2", new Keyword("Keyword 1"));
    		graphDispatcher.linkObjectToKeyword("Object 2", new Keyword("Keyword 3"));

//        try ( GraphDispatcher graphDispatcher = new GraphDispatcher( "bolt://localhost:7687", "ammar", "ammar" ) )
//        {
//        		greeter.startNewProject("Car", "Design");
//        		greeter.addAspectsToProject("Car", "Performance");
//        		greeter.addTriplet("Automotive Design", "a set of vocations and occupations", "IS", "Car", "Design");
//        		greeter.addTriplet("Automotive Suspension Design", "a kind of design", "IS", "Car", "Design");
//            greeter.printGreeting( "hello, world" );
//        }
    }
    
    
	
	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		driver.close();
	}

	public String getProjectName() {
		return project;
	}

	public void setProjectName(String projectName) {
		this.project = projectName;
	}

}
