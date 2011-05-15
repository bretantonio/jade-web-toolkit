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
 * created by Carole Adam, November 2007
 */

package jade.semantics.ext.institutions.sips.planning;

/*
 * Class ObligationTransferSIPAdapter
 * Created by Carole Adam, 19 November 2007
 */

import jade.semantics.ext.institutions.InstitutionTools;
import jade.semantics.ext.institutions.InstitutionalCapabilities;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationPrinciple;
import jade.semantics.interpreter.SemanticInterpretationPrincipleTable;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.BelieveNode;
import jade.semantics.lang.sl.grammar.DoneNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.InstitutionalFactNode;
import jade.semantics.lang.sl.grammar.NotNode;
import jade.semantics.lang.sl.grammar.ObligationNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;

/**
 * This Semantic Interpretation Principle Adapter makes it possible to finely
 * control the obligations that the semantic agent adopts for himself, i.e.
 * that he transforms in intentions, depending on the originating institution
 * (an obligation is necessarily encapsulated in an institutional fact telling 
 * which institution imposes this obligation).
 * 
 * The obligation adoption control must be specified within the abstract
 * {@link #doApply(MatchResult, MatchResult, ArrayList, ArrayList, SemanticRepresentation)}
 * method.
 * <br>
 * Roughly speaking, this SIP adapter consumes Semantic Representations of the
 * form <code>(D ??inst (O ??phi))</code>, and, if applicable (that
 * is, if the obligation adoption is decided), produces SRs of the form
 * <code>(I ??myself ??phi)</code>.
 * </br>
 * <br>
 * Several instances of such a SIP may be added to the SIP table of the agent.
 * </br>
 * <br>
 * The goals to adopt, which are not handled by an Obligation Transfer SIP Adapter,
 * will be handled by the default generic
 * {@link jade.semantics.interpreter.sips.ObligationAdoption} SIP (that is, the
 * semantic agent will systematically adopt all obligations that he performs a
 * given action, and so systematically try to achieve the corresponding intention).
 * Other forms of obligations are not managed for now.
 * 
 * </br>
 * @see jade.semantics.ext.institutions.sips.planning.ObligationTransfer
 * @author Carole Adam - France Telecom
 * @date 19 November 2007
 * @since JSA 1.5
 */
public abstract class ObligationTransferSIPAdapter extends
		SemanticInterpretationPrinciple {

    /**
     * Patterns used to compute the application of this SIP
     */
    private Formula i_myself_PHI; 
	private Formula obligationPattern;
	private Term institutionPattern;
    	
	/**
	 * Creates an Obligation Transfer SIP Adapter that controls the adoption 
	 * (the transformation into a corresponding intention) of a
	 * given pattern of obligation imposed by a given institution.
	 * 
	 * @param capabilities      {@link InstitutionalCapabilities} instance of the
	 *                          semantic agent owning this instance of SIP.
	 * @param obligPattern      the pattern of the obligation to adopt, which is
	 *                          controlled by this Obligation SIP adapter.
	 * @param instPattern 		the pattern of the institution imposing this
	 * 							obligation.
	 */
	public ObligationTransferSIPAdapter(InstitutionalCapabilities capabilities, Formula obligPattern, Term instPattern) {
		super(capabilities,
			  "(D ??inst (O ??phi))",
			  SemanticInterpretationPrincipleTable.INTENTION_TRANSFER);
		
		i_myself_PHI = SL.formula("(I " +  myCapabilities.getAgentName() + " ??__phi)");
        obligationPattern = obligPattern.getSimplifiedFormula();
        institutionPattern = instPattern.getSimplifiedTerm();
	}
	
	/**
	 * Creates an Obligation Transfer SIP Adapter that controls the adoption of a
	 * given pattern of obligation.
	 * Equivalent to the
	 * {@link #ObligationTransferSIPAdapter(InstitutionalCapabilities, Formula)}
	 * constructor, with the <code>obligPattern</code> parameter specified as
	 * a {@link String} object (representing a FIPA-SL expression).
	 * 
	 * @param capabilities      {@link InstitutionalCapabilities} instance of the
	 *                          semantic agent owning this instance of SIP.
	 * @param obligPattern      the pattern of the obligation to adopt, which is
	 *                          controlled by this Obligation SIP adapter.
	 * @param instPattern		the pattern of the institution imposing the obligation,
	 * 							given as a String.
	 */
	public ObligationTransferSIPAdapter(InstitutionalCapabilities capabilities, String obligPattern, String instPattern) {
		this(capabilities,
			 SL.formula(obligPattern), SL.term(instPattern));
	}


	/**
	 * Apply method manages several cases
	 */
	@Override
	final public ArrayList apply(SemanticRepresentation sr)
	throws SemanticInterpretationPrincipleException {
		MatchResult matchResult = pattern.match(sr.getSLRepresentation());	// FIXME getSimplified formula ??
		
		if (matchResult != null) {
			// get the institution imposing the obligation
			Term institution = matchResult.term("inst").getSimplifiedTerm();
			// and the formula that should be true according to this obligation
			Formula obligedPhi = matchResult.formula("phi").getSimplifiedFormula();
			
			// check if the obligation is already reached (formula obligation already true)
			// if not reached yet
			if (myCapabilities.getMyKBase().query(obligedPhi) == null) {
				// check if the agent already has the intention to achieve it
				Formula intention = i_myself_PHI.instantiate("__phi", obligedPhi).getSimplifiedFormula();
				
				// if he does not intend to respect this obligation yet
				if (myCapabilities.getMyKBase().query(intention) == null) {
					// check the precise matching with the given patterns of obligation and institution
					MatchResult matchObligation = obligationPattern.match(obligedPhi);
					MatchResult matchInstitution = institutionPattern.match(institution);
					
					if ((matchObligation != null) && (matchInstitution != null)) {
						// ***********************************************************
						// Prepare the SR to produce if obligation transfer is accepted
						ArrayList acceptResult = new ArrayList();

						// Be the obligation accepted or refused, the initial obligation SR
						// is transmitted to be asserted (and it is further interpreted)
						sr.setSemanticInterpretationPrincipleIndex(sr.getSemanticInterpretationPrincipleIndex()+1);
						
						// build the intention to respect the obligation, keeping the annotations of sr
						SemanticRepresentation intentionSR = new SemanticRepresentation(intention, sr);

						// If the intention is reached, the obligation is retracted
						// WARNING : only retract obligations to perform an action ??
						// WARNING : obligations of another agent are not retracted, even if he is believed to have fulfilled them
						Formula successAnnotation = new BelieveNode(
														myCapabilities.getAgentName(),
														new NotNode(new InstitutionalFactNode(
															institution,
															new ObligationNode(obligedPhi))
														));
						intentionSR.putAnnotation(SemanticRepresentation.INTERPRET_ON_REACHED_GOAL_KEY,successAnnotation);
						
						/* Sets the SR to interpret if the adopted intention is unreachable
						 * (annotation used by the UnreachableGoalSIP): just abandon the intention.
						 * 
						 * FIXME: what should the agent do when he cannot respect his obligations ?
						 * If someone reproaches it to him, is that he valid excuse to say it was not
					 	 * feasible ? What belief should we make the agent interpret in case of failure
					 	 * so that he can explain that he does not respect his obligation ?
					 	 */
						
						/*
						 * Feasibility failure annotation 
						 */
						if (obligedPhi instanceof DoneNode) {
							Term obligedAction = ((DoneNode)obligedPhi).as_action();
							Formula feasibilityFailureAnnotation =
								InstitutionTools.buildIsTrying(myCapabilities.getAgentName(), institution, obligedAction);
							intentionSR.putAnnotation(SemanticRepresentation.INTERPRET_ON_UNREACHED_GOAL_KEY,feasibilityFailureAnnotation);
						}
						
						acceptResult.add(intentionSR);
						acceptResult.add(sr);

						// **********************************************************
						// Prepare the SR to produce if obligation transfer is refused: 
						// keep the obligation to be stored (but not interpreted anymore)
						ArrayList refuseResult = new ArrayList();
						refuseResult.add(sr);
						// **************************************************************
						
						return doApply(matchObligation, matchInstitution, acceptResult, refuseResult, sr);
					}//end if (matchAgent and matchGoal != null)
				}//end if query(intention)==null
			}//end if query(goal) == null 
			else {
				// If the corresponding goal was already reached, transparently transmits the SR
				ArrayList result = new ArrayList();
				sr.setSemanticInterpretationPrincipleIndex(sr.getSemanticInterpretationPrincipleIndex()+1);
				result.add(sr);
				return result;
			}
		}// end if (matchResult != null)
		return null;
	}
	
	/**
	 * Method to be overridden in each sub-class, to control the obligation adoption
	 * defined by this Obligation Transfer SIP Adapter.
	 * This method works as the regular
	 * {@link SemanticInterpretationPrinciple#apply(SemanticRepresentation)}
	 * method and must return either:
	 * <ul>
	 *     <li><code>null</code> if the SIP Adapter is actually not applicable,</li>
	 *     <li>or a list of produced Semantic Representations otherwise.</li>
	 * </ul>
	 * To facilitate the design, two Semantic Representation ArrayLists are 
	 * pre-computed and passed as arguments of this method :
	 * <ul>
	 * 		<li> <code>acceptResult</code> is the set of SR stating that the
	 * controlled obligation has to be adopted by the agent
	 * 		<li> <code>refuseResult</code> is the set of produced SR that must 
	 * be returned if the obligation must not be adopted.
	 * <br>
	 * 
	 * If a more complex activity of the agent is needed to determine whether
	 * the controlled obligation must be adopted, this method should return an empty
	 * {@link ArrayList} (meaning the SIP Adapter is "absorbent") and add a
	 * behaviour to the agent, which will perform this activity. When finished,
	 * if the obligation must finally be adopted, this behaviour should run a new
	 * interpretation process on the <code>acceptResult</code> pre-computed SR,
	 * by calling the {@link SemanticCapabilities#interpret(SemanticRepresentation)}
	 * method with this parameter. Otherwise, this behaviour should run an
	 * interpretation process on the <code>refuseResult</code> SR.
	 * 
	 * See also the {@link SemanticCapabilities#interpretAfterPlan(ActionExpression,
	 * ArrayList,ArrayList)} method that automates this process of decision: the first 
	 * argument is a plan that is performed first, the second and third arguments are
	 * the SR to be interpreted depending on the result of the plan.
	 * 
	 * </br>
	 * @param matchObligation   result of the matching of the pattern of obligation to
	 *                     		adopt, which is controlled by this Obligation Transfer
	 *                     		SIP Adapter, against the obligation received.
	 * @param matchInstitution  result of the matching of the pattern of institution against
	 * 							the institution imposing the received obligation.
	 * @param acceptResult 		pre-computed result to return, if the received obligation
								must finally be adopted by the semantic agent.
	 * @param refuseResult 		pre-computed result to return, if the received obligation
								must finally not be adopted by the semantic agent.
	 * @param sr           incoming Semantic Representation that triggered the
	 *                     application of this Obligation Transfer SIP Adapter (this
	 *                     SR has therefore been consumed).
	 * @return             a list of SR produced by the application of this SIP,
	 *                     or <code>null</code> if this SIP is not applicable.
	 */
	protected abstract ArrayList doApply(MatchResult matchObligation, 
										 MatchResult matchInstitution,
										 ArrayList acceptResult, 
										 ArrayList refuseResult,
										 SemanticRepresentation sr);
}
