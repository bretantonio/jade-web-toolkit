/*
 * Copyright (C) 2005 Chair of Computer Science 4
 * Aachen University of Technology
 *
 * Copyright (C) 2005 Dpt. of Communcation and Distributed Systems
 * University of Hamburg
 *
 * This file is part of the ASCML.
 *
 * The ASCML is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * The ASCML is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ASCML; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


package jade.tools.ascml.launcher.remoteactions;

import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.OntologyException;
import jade.content.onto.UngroundedException;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Result;
import jade.core.*;
import jade.core.behaviours.SenderBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.introspection.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREInitiator;
import jade.proto.SimpleAchieveREInitiator;
import jade.proto.SimpleAchieveREResponder;
import jade.tools.ToolAgent;
import jade.tools.ascml.absmodel.*;
import jade.tools.ascml.onto.*;
import jade.tools.ascml.launcher.*;
import jade.tools.ascml.exceptions.ModelActionException;
/*
	public class ModelActionRequestBehaviour extends AchieveREInitiator
	{
		AbstractMARWaitThread dt;
		public ModelActionRequestBehaviour(ACLMessage request,AbstractMARWaitThread dt,AgentLauncher al)
		{
			super(al, request);
			this.dt=dt;
		}
		
		protected void handleAgree(ACLMessage agree)
		{
			System.out.println("MARBehaviour: We just received an agree");
		}
		
		protected void handleInform(ACLMessage reply)
		{
			System.out.println("MARBehaviour: We have been informed by the remote agent");
			dt.setCompleted();			
		}
		protected void handleFailure(ACLMessage reply)
		{
			String s = new String(reply.getContent());
			if (s.indexOf("nested") > 0) {
			    s = s.substring(s.indexOf("nested"), s.length() - 1);
			} else if (s.indexOf("internal-error") > 0) {
			    s = s.substring(s.indexOf("internal-error")+"internal-error".length(), s.length() - 2);
			} else {
			    System.err.println("MARBehaviour: \n"+s+"\n");
			}
			System.out.println("MARBehaviour: Got a failure");
			dt.error(new ModelActionException("Error while processing the requested action.", "This ASCML sent out a request to perform an action and this request couldn't be successfully processed. The reason for this is: " + s));
		}
		protected void handleRefuse(ACLMessage reply)
		{
			System.out.println("MARBehaviour: Got a refuse");
			dt.error(new ModelActionException("The requested action has been refused.", "This ASCML sent out a request to perform an action and this request has been refused. The reason for this is: " + reply.getContent()));
		}
	}
*/