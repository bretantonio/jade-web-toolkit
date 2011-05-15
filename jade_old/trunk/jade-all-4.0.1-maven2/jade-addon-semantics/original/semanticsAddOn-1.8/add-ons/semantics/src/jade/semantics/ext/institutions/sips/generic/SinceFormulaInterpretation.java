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
 * Created by Carole Adam, 10 April 2008
 * to replace FutureFormulaInterpretation
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
import jade.semantics.kbase.observers.Observer;
import jade.semantics.lang.sl.grammar.DateTimeConstantNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;

/**
 * This SIP allows to interpret formulas of the form 
 * <code> (since (fact ??formula) (fact ??begin)) </code>
 * Such formulas should become true only once the given ??begin
 * formula has become true. The begin formula can be a time
 * predicate (<code> (time ??delay)</code>) to express that the 
 * formula will become true after the given delay.
 * Thus, this SIP posts a specific observer that will wait for the
 * ??begin formula to become true, and then interpret the since-formula. 
 * If the begin formula is a time predicate, the SIP
 * also posts a waker behaviour that will assert it after the given
 * delay is elapsed. (This time predicate thus triggers the waiting 
 * observer, and is then immediately retracted by {@link TimePredicateSIP}.
 * 
 * @author wdvh2120
 * @version 1.0 18 March 2008
 */
public class SinceFormulaInterpretation extends SemanticInterpretationPrinciple {

	private final boolean DEBUG = false;

	private final static Formula time_VALUE = SL.formula("(time ??value)");

	private final static Formula dateTime_DT = SL.formula("(dateTime ??dt)");
	
	public SinceFormulaInterpretation(SemanticCapabilities capabilities) {
		super(capabilities,"(since (fact ??formula) (fact ??begin))",
				SemanticInterpretationPrincipleTable.SPLIT_FORMULA);
	}

	@Override
	public ArrayList apply(SemanticRepresentation sr)
	throws SemanticInterpretationPrincipleException {

		MatchResult applyResult = pattern.match(sr.getSLRepresentation());

		InstitutionTools.printTraceMessage("since formula interpretation, formula="+sr.getSLRepresentation(),DEBUG);
		InstitutionTools.printTraceMessage("ffi, pattern="+pattern,DEBUG);

		if (applyResult != null) {
			InstitutionTools.printTraceMessage("ffi, applyResult="+applyResult, DEBUG);
			ArrayList resultNew = new ArrayList();

			// extract the formula to interpret
			Formula sinceFormula = applyResult.formula("formula");
			Formula beginFormula = applyResult.formula("begin");

			// query if the begin-validity-period formula is already true
			QueryResult qrBegin = myCapabilities.getMyKBase().query(beginFormula);
			InstitutionTools.printTraceMessage("ffi, query begin-formula="+beginFormula+" => qr="+qrBegin, DEBUG);
			if (qrBegin != null) {
				// if so the since formula is actually immediately valid
				resultNew.add(new SemanticRepresentation(sinceFormula));
			}
			else {
				// CASE 1 : ABSOLUTE DATE-TIME
				MatchResult mrDT = dateTime_DT.match(beginFormula);
				if (mrDT != null) {
					Long timeToWait = ((DateTimeConstantNode)mrDT.term("dt")).lx_value().getTime()-System.currentTimeMillis();
					beginFormula = (Formula)SL.instantiate(time_VALUE,"value",SL.integer(timeToWait));
				}
				
				// CASE 2 : RELATIVE TIME VALUE
				// check if the begin formula is a time predicate
				MatchResult mr = time_VALUE.match(beginFormula);
				
				if (mr != null) {
					// if so handle this special case by posting a behaviour
					// that will assert this predicate after the specified delay
					long delay = Long.parseLong(mr.term("value").toString()); 
					// the WakerBehaviour that will interpret (time ??value) after 
					// the given delay in order to awake the observer
					BeginOfValidityBehaviour beginOfValidityBehaviour = 
						new BeginOfValidityBehaviour(
								myCapabilities.getAgent(),
								delay,
								myCapabilities);
					InstitutionTools.printTraceMessage("post behaviour, delay="+delay, DEBUG);
					myCapabilities.getAgent().addBehaviour(beginOfValidityBehaviour);
				}
				// if the validity formula is NOT a time predicate,
				// do not retract it at the end of the validity period !

				// IN ALL CASES : POST OBSERVER ON BEGIN TO INTERPRET FORMULA
				// post an observer that will wait for the ??begin formula to be true 
				// before interpreting this formula
				Observer obs = new EventCreationObserver(myCapabilities.getMyKBase(),
						beginFormula,
						sinceFormula,
						myCapabilities.getSemanticInterpreterBehaviour(),
						true);
				InstitutionTools.printTraceMessage("post observer waiting for "+beginFormula, DEBUG);
				myCapabilities.getMyKBase().addObserver(obs);
				obs.update(null);
			}		
			// finally
			return resultNew;
		}
		return null;
	}


	/**
	 * INTERNAL CLASS
	 * The behaviour in charge of asserting the time predicate that
	 * will trigger the observer when the begin-of-validity formula
	 * is a time value (predicate (time ??val))
	 * 
	 * @author wdvh2120
	 */
	class BeginOfValidityBehaviour extends WakerBehaviour {

		private final boolean DEBUG = false;
		// the capabilities used to interpret the formula
		SemanticCapabilities theCapabilities;

		long myTimeout;

		public BeginOfValidityBehaviour(Agent a, long timeout, SemanticCapabilities capabilities) { // , Formula sinceFormula) {
			super(a,timeout);
			theCapabilities = capabilities;
			// local copy of timeout (super.timeout not visible...)
			myTimeout = timeout; 
		}

		// method launched after expiration of timeout
		@Override
		protected void onWake() {
			Formula timeValue = (Formula)SL.instantiate(time_VALUE,"value",SL.integer(myTimeout));
			QueryResult qr = theCapabilities.getMyKBase().query(timeValue);
			InstitutionTools.printTraceMessage("WAKE UP !!! ", DEBUG);
			if (qr == null) {
				InstitutionTools.printTraceMessage("interpret : "+timeValue, DEBUG);
				theCapabilities.interpret(timeValue);
			}
		}	
	}//end of behaviour


}//end of sip
