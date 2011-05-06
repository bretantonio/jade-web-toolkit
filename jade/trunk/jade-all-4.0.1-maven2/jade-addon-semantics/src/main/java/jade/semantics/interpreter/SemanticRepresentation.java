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
 * SemanticRepresentation.java
 * Created on 29 oct. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.interpreter;

import jade.lang.acl.ACLMessage;
import jade.semantics.lang.sl.grammar.Formula;
import jade.util.leap.ArrayList;
import jade.util.leap.HashMap;
import jade.util.leap.Iterator;

/**
 * Class that represents a Semantic Representation.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2004/10/29 Revision: 1.0
 * @version Date: 2007/11/06 Revision: 1.1 (Carole Adam, new keys)
 */
public class SemanticRepresentation {

	/**
	 * Index of the initial semantic interpretation principle for this semantic representation
	 */
	public static final int INITIAL_DEDUCTIVE_STEP = 0;
	
	/**
	 * Index value of the SR that makes it no longer applicable to any SIP
	 */
	public static final int NO_LONGER_APPLICABLE = Integer.MAX_VALUE;
	
	// ArrayList of Formula
	// List of formulas that cancel the intention or the plan represented by the SR
	public static final String CANCEL_ON_KEY = "cancel";
	
	// Formula
	// Formula to interpret, before achieving the intention or the plan represented by the SR
	// Typically used to manage the AGREE/REFUSE feedback
	public static final String INTERPRET_BEFORE_GOAL_KEY = "before"; 

	// Formula
	// Formula to interpret, when the intention or the plan represented by the SR succeeds
	// Typically used to manage the INFORM(done) feedback
	public static final String INTERPRET_ON_REACHED_GOAL_KEY = "reached"; 

	// Formula
	// Formula to interpret, when the intention or the plan represented by the SR cannot be reached
	// Typically used to manage the FAILURE feedback
	public static final String INTERPRET_ON_UNREACHED_GOAL_KEY = "unreached"; 

	// Formula
	// Behaviour to restart when the intention of the plan represented by the SR is reached
	public static final String WAITING_BEHAVIOUR_KEY = "waiting";
	
	// Formula
	// Reason why the achievement of a goal has failed
	public static final String REASON_WHY_UNREACHED_KEY = "reason";
	
	// ArrayList of SR
	// List of SR to interpret if the achievement of a goal fails (feasibility failure)
	public static final String INTERPRET_ON_PLAN_FAILURE_KEY = "failure";

	// ArrayList of SR
	// List of SR to interpret if the achievement of a goal fails (feasibility failure)
	public static final String INTERPRET_ON_PLAN_EXCEPTION_KEY = "exception";
	
	// ArrayList of SR
	// List of SR to interpret if the achievement of a goal succeeds
	public static final String INTERPRET_ON_PLAN_SUCCESS_KEY = "success";

	// Formula
	// Committed goal
	public static final String GOAL_KEY = "goal";
	
	// Formula
	// Original intention
	public static final String TRANSFERRED_INTENTION_KEY = "transferred";

//	// ActionExpression
//	public static final String BEFORE_PLAN_KEY = "before";
//	// ActionExpression
//	public static final String AFTER_PLAN_KEY = "after";
//	// ArrayList of SemanticRepresentation (to interpret when plan succeeds)
//	public static final String SUCCESS_KEY = "success";
//	// SemanticRepresentation
//	//public static final String FAILED_GOAL_KEY = "exception";
//	// SemanticRepresentation
//	public static final String UNREACHABLE_GOAL_KEY = "unfeasible";
//	// ArrayList of Formulas to observe
//	public static final String CANCEL_PLAN_KEY = "cancel";
//	// SemanticRepresentation (to interpret when plan falls in execution failure)
//	public static final String EXECUTION_FAILURE_KEY = "execfail";
//	// SemanticRepresentation (to interpret when plan falls in feasibility failure)
//	public static final String FEASIBILITY_FAILURE_KEY = "feasibfail";

	/**
	 * An ACL message
	 */
	private ACLMessage aclMessage;

	/**
	 * A SL Formula
	 */
	private Formula slFormula;

	/**
	 * A semantic interpretation principle index
	 */
	private int semanticInterpretationPrincipleIndex;
        
    /**
     * Set of annotations attached to the SR
     */
    private HashMap annotations = null;

	/** ****************************************************************** */
	/** CONSTRUCTOR * */
	/** ****************************************************************** */

	/**
	 * Creates a copy of a given Semantic Representation.
	 * 
	 * @param sr the SR to copy.
	 */
	public SemanticRepresentation(SemanticRepresentation sr) {
		this(sr, null, null, -1, true);
	} 

	/**
	 * Creates a copy of a given Semantic Representation, with its index replaced
	 * by a given value
	 * 
	 * @param sr the SR to copy.
	 * @param index the specific index associated to the created SR.
	 */
	public SemanticRepresentation(SemanticRepresentation sr, int index) {
		this(sr, null, null, index, true);
	} 

    /**
	 * Creates an empty Semantic Representation.
	 * This SR has neither formula nor ACL message associated.
	 * Its index is set to the initial SIP index value of the SIP table. 
	 */
	public SemanticRepresentation() {
		this(null, null, INITIAL_DEDUCTIVE_STEP);
	} 

	/**
	 * Creates a Semantic Representation with a given formula.
	 * This SR has no ACL message associated.
	 * Its index is set to the initial SIP index value of the SIP table.
	 *  
	 * @param formula the SL formula associated to the created SR
	 */
	public SemanticRepresentation(Formula formula) {
		this(formula, null, INITIAL_DEDUCTIVE_STEP);
	} 

	/**
	 * Creates a copy of an existing Semantic Representation, resets
	 * its SIP index and sets its SL representation to a given formula.
	 * 
	 * @param formula SL Representation to assign to the created
	 * 		  Semantic Representation
	 * @param sr Semantic Representation to copy
	 */
	//TODO modify the signature and check the semantics (index should be copied from the given SR)
	public SemanticRepresentation(Formula formula, SemanticRepresentation sr) {
		this(sr, formula, null, INITIAL_DEDUCTIVE_STEP, true);
	}
	
	
	/**
	 * Creates a new Semantic Representation with a given formula, a given ACL
	 * message and a given SIP index.
	 * The created SR has no annotation.
	 * 
	 * @param formula the SL representation associated to the created SR
	 * @param message the ACL message associated to the created SR
	 * @param index the SIP index associated to the created SR
	 */
	public SemanticRepresentation(Formula formula, ACLMessage message, int index) {
		this.slFormula = formula;
		this.aclMessage = message;
		this.semanticInterpretationPrincipleIndex = index;
	}
	
	/**
	 * Creates a copy of a given Semantic Representation.
	 * The formula, the ACL message and the SIP index may be replaced with a given
	 * (non <code>null</code> or non negative) value.
	 * The set of annotations associated to the original SR may not be copied.
	 * 
	 * @param sr the SR to copy.
	 * @param formula if non <code>null</code>, the formula associated to the created
	 *        SR is set to this value.
	 * @param message if non <code>null</code>, the ACL message associated to the created
	 *        SR is set to this value.
	 * @param index if non negative, the SIP index associated to the created
	 *        SR is set to this value.
	 * @param copyAnnotations if <code>true</code>, the set of annotations is copied
	 *        into the created SR, otherwise the copied SR is created with no annotation.
	 */
	// sr should not be null !!!!
	public SemanticRepresentation(SemanticRepresentation sr, Formula formula, ACLMessage message, int index, boolean copyAnnotations) {
		this.slFormula = (formula == null ? sr.getSLRepresentation() : formula);
		this.aclMessage = (message == null ? sr.getMessage() : message);
		this.semanticInterpretationPrincipleIndex = (index < 0 ? sr.getSemanticInterpretationPrincipleIndex() : index);
		if (copyAnnotations && sr.annotations != null) {
			this.annotations = new HashMap(sr.annotations.size()); // to optimize the creation of the HashMap
			Iterator keys = sr.annotations.keySet().iterator();
			while (keys.hasNext()) {
				String key = (String)keys.next();
				putAnnotation(key, sr.getAnnotation(key));
			}
		}
		else {
			this.annotations = sr.annotations;
		}
	}

	/** ****************************************************************** */
	/** METHOD * */
	/** ****************************************************************** */

	/**
	 * @return Returns the ACL message
	 */
	public ACLMessage getMessage() {
		return aclMessage;
	}

	/**
	 * Sets the ACL message
	 * @param msg the message to set
	 */
	public void setMessage(ACLMessage msg) {
		aclMessage = msg;
	}

	/**
     * Returns the SL representation
	 * @return the SL representation
	 */
	public Formula getSLRepresentation() {
		return slFormula;
	}

	/**
	 * Sets the SL representation
	 * @param formula the formula to set
	 */
	public void setSLRepresentation(Formula formula) {
		slFormula = formula;
	}

	/**
     * Returns the index of the semantic interpretation principle
	 * @return the index of the semantic interpretation principle
	 */
	public int getSemanticInterpretationPrincipleIndex() {
		return semanticInterpretationPrincipleIndex;
	}

	/**
	 * Sets the semantic interpretation principle index
	 * @param i the index to set
	 */
	public void setSemanticInterpretationPrincipleIndex(int i) {
		semanticInterpretationPrincipleIndex = i;
	}
    
	/**
	 * Sets an annotation on a list of SRs.
	 * Equivalent to the {@link #putAnnotation(String, Object)} method applied on
	 * each SR of the given list of SRs.
	 * 
	 * @param SRList list of SRs given as an {@link ArrayList}.
	 * @param key the key identifying the annotation to set.
	 * @param value the value of the annotation to set.
	 * @see #putAnnotation(String, Object)
	 */
	static public void putAnnotation(ArrayList SRList, String key, Object value) {
		for (Iterator i = SRList.iterator(); i.hasNext(); ) {
			((SemanticRepresentation)i.next()).putAnnotation(key, value);
		}
	}
	
    /**
     * Sets an annotation on the SR.
     * If an annotation with the same key already exists, it is replaced by the
     * newly specified one.
     * 
     * @param key the key identifying the annotation to set.
     * @param value the value of the annotation to set.
     */
    public void putAnnotation(String key, Object value) {
    	if (annotations == null) {
    		annotations = new HashMap();
    	}
    	annotations.put(key, value);
    }
    
    // FIXME a method to add an annotation without removing the possibly 
    // existing ones could be useful... (some annotations are lost)
    // Problem: the type of the annotation is not known here...
    
    /**
     * Adds an annotation on the SR.
     * If no annotation with the given key exists, the method is equivalent to
     * {@link #putAnnotation(String, Object)}. Otherwise, a new annotation is generated as an
     * {@link ArrayList} including the existing existing value and the newly specified
     * one.
     * 
     * @param key the key identifying the annotation to add.
     * @param value the value of the annotation to add.
     */
    public void addAnnotation(String key, Object value) {
    	if (annotations != null) {
    		Object existingValue = annotations.get(key);
    		if (existingValue != null) {
    			if (existingValue instanceof ArrayList) {
    				((ArrayList)existingValue).add(value);
    			}
    			else {
    				ArrayList list = new ArrayList(2);
    				list.add(existingValue);
    				list.add(value);
    				annotations.put(key, list);
    			}
    			return;
    		}
    	}
    	ArrayList list = new ArrayList(1);
    	list.add(value);
    	putAnnotation(key, list);
    }
    
    /**
     * Get the value of a given annotation (associated to the SR).
     * 
     * @param key the key of the annotation to retrieve.
     * @return the value of the annotation with the given key, or <code>null</code>
     *         if the SR has no annotation with this key.
     */
    public Object getAnnotation(String key) {
    	if (annotations != null) {
    		return annotations.get(key);
    	}
    	//else {
    		return null;
    	//}
    }
    
    /**
     * Remove a given annotation from the SR and returns it.
     * 
     * @param key the key of the annotation to remove.
     * @return the value of the removed annotation, or <code>null</code> if the
     *         SR has no annotation with the given key.
     */
    public Object removeAnnotation(String key) {
    	if (annotations != null) {
    		return annotations.remove(key);
    	}
    	//else {
    		return null;
    	//}
    }
    
    /**
     * Removes an annotation with a given key and a given value.
     * If the key represents an annotation with several values, only the given
     * value is removed from this list. The annotation is actually removed only
     * if the resulting list of values is empty.
     * 
     * @param key the key of the annotation to remove.
     * @param value the value of the annotation to remove.
     * @return {@code true} if the given value could be found and removed from the
     *         annotations.
     */
    public boolean removeAnnotation(String key, Object value) {
    	Object annotation = getAnnotation(key);
    	if (annotation != null) {
    		if (annotation instanceof ArrayList) {
    			boolean result = ((ArrayList)annotation).remove(value);
    			if (result && ((ArrayList)annotation).isEmpty()) {
    				removeAnnotation(key);
    			}
    			return result;
    		}
    		//else {
    			if (annotation.equals(value)) {
    				removeAnnotation(key);
    				return true;
    			}
    		//}
    	}
    	return false;
    }
    
	/**
	 * Returns the SL formula that represents the semantic representation
	 * @return the SL formula that represents the semantic representation
	 */
	@Override
	public String toString() {
		return slFormula.toString();
	} // End of ToString/0
    
    /**
     * Two SR are equals if their SL representations are equal.
     * @see Object#equals(java.lang.Object) 
     */
    @Override
	public boolean equals(Object o) {
        if (o instanceof SemanticRepresentation) {
            return ((SemanticRepresentation)o).getSLRepresentation().equals(slFormula);
        } 
        return false;
    }   
}
