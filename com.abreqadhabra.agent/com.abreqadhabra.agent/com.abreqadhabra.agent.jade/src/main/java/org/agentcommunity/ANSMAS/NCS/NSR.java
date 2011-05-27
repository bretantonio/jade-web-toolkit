package org.agentcommunity.ANSMAS.NCS;

import java.io.*;
import java.util.*;
import org.agentcommunity.ANSMAS.NCS.ontologies.*;
import org.agentcommunity.ANSMAS.*;

	public class NSR implements NIVocabulary{

	protected NMA nma;

				 String[] uid ={"001","002","003"};
				 String[] catalogId ={"CAT001","CAT002","CAT003"};
				 String[] unspscId ={"43211600","43211509","43211503"};//ComputerAccessories","TabletComputers","NotebookComputers
				 String[] providerId ={"company001","company002","company003"};
				 String[] providerStrategyUid ={"ns001","ns002","ns003"};
				 String[] deciderId ={"company004","company005","company006"};
				 String[] deciderStrategyUid ={"ns003","ns001","ns002"};
				 String[] result ={"","",""};

		public NSR(NMA n){
		nma = n;
		registerSampleDataset();
		}




		protected void registerSampleDataset(){



				System.out.println(Utils.getSystemDate() + nma.getLocalName() + ": Loading NSR and insert into Sample Dataset...ok");	
				for (int i=0; i<uid.length; i++){

				NegotiationInformation ni = new NegotiationInformation();

				ni.setUID(uid[i]);
				ni.setCatalogId(catalogId[i]);
				ni.setUnspscId(unspscId[i]);
				ni.setProviderId(providerId[i]);
				ni.setProviderStrategyUid(providerStrategyUid[i]);
				ni.setDeciderId(deciderId[i]);
				ni.setDeciderStrategyUid(deciderStrategyUid[i]);
				ni.setResult(result[i]);
				nma.nsr.put(uid[i], ni);


			}//for
		}//registerSampleDataset()
	}// NSR