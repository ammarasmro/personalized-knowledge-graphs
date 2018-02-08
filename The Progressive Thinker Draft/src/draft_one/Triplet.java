package draft_one;

public class Triplet {
	
	private String subject;
	private String verb;
	private String object;
	
	private RichTextNode subjectTry;
	private RichTextNode objectTry;
	
	public Triplet() {
		
	}
	
//	public Triplet(String subject, String verb, String object) {
//		this.subject = subject;
//		this.verb = verb;
//		this.object = object;
//	}
	
	public Triplet(String subject, String verb, String object) {
		this.subjectTry = new RichTextNode(subject);
		this.verb = verb;
		this.objectTry = new RichTextNode(object);
	}
	
	public Triplet(RichTextNode subjectTry, String verb, RichTextNode objectTry) {
		this.subjectTry = subjectTry;
		this.verb = verb;
		this.objectTry = objectTry;
	}
	
	public String getSubject() {
		return subject;
	}
	
	public String getVerb() {
		return verb;
	}
	
	public String getObject() {
		return object;
	}
	
	public RichTextNode getSubjectTry() {
		return subjectTry;
	}

	public RichTextNode getObjectTry() {
		return objectTry;
	}

	public void setObjectTry(RichTextNode objectTry) {
		this.objectTry = objectTry;
	}

	public void setSubjectTry(RichTextNode subjectTry) {
		this.subjectTry = subjectTry;
	}

	@Override 
	public String toString(){
		String subjectKeywords = "";
		String objectKeywords = "";
		try {
			subjectKeywords = subjectTry.getKeywords().toString();
			objectKeywords = objectTry.getKeywords().toString();
		} catch(Exception e) {
			System.out.println("Error: No keywords");
		}
		return String.format("%s ---- %s ----> %s \n"
				+ "Keywords with subject: %s\n"
				+ "Keywords with object: %s\n", subjectTry, verb, objectTry,
				subjectKeywords, objectKeywords);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((objectTry == null) ? 0 : objectTry.hashCode());
		result = prime * result + ((subjectTry == null) ? 0 : subjectTry.hashCode());
		result = prime * result + ((verb == null) ? 0 : verb.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Triplet other = (Triplet) obj;
		if (objectTry == null) {
			if (other.objectTry != null)
				return false;
		} else if (!objectTry.equals(other.objectTry))
			return false;
		if (subjectTry == null) {
			if (other.subjectTry != null)
				return false;
		} else if (!subjectTry.equals(other.subjectTry))
			return false;
		if (verb == null) {
			if (other.verb != null)
				return false;
		} else if (!verb.equals(other.verb))
			return false;
		return true;
	}
	
//	@Override 
//	public String toString(){
//		return String.format("%s ---- %s ----> %s", subject, verb, object);
//	}
}
