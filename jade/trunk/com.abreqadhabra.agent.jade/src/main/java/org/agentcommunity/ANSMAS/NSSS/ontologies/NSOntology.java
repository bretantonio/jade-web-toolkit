package org.agentcommunity.ANSMAS.NSSS.ontologies;

import jade.content.onto.*;
import jade.content.schema.*;

	public class NSOntology extends Ontology implements NSVocabulary{
			// ----------> The name identifying this ontology
			public static final String ONTOLOGY_NAME = "Negotiation-Strategy-Ontology";
			// ----------> The singleton instance of this ontology
			private static Ontology instance = new NSOntology();
			// ----------> Method to access the singleton ontology object
			public static Ontology getInstance() { return instance; }
			// Private constructor
			private NSOntology() {
				super(ONTOLOGY_NAME, BasicOntology.getInstance());
				try {
				// ------- Add Concepts
				// Negotiation Strategy
					PredicateSchema cs = new PredicateSchema(NEGOTIATION_STRATEGY);
					add(cs, NegotiationStrategy.class);
					cs.add(NEGOTIATION_STRATEGY_UID, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.MANDATORY);
					cs.add(NEGOTIATION_STRATEGY_NAME, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.OPTIONAL);
					cs.add(NEGOTIATION_STRATEGY_AUTHOR, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.OPTIONAL);
					cs.add(NEGOTIATION_STRATEGY_VERSION, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.OPTIONAL);
					cs.add(NEGOTIATION_STRATEGY_ALGORITHM, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.OPTIONAL);
					cs.add(NEGOTIATION_STRATEGY_ISSUES, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.OPTIONAL);
					cs.add(NEGOTIATION_STRATEGY_DESCRIPTION, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.OPTIONAL);
					cs.add(NEGOTIATION_STRATEGY_RELATIVE_URI, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.OPTIONAL);
				}catch (OntologyException oe) {
					oe.printStackTrace();
				}//try
		}//Private constructor
	}// NSOntology