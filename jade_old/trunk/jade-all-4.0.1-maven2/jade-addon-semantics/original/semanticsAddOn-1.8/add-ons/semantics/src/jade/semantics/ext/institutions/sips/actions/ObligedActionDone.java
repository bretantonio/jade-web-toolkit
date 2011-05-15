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

package jade.semantics.ext.institutions.sips.actions;

/*
 * Class ObligedActionDone
 * Created by Carole Adam, 4 January 2008
 */

import jade.semantics.ext.institutions.InstitutionTools;
import jade.semantics.ext.institutions.InstitutionalCapabilities;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.interpreter.sips.adapters.ActionDoneSIPAdapter;
import jade.semantics.kbase.QueryResult;
import jade.semantics.lang.sl.grammar.ActionExpressionNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;

/**
 * This SIP catches all actions the agent believes to have been performed
 * and then checks if these actions obey an obligation. If it is the case, 
 * the fulfilled obligation is retracted from the agent's KBase
 * 
 * This retract is not made in the observer watching the respect of the obligation
 * (ObligationRespectObserver) because such an observer is posted only for conscientious
 * agents so it would be a less generic solution
 * 
 * NEW: also retract here the is-trying (for actions of other agents)
 * 
 * TODO simplify the retract code for obligation ? 
 * (retract a formula with meta-ref should retract all possible instantiations)
 * 
 * @author wdvh2120
 * @version 1.0 date 7 January 2007
 */
public class ObligedActionDone extends ActionDoneSIPAdapter {

	private final boolean DEBUG = false;
	
	// patterns
	private static final Formula d_INST_o_done_ANACTION_true = SL.formula("(D ??inst (O (done ??anAction true)))");
	
	
	public ObligedActionDone(InstitutionalCapabilities capabilities) {
		// catch any action by any agent
		super(capabilities,new ActionExpressionNode(
								new MetaTermReferenceNode("author"), 
								new MetaTermReferenceNode("action")));
	}
	
	/**
	 * This method checks if the observed action was believed (by the agent
	 * interpreting it) to be an obligation for its author.
	 * In this case this obligation is retracted (since it has been fulfilled
	 * by the performance of this action)
	 * Moreover the possible is_trying formula is retracted anyway
	 */
	@Override
	protected ArrayList doApply(MatchResult applyResult, ArrayList result,
			SemanticRepresentation sr) {

		/**********************************************
		 * EXTRACT FEATURES AND BUILD USEFUL FORMULAS *
		 **********************************************/
		Term observedAction = applyResult.term("action");
		Term author = applyResult.term("author");
		Formula obligation = (Formula)SL.instantiate(
				d_INST_o_done_ANACTION_true,
				"anAction", new ActionExpressionNode(author,observedAction));
		
		InstitutionTools.printTraceMessage("*** ObligedActionDone is applied by "+myCapabilities.getAgentName(), DEBUG);
		InstitutionTools.printTraceMessage("*** && action = "+observedAction, DEBUG);
		InstitutionTools.printTraceMessage("*** && corresponding obligation = "+obligation, DEBUG);
		
		/*************************************************
		 * FIND ALL INSTITUTIONS imposing the obligation *
		 * to perform this action, and retract all the   *
		 * corresponding institutional facts             *
		 *************************************************/
		// check if the performed action was obliged in any institution
		// (by querying the corresponding obligation to the KBase)
		QueryResult qr = myCapabilities.getMyKBase().query(obligation); 
		// possibly contains several instantiations if this action is obliged in several institutions
		if (qr != null) {
			if (qr.size() == 0) { // qr=KNOWN
				// in this case obligation is already fully instantiated (should not occur because of ??inst)
				myCapabilities.getMyKBase().retractFormula(obligation);
				System.err.println("&& \n this case should not happen... \n &&");
			}
			else {
				InstitutionTools.printTraceMessage("*** && --> qr (size="+qr.size()+") = "+qr, DEBUG);
				for (int k=0;k<qr.size();k++) {
					MatchResult mrk = qr.getResult(k);
					// use the first result of this query to instantiate the institution in the obligation formula
					Formula obligationk = (Formula)InstitutionTools.instantiateFromMatchResult(obligation,mrk);
					InstitutionTools.printTraceMessage("*** && retract obligation("+k+")="+obligationk, DEBUG);
					// retract this obligation (instantiated with the institution imposing it)
					myCapabilities.getMyKBase().retractFormula(obligationk);
				}//end for
			}//end if (qr.size != 0)
		}//end if (qr != null)
		else {
			InstitutionTools.printTraceMessage("*** && --> qr = null !", DEBUG);
		}
		
		/*********************************
		 * RETRACT THE IS_TRYING PATTERN *
		 *********************************/
		Formula isTryingThisAction = InstitutionTools.buildIsTrying(
				author,new MetaTermReferenceNode("institution"),new ActionExpressionNode(author,observedAction));
		myCapabilities.getMyKBase().retractFormula(isTryingThisAction);

		/***********************************************
		 * FINALLY : transmit the SR (transparent SIP) *
		 ***********************************************/
		return result;
	}

}
