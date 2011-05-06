/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
JSA - JADE Semantics Add-on is a framework to develop cognitive
agents in compliance with the FIPA-ACL formal specifications.

Copyright (C) 2007 France Telecom

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*****************************************************************/

/*
 * SemanticInterpretationPrincipleTable.java
 * Created on 18 mars 2005
 * Author : Vincent Pautret
 */
package jade.semantics.interpreter;


/**
 * Interface of the Semantic Interpretation Principle Table. 
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/03/18 Revision: 1.0
 */
public interface SemanticInterpretationPrincipleTable {
    
    /**
     * Indexes of the main SIP classes within the SIP table. 
     */
    public final static int APPLICATION_SPECIFIC = 0;
    public final static int SPLIT_FORMULA = 1;
    public final static int ACTION_FEATURE = 2;
    public final static int INTENTION_FILTERING = 3;
    public final static int COOPERATION = 4;
    public final static int BELIEF_TRANSFER = 5; //4;
    public final static int REQUEST_WHEN = 6; //5;
    public final static int INTENTION_TRANSFER = 7; //6;
    public final static int QUERY = 8;
    public final static int PLANNING = 9; //7;
    public final static int ACTING = 10; // added 6Nov07, CA

    public final static int INSTITUTIONAL = 11; // added 27Nov07, CA
    
    public final static int REFUSE = 12; //8;
    public final static int REJECT_PROPOSAL = 13; //9;
    public final static int AGREE = 14; //10;
    public final static int PROPOSE = 15; //11;
    public final static int REQUEST_WHENEVER = 16; //12;
    public final static int SUBSCRIBE = 17; //13;
    public final static int UNSUBSCRIBE = 18; //14;
    
    public final static int UNREACHABLE_GOAL = 19; //15;
	
    public final static int NB_SIP_CLASSES = 20;	

    /**
     * Adds a new semantic interpretation principle at the beginning of its class
     * @param semanticInterpretationPrinciple the semantic interpretation 
     * principle to add 
     */
    public void addSemanticInterpretationPrinciple(SemanticInterpretationPrinciple semanticInterpretationPrinciple);
    
    /**
     * Removes all the semantic interpretation principles that correspond to the
     * finder.
     * @param finder the finder that permits the recognition of semantic 
     * interpretation principles
     */
    public void removeSemanticInterpretationPrinciple(Finder finder);
    
    /**
     * Returns the semantic interpretation principle at the specified index 
     * in the table
     * @param index an index
     * @return a semantic interpretation principle
     */
    public SemanticInterpretationPrinciple getSemanticInterpretationPrinciple(int index);
    
    /**
     * Returns the size of the table
     * @return the size of the table 
     */
    public int size();
	
	/** 
	 * Returns the actual index of the SIP within the SIP table
	 * @param sip the SIP the index of which has to be returned
	 * @return the index of the SIP within the SIP table
	 */
	public int getIndex(SemanticInterpretationPrinciple sip);
    
    /**
     * Returns the actual index within the whole SIP table of the first SIP of
     * the given class of SIP (identified by a class index among the constants
     * defined in this interface).
     * 
     * @param classIndex identifier of the class of SIP, the index of the
     * first SIP of which is looked for
     * @return the index of the first SIP belonging to the given class of SIP
     */
	public int getIndex(int classIndex);
	
    /**
     * Removes the semantic interpretation principle at the specified index
     * @param index the index in the table
     */
    public void removeSemanticInterpretationPrinciple(int index);

	/**
     * Removes the semantic interpretation principle at the specified index
     * @param sip the sip to remove
     */
    public void removeSemanticInterpretationPrinciple(SemanticInterpretationPrinciple sip);
} // End of interface SemanticInterpretationPrincipleTable
