/*****************************************************************
 * JADE - Java Agent DEvelopment Framework is a framework to develop
 * multi-agent systems in compliance with the FIPA specifications.
 * Copyright (C) 2000 CSELT S.p.A.
 *
 * The updating of this file to JADE 2.0 has been partially supported by the
 * IST-1999-10211 LEAP Project
 *
 * This file refers to parts of the FIPA 99/00 Agent Message Transport
 * Implementation Copyright (C) 2000, Laboratoire d'Intelligence
 * Artificielle, Ecole Polytechnique Federale de Lausanne
 *
 * GNU Lesser General Public License
 *
 * This library is free software; you can redistribute it sand/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation,
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.
 *****************************************************************/

package jade.mtp.http2;

//import java.net.*;
import cascom.net.Socket;


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
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.util.BasicProperties;
import jade.util.leap.List;
import jade.util.leap.Iterator;
import jade.core.Profile;
import jade.core.ProfileException;
import jade.core.Specifier;
import cascom.fipa.envelope.EnvelopeCodec;
import jade.util.Logger;
import cascom.net.MalformedURLException;
import cascom.net.InetAddress;

/**
 * Implementation of FIPA HTTP Transport Protocol for JADE. This class
 * have been modified to be compatible with J2ME.
 *
 * @author Jose Antonio Exposito
 * @author MARISM-A Development group ( marisma-info@ccd.uab.es )
 * @version 0.1
 * @author Nicolas Lhuillier (Motorola Labs)
 * @version 1.0
 * @author Ahti Syreeni (TeliaSonera)
 * @version 1.0 (CASCOM)
 */
public class MessageTransportProtocol implements MTP {
    
    // DEFAULT VALUES
    private static final String DEFAULT_ENV_CODEC_CLASS ="cascom.fipa.envelope.BitEfficientEnvelopeCodec";
    
    /*
     * Specifier key in Profile. This should be in profile but because it is not, we have to put it in here
     * so this implementation would work with original versions of Jade-Leap.
     */
    public static final  String ENVCODECS ="envcodecs";
    
    private static final int    IN_PORT    = 10500;
    private static final String OUT_PORT   = "-1";
    private static final String PROXY_PORT = "1080";
    private static final String MAX_KA     = "10";
    
    private static final String POLICY     = "conservative"; //conservative or aggressive
    public static final String PREFIX     = "jade_mtp_http_";
    
    /**
     * This timeout is just for server side to finally close dead connections.
     * It must be sure that the connection is dead so this timeout should be
     * long, like an hour or so. This way the timeout should be longer than
     * timeout set in the client side for keeping the persistent connection open.
     * Client side is reponsible to close the connection in clean way sending
     * HTTP-request to this MTP when it has not sent messages for some time.
     *
     * If this timeout is less than the timeout in client side for clean closing,
     * server closes the connection in brutal way, which may cause problems
     * at the client side. You may change this timeout using option -jade_mtp_http_timeout
     */
    protected static final String TIMEOUT    = "3600000";
    private static final String DEFAULT_PAYLOAD_ENCODING = "ISO-8859-1";
    
    
    private int    numKA;
    private int    outPort;
    private String proxyHost;
    private int    proxyPort;
    private int    timeout;
    
    private boolean policy;
    private boolean keepAlive  = false;
    private boolean useProxy   = false;
    private boolean useOutPort = false;
    private boolean useHttps   = false;
    
    private String[] protocols = {"http"};
    private String FIPA_NAME = "fipa.mts.mtp.http.std";
    private Hashtable addr2srv = new Hashtable();
    
    //Object Keep-Alive connections
    private KeepAlive ka;
    private Object lock = new Object();
    
    // Warning: codecs should not be removed or added after init
    // operations getting codecs are not synchronized
    private Hashtable codecs;
    
    private static String default_codec_name = null;
    private static Logger logger = Logger.getMyLogger("MessageTransportProtocol");
    
    private ConnectionManager connectionManager = null;
    private String mainHost = "";
    
    
    /** MTP Interface Methods */
    public TransportAddress strToAddr(String rep) throws MTPException {
        try {
            return new HTTPAddress(rep);
        } catch(MalformedURLException mtpe) {
            throw new MTPException("Address mismatch: this is not a valid HTTP address.");
        }
    }
    
    public String addrToStr(TransportAddress ta) throws MTPException {
        try {
            return ((HTTPAddress) ta).toString();
        } catch(Exception e) {
            throw new MTPException(e.toString());
        }
    }
    
    public String getName() {
        return FIPA_NAME;
    }
    
    public String[] getSupportedProtocols() {
        return protocols;
    }
    
    /********************************
     *   InChannel Interface Methods *
     *********************************/
    
    /**
     * Old method, only for compliance with former versions (prior 3.0)
     */
    public TransportAddress activate(InChannel.Dispatcher disp)
    throws MTPException {
        try {
            return activate(disp,new ProfileImpl(new BasicProperties()));
        } catch(Exception e) {
            throw new MTPException(e.getMessage());
        }
    }
    
    public void activate(InChannel.Dispatcher disp, TransportAddress ta)
    throws MTPException {
        try {
            activate(disp,ta,new ProfileImpl(new BasicProperties()));
        } catch(Exception e) {
            throw new MTPException(e.getMessage());
        }
    }
    
    public TransportAddress activate(InChannel.Dispatcher disp, Profile p)
    throws MTPException {
        return activateServer(disp,null,p);
    }
    
    /**
     * Actual method to activate the HTTP MTP.
     *
     * Customizable parameters read from profile:<UL>
     * <LI><B>port</B>: the port this HTTP server listens to.</LI>
     * <LI><B>numKeepAlive</B>: Maximum number of keep-alive connections.
     * Default value is 10. Set to 0 to disable keep-alive coonections (possible performance impact).</LI>
     * <LI><B>proxyHost</B>: Proxy host name or IP-address. No default value.</LI>
     * <LI><B>proxyPort</B>: Default value is 1080.</LI>
     * <LI><B>outPort</B>: Fix port to be used by HTTP client (for firewall configuration).
     * Default value is freely chosen by Java</LI>
     * <LI><B>parser</B>: XML SAX2 parser implementation to use.
     * Default value is JDK 1.4 default parser.</LI>
     * <LI><B>policy</B>: "conservative" (default value) or "aggressive".
     * (see documentation for details).</LI>
     * <LI><B>timeout</B>: Timeout for dead socket abnormal closing in server side. <i>Please see ConnectionManager and its subclasses for client side timeouts</i></LI>
     *
     * </UL>
     *
     * Note that all these parameters must be prefixed with "jade_mtp_http_".
     */
    public void activate(InChannel.Dispatcher disp, TransportAddress ta, Profile p) throws MTPException {
        activateServer(disp, ta, p);
    }
    
    private TransportAddress activateServer(InChannel.Dispatcher disp, TransportAddress ta, Profile p)
    throws MTPException {
        
        //long time = System.currentTimeMillis();
        /*
         * Read available codecs
         */
        EnvelopeCodec defcodec = null;
        this.codecs = new Hashtable();
        try { // default codec must exist
            
            // some obfuscators may not handle forName()
            defcodec = new cascom.fipa.envelope.BitEfficientEnvelopeCodec();
            //defcodec = (EnvelopeCodec) Class.forName(DEFAULT_ENV_CODEC_CLASS).newInstance();
        } catch (Exception e){
            throw new MTPException("Could not load default Envelope codec ("+DEFAULT_ENV_CODEC_CLASS+", reason: \n"+e.getMessage());
        }
        this.codecs.put((Object)defcodec.getName().toLowerCase(), (Object)defcodec);
        this.default_codec_name = defcodec.getName().toLowerCase();
        
        List listOfCodecs = null;
        Iterator it = null;
        try {
            listOfCodecs =  p.getSpecifiers(ENVCODECS);
            it = listOfCodecs.iterator();
        } catch (ProfileException pe){}
        Specifier sp;
        EnvelopeCodec envc;
        
        while(it != null && it.hasNext()){
            try {
                sp = (Specifier) it.next();
                try {
                    envc = (EnvelopeCodec)Class.forName(sp.getClassName()).newInstance();
                    if(envc.getName() != null){
                        this.codecs.put((Object)envc.getName().toLowerCase(), (Object)envc);
                        default_codec_name = envc.getName().toLowerCase();
                    } else {
                        if(logger.isLoggable(Logger.WARNING))
                            logger.log(Logger.WARNING,"Warning: envelope codec was not loaded as getName() returned null");
                    }
                } catch (ClassNotFoundException cnf){
                    if(logger.isLoggable(Logger.WARNING))
                        logger.log(Logger.WARNING,"class not found:"+sp.getClass());
                } catch (ClassCastException cce){
                    if(logger.isLoggable(Logger.WARNING))
                        logger.log(Logger.WARNING,"not EnvelopeCodec:"+sp.getClass());
                } catch (Exception ne){
                    if(logger.isLoggable(Logger.WARNING))
                        logger.log(Logger.WARNING,"Exeption occured while loading envelopeCodec:"+ne.getMessage());
                }
            } catch (ClassCastException e){
                if(logger.isLoggable(Logger.WARNING))
                    logger.log(Logger.WARNING,"Warning: invalid specifier found while loading envelope codecs in jade.mtp.http2:"+e.getMessage());
            }
        }
        
        if(this.codecs.size() < 1){
            throw new MTPException("jade.mtp.http2.MessageTransportProtocol cannot be started without EnvelopeCodecs. Please specify them (same way as ACLCodecs and MTPs)");
        }
        
        //Comprobation of correct HTTPAddress
        int port = -1;
        boolean changePortIfBusy = false;
        //String saxClass = null;
        HTTPAddress hta = null;
        try {
            if (ta != null) {
                hta = (HTTPAddress)ta;
            } else {
                try {
                    // Create default HTTPAddress
                    String tmp;
                    if ((tmp = p.getParameter(PREFIX+"port",null)) != null) {
                        port = Integer.parseInt(tmp.trim());
                    } else {
                        // Use default port
                        port = IN_PORT;
                        changePortIfBusy = true;
                    }
                    if((tmp = p.getParameter(PREFIX+"host",null)) != null){
                        // In J2ME, the name or IP-address of local host should be given by user.
                        hta = new HTTPAddress(tmp,port, useHttps);
                    } else {
                        hta = new HTTPAddress(InetAddress.getLocalHost(),port, useHttps);
                    }
                } catch( MalformedURLException mexc ) {
                    throw new MTPException("Cannot activate MTP on default address: Malformed URL: "+InetAddress.getLocalHost());
                } catch( NumberFormatException nfexc ) {
                    throw new MTPException("Cannot activate MTP on default address: Invalid port");
                } catch( Exception e ) {
                    throw new MTPException("Cannot activate MTP on default address:"+e.getMessage());
                }
                
            }
            
            port = hta.getPortNo();
            if((port <= 0) || (port > 65535)) {
                throw new MTPException("Invalid port number "+ta.getPort());
            }
            
            // Parse other profile parameters
            // FIX-ME Why is numKA used also in HttpServer anyway? Should be used
            // only in ConnectionManager.
            numKA     = Integer.parseInt(p.getParameter(PREFIX+"numKeepAlive",MAX_KA));
            if (numKA > 0) {
                keepAlive = true;
                ka = new KeepAlive(numKA);
            }
            
            
            /* Parsed now in ConnectionManager
            policy    = (p.getParameter(PREFIX+"policy",POLICY).equals("aggressive"))?true:false;
             
             
            outPort   = Integer.parseInt(p.getParameter(PREFIX+"outPort",OUT_PORT));
            if (outPort != -1) {
                useOutPort = true;
            }
             
            proxyHost = p.getParameter(PREFIX+"proxyHost",null);
            if (proxyHost != null) {
                useProxy = true;
                proxyPort = Integer.parseInt(p.getParameter(PREFIX+"proxyPort",PROXY_PORT));
            }
             */
            
            timeout = Integer.parseInt(p.getParameter(PREFIX+"timeout",TIMEOUT));
            p.setParameter(p.getParameter(PREFIX+"timeout",TIMEOUT),TIMEOUT);
            
            
            if(p.getParameter(PREFIX+"connectionManager",null) != null){
                try {
                    this.connectionManager = (ConnectionManager)this.getClass().forName(p.getParameter(PREFIX+"connectionManager",null)).newInstance();
                } catch (Exception ce){
                    throw new MTPException("Error loading class :"+p.getParameter(PREFIX+"connectionManager",null)+":"+ce.getMessage());
                }
            } else {
                try {
                    this.connectionManager = (ConnectionManager)this.getClass().forName("jade.mtp.http2.ConnectionManager").newInstance();
                } catch (Exception ce){
                    throw new MTPException("jade.mtp.http2.ConnectionManager could not be loaded:"+ce.getMessage());
                }
            }
            this.mainHost = p.getParameter(p.MAIN_HOST,"none");
            
            
            try{
                HTTPSocketFactory.getInstance().configure(p, hta);
            } catch(Exception e){
                throw new MTPException("Error configuring Socket Factory", e);
            }
        } catch (ClassCastException cce) {
            throw new MTPException("User supplied transport address not supported.");
        } catch( NumberFormatException nexc ) {
            throw new MTPException(nexc.getMessage());
        }
        
        //Creation of the Server
        String err_str="2";
        
        
        
        try {
            //Create object server
            
            
            /*
             * User may define different HTTPServer to be used
             */
            HTTPServer srv = null;
            
            if(p.getParameter(PREFIX+"server",null) != null){
                try{
                    srv = (HTTPServer) this.getClass().forName(p.getParameter(PREFIX+"server",null)).newInstance();
                } catch (Exception e){
                    throw new MTPException("The server class defined with parameter"+PREFIX+"server not found or error loading it:"+e.getMessage());
                }
            } else {
                srv = new HTTPServer();
            }
            
            // init the server
            err_str+=" 3";
            
            HTTPAddress requestedAddress = hta;
            
            
            srv.init(port,disp,timeout,true,this.codecs,this.connectionManager,p);
            
            err_str+=" 4";
            if(port != srv.getLocalPort()){
                String anchor ="";
                if(hta.getAnchor() != null){
                    anchor += "#"+anchor;
                }
                try {
                    err_str+=" 5";
                    
                    System.out.println("== http://"+hta.getHost()+":"+srv.getLocalPort()+"/"+hta.getFile()+anchor);
                    
                    hta = new HTTPAddress(("http://"+hta.getHost()+":"+srv.getLocalPort()+"/"+hta.getFile()+anchor).trim());
                } catch (Exception e4){
                    err_str+=" 5e";
                    System.out.println("Error in new Address: http://"+hta.getHost()+":"+srv.getLocalPort()+"/"+hta.getFile()+anchor);
                    e4.printStackTrace();
                }
            }
            
            err_str+=" 6";
            this.connectionManager.init(p,hta,disp,this.codecs);
            
            err_str+=" 7";
            // if gateway is used, JADE must see this MTP as the address of the
            // actual gateway so when agents are answering to agents they use
            // the http-address of the gateway, not this MTP
            if(this.connectionManager.getGatewayAddress() != null){
                hta = this.connectionManager.getGatewayAddress();
            }
            
           /**
             * Allows using port forwarding of firewall:
             */
            String prm = "allow-port-forwarding";
            if(p.getParameter(PREFIX+prm, null) != null){
                hta = new HTTPAddress(requestedAddress.getHost(),srv.getLocalPort(), false);
            }            
            
            
            //Save the reference to HTTPServer
            addr2srv.put(hta.toString(),srv);
            //Execute server
            err_str+=" 8";
            srv.start();
            err_str+=" 9";
            if(logger.isLoggable(Logger.INFO))
                logger.log(Logger.INFO,"FIPA-HTTP MTP (version 1.0 ) running at "+hta.toString());
            return hta;
        } catch(Exception e ) {
            throw new MTPException("While activating MTP got exception "+e.getMessage()+" (error:"+err_str+")");
        }
    }
    
    
    public void deactivate(TransportAddress ta) throws MTPException {
// Shutdown HTTP Server
        HTTPServer srv = (HTTPServer)addr2srv.get(ta.toString());
        if( srv != null ) {
            addr2srv.remove(ta.toString());
            srv.desactivate();
            //srv.interrupt();
        } else {
            throw new MTPException("No server on address "+ta);
        }
    }
    
    public void deactivate() throws MTPException {
        for(Enumeration it=addr2srv.keys(); it.hasMoreElements() ; ) {
            HTTPServer ta=(HTTPServer) addr2srv.get((String)it.nextElement());
            ta.desactivate();
        }
        addr2srv.clear();
    }
    
    
    /********************************
     *  OutChannel Interface Methods *
     *********************************/
    
    public void deliver(String addr, Envelope env, byte[] payload) throws MTPException {
        //System.err.println("MTP deliver 1");
        if(addr == null || env == null || payload == null){
            throw new MTPException(" Address, envelope or payload was null");
        }
        
        //synchronized(lock){
        //System.err.println("MTP deliver 2");
        // Is this a configuration request for this component and should not be delivered?
        HTTPAddress htadr = null;
        try {
            htadr = new HTTPAddress(addr);
        } catch (Exception e){
            throw new MTPException("Illegal address:"+addr);
        }
        
            /*
             * This is configuration request that should be handled by ConnectionManager
             */
        if(htadr.getHost().equals(this.mainHost) && htadr.getPort().equals("0")){
            //System.err.println("Config request");
            this.connectionManager.handleConfigurationRequest(env, payload);
            return;
        }
        //System.err.println("MTP deliver 3");
        
        // encode the envelope using requested encoding or default encoding if requested not available
        env.setPayloadLength(new Long((long)payload.length));
        EnvelopeCodec envCodec = null;
        FipaHttpObject fho = new FipaHttpObject();
        
            /*
            if(env.getAclRepresentation() != null && this.codecs.containsKey((Object)env.getAclRepresentation().toLowerCase())){
                try {
                    envCodec = (EnvelopeCodec)this.codecs.get((Object)env.getAclRepresentation().toLowerCase());
                } catch (Exception cce){
                    throw new MTPException("Error while delivering via MTP in jade.mtp.http2.MessageTransportProtocol, finding EnvelopeCodec:"+cce.getMessage());
                }
            } else {
             */
        // FIXME: is the acl-presentation is not supported, we should negotiate for proper encoding
        try {
            envCodec = (EnvelopeCodec)this.codecs.get((Object)default_codec_name);
            fho.setEnvelopePresentation(envCodec.getName());
        } catch (Exception en){
            throw new MTPException("Error while delivering via MTP in jade.mtp.http2.MessageTransportProtocol, finding default EnvelopeCodec:"+en.getMessage());
        }
        //}
        //System.err.println("MTP deliver 4");
        
        byte[] encodedEnvelopeBytes = null;
        try {
            encodedEnvelopeBytes = envCodec.encode(env);
        } catch (Exception ee) {
            throw new MTPException("Error while encoding envelope:"+ee.getMessage());
        }
        
            /*
            System.out.println("*** Envelope ***");
            for(int i=0; i < encodedEnvelopeBytes.length; i++){
                System.out.print((char)encodedEnvelopeBytes[i]);
                if(i % 2 == 0){
                    System.out.print("");
                }
            }
            System.out.println("***");
             
             */
        // create request object and add all information related to envelope and acl
        fho.setEnvelope(encodedEnvelopeBytes);
        fho.setACLMessage(payload);
        fho.setACLMsgPresentation(env.getAclRepresentation());
        fho.setIsFipaRequest(true);
        if(env.getPayloadEncoding() != null){
            fho.setPayloadEncoding(env.getPayloadEncoding());
        } else {
            fho.setPayloadEncoding("ISO-8859-1");
        }
        
        fho.setEnvelopePresentation(envCodec.getName());
        //System.err.println("MTP deliver 5");
        
        // deliver the envelope using connection manager services
        try {
            this.connectionManager.deliver(htadr, fho);
        } catch (Exception e){
            e.printStackTrace();
            throw new MTPException(e.getMessage());
        }
        
        System.gc();
        /*
        System.err.println("Envelope:");
        for(int i=0; i < encodedEnvelopeBytes.length; i++){
                    System.err.print(Integer.toHexString((int)encodedEnvelopeBytes[i])+" ");
        }
        System.err.println("=== Size:"+encodedEnvelopeBytes.length);
         
         */
        //}
    }
    /*
    private int sendOut(KeepAlive.KAConnection kac, byte[] req, boolean newC) throws IOException {
        //Capture the streams
        OutputStream os = kac.getOut();
        InputStream is = kac.getIn();
        HTTPIO.writeAll(os,req);
        //Capture the HTTPresponse
        StringBuffer typeConnection = new StringBuffer();
        int code = HTTPIO.getResponseCode(is,typeConnection);
        if (!HTTPIO.KA.equals(typeConnection.toString())) {
            // Close the connection
            kac.close();
            if (!newC) {
                ka.remove(kac);
            }
        } else if (newC) {
            // Store the new connection
            ka.add(kac);
        }
        return code;
    }
     */
} // End of MessageTransportProtocol class
