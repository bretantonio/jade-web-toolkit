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


package jade.tools.ascml.launcher.remotestatus;

import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.UngroundedException;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import jade.tools.ascml.absmodel.IAbstractRunnable;
import jade.tools.ascml.exceptions.ModelActionException;
import jade.tools.ascml.launcher.AgentLauncher;
import jade.tools.ascml.onto.Error;
import jade.tools.ascml.onto.Status;

/** 
 * @author Sven Lilienthal (ascml@sven-lilienthal.de)
 */

public class RemoteStatusInquirer extends AchieveREInitiator
{
	private Status result = null;
	private AgentLauncher al;
	private IAbstractRunnable remoteModel;

	public RemoteStatusInquirer(ACLMessage request, AgentLauncher al, IAbstractRunnable remoteModel)
	{
		super(al, request);
		this.al=al;
		this.remoteModel=remoteModel;
	}
	
	protected void handleAgree(ACLMessage agree)
	{
		// Our message was apparently ok, now let's wait for the result
		//System.out.println("MARGetStatusBehaviour: We just received an agree");
	}
	
	protected void handleInform(ACLMessage reply)
	{
		try {
			result = al.getStatusFromInform(reply);
			
			// We should only receive one inform-ref which contains the status we requested.
			//System.out.println("MARGetStatusBehaviour: We have been informed by the remote agent. Status is: "+result);
			// Fill the result with the message content 
			// This sets the calling DispatcherThread as completed
			remoteModel.setStatus(result);
		} catch (Exception e) {
			System.err.println("GetStatus failed "+ e.getClass().getSimpleName() +" " + e);
			remoteModel.setDetailedStatus("The ASCML could not get the remote Status. " + e);
			remoteModel.setStatus(new Error());
		}
	}
	protected void handleFailure(ACLMessage reply)
	{
		String s = new String(reply.getContent());
		if (s.indexOf("nested") > 0) {
			s = s.substring(s.indexOf("nested"), s.length() - 1);
		} else if (s.indexOf("internal-error") > 0) {
			s = s.substring(s.indexOf("internal-error")+"internal-error".length(), s.length() - 2);
		} else {
			System.err.println("MARGetStatusBehaviour: \n"+s+"\n");
		}
		System.err.println("MARGetStatusBehaviour: Got a failure");
		remoteModel.setDetailedStatus("This ASCML sent out a request to perform an action and this request couldn't be successfully processed. The reason for this is: " + s);
		remoteModel.setStatus(new Error());
	}
	protected void handleRefuse(ACLMessage reply)
	{
		System.err.println("MARGetStatusBehaviour: Got a refuse");
		remoteModel.setDetailedStatus("This ASCML sent out a request to perform an action and this request has been refused. The reason for this is: " + reply.getContent());
		remoteModel.setStatus(new Error());
	}

	protected void handleOutOfSequence(ACLMessage msg) {
		if (msg.getPerformative()==ACLMessage.INFORM_REF) {
			handleInform(msg);
		} else {
			System.err.println("MARGetStatusBehaviour: HOOS: "+msg.toString());
			super.handleOutOfSequence(msg);
		}
	}

	
	
}


