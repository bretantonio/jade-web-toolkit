package cascom.jade.mtp.http;
import jade.core.Profile;
import jade.mtp.*;
import java.io.*;
import java.util.Hashtable;
import java.util.Enumeration;
import java.lang.ClassCastException;
import java.lang.ClassNotFoundException;

import jade.mtp.InChannel;
import jade.mtp.MTP;
import jade.mtp.MTPException;
import jade.mtp.TransportAddress;
import jade.domain.FIPAAgentManagement.Envelope;
import jade.util.leap.List;
import jade.util.leap.LinkedList;
import jade.util.leap.Iterator;
import jade.core.Profile;
import jade.core.ProfileException;
import jade.core.Specifier;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.ACLCodec;
import jade.util.Logger;
import cascom.fipa.envelope.EnvelopeCodec;
import cascom.net.InetAddress;
import cascom.net.SocketFactory;
import cascom.net.Socket;
import jade.mtp.http2.*;
import java.util.TimerTask;
import java.util.Timer;
import java.io.EOFException;

/**
 * Class implementing buffered message delivery to be used with
 * CASCOM Messaging Gateway. Messages are buffered if
 * there is no connection open to gateway. Agent can send following commands to
 * this manager by sending ACLMessage:s to address http://[mainhost]:0 where
 * [mainhost] is the main host specified in jade.core.Profile. ACLMessage may contain
 * following commands: open-connection (opens connection to gateway), close-connection
 * (closes connection to gateway).
 *
 * This class sends following messages to the agent with
 * name corresponding value of parameter jade_mtp_http_connectionAgent specified
 * in jade.core.Profile: connection-opened, connection-closed error: [errormsg]. These
 * messages informs the agent about current state of the gateway connection.
 *
 * By default the connection to the gateway is closed after 36000 seconds of inactivity
 * between client and the gateway. You can change the time (in seconds) using option
 * jade_mtp_http_keepalive-timeout
 *
 * @author Ahti Syreeni - TeliaSonera
 */
public class CascomClientConnectionManager extends ConnectionManager implements ConnectionListener {
    // the address that is reserved for configuration, messages
    // sent to this address are handled by this component as
    // internal configuration commands
    public String configAddress = null;
    public static final String OPT_KEY = "cascomGateway";
    public static final String MTP_NAME = "cascommtp";
    public static final String OPT_RECONNECT = "autoreconnect";
    public static final String OPT_BSIZE = "bufferSize";
    public static final String OPT_MANAGER_AGENT_NAME = "connectionAgent";
    public static final String OPT_AUTOREGISTER = "autoRegister";
    
    private int linger = 1000;
    private int connectionOpenTime = Integer.MAX_VALUE;
    
    /** How many times the connection to the gateway is tried */
    private int maxRetry = 1;
    
    private HTTPAddress gateWayAddress = null;
    private int maxBufferSize = 2048;
    private String platformID = null;
    
    private CascomGWConnectionManager mBuffer = null;
    
    private String connectionAgentName = "ConnectionManagerAgent";
    private Hashtable aclCodecs = null;
    private Hashtable envCodecs = null;
    private ACLCodec aclCodec = null;
    
    private HTTPServer.ServerThread gwConnectionThread;
    private boolean autoOpen = true;
    private boolean isFirstMessage = true;
    private Logger logger = Logger.getMyLogger(this.getClass().getName());
    
    private static Object gwOpenLock = new Object();
    private OpenConnectionThread oct = null;
    
    private boolean autoReconnect = false;
    
    // connection to gateway
    KeepAlive.KAConnection gwConnection = null;
    
    
    public class ResponseTimeout extends TimerTask {
        private OpenConnectionThread oct = null;
        public ResponseTimeout(OpenConnectionThread oct){
            this.oct = oct;
        }
        
        /*
        public boolean cancel(){
            return false;
        }
         */
        
        public void run(){
            oct.timeout();
        }
        /*
        public long scheduledExecutionTime(){
            return -1;
        }
         */
    }
    
    public class OpenConnectionThread extends Thread {
        CascomClientConnectionManager ccm = null;
        boolean inform = false;
        boolean active = true;
        Socket socket = null;
        Timer timer = new Timer();
        TimerTask timerTask = null;
        private int retryTime = 0;
        
        public OpenConnectionThread(CascomClientConnectionManager ccm){
            this.ccm = ccm;
        }
        
        public void setInform(boolean inform){
            this.inform = inform;
        }
        
        
        public synchronized void timeout(){
            if(this.socket != null){
                if(logger.isLoggable(Logger.WARNING)){
                    logger.log(Logger.WARNING,"Error: gateway not responding (timeout)");
                }
                try {
                    this.socket.close();
                } catch (Exception e){ e.printStackTrace();}
            }
        }
        
        public synchronized void terminate(){
            this.active = false;
            if(this.socket != null){
                try {
                    logger.log(Logger.WARNING,"Timeout while contacting gateway");
                    this.socket.close();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        
        
        public void run(){
            //logger.log(Logger.INFO,"Now opening gwcon");
            while(this.active){
                synchronized(this){
                    try {
                        this.wait();
                    } catch (InterruptedException e){
                        if(!this.active){
                            break;
                        } else {
                            continue;
                        }
                    }
                }
                
                if(!this.active){
                    break;
                }
                if(gwConnection != null){
                    continue;
                }
                this.openConnection();
            }
        }
        
        
        private void openConnection(){
            FipaHttpObject fho,response;
            //System.err.println("#DEBUG Now opening gwconnection");
            //logger.log(Logger.INFO,"Now opening gwcon 2");
            
            //DEBUG
            String statusString ="";
            
            this.retryTime ++;
            try {
                SocketFactory sf = SocketFactory.getDefault();
                statusString = "SocketFactory got,";
                
                //Socket socket;
                if (outPort > -1) {
                    socket = sf.createSocket(gateWayAddress.getHost(),
                            gateWayAddress.getPortNo(),
                            InetAddress.getLocalHost(),outPort, false);
                    statusString+="outport >-1,";
                } else {
                    socket = sf.createSocket(gateWayAddress.getHost(),gateWayAddress.getPortNo(), false);
                    statusString+="outport ==-1,";
                }

                gwConnection = new KeepAlive.KAConnection(socket, gateWayAddress);
                statusString+="gwConnection created,";
                
                if(gwConnection == null){
                    throw new IOException("Connection null");
                }
                
                //socket.setSoTimeout(responseTimeout);
                socket.setKeepAlive(true);
                socket.setLinger(linger);
                statusString+="socketOptions set,";
                
                // request CASCOM gateway to open connection
                fho = new FipaHttpObject();
                fho.setIsHttpRequest(true);
                fho.setHttpHeader(fho.HTTP_HOST, gateWayAddress.getHost());
                fho.setHttpHeader(CascomGWConnectionManager.ID_HEADER, platformID);                
                fho.setHostAddr(gateWayAddress);
                
                statusString+=",FipaHttpObject created";
                //logger.log(Logger.INFO,"Now opening gwcon 3");
                
                if(isFirstMessage){
                    isFirstMessage = false;
                    fho.setHttpHeader(CascomGWConnectionManager.STATUS_HEADER, CascomGWConnectionManager.STATUS_NEW);
                    statusString+=",was first message,";
                } else {
                    fho.setHttpHeader(CascomGWConnectionManager.STATUS_HEADER, CascomGWConnectionManager.STATUS_CONTINUE);
                    
                    // This is not documented: send ack for the latest received message with open
                    // connection request. This way the other end will know which is the
                    // last message that was received before connection was lost
                    fho.setHttpHeader(CascomGWConnectionManager.MSGACK_HEADER, ""+mBuffer.getAckNumber(-1));
                    statusString+="was not first message,";
                }
                
                
                // Because we cannot set socket timeout values in MIDP, we must
                // create our own timeout. Actually, we could set timeouts on/off
                // but in that case the default timeout could be too short in
                // some cases
                
                this.timerTask =  new ResponseTimeout(this);
                this.timer.schedule(this.timerTask, responseTimeout);
                
                
                //System.err.println("#DEBUG Send request");
                // Send request and receive response
                HTTPIO.writeHttpRequest(gwConnection.getOut(), fho, gateWayAddress, false);

                statusString+="wroteHttpRequest,";
                
                response = new FipaHttpObject();
                HTTPIO.parseRequest(response,gwConnection.getIn());
                //System.err.println("#DEBUG Response received");
                
                statusString+="got response,";
                this.timerTask.cancel();
                this.timerTask = null;
                
                if(response == null || gwConnection == null){
                    logger.log(Logger.INFO,"cannot establish connection to gateway, response null");
                    gwConnection = null;
                    throw new IOException("cannot establish connection to gateway, response null");
                }
                
                //retry once if the status code is not OK but the problem was not the connection,
                // because the response code was received
                if(response.getStatusCodeInt()!=200){
                    logger.log(Logger.INFO,"Response from gateway was not 200, retrying, it was "+response.getStatusCode());
                    this.timerTask =  new ResponseTimeout(this);
                    this.timer.schedule(this.timerTask, responseTimeout);
                    HTTPIO.writeHttpRequest(gwConnection.getOut(), fho, gateWayAddress, false);
                    response = new FipaHttpObject();
                    HTTPIO.parseRequest(response,gwConnection.getIn());
                    this.timerTask.cancel();
                    this.timerTask = null;
                    if(response == null || gwConnection == null){
                        gwConnection = null;
                        throw new IOException("Response from gateway not 200");
                    }
                }
                
                //logger.log(Logger.INFO,"Now opening gwcon 8");
                
                gwConnectionThread = new HTTPServer.ServerThread(socket ,gwConnection.getIn(), disp, envCodecs, this.ccm);
                gwConnectionThread.start();
                //logger.log(Logger.INFO,"Now opening gwcon 9");
                
                mBuffer.startSending(gwConnection.getOut(),this.ccm,response);
                if(this.inform){
                    //logger.log(Logger.INFO,"Now opening gwcon 10");
                    informAgent("connection-open");
                }
                
                autoOpen = true;
                this.socket = null;
                this.retryTime = 0;
                
            } catch (IOException ieo){
                if(this.timerTask != null){
                    this.timerTask.cancel();
                    this.timerTask = null;
                }
                
                if(gwConnectionThread != null){
                    gwConnectionThread.setActive(false);
                    gwConnectionThread = null;
                }
                
                if(gwConnection != null){
                    gwConnection.close();
                    gwConnection = null;
                }
                
                this.socket = null;
                
                if(!this.active){
                    logger.log(Logger.WARNING,"Exception when opening connection to gateway:"+ieo.getClass().getName()+", msg:+"+ieo.getMessage()+". Thread is not active so no retrys will be made now.");                
                    return;
                }
                
                
                // Retry up to maxRetry times opening the connection
                
                if(autoReconnect){
                    if(this.retryTime == 1&& this.inform){                        
                        informAgent("connection-closed error:"+ieo.getMessage());
                    }
                    synchronized(this){
                        try {
                            this.wait(3000);
                        } catch (Exception e){}
                    }                    
                    this.openConnection();
                } else if(this.retryTime >= maxRetry){
                    //ieo.printStackTrace();
                    informAgent("connection-closed error:"+ieo.getMessage());
                    this.retryTime = 0;
                } else {
                    //System.err.println("#DEBUG IOError:"+ieo.getMessage()+" retrying connection to gw");
                    this.openConnection();
                }
            } catch (Exception e){
                e.printStackTrace();
                // This is unexpected
                logger.log(Logger.SEVERE,"Unexpected exception (+"+e.getClass().getName()+", msg: "+e.getMessage()+") opening connection to gateway (see stacktrace). ActionLog:"+statusString);
                e.printStackTrace();
            }
        }
        
    }
    
    /**
     * Initialize this class. Read properties from Profile and install client to
     * given HTTPAddress. Deliver messages to platform using given Dispatcher and
     * use available EnvelopeCodecs to encode/decode message envelopes.
     *
     */
    public void init(Profile p, HTTPAddress addr, InChannel.Dispatcher disp, Hashtable envelopeCodecs) throws MTPException {
        super.init(p,addr, disp,envelopeCodecs);
                        
        if(p.getParameter(super.PREFIX+OPT_KEY, null) != null){
            try {
                this.gateWayAddress = new HTTPAddress(p.getParameter(super.PREFIX+OPT_KEY,null));
                LinkedList l = new LinkedList();
                l.addLast((Object)this.gateWayAddress.toString());
                p.setSpecifiers(Profile.MTPS, l);
            } catch (Exception e){
                throw new MTPException("Invalid address for CASCOM Messaging Gateway");
            }
        } else {
            throw new MTPException("No address for CASCOM Messaging Gateway, use option "+super.PREFIX+OPT_KEY+" to specify it.");
        }
        
        try {
            this.platformID = p.getParameter(Profile.PLATFORM_ID,null);
        } catch (Exception e){
            throw new MTPException("Platform id not specified in Profile. Please set it manually as parameter at startup using JADE parameter "+Profile.PLATFORM_ID);
        }
        
        if(this.platformID == null){
            throw new MTPException("Platform id not correctly specied with startup parameter. Please set it manually as parameter at startup using JADE parameter "+Profile.PLATFORM_ID);
        }
        
        if(p.getParameter(super.PREFIX+OPT_BSIZE, null) != null){
            try {
                this.maxBufferSize = Integer.parseInt(p.getParameter(super.PREFIX+OPT_BSIZE,null));
            } catch (Exception e){
                throw new MTPException("Invalid buffer size specified for MTP message buffer");
            }
        }
        
        
        if(p.getParameter(super.PREFIX+OPT_RECONNECT, null) != null && p.getParameter(super.PREFIX+OPT_RECONNECT, null).trim().equals("true")){
            this.autoReconnect = true;
            System.err.println("Automatic reconnect set to true");
        }
        
        // create new client side CascomGWConnectionManager
        this.mBuffer = new CascomGWConnectionManager(this.maxBufferSize,true);
        this.mBuffer.setDefaultHost(this.gateWayAddress);
        
        this.mBuffer.setResponseTimeOut(super.responseTimeout);
        this.linger = super.linger;
        
        
        if(p.getParameter(PREFIX+"keepalive-timeout", null) != null){
            try {
                int connectionOpenTime = Integer.parseInt(p.getParameter(PREFIX+"keepalive-timeout", null));
                if(logger.isLoggable(Logger.INFO)){
                    logger.log(Logger.INFO,"Idle timeout for gateway connection:"+connectionOpenTime+
                            " s.");
                }
                this.mBuffer.setClosingTimeout(connectionOpenTime);
            } catch (Exception e){
                logger.log(Logger.WARNING,"Invalid parameter value for keepalive-timeout:"+p.getParameter(PREFIX+"keepalive-timeout", null));
            }
        }
        
        if(p.getParameter(PREFIX+"ping-timeout", null) != null){
            try {
                int ping = Integer.parseInt(p.getParameter(PREFIX+"ping-timeout", null));
                if(logger.isLoggable(Logger.INFO)){
                    logger.log(Logger.INFO,"Ping timeout for gateway connection:"+ping+
                            " s.");
                }
                this.mBuffer.setPingingTimeout(ping);
            } catch (Exception e){
                logger.log(Logger.WARNING,"Invalid parameter value for keepalive-timeout:"+p.getParameter(PREFIX+"keepalive-timeout", null));
            }
        }
        
        
        
        this.configAddress = addr.toString();
        
        // Load aclCodecs as they are needed in dynamic configuration
        // Codecs
        this.aclCodecs = new Hashtable(2);
        try {
            List l = p.getSpecifiers(Profile.ACLCODECS);
            Iterator codecs = l.iterator();
            while (codecs.hasNext()) {
                Specifier spec = null;
                String className = null;
                try{
                    spec = (Specifier) codecs.next();
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
        
        if(p.getParameter(super.PREFIX+OPT_MANAGER_AGENT_NAME, null) != null){
            this.connectionAgentName = p.getParameter(super.PREFIX+OPT_MANAGER_AGENT_NAME, null);
        }
        this.envCodecs = envelopeCodecs;
        
        this.oct = new OpenConnectionThread(this);
        oct.start();
        
        if(p.getParameter(super.PREFIX+OPT_AUTOREGISTER, null) != null && (
                p.getParameter(super.PREFIX+OPT_AUTOREGISTER, "false").trim().toLowerCase().equals("true") ||
                p.getParameter(super.PREFIX+OPT_AUTOREGISTER, "false").trim().toLowerCase().equals("yes"))
                ){
            System.err.println("Open connection at startup");
            this.openConnection(true);
        }
        
        
    }
    
    /**
     *  Delivers the FipaHttpObject to given destination address or buffers
     *  it if there is no connection open currently to the CASCOM Messaging Gateway
     */
    public void deliver(HTTPAddress host, FipaHttpObject httpObject) throws MTPException {
        // open connection when first message comes
        try {
            
            //System.err.println("mtp.deliver 1");
            // if connection is closed normally, open it first
            if(this.mBuffer.connectionClosed && this.autoOpen){
                //System.err.println("mtp.deliver 2");
                this.autoOpen = false;
                this.openConnection(true);
            }
            //System.err.println("mtp.deliver 3");
            
            this.mBuffer.deliver(host,httpObject);
            
        } catch (Exception e){
            e.printStackTrace();
            throw new MTPException(e.getMessage());
        }
    }
    
    /**
     * Returns the address of the CASCOM Messaging Gateway, as this
     * ConnectionManager uses it as the gateway.
     */
    public HTTPAddress getGatewayAddress(){
        return this.gateWayAddress;
    }
    
    
    /**
     * Handles HTTP-response.
     */
    public void handleHttpResponse(HTTPServer.ServerThread sth, FipaHttpObject ro) {
        if(sth == this.gwConnectionThread){
            this.mBuffer.handleHttpResponse(ro);
        }
        /*
        else {
            System.err.println("Unexpected error: thread calling handleHttpResponse is not thread of gateway listener thread");
        }
         */
    }
    
    
    /**
     * ServerThread will use this method to inform that the message is parsed
     * and delivered to the agent platform without errors.
     */
    public void messageDelivered(HTTPServer.ServerThread sth, FipaHttpObject fho){
        this.mBuffer.messageDelivered(fho);
    }
    
    
    /**
     * ServerThread will use this method to inform that message is not delivered
     * due to error.
     */
    public void undeliveredMessage(HTTPServer.ServerThread sth,FipaHttpObject ro){
        this.mBuffer.undeliveredMessage(ro);
    }
    
    
    /**
     * Handles configuration request. This connection manager supports dynamic
     * configuration done by preselected agent. Thus, the agent name must
     * be given in Profile during the startup with proper property. Currently
     * only connection open and close commands are implemented.
     */
    public void handleConfigurationRequest(Envelope env, byte[] request) throws MTPException {
        //System.err.println("ConnectionManager got configuration request");
        ACLCodec aclcodec = (ACLCodec)this.aclCodecs.get((Object)env.getAclRepresentation().toLowerCase());
        if(aclcodec == null){
            throw new MTPException("invalid encoding in configuration request");
        }
        
        ACLMessage acl = null;
        try {
            acl = aclcodec.decode(request, env.getPayloadEncoding());
        } catch (Exception e){
            throw new MTPException("Error decoding ACLMessage in CascomConnectionManager:"+e.getMessage());
        }
        
        /*
        if(env.getFrom() == null || !env.getFrom().getLocalName().equals(this.connectionAgentName)){
            throw new MTPException("This agent has no persmission for MTP dynamic config.");
        }
         */
        
        if(acl.getContent()!=null){
            this.aclCodec = aclcodec;
            synchronized(this){
                if(acl.getContent().equals("open-connection")){
                    this.openConnection(true);
                    this.autoOpen = true;
                } else if(acl.getContent().equals("close-connection")){
                    this.autoOpen = false;
                    this.mBuffer.closeConnection();
                } else if(acl.getContent().equals("break-connection")){
                    try {
                        this.gwConnection.close();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    // method for CascomGWConnectionManager to inform that connection is closed
    public void connectionClosed(){
        //shutdown thread, it will call closeConnections when it is shutting down
        //System.err.println("CascomClientConnectionManager was informed that connection closed normally");
        if(this.gwConnection != null){
            this.gwConnectionThread.setActive(false);
        }
    }
    
    
    public void connectionClosedByError(String message){
        if(this.gwConnection != null){
            this.gwConnectionThread.setActive(false);
        }
        this.informAgent("connection-closed error:"+message);
        // Connection should not be opened automatically as
        // it was not closed normally
        
        
        if(!this.autoReconnect){
            this.autoOpen = false;
        }
        
    }
    
    
    /**
     * Inform this ConnectionManager that error occured while parsing input.
     */
    public void readError(HTTPServer.ServerThread st, FipaHttpObject fho, Exception ioe){
        /**
         * Problem in MIDP: we cannot know for sure if the error is just
         * timeout or a real TCP error because we can not trust
         * on device dependend error messages and SocketTimeoutException
         * is not available in J2ME.
         *
         * There is just no method
         * to test if the input is still open and readable.
         *
         * Thus, whether timeout or error is detected it is expected that
         * the error is discovered next time the connection manager
         * tries to send message. So, we expect that if error occurs, the
         * connection will be lost to both directions.
         */
        
        if(this.mBuffer.connectionClosed()) {
            st.setActive(false);
        } else {
            // End of file means the connection is closed.
            if(ioe instanceof EOFException){
                this.mBuffer.closeOutput(ioe.getMessage());
            } else if (fho.responseExpected()) {
                this.mBuffer.undeliveredMessage(fho);
            }
            // else: this is propably time-out. If the connection is dead,
            // next time the input.read() -method should return -1 and
            // HTTPIO.parseRequest() should throw EOFException so the connection
            // will be closed at that time
            //this.mBuffer.closeConnection();
        }
    }
    
    public void closeConnections(HTTPServer.ServerThread sth){
        //this.mBuffer.closeConnection();
        // OutputStream will be closed be CascomGWConnectionManager
        synchronized(this){
            if(sth == this.gwConnectionThread){
                this.gwConnection.close();
                this.gwConnection = null;
                this.gwConnectionThread = null;
                
                if(this.autoReconnect){
                    this.openConnection(true);
                }
                
            } else {
                System.err.println("Warning: not gw thread");
            }
        }
    }
    
    
    public void closeAllConnections(){
        this.mBuffer.closeConnection();
        this.mBuffer.releaseResources();
        if(this.gwConnectionThread != null){
            this.gwConnectionThread.setActive(false);
            this.gwConnection = null;
            this.gwConnectionThread = null;
        }
        if(this.oct != null){
            this.oct.terminate();
            this.oct = null;
        }
    }
    
    
    
    
    
    
    
    /**
     *  Opens the connection to the gateway.
     *  The received messages in the buffer will be removed.
     *  This method will not block as connection will be opened
     *  in another thread.
     */
    
    public void openConnection(boolean informAgent) {
        synchronized(this.oct){
            this.oct.setInform(informAgent);
            this.oct.notifyAll();
        }
    }
    
    
    
    private void informAgent(String msg){
        final String message = msg;
        
        Runnable r = new Runnable(){
            public void run(){
                ACLMessage temp = new ACLMessage(ACLMessage.INFORM);
                temp.setEncoding("ISO-8859-1");
                AID rec = new AID(MTP_NAME,AID.ISLOCALNAME);
                rec.addAddresses(gateWayAddress.toString());
                temp.setSender(rec);
                rec = new AID(connectionAgentName,AID.ISLOCALNAME);
                rec.addAddresses(gateWayAddress.toString());
                temp.addReceiver(rec);
                
                temp.setContent(message);
                temp.setEncoding("iso-8859-1");
                temp.setDefaultEnvelope();
                temp.getEnvelope().setAclRepresentation("fipa.acl.rep.bitefficient.std");
                temp.getEnvelope().addIntendedReceiver(rec);
                byte[] aclm = null;
                
                if(aclCodec == null){
                    aclCodec = (ACLCodec)aclCodecs.get((Object)temp.getEnvelope().getAclRepresentation());
                }
                
                try {
                    aclm = aclCodec.encode(temp, temp.getEncoding());
                } catch (Exception e){
                    //System.err.println("Error encoding acl");
                    e.printStackTrace();
                }
                
                // FIXME Sometimes the message is not delivered to the agent and just disappears
                // without any errors in messaging system !!
                //System.out.println("### Informing agent:"+message);
                disp.dispatchMessage(temp.getEnvelope(),aclm);                
            }
        };
        Thread thr = new Thread(r);
        thr.start();
    }
}