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



package jade.tools.ascml.launcher;


import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.core.*;
import jade.core.behaviours.SenderBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.MessageTemplate;
import jade.tools.ascml.onto.*;
import jade.tools.ascml.launcher.remoteactions.RemoteActionRequestListener;
import jade.tools.ascml.repository.Repository;
import jade.tools.ascml.gui.GUI;

import java.util.*;
import  javax.swing.JApplet;


public class AgentLauncherApplet extends AgentLauncher {




	public Codec codec = new SLCodec();
	public JApplet website;

	public AgentLauncherApplet(JApplet caller) {
		super();
		this.website=caller;		
	}




	protected void toolSetup()
	{
		(this.getContentManager()).registerLanguage(codec);
		(this.getContentManager()).registerOntology(jade.domain.JADEAgentManagement.JADEManagementOntology.getInstance());
		//getContentManager().registerOntology(new ASCMLOntology());

		/** Registration with the DF */

		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("ASCML");
		sd.setName(getName());
		sd.setOwnership("RWTHAachenAndUniHamburg");
		dfd.setName(getAID());
		dfd.addServices(sd);
		try
		{
			DFAgentDescription actualDfd =
					DFService.register(this, getDefaultDF(), dfd);
			actualDfd.setLeaseTime(new Date(20000));
			Date lease = actualDfd.getLeaseTime();
			System.out.println("Lease: "+lease.toString());
			DFService.keepRegistered((Agent)this,getDefaultDF(),actualDfd,null);
		}
		catch(FIPAException e)
		{
			System.err.println(getLocalName()
					+" registration with the DF unsucceeded. Reason: "
					+e.getMessage());
			doDelete();
		}

		SequentialBehaviour AMSSubscribe = new SequentialBehaviour();		
		// Send 'subscribe' message to the AMS
		AMSSubscribe.addSubBehaviour(new SenderBehaviour(this, getSubscribe()));

		// Handle incoming 'inform' messages
		AMSSubscribe.addSubBehaviour(new platformEventListener());

		// Schedule Behaviours for execution
		addBehaviour(AMSSubscribe);

		// adding ActionRequestListener
		addBehaviour(new RemoteActionRequestListener(this));
		
		li = new LauncherInterface(this);

		repository = new Repository();
		repository.getListenerManager().addExceptionListener(this.getlmi()); // AgentLauncher now has to implement exceptionThrown-method (see below)
		repository.getListenerManager().addToolTakeDownListener(this.getlmi()); // AgentLauncher now has to implement toolTakeDown-method (see below)

		repository.getListenerManager().addModelActionListener(li);
		gui = new GUI(repository);
		repository.init("");
	}

	
}
