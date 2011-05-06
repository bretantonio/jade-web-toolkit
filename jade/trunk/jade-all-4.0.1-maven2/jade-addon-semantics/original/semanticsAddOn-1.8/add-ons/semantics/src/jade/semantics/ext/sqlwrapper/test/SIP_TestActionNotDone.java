/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
JSA - JADE Semantics Add-on is a framework to develop cognitive
agents in compliance with the FIPA-ACL formal specifications.

Copyright (C) 2008 France Télécom

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

package jade.semantics.ext.sqlwrapper.test;

import jade.semantics.ext.sqlwrapper.sqltool.SQLTools;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.interpreter.sips.adapters.NotificationSIPAdapter;
import jade.semantics.kbase.QueryResult;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;

/**
 * This principle is intended to be applied to all intentions that have not be 
 * realised. These intentions are considered as not feasible.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/06/20 Revision: 1.0
 */
public class SIP_TestActionNotDone extends NotificationSIPAdapter {
    
    private static Formula Pattern = SL.formula("(B ??myself (forall ?e (not (B ??agent (feasible ?e (done (action ??agent ??action)))))))");

    private SemanticCapabilities get_sc()
    { return this.myCapabilities;}
  
    
    public SIP_TestActionNotDone(SemanticCapabilities capabilities) {
        super(capabilities, Pattern);
    } // End of Failure/1
    
    
    /*********************************************************************/
    /**                         METHODS                                 **/
    /*********************************************************************/
    
    private boolean
    action_is_not_done_action(Term t)
    {
        Formula f = SL.formula("(action_not_done ??action)").instantiate("action", t);
        QueryResult lr = this.get_sc().getMyKBase().query(f);
        if (lr == null)
            return false;
        return true;
    }
    
    /**
     * @inheritDoc
     */
    public void notify(MatchResult applyResult, 
            SemanticRepresentation sr)
    {
        if (SL.match(applyResult.term("action"), SQLTools.ADD_SQLMAPPING_ACTION_PATTERN) != null ||
                SL.match(applyResult.term("action"), SQLTools.ADD_SQLSERVICE_ACTION_PATTERN) != null ||
                SL.match(applyResult.term("action"), SQLTools.REMOVE_SQLMAPPING_ACTION_PATTERN) != null ||
                SL.match(applyResult.term("action"), SQLTools.REMOVE_SQLSERVICE_ACTION_PATTERN) != null )
        {
            if (action_is_not_done_action(applyResult.term("action")))
            {
                System.out.println("[OK] :ActionNotDone:" + applyResult.term("action").toString());   
            }
            else
            {
                System.out.println("#KO# :ActionNotDone:" + applyResult.term("action").toString());   
            }      
        }
    } // End of apply/1
    
} // End of class Failure
