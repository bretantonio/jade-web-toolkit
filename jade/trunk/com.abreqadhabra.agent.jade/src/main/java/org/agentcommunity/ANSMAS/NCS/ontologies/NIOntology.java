package org.agentcommunity.ANSMAS.NCS.ontologies;

import jade.content.onto.*;
import jade.content.schema.*;

	public class NIOntology extends Ontology implements NIVocabulary{
			// ----------> The name identifying this ontology
			public static final String ONTOLOGY_NAME = "Negotiation-Information-Ontology";
			// ----------> The singleton instance of this ontology
			private static Ontology instance = new NIOntology();
			// ----------> Method to access the singleton ontology object
			public static Ontology getInstance() { return instance; }
			// Private constructor
			private NIOntology() {
				super(ONTOLOGY_NAME, BasicOntology.getInstance());
				try {
				// ------- Add Concepts
				// Negotiation Strategy
					PredicateSchema cs = new PredicateSchema(NEGOTIATION_INFORMATION);
					add(cs, NegotiationInformation.class);
					cs.add(NEGOTIATION_INFORMATION_UID, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.MANDATORY);
					cs.add(NEGOTIATION_INFORMATION_UNSPSC_ID, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.OPTIONAL);
					cs.add(NEGOTIATION_INFORMATION_PROVIDER_ID, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.OPTIONAL);
					cs.add(NEGOTIATION_INFORMATION_PROVIDER_STRATEGY_UID, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.OPTIONAL);
					cs.add(NEGOTIATION_INFORMATION_DECIDER_ID, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.OPTIONAL);
					cs.add(NEGOTIATION_INFORMATION_DECIDER_STRATEGY_UID, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.OPTIONAL);
				//	cs.add(NEGOTIATION_INFORMATION_RESULT, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.OPTIONAL);
				}catch (OntologyException oe) {
					oe.printStackTrace();
				}//try
		}//Private constructor
	}// NIOntology