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
* created on 12 mars 07 by Thierry Martinez
* added actions Test and Interpret: Carole Adam, 9 November 2007
*/

package jade.semantics.actions;

import jade.semantics.actions.operators.Alternative;
import jade.semantics.actions.operators.Sequence;
import jade.semantics.actions.performatives.AcceptProposal;
import jade.semantics.actions.performatives.Agree;
import jade.semantics.actions.performatives.CallForProposal;
import jade.semantics.actions.performatives.Cancel;
import jade.semantics.actions.performatives.Confirm;
import jade.semantics.actions.performatives.Disconfirm;
import jade.semantics.actions.performatives.Failure;
import jade.semantics.actions.performatives.Inform;
import jade.semantics.actions.performatives.InformIf;
import jade.semantics.actions.performatives.InformRef;
import jade.semantics.actions.performatives.NotUnderstood;
import jade.semantics.actions.performatives.Propose;
import jade.semantics.actions.performatives.QueryIf;
import jade.semantics.actions.performatives.QueryRef;
import jade.semantics.actions.performatives.Refuse;
import jade.semantics.actions.performatives.RejectProposal;
import jade.semantics.actions.performatives.Request;
import jade.semantics.actions.performatives.RequestWhen;
import jade.semantics.actions.performatives.RequestWhenever;
import jade.semantics.actions.performatives.Subscribe;

public class DefaultSemanticActionLoader implements SemanticActionLoader {
	
	/* (non-Javadoc)
	 * @see jade.semantics.actions.SemanticActionLoader#load(jade.semantics.actions.SemanticActionTable)
	 */
	public void load(SemanticActionTable table) {
		table.addSemanticAction(new AcceptProposal(table.getSemanticCapabilities()));
		table.addSemanticAction(new Agree(table.getSemanticCapabilities()));
		table.addSemanticAction(new CallForProposal(table.getSemanticCapabilities()));
		table.addSemanticAction(new Cancel(table.getSemanticCapabilities()));
		table.addSemanticAction(new Failure(table.getSemanticCapabilities()));
		table.addSemanticAction(new NotUnderstood(table.getSemanticCapabilities()));
		table.addSemanticAction(new Propose(table.getSemanticCapabilities()));
		table.addSemanticAction(new Refuse(table.getSemanticCapabilities()));
		table.addSemanticAction(new RejectProposal(table.getSemanticCapabilities()));
		table.addSemanticAction(new QueryIf(table.getSemanticCapabilities()));
		table.addSemanticAction(new QueryRef(table.getSemanticCapabilities()));
		table.addSemanticAction(new InformRef(table.getSemanticCapabilities()));
		table.addSemanticAction(new InformIf(table.getSemanticCapabilities()));
		table.addSemanticAction(new RequestWhenever(table.getSemanticCapabilities()));
		table.addSemanticAction(new Subscribe(table.getSemanticCapabilities()));
		table.addSemanticAction(new RequestWhen(table.getSemanticCapabilities()));
		table.addSemanticAction(new Request(table.getSemanticCapabilities()));
		table.addSemanticAction(new Inform(table.getSemanticCapabilities()));
		table.addSemanticAction(new Confirm(table.getSemanticCapabilities()));
		table.addSemanticAction(new Disconfirm(table.getSemanticCapabilities()));
		
        table.addSemanticAction(new Succeed(table.getSemanticCapabilities()));
        table.addSemanticAction(new Fail(table.getSemanticCapabilities()));
        table.addSemanticAction(new Do(table.getSemanticCapabilities()));
        
        table.addSemanticAction(new Test(table.getSemanticCapabilities()));
        table.addSemanticAction(new Interpret(table.getSemanticCapabilities()));
        table.addSemanticAction(new Wait(table.getSemanticCapabilities()));
        
		table.addSemanticAction(new Alternative(table.getSemanticCapabilities()));
		table.addSemanticAction(new Sequence(table.getSemanticCapabilities()));
	}

}

