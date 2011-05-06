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

import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.DoneNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.tools.SL;

import java.util.Date;

/**
 * This SIP monitors the belief that a given action has been done. This typically
 * happens when the agent receives an <code>INFORM-DONE</code> message (i.e.
 * another agent informs him that a particular action has been performed) or
 * when the agent has himself performed an action.
 * Extending this adapter mainly consists in overriding the
 * {@link jade.semantics.interpreter.sips.adapters.ApplicationSpecificSIPAdapter#apply(jade.semantics.interpreter.SemanticRepresentation)}
 * abstract method.
 * 
 * <br>
 * Roughly speaking, this SIP adapter consumes Semantic Representations of the
 * form <code>(B ??myself (done ??action))</code>, and should generally be
 * neutral, that is, produce the same SR. Be careful if this SIP does not produce
 * at least the consumed SR. In this case, the fact that the action has been done
 * (and all subsequent facts, such as the action postcondition) will likely not be
 * asserted into the semantic agent's belief base.
 * </br>
 * <br>
 * Several instances of such a SIP may be added to the SIP table of the agent.
 * </br>
 * 
 * @since JSA 1.4
 * @author Vincent Louis - France Telecom
 *
 */
public abstract class ActionDoneSIPAdapter extends
		ApplicationSpecificSIPAdapter {
	
	/***************************************************************************
	 **** CONSTRUCTORS
	 **************************************************************************/

	/**
	 * Create an ActionDoneSIPAdapter that monitors the belief that a given
	 * pattern of action has been done. The pattern to specify is actually a
	 * pattern of FIPA-SL action expression, that is, a pattern of the form
	 * <code>(action ??actor ??act)</code>. Thus, action expressions make it
	 * possible to specify the pattern of the action actor, as well as the
	 * pattern of the functional term describing the action itself. 
	 * 
	 * @param capabilities  {@link SemanticCapabilities} instance of the
	 *                      semantic agent owning this instance of SIP.
	 * @param pattern       the pattern of action to monitor.
	 */
	public ActionDoneSIPAdapter(SemanticCapabilities capabilities, ActionExpression pattern) {
		super(capabilities, getActionDonePattern(pattern));
	}

	/**
	 * Create an ActionDoneSIPAdapter that monitors the belief that a given
	 * action has been done. Equivalent to
	 * {@link #ActionDoneSIPAdapter(SemanticCapabilities, ActionExpression)},
	 * with the <code>pattern</code> parameter specified as a {@link String}
	 * object (representing a FIPA-SL action expression).
	 *  
	 * @param capabilities  {@link SemanticCapabilities} instance of the
	 *                      semantic agent owning this instance of SIP.
	 * @param pattern       the pattern of action to monitor.
	 */
	public ActionDoneSIPAdapter(SemanticCapabilities capabilities, String pattern) {
		this(capabilities, (ActionExpression)SL.term(pattern));
	}

	/**
	 * Create an ActionDoneSIPAdapter that monitors the belief that a given
	 * action has been done within a given deadline. A <code>null</code>
	 * deadline does not set any "timeout" or "one shot" option of the SIP.
	 * 
	 * @param capabilities  {@link SemanticCapabilities} instance of the
	 *                      semantic agent owning this instance of SIP.
	 * @param pattern       the pattern of action to monitor.
	 * @param timeout       the deadline (given as a {@link Date}) attached to the SIP.
	 * 
	 * @see ApplicationSpecificSIPAdapter#setTimeout(Date)
	 */
	public ActionDoneSIPAdapter(SemanticCapabilities capabilities, ActionExpression pattern, Date timeout) {
		super(capabilities, getActionDonePattern(pattern), timeout);
	}

	/**
	 * Create an ActionDoneSIPAdapter that monitors the belief that a given
	 * action has been done within a given timeout. A null timeout only sets the
	 * SIP in "one shot" mode (without timeout). 
	 * 
	 * @param capabilities  {@link SemanticCapabilities} instance of the
	 *                      semantic agent owning this instance of SIP.
	 * @param pattern       the pattern of action to monitor.
	 * @param timeout       the timeout (in milliseconds) attached to the SIP.
	 *                      If <code>0</code>, the SIP is only "one shot".
	 * 
	 * @see ApplicationSpecificSIPAdapter#setTimeout(long)
	 */
	public ActionDoneSIPAdapter(SemanticCapabilities capabilities, ActionExpression pattern, long timeout) {
		super(capabilities, getActionDonePattern(pattern), timeout);
	}

	/**
	 * Create an ActionDoneSIPAdapter that monitors the belief that a given
	 * action has been done within a given deadline. Equivalent to
	 * {@link #ActionDoneSIPAdapter(SemanticCapabilities, ActionExpression, Date)},
	 * with the <code>pattern</code> parameter specified as a {@link String}
	 * object (representing a FIPA-SL action expression).
	 * 
	 * @param capabilities  {@link SemanticCapabilities} instance of the
	 *                      semantic agent owning this instance of SIP.
	 * @param pattern       the pattern of action to monitor.
	 * @param timeout       the deadline (given as a {@link Date}) attached to the SIP.
	 * 
	 * @see ApplicationSpecificSIPAdapter#setTimeout(Date)
	 */
	public ActionDoneSIPAdapter(SemanticCapabilities capabilities, String pattern, Date timeout) {
		this(capabilities, (ActionExpression)SL.term(pattern), timeout);
	}

	/**
	 * Create an ActionDoneSIPAdapter that monitors the belief that a given
	 * action has been done within a given timeout. Equivalent to
	 * {@link #ActionDoneSIPAdapter(SemanticCapabilities, ActionExpression, long)},
	 * with the <code>pattern</code> parameter specified as a {@link String}
	 * object (representing a FIPA-SL action expression).
	 * 
	 * @param capabilities  {@link SemanticCapabilities} instance of the
	 *                      semantic agent owning this instance of SIP.
	 * @param pattern       the pattern of action to monitor.
	 * @param timeout       the timeout (in milliseconds) attached to the SIP.
	 *                      If <code>0</code>, the SIP is only "one shot".
	 * 
	 * @see ApplicationSpecificSIPAdapter#setTimeout(long)
	 */
	public ActionDoneSIPAdapter(SemanticCapabilities capabilities, String pattern, long timeout) {
		this(capabilities, (ActionExpression)SL.term(pattern), timeout);
	}
	
	/***************************************************************************
	 **** PRIVATE METHODS
	 **************************************************************************/

	// computes the pattern of belief that a given action has been done
	static private Formula getActionDonePattern(ActionExpression pattern) {
		return new DoneNode(pattern, SL.TRUE);
//		return SL.formula("(exists ??e (done (; ??action ??e)))")
//				.instantiate("action", pattern);
	}
}
