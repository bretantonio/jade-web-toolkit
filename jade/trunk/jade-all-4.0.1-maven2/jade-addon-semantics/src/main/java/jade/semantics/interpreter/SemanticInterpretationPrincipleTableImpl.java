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
 * SemanticInterpretationPrincipleTableImpl.java
 * Created on 2 nov. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.interpreter;

import jade.util.leap.ArrayList;

/**
 * Class that represents a table which contains all the semantic interpretation
 * principles known by the agent.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2004/11/02 Revision: 1.0
 * @version Revision: 1.4 (Thierry Martinez, Vincent Louis)
 */
public class SemanticInterpretationPrincipleTableImpl extends ArrayList implements SemanticInterpretationPrincipleTable {
	

    /*********************************************************************/
    /**                         Inner class                             **/
    /*********************************************************************/
	class SIPClass extends ArrayList {
		
	}
	
	/*********************************************************************/
    /**                         CONSTRUCTOR                             **/
    /*********************************************************************/

    /**
     * Creates a new table
     */
    public SemanticInterpretationPrincipleTableImpl() {
        super();
		for (int i=0; i<NB_SIP_CLASSES; i++) {
			add(new SIPClass());
		}
    } 

    /**
     * Creates a new table
     * @param loader a particular loader to add SIPs within the table 
     */
    public SemanticInterpretationPrincipleTableImpl(SemanticInterpretationPrincipleLoader loader) {
        this();
        if ( loader != null ) {loader.load(this);}
    } 

    /**************************************************************************/
    /**                                 PUBLIC METHODS                       **/
    /**************************************************************************/

    /**
     * Adds a semantic interpretation principle at the beginning of the the
     * table and updates the semantic interpretation principle index of the
     * added sip.
     * @param sip the semantic interpretation principle to be added
     * @deprecated SIPs are now classified 
     * @see SemanticInterpretationPrincipleTable#addSemanticInterpretationPrinciple(SemanticInterpretationPrinciple, int)
     */
    @Deprecated
	public void addSemanticInterpretationPrinciple(SemanticInterpretationPrinciple sip) {
        this.addSemanticInterpretationPrinciple(sip, sip.getClassIndex());
    } 

    /**
     * Adds a semantic interpretation principle in the table at the specified
     * index
     * @param sip the sip to add
     * @param classIndex the index in the table (an index should be one of the given SIP class indexes)
     * @see SemanticInterpretationPrincipleTable to know which classIndex to use
     */
    protected void addSemanticInterpretationPrinciple(SemanticInterpretationPrinciple sip, int classIndex) {
		SIPClass aSIPClass = (SIPClass)get(classIndex);
		aSIPClass.add(0,sip);
    } 

    /**
     * Returns the actual index within the whole SIP table of the first SIP of
     * the given class of SIP (identified by a class index among the constants
     * defined in the {@link SemanticInterpretationPrincipleTable} interface).
     * 
     * @param classIndex identifier of the class of SIP, the index of the
     * first SIP of which is looked for
     * @return the index of the first SIP belonging to the given class of SIP
     */
    public int getIndex(int classIndex) {
    	int actualIndex = 0;
    	for (int i=0; i<classIndex; i++) {
    		actualIndex += ((SIPClass)get(i)).size();
    	}
    	return actualIndex;
    }
    
    /**
     * Returns the semantic interpretation principle at the specified index
     * @param index an index
     * @return the semantic interpretation principle at the specified index
     */
    public SemanticInterpretationPrinciple getSemanticInterpretationPrinciple(int index) {
		int currentClassIndex = 0;
		int currentClassSize = 0;
		while ( (currentClassIndex < super.size()) 
			 && ((currentClassSize = ((SIPClass)get(currentClassIndex)).size()) <= index) ) {
			currentClassIndex ++;
			index -= currentClassSize;
		}
		if ( currentClassIndex < super.size() ) {
			return (SemanticInterpretationPrinciple)((SIPClass)get(currentClassIndex)).get(index);
		}
		//else {
			return null;
		//}
    } 

    /**
     * Removes the semantic interpretation principle corresponding to a given
     * specification
     * @param sipFinder specifies the semantic interpretation principle to remove
     */
    public void removeSemanticInterpretationPrinciple(Finder sipFinder) {
		boolean found = false;
		int index = -1;
		for (int currentClassIndex = 0; (!found) && (currentClassIndex < super.size()); currentClassIndex++ ) {
			SIPClass aSIPClass = (SIPClass)get(currentClassIndex);
			for (index = 0; (!found) && (index < aSIPClass.size()); index++ ) {
				found = (sipFinder.identify(aSIPClass.get(index)));
			}
		}
		if ( found ) {
			removeSemanticInterpretationPrinciple(index);
		}
    } 

	/** 
	 * Returns the actual index of the SIP whitin the SIP table
	 * @param sip the SIP the index of which has to be returned
	 * @return the index of the SIP within the SIP table
	 */
	public int getIndex(SemanticInterpretationPrinciple sip)
	{
		boolean found = false;
		int index = 0;
		for (int currentClassIndex = 0; (!found) && (currentClassIndex < super.size()); currentClassIndex++ ) {
			SIPClass aSIPClass = (SIPClass)get(currentClassIndex);
			for (int i = 0; (!found) && (i < aSIPClass.size()); i++ ) {
				found = (sip == aSIPClass.get(i));
				if ( found ) {
					index += i;
				}
			}
			if ( !found ) {
				index += aSIPClass.size();
			}
		}
		return (found ? index : -1);
	}

    /**
     * Removes the semantic interpretation principle corresponding to the
     * specified index.
     * @param index index in the table of the semantic interpretation principle
     *  to remove
     */
    public void removeSemanticInterpretationPrinciple(int index) {
		int currentClassIndex = 0;
		int currentClassSize = 0;
		while ( (currentClassIndex < super.size()) 
			 && ((currentClassSize = ((SIPClass)get(currentClassIndex)).size()) <= index) ) {
			currentClassIndex ++;
			index -= currentClassSize;
		}
		if ( currentClassIndex < size() ) {
			((SIPClass)get(currentClassIndex)).remove(index);
		}
    } 
	
    /**
     * Removes the semantic interpretation principle corresponding to the
     * specified index.
     * @param sip the sip to be removed
     */
    public void removeSemanticInterpretationPrinciple(SemanticInterpretationPrinciple sip) {
		removeSemanticInterpretationPrinciple(getIndex(sip));
    } 
	
	/**
	 * @return the size of the whole table, i.e., including size of subtables
	 */
	@Override
	public int size() {
		int result = 0;
		for (int currentClassIndex = 0; currentClassIndex < super.size(); currentClassIndex++ ) {
			result += ((SIPClass)get(currentClassIndex)).size();
		}	
		return result;
	}

    /**
     * For debugging purpose only
     * @return the string that represents the SIPs of the table
     */
    @Override
	public String toString() {
        String result = ("------------------ SIP TABLE CONTENT\n");
        int size = this.size();
        for (int i=0 ; i<size ; i++) {
            result = result + "(" + i + ") " + getSemanticInterpretationPrinciple(i).getClass() + "\n";
        }
        return result + "-----------------------------------------------";
    } 

} 
