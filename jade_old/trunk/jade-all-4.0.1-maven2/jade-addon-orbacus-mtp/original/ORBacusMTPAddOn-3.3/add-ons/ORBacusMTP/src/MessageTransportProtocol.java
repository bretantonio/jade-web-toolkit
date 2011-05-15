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

package orbacus;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;

import java.io.Serializable;

import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;

// Import ORBacus-specific POAManager factory support
import com.ooc.OBPortableServer.POAManagerFactory;
import com.ooc.OBPortableServer.POAManagerFactoryHelper;

// Import ORBacus-specific Boot Manager support
import com.ooc.OB.BootManager;
import com.ooc.OB.BootManagerHelper;

import FIPA.*; // OMG IDL stubs

import jade.core.AID;
import jade.core.Profile;

import jade.domain.FIPAAgentManagement.Envelope;
import jade.domain.FIPAAgentManagement.ReceivedObject;
import jade.domain.FIPAAgentManagement.Property;
import jade.domain.FIPANames;

import jade.mtp.InChannel;
import jade.mtp.OutChannel;
import jade.mtp.MTP;
import jade.mtp.MTPException;
import jade.mtp.TransportAddress;

import jade.util.Logger;


public class MessageTransportProtocol implements MTP {

  private static final String[] PROTOCOLS = new String[] { "IOR", "corbaloc", "corbaname" };

  private final static ORB myORB;
  private static POA rootPOA;
  private static POAManagerFactory mgrFactory;
  private static BootManager bootMgr;
  private static Logger logger = Logger.getMyLogger(MessageTransportProtocol.class.getName());

  static {
   // Initialize the CORBA ORB
   Properties props = System.getProperties();
   props.put("org.omg.CORBA.ORBClass", "com.ooc.CORBA.ORB");
   props.put("org.omg.CORBA.ORBSingletonClass", "com.ooc.CORBA.ORBSingleton");

   // The following property is useful to debug ORBacus connection handling
   // props.put("ooc.orb.trace.connections", "1");
   myORB = ORB.init(new String[0], props);

   try {
     // Create the root POA
     rootPOA = (POA)myORB.resolve_initial_references("RootPOA");

     // Create the POA manager factory - ORBacus specific
     org.omg.CORBA.Object o1 = myORB.resolve_initial_references("POAManagerFactory");
     mgrFactory = POAManagerFactoryHelper.narrow(o1);

     // Create the boot manager - ORBacus specific
     org.omg.CORBA.Object o2 = myORB.resolve_initial_references("BootManager");
     bootMgr = BootManagerHelper.narrow(o2);

     Runnable servantLoop = new Runnable() {
       public void run() {
	 try {
	   rootPOA.the_POAManager().activate();
	   myORB.run();
	 }
	 catch(org.omg.PortableServer.POAManagerPackage.AdapterInactive ai) {
	   ai.printStackTrace();
	 }
       }
     };

     Thread t = new Thread(servantLoop, "ORBacus servant");
     t.start();

   }
   catch(org.omg.CORBA.ORBPackage.InvalidName in) {
     in.printStackTrace();
   }

  }

  // CORBA Object Implementation class
  private static class MTSServant extends MTSPOA {

    private final InChannel.Dispatcher dispatcher;

    public MTSServant(InChannel.Dispatcher disp) {
      dispatcher = disp;
    }

    public void message(FipaMessage aFipaMessage) {
      FIPA.Envelope[] envelopes = aFipaMessage.messageEnvelopes;
      byte[] payload = aFipaMessage.messageBody;
      
    
      Envelope env = new Envelope();

      // Read all the envelopes sequentially, so that later slots
      // overwrite earlier ones.
      for(int e = 0; e < envelopes.length; e++) {
	FIPA.Envelope IDLenv = envelopes[e];

	// Read in the 'to' slot
	if(IDLenv.to.length > 0)
	  env.clearAllTo();
	for(int i = 0; i < IDLenv.to.length; i++) {
	  AID id = unmarshalAID(IDLenv.to[i]);
	  env.addTo(id);
	}

	// Read in the 'from' slot
	if(IDLenv.to.length > 0) {
	  AID id = unmarshalAID(IDLenv.from[0]);
	  env.setFrom(id);
	}

	// Read in the 'intended-receiver' slot
	if(IDLenv.intendedReceiver.length > 0)
	  env.clearAllIntendedReceiver();
	for(int i = 0; i < IDLenv.intendedReceiver.length; i++) {
	  AID id = unmarshalAID(IDLenv.intendedReceiver[i]);
	  env.addIntendedReceiver(id);
	}

	// Read in the 'encrypted' slot
	/* Commented for compliance to FIPA Standard 2002 
	if(IDLenv.encrypted.length > 0)
	  env.clearAllEncrypted();
	for(int i = 0; i < IDLenv.encrypted.length; i++) {
	  String word = IDLenv.encrypted[i];
	  env.addEncrypted(word);
	} */

	// Read in the other slots
	if(IDLenv.comments.length() > 0)
	  env.setComments(IDLenv.comments);
	if(IDLenv.aclRepresentation.length() > 0)
	  env.setAclRepresentation(IDLenv.aclRepresentation);
	if(IDLenv.payloadLength > 0)
	  env.setPayloadLength(new Long(IDLenv.payloadLength));
	if(IDLenv.payloadEncoding.length() > 0)
	  env.setPayloadEncoding(IDLenv.payloadEncoding);
	if(IDLenv.date.length > 0) {
	  Date d = unmarshalDateTime(IDLenv.date[0]);
	  env.setDate(d);
	}

	// Read in the 'received' stamp
	if(IDLenv.received.length > 0)
	  env.addStamp(unmarshalReceivedObj(IDLenv.received[0]));

	// Read in the 'user-defined properties' slot
	if(IDLenv.userDefinedProperties.length > 0)
	    env.clearAllProperties();
	for(int i = 0; i < IDLenv.userDefinedProperties.length; i++) {
	    env.addProperties(unmarshalProperty(IDLenv.userDefinedProperties[i]));
 	}
      }

      // Dispatch the message
      dispatcher.dispatchMessage(env, payload);

    }


    private AID unmarshalAID(FIPA.AgentID id) {
      AID result = new AID();
      result.setName(id.name);
      for(int i = 0; i < id.addresses.length; i++)
	result.addAddresses(id.addresses[i]);
      for(int i = 0; i < id.resolvers.length; i++)
	result.addResolvers(unmarshalAID(id.resolvers[i]));
      return result;
    }

    private Date unmarshalDateTime(FIPA.DateTime d) {
      Date result = new Date();
      return result;
    }


      private Property unmarshalProperty(FIPA.Property p) {
	  return new Property(p.keyword, p.value.extract_Value());
      }

    private ReceivedObject unmarshalReceivedObj(FIPA.ReceivedObject ro) {
      ReceivedObject result = new ReceivedObject();
      result.setBy(ro.by);
      result.setFrom(ro.from);
      result.setDate(unmarshalDateTime(ro.date));
      result.setId(ro.id);
      result.setVia(ro.via);
      return result;
    }

  } // End of MTSServant class


  private static abstract class OBAddress implements TransportAddress {

    public abstract String getString();

    public abstract void deactivate() throws MTPException;

    public MTS getObject() {
      return FIPA.MTSHelper.narrow(myORB.string_to_object(getString()));
    }

  }


  private static class OBAddressIOR extends OBAddress {

    private String ior;


    public OBAddressIOR(String rep) {
      org.omg.CORBA.Object o = myORB.string_to_object(rep);
      ior = myORB.object_to_string(o);
    }

    public String getString() {
      return ior;
    }

    public void deactivate() throws MTPException {
      try {
	org.omg.CORBA.Object objRef = myORB.string_to_object(ior);
	byte[] oid = rootPOA.reference_to_id(objRef);
	rootPOA.deactivate_object(oid);
      }
      catch(SystemException se) {
	throw new MTPException("Error during 'IOR' address deactivation", se);
      }
      catch(UserException ue) {
	throw new MTPException("Error during 'IOR' address deactivation", ue);
      }
    }

    public String getProto() {
      return "IOR:";
    }

    public String getHost() {
      return "";
    }

    public String getPort() {
      return "";
    }

    public String getFile() {
      return "";
    }

    public String getAnchor() {
      return "";
    }

  } // End of OBAddressIOR class


  private static class OBAddressURL extends OBAddress {

    private String proto;
    private String host;
    private String port;
    private String file;

    public OBAddressURL(String rep) throws MTPException {
      if(!rep.startsWith("corbaloc:"))
        throw new MTPException("Missing 'corbaloc': " + rep);
      int secondColonPos = rep.indexOf(':', 9);
      if(secondColonPos != -1)
	proto = rep.substring(0, secondColonPos + 1);
      else
        throw new MTPException("Missing protocol name: " + rep);
      int thirdColonPos = rep.indexOf(':', secondColonPos + 1); // Include ':'
      int slashPos = rep.indexOf('/');
      if((thirdColonPos != -1)&&(slashPos != -1)) {
	// The host name is between two ':', both excluded
	host = rep.substring(secondColonPos + 1, thirdColonPos);
	// The port number is between ':' and '/', both excluded
	port = rep.substring(thirdColonPos + 1, slashPos);
	// The object key is after the '/'
	file = rep.substring(slashPos + 1);
      }
      else
	throw new MTPException("Missing port or object key: " + rep);
    }

    public String getString() {
      return proto + host + ':' + port + "/" + file;
    }

    public void deactivate() throws MTPException {
     if(logger.isLoggable(Logger.CONFIG))
     	logger.log(Logger.CONFIG,"Deactivating " + getString() + " ...");
      // Retrieve the POA for this address port.
      try {
	// Name for the POA at the given address
	String POAname = "POA" + getPort();
	String mgrName = POAname + "Manager";
	POA myPOA = rootPOA.find_POA(POAname, false); // Don't try to create it if it doesn't exist

	// Close the POA and the managed object, waiting for pending
	// calls to complete
	myPOA.destroy(true, true);
	if(logger.isLoggable(Logger.CONFIG))
		logger.log(Logger.CONFIG,"Done");
      }
      catch(org.omg.PortableServer.POAPackage.AdapterNonExistent ane) {
	throw new MTPException("No POA is active on the port " + getPort(), ane);
      }
    }

    public String getProto() {
      return proto;
    }

    public String getHost() {
      return host;
    }

    public String getPort() {
      return port;
    }

    public String getFile() {
      return file;
    }

    public String getAnchor() {
      return "";
    }

  } // End of OBAddressURL class


  private static class OBAddressNS extends OBAddress {

    private String proto;
    private String host;
    private String port;
    private String file;
    private String anchor;

    public OBAddressNS(String rep) throws MTPException {
      if(!rep.startsWith("corbaname:"))
        throw new MTPException("Missing 'corbaname': " + rep);
      int secondColonPos = rep.indexOf(':', 10);
      if(secondColonPos != -1)
	proto = rep.substring(0, secondColonPos + 1);
      else
        throw new MTPException("Missing protocol name: " + rep);
      int thirdColonPos = rep.indexOf(':', secondColonPos + 1); // Include ':'
      int slashPos = rep.indexOf('/');
      int poundPos = rep.indexOf('#');
      if((thirdColonPos != -1)&&(slashPos != -1)) {
	// The host name is between two ':', both excluded
	host = rep.substring(secondColonPos + 1, thirdColonPos);
	// The port number is between ':' and '/', both excluded
	port = rep.substring(thirdColonPos + 1, slashPos);

	if(poundPos != -1) {
	  // The object key is between '/' and '#', both excluded
	  file = rep.substring(slashPos + 1, poundPos);
	  anchor = rep.substring(poundPos + 1);
	}
	else {
	  // the object key is all that's left in the string
	  file = rep.substring(slashPos + 1);
	  anchor = "";
	}
      }
      else
	throw new MTPException("Missing port or object key: " + rep);
    }

    public String getString() {
      return proto + host + ':' + port + "/" + file + "#" + anchor;
    }

    public void deactivate() throws MTPException {
	
      if(logger.isLoggable(Logger.CONFIG))
      	logger.log(Logger.CONFIG,"Deactivating " + getString() + " ...");
      // Contact the Naming Service the address points to and bind the
      // object reference to it. The String points to the root of the
      // naming service.
      String namingServiceURL = "corbaloc::" + getHost() + ':' + getPort() + '/';
      String namingServiceObjectID = getFile();
      if((namingServiceObjectID == null)||(namingServiceObjectID.length() == 0))
	namingServiceObjectID = "NameService";
      namingServiceURL = namingServiceURL.concat(namingServiceObjectID);

      try {
	org.omg.CORBA.Object o = myORB.string_to_object(namingServiceURL);
	NamingContextExt rootCtx = NamingContextExtHelper.narrow(o);

	String objName = getAnchor();
	if((objName == null) || (objName.length() == 0))
	  throw new MTPException("Missing Binding Name in 'corbaname' address.");

	// Retrieve the structured name of the object
	NameComponent[] n = rootCtx.to_name(objName);
	org.omg.CORBA.Object objRef = rootCtx.resolve(n);

	// Remove the Servant from the Naming Service
	rootCtx.unbind(n);

	// Deactivate the Servant
	byte[] oid = rootPOA.reference_to_id(objRef);
	rootPOA.deactivate_object(oid);
	if(logger.isLoggable(Logger.CONFIG))
		logger.log(Logger.CONFIG,"Done");
      }
      catch(SystemException se) {
	throw new MTPException("Error during 'corbaname' address deactivation", se);
      }
      catch(UserException ue) {
	throw new MTPException("Error during 'corbaname' address deactivation", ue);
      }

    }

    public String getProto() {
      return proto;
    }

    public String getHost() {
      return host;
    }

    public String getPort() {
      return port;
    }

    public String getFile() {
      return file;
    }

    public String getAnchor() {
      return anchor;
    }

  } // End of OBAddressNS

  public TransportAddress activate(InChannel.Dispatcher disp, Profile p) throws MTPException {

    // Create a servant object on the root POA
    MTSServant servant = new MTSServant(disp);

    // Activate Servant
    MTS objRef = servant._this(myORB);

    String ior = myORB.object_to_string(objRef);
    TransportAddress ta = new OBAddressIOR(ior);

    return ta;

  }

  public void activate(InChannel.Dispatcher disp, TransportAddress ta, Profile p) throws MTPException {
    try {
      OBAddressURL addr = (OBAddressURL)ta;

      // Create a servant object on a POA listening to the given port,
      // using the given object key
      MTSServant servant = new MTSServant(disp);

      // Name for the POA at the given address
      String POAname = "POA" + ta.getPort();
      String mgrName = POAname + "Manager";

      // Retrieve the POA for the given port or create it if it doesn't exist yet.
      POA adapter;
      try {
	adapter = rootPOA.find_POA(POAname, false); // Don't try to create it if it doesn't exist
      }
      catch(org.omg.PortableServer.POAPackage.AdapterNonExistent ane) {

	// Create a new POA manager and set host and port properties for it.
	Properties ps = System.getProperties();
	ps.setProperty("ooc.iiop.acceptor." + mgrName + ".host", ta.getHost());
	ps.setProperty("ooc.iiop.acceptor." + mgrName + ".port", ta.getPort());
	POAManager mgr = mgrFactory.create_poa_manager(mgrName);


	Policy[] policies = new Policy[3];
	policies[0] = rootPOA.create_lifespan_policy(LifespanPolicyValue.PERSISTENT);
	policies[1] = rootPOA.create_id_assignment_policy(IdAssignmentPolicyValue.USER_ID);
	policies[2] = rootPOA.create_implicit_activation_policy(ImplicitActivationPolicyValue.NO_IMPLICIT_ACTIVATION);

	// Create a new POA with the given name, manager and policies
	adapter = rootPOA.create_POA(POAname, mgr, policies);

	// Start accepting requests on the newly created POA
	mgr.activate();

      }

      byte[] oid = ta.getFile().getBytes();
      adapter.activate_object_with_id(oid, servant);
      org.omg.CORBA.Object obj = adapter.servant_to_reference(servant);
      bootMgr.add_binding(oid, obj);

    }
    catch(ClassCastException cce) {
      try {
	// Try with a corbaname: address
	OBAddressNS addrNS = (OBAddressNS)ta;
	activateNS(disp, addrNS);
      }
      catch(ClassCastException cce2) {
	throw new MTPException("Wrong IIOP address: " + cce2.getMessage());
      }
    }
    catch(SystemException se) {
      throw new MTPException("Error during address deactivation", se);
    }
    catch(UserException ue) {
      throw new MTPException("Error during address activation", ue);
    }
  }


  private void activateNS(InChannel.Dispatcher disp, OBAddressNS addrNS) throws MTPException {

    // Create a servant object on the root POA
    MTSServant servant = new MTSServant(disp);

    // Activate Servant
    MTS objRef = servant._this(myORB);

    // Contact the Naming Service the address points to and bind the
    // object reference to it. The String points to the root of the
    // naming service.
    String namingServiceURL = "corbaloc::" + addrNS.getHost() + ':' + addrNS.getPort() + '/';
    String namingServiceObjectID = addrNS.getFile();
    if((namingServiceObjectID == null)||(namingServiceObjectID.length() == 0))
      namingServiceObjectID = "NameService";
    namingServiceURL = namingServiceURL.concat(namingServiceObjectID);

    try {
      org.omg.CORBA.Object o = myORB.string_to_object(namingServiceURL);
      NamingContextExt rootCtx = NamingContextExtHelper.narrow(o);

      String objName = addrNS.getAnchor();
      if((objName == null) || (objName.length() == 0))
	throw new MTPException("Missing Binding Name in 'corbaname' address.");

      // Bind the newly created Servant into the Naming Service
      NameComponent[] n = rootCtx.to_name(objName);
      rootCtx.rebind(n, objRef);
    }
    catch(SystemException se) {
      throw new MTPException("Error during interaction with CORBA Naming Service", se);
    }
    catch(UserException ue) {
      throw new MTPException("Error during interaction with CORBA Naming Service", ue);
    }

  }

  public void deactivate(TransportAddress ta) throws MTPException {
    try {
      OBAddress obTA = (OBAddress)ta;
      obTA.deactivate();
    }
    catch(ClassCastException cce) {
      throw new MTPException("Invalid Transport Address.", cce);
    }
  }

  public void deactivate() throws MTPException {
    if(rootPOA != null) {
      synchronized(rootPOA) { // Double Checked Locking
	if(rootPOA != null) {
	  // Destory all POAs, etherealizing active objects and
	  // waiting for completion of all pending requests.
	  rootPOA.destroy(true, true);
	  rootPOA = null;
	  ((com.ooc.CORBA.ORB)myORB).destroy();
	}
      }
    }
  }

  public void deliver(String addr, Envelope env, byte[] payload) throws MTPException {
    try {
      TransportAddress ta = strToAddr(addr);
      OBAddress obAddr = (OBAddress)ta;
      MTS objRef = obAddr.getObject();

      // verifies if the server object really exists (useful if the IOR is
      // valid, i.e corresponds to a good object) (e.g. old IOR)
      // FIXME. To check if this call slows down performance
      if (objRef._non_existent()) 
        throw new MTPException("Bad IIOP server object reference:"+objRef.toString());

      // Fill in the 'to' field of the IDL envelope
      Iterator itTo = env.getAllTo();
      List to = new ArrayList();
      while(itTo.hasNext()) {
	AID id = (AID)itTo.next();
	to.add(marshalAID(id));
      }

      FIPA.AgentID[] IDLto = new FIPA.AgentID[to.size()];
      for(int i = 0; i < to.size(); i++)
	IDLto[i] = (FIPA.AgentID)to.get(i);


      // Fill in the 'from' field of the IDL envelope
      AID from = env.getFrom();
      Iterator itFrom = from.getAllAddresses();

      FIPA.AgentID[] IDLfrom = new FIPA.AgentID[] { marshalAID(from) };

      // Fill in the 'intended-receiver' field of the IDL envelope
      Iterator itIntendedReceiver = env.getAllIntendedReceiver();
      List intendedReceiver = new ArrayList();
      while(itIntendedReceiver.hasNext()) {
	AID id = (AID)itIntendedReceiver.next();
	intendedReceiver.add(marshalAID(id));
      }

      FIPA.AgentID[] IDLintendedReceiver = new FIPA.AgentID[intendedReceiver.size()];
      for(int i = 0; i < intendedReceiver.size(); i++)
	IDLintendedReceiver[i] = (FIPA.AgentID)intendedReceiver.get(i);


      // Fill in the 'encrypted' field of the IDL envelope
	  /* Commented for compliance to FIPA 2002 standard
      Iterator itEncrypted = env.getAllEncrypted();
      List encrypted = new ArrayList();
      while(itEncrypted.hasNext()) {
	String word = (String)itEncrypted.next();
	encrypted.add(word);
      }

      String[] IDLencrypted = new String[encrypted.size()];
      for(int i = 0; i < encrypted.size(); i++)
	IDLencrypted[i] = (String)encrypted.get(i); */
	  String[] IDLencrypted = new String[0];	

      // Fill in the other fields of the IDL envelope ...
      String IDLcomments = (env.getComments() != null)?env.getComments():"";
      String IDLaclRepresentation = env.getAclRepresentation();
      Long payloadLength = env.getPayloadLength();
      int IDLpayloadLength = payloadLength.intValue();
      String IDLpayloadEncoding = (env.getPayloadEncoding() != null)?env.getPayloadEncoding():"";
      FIPA.DateTime[] IDLdate = new FIPA.DateTime[] { marshalDateTime(env.getDate()) };
      FIPA.Property[][] IDLtransportBehaviour = new FIPA.Property[][] { };
      // Fill in the 'userdefined-properties' field of the IDL envelope
      Iterator itUserDefProps = env.getAllProperties();
      List userDefProps = new ArrayList();
      while(itUserDefProps.hasNext()) {
	Property p = (Property)itUserDefProps.next();
	userDefProps.add(marshalProperty(p));
      }
      FIPA.Property[] IDLuserDefinedProperties = new FIPA.Property[userDefProps.size()];
      for(int i = 0; i < userDefProps.size(); i++)
	IDLuserDefinedProperties[i] = (FIPA.Property)userDefProps.get(i);

      // Fill in the list of 'received' stamps
      /* FIXME: Maybe several IDL Envelopes should be generated, one for every 'received' stamp...
      ReceivedObject[] received = env.getStamps();
      FIPA.ReceivedObject[] IDLreceived = new FIPA.ReceivedObject[received.length];
      for(int i = 0; i < received.length; i++)
	IDLreceived[i] = marshalReceivedObj(received[i]);
      */

      // FIXME: For now, only the current 'received' object is considered...
      ReceivedObject received = env.getReceived();
      FIPA.ReceivedObject[] IDLreceived;
      if(received != null)
	IDLreceived = new FIPA.ReceivedObject[] { marshalReceivedObj(received) };
      else
	IDLreceived = new FIPA.ReceivedObject[] { };

      FIPA.Envelope IDLenv = new FIPA.Envelope(IDLto,
					       IDLfrom,
					       IDLcomments,
					       IDLaclRepresentation,
					       IDLpayloadLength,
					       IDLpayloadEncoding,
					       IDLdate,
					       IDLencrypted,
					       IDLintendedReceiver,
					       IDLreceived,
					       IDLtransportBehaviour,
					       IDLuserDefinedProperties);

      FipaMessage msg = new FipaMessage(new FIPA.Envelope[] { IDLenv }, payload);
      objRef.message(msg);
    }
    catch(ClassCastException cce) {
      cce.printStackTrace();
      throw new MTPException("Address mismatch: this is not a valid IIOP address.");
    }
    catch(Exception cce2) {
      cce2.printStackTrace();
      throw new MTPException("Address mismatch: this is not a valid IIOP address.");
    }

  }

  public TransportAddress strToAddr(String rep) throws MTPException {
    if(rep.toLowerCase().startsWith("ior:"))
      return new OBAddressIOR(rep);
    else if(rep.toLowerCase().startsWith("corbaloc:"))
      return new OBAddressURL(rep);
    else if(rep.toLowerCase().startsWith("corbaname:"))
      return new OBAddressNS(rep);
    else
      throw new MTPException("Invalid protocol prefix: " + rep);
  }

  public String addrToStr(TransportAddress ta) throws MTPException {
    try {
      OBAddress obta = (OBAddress)ta;
      return obta.getString();
    }
    catch(ClassCastException cce) {
      cce.printStackTrace();
      throw new MTPException("Address mismatch: this is not a valid IIOP address.");
    }
  }

  public String getName() {
    return FIPANames.MTP.IIOP; 
  }

  public String[] getSupportedProtocols() {
    return PROTOCOLS;
  }

  private FIPA.Property marshalProperty(Property p) {
    org.omg.CORBA.Any value = myORB.create_any();
    value.insert_Value((Serializable)p.getValue());
    return new FIPA.Property(p.getName(), value);
  }

  private FIPA.AgentID marshalAID(AID id) {
    String name = id.getName();
    String[] addresses = id.getAddressesArray();
    AID[] resolvers = id.getResolversArray();
    FIPA.Property[] userDefinedProperties = new FIPA.Property[] { };
    int numOfResolvers = resolvers.length;
    FIPA.AgentID result = new FIPA.AgentID(name, addresses, new AgentID[numOfResolvers], userDefinedProperties);
    for(int i = 0; i < numOfResolvers; i++) {
      result.resolvers[i] = marshalAID(resolvers[i]); // Recursively marshal all resolvers, which are, in turn, AIDs.
    }

    return result;

  }

  private FIPA.DateTime marshalDateTime(Date d) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(d);
    short year = (short)cal.get(Calendar.YEAR);
    short month = (short)cal.get(Calendar.MONTH);
    short day = (short)cal.get(Calendar.DAY_OF_MONTH);
    short hour = (short)cal.get(Calendar.HOUR_OF_DAY);
    short minutes = (short)cal.get(Calendar.MINUTE);
    short seconds = (short)cal.get(Calendar.SECOND);
    short milliseconds = 0; // FIXME: This is truncated to the second
    char typeDesignator = ' '; // FIXME: Uses local timezone ?
    FIPA.DateTime result = new FIPA.DateTime(year,
					     month,
					     day,
					     hour,
					     minutes,
					     seconds,
					     milliseconds,
					     typeDesignator);
    return result;

  }

  private FIPA.ReceivedObject marshalReceivedObj(ReceivedObject ro) {
    FIPA.ReceivedObject result = new FIPA.ReceivedObject();
    result.by = ro.getBy();
    result.from = ro.getFrom();
    result.date = marshalDateTime(ro.getDate());
    result.id = ro.getId();
    result.via = ro.getVia();
    return result;
  }

}
