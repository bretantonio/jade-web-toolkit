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


package jade.semantics.ext.institutions.sips.generic;

/*
 * Class UntilFormulaInterpretation
 * Created by Carole Adam, 8 April 2008
 */

import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import jade.semantics.ext.institutions.InstitutionTools;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationPrinciple;
import jade.semantics.interpreter.SemanticInterpretationPrincipleTable;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.kbase.QueryResult;
import jade.semantics.kbase.observers.EventCreationObserver;
import jade.semantics.lang.sl.grammar.BelieveNode;
import jade.semantics.lang.sl.grammar.DateTimeConstantNode;
import jade.semantics.lang.sl.grammar.FalseNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.NotNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;

import java.util.Date;

/**
 * This SIP interprets until formulas (previously named temporary formulas), 
 * that are formulas encapsulated in a predicate (until ??formula ??end) 
 * meaning that they are only valid until ??end becomes true, and should 
 * be retracted once ??end is true
 * 
 * ??end can be either :
 *   - any logical formula, like in Robert Demolombe's logic of obligation with delay.
 *   In this case an observer will watch ??end becoming true to retract the until
 *   formula ??formula
 *   - a FIPA date specifying either the absolute date of end of validity, 
 *   or a relative date from now if prefixed with + or - (relative 
 *   DateTime not handled in the grammar for now)
 * 
 * Replaces the old TemporaryFormulaInterpretation:
 *    - change of pattern (predicate named "until" instead of "temporary-formula"
 *    - handles end-validity formulas of the form (dateTime ??fipaDate)
 * 
 * @author wdvh2120
 * @version 1.2 : use of FIPA dates to give an absolute date of end of validity 
 */

public class UntilFormulaInterpretation extends
SemanticInterpretationPrinciple {

	private final boolean DEBUG = false;

	private final static Formula dateTime_DT = SL.formula("(dateTime ??dt)");
	public final static Formula time_VALUE = SL.formula("(time ??value)");
	
	public UntilFormulaInterpretation(SemanticCapabilities capabilities) {
		super(capabilities,"(until (fact ??formula) (fact ??end))",
				SemanticInterpretationPrincipleTable.SPLIT_FORMULA);
	}

	@Override
	public ArrayList apply(SemanticRepresentation sr)
	throws SemanticInterpretationPrincipleException {

		MatchResult applyResult = pattern.match(sr.getSLRepresentation());

		if (applyResult != null) {

			ArrayList resultNew = new ArrayList();

			InstitutionTools.printTraceMessage("UntilInterp SIP, pattern="+pattern, DEBUG);
			InstitutionTools.printTraceMessage("UntilFormulaInterp SIP, sr="+sr, DEBUG);
			InstitutionTools.printTraceMessage("UntilFormulaInterp SIP, match="+applyResult, DEBUG);

			// extract the formula to interpret
			Formula untilFormula = applyResult.formula("formula");
			resultNew.add(new SemanticRepresentation(untilFormula));

			InstitutionTools.printTraceMessage(" # interpret until formula = "+untilFormula, DEBUG);

			// extract the ??end formula
			Formula endFormula = applyResult.formula("end");
			InstitutionTools.printTraceMessage(" # end formula = "+endFormula, DEBUG);

			// if the end of validity is false: the formula is actually permanent: do not post all the mechanism
			if (!(endFormula instanceof FalseNode)) {

				/************************
				 * CASE ONE : FIPA DATE *
				 ************************/
				MatchResult mrDateTime = dateTime_DT.match(endFormula);
				if (mrDateTime != null) {
					Date endValidityDate = ((DateTimeConstantNode)mrDateTime.term("dt")).lx_value(); 
					Long timeToWait = endValidityDate.getTime()-System.currentTimeMillis();
					endFormula = time_VALUE.instantiate("value",SL.integer(timeToWait));
				}
				
				/**************************
				 * CASE TWO : ANY FORMULA *
				 **************************/
				// add an observer to retract the until formula at the end of its validity
				EndOfValidityObserver endOfValidityObserver = 
					new EndOfValidityObserver(myCapabilities,
							endFormula,untilFormula);
				myCapabilities.getMyKBase().addObserver(endOfValidityObserver);
				endOfValidityObserver.update(null);

				// handle the special case where the end-of-validity formula is a time predicate
				MatchResult mr = time_VALUE.match(endFormula);
				if (mr != null) {
					long delay = Long.parseLong(mr.term("value").toString()); 
					// add a WakerBehaviour that will interpret (time ??value) after the given delay
					EndOfValidityBehaviour endOfValidityBehaviour = 
						new EndOfValidityBehaviour(
								myCapabilities.getAgent(),
								delay,
								myCapabilities,
								untilFormula);
					myCapabilities.getAgent().addBehaviour(endOfValidityBehaviour);
				}
			}
			// absorb the predicate until-formula (should not be asserted)
			return resultNew;
		}
		return null;
	}

	
	/******************
	 * INTERNAL BEHAVIOUR
	 */
	
	class EndOfValidityBehaviour extends WakerBehaviour {

		// the capabilities used to interpret the formula
		SemanticCapabilities theCapabilities;

		long myTimeout;
		// the until formula to retract
		Formula theUntilFormula;

		public EndOfValidityBehaviour(Agent a, long timeout, SemanticCapabilities capabilities, Formula untilFormula) {
			super(a,timeout);
			theCapabilities = capabilities;
			// local copy of timeout (super.timeout not visible...)
			myTimeout = timeout; 
			theUntilFormula = untilFormula;		
		}

		@Override
		protected void onWake() {
			Formula timeValue = (Formula)SL.instantiate(time_VALUE,"value",SL.integer(myTimeout));
			QueryResult qr = theCapabilities.getMyKBase().query(timeValue);
			if (qr == null) {
				theCapabilities.interpret(timeValue);
				// this formula will be retracted by a TimePredicateSIP 
				// once it has triggered the observers waiting for it
			}
		}	
	}//end behaviour class
	
}



class EndOfValidityObserver extends EventCreationObserver {

	SemanticCapabilities capab;
	Formula tempFormula;

	public Formula getTempFormula() {
		return tempFormula;
	}

	public EndOfValidityObserver(
			SemanticCapabilities capabilities, 
			Formula endFormula,
			Formula untilFormula) {
		// not oneshot or bug ... (the wrong observer is removed...)
		super(capabilities.getMyKBase(),endFormula,new NotNode(untilFormula),capabilities.getSemanticInterpreterBehaviour(),false);
		capab = capabilities;
		tempFormula = untilFormula;
	}

	@Override
	public void action(QueryResult value) {
		super.action(value);
		capab.getMyKBase().removeObserver(this);

		// if the subscribed event is (time ??value) retract it
		Formula timePattern = new BelieveNode(getMyKBase().getAgentName(),SL.formula("(time ??value)"));
		if (timePattern.match(getObservedFormula()) != null) {
			getMyKBase().retractFormula(getObservedFormula());
		}
	}

}

