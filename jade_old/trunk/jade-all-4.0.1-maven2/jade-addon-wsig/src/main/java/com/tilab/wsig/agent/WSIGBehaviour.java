/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2002 TILAB

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

package com.tilab.wsig.agent;

import jade.content.AgentAction;
import jade.content.abs.AbsContentElement;
import jade.content.abs.AbsTerm;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.BasicOntology;
import jade.content.onto.Ontology;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;

import java.util.Date;
import java.util.Vector;

public class WSIGBehaviour extends AchieveREInitiator {

	private static final long serialVersionUID = 6463142354593931841L;
	
	public static final int UNKNOWN_STATUS  = 0;
	public static final int SUCCESS_STATUS  = 1;
	public static final int FAILURE_STATUS  = 2;
	
	private int status;
	private AbsTerm result;
	private String error;
	private SLCodec codec = new SLCodec();
	private Ontology onto;
	private AgentAction agentAction;
	private AID agentExecutor;
	private int timeout = 0;

	
	public WSIGBehaviour(AID agentExecutor, AgentAction agentAction,  Ontology onto, int timeout) {
		super(null, null);
		
		this.status = UNKNOWN_STATUS;
		this.onto = onto;
		this.agentAction = agentAction;
		this.timeout = timeout;
		this.agentExecutor = agentExecutor;
	}

	public void onStart() {
		super.onStart();
		
		myAgent.getContentManager().registerOntology(onto);
		myAgent.getContentManager().registerLanguage(codec);
	}

	protected Vector prepareRequests(ACLMessage msg) {
		Vector v = new Vector(1);
		try {
			ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
			request.setLanguage(codec.getName());
			request.setOntology(onto.getName());
			request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
			request.setReplyByDate(new Date(System.currentTimeMillis() + timeout));
			request.addReceiver(agentExecutor);
			myAgent.getContentManager().fillContent(request, new Action(agentExecutor, agentAction));
			
			v.addElement(request);
			
		} catch (Exception e) {
			status = FAILURE_STATUS;
			error = e.getMessage();
		}
		return v;
	}
	
	protected void handleInform(ACLMessage message)	{
		try {
			status = SUCCESS_STATUS;

			AbsContentElement content = myAgent.getContentManager().extractAbsContent(message);
			String resultType = content.getTypeName();
			if (BasicOntology.RESULT.equals(resultType)) {
				result = ((AbsTerm)content.getAbsObject(BasicOntology.RESULT_VALUE));
				
			} else if (BasicOntology.DONE.equals(resultType)) {
				result = null;
				
			} else {
				throw new Exception("Abs content element of type "+content.getTypeName()+" not supported");
			}
		} catch (Exception e) {
			status = FAILURE_STATUS;
			error = "Extracting result error: "+e.getMessage();
		}
	}

	protected void handleFailure(ACLMessage failure)	{
		status = FAILURE_STATUS;
		
		// Check for AMS response
		if (failure.getSender().equals(myAgent.getAMS())) {
			// Executor agent unreachable
			error = "Agent "+agentExecutor.getLocalName()+" UNREACHABLE";
		} else {
			// Applicative failure
			error = failure.getContent();
		}
	}

	protected void handleRefuse(ACLMessage refuse) {
		status = FAILURE_STATUS;
		error = "Agent "+refuse.getSender().getLocalName()+" REFUSE request";
	}

	protected void handleNotUnderstood(ACLMessage notUnderstood) {
		status = FAILURE_STATUS;
		error = "Agent "+notUnderstood.getSender().getLocalName()+" NOT_UNDERSTOOD request";
	}
	
	public String getError() {
		return error;
	}

	public int getStatus() {
		return status;
	}

	public AbsTerm getAbsResult() {
		return result;
	}
}
