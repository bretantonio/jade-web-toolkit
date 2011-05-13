package org.agentcommunity.ANSMAS.NCS.ontologies;

import jade.content.Predicate;

	public class NegotiationInformation implements Predicate {

		private String uid;//NEGOTIATION INFORMATION UID
		private String catalogId;//NEGOTIATION INFORMATION CATALOG ID
		private String unspscId;//NEGOTIATION INFORMATION UNSPSC ID

		private String providerId;//NEGOTIATION INFORMATION PROVIDER ID
		private String providerStrategyUid;//NEGOTIATION INFORMATION PROVIDER STRATEGY UID
		private String deciderId;//NEGOTIATION INFORMATION DECIDER ID
		private String deciderStrategyUid;//NEGOTIATION INFORMATION DECIDER STRATEGY UID
		private String result;//NEGOTIATION INFORMATION RESULT


		//UID
		public void setUID(String uid) {
			this.uid = uid;
		}
		public String getUID() {
			return uid;
		}

		//NEGOTIATION INFORMATION CATALOG ID
		public void setCatalogId(String catalogId) {
			this.catalogId = catalogId;
		}
		public String getCatalogId() {
			return catalogId;
		}
		//NEGOTIATION INFORMATION UNSPSC ID
		public void setUnspscId(String unspscId) {
			this.unspscId = unspscId;
		}
		public String getUnspscId() {
			return unspscId;
		}
		//NEGOTIATION INFORMATION PROVIDER UID
		public void setProviderId(String providerId) {
			this.providerId = providerId;
		}
		public String getProviderId() {
			return providerId;
		}
		//NEGOTIATION INFORMATION PROVIDER STRATEGY UID
		public void setProviderStrategyUid(String providerStrategyUid) {
			this.providerStrategyUid = providerStrategyUid;
		}
		public String getProviderStrategyUid() {
			return providerStrategyUid;
		}
		//NEGOTIATION INFORMATION DECIDER UID
		public void setDeciderId(String deciderId) {
			this.deciderId = deciderId;
		}
		public String getDeciderId() {
			return deciderId;
		}
		//NEGOTIATION INFORMATION DECIDER STRATEGY UID
		public void setDeciderStrategyUid(String deciderStrategyUid) {
			this.deciderStrategyUid = deciderStrategyUid;
		}
		public String getDeciderStrategyUid() {
			return deciderStrategyUid;
		}	
			
				
						//NEGOTIATION INFORMATION Result
		public void setResult(String result) {
			this.result = result;
		}
		public String getResult() {
			return result;
		}
		public String toString() {

					
		
		

		return "NEGOTIATION INFORMATION UID:" +  uid +  "\n--> NEGOTIATION INFORMATION CATALOG ID:" +  catalogId +  "\n--> NEGOTIATION INFORMATION UNSPSC ID:" +  unspscId +  "\n--> NEGOTIATION INFORMATION PROVIDER ID: " + providerId +  "\n--> NEGOTIATION INFORMATION PROVIDER STRATEGY UID: " + providerStrategyUid +  "\n--> NEGOTIATION INFORMATION DECIDER ID: " + deciderId +  "\n--> NEGOTIATION INFORMATION DECIDER STRATEGY ID: " + deciderStrategyUid +  "\n--> NEGOTIATION INFORMATION RESULT: " + result;

		}
}		
				
	