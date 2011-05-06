/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A.

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
package jade.tools.persistence;

import jade.content.onto.basic.Action;

import jade.core.AID;
import jade.core.ContainerID;
import jade.core.IMTPException;
import jade.core.NotFoundException;
import jade.core.ServiceException;

import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SenderBehaviour;
import jade.core.behaviours.SequentialBehaviour;

import jade.core.persistence.PersistenceHelper;

import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.APDescription;

import jade.domain.introspection.*;

import jade.domain.persistence.*;

import jade.lang.acl.ACLMessage;

import jade.proto.SimpleAchieveREInitiator;

import jade.tools.ToolAgent;

import jade.util.leap.LinkedList;
import jade.util.leap.List;

import java.net.InetAddress;
import java.net.UnknownHostException;

import java.util.Map;
import javax.swing.JOptionPane;


/**
 *
 * @author  Giovanni Rimassa - FRAMeTech s.r.l.
 */
public class PersistenceManagerAgent extends ToolAgent {
    private AgentPlatformWindow myGUI;
    private SequentialBehaviour AMSSubscribe = new SequentialBehaviour();

    public PersistenceManagerAgent(ActionProcessor ap) {
        // Create Graphical User Interface
        myGUI = new AgentPlatformWindow(this, ap);
    }

    public AgentPlatformWindow getGUI() {
        return myGUI;
    }

    // External method; called by the GUI
    public void fetchRepositories() {
        addBehaviour(new OneShotBehaviour() {
                public void action() {
                    try {
                        // Retrieve the list of the repositories
                        PersistenceHelper helper = (PersistenceHelper) getHelper(PersistenceHelper.NAME);
                        String[] nodes = helper.getNodes();
                        List repositoriesList = new LinkedList();

                        for (int i = 0; i < nodes.length; i++) {
                            String[] repositories = helper.getRepositories(nodes[i]);
                            repositoriesList.add(repositories);
                        }

                        // Notify the GUI about it
                        myGUI.repositoriesFetched(nodes, repositoriesList);
                    } catch (ServiceException se) {
                        myGUI.fetchRepositoriesFailed(se);
                    } catch (IMTPException imtpe) {
                        myGUI.fetchRepositoriesFailed(imtpe);
                    } catch (NotFoundException nfe) {
                        myGUI.fetchRepositoriesFailed(nfe);
                    }
                }
            });
    }

    // External method; called by the GUI
    public void readRepository(final String nodeName,
        final String repositoryName) {
        addBehaviour(new OneShotBehaviour() {
                public void action() {
                    try {
                        // Retrieve the list of the repositories
                        PersistenceHelper helper = (PersistenceHelper) getHelper(PersistenceHelper.NAME);
                        String[] savedAgents = helper.getSavedAgents(nodeName,
                                repositoryName);
                        String[] frozenAgents = helper.getFrozenAgents(nodeName,
                                repositoryName);
                        String[] savedContainers = helper.getSavedContainers(nodeName,
                                repositoryName);

                        // Notify the GUI about it
                        myGUI.repositoryRead(savedAgents, frozenAgents,
                            savedContainers);
                    } catch (ServiceException se) {
                        myGUI.readRepositoryFailed(se);
                    } catch (IMTPException imtpe) {
                        myGUI.readRepositoryFailed(imtpe);
                    } catch (NotFoundException nfe) {
                        myGUI.readRepositoryFailed(nfe);
                    }
                }
            });
    }

    // External method, called from the GUI
    public void reloadAgent(AID agentID, String repository) {
    	if (getAID().equals(agentID)) {
    		JOptionPane.showMessageDialog(myGUI, "The PersistenceManagerAgent cannot be reloaded");
    	}
    	else {
	        ReloadAgent reloadAct = new ReloadAgent();
	        reloadAct.setAgent(agentID);
	        reloadAct.setRepository(repository);
	
	        try {
	            Action a = new Action();
	            a.setActor(getAMS());
	            a.setAction(reloadAct);
	
	            ACLMessage requestMsg = getRequest();
	            requestMsg.setOntology(PersistenceVocabulary.NAME);
	            getContentManager().fillContent(requestMsg, a);
	            addBehaviour(new AMSClientBehaviour("ReloadAgent", requestMsg));
	        } catch (Exception fe) {
	            fe.printStackTrace();
	        }
    	}
    }

    // External method, called from the GUI
    public void saveAgent(AID agentID, String repository) {
    	if (getAID().equals(agentID)) {
    		JOptionPane.showMessageDialog(myGUI, "The PersistenceManagerAgent cannot be saved");
    	}
    	else {
	        SaveAgent saveAct = new SaveAgent();
	        saveAct.setAgent(agentID);
	        saveAct.setRepository(repository);
	
	        try {
	            Action a = new Action();
	            a.setActor(getAMS());
	            a.setAction(saveAct);
	
	            ACLMessage requestMsg = getRequest();
	            requestMsg.setOntology(PersistenceVocabulary.NAME);
	            getContentManager().fillContent(requestMsg, a);
	            addBehaviour(new AMSClientBehaviour("SaveAgent", requestMsg));
	        } catch (Exception fe) {
	            fe.printStackTrace();
	        }
    	}
    }

    // External method, called from the GUI
    public void saveContainer(String containerName, String repository) {
    	if (here().getName().equals(containerName)) {
    		JOptionPane.showMessageDialog(myGUI, "The PersistenceManagerAgent container cannot be saved");
    	}
    	else {
	        SaveContainer saveAct = new SaveContainer();
	        saveAct.setContainer(new ContainerID(containerName, null));
	        saveAct.setRepository(repository);
	
	        try {
	            Action a = new Action();
	            a.setActor(getAMS());
	            a.setAction(saveAct);
	
	            ACLMessage requestMsg = getRequest();
	            requestMsg.setOntology(PersistenceVocabulary.NAME);
	            getContentManager().fillContent(requestMsg, a);
	            addBehaviour(new AMSClientBehaviour("SaveContainer", requestMsg));
	        } catch (Exception fe) {
	            fe.printStackTrace();
	        }
    	}
    }

    // External method, called from the GUI
    public void loadAgent(AID agentID, String repository, String containerName) {
    	if (getAID().equals(agentID)) {
    		JOptionPane.showMessageDialog(myGUI, "The PersistenceManagerAgent cannot be loaded");
    	}
    	else {
	        LoadAgent loadAct = new LoadAgent();
	        loadAct.setAgent(agentID);
	        loadAct.setRepository(repository);
	
	        ContainerID where = new ContainerID(containerName, null);
	        loadAct.setWhere(where);
	
	        try {
	            Action a = new Action();
	            a.setActor(getAMS());
	            a.setAction(loadAct);
	
	            ACLMessage requestMsg = getRequest();
	            requestMsg.setOntology(PersistenceVocabulary.NAME);
	            getContentManager().fillContent(requestMsg, a);
	            addBehaviour(new AMSClientBehaviour("LoadAgent", requestMsg));
	        } catch (Exception fe) {
	            fe.printStackTrace();
	        }
    	}
    }

    // External method, called from the GUI
    public void loadContainer(String containerName, String repository) {
    	if (here().getName().equals(containerName)) {
    		JOptionPane.showMessageDialog(myGUI, "The PersistenceManagerAgent container cannot be loaded");
    	}
    	else {
	        LoadContainer loadAct = new LoadContainer();
	        loadAct.setContainer(new ContainerID(containerName, null));
	        loadAct.setRepository(repository);
	
	        try {
	            Action a = new Action();
	            a.setActor(getAMS());
	            a.setAction(loadAct);
	
	            ACLMessage requestMsg = getRequest();
	            requestMsg.setOntology(PersistenceVocabulary.NAME);
	            getContentManager().fillContent(requestMsg, a);
	            addBehaviour(new AMSClientBehaviour("LoadContainer", requestMsg));
	        } catch (Exception fe) {
	            fe.printStackTrace();
	        }
    	}
    }

    // External method, called from the GUI
    public void deleteAgent(AID agentID, String containerName, String repository) {
        ContainerID where = new ContainerID(containerName, null);
        DeleteAgent deleteAct = new DeleteAgent();
        deleteAct.setAgent(agentID);
        deleteAct.setWhere(where);
        deleteAct.setRepository(repository);

        try {
            Action a = new Action();
            a.setActor(getAMS());
            a.setAction(deleteAct);

            ACLMessage requestMsg = getRequest();
            requestMsg.setOntology(PersistenceVocabulary.NAME);
            getContentManager().fillContent(requestMsg, a);
            addBehaviour(new AMSClientBehaviour("DeleteAgent", requestMsg));
        } catch (Exception fe) {
            fe.printStackTrace();
        }
    }

    // External method, called from the GUI
    public void deleteContainer(String containerName, String whereName, String repository) {
        ContainerID cid = new ContainerID(containerName, null);
        ContainerID where = new ContainerID(whereName, null);
        DeleteContainer deleteAct = new DeleteContainer();
        deleteAct.setContainer(cid);
        deleteAct.setWhere(where);
        deleteAct.setRepository(repository);

        try {
            Action a = new Action();
            a.setActor(getAMS());
            a.setAction(deleteAct);

            ACLMessage requestMsg = getRequest();
            requestMsg.setOntology(PersistenceVocabulary.NAME);
            getContentManager().fillContent(requestMsg, a);
            addBehaviour(new AMSClientBehaviour("DeleteContainer", requestMsg));
        } catch (Exception fe) {
            fe.printStackTrace();
        }
    }

    // External method, called from the GUI
    public void freezeAgent(AID agentID, String whereName, String repository) {
    	if (getAID().equals(agentID)) {
    		JOptionPane.showMessageDialog(myGUI, "The PersistenceManagerAgent cannot be freezed");
    	}
    	else {
	        ContainerID where = new ContainerID(whereName, null);
	
	        FreezeAgent freezeAct = new FreezeAgent();
	        freezeAct.setAgent(agentID);
	        freezeAct.setBufferContainer(where);
	        freezeAct.setRepository(repository);
	
	        try {
	            Action a = new Action();
	            a.setActor(getAMS());
	            a.setAction(freezeAct);
	
	            ACLMessage requestMsg = getRequest();
	            requestMsg.setOntology(PersistenceVocabulary.NAME);
	            getContentManager().fillContent(requestMsg, a);
	            addBehaviour(new AMSClientBehaviour("FreezeAgent", requestMsg));
	        } catch (Exception fe) {
	            fe.printStackTrace();
	        }
    	}
    }

    // External method, called from the GUI
    public void thawAgent(AID agentID, String whereName, String repository) {
    	if (getAID().equals(agentID)) {
    		JOptionPane.showMessageDialog(myGUI, "The PersistenceManagerAgent cannot be thawn");
    	}
    	else {
	        ContainerID where = new ContainerID(whereName, null);
	
	        ThawAgent thawAct = new ThawAgent();
	        thawAct.setAgent(agentID);
	        thawAct.setRepository(repository);
	        thawAct.setNewContainer(where);
	
	        try {
	            Action a = new Action();
	            a.setActor(getAMS());
	            a.setAction(thawAct);
	
	            ACLMessage requestMsg = getRequest();
	            requestMsg.setOntology(PersistenceVocabulary.NAME);
	            getContentManager().fillContent(requestMsg, a);
	            addBehaviour(new AMSClientBehaviour("ThawAgent", requestMsg));
	        } catch (Exception fe) {
	            fe.printStackTrace();
	        }
    	}
    }

    /**
        This method starts the agent behaviours to allow the agent
        to carry on its duties within <em><b>JADE</b></em> agent platform.
    */
    protected void toolSetup() {
        // Register the supported ontologies
        getContentManager().registerOntology(PersistenceOntology.getInstance());

        // Send 'subscribe' message to the AMS
        AMSSubscribe.addSubBehaviour(new SenderBehaviour(this, getSubscribe()));

        // Handle incoming 'inform' messages
        AMSSubscribe.addSubBehaviour(new MyAMSListenerBehaviour());

        // Schedule Behaviour for execution
        addBehaviour(AMSSubscribe);
    }

    /**
        Cleanup during agent shutdown. This method cleans things up when
        the agent is destroyed, disconnecting from <em>AMS</em>
        agent and closing down the persistence administration <em>GUI</em>.
    */
    protected void toolTakeDown() {
        send(getCancel());
    }

    private class AMSClientBehaviour extends SimpleAchieveREInitiator {
        private String actionName;

        public AMSClientBehaviour(String an, ACLMessage request) {
            super(PersistenceManagerAgent.this, request);
            actionName = an;
        }

        protected void handleNotUnderstood(ACLMessage reply) {
            myGUI.showErrorDialog("NOT-UNDERSTOOD received during " +
                actionName, reply);
        }

        protected void handleRefuse(ACLMessage reply) {
            myGUI.showErrorDialog("REFUSE received during " + actionName, reply);
        }

        protected void handleAgree(ACLMessage reply) {
            // System.out.println("AGREE received" + reply);
        }

        protected void handleFailure(ACLMessage reply) {
            myGUI.showErrorDialog("FAILURE received during " + actionName, reply);
        }

        protected void handleInform(ACLMessage reply) {
            // Update the Repository GUI
            fetchRepositories();
        }
    } // End of AMSClientBehaviour class

    class MyAMSListenerBehaviour extends AMSListenerBehaviour {
        protected void installHandlers(Map handlersTable) {
            // Fill the event handler table.
            handlersTable.put(IntrospectionVocabulary.META_RESETEVENTS,
                new EventHandler() {
                    public void handle(Event ev) {
                        ResetEvents re = (ResetEvents) ev;
                        myGUI.resetTree();
                    }
                });

            handlersTable.put(IntrospectionVocabulary.ADDEDCONTAINER,
                new EventHandler() {
                    public void handle(Event ev) {
                        AddedContainer ac = (AddedContainer) ev;
                        ContainerID cid = ac.getContainer();
                        String name = cid.getName();
                        String address = cid.getAddress();

                        try {
                            InetAddress addr = InetAddress.getByName(address);
                            myGUI.addContainer(name, addr);
                        } catch (UnknownHostException uhe) {
                            myGUI.addContainer(name, null);
                        }
                    }
                });

            handlersTable.put(IntrospectionVocabulary.REMOVEDCONTAINER,
                new EventHandler() {
                    public void handle(Event ev) {
                        RemovedContainer rc = (RemovedContainer) ev;
                        ContainerID cid = rc.getContainer();
                        String name = cid.getName();
                        myGUI.removeContainer(name);
                    }
                });

            handlersTable.put(IntrospectionVocabulary.BORNAGENT,
                new EventHandler() {
                    public void handle(Event ev) {
                        BornAgent ba = (BornAgent) ev;
                        ContainerID cid = ba.getWhere();
                        String container = cid.getName();
                        AID agent = ba.getAgent();
                        myGUI.addAgent(container, agent);
                        myGUI.modifyAgent(container, agent, ba.getState(),
                            ba.getOwnership());
                    }
                });

            handlersTable.put(IntrospectionVocabulary.DEADAGENT,
                new EventHandler() {
                    public void handle(Event ev) {
                        DeadAgent da = (DeadAgent) ev;
                        ContainerID cid = da.getWhere();
                        String container = cid.getName();
                        AID agent = da.getAgent();
                        myGUI.removeAgent(container, agent);
                    }
                });

            handlersTable.put(IntrospectionVocabulary.SUSPENDEDAGENT,
                new EventHandler() {
                    public void handle(Event ev) {
                        SuspendedAgent sa = (SuspendedAgent) ev;
                        ContainerID cid = sa.getWhere();
                        String container = cid.getName();
                        AID agent = sa.getAgent();
                        myGUI.modifyAgent(container, agent,
                            AMSAgentDescription.SUSPENDED, null);
                    }
                });

            handlersTable.put(IntrospectionVocabulary.RESUMEDAGENT,
                new EventHandler() {
                    public void handle(Event ev) {
                        ResumedAgent ra = (ResumedAgent) ev;
                        ContainerID cid = ra.getWhere();
                        String container = cid.getName();
                        AID agent = ra.getAgent();
                        myGUI.modifyAgent(container, agent,
                            AMSAgentDescription.ACTIVE, null);
                    }
                });

            handlersTable.put(IntrospectionVocabulary.FROZENAGENT,
                new EventHandler() {
                    public void handle(Event ev) {
                        FrozenAgent fa = (FrozenAgent) ev;
                        String oldContainer = fa.getWhere().getName();
                        String newContainer = fa.getBufferContainer().getName();
                        AID agent = fa.getAgent();
                        myGUI.modifyFrozenAgent(oldContainer, newContainer,
                            agent);
                    }
                });

            handlersTable.put(IntrospectionVocabulary.THAWEDAGENT,
                new EventHandler() {
                    public void handle(Event ev) {
                        ThawedAgent ta = (ThawedAgent) ev;
                        String oldContainer = ta.getWhere().getName();
                        String newContainer = ta.getBufferContainer().getName();
                        AID agent = ta.getAgent();
                        myGUI.modifyThawedAgent(oldContainer, newContainer,
                            agent);
                    }
                });

            handlersTable.put(IntrospectionVocabulary.MOVEDAGENT,
                new EventHandler() {
                    public void handle(Event ev) {
                        MovedAgent ma = (MovedAgent) ev;
                        AID agent = ma.getAgent();
                        ContainerID from = ma.getFrom();
                        ContainerID to = ma.getTo();
                        myGUI.moveAgent(from.getName(), to.getName(), agent);
                    }
                });

            //handle the APDescription provided by the AMS
            handlersTable.put(IntrospectionVocabulary.PLATFORMDESCRIPTION,
                new EventHandler() {
                    public void handle(Event ev) {
                        PlatformDescription pd = (PlatformDescription) ev;
                        APDescription desc = pd.getPlatform();
                        myGUI.refreshLocalPlatformName(desc.getName());
                    }
                });
        }
    } // END of inner class MyAMSListenerBehaviour
      // Creates a new instance of PersistenceManager 
}
