/*
 * Copyright (C) 2005 Chair of Computer Science 4
 * Aachen University of Technology
 *
 * Copyright (C) 2005 Dpt. of Communcation and Distributed Systems
 * University of Hamburg
 *
 * This file is part of the ASCML.
 *
 * The ASCML is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * The ASCML is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ASCML; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


// file: ToolAgentParameterSetOntology.java generated by ontology bean generator.  DO NOT EDIT, UNLESS YOU ARE REALLY SURE WHAT YOU ARE DOING!
package jade.domain.toolagent;

import jade.content.onto.*;
import jade.content.schema.*;
import jade.util.leap.HashMap;
import jade.content.lang.Codec;
import jade.core.CaseInsensitiveString;

/** file: ToolAgentParameterSetOntology.java
 * @author ontology bean generator
 * @version 2005/02/28, 21:35:26
 */
public class ToolAgentParameterSetOntology extends jade.content.onto.Ontology implements ProtegeTools.ProtegeOntology {
   /**
    * These hashmap store a mapping from jade names to either protege names of SlotHolder 
    * containing the protege names.  And vice versa
    */  
   private HashMap jadeToProtege;

  //NAME
  public static final String ONTOLOGY_NAME = "ToolAgentParameterSet";
  // The singleton instance of this ontology
  private static ProtegeIntrospector introspect = new ProtegeIntrospector();
  private static Ontology theInstance = new ToolAgentParameterSetOntology();
  public static Ontology getInstance() {
     return theInstance;
  }

   // ProtegeOntology methods
   public SlotHolder getSlotNameFromJADEName(SlotHolder jadeSlot) {
     return (SlotHolder) jadeToProtege.get(jadeSlot);
   }


   // storing the information
   private void storeSlotName(String jadeName, String javaClassName, String slotName){
       jadeToProtege.put(new SlotHolder(javaClassName, jadeName), new SlotHolder(javaClassName, slotName));
   }


   // VOCABULARY
    public static final String TOOLAGENTPARAMETERSET_PARAMETER="Parameter";
    public static final String TOOLAGENTPARAMETERSET_AGENTID="AgentID";
    public static final String TOOLAGENTPARAMETERSET="ToolAgentParameterSet";
    public static final String TOOLAGENTPARAMETER_TYPE="Type";
    public static final String TOOLAGENTPARAMETER_VALUE="Value";
    public static final String TOOLAGENTPARAMETER="ToolAgentParameter";

  /**
   * Constructor
  */
  private ToolAgentParameterSetOntology(){ 
    super(ONTOLOGY_NAME, BasicOntology.getInstance());
    introspect.setOntology(this);
    jadeToProtege = new HashMap();
    try { 

    // adding Concept(s)
    ConceptSchema toolAgentParameterSchema = new ConceptSchema(TOOLAGENTPARAMETER);
    add(toolAgentParameterSchema, jade.domain.toolagent.ToolAgentParameter.class);

    // adding AgentAction(s)
    AgentActionSchema toolAgentParameterSetSchema = new AgentActionSchema(TOOLAGENTPARAMETERSET);
    add(toolAgentParameterSetSchema, jade.domain.toolagent.ToolAgentParameterSet.class);

    // adding AID(s)

    // adding Predicate(s)


    // adding fields
    toolAgentParameterSchema.add(TOOLAGENTPARAMETER_VALUE, (TermSchema)getSchema(BasicOntology.STRING), ObjectSchema.MANDATORY);
    toolAgentParameterSchema.add(TOOLAGENTPARAMETER_TYPE, (TermSchema)getSchema(BasicOntology.STRING), ObjectSchema.MANDATORY);
    toolAgentParameterSetSchema.add(TOOLAGENTPARAMETERSET_AGENTID, (ConceptSchema)getSchema(BasicOntology.AID), ObjectSchema.MANDATORY);
    toolAgentParameterSetSchema.add(TOOLAGENTPARAMETERSET_PARAMETER, toolAgentParameterSchema, 1, ObjectSchema.UNLIMITED);

    // adding name mappings
    storeSlotName("Value", "jade.domain.toolagent.ToolAgentParameter", "Value");  
    storeSlotName("Type", "jade.domain.toolagent.ToolAgentParameter", "Type");  
    storeSlotName("AgentID", "jade.domain.toolagent.ToolAgentParameterSet", "AgentID");  
    storeSlotName("Parameter", "jade.domain.toolagent.ToolAgentParameterSet", "Parameter");  

    // adding inheritance

   }catch (java.lang.Exception e) {e.printStackTrace();}
  }
  }