package org.agentcommunity.ANSMAS.NCS.ontologies;

	public interface NIVocabulary{
		//-------> Basic vocabulary
		
		static final int NMA_EXIT = 1000;// GUI �̺�Ʈ ��� (����)
		static final int NMA_NEW_NAGENT = 2000;// GUI �̺�Ʈ ��� (���� ������Ʈ ����)
		static final int NMA_KILL_NAGENT = 3000; // GUI �̺�Ʈ ��� (������Ʈ �Ҹ�)
		static final int NMA_MOVE_NAGENT = 4000; // GUI �̺�Ʈ ��� (������Ʈ �Ҹ�)


		static final int MCA_EXIT = 1000;// GUI �̺�Ʈ ��� (����)
		static final int MCA_NEW_NAGENT = 2000;// GUI �̺�Ʈ ��� (���� ������Ʈ ����)
		static final int MCA_KILL_NAGENT = 3000; // GUI �̺�Ʈ ��� (������Ʈ �Ҹ�)
		static final int MCA_MOVE_NAGENT = 4000; // GUI �̺�Ʈ ��� (������Ʈ �Ҹ�)
		static final int MCA_CLONE_NAGENT = 4000; // GUI �̺�Ʈ ��� (������Ʈ �Ҹ�)

		static final int AUTOMATED_NEGOTIATION_START = 9000; // GUI �̺�Ʈ ��� (������Ʈ �Ҹ�)

		public static final String NMA_SERVICE_TYPE = "NegotiationManagerAgent";
		public static final String NMA_OWNERSHIP = "NegotiationControlServer";
		//-------> Ontology vocabulary
		//Negotiation Information
		public static final String NEGOTIATION_INFORMATION = "NegotiationInformation";//
		public static final String NEGOTIATION_INFORMATION_UID = "uid";//
		public static final String NEGOTIATION_INFORMATION_CATALOG_ID = "catalogId";//
		public static final String NEGOTIATION_INFORMATION_UNSPSC_ID = "unspscId";//
		public static final String NEGOTIATION_INFORMATION_PROVIDER_ID = "providerId";//
		public static final String NEGOTIATION_INFORMATION_PROVIDER_STRATEGY_UID = "providerStrategyUid";//
		public static final String NEGOTIATION_INFORMATION_DECIDER_ID = "deciderId";//
		public static final String NEGOTIATION_INFORMATION_DECIDER_STRATEGY_UID = "deciderStrategyUid";//
		public static final String NEGOTIATION_INFORMATION_RESULT = "result";//




	}
