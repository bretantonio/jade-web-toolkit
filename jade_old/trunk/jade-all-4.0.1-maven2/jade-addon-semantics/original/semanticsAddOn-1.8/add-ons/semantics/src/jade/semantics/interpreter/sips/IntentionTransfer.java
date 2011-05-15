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
 * IntentionTransfer.java
 * Created on 4 nov. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.interpreter.sips;

import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.interpreter.sips.adapters.IntentionTransferSIPAdapter;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.util.leap.ArrayList;

/**
 * This semantic interpretation principle expresses a necessary cooperation principle of the Jade
 * agent (receiving the ACL message) towards the intentions that the sender 
 * intends to communicate. This step is typically used to interpret incoming 
 * <code>Request</code> or <code>Inform</code> messages, the content of which 
 * being an intention of the sender. 
 * @author Vincent Pautret - France Telecom
 * @version Date: 2004/11/30 Revision: 1.0 
 */
public class IntentionTransfer extends IntentionTransferSIPAdapter {
	
	public IntentionTransfer(SemanticCapabilities capabilities) {
		super(capabilities, "??__goal", "??__agent");
	}
	
	/* (non-Javadoc)
     * @see jade.semantics.interpreter.sips.adapters.IntentionTransferSIPAdapter#doApply(jade.semantics.lang.sl.tools.MatchResult, jade.semantics.lang.sl.tools.MatchResult, jade.util.leap.ArrayList, jade.util.leap.ArrayList, jade.semantics.interpreter.SemanticRepresentation)
     */
    @Override
	protected ArrayList doApply(MatchResult matchGoal, MatchResult matchAgent, ArrayList acceptResult, ArrayList refuseResult, SemanticRepresentation sr) {
    	return acceptResult;
    }   
    
}
