package org.agentcommunity.ANSMAS.NSSS.ontologies;

import jade.content.Predicate;

	public class NegotiationStrategy implements Predicate {

		private String uid;
		private String name;
		private String author;
		private String version;
		private String algorithm;
		private String issues;
		private String description;
		private String relativeURI;
		//UID
		public void setUID(String uid) {
			this.uid = uid;
		}
		public String getUID() {
			return uid;
		}
		//NAME
		public void setName(String name) {
			this.name = name;
		}
		public String getName() {
			return name;
		}
		//AUTHOR
		public void setAuthor(String author) {
			this.author = author;
		}
		public String getAuthor() {
			return author;
		}
		//VERSION
		public void setVersion(String version) {
			this.version = version;
		}
		public String getVersion() {
			return version;
		}
		//ALGORITHM
		public void setAlgorithm(String algorithm) {
			this.algorithm = algorithm;
		}
		public String getAlgorithm() {
			return algorithm;
		}
		//ISSUES
		public void setIssues(String issues) {
			this.issues = issues;
		}
		public String getIssues() {
			return issues;
		}
		//DESCRIPTION
		public void setDescription(String description) {
			this.description = description;
		}
		public String getDescription() {
			return description;
		}
		//RelativeURI
		public void setRelativeURI(String relativeURI) {
			this.relativeURI = relativeURI;
		}
		public String getRelativeURI() {
			return relativeURI;
		}
				
		public String toString() {
			return "UID: " + uid +  "\n--> name = " + name + "\n--> author = " + author + "\n--> version = " + version + "\n--> algorithm = " + algorithm + "\n--> issues = " + issues + "\n--> description = " + description + "\n--> relativeURI = " + relativeURI; 
		}
}		
				
	