package cascom.jade.mtp.http;

import cascom.net.ServerSocket;
import cascom.net.Socket;
import cascom.net.SocketException;
import cascom.fipa.util.BufferedInputStream;
import cascom.fipa.util.BufferedOutputStream;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.Exception;
import java.util.Hashtable;
import jade.mtp.InChannel;
import jade.mtp.InChannel.Dispatcher;
import jade.mtp.MTPException;
import jade.domain.FIPAAgentManagement.Envelope;
import jade.core.AID;
import jade.util.Logger;
import jade.core.Profile;
import jade.core.Specifier;
import jade.lang.acl.*;
import jade.domain.FIPAAgentManagement.Envelope;
import jade.util.leap.Iterator;
import jade.util.leap.List;
import jade.core.ProfileException;
import jade.mtp.http2.*;

import cascom.fipa.envelope.EnvelopeCodec;
import cascom.net.InetAddress;
import cascom.fipa.envelope.EnvelopeCodec;
import java.io.InterruptedIOException;

import jade.mtp.http2.TimerDebugger;
import cascom.net.InetAddress;
import jade.util.leap.HashMap;

/**
 * Class implementing the Cascom Messaging Gateway. Class uses
 * CascomGWConnectionManager to handle connections between the
 * gateway client and the gateway. Gateway extends HTTPServer, which
 * uses the default (or user defined) ConnectionManager to handle
 * connections between the gateway and the (non proxy client) agent platform MTP:s.
 *
 * This gateway should act like the original HTTP MTP but routes
 * the messages to connected mobile agent platforms when needed.
 *
 * @author Ahti Syreeni - TeliaSonera
 */
public class CascomGatewayServer extends HTTPServer {
    /**
     * In CASCOM system, there can be encrypted messages. If such message cannot be
     * delivered, it cannot be opened for creating error response. However, the
     * encryption concerns only content and this prefix is added to the actual ACLCodec name.
     * So, using the ACLCodec with name after this prefix, it should be possible to open the
     * headers of encrypted aclmessage.
     */
    public static final String SECURE_ACLCODING_PREFIX ="secure-";
    
    
    /** Constructor: Store the information, uses init() to initialize the server */
    private Hashtable managersForProxyClients = null;
    
    /** Prefix for configuration parameters in jade.core.Profile meant for this component */
    public static final String PARAM_PREFIX="cascom_gateway_";
    
    /** Maxinum number of clients this gateway can store (not used currently) */
    public static final String PARAM_MAXCLIENTS="maxclients";
    
    /** Default value for PARAM_MAXCLIENTS */
    public static final int DEFAULT_MAXCLIENTS = 10;
    
    /** Default size of message buffer for one mobile client */
    public static final int DEFAULT_BUFFERSIZE = 1024;
    
    /** The platform ID of the host agent platform if this gateway is part of agent platform */
    private String platformID = null;
    
    /** JADE Logger which logs events for this class */
    private Logger logger = Logger.getMyLogger(this.getClass().getName());
    
    /** ACLCodecs. This is used only for generating error messages in ACL */
    private Hashtable aclCodecs = null;
    
    
    /* Number of currently active connections */
    private int activeConnections = 0;
    private Object connectionsLock = new Object();
    
    /* Time to wait for closing connections when desactivating gateway */
    private static final int CLOSING_WAITING_TIME = 5000;
    private boolean state_desactivating = false;
    private HTTPAddress local_ip_num;
    private HTTPAddress local_ip_dns;
    
    
    /**
     *  Returns the platform id of the host platform if this gateway is part of the agent platform
     */
    public String getPlatformID(){
        return this.platformID;
    }
    
    
    /**
     * Returns Hastable containing CascomGWConnectionManagers handling connections to
     * mobile clients.
     */
    public Hashtable getClients(){
        return this.managersForProxyClients;
    }
    
    
    /**
     * Gets the ConnectionManager which handles connections
     * related issues (HTTP-responses, opening and closing of connections etc.)
     * to the other agent platforms. This ConnectionManager does not handle
     * connections between mobile clients connecting to gateway.
     */
    public ConnectionManager getConnectionManager(){
        return super.cm;
    }
    
    /**
     * Gets the logger for this class.
     */
    public Logger getLogger(){
        return this.logger;
    }
    
    /**
     * Release resources, shut down all connections and close sockets
     * before exiting.
     */
    public void desactivate(){
        state_desactivating = true;
        super.desactivate();
        super.active = false;
        
        
        Enumeration keys = this.managersForProxyClients.keys();
        CascomGWConnectionManager temp;
        while(keys.hasMoreElements()){
            temp = (CascomGWConnectionManager) this.managersForProxyClients.get(keys.nextElement());
            temp.closeConnection();
        }
        
        this.getConnectionManager().closeAllConnections();
        
        synchronized(connectionsLock) {
            long startTime = System.currentTimeMillis();
            while(activeConnections > 0){
                if(System.currentTimeMillis() > startTime+CLOSING_WAITING_TIME){
                    break;
                }
                try {
                    connectionsLock.wait(1000);
                } catch (Exception e){}
            }
        }
        
        if(activeConnections > 0){
            if(logger.isLoggable(Logger.WARNING)){
                logger.log(Logger.WARNING,"There are still "+activeConnections+" gateway clients connected. Terminating connections now.");
            }
        }
        
        
        keys = this.managersForProxyClients.keys();
        while(keys.hasMoreElements()){
            temp = (CascomGWConnectionManager) this.managersForProxyClients.get(keys.nextElement());
            temp.releaseResources();
        }
        
        
        
        
        
        this.managersForProxyClients.clear();
        this.managersForProxyClients = null;
    }
    
    
    /**
     * Returns ACL codecs available. These are stored
     * in Hashtable as objects implemening jade.lang.acl.ACLCodec interface.
     */
    public Hashtable getACLCodecs(){
        return this.aclCodecs;
    }
    
    
    
    /**
     * Init new instance of CascomGatewayServer and sets properties
     * given in jade.core.Profile
     */
    public void init(int p, InChannel.Dispatcher d, int t, boolean changePortIfBusy, Hashtable codecs, ConnectionManager cm, Profile profile) throws MTPException {
        super.init(p,d,t,changePortIfBusy,codecs,cm, profile);
        logger.log(Logger.INFO,"CASCOMGW timeouts: readtimeout input:"+timeout);
        if(profile.getParameter(PARAM_PREFIX+PARAM_MAXCLIENTS,null) != null){
            try{
                this.managersForProxyClients = new Hashtable(
                        Integer.parseInt(profile.getParameter(PARAM_PREFIX+PARAM_MAXCLIENTS,null)));
            } catch (Exception e){
                throw new MTPException("ERROR: parameter "+PARAM_PREFIX+PARAM_MAXCLIENTS+" not integer");
            }
        }
        
        InetAddress ia = new InetAddress();
        try {
            this.local_ip_num = new HTTPAddress("http://"+ia.getLocalHost()+":"+this.port);
            this.local_ip_dns = new HTTPAddress("http://"+ia.getLocalHostName()+":"+this.port);
        } catch (Exception e){
            e.printStackTrace();
        }
        
        if(this.managersForProxyClients == null){
            this.managersForProxyClients = new Hashtable(DEFAULT_MAXCLIENTS);
        }
        
        // the platform-id is needed for routing the messages to connected proxy clients
        try {
            this.platformID = profile.getParameter(Profile.PLATFORM_ID,null);
            if(this.platformID == null){
                this.platformID = Profile.getDefaultNetworkName()+":"+profile.getParameter(Profile.MAIN_PORT,"1099")+"/JADE";
            }
        } catch (Exception e){
            throw new MTPException("Platform id not specified in Profile. It is required for CASCOM Messaging Gateway");
        }
        
        if(this.logger.isLoggable(Logger.INFO)){
            this.logger.log(Logger.INFO,"Cascom Messaging Gateway started");
        }
        
        
        // Load aclCodecs as they are needed in dynamic configuration
        // Codecs
        this.aclCodecs = new Hashtable(2);
        try {
            List l = profile.getSpecifiers(Profile.ACLCODECS);
            Iterator envcodecs = l.iterator();
            while (envcodecs.hasNext()) {
                Specifier spec = null;
                String className = null;
                try{
                    spec = (Specifier) envcodecs.next();
                    className = spec.getClassName();
                    Class c = Class.forName(className);
                    ACLCodec codec = (ACLCodec)c.newInstance();
                    this.aclCodecs.put(codec.getName().toLowerCase(), codec);
                    // FIXME: notify the AMS of the new Codec to update the APDescritption.
                } catch(ClassNotFoundException cnfe){
                    throw new jade.lang.acl.ACLCodec.CodecException("ERROR: The class " +className +" for the ACLCodec not found.", cnfe);
                } catch(InstantiationException ie) {
                    throw new jade.lang.acl.ACLCodec.CodecException("The class " + className + " raised InstantiationException (see NestedException)", ie);
                } catch(IllegalAccessException iae) {
                    throw new jade.lang.acl.ACLCodec.CodecException("The class " + className  + " raised IllegalAccessException (see nested exception)", iae);
                }
            }
        } catch (jade.lang.acl.ACLCodec.CodecException ce){
            throw new MTPException(ce.getMessage());
        } catch (ProfileException pe){
            throw new MTPException("ProfileExeption:"+pe.getMessage());
        }
    }
    
    
    /**
     * Use CascomServerThread instead of original ServerThread.
     */
    public void run() {
        while(super.active) {  //Accept the input connections
            try {
                Socket client = super.server.accept();
                //client.setSoTimeout(timeout);
                
                // Do not accept calls if server is shutting down
                if(!state_desactivating){
                    new CascomServerThread(client, super.dispatcher,this).start();
                }
                
            } catch (IOException ioe){
                // Some virtual machines  may have timeouts for server.accept()
            } catch( Exception e ) {
                if(this.logger.isLoggable(Logger.INFO))
                    this.logger.log(Logger.WARNING,"Unexpected error at CASCOM Gateway Server (run()) :"+e.getMessage());
            }
        }
    }
    
    
    /**
     * Server thread for handling the tasks of normal ServerThread and also
     * the special functionality required for CASCOM gateway. This server thread
     * serves the public side of the gateway, thus is listening for incoming messages
     * and routes them to the right Thread to handle or
     * creates new Thread when a proxy client requests to connect
     * to the gateway.
     */
    public class CascomServerThread extends HTTPServer.ServerThread {
        // the main class, needed to get platform-id and references to clients
        private CascomGatewayServer server = null;
        private boolean proxyClientConnection = false;
        private String sender ="Unknown";
        
        public CascomServerThread(Socket client, InChannel.Dispatcher disp, CascomGatewayServer server){
            super(client, disp, server.getEnvelopeCodecs(),server.getConnectionManager());
            this.server = server;
            super.active = true;
            if(client != null){
                try {
                    sender = client.getAddress();
                } catch (Exception e){}
            } 
        }
        public void setActive(boolean active){
            super.setActive(active);
        }
        
        public void run() {
            if(state_desactivating){
                this.shutdown();
                return;
            }
            
            try {
                // we can read input stream but we should never use output
                // because ConnectionManager (or its subclasses) handle
                // output and delivering messages out
                super.input = new BufferedInputStream(client.getInputStream());
                //super.input.setNextEOS(1);
                //output = client.getOutputStream();
                do {
                    //System.err.println("CascomGatewayServer handling request");
                    FipaHttpObject ro = new FipaHttpObject();
                    try {
                        
                        //TimerDebugger td = new TimerDebugger(ro, System.currentTimeMillis()+": Parsing taking too long in CascomGatewayServer.ServerThread:");
                        //java.util.Timer timer = new java.util.Timer();
                        //timer.schedule(td,3000);
                        
                        HTTPIO.parseRequest(ro, super.input);                        
                        //timer.cancel();                        
                    } catch(IOException ioe) {
                        //ioe.printStackTrace();
                        this.cm.readError(this, ro, ioe);
                        if(this.active){
                            if(logger.isLoggable(Logger.INFO)){                                
                                logger.log(Logger.INFO,"Still reading input from sender "+this.sender+". Exception ("+ioe.getClass().getName()+", msg: "+ioe.getMessage()+") means that connection is still on");
                            }
                            continue;
                        } else {
                            if(logger.isLoggable(Logger.INFO)){
                                logger.log(Logger.INFO,"Stop reading input from sender "+this.sender+". Exception ("+ioe.getClass().getName()+", msg: "+ioe.getMessage()+") means that connection was broken.");
                            }
                            break;
                        }
                    } catch (Exception ex2){
                        if(logger.isLoggable(Logger.SEVERE)){
                            logger.log(Logger.SEVERE,"Unexpected error while reading input from sender "+this.sender+". Exception ("+ex2.getClass().getName()+", msg: "+ex2.getMessage()+"). Connection will be closed now.");
                        }
                        break;
                    }
                    
                    if(ro == null){
                        continue;
                    } else if(ro.isFipaRequest()){
                        //System.err.println("%% Gateway got fiparequest");
                        this.handleFipaRequest(ro);
                    } else if(ro.isResponse()){
                        //System.err.println("%% Gateway got response");
                        this.handleHttpResponse(ro);
                    } else if(ro.isHttpRequest()){
                        //System.err.println("%% Gateway got http request");
                        this.handleHttpRequest(ro);
                    } else {
                        //System.err.println("%% Gateway got invalid request");
                        super.handleInvalidRequest(ro);
                    }
                    
                } while(super.active);
                
            } catch(Exception e ) {
                if(this.server.getLogger().isLoggable(Logger.SEVERE)){
                    this.server.getLogger().log(Logger.SEVERE,"Unexpected error handling parsed input of sender "+this.sender +" ("+e.getClass().getName()+", msg: "+e.getMessage()+"). Shutting down reading thread.");
                }
            }
            this.shutdown();
        }
        
        
        
        private void shutdown(){
            super.active = false;
            
            // connection was not closed by CascomGatewayClientThread
            if(!this.proxyClientConnection){
                if(cm != null){
                    super.cm.closeConnections(this);
                } else {
                    if(this.server.getLogger().isLoggable(Logger.WARNING)){
                        this.server.getLogger().log(Logger.WARNING,"No connection manager for "+this.sender+" when shutting down thread. Socket could not be closed.");
                    }
                }
                //System.err.println("### GatewayServerThread "+Thread.currentThread()+" shutted down");
                super.client = null;
            }
        }
        
        
        
        /**
         * Handles HTTP-requests which contain FIPA-envelope and ACL message.
         */
        private void handleFipaRequest(FipaHttpObject ro){
            if(!super.codecs.containsKey((Object)ro.getEnvelopePresentation().toLowerCase())){
                super.handleUnsupportedEncoding(ro);
                return;
            }
            Envelope env = null;
            synchronized (codecs) {
                try {
                    EnvelopeCodec rightCodec = ((EnvelopeCodec)super.codecs.get((Object)ro.getEnvelopePresentation().toLowerCase()));
                    env = rightCodec.decode(ro.getEnvelope());
                } catch (Exception pe){
                    super.handleInternalError(ro);
                    dispatcher.notifyAll();
                    return;
                }
            }
            
            if ((env.getPayloadLength() != null)&&(env.getPayloadLength().longValue() != ro.getACLMessage().length)) {
                if(this.server.getLogger().isLoggable(Logger.WARNING))
                    this.server.getLogger().log(Logger.WARNING,"Payload size does not match envelope information: env:"+env.getPayloadLength()+", aclmsg:"+ro.getACLMessage().length);
                
            }
            
            // route the message according to platform-id
            // there can be several receivers, we have to try to send
            // the message to each of the addresses which are related to the mobile clients
            // or host agent platform of this gateway.
            String destinationPlatformID = null;
            Iterator it = env.getAllIntendedReceiver();
            
            if(it == null || !it.hasNext()){
                it = env.getAllTo();
                this.server.getLogger().log(Logger.WARNING,"Gateway received message with missing intended-receiver slot, it will be generated from to-field");
                while(it.hasNext()){
                    env.addIntendedReceiver((AID)it.next());
                }
                it = env.getAllIntendedReceiver();
            }
            
            String platformID;
            boolean succeeded = true;
            
            if(it.hasNext()){
                AID aid = (AID)it.next();
                if(aid == null || !this.routeMessage(env, ro,aid)){
                    succeeded = false;
                }
            } else {
                succeeded = false;
            }
            
            
            //FIXME: Should send standard FIPA error message back about receivers that
            //could not be reached
            if(!succeeded){
                System.err.println(System.currentTimeMillis()+": Error while routing CascomGatewayServer");
                ro.setErrorMsg("Error while routing");
                super.handleInvalidRequest(ro);
            } else {
                super.cm.messageDelivered(this, ro);
            }
            
            ro = null;
        }
        
        
        /**
         * Routes the given message to given destination platform. Destination can be
         * the host of this gateway (in this case, the gateway acts like original HTTP MTP) or the
         * destination can be mobile client registered (once connected) to this gateway. In
         * that case the message is passed for the mobile client's CascomGWConnectionManager for
         * delivery.
         */
        private boolean routeMessage(Envelope env, FipaHttpObject ro, AID currentReceiver){
            //System.out.println("Searching cascom-id from clients:"+destinationPlatformID+", this platform:"+this.server.getPlatformID());
            String destinationPlatformID = currentReceiver.getHap();
            
            
            
            
            // Route message to this platform
            if(destinationPlatformID.trim().equals(this.server.getPlatformID())){
                // this is a normal message intended for this host platform
                //System.err.println("RouteMessage: Dispatching message to platform");
                
                
                
                
                synchronized(super.dispatcher){
                    dispatcher.dispatchMessage(env,ro.getACLMessage());
                }
                
                
                return true;
                
            } else {
                // If the platform-id is one of mobile clients, send there
                
                Hashtable gwClientManagers = this.server.getClients();
                if(gwClientManagers.containsKey((Object)destinationPlatformID)){
                    // this message is intended for gateway client and that platform
                    // is registered (once connected) to this gateway
                    //System.out.println("RouteMessage Sending message to gateway client:"+destinationPlatformID);
                    
                    synchronized(gwClientManagers){
                        CascomGWConnectionManager cgct =
                                (CascomGWConnectionManager) gwClientManagers.get((Object)destinationPlatformID);
                        try {
                            cgct.deliver(ro);
                            return true;
                        } catch (Exception mtpe){
                            mtpe.printStackTrace();
                            // what should we actually do?
                            return false;
                        }
                    }
                } else {
                    
                    // This case should not happen often, for some reason
                    // the normal host has sent message to this gateway containing
                    // also normal network receivers as addresses. So now copy of this message
                    // must be sent to them as well.
                    
                    if(this.server.getLogger().isLoggable(Logger.WARNING)){
                        //Iterator it = it.getAllIntendedReceiver();
                        
                                /*
                                TimerDebugger.printError(ro,new Exception("Receiver unknown"),"Gateway got message which has no known receivers:"+env.toString());
                                if(ro.getACLMessage() != null){
                                    System.out.println(new String(ro.getACLMessage()));
                                }
                                 */
                        ConnectionManager gmanager = server.getConnectionManager();
                        String[] toAddr;
                        
                        toAddr = currentReceiver.getAddressesArray();
                        HTTPAddress to;
                        if(toAddr != null){
                            for(int i=0; i < toAddr.length; i++){
                                System.err.println("Trying to route message to destination "+toAddr[i]);
                                if(toAddr[i] != null){
                                    try {
                                        to = new HTTPAddress(toAddr[i]);
                                    } catch (Exception e){
                                        e.printStackTrace();
                                        continue;
                                    }
                                    // Prevent local loop if the
                                    if(to.getPortNo() == local_ip_num.getPortNo() &&
                                            (to.getHost().equals(local_ip_num.getHost()) ||
                                            to.getHost().equals(local_ip_dns.getHost()))){
                                        System.err.println("Preventing local loop...");
                                        continue;
                                    }
                                    try {
                                        //System.err.println("Routing disabled, not delivring message now");
                                        gmanager.deliver(ro.getHostAddr(), (FipaHttpObject)ro.clone());
                                        return true;
                                    } catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                        
                        //this.server.getLogger().log(Logger.WARNING,"Gateway got message which has no known receivers:"+env.toString());
                        /*
                        if(it != null && it.hasNext(){
                                this.server.getLogger().log(Logger.WARNING,"Gateway got message which has no known receivers:\nFrom:"+env.getFrom().getName()+"\nFrom platform:"+env.getFrom().getHap()+"\nTo: not available");
                        } else {
                                AID to = (AID)it.next();
                                this.server.getLogger().log(Logger.WARNING,"Gateway got message which has no known receivers:\nFrom:"+env.getFrom().getName()+"\nFrom platform:"+env.getFrom().getHap()+"\nTo:"+ to.getName()+"\nTo platform:"+to.getHap()+"\nContent:"+env.getPayload()+);
                        }*/
                    }
                    System.err.println("Could not route message to any of addresses of receiver agents "+currentReceiver.getName());;
                    
                    
                    
                    // routing failed
                    return false;
                }
            }
        }
        
        
        
        /**
         * Handles the pure HTTP request. The only HTTP request that this gateway can receive
         * is the request from mobile client to connect to the gateway. In that case
         * new CascomGatewayClientThread is created for handling the message exchange between
         * the mobile client and this gateway during the time the client is connected.
         * CascomGatewayClientThread is actually
         * not Thread, it is only an ordinary class and used for simplyfying the code.
         */
        private void handleHttpRequest(FipaHttpObject ro){
            // if this a request for connecting gateway client, handle it here
            if(ro.getHttpHeader(CascomGWConnectionManager.STATUS_HEADER) != null){
                //System.err.println("%%%% Gateway recognizes cascom status header");
                String cid = (ro.getHttpHeader(CascomGWConnectionManager.ID_HEADER));
                if(cid != null){
                    
                    // Server is shutting down, cannot accept clients now
                    
                    CascomGWConnectionManager cgw = null;
                    if(CascomGWConnectionManager.STATUS_CONTINUE.equals(
                            ro.getHttpHeader(CascomGWConnectionManager.STATUS_HEADER).trim()) &&
                            this.server.getClients().containsKey((Object)cid)){
                        // this should be existing connection
                        //System.err.println("%%%% Gateway continues session for "+cid);
                        try {
                            cgw = (CascomGWConnectionManager)this.server.getClients().get((Object)cid);
                            // in case the connection have been left open last time...
                            this.server.getLogger().log(Logger.WARNING,"Gateway closing old socket of client "+cid);
                            cgw.closeOutput();
                        } catch (Exception e){
                            if(this.server.getLogger().isLoggable(Logger.WARNING)){
                                this.server.getLogger().log(Logger.WARNING,"Gateway client not found:"+cid+", starting new session anyway...");
                            }
                            cgw = null;
                        }
                    } else {
                        //System.err.println("### New connection, header:"+ro.getHttpHeader(CascomGWConnectionManager.STATUS_HEADER).trim());
                    }
                    
                    if(cgw == null){
                        if(this.server.getClients().containsKey((Object)cid)){
                            CascomGWConnectionManager old = (CascomGWConnectionManager)this.server.getClients().get((Object)cid);
                            //FIXME: Problem: if enable, client can not shutdown, if disabled, system will slow down
                            old.closeOutput();
                            this.server.getClients().remove((Object)cid);
                            if(this.server.getLogger().isLoggable(Logger.WARNING)){
                                this.server.getLogger().log(Logger.WARNING,"Old buffer removed:"+cid);
                            }
                        }
                        cgw = new CascomGWConnectionManager(this.server.DEFAULT_BUFFERSIZE,false);
                        if(this.server.getLogger().isLoggable(Logger.INFO)){
                            this.server.getLogger().log(Logger.INFO,"New gateway client added:"+cid);
                        }
                        this.server.getClients().put((Object)cid, (Object)cgw);
                    }
                    
                    try {
                        cgw.setDefaultHost(new HTTPAddress(super.getSocket().getAddress()));
                        if(this.server.getLogger().isLoggable(Logger.INFO)){
                            this.server.getLogger().log(Logger.INFO,"Gateway client "+cid+" connected with address "+super.getSocket().getAddress());
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                        // error should not occur, the messages will be delivered
                        // even if the host name in HTTP request is wrong...
                    }
                    
                    try {
                        CascomGatewayServer.CascomGatewayClientThread th = new CascomGatewayServer.CascomGatewayClientThread(super.client,
                                super.getInputStream(),
                                super.getOutputStream(),
                                cgw,this.server, ro,cid);
                    } catch (Exception e){
                        if(this.server.getLogger().isLoggable(Logger.SEVERE)){
                            this.server.getLogger().log(Logger.SEVERE,"Could not start thread for client:"+e.getMessage());
                        }
                        try {
                            this.sendErrorToClient(ro, super.getOutputStream());
                        } catch (IOException ioe){}
                    }
                    
                    // the CascomGatewayClientThread has finnished, the client is disconnected
                    // this thread can shutdown as the client class has finnished
                    this.proxyClientConnection = true;
                    super.setActive(false);
                } else {
                    if(this.server.getLogger().isLoggable(Logger.WARNING)){
                        this.server.getLogger().log(Logger.WARNING,"Gateway: CASCOM-ID is missing from client request");
                    }
                    ro.setErrorMsg("ID missing");
                    // communicate directly without ConnectionManager
                    try {
                        this.sendErrorToClient(ro, super.getOutputStream());
                    } catch (IOException ioe){
                        ioe.printStackTrace();
                    }
                }
            } else {
                super.cm.handleHttpRequest(this,ro);
            }
        }
        
        
        /**
         * Handles HTTP response received from the other
         * agent platform MTP
         */
        private void handleHttpResponse(FipaHttpObject fho){
            /*
             * HTTP Responses cannot be delivered to mobile clients, let
             * the ConnectionManager to handle the connection between
             * the gateway and other platform MTP accoring to this
             * response.
             */
            super.cm.handleHttpResponse(this, fho);
        }
        
        
        /**
         * Sends ACL error message to client, which can be mobile client requesting
         * connection to gateway or other agent platform. NOT IMPLEMENTED YET.
         *
         */
        private void sendErrorToClient(FipaHttpObject fho, OutputStream out){
            if(this.server.getLogger().isLoggable(Logger.WARNING)){
                this.server.getLogger().log(Logger.WARNING,"Sending error to client:"+fho.getErrorMsg());
            }
        }
    }
    
    
    /**
     * Class for handling communication between mobile client connected to the gateway
     * and the gateway. Class lets the CascomGWConnectionManager to handle connection
     * issues and acts like reader for the messages that mobile client sends. This class
     * sorts the messages (HTTP responses, FIPA HTTP request and pure HTTP requests) and
     * delivers the FIPA messages to the destination using the deliver()-method in
     * ConnectionManager of this gateway. Note that this class is NOT a Thread, it
     * uses the caller Thread.
     */
    public class CascomGatewayClientThread implements ConnectionListener {
        private Socket client = null;
        private BufferedInputStream input = null;
        private BufferedOutputStream output = null;
        private CascomGWConnectionManager cgw = null;
        private CascomGatewayServer cgs = null;
        boolean active = true;
        private int timeOutCounter = 0;
        private Object lock = new Object();
        private String name ="none";
        
        /**
         * Inits the class and calls run() method.
         */
        public CascomGatewayClientThread(Socket client, InputStream input,
                OutputStream output,
                CascomGWConnectionManager cgw,
                CascomGatewayServer cgs,
                FipaHttpObject initRequest,
                String name){
            this.client = client;
            this.input = new BufferedInputStream(input);
            this.input.setNextEOS(1);
            this.output = new BufferedOutputStream(output);
            this.cgw = cgw;
            this.cgs = cgs;
            this.client.setKeepAlive(true);
            this.name = name;
            this.run(initRequest);
        }
        
        /**
         * Sets this class active. Once this class have been
         * disactivated it cannot be activated again.
         */
        public void setActive(boolean active){
            //System.err.println(Thread.currentThread().dumpStack());
            this.active = active;
            if(!active){
                this.client.setKeepAlive(false);
            }
        }
        
        
        /**
         * Handles the messages sent be the mobile client. Continues untill this
         * class have been set inactive using setActive()-method.
         * HTTP requests containing
         * FIPA ACL are sent to the destination platform, HTTP responses and pure
         * HTTP requests are passed for the CascomGWConnectionManager, which
         * handles the connection related issues between the gateway and the mobile client.
         */
        public void run(FipaHttpObject initRequest) {
            activeConnections ++;
            try {
                FipaHttpObject fho = new FipaHttpObject();
                cgw.startSending(this.output,this, initRequest);
                // start serving the client
                do {
                    fho = new FipaHttpObject();
                    //TimerDebugger td = new TimerDebugger(fho, System.currentTimeMillis()+": parsing taking too long in CascomGatewayServer.ClientThread");
                    //java.util.Timer timer = new java.util.Timer();
                    //timer.schedule(td,60000);
                    try {
                        HTTPIO.parseRequest(fho, this.input);
                        //timer.cancel();
                    } catch (IOException ioe){
                        //timer.cancel();
                        // this may be just timeout
                        // if sender is currently sending big message, don't
                        // close connection
                        //TimerDebugger.printError(fho,ioe, System.currentTimeMillis()+": Error parsing input in CascomGatewayServer.ClientThread:"+ioe.getMessage());
                        
                        //ConnectionManager will send response message if needed
                        this.cgw.readError(fho,ioe);
                        if(this.active){
                            if(this.cgs.getLogger().isLoggable(Logger.INFO)){
                                this.cgs.getLogger().log(Logger.INFO,"Still reading input from gateway client:"+this.name+". (IOException "+ioe.getMessage()+" detected as minor)");
                            }
                            continue;
                        } else {
                            if(this.cgs.getLogger().isLoggable(Logger.INFO)){
                                this.cgs.getLogger().log(Logger.INFO,"Stop reading input from gateway client:"+this.name+". (IOException "+ioe.getMessage()+" detected as connection lost)");
                            }
                            break;
                        }
                    } catch (Exception ex2){
                        if(this.cgs.getLogger().isLoggable(Logger.SEVERE)){
                            this.cgs.getLogger().log(Logger.SEVERE,"Unexpected exception ("+ex2.getClass().getName()+", see stacktrace) when reading input from gateway client:"+this.name+", message: "+ex2.getMessage()+". Shutting down reading now.");
                        }
                        ex2.printStackTrace();
                        this.shutDown("Unexpected exception ("+ex2.getClass().getName()+", msg:"+ex2.getMessage()+") when reading and parsing input");
                        return;
                    }
                    
                    
                    //System.err.println("Debug:thread"+Thread.currentThread().hashCode()+" parsed input and proceeding");
                    
                    if(fho == null){
                       continue;
                    } else if(fho.isResponse()){
                        //System.err.println("%%%% Gateway client thread: request is response "+fho.getHttpHeader(CascomGWConnectionManager.MSGACK_HEADER));
                        this.cgw.handleHttpResponse(fho);
                    } else if(fho.isFipaRequest()){
                        //System.err.println("%%%% Gateway client thread: request is fiparequest :"+fho.getHttpHeader(CascomGWConnectionManager.MSGNUMBER_HEADER));
                        fho.removeHttpHeader("Proxy-Connection");
                        this.cgw.messageDelivered(fho);
                        
                        
                        //FIXME: shouldn't done in same thread. Add own Threads for each target platform.
                        //if network-timeout happens when delivering to one host, delivery to other
                        //hosts should still happen while the ConnectionManager is waiting for time-out for
                        //one destination host.
                        
                        try {
                            //System.err.println("Sending fipa request to host:"+fho.getHost());
                            fho.removeHttpHeader(CascomGWConnectionManager.MSGNUMBER_HEADER);
                            this.cgs.getConnectionManager().deliver(fho.getHostAddr(), fho);
                        } catch (MTPException e){
                            if(this.cgs.getLogger().isLoggable(Logger.WARNING)){
                                this.cgs.getLogger().log(Logger.WARNING,"Could not route message comming from gateway client "+this.name+", reason "+e.getMessage()+". Sender will now be notified with ACL failure message.");
                            }
                            this.sendACLErrorResponse(fho, HTTPIO.ERROR_NOTFOUND);
                            //this.cgw.undeliveredMessage(fho);
                        }
                    } else if(fho.isHttpRequest()){
                        
                        /*
                         * TCP layer decided to continue with same socket
                         * This can happen sometimes if neither of the sides
                         * detects that connection is lost and thus sockets are
                         * not closed. In that case some TCP implementations may
                         * use the "still open" connection automatically.
                         */
                        if(fho.getHttpHeader(CascomGWConnectionManager.STATUS_HEADER) != null){
                            this.cgs.getLogger().log(Logger.WARNING,"TCP layer continues with dead socket with client "+this.name);
                            if(CascomGWConnectionManager.STATUS_NEW.equals(
                                    fho.getHttpHeader(CascomGWConnectionManager.STATUS_HEADER).trim())){
                                this.cgs.getLogger().log(Logger.WARNING,"Now removing buffer of client "+this.name);
                                CascomGWConnectionManager oldcgw = this.cgw;
                                this.cgw = new CascomGWConnectionManager(this.cgs.DEFAULT_BUFFERSIZE,false);
                                if(this.cgs.getClients().remove(this.name) == null){
                                    this.cgs.getLogger().log(Logger.WARNING,"Old buffer of client "+this.name+" could not be removed!!");
                                }
                                this.cgs.getClients().put((Object)this.name, (Object)this.cgw);
                                this.cgw.setDefaultHost(oldcgw.getDefaultHost());
                                //oldcgw.releaseResources();
                            }
                            this.active = true;
                            this.cgw.startSending(this.output,this, fho);
                        } else {
                            cgw.handleHttpRequest(fho);
                        }
                    } else {
                        cgw.undeliveredMessage(fho);
                    }
                } while(this.active);
            } catch (Exception e){
                if(this.cgs.getLogger().isLoggable(Logger.SEVERE)){
                    this.cgs.getLogger().log(Logger.SEVERE,"Unexpected exception ("+this.name+e.getClass().getName()+")  when handling the parsed input of gateway client "+this.name+", exception message is "+e.getMessage());
                }
                e.printStackTrace();
                this.shutDown("Unexpected exception handling parsed input in CascomClientThread:"+e.getMessage());
                return;
            }
            this.shutDown();
        }
        
        
        /**
         * Method for CascomGWConnectionManager to inform this class that streams
         * have been closed due to connection loss in CascomGWConnectionManager.
         * The client has disconnected from the gateway in normal way and
         * reading of the input should stop.
         */
        public void connectionClosed(){
            if(this.cgs.getLogger().isLoggable(Logger.INFO)){
                this.cgs.getLogger().log(Logger.INFO,"Gateway client "+this.name+" ("+this.cgw.getDefaultHost().toString()+") disconnected from gateway");
            }
            this.active = false;
            
            try {
                this.input.close();
            } catch (Exception e){}
            try {
                this.client.close();
            } catch (Exception e){}
            this.client = null;
            this.input = null;
            //System.err.println("Debug:thread"+Thread.currentThread().hashCode()+" shutdown complete");
            
        }
        
        /**
         * Method for CascomGWConnectionManager to inform this class that streams
         * have been closed due to connection loss in CascomGWConnectionManager.
         * The client has not disconnected properly and the given error message
         * indicates the reason why the CascomGWConnectionManager has closed the
         * connection. Reading of the input should end.
         */
        public void connectionClosedByError(String message){
            if(this.cgs.getLogger().isLoggable(Logger.INFO)){
                this.cgs.getLogger().log(Logger.INFO,"Gateway client "+this.name+"("+this.cgw.getDefaultHost().toString()+") removed from gateway, reason:"+message);
            }
            this.connectionClosed();
        }
        
        /**
         * Shut down normally.
         */
        public void shutDown(){
            this.shutDown(null);
        }
        
        /**
         * Shut down due to error.
         */
        public void shutDown(String errorMsg){
            this.active = false;
            //let the connection manager close OutputStream
            if(errorMsg == null){
                this.cgw.closeOutput();
            } else {
                this.cgw.closeOutput(errorMsg);
            }
            activeConnections --;
            
            synchronized(connectionsLock){
                connectionsLock.notify();
            }
        }
        
        
        /**
         * Send error message using ACL to inform the sender agent that the message could not be
         * delivered. This indicates that the error has occured between the gateway and the destination
         * agent platform even if the proxy agent platform MTP  would resend the message to the gateway, it
         * would not help because the message is correctly received by this gateway. So, HTTP error codes cannot
         * be used and this gateway must generate FIPA error message to inform the sender agent in
         * mobile platform.
         */
        public void sendACLErrorResponse(FipaHttpObject fho, String message){
            // we must open envelope and ACLMessage to get the return values
            Envelope env = null;
            EnvelopeCodec rightCodec = null;
            try {
                rightCodec = ((EnvelopeCodec)this.cgs.getEnvelopeCodecs().get((Object)fho.getEnvelopePresentation().toLowerCase()));
                env = rightCodec.decode(fho.getEnvelope());
            } catch (Exception pe){
                if(this.cgs.getLogger().isLoggable(Logger.WARNING)){
                    this.cgs.getLogger().log(Logger.WARNING,"CascomGatewayServer.ClientThread Cannot inform agent about error, error while decoding envelope:"+pe.getMessage());
                }
                return;
            }
            
            ACLMessage originalACL = null;
            ACLCodec rightACLCodec = null;
            try {
                rightACLCodec = ((ACLCodec)this.cgs.getACLCodecs().get((Object)env.getAclRepresentation()));
                originalACL = rightACLCodec.decode(fho.getACLMessage(),env.getPayloadEncoding());
            } catch (Exception e){
                String aclRep = env.getAclRepresentation();
                if(aclRep != null && aclRep.startsWith(SECURE_ACLCODING_PREFIX)){
                    aclRep = aclRep.substring(SECURE_ACLCODING_PREFIX.length(),aclRep.length());
                    if(this.cgs.getACLCodecs().containsKey(aclRep)){
                        try {
                            ACLCodec testCodec = ((ACLCodec)this.cgs.getACLCodecs().get(aclRep));
                            originalACL = testCodec.decode(fho.getACLMessage(),env.getPayloadEncoding());
                            rightACLCodec = testCodec;
                        } catch (Exception e5){
                            if(this.cgs.getLogger().isLoggable(Logger.WARNING)){
                                this.cgs.getLogger().log(Logger.WARNING,"Gateway cannot decode encrypted ACLMessage headers for creating proper error message:"+e5.getMessage()+" (aclrepresentation tried:"+aclRep+")");
                            }
                            return;
                        }
                    } else {
                        if(this.cgs.getLogger().isLoggable(Logger.WARNING)){
                            this.cgs.getLogger().log(Logger.WARNING,"Gateway cannot decode ACLMessage for creating proper error message using ACLCodec "+rightCodec.getName()+", error:"+e.getMessage());
                        }
                        return;
                    }
                } else {
                    if(this.cgs.getLogger().isLoggable(Logger.WARNING)){
                        this.cgs.getLogger().log(Logger.WARNING,"Gateway cannot decode ACLMessage for creating proper error message using ACLCodec "+rightCodec.getName()+", error:"+e.getMessage());
                    }
                    return;
                }
                
                
                /* Depricated: would be too time-consuming.
                // Try other available codecs. This is usefull if only the content could not be decoded like
                // in encrypted messages.
                Enumeration allCodecs = this.cgs.getACLCodecs().keys();
                ACLCodec testCodec;
                boolean found = false;
                while(allCodecs.hasMoreElements()){
                    testCodec = (ACLCodec)allCodecs.nextElement();
                    if(testCodec != rightCodec){
                        try {
                            originalACL = testCodec.decode(fho.getACLMessage(),env.getPayloadEncoding());
                            originalACLCodec = testCodec;
                            found = true;
                            break;
                        } catch (Exception ioe5){}
                    }
                }
                if(!found){
                    if(this.cgs.getLogger().isLoggable(Logger.WARNING)){
                        this.cgs.getLogger().log(Logger.WARNING,"Cannot inform agent about error, could not decode message with any of available codecs.");
                    }
                    return;
                }
                 */
            }
            
            // TODO: Send FIPA compliant error message as specified in fipa fipa00067.
            
            ACLMessage reply = originalACL.createReply();
            reply.setPerformative(ACLMessage.FAILURE);
            reply.setContent("((action\n"+originalACL.getSender().toString()+"\n(MTS-error(agent-identifier :name "+originalACL.getSender().toString()+")\n(internal-error \"No valid addresses contained within the AID "+((AID)originalACL.getAllReceiver().next()).toString()+")))");
            AID sender  = new AID("ams@"+this.cgs.getPlatformID(),AID.ISGUID);
            sender.addAddresses("http://"+InetAddress.getLocalHostName());
            reply.setSender(sender);
            reply.setEncoding("ISO-8859-1");
            reply.setProtocol("fipa-agent-management");
            reply.setDefaultEnvelope();
            reply.getEnvelope().setAclRepresentation(rightCodec.getName());
            reply.getEnvelope().addIntendedReceiver(originalACL.getSender());
            fho.setHttpHeader(fho.HTTP_HOST, "none"); // default host will be used anyway in Connection Manager
            fho.setIsFipaRequest(true);
            
            try {
                fho.setACLMessage(rightACLCodec.encode(reply,env.getPayloadEncoding()));
                reply.getEnvelope().setPayloadLength(new Long(fho.getACLMessage().length));
                fho.setEnvelope(rightCodec.encode(reply.getEnvelope()));
                this.cgw.deliver(fho);
            } catch (Exception e){
                if(this.cgs.getLogger().isLoggable(Logger.WARNING)){
                    this.cgs.getLogger().log(Logger.WARNING, System.currentTimeMillis()+": Could not  deliver error ACL to sender, reason:"+e.getMessage());
                }
            }
        }
    }
}