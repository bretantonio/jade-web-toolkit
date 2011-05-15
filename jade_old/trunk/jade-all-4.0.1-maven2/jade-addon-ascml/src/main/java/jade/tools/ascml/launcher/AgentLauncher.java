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

import jade.content.abs.AbsConcept;
import jade.content.abs.AbsIRE;
import jade.content.abs.AbsPredicate;
import jade.content.abs.AbsVariable;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SL2Vocabulary;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.OntologyException;
import jade.core.AID;
import jade.core.behaviours.SenderBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.AMSService;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.introspection.BornAgent;
import jade.domain.introspection.DeadAgent;
import jade.domain.introspection.Event;
import jade.domain.introspection.IntrospectionVocabulary;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREInitiator;
import jade.tools.ToolAgent;
import jade.tools.ascml.absmodel.IRunnableAgentInstance;
import jade.tools.ascml.absmodel.IRunnableRemoteSocietyInstanceReference;
import jade.tools.ascml.absmodel.IToolOption;
import jade.tools.ascml.absmodel.dependency.IDependency;
import jade.tools.ascml.dependencymanager.DependencyManager;
import jade.tools.ascml.events.ModelChangedListener;
import jade.tools.ascml.gui.GUI;
import jade.tools.ascml.launcher.remoteactions.*;
import jade.tools.ascml.launcher.remotestatus.*;
import jade.tools.ascml.launcher.toolRequesters.*;
import jade.tools.ascml.onto.*;
import jade.tools.ascml.onto.Error;
import jade.tools.ascml.repository.Repository;
import jade.util.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

/**
 * This is the main class of the ASCML.
 * @author Sven Lilienthal (ascml@sven-lilienthal.de)
 */
public class AgentLauncher extends ToolAgent {

	public final static String ASCML_VERSION = "0.9";
    
	protected Codec codec = new SLCodec(2);

    protected Repository repository;
    protected GUI gui;
	protected LauncherInterface li;

	private HashMap<String, ToolRequester> toolMap;
    private ListenerManagerInterface lmi;

	private StatusSubscriptionManager subscriptionManager = new StatusSubscriptionManager(this);
	private DependencyManager myDependencyManager;
	
	//TODO: Use the logger
	public Logger myLogger = Logger.getMyLogger(this.getClass().getName());
	public static String ASCML_NAME = "";

	protected HashMap<IRunnableRemoteSocietyInstanceReference,StatusSubscriptionInitiator> remoteSocietySubscriptions;
	
    /**
     * Listens to platform evens like DEADAGENT,BORNAGENT,...
     * We only care about DEADAGENT and use it to notify the dependency manager
     */
    protected class platformEventListener extends AMSListenerBehaviour {

        protected void installHandlers(Map handlersTable) {			
            handlersTable.put(IntrospectionVocabulary.DEADAGENT, new ToolAgent.EventHandler() {
				public void handle(Event ev) {
					DeadAgent da = (DeadAgent) ev;
					AID agent = da.getAgent();
					myDependencyManager.agentDied(agent);
				}
			});      
			handlersTable.put(IntrospectionVocabulary.BORNAGENT, new ToolAgent.EventHandler() {
				public void handle(Event ev) {
					BornAgent ba = (BornAgent) ev;
					AID agent = ba.getAgent();
					myDependencyManager.agentBorn(agent);
				}
			});             
        }

    } // END of inner class plattformEventListener

    /**
     * Tries to get the status of the remote society
     * if it isn't starting or functional it will be started
     * @param remoteSociety The IRunnableRemoteSocietyInstanceReference to test and start
     */
	public void inquirerAndStartRemoteSociety(IRunnableRemoteSocietyInstanceReference remoteSociety) {
		//First we create a new listener, which will check the status updates of the remote society
		ModelChangedListener inqAndStart = new RemoteInquirerAndStarter(this,remoteSociety);
		getRepository().getListenerManager().addModelChangedListener(inqAndStart);
		
		try {
			//Now we will construct the required message and send it
			AID receiver = new AID(remoteSociety.getLauncher().getName(), AID.ISGUID);
			SocietyInstance newsoc = new SocietyInstance();
			newsoc.setFullQuallifiedName(remoteSociety.getFullyQualifiedName());
			receiver.addAddresses(remoteSociety.getLauncher().getAddresses()[0]);
			ACLMessage message = createSubscription(receiver, newsoc);
			message.setPerformative(ACLMessage.QUERY_REF);
			message.setProtocol(FIPANames.InteractionProtocol.FIPA_QUERY);
			RemoteStatusInquirer rsi = new RemoteStatusInquirer(message, this, remoteSociety);
			addBehaviour(rsi);
		}
		catch (Exception e) {
			remoteSociety.setDetailedStatus(e.getMessage());
			remoteSociety.setStatus(new Error());
			e.printStackTrace();
		}
	}
	
	/**
     * adds a Behaviour which attempts to start a society on a remote plattform
     */
	protected void startRemoteSociety(IRunnableRemoteSocietyInstanceReference remoteSociety) {
		// If there are no adresses, then I just don't care.
		// If there are multiple adresses, I don't know what to do
		// What has to be done is yet to be defined
		String[] launcherAdresses = remoteSociety.getLauncher().getAddresses();
		String launcherName = remoteSociety.getLauncher().getName();
		AID launcherAID = new AID(launcherName,AID.ISGUID);
		launcherAID.addAddresses(launcherAdresses[0]);

		//If we start a remote society we want to be kept informed about its status
		AbsModel absSociety = new AbsModel();
		absSociety.setFullQuallifiedName(remoteSociety.getFullyQualifiedName());
		ACLMessage subscriptionMessage=null;
		try {
			subscriptionMessage=createSubscription(launcherAID, absSociety);
			StatusSubscriptionInitiator ssi = new StatusSubscriptionInitiator(this,subscriptionMessage,remoteSociety);
			addBehaviour(ssi);
			remoteSocietySubscriptions.put(remoteSociety,ssi);
		}
		catch (CodecException e1) {
			e1.printStackTrace();
			ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
			message.setLanguage(FIPANames.ContentLanguage.FIPA_SL);
			message.setOntology(ASCMLOntology.ONTOLOGY_NAME);
			message.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
			message.addReceiver(launcherAID);

			// Now we fill in the message content
			jade.content.onto.basic.Action contentAction = new jade.content.onto.basic.Action();
			Start start = new Start();
			//TODO: Check FIPA-standard: which one should we use?
			contentAction.setActor(launcherAID);
			start.setActor(launcherAID);
			SocietyInstance newsoc = new SocietyInstance();
			newsoc.setFullQuallifiedName(remoteSociety.getFullyQualifiedName());		
			start.addModels(newsoc);
			contentAction.setAction(start);
			try {
				getContentManager().setValidationMode(true);
				getContentManager().fillContent(message, contentAction);
				RemoteStarterBehaviour rsb = new RemoteStarterBehaviour(this, message, subscriptionMessage);
		        addBehaviour(rsb);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		catch (OntologyException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    }
	
	/**
     * adds a Behaviour which attempts to stop a society on a remote plattform
     * @param remoteSociety The IRunnableRemoteSocietyInstanceReference to stop
     */	
	public void stopRemoteSociety(IRunnableRemoteSocietyInstanceReference remoteSociety) {
		// If there are no adresses, then I just don't care.
		// If there are multiple adresses, I don't know what to do
		// What has to be done is yet to be defined
		String[] launcherAdresses = remoteSociety.getLauncher().getAddresses();
		String launcherName = remoteSociety.getLauncher().getName();
		AID launcherAID = new AID(launcherName, AID.ISGUID);
		launcherAID.addAddresses(launcherAdresses[0]);

		ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
		message.setLanguage(FIPANames.ContentLanguage.FIPA_SL);
		message.setOntology(ASCMLOntology.ONTOLOGY_NAME);
		message.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
		message.addReceiver(launcherAID);

		// Now we fill in the message content
		jade.content.onto.basic.Action contentAction = new jade.content.onto.basic.Action();
		Stop stop = new Stop();
		// TODO: Check FIPA-standard: which one should we use?
		contentAction.setActor(launcherAID);
		stop.setActor(launcherAID);
		SocietyInstance newsoc = new SocietyInstance();
		newsoc.setFullQuallifiedName(remoteSociety.getFullyQualifiedName());
		stop.addModels(newsoc);
		contentAction.setAction(stop);
		try {
			getContentManager().setValidationMode(true);
			getContentManager().fillContent(message, contentAction);
			RemoteStopperBehaviour rsb = new RemoteStopperBehaviour(this, message, remoteSocietySubscriptions.get(remoteSociety));
			addBehaviour(rsb);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void startRemoteDependency(IDependency remoteDep) {
		//FIXME: Start remote dependencies
	}

	/**
	 * @param option The tooloption, identfiing the tool
     * @param agentModel
     *            Name of the agent the tool will be used with
     * @param synchobject
     *            Object to be notified when the Introspector answers
	 */
	public void doToolOption(IToolOption option, IRunnableAgentInstance model, StringBuffer synchobject) {
		ToolRequester tool = toolMap.get(option.getType());
		if (tool == null) {
			System.err.println("Tooloption "+option.getType()+" not implemented!");			
		} else {
			tool.requestTool(model,synchobject);
		}
	}
    /**
     * @param msg
     *            The message sent to the AMS
     * @param result
     *            The added behaviour calls result.notfiyAll() when a inform or a failure message arrives.            
     *            Afterwords tt cointains the failure message or it is empty if the answer was an inform.
     * @param aName
     *            identifier for the request
     */
    protected void addAMSBehaviour(ACLMessage msg, StringBuffer result, String aName) {
        addBehaviour(new AMSClientBehaviour(msg, result, aName));
    }

    public void addAMSBehaviour(AchieveREInitiator initiator) {
        addBehaviour(initiator);
    }

    protected void clearMaps() {
        // Clean up:
		for (String key : toolMap.keySet()) {
			ToolRequester tr = toolMap.get(key);
			tr.reset();
		}
    }
    
    protected void toolSetup() {
		
		// arguments they may have been passed to the ASCML at startup		
		boolean noGUI = false;
		String propertyFileLocation = "";

        // check if arguments have been passed
        Object[] args = this.getArguments();

		for (int i=0; (args != null) && (i < args.length); i++)
		{
			if (((String)args[i]).equalsIgnoreCase("nogui"))
				noGUI = true;
			else if (((String)args[i]).equalsIgnoreCase("log-warning"))
				myLogger.setLevel(Level.WARNING);
			else if (((String)args[i]).equalsIgnoreCase("log-info"))
				myLogger.setLevel(Level.INFO);
			else
				propertyFileLocation = ((String)args[i]).trim();
		}

		if (noGUI == false)
        	System.out.println("ASCML Version " + ASCML_VERSION);
		else
			System.out.println("ASCML Version " + ASCML_VERSION + " (without GUI)");

        if (!propertyFileLocation.equals(""))
            System.out.println("ASCML properties: " + propertyFileLocation);
        ASCML_NAME = this.getName();
		System.out.println("ASCML running as: " + ASCML_NAME);
        Iterator addr = this.getAMS().getAllAddresses();
        while (addr.hasNext()) {
            System.err.println("ASCML running on: " + addr.next());
        }

        (this.getContentManager()).registerLanguage(codec);
        (this.getContentManager()).registerOntology(jade.domain.JADEAgentManagement.JADEManagementOntology.getInstance());
        (this.getContentManager()).registerOntology(ASCMLOntology.getInstance());	

        /** Registration with the DF */

        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("ASCML");
        sd.setName(getName());
        sd.setOwnership("RWTHAachenAndUniHamburg");
        dfd.setName(getAID());
        dfd.addServices(sd);
        try {
            DFAgentDescription actualDfd = DFService.register(this, getDefaultDF(), dfd);
//            actualDfd.setLeaseTime(new Date(20000));
//            Date lease = actualDfd.getLeaseTime();
//            System.out.println("Lease: " + lease.toString());
            DFService.keepRegistered(this, getDefaultDF(), actualDfd, null);
        } catch (FIPAException e) {
            System.err.println(getLocalName() + " registration with the DF unsucceeded. Reason: " + e.getMessage());
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
		
        MessageTemplate template2 = MessageTemplate.MatchOntology(ASCMLOntology.ONTOLOGY_NAME);
		template2 = MessageTemplate.and(template2, MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_QUERY));
		template2 = MessageTemplate.and(template2, MessageTemplate.MatchPerformative(ACLMessage.QUERY_REF));
        addBehaviour(new RemoteStatusResponder(this, template2));
                
        li = new LauncherInterface(this);
        lmi = new ListenerManagerInterface(this);

        repository = new Repository();
        repository.getListenerManager().addExceptionListener(lmi); // AgentLauncher now has to implement exceptionThrown-method (see below)
        repository.getListenerManager().addToolTakeDownListener(lmi); // AgentLauncher now has to implement toolTakeDown-method (see below)
		repository.getListenerManager().addModelChangedListener(lmi); // AgentLauncher now has to implement modelChanged-method (see below)
        repository.getListenerManager().addModelActionListener(li);
        repository.getListenerManager().addLongTimeActionStartListener(lmi);

		myDependencyManager = new DependencyManager(this);
		repository.getListenerManager().addModelChangedListener(myDependencyManager);		

		if (!noGUI)
			gui = new GUI(repository);

        // toDO: when user presses cancel&quit in the propertyChoose-Dialog init
        // doesn't need to be called
        repository.init(propertyFileLocation);

		if (!noGUI)
        	gui.showMainApplicationGUI(getName());
        else
			System.err.println(repository.getModelIndex());
		
		remoteSocietySubscriptions = new HashMap<IRunnableRemoteSocietyInstanceReference,StatusSubscriptionInitiator>();
		
		toolMap = new HashMap<String, ToolRequester>();
		toolMap.put(IToolOption.TOOLOPTION_BENCHMARK,new BenchmarkerRequester(this));
		toolMap.put(IToolOption.TOOLOPTION_INTROSPECTOR,new IntrospectorRequester(this));
		toolMap.put(IToolOption.TOOLOPTION_SNIFF,new SnifferRequester(this));
		
		/*try
		{
			int randomNumber = (int)(Math.random()*1000) % 1000;
			System.err.println("AgentLauncher: Output redirected to file " + randomNumber + "_[error|out].txt... ");
			System.setErr(new java.io.PrintStream(new java.io.FileOutputStream(new java.io.File(randomNumber + "_error.txt"), true)));
			System.setOut(new java.io.PrintStream(new java.io.FileOutputStream(new java.io.File(randomNumber + "_out.txt"), true)));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}*/
    }


    /**
     * Get the repository-object. The repository-object allows accessing the
     * agent- and society-types.
     * 
     * @return The Repository-object.
     */
    public Repository getRepository() {
        return this.repository;
    }

    /**
     * Every toolAgent has to implement the toolTakeDown method. It is called
     * like agent.takeDown(), when an agent is killed via the RMA-GUI or when
     * the plattform is going down.
     * 
     * @see jade.core.Agent#takeDown()
     */
    protected void toolTakeDown() {
        send(getCancel());
        try {
            DFService.deregister(this);
//            System.err.println("AgentLauncher.toolTakeDown: ASCML deregistered @ DF");
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        repository.exit();
        gui.exit();
		try {
			AMSService.deregister(this);
		}
		catch (FIPAException e) {
			e.printStackTrace();
		}
        super.toolTakeDown();
        //System.err.println("ASCML shutdown completed.");
    }

    static {
        // Allow debugging outputs to be printed.
        System.setErr(System.out);
    }

    /**
     * This behavoiur sends messages to the AMS and gets its answers in return.
     * Whoever added this vis addAMSBehvious is informed vis result.notifyAll() 
     *
     */
    private class AMSClientBehaviour extends AchieveREInitiator {
        protected StringBuffer result;

        protected String aName;

        public AMSClientBehaviour(ACLMessage request, StringBuffer result, String aName) {
            super(AgentLauncher.this, request);
            this.result = result;
            this.aName = aName;
            // System.err.println("AMSClientBehaviour.constructor");
            // System.out.println("AMS BEHAVIOUR STARTED: " + aName);
        }

        protected void handleNotUnderstood(ACLMessage reply) {
            System.err.println("AMSClientBehaviour: NOT-UNDERSTOOD received" + reply);
        }

        protected void handleRefuse(ACLMessage reply) {
            System.err.println("AMSClientBehaviour: REFUSE received" + reply);
        }

        protected void handleAgree(ACLMessage reply) {
            System.err.println("AMSClientBehaviour: AGREE received" + reply);
        }

        protected void handleFailure(ACLMessage reply) {
            System.err.println("AMSClientBehaviour: FAILURE received" + reply);
            // System.err.println("Failure: "+reply);
            String s = new String(reply.getContent());
            if (s.indexOf("nested") > 0) {
                s = s.substring(s.indexOf("nested"), s.length() - 1);
            } else if (s.indexOf("already-register") > 0) {
                s = aName + " is already registered";
            } else if (s.indexOf("internal-error") > 0) {
                s = s.substring(s.indexOf("internal-error") + "internal-error".length(), s.length() - 2);
            } else {
                System.err.println("\n" + s + "\n");
            }
            synchronized (result) {
                if (s != null) {
                    result.setLength(0);
                    result.append(s);
                }
                result.notifyAll();
            }
            removeBehaviour(this);
        }

        protected void handleInform(ACLMessage Inform) {
            // System.err.println("AMSClientBehaviour: INFORM received" +
            // reply);
            synchronized (result) {
                result.setLength(0);
                result.notifyAll();
            }
            removeBehaviour(this);
        }

    }

    /**
     * Retrieve the ListenerManagerInterface of the ASMCL
     * @return The ListenerManagerInterface of the ASCML
     */
    protected ListenerManagerInterface getlmi() {
        return lmi;
    }

	protected StatusSubscriptionManager getSubscriptionManager() {
		return subscriptionManager;
	}
	
	
	/**
	 * Extracts the Status Object from an Inform-Message received from an ASMCL
	 * @param msg
	 * 	The ACLMessage received from an ASCML  
	 * @return
	 * 	The Status Object included in the message
	 * @throws CodecException
	 * @throws OntologyException
	 */
	public Status getStatusFromInform(ACLMessage msg) throws CodecException, OntologyException {
		AbsPredicate absEQ = (AbsPredicate) getContentManager().extractAbsContent(msg);
		AbsConcept absS = (AbsConcept) absEQ.getAbsObject("right");
		Status status = (Status)ASCMLOntology.getInstance().toObject(absS);		
		return status;
	}
	
	/**
	 * Extracts the full-quallified name of the Model from an Inform-Message received from an ASMCL
	 * @param msg
	 * 	The ACLMessage received from an ASCML  
	 * @return
	 * 	The full-quallified name of the Model, the inform-ref refers to
	 * @throws CodecException
	 * @throws OntologyException
	 */	
	public String getFQNfromInform(ACLMessage msg) throws CodecException, OntologyException {
		String fqn="";
		AbsPredicate absOuterEQ = (AbsPredicate) getContentManager().extractAbsContent(msg);
		AbsIRE absIota = (AbsIRE) absOuterEQ.getAbsObject("left");
		AbsPredicate absInnerEQ = absIota.getProposition();
		AbsConcept absModel = (AbsConcept) absInnerEQ.getAbsObject("left");
		fqn = absModel.getString("Name");				
		return fqn;
	}
		
	/**
	 * Creates a subscription message which can be used to subscribe to an ASCML.
	 * If you want to use it to just get the status of a model, you have to change
	 * the performative and the protocol
	 * @param ascml
	 * 	The ascml to adress the message to
	 * @param model
	 *  The model to subcribe to
	 * @return  
	 * 	The ACLMessage to send away to start a subscription
	 * @throws CodecException
	 * @throws OntologyException
	 */
	public ACLMessage createSubscription(AID ascml, AbsModel model) throws CodecException, OntologyException {
		ACLMessage msg = new ACLMessage(ACLMessage.SUBSCRIBE);		
		msg.setProtocol(FIPANames.InteractionProtocol.FIPA_SUBSCRIBE);		
		msg.setLanguage(codec.getName());
		msg.setOntology(ASCMLOntology.ONTOLOGY_NAME);	
		msg.addReceiver(ascml);
		
		AbsIRE absIota = new AbsIRE(SL2Vocabulary.IOTA);
		absIota.setVariable(new AbsVariable("x",ASCMLOntology.STATUS));
		AbsConcept absModel = (AbsConcept) ASCMLOntology.getInstance().fromObject(model);
		absModel.set("Name",model.getFullQuallifiedName());
		absModel.set("ModelStatus",new AbsVariable("x",ASCMLOntology.STATUS));			
		AbsPredicate abseq = new AbsPredicate(SL2Vocabulary.EQUALS);
		abseq.set("left",absModel);
		abseq.set("right",new AbsVariable("x",ASCMLOntology.STATUS));
		absIota.setProposition(abseq);		
		
		getContentManager().fillContent(msg,absIota);
		
		return msg;
		/*
		 * This is an example of how to answer these messages:
		 *			AbsPredicate abseq2 = new AbsPredicate(SL2Vocabulary.EQUALS);
		 *			abseq2.set("left",absIota);
		 *			Status stat = new Available();
		 *			AbsConcept absStat = (AbsConcept) ASCMLOntology.getInstance().fromObject(stat);
		 *			abseq2.set("right",absStat);
		 *			
		 *			getContentManager().fillContent(msg,abseq2);
		 */		
		//System.out.println(SLFormatter.format(msg.toString()));
		
				
	}

	/**
	 * Retrive the DependencyManager of this ASMCL
	 * @return The DependencyManager of this ASMCL
	 */
	public DependencyManager getDependencyManager() {
		return myDependencyManager;
	}

	/**
	 * @return the codec used by this ASCML
	 */
	public Codec getCodec() {
		return codec;
	}

}
