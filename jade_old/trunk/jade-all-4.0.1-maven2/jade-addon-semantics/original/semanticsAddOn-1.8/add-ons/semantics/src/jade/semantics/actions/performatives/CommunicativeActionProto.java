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
 * CommunicativeActionProto.java
 * Created on 29 juin 2005
 * Author : Vincent Pautret
 */
package jade.semantics.actions.performatives;

import jade.semantics.actions.CommunicativeAction;
import jade.semantics.actions.SemanticAction;
import jade.semantics.interpreter.SemanticInterpretationException;
import jade.semantics.lang.sl.grammar.Content;
import jade.semantics.lang.sl.grammar.ContentNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;

/**
 * Internal interface that provides methods to manage communicative actions
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/06/29 version: 1.0
 */
interface CommunicativeActionProto extends CommunicativeAction {
    
    /**
     * Returns a new instance of CommunicativeActionProto
     * @return a new instance of CommunicativeActionProto
     */
     abstract CommunicativeActionProto createInstance();
    
    /**
     * Checks the validity of the built action and sets the content of it.
     * @return a semantic action
     * @throws SemanticInterpretationException if any exception occurs 
     */
     SemanticAction buildAction() throws SemanticInterpretationException;
    
     /**
      * Parses the given content according to the given language.
      * @param content a content
      * @param language a language
      * @return a parsed content 
      * @throws SemanticInterpretationException if any exception occurs
      */
     ContentNode parseContent(String content, String language) throws SemanticInterpretationException; 
    
     /**
      * Creates a Semantic Action using the given surface content. 
      * @param surfaceContent a content
      * @return a semantic action
      * @throws SemanticInterpretationException if any exception occurs
      */
     abstract SemanticAction doNewAction(Content surfaceContent) throws SemanticInterpretationException;
    
     /**
      * Returns true if it succeeds in setting the receiver and the content from
      * the rational effect, false if not. 
      * @param rationalEffectMatching the match result between the rational effect
      * recognition pattern and a given rational effect
      * @return true if it succeeds in setting the receiver and the content from
      * the rational effect, false if not.
      * @throws Exception if any exception occurs
      */
     abstract boolean setFeaturesFromRationalEffect(MatchResult rationalEffectMatching) throws Exception;

     /**
      * Returns the action expression pattern that represents the action
      * @return the action expression pattern that represents the action 
      */
     Term getMyActionExpressionPattern();
     
     /**
      * Returns an array that contains the classes constituting the surface 
      * content
      * @return an array that contains the classes constituting the surface 
      * content
      */
     
	Class[] getSurfaceContentFormat();
     
     /**
      * Returns the size of the content
      * @return the size of the content
      */
     int getContentSize();
    
     /**
      * Returns the surface content format message
      * @return the surface content format message
      */
     String getSurfaceContentFormatMessage();
    
     /**
      * Returns the rational effect recognition pattern
      * @return the rational effect recognition pattern
      */
     Formula getRationalEffectRecognitionPattern();
    
} // End of interface CommunicativeActionProto