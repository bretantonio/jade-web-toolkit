package org.agentcommunity.ANSMAS.NSSS.ontologies;

	public interface NSVocabulary{
		//-------> Basic vocabulary
		
		public static final String NSA_SERVICE_TYPE = "NegotiationStrategyAgent";
		public static final String NSA_OWNERSHIP = "NegotiationStrategySharedServer";

		public static final String NCA_SERVICE_TYPE = "NegotiationControlAgent";
		public static final String NCA_OWNERSHIP = "NegotiationAgentExecutionServer";

		/*public static final int DELETE_NEGOTIATION_STRATEGY = 2;
		public static final int SELECT_NEGOTIATION_STRATEGY = 3;
		public static final int UPDATE_NEGOTIATION_STRATEGY = 4;
		public static final int NOT_ENOUGH_NEGOTIATION_STRATEGY_INFORMATION = 5;
		public static final int NEGOTIATION_STRATEGY_NOT_FOUND = 6;
		public static final int ILLEGAL_OPERATION = 7;		
		*/
		public static final String NSSS_AGENT = "Negotiation Strategy Agent";
		public static final String NSSS_NEGOTIATION_STRATEGY_NOT_FOUND = "Negotiation strategy not found";
		public static final String NSSS_NOT_ENOUGH_NEGOTIATION_STRATEGY_INFORMATION = "Not enough negotiation strategy information";
		public static final String NSSS_ILLEGAL_OPERATION = "Illegal operation";
		//-------> Ontology vocabulary
		//Negotiation Strategy
		public static final String NEGOTIATION_STRATEGY = "NegotiationStrategy";// UID
		public static final String NEGOTIATION_STRATEGY_UID = "uid";// UID
		public static final String NEGOTIATION_STRATEGY_NAME = "name"; //name
		public static final String NEGOTIATION_STRATEGY_AUTHOR = "author"; //author
		public static final String NEGOTIATION_STRATEGY_VERSION = "version"; // version & release & date
		public static final String NEGOTIATION_STRATEGY_ALGORITHM = "algorithm"; // Algorithm 
		public static final String NEGOTIATION_STRATEGY_ISSUES = "issues"; // (comma), seperated
		public static final String NEGOTIATION_STRATEGY_DESCRIPTION = "description"; // information description
		public static final String NEGOTIATION_STRATEGY_RELATIVE_URI = "relativeURI"; // class relative URI
	}
