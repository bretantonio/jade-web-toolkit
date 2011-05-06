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

package jade.semantics.interpreter.sips.adapters;

import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationPrinciple;
import jade.semantics.interpreter.SemanticInterpretationPrincipleTable;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.lang.sl.grammar.AllNode;
import jade.semantics.lang.sl.grammar.EqualsNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.ListOfFormula;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.NotNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TermSetNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;

/**
 * This Semantic Interpretation Principle Adapter makes it possible to finely
 * control the facts the semantic agent is allowed to adopt among his beliefs
 * depending on the originating agent.
 * The belief adoption control must be specified within the abstract
 * {@link #doApply(MatchResult, MatchResult, ArrayList, ArrayList, SemanticRepresentation)}
 * method.
 * 
 * <br>
 * Roughly speaking, this SIP adapter consumes Semantic Representations of the
 * form <code>(B ??myself (I ??agent (B ??myself ??fact)))</code>, and, if
 * applicable (that is, if the belief adoption is decided), produces SRs of the
 * form <code>(B ??myself ??fact)</code>.
 * </br>
 * <br>
 * Several instances of such a SIP may be added to the SIP table of the agent.
 * </br>
 * <br>
 * The beliefs to adopt, which are not handled by a Belief Transfer SIP Adapter,
 * will be handled by the default generic
 * {@link jade.semantics.interpreter.sips.BeliefTransfer} SIP (that is, they
 * will be systematically adopted among the semantic agent's beliefs).
 * </br>
 * 
 * MODIFICATION BY CAROLE ADAM - 22 April 2008
 * To allow BeliefTransferSIPAdapters to perform several complementary checks 
 * on incoming beliefs, there are now three pre-computed results:
 * 		- standard refuse result corresponding to immediate refusal of belief
 *  	- immediateAcceptResult corresponding to the previous acceptResult,
 *  	and leading to immediate acceptance of incoming belief
 *  	- localAcceptResult (NEW) corresponding to the success of local checks,
 *  	but allowing to perform further checks with further BeliefTransferSIPAdapters
 *  	before definitely accepting the incoming belief. This result actually 
 *  	contains the original SR with increased index 
 * 
 * @since JSA 1.4
 * @see jade.semantics.interpreter.sips.BeliefTransfer
 * @author Vincent Louis - France Telecom
 * @version Date: 2006/07/31
 * 
 * @version 2.0 author CA date 22 April 2008 - doApply with a sixth parameter: local accept result
 */
public abstract class BeliefTransferSIPAdapter extends
		SemanticInterpretationPrinciple {
	
	private ListOfFormula formulaToBelievePatterns = new ListOfFormula();
	private Term originatingAgentPattern;
	
    /*********************************************************************/
    /**				 			CONSTRUCTORS							**/
    /*********************************************************************/
	
	/**
	 * Creates a Belief Transfer SIP Adapter that controls the adoption of a
	 * given pattern of belief originated by a given pattern of agent.
	 * 
	 * @param capabilities            {@link SemanticCapabilities} instance of the
	 *                                semantic agent owning this instance of SIP.
	 * @param formulaToBelievePattern the pattern of the formula to adopt as a
	 *                                belief, which is controlled by this Belief
	 *                                SIP adapter.
	 * @param originatingAgentPattern the pattern of the agent originating the
	 *                                belief to adopt, which is controlled by this
	 *                                SIP adapter.
	 *                                <br>If set to <code>null</code>, there is
	 *                                no control on the originating agent.</br>
	 * @param notPattern              if <code>true</code>, the Belief Transfer
	 *                                SIP will also control the adoption of the
	 *                                negation of the given pattern of formula
	 *                                to adopt.
	 * @param allPattern              if <code>true</code>, the Belief Transfer
	 *                                SIP will also control the adoption of
	 *                                <code>(= (all ??X ??F (set)))</code>,
	 *                                where <code>??F</code> is replaced with the
	 *                                pattern of formula to adopt (and its
	 *                                negation if the <code>notPattern</code>
	 *                                parameter is set to <code>true</code>).
	 */
	public BeliefTransferSIPAdapter(SemanticCapabilities capabilities,
			Formula formulaToBelievePattern, Term originatingAgentPattern,
			boolean notPattern, boolean allPattern) {
		super(capabilities,
			  "(B ??myself (I ??sender ??goal))",
			  SemanticInterpretationPrincipleTable.BELIEF_TRANSFER);
		formulaToBelievePattern = formulaToBelievePattern.instantiate("myself", myCapabilities.getAgentName()).getSimplifiedFormula();
		formulaToBelievePatterns.append(formulaToBelievePattern);
		if (notPattern) formulaToBelievePatterns.append(new NotNode(formulaToBelievePattern).getSimplifiedFormula());
		if (allPattern) {
			int size = formulaToBelievePatterns.size();
			for (int i=0; i<size; i++) {
				formulaToBelievePatterns.append(
						new EqualsNode(
								new AllNode(new MetaTermReferenceNode("__quantified_term"), formulaToBelievePatterns.element(i)),
								new TermSetNode(new ListOfTerm())).getSimplifiedFormula());
			}
		}
		this.originatingAgentPattern = (originatingAgentPattern==null ?
				new MetaTermReferenceNode("sender_agent") :
				originatingAgentPattern);
	}

	/**
	 * Creates a Belief Transfer SIP Adapter equivalent to the
	 * {@link #BeliefTransferSIPAdapter(SemanticCapabilities, Formula, Term, boolean, boolean)}
	 * constructor, whith the <code>notPattern</code> and <code>allPattern</code>
	 * parameters set to <code>true</code>.
	 * 
	 * @param capabilities            {@link SemanticCapabilities} instance of the
	 *                                semantic agent owning this instance of SIP.
	 * @param formulaToBelievePattern the pattern of the formula to adopt as a
	 *                                belief, which is controlled by this Belief
	 *                                SIP adapter. Note that the adoption of both
	 *                                the negation of this pattern and its nesting
	 *                                within an <code>all</code> IRE will also be
	 *                                controlled by this SIP.
	 * @param originatingAgentPattern the pattern of the agent originating the
	 *                                belief to adopt, which is controlled by this
	 *                                SIP adapter.
	 *                                <br>If set to <code>null</code>, there is
	 *                                no control on the originating agent.</br>
	 */
	public BeliefTransferSIPAdapter(SemanticCapabilities capabilities,
			Formula formulaToBelievePattern, Term originatingAgentPattern) {
		this(capabilities,
				formulaToBelievePattern, originatingAgentPattern,
				true, true);
	}

	/**
	 * Creates a Belief Transfer SIP Adapter with no control on the agent
	 * originating the belief to adopt.
	 * Equivalent to the
	 * {@link #BeliefTransferSIPAdapter(SemanticCapabilities, Formula, Term, boolean, boolean)}
	 * constructor, with the <code>originatingAgentPattern</code> parameter set
	 * to <code>null</code>.
	 * 
	 * @param capabilities            {@link SemanticCapabilities} instance of the
	 *                                semantic agent owning this instance of SIP.
	 * @param formulaToBelievePattern the pattern of the formula to adopt as a
	 *                                belief, which is controlled by this Belief
	 *                                SIP adapter.
	 * @param notPattern              if <code>true</code>, the Belief Transfer
	 *                                SIP will also control the adoption of the
	 *                                negation of the given pattern of formula
	 *                                to adopt.
	 * @param allPattern              if <code>true</code>, the Belief Transfer
	 *                                SIP will also control the adoption of
	 *                                <code>(= (all ??X ??F (set)))</code>,
	 *                                where <code>??F</code> is replaced with the
	 *                                pattern of formula to adopt (and its
	 *                                negation if the <code>notPattern</code>
	 *                                parameter is set to <code>true</code>).
	 */
	public BeliefTransferSIPAdapter(SemanticCapabilities capabilities,
			Formula formulaToBelievePattern,
			boolean notPattern, boolean allPattern) {
		this(capabilities,
				formulaToBelievePattern,
				null, notPattern, allPattern);
	}
	
	/**
	 * Creates a Belief Transfer SIP Adapter with no control on the agent
	 * originating the belief to adopt.
	 * Equivalent to the
	 * {@link #BeliefTransferSIPAdapter(SemanticCapabilities, Formula, Term)}
	 * constructor, with the <code>originatingAgentPattern</code> parameter set
	 * to <code>null</code>.
	 * 
	 * @param capabilities            {@link SemanticCapabilities} instance of the
	 *                                semantic agent owning this instance of SIP.
	 * @param formulaToBelievePattern the pattern of the formula to adopt as a
	 *                                belief, which is controlled by this Belief
	 *                                SIP adapter. Note that the adoption of both
	 *                                the negation of this pattern and its nesting
	 *                                within an <code>all</code> IRE will also be
	 *                                controlled by this SIP.
	 */
	public BeliefTransferSIPAdapter(SemanticCapabilities capabilities,
			Formula formulaToBelievePattern) {
		this(capabilities,
				formulaToBelievePattern,
				null);
	}
	
	/**
	 * Creates a Belief Transfer SIP Adapter that controls the adoption of a
	 * given pattern of belief originated by a given pattern of agent.
	 * Equivalent to the
	 * {@link #BeliefTransferSIPAdapter(SemanticCapabilities, Formula, Term, boolean, boolean)}
	 * constructor, with the <code>formulaToBelievePattern</code> and
	 * <code>originatingAgentPattern</code> parameters specified as
	 * {@link String} objects (representing FIPA-SL expressions).
	 * 
	 * @param capabilities            {@link SemanticCapabilities} instance of the
	 *                                semantic agent owning this instance of SIP.
	 * @param formulaToBelievePattern the pattern of the formula to adopt as a
	 *                                belief, which is controlled by this Belief
	 *                                SIP adapter.
	 * @param originatingAgentPattern the pattern of the agent originating the
	 *                                belief to adopt, which is controlled by this
	 *                                SIP adapter.
	 *                                <br>If set to <code>null</code>, there is
	 *                                no control on the originating agent.</br>
	 * @param notPattern              if <code>true</code>, the Belief Transfer
	 *                                SIP will also control the adoption of the
	 *                                negation of the given pattern of formula
	 *                                to adopt.
	 * @param allPattern              if <code>true</code>, the Belief Transfer
	 *                                SIP will also control the adoption of
	 *                                <code>(= (all ??X ??F (set)))</code>,
	 *                                where <code>??F</code> is replaced with the
	 *                                pattern of formula to adopt (and its
	 *                                negation if the <code>notPattern</code>
	 *                                parameter is set to <code>true</code>).
	 */
	public BeliefTransferSIPAdapter(SemanticCapabilities capabilities,
			String formulaToBelievePattern, String originatingAgentPattern,
			boolean notPattern, boolean allPattern) {
		this(capabilities,
				SL.formula(formulaToBelievePattern),
				(originatingAgentPattern==null ?
						null :
						SL.term(originatingAgentPattern)),
				notPattern, allPattern);
	}
	
	/**
	 * Creates a Belief Transfer SIP Adapter equivalent to the
	 * {@link #BeliefTransferSIPAdapter(SemanticCapabilities, String, String, boolean, boolean)}
	 * constructor, whith the <code>notPattern</code> and <code>allPattern</code>
	 * parameters set to <code>true</code>.
	 * 
	 * @param capabilities            {@link SemanticCapabilities} instance of the
	 *                                semantic agent owning this instance of SIP.
	 * @param formulaToBelievePattern the pattern of the formula to adopt as a
	 *                                belief, which is controlled by this Belief
	 *                                SIP adapter. Note that the adoption of both
	 *                                the negation of this pattern and its nesting
	 *                                within an <code>all</code> IRE will also be
	 *                                controlled by this SIP.
	 * @param originatingAgentPattern the pattern of the agent originating the
	 *                                belief to adopt, which is controlled by this
	 *                                SIP adapter.
	 *                                <br>If set to <code>null</code>, there is
	 *                                no control on the originating agent.</br>
	 */
	public BeliefTransferSIPAdapter(SemanticCapabilities capabilities,
			String formulaToBelievePattern, String originatingAgentPattern) {
		this(capabilities,
				formulaToBelievePattern, originatingAgentPattern,
				true, true);
	}

	/**
	 * Creates a Belief Transfer SIP Adapter with no control on the agent
	 * originating the belief to adopt.
	 * Equivalent to the
	 * {@link #BeliefTransferSIPAdapter(SemanticCapabilities, String, String, boolean, boolean)}
	 * constructor, with the <code>originatingAgentPattern</code> parameter set
	 * to <code>null</code>.
	 * 
	 * @param capabilities            {@link SemanticCapabilities} instance of the
	 *                                semantic agent owning this instance of SIP.
	 * @param formulaToBelievePattern the pattern of the formula to adopt as a
	 *                                belief, which is controlled by this Belief
	 *                                SIP adapter.
	 * @param notPattern              if <code>true</code>, the Belief Transfer
	 *                                SIP will also control the adoption of the
	 *                                negation of the given pattern of formula
	 *                                to adopt.
	 * @param allPattern              if <code>true</code>, the Belief Transfer
	 *                                SIP will also control the adoption of
	 *                                <code>(= (all ??X ??F (set)))</code>,
	 *                                where <code>??F</code> is replaced with the
	 *                                pattern of formula to adopt (and its
	 *                                negation if the <code>notPattern</code>
	 *                                parameter is set to <code>true</code>).
	 */
	public BeliefTransferSIPAdapter(SemanticCapabilities capabilities,
			String formulaToBelievePattern,
			boolean notPattern, boolean allPattern) {
		this(capabilities,
				formulaToBelievePattern,
				null, notPattern, allPattern);
	}
	
	/**
	 * Creates a Belief Transfer SIP Adapter with no control on the agent
	 * originating the belief to adopt.
	 * Equivalent to the
	 * {@link #BeliefTransferSIPAdapter(SemanticCapabilities, String, String)}
	 * constructor, with the <code>originatingAgentPattern</code> parameter set
	 * to <code>null</code>.
	 * 
	 * @param capabilities            {@link SemanticCapabilities} instance of the
	 *                                semantic agent owning this instance of SIP.
	 * @param formulaToBelievePattern the pattern of the formula to adopt as a
	 *                                belief, which is controlled by this Belief
	 *                                SIP adapter. Note that the adoption of both
	 *                                the negation of this pattern and its nesting
	 *                                within an <code>all</code> IRE will also be
	 *                                controlled by this SIP.
	 */
	public BeliefTransferSIPAdapter(SemanticCapabilities capabilities,
			String formulaToBelievePattern) {
		this(capabilities,
				formulaToBelievePattern,
				null);
	}
	

    //*********************************************************************/
    //**				 			METHODS     							**/
    //*********************************************************************/

	/* (non-Javadoc)
	 * @see jade.semantics.interpreter.SemanticInterpretationPrinciple#apply(jade.semantics.interpreter.SemanticRepresentation)
	 */
	@Override
	final public ArrayList apply(SemanticRepresentation sr)
			throws SemanticInterpretationPrincipleException {
		try {
            MatchResult matchResult = pattern.match(sr.getSLRepresentation());
            if (matchResult != null) {
				Formula goal = matchResult.formula("goal");
                Formula belief = goal.isBeliefFrom(myCapabilities.getAgentName());
                if (belief != null) {
    				MatchResult matchAgent = originatingAgentPattern.match(matchResult.term("sender"));
    				if (matchAgent != null) {
    					MatchResult matchFormula = null;
    					for (int i=0; i<formulaToBelievePatterns.size(); i++) {
    						matchFormula = formulaToBelievePatterns.element(i).match(belief);
    						if (matchFormula != null) break;
    					}
    					if ( matchFormula != null) {
    						// MODIF CA 22 April 2008
    						// prepare TWO accept results
    						// call doApply with 6 parameters instead of 5
    						ArrayList acceptResult = new ArrayList();
    						ArrayList localAcceptResult = new ArrayList();
    						acceptResult.add(new SemanticRepresentation(goal, sr));
    						localAcceptResult.add(new SemanticRepresentation(sr,sr.getSemanticInterpretationPrincipleIndex()+1));
    						return doApply(matchFormula, matchAgent, acceptResult, localAcceptResult, new ArrayList(), sr);
    					}
					}
                }
            } 
        } catch (Exception e) {
            e.printStackTrace();
            throw new SemanticInterpretationPrincipleException();
        }
        return null;
	}
	
	
	// CHANGE BY CA 22 April 2008: keep the old doApply method for retro-compatibility
	/**
	 * Method to be overridden in each sub-class, to control the belief adoption
	 * defined by this Belief Transfer SIP Adapter.
	 * This method works as the regular
	 * {@link SemanticInterpretationPrinciple#apply(SemanticRepresentation)}
	 * method and must return either:
	 * <ul>
	 *     <li><code>null</code> if the SIP Adapter is actually not applicable,</li>
	 *     <li>or a list of produced Semantic Representations otherwise.</li>
	 * </ul>
	 * To facilitate the design, the Semantic Representation stating that the
	 * controlled belief has finally to be adopted by the agent is pre-computed
	 * and passed as the <code>acceptResult</code> argument of the method. In
	 * the same way, the <code>refuseResult</code> argument is a pre-computed
	 * set of produced SR that must be returned if the belief has finally not
	 * to be adopted.
	 * <br>
	 * If a more complex activity of the agent is needed to determine whether
	 * the controlled belief must be adopted, this method should return an empty
	 * {@link ArrayList} (meaning the SIP Adapter is "absorbent") and add a
	 * behaviour to the agent, which will perform this activity. When finished,
	 * if the belief must finally be adopted, this behaviour should run a new
	 * interpretation process on the <code>acceptResult</code> pre-computed SR,
	 * by calling the {@link SemanticCapabilities#interpret(SemanticRepresentation)}
	 * method with this parameter. Otherwise, this behaviour should run an
	 * interpretation process on the <code>refuseResult</code> SR.
	 * </br>
	 * 
	 * @see SemanticInterpretationPrinciple#apply(SemanticRepresentation)
	 * 
	 * @param matchFormula result of the matching of the pattern of formula to
	 *                     believe, which is controlled by this Belief Transfer
	 *                     SIP Adapter, against the formula to believe, intended
	 *                     by an external agent.
	 * @param matchAgent   result of the matching of the pattern of originating
	 *                     agent, which is controlled by this Belief Transfer
	 *                     SIP Adapter, against the external agent originating
	 *                     the formula to believe.
	 * @param acceptResult pre-computed result to return, if the formula to believe
	 *                     must finally be adopted by the semantic agent.
	 * @param refuseResult pre-computed result to return, if the formula to believe
	 *                     must finally not be adopted by the semantic agent.
	 * @param sr           incoming Semantic Representation that triggered the
	 *                     application of this Belief Transfer SIP Adapter (this
	 *                     SR is therefore consumed if the SIP is eventually
	 *                     applicable).
	 * @return             a list of SR produced by the application of this SIP,
	 *                     or <code>null</code> if this SIP is not applicable.
	 */
	protected abstract ArrayList doApply(MatchResult matchFormula, 
			                             MatchResult matchAgent,
			                             ArrayList acceptResult,
			                             ArrayList refuseResult,
			                             SemanticRepresentation sr);
	
	
	// CHANGE BY CA 22 April 2008 : added this second doApply method
	/**
	 * Same method with 6 parameters, including the new localAcceptResult
	 * to let the agent perform other checks before definitely accepting
	 * the incoming belief. 
	 */
	protected ArrayList doApply(MatchResult matchFormula,
										MatchResult matchAgent,
										ArrayList immediateAcceptResult,
										ArrayList localAcceptResult,
										ArrayList refuseResult,
										SemanticRepresentation sr) {
		// default code for BeliefTransferSIPAdapters that do not override this 6-parameter method
		return doApply(matchFormula,matchAgent,immediateAcceptResult,refuseResult,sr);
		// if the doApply method with 6 parameters is not redefined in the children class
		// this call the standard doApply method with only 5 parameters (thus ignore the localAcceptResult)
	}
}
