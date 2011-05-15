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
 * ActConditionInform.java
 * Created on 8 juin 2005
 * Author : Vincent Louis
 */
package jade.semantics.actions.performatives;

import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.Content;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.semantics.lang.sl.tools.SL.WrongTypeException;

/**
 * This class specifies an action <code>Inform</code>. The content of this kind 
 * of action contains two elements. The first one relate to an action and the 
 * second one relate to a reason for not performing the action.
 * @author Vincent Louis - France Telecom
 * @version Date: 2005/06/08 Revision: 1.0
 */
public abstract class ActionReasonInform extends NonPrimitiveInform {
    
    /**
     * Creates a new ActionReasonInform prototype. By default, the surface content
     * format is set to [ActionExpression, Formula].
     * @param table the SemanticActionTable, which this action prototype belongs
     * to
     * @param surfacePerformative the surface form
     * @param surfaceContentFormat the list of class expected in the surface
     * content
     * @param surfaceContentFormatMessage the message to send when an 
     * unexpected exception occurs
     * @param informContentPattern pattern of the inform content
     */
    
	public ActionReasonInform(SemanticCapabilities capabilities,
			                  int surfacePerformative, 
			                  Class[] surfaceContentFormat, 
			                  String surfaceContentFormatMessage, 
			                  Formula informContentPattern) {
        super(capabilities,
			  surfacePerformative,
			  (surfaceContentFormat == null ? new Class[] {ActionExpression.class, Formula.class} : surfaceContentFormat),
			  (surfaceContentFormatMessage == null ? "an action expression and a formula" : surfaceContentFormatMessage),
			  informContentPattern);
    }
    
    /**
     * @inheritDoc
     */
    @Override
	protected Formula instantiateInformContentPattern(Content surfaceContent) throws WrongTypeException {
        return (Formula)SL.instantiate(informContentPattern,
                "action", surfaceContent.getContentElement(0),
                "reason", surfaceContent.getContentElement(1));
    }
    
    /**
     * @inheritDoc
     * @return By default, returns false.
     */
    @Override
	public boolean setFeaturesFromRationalEffect(MatchResult rationalEffectMatching) throws Exception {
        return false;
    } // End of setFeaturesFromRationalEffect/1
} // End of ActionReasonInform
