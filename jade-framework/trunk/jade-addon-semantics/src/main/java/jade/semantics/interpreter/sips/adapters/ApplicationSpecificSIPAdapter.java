/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
JSA - JADE Semantics Add-on is a framework to develop cognitive
agents in compliance with the FIPA-ACL formal specifications.

Copyright (C) 2006 France Télécom

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

import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationPrinciple;
import jade.semantics.interpreter.SemanticInterpretationPrincipleTable;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;

import java.util.Date;

/**
 * This SIP Adapter is the root class to create all application-specific SIPs
 * aiming at controlling the semantic agent's general behaviour. The right way to
 * use it simply consists in overriding the abstract
 * {@link #doApply(MatchResult, ArrayList, SemanticRepresentation)} method.
 * 
 * <br>
 * Additionally, such an Adapter makes available a "one shot" and a
 * "timeout" options. When activated, the first option removes the SIP from the
 * agent's SIP table as soon as the SIP is applied (the SIP will then no longer
 * be applied). The second option removes the SIP from the agent's SIP table as
 * soon as the SIP is applied, if it is applied before a given timeout expires,
 * or when this timeout expires, if the SIP was not applied before.
 * </br>
 * 
 * @author Thierry Martinez - France Telecom
 * @author Vincent Louis - France Telecom
 * @since JSA 1.4
 *
 */
public abstract class ApplicationSpecificSIPAdapter extends
		SemanticInterpretationPrinciple {
	
	/**
	 * When true, the SIP Adapter is removed from the agent's SIP table as soon
	 * as it is applied or its timeout expires 
	 */
	private boolean isOneShot;
	
	/**
	 * When true, the SIP Adapter has already been applied
	 */
	private boolean shot;

	/***************************************************************************
	 **** CONSTRUCTORS
	 **************************************************************************/

	/**
	 * Creates an application-specific SIP, which applies to a given pattern of
	 * SR. This pattern obeys the same rules as the one given to the
	 * {@link SemanticInterpretationPrinciple} super-class: it is automatically
	 * surrounded by a belief modality and all <code>??myself</code> meta-references
	 * are instantiated with the semantic agent's AID.
	 * 
	 * @param capabilities {@link SemanticCapabilities} instance of the semantic
     *                     agent owning this instance of SIP.
	 * @param pattern the pattern of SR the SIP applies to.
	 */
	public ApplicationSpecificSIPAdapter(SemanticCapabilities capabilities, Formula pattern) {
		super(capabilities, pattern, SemanticInterpretationPrincipleTable.APPLICATION_SPECIFIC);
		isOneShot = false;
		shot = false;
	}

	/**
	 * Creates an application-specific SIP, which applies to a given pattern of
	 * SR. This pattern obeys the same rules as the one given to the
	 * {@link SemanticInterpretationPrinciple} super-class: it is automatically
	 * surrounded by a belief modality and all <code>??myself</code> meta-references
	 * are instantiated with the semantic agent's AID.
	 * 
	 * @param capabilities {@link SemanticCapabilities} instance of the semantic
     *                     agent owning this instance of SIP.
	 * @param pattern the pattern of SR the SIP applies to (given as a {@link String}
	 *                representing a FIPA-SL formula).
	 */
	public ApplicationSpecificSIPAdapter(SemanticCapabilities capabilities, String pattern) {
		this(capabilities, SL.formula(pattern));
	}
	
	/**
	 * Creates an application-specific SIP, which applies to a given pattern of
	 * SR and has a given timeout. A null timeout only sets the SIP in "one
	 * shot" mode (without timeout).
	 * 
	 * @param capabilities {@link SemanticCapabilities} instance of the semantic
     *                     agent owning this instance of SIP.
	 * @param pattern the pattern of SR the SIP applies to.
	 * @param timeout the timeout (in milliseconds) attached to the SIP.
	 *                If <code>0</code>, the SIP is only "one shot".
	 * 
	 * @see #setTimeout(long)
	 * @see #ApplicationSpecificSIPAdapter(SemanticCapabilities, Formula)
	 */
	public ApplicationSpecificSIPAdapter(SemanticCapabilities capabilities, Formula pattern, long timeout) {
		this(capabilities, pattern);
		setTimeout(timeout);
	}
	
	/**
	 * Creates an application-specific SIP, which applies to a given pattern of
	 * SR and has a given timeout. A null timeout only sets the SIP in "one
	 * shot" mode (without timeout).
	 * 
	 * @param capabilities {@link SemanticCapabilities} instance of the semantic
     *                     agent owning this instance of SIP.
	 * @param pattern the pattern of SR the SIP applies to (given as a {@link String}
	 *                representing a FIPA-SL formula).
	 * @param timeout the timeout (in milliseconds) attached to the SIP.
	 *                If <code>0</code>, the SIP is only "one shot".
	 * 
	 * @see #setTimeout(long)
	 * @see #ApplicationSpecificSIPAdapter(SemanticCapabilities, String)
	 */
	public ApplicationSpecificSIPAdapter(SemanticCapabilities capabilities, String pattern, long timeout) {
		this(capabilities, pattern);
		setTimeout(timeout);
	}	
	
	/**
	 * Creates an application-specific SIP, which applies to a given pattern of
	 * SR and has a given deadline to be applied. A <code>null</code> deadline
	 * does not set any "timeout" or "one shot" option of the SIP.
	 * 
	 * @param capabilities {@link SemanticCapabilities} instance of the semantic
     *                     agent owning this instance of SIP.
	 * @param pattern the pattern of SR the SIP applies to.
	 * @param timeout the deadline (given as a {@link Date}) attached to the SIP.
	 * 
	 * @see #setTimeout(Date)
	 * @see #ApplicationSpecificSIPAdapter(SemanticCapabilities, Formula)
	 */
	public ApplicationSpecificSIPAdapter(SemanticCapabilities capabilities, Formula pattern, Date timeout) {
		this(capabilities, pattern);
		setTimeout(timeout);
	}

	/**
	 * Creates an application-specific SIP, which applies to a given pattern of
	 * SR and has a given deadline to be applied. A <code>null</code> deadline
	 * does not set any "timeout" or "one shot" option of the SIP.
	 * 
	 * @param capabilities {@link SemanticCapabilities} instance of the semantic
     *                     agent owning this instance of SIP.
	 * @param pattern the pattern of SR the SIP applies to (given as a {@link String}
	 *                representing a FIPA-SL formula).
	 * @param timeout the deadline (given as a {@link Date}) attached to the SIP.
	 * 
	 * @see #setTimeout(Date)
	 * @see #ApplicationSpecificSIPAdapter(SemanticCapabilities, String)
	 */
	public ApplicationSpecificSIPAdapter(SemanticCapabilities capabilities, String pattern, Date timeout) {
		this(capabilities, pattern);
		setTimeout(timeout);
	}

	/**
	 * Creates an application-specific SIP, which applies to a given pattern of
	 * SR and may be set in "one shot" mode.
	 * 
	 * @param capabilities {@link SemanticCapabilities} instance of the semantic
     *                     agent owning this instance of SIP.
	 * @param pattern the pattern of SR the SIP applies to.
	 * @param isOneShot <code>true</code> to set the SIP in "one shot" mode.
	 * 
	 * @see #setTimeout(Date)
	 * @see #ApplicationSpecificSIPAdapter(SemanticCapabilities, Formula)
	 */
	public ApplicationSpecificSIPAdapter(SemanticCapabilities capabilities, Formula pattern, boolean isOneShot) {
		this(capabilities, pattern);
		if (isOneShot) setOneShot();
	}

	/**
	 * Creates an application-specific SIP, which applies to a given pattern of
	 * SR and may be set in "one shot" mode.
	 * 
	 * @param capabilities {@link SemanticCapabilities} instance of the semantic
     *                     agent owning this instance of SIP.
	 * @param pattern the pattern of SR the SIP applies to (given as a {@link String}
	 *                representing a FIPA-SL formula).
	 * @param isOneShot <code>true</code> to set the SIP in "one shot" mode.
	 * 
	 * @see #setTimeout(Date)
	 * @see #ApplicationSpecificSIPAdapter(SemanticCapabilities, String)
	 */
	public ApplicationSpecificSIPAdapter(SemanticCapabilities capabilities, String pattern, boolean isOneShot) {
		this(capabilities, pattern);
		if (isOneShot) setOneShot();
	}

	//***************************************************************************
	//**** OVERRIDDEN METHODS
	//**************************************************************************/

	/* (non-Javadoc)
	 * @see jade.semantics.interpreter.SemanticInterpretationPrinciple#apply(jade.semantics.interpreter.SemanticRepresentation)
	 */
	@Override
	final public ArrayList apply(SemanticRepresentation sr) throws SemanticInterpretationPrincipleException {
			
		MatchResult applyResult = pattern.match(sr.getSLRepresentation());
		
		// BEGIN DEBUG
		//if (this instanceof orangejsademos.business.BuyerAgent.BuyerC) {
//		if (applyResult != null) {
//			System.out.println("class="+this.getClass());
//			System.out.println("pattern="+pattern);
//			System.out.println("sr="+sr);
//			System.out.println("match="+applyResult);
//		}
		//}
		// END DEBUG
		
		if (applyResult != null) {
			ArrayList neutralResult = new ArrayList();
			neutralResult.add(new SemanticRepresentation(sr, sr.getSemanticInterpretationPrincipleIndex() +1));
			ArrayList result = doApply(applyResult, neutralResult, sr);
			// if the SIP has actually been applied
			if (result != null) {
				shot = true;
				// if the SIP is "one shot", then remove it from the agent's table
				if (isOneShot()) {
					// FIXME update SR's indexes within the result ArrayList, to take into accout the removal of the SIP
					myCapabilities.getMySemanticInterpretationTable().removeSemanticInterpretationPrinciple(this);
				}
			}
			return result;
		}
		//else {
			return null;
		//}
	}
	
	/***********************************************************
	 * METHODS TO OVERRIDE
	 ***********************************************************/

	/** 
	 * This method is called when the SIP Adapter is applied to a matching SR.
	 * It consumes the input matched SR and produces the returned list of SRs.
	 * It must be overriden in all subclasses to specify what the application of
	 * the SIP Adapter consists of.
	 * 
	 * @see SemanticInterpretationPrinciple#apply(SemanticRepresentation)
	 * 
	 * @param applyResult the result of the matching between the input SR and
	 *                    the SIP pattern. It contains all the matched
	 *                    meta-references of the pattern.
	 * @param result the default list of SR to return if the SIP is neutral
	 *               (that is, if it produces the same SR as the one it
	 *               consumes).
	 * @param sr the input SR, which the SIP applies to. 
	 * @return <code>null</code> if the SIP is actually not applicable (in this
	 *         case, the input SR is not consumed and the following SIP is
	 *         tried).
	 *         <br>or the list of produced SRs otherwise. If the SIP is neutral,
	 *         the <code>result</code> argument can be returned.</br>
	 */
	protected abstract ArrayList doApply(MatchResult applyResult, 
				                         ArrayList result,
				                         SemanticRepresentation sr);

	/**
	 * This method should be overridden to specify what to do when the timeout
	 * of the SIP Adapter expires. If no timeout is specified, this method is
	 * useless (by default, it does nothing).
	 *
	 * @see #setTimeout(long)
	 * @see #setTimeout(Date)
	 */
	protected void timeout() {
	}

	/***************************************************************************
	 **** SETTERS AND GETTERS
	 **************************************************************************/

	/**
	 * Sets up a timeout, which will remove the SIP Adapter from the agent's SIP
	 * table, if it expires before the SIP is actually applied. This method
	 * implicitly makes the SIP Adapter "one shot".
	 * 
	 * @see #setOneShot() 
	 * @param timeout timeout value in milliseconds.
	 *                <br>If equals to 0, no timeout is set, but the method
	 *                is equivalent to {@link #setOneShot()}.</br>
	 *                <br>If negative, the method has no effect.</br>
	 */
	public void setTimeout(long timeout) {
		if (!isOneShot) {
			if (timeout >= 0) {
				setOneShot();
			}
			if (timeout > 0) {
				Agent myAgent = myCapabilities.getAgent();
				myAgent.addBehaviour(new SIPWakerBehaviour(myAgent, timeout));
			}
		}
	}
	
	/**
	 * Sets up a deadline, which will remove the SIP Adapter from the agent's SIP
	 * table, if it expires before the SIP is actually applied. This method
	 * implicitly makes the SIP Adapter "one shot".
	 * 
	 * @see #setOneShot()
	 * @param timeout deadline value (express as a {@link Date}).
	 *                <br>If <code>null</code>, the method has no effect.</br>
	 */
	public void setTimeout(Date timeout) {
		if (!isOneShot && timeout != null) {
			Agent myAgent = myCapabilities.getAgent();
			setOneShot();
			myAgent.addBehaviour(new SIPWakerBehaviour(myAgent, timeout));
		}
	}

	/**
	 * Sets the SIP Adapter "one shot", which means it will be removed from the
	 * agent's SIP table as soon as it is applied or its timeout (if set)
	 * expires.
	 */
	public void setOneShot() {
		this.isOneShot = true;
	}

	/**
	 * Indicates whether the SIP Adapter is "one shot".
	 * 
	 * @see #setOneShot()
	 * @return <code>true</code> if the SIP Adapter is "one shot",
	 *         <br><code>false</code> otherwise.</br>
	 */
	public boolean isOneShot() {
		return isOneShot;
	}

	/***************************************************************************
	 **** INNER CLASS
	 **************************************************************************/

	private class SIPWakerBehaviour extends WakerBehaviour {
		

		// standard WakerBehaviour constuctor
		SIPWakerBehaviour(Agent arg0, Date arg1) {
			super(arg0, arg1);
		}

		// standard WakerBehaviour constuctor
		SIPWakerBehaviour(Agent arg0, long arg1) {
			super(arg0, arg1);
		}

		// overriding onWake to remove the SIP from the agent's table and notify the SIP it is timed out
		@Override
		protected void onWake() {
			if (!ApplicationSpecificSIPAdapter.this.shot) {
				myCapabilities.getMySemanticInterpretationTable().removeSemanticInterpretationPrinciple(
						ApplicationSpecificSIPAdapter.this);
				ApplicationSpecificSIPAdapter.this.timeout();
			}
		}
	}

}
