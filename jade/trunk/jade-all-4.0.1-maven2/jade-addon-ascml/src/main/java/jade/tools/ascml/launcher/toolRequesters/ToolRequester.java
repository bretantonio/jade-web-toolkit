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


/*
 * $Id: ToolRequester.java,v 1.11 2005/08/24 08:28:44 medha Exp $
 * Created on 14.07.2004
 * TODO Copyright notice
 *
 */
package jade.tools.ascml.launcher.toolRequesters;

import jade.content.AgentAction;
import java.util.ConcurrentModificationException;
import java.util.Hashtable;
import java.util.Iterator;
import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.ContentManager;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.UngroundedException;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Done;
import jade.core.AID;
import jade.core.ContainerID;
import jade.domain.FIPANames.InteractionProtocol;
import jade.domain.JADEAgentManagement.CreateAgent;
import jade.domain.JADEAgentManagement.DebugOn;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.domain.JADEAgentManagement.SniffOn;
import jade.domain.toolagent.ToolAgentParameter;
import jade.domain.toolagent.ToolAgentParameterSet;
import jade.domain.toolagent.ToolAgentParameterSetOntology;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import jade.tools.ascml.launcher.AgentLauncher;
import jade.tools.ascml.model.runnable.RunnableAgentInstance;
import jade.tools.ascml.absmodel.IProperty;
import jade.tools.ascml.absmodel.IRunnableAgentInstance;
import jade.tools.ascml.absmodel.IToolOption;
import jade.wrapper.ControllerException;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.SwingUtilities;

/** start - modified by ds.kim 2011.05.06 **/
import com.sun.java.swing.SwingUtilities3;
//import com.sun.java.swing.SwingUtilities2;
/** end - modified by ds.kim 2011.05.06 **/

/**
 * @author Tim Niemueller & Sven Lilienthal (ascml@sven-lilienthal.de)
 *
 */
public abstract class ToolRequester {
    
	protected String ontologyName;
    private boolean waitForToolCreate = false;
    private Hashtable<String,StringBuffer> waitingAgents;
    private Hashtable<String,StringBuffer> requestAgents;
    private Hashtable<String,IProperty[]> agentTooloptionProperties;
    private int toolCount = 0;
    private AgentLauncher launcher;
    
    public ToolRequester(AgentLauncher launcher) {
		ontologyName = JADEManagementOntology.NAME;
        this.launcher=launcher;
        waitingAgents = new Hashtable<String,StringBuffer>();
        requestAgents = new Hashtable<String,StringBuffer>();
        agentTooloptionProperties = new Hashtable<String,IProperty[]>();
        launcher.getContentManager().registerOntology(ToolAgentParameterSetOntology.getInstance());
    }
    
    private void newToolAgent(String arg, StringBuffer result) throws jade.content.lang.Codec.CodecException, OntologyException {
        CreateAgent ca = new CreateAgent();
        ca.setAgentName(getToolPrefix()+toolCount);
        ca.setClassName(getToolClass());
        ca.addArguments(arg);
        try {
            ca.setContainer(new ContainerID(launcher.getContainerController().getContainerName(), null));
        } catch (ControllerException e) {
            System.err.println(e.toString());
            e.printStackTrace();
        }
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.setLanguage(launcher.getCodec().getName());
        msg.setOntology(JADEManagementOntology.NAME);
        msg.addReceiver(launcher.getAMS());
        msg.setSender(launcher.getAID());
        msg.setProtocol(InteractionProtocol.FIPA_REQUEST);
        Action contentAction = new Action();
        contentAction.setAction(ca);
        contentAction.setActor(launcher.getAID());
        ContentManager manager = launcher.getContentManager();
        manager.setValidationMode(false);
        manager.fillContent(msg, (ContentElement)contentAction);
        launcher.addAMSBehaviour(new ToolCreateInitiator(msg, result, getToolPrefix(), launcher));
    }
    
    /**
	 * @return The String representation of the tool class
	 */
	protected abstract String getToolClass();

	/**
	 * @return The prefix every tool started through the ASCML should have
	 */
	protected abstract String getToolPrefix();

	private class ToolCreateInitiator extends AchieveREInitiator {
        protected StringBuffer result;
        protected String aName;
        protected AgentLauncher launcher;
        
        public ToolCreateInitiator(ACLMessage request, StringBuffer result, String aName, AgentLauncher launcher) {
            super(launcher, request);
            this.result = result;
            this.aName = aName;
            this.launcher = launcher;
        }
        
        protected void handleNotUnderstood(ACLMessage reply) {
            System.err.println("NOT-UNDERSTOOD received" + reply);
        }
        
        protected void handleRefuse(ACLMessage reply) {
            System.err.println("REFUSE received" + reply);
        }
        
        protected void handleAgree(ACLMessage reply) {
        }
        
        protected void handleFailure(ACLMessage reply) {
            // System.err.println("Failure: "+reply);
            String s = new String(reply.getContent());
            if (s.indexOf("nested") > 0) {
                s = s.substring(s.indexOf("nested"), s.length() - 1);
            } else if (s.indexOf("already-register") > 0) {
                s = aName + " is already registered";
            } else if (s.indexOf("internal-error") > 0) {
                s = s.substring(s.indexOf("internal-error")+"internal-error".length(), s.length() - 2);
            } else {
                System.err.println("\n"+s+"\n");
            }
            synchronized (result) {
                if (s != null) {
                    result.setLength(0);
                    result.append(s);
                }
                result.notifyAll();
            }
            launcher.removeBehaviour(this);
        }
        
        protected void handleInform(ACLMessage reply) {
            synchronized (result) {
                result.setLength(0);
                result.notifyAll();
            }
            launcher.removeBehaviour(this);
            waitForToolCreate = false;
			SwingUtilities.invokeLater(new Runnable() {
				public void run () {
					toolReady();
				}
			});
        }
    }
    
    private class ToolRequestInitiator extends AchieveREInitiator {
        
        private String agents;
        private StringBuffer o;
        
        public ToolRequestInitiator(String agents, ACLMessage request, StringBuffer o) {
            super(launcher, request);
            this.agents = agents;
            this.o = o;
        }
        
        protected void handleNotUnderstood(ACLMessage reply) {
            System.err.println("NOT-UNDERSTOOD received" + reply);
        }
        
        protected void handleRefuse(ACLMessage reply) {
            try {
                newToolAgent(agents, o);
            } catch (Exception e) {
                System.err.println(e);
            }
            toolCount++;
            launcher.removeBehaviour(this);
        }
        
        protected void handleFailure(ACLMessage reply) {
            try {
                newToolAgent(agents,o);
            } catch (Exception e) {
                System.err.println(e);
            }
            toolCount++;
            launcher.removeBehaviour(this);
        }
        
        protected void handleInform(ACLMessage reply) {
            synchronized (o) {
                o.notifyAll();
            }
            launcher.removeBehaviour(this);
            try {
                Done done = (Done)launcher.getContentManager().extractContent(reply);
                Action a = (Action)done.getAction();
                AgentAction aa = (AgentAction)a.getAction();
                if (aa instanceof SniffOn) {
                    SniffOn so = (SniffOn)aa;
                    for (Iterator i = so.getAllSniffedAgents(); i.hasNext();) {
                        AID aid = (AID)i.next();
                        System.out.println(aid.getLocalName());
                        
                        if ( agentTooloptionProperties.containsKey(aid.getLocalName()) ) {
                            sendToolAgentParameters(aid, o);
                        } else {
                            waitingAgents.remove(aid.getLocalName());
                        }
                    }
                }
            } catch (CodecException e) {
                System.err.println("CodecException: "+e);
            } catch (UngroundedException e) {
                System.err.println(e);
            } catch (OntologyException e) {
                System.err.println(e);
            }
        }
    }
    
    private class ToolParameterInitiator extends AchieveREInitiator {
        
        private StringBuffer o;
        
        public ToolParameterInitiator(ACLMessage request, StringBuffer o) {
            super(launcher, request);
            this.o = o;
        }
        
        protected void handleNotUnderstood(ACLMessage reply) {
            System.err.println("NOT-UNDERSTOOD received" + reply);
            launcher.removeBehaviour(this);
        }
        
        protected void handleRefuse(ACLMessage reply) {
            System.err.println("ToolAgent "+getToolClass()+" refused parameter set.");
            launcher.removeBehaviour(this);
        }
        
        protected void handleFailure(ACLMessage reply) {
            System.err.println("ToolAgent "+getToolClass()+" failed on parameter set.");
            launcher.removeBehaviour(this);
        }
        
        protected void handleInform(ACLMessage reply) {
            synchronized (o) {
                o.notifyAll();
            }
            launcher.removeBehaviour(this);
            try {
                Done done = (Done)launcher.getContentManager().extractContent(reply);
                Action a = (Action)done.getAction();
                AgentAction aa = (AgentAction)a.getAction();
                if (aa instanceof ToolAgentParameterSet) {
                    ToolAgentParameterSet ps = (ToolAgentParameterSet)aa;
                    AID aid = ps.getAgentID();
                    agentTooloptionProperties.remove(aid.getLocalName());
                    waitingAgents.remove(aid.getLocalName());
                }
            } catch (CodecException e) {
                System.err.println("CodecException: "+e);
            } catch (UngroundedException e) {
                System.err.println(e);
            } catch (OntologyException e) {
                System.err.println(e);
            }
        }        
    }

    private void sendToolAgentParameters(AID aid, StringBuffer o) {
		IProperty[] toolOptionProperties = agentTooloptionProperties.get(aid.getLocalName());
        if (toolOptionProperties == null) {
            return;
        }

        AID toolAID = new AID(getToolPrefix()+(toolCount-1), AID.ISLOCALNAME);
        Action a = new Action();
        ToolAgentParameterSet ps = new ToolAgentParameterSet();
        ps.setAgentID(aid);
        for (IProperty oneProperty: toolOptionProperties) {
			String type = oneProperty.getName();
			String value = oneProperty.getProperty();
            ps.addParameter(new ToolAgentParameter(type, value));
        }
        a.setAction(ps);
        a.setActor(toolAID);
        ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
        requestMsg.setOntology(ToolAgentParameterSetOntology.ONTOLOGY_NAME);
        requestMsg.setLanguage(launcher.getCodec().getName());
        requestMsg.addReceiver(toolAID);
        requestMsg.setSender(launcher.getAID());
        try {
            launcher.getContentManager().fillContent(requestMsg, a);
            launcher.addBehaviour(new ToolParameterInitiator(requestMsg, o));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    protected void toolReady(){
        while (! (requestAgents.isEmpty() && waitingAgents.isEmpty())) {
            try {
                Iterator<String> iterator = requestAgents.keySet().iterator();
                while(iterator.hasNext()) {
                    String key = iterator.next();
                    StringBuffer synchobject = requestAgents.get(key);
                    internalRequestTool(key, synchobject);
                }
            } catch(ConcurrentModificationException cme) {
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
            }
        }
    }
    
    public void reset() {
        waitingAgents.clear();
        requestAgents.clear();
        agentTooloptionProperties.clear();
        waitForToolCreate = false;
    }
    
    private void sendRequest(String agent, StringBuffer synchobject) {
        AID toolAID = new AID(getToolPrefix()+(toolCount-1), AID.ISLOCALNAME);
        Action a = new Action();
        Concept so = getAction(toolAID,agent);
        a.setAction(so);
        a.setActor(toolAID);
        ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
        requestMsg.setOntology(ontologyName);
        requestMsg.setLanguage(launcher.getCodec().getName());
        requestMsg.addReceiver(toolAID);
        requestMsg.setSender(launcher.getAID());
        try {
            launcher.getContentManager().fillContent(requestMsg, a);
            launcher.addBehaviour(new ToolRequestInitiator(agent, requestMsg, synchobject));
            waitingAgents.put(agent, synchobject);
            requestAgents.remove(agent);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    private void internalRequestTool(String agent, StringBuffer synchobject) {
        if (waitingAgents.containsKey(agent) || waitForToolCreate) {
            return;
        }
        sendRequest(agent, synchobject);
    }
    
    public void requestTool(IRunnableAgentInstance agentModel, StringBuffer synchobject ) {
        if (waitingAgents.containsKey(agentModel.getName())) {
            return;
        }
		IToolOption toolOption = agentModel.getToolOption(getToolOptionName());
		IProperty[] properties = toolOption.getProperties();
        if ((properties != null) && (properties.length > 0)) {
            agentTooloptionProperties.put(agentModel.getName(), properties);
        }

        if( !waitForToolCreate ) {
            if(toolCount == 0) {
                waitForToolCreate = true;
                try {
                    newToolAgent(agentModel.getName(),synchobject);
                    requestAgents.put(agentModel.getName(), synchobject);
                    toolCount += 1;
                } catch(Exception e) {
                    System.err.println(e);
                }
            } else {
                sendRequest(agentModel.getName(), synchobject);
            }
        } else {
            requestAgents.put(agentModel.getName(), synchobject);
        }
    }
	
	/**
	 * 
	 * @param toolAID The AID of the toolagent
	 * @param agent The agent the toolagent should use
	 * @return The action for this toolagent
	 */
	public abstract Concept getAction(AID toolAID, String agent);
	
	/**
	 * 
	 * @return the String identifying the tool option used for this tool
	 */
	public abstract String getToolOptionName();
}
