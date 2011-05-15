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
//import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;

import cascom.net.ServerSocket;
import cascom.net.Socket;
import cascom.net.SocketException;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.lang.Exception;
//import jade.core.AID;

import cascom.fipa.util.BufferedInputStream;
import cascom.fipa.util.BufferedOutputStream;

import cascom.fipa.envelope.EnvelopeCodec;

// needed only for debugging...
//import cascom.fipa.acl.*;
//import cascom.fipa.util.*;

import java.util.*;
import jade.mtp.InChannel;
import jade.mtp.InChannel.Dispatcher;
import jade.mtp.MTPException;
import jade.domain.FIPAAgentManagement.Envelope;
import jade.util.Logger;
import jade.core.Profile;

//import jade.lang.acl.*;


/**
 * This class implements server side of HTTP message transport protocol.
 *
 * @author Jose Antonio Exposito
 * @author MARISM-A Development group ( marisma-info@ccd.uab.es )
 * @version 0.1
 * @author Nicolas Lhuillier (Motorola Labs)
 * @version 1.0
 * @author Ahti Syreeni - TeliaSonera
 * @version 1.1
 */

public class HTTPServer extends Thread {
    
    // Codec class
    //static String CODEC = "org.apache.xerces.parsers.SAXParser";
    //static String CODEC   = "org.apache.crimson.parser.XMLReaderImpl";
    Hashtable codecs;
    
    protected int port;
    protected InChannel.Dispatcher dispatcher;
    //private int maxKA;
    protected int timeout;
    protected ServerSocket server;
    
    //logging
    protected static Logger logger = Logger.getMyLogger("HTTPServer");
    
    //private Vector threads; // for keep alive connections
    
    //attribute for synchronized
    protected static Object lock = new Object();
    
    // the flag that shows if the server is active or not
    protected boolean active = true;
    
    protected ConnectionManager cm = null;
    
    
    /**
     *  Inits the HTTPServer
     */
    public void init(int p, InChannel.Dispatcher d, int t, boolean changePortIfBusy,
            Hashtable codecs, ConnectionManager cm, Profile profile) throws MTPException {
        port = p;
        
        //logger.log(Logger.INFO,"Initing Server");
        
        dispatcher = d;
        this.codecs = codecs;
        this.cm = cm;
        timeout = t;
        ServerThread.timeout = timeout;
        try {
            //server = new ServerSocket(port);
            //logger.log(Logger.INFO,"Starting ServerSocket");
            server = HTTPSocketFactory.getInstance().createServerSocket(port);
            //logger.log(Logger.INFO,"ServerSocket started without errors");
        } catch (IOException ioe) {
            //logger.log(Logger.WARNING,"IOException creating ServerSocket "+ioe.getMessage());
            
            if (changePortIfBusy) {
                // The specified port is busy. Let the system find a free one
                //server = new ServerSocket(0);
                if(logger.isLoggable(Logger.WARNING)){
                    logger.log(Logger.WARNING,"Port "+p+" is already in used, selected another one");
                }
                
                try {
                    server = HTTPSocketFactory.getInstance().createServerSocket(0);
                } catch (IOException io){
                    logger.log(Logger.WARNING,"IOException (2) creating ServerSocket "+io.getMessage());
                    throw new MTPException("Can not create server even if changing port:"+io.getMessage());
                }
            } else {
                logger.log(Logger.WARNING,"Change port if busy is false, returning MTPException");
                throw new MTPException(ioe.getMessage());
            }
        }
    }
    
    /**
     *  Returns the envelope codecs used by this HTTPServer.
     */
    public Hashtable getEnvelopeCodecs(){
        return this.codecs;
    }
    
    public HTTPServer(){}
    
    
    /**
     * Desactivate The HTTPServerThread and all other sub-threads
     **/
    public synchronized void desactivate() {
        active = false;
        if(logger.isLoggable(Logger.INFO))
            logger.log(Logger.INFO,"desactivating httpserver");
        
        /*
        //Stop keep-alive Threads
        for(int i=0 ; i < threads.size(); i++) {
            ((ServerThread) threads.elementAt(i)).shutdown();
        }
        // The non-keep-alive will close themselves after a while
         */
        this.cm.closeAllConnections();
        
        if(server != null){
            try {
                server.close();
            } catch(Exception e) { // Does nothing as we asked to close
                try {
                    server.close();
                } catch(Exception e2) { // Does nothing as we asked to close
                    if(logger.isLoggable(Logger.WARNING))
                        logger.log(Logger.WARNING,"Server socket could not be closed:"+e2.getMessage());
                }
            }
        }
        server = null;
    }
    
    int getLocalPort() {
        return server.getLocalPort();
    }
    
    
    
    /**
     * Entry point for the master server thread
     */
    public void run() {
        //logger.log(Logger.INFO,"Server running, active:"+active);
        while(active) {  //Accept the input connections            
            try {
                Socket client = server.accept();
                //logger.log(Logger.INFO,"connection accepted");
                client.setSoTimeout(timeout);
                //logger.log(Logger.INFO,"New serverthread");
                new ServerThread(client,dispatcher,this.codecs, this.cm).start();
                //logger.log(Logger.INFO,"New serverthread started");
            } catch(InterruptedIOException e ) {
                //System.err.println("Expected timeout for serversocket");
                //e.printStackTrace();
                // Some virtual machines, like IBM J9, may have internal time-outs
                // which will cause InterruptedIOException after some time (like 3 seconds)
            }catch (Exception e){
                logger.log(Logger.SEVERE,System.currentTimeMillis()+":Error in HTTPServer:"+e.getMessage());
                e.printStackTrace();
            }
        }
        if(logger.isLoggable(Logger.INFO))
            logger.log(Logger.INFO,"HTTP Server closed on port "+port);
    }
    
    
    /**
     * ServerThread which reads the input, parses HTTP request
     * using HTTPIO and delivers message to agent platform or
     * let ConnectionManager to handle the request if it is not
     * FIPA-request.
     */
    public static class ServerThread extends Thread {
        protected static long timeout;
        
        //private HTTPServer           father;
        protected Socket               client;
        protected BufferedInputStream  input;
        
        protected BufferedOutputStream output;
        protected InChannel.Dispatcher dispatcher;
        //private XMLCodec             codec;
        //private boolean             keepAlive = false;
        protected boolean             active    = false;
        protected Hashtable           codecs;
        protected ConnectionManager   cm;
        private String sender ="Unknown";
        
        public ServerThread(Socket s, InputStream openStream,InChannel.Dispatcher d, Hashtable theCodecs, ConnectionManager cm){
            this.client = s;
            this.input = new BufferedInputStream(openStream);
            
            // to prevent blocking, the BufferedInputStream is not buffered unless
            // the number of bytes that can be read is known
            //this.input.setNextEOS(1);
            this.dispatcher = d;
            this.codecs = theCodecs;
            this.cm = cm;
            if(s != null){
                try {
                this.sender = s.getAddress();
                } catch (Exception e){}
            }
        }
        
        public ServerThread(Socket s, InChannel.Dispatcher d, Hashtable theCodecs, ConnectionManager cm) {
            //father = f;
            client = s;
            dispatcher = d;
            codecs = theCodecs;
            this.cm = cm;
            try {
                this.input = new BufferedInputStream(this.client.getInputStream());
                // to prevent blocking, the BufferedInputStream is set not to buffer until
                // the number of bytes that can be read before blocking is known
                //this.input.setNextEOS(1);
            } catch (Exception e){
                System.err.println(System.currentTimeMillis()+": error creating inputStream in ServerThread");
                e.printStackTrace();
            }
            if(s != null){
                try {
                this.sender = s.getAddress();
                } catch (Exception e){}
            }
        }
        
        
        /** Constructor: Store client port*/
        public OutputStream getOutputStream() throws IOException {
            if(this.output == null){
                this.output = new BufferedOutputStream(this.client.getOutputStream());
            }
            return this.output;
        }
        public Socket getSocket(){
            return this.client;
        }
        
        public InputStream getInputStream() throws IOException {
            if(this.input == null){
                this.input = new BufferedInputStream(this.client.getInputStream());
            }
            return this.input;
        }
        
        public void setActive(boolean active){
            this.active = active;
        }
        
        
        /**
         * Entry point for the slave server thread
         */
        
        
        
        long latestError = 0;
     
        long ioTimeOut = timeout;
        
        public void run() {
            this.active = true;
            try {
                
                // we can read input stream but we should never use output
                // because ConnectionManager (or its subclasses) handle
                // output and delivering messages out
                // output = client.getOutputStream();
                FipaHttpObject ro = new FipaHttpObject();
                while(this.active){
                    //System.err.println("HttpServer reading");
                    //logger.log(Logger.INFO,"HttpServer reading");
                    // System.err.println("HttpServer handling request");
                    // codec = new XMLCodec(HTTPServer.CODEC);
                    // Read the request from client
                    // read the envelope, aclmessage and http-headers
                    ro.reset();
                    
                        //TimerDebugger ttask = new TimerDebugger(ro, System.currentTimeMillis()+": Parsing taking too long in HttpServer.ServerThread: current input:");
                        //java.util.Timer timer = new Timer();
                        //timer.schedule(ttask,20000);
                    try {
                        //Debug                
             
                        HTTPIO.parseRequest(ro, this.input);
                        
                        //timer.cancel();
                        
                        //System.err.println("Server 3:"+(ro==null));
                        //System.err.println("Parsing");
                    } catch (IOException ioe){
                        //timer.cancel();
                        //ioe.printStackTrace();
                        this.cm.readError(this, ro, ioe);
                        if(this.active){
                            if(latestError+ioTimeOut < System.currentTimeMillis()){
                                latestError = System.currentTimeMillis();                                
                                logger.log(Logger.FINER,"Still trying to read input from sender:"+this.sender+" ("+ioe.getClass().getName()+", msg:"+ioe.getMessage()+")");
                                continue;
                            } else {
                                logger.log(Logger.FINER,"Stop reading input from sender:"+this.sender+". Connection was closed ("+ioe.getClass().getName()+", msg:"+ioe.getMessage()+") ");
                                break;
                            }
                        } else {
                           logger.log(Logger.FINER,"Stop reading input from sender:"+this.sender+". Connection was closed ("+ioe.getClass().getName()+", msg:"+ioe.getMessage()+") ");
                           break;
                        }
                    } catch (Exception e2){
                        logger.log(Logger.WARNING,"Unexpected exception while parsing in HttpServer.ServerThread ("+e2.getClass().getName()+", msg:"+e2.getMessage()+")");
                        e2.printStackTrace();
                        break;
                    }
                    //System.err.println("Server 4");
                    
                    if(ro == null){
                        continue;
                    } else if(ro.isFipaRequest()){
                        //System.err.println("Parsed fiparequest");
                        //logger.log(Logger.WARNING,"Parsed fiparequest");
                        
                        if(!codecs.containsKey((Object)ro.getEnvelopePresentation().toLowerCase())){
                            this.handleUnsupportedEncoding(ro);
                            continue;
                        }
                        
                        Envelope env = null;
                        
                            /*//Debug
System.out.println("ACLMessage:");
for(int i=0; i<ro.getACLMessage().length;i++){
    System.out.print(ro.getACLMessage()[i]);
}
System.out.println("-----acl");*/
                        
                        try {                            
                            EnvelopeCodec rightCodec = ((EnvelopeCodec)codecs.get((Object)ro.getEnvelopePresentation().toLowerCase()));
                            env = rightCodec.decode(ro.getEnvelope());
                        } catch (Exception pe){
                            logger.log(Logger.WARNING, System.currentTimeMillis()+": error decoding envelope:"+pe.getMessage());
                            pe.printStackTrace();
                            ro.setErrorMsg(pe.getMessage());
                            this.handleInternalError(ro);
                            dispatcher.notifyAll();
                            continue;
                        }
                        if ((env.getPayloadLength() != null)&&(env.getPayloadLength().longValue() != ro.getACLMessage().length)) {
                            if(logger.isLoggable(Logger.WARNING))
                                logger.log(Logger.WARNING,"Payload size does not match envelope information: env:"+env.getPayloadLength()+", aclmsg:"+ro.getACLMessage().length);
                            
                        }
                        //System.err.println("Dispaching message to agent "+((AID)env.getAllIntendedReceiver().next()).toString());
                        //logger.log(Logger.WARNING,"Dispatch");
                        
                        this.cm.messageDelivered(this,ro);
                        
                        synchronized (dispatcher) {
                            dispatcher.dispatchMessage(env,ro.getACLMessage());
                            dispatcher.notifyAll();
                            //System.err.println("Message dispatched to JADE, ACL-length:"+ro.getACLMessage().length+" receiver:|"+env.getAllTo().next().toString()+"|");
                        }
                        
                        //logger.log(Logger.WARNING,"Dispatched");
                        /*
                        if (HTTPIO.KA.toLowerCase().equals(ro.getConnectionType().toLowerCase())) {
                            if (! keepAlive) {
                                // This thread is not known yet
                              if (father.isSpaceLeft()) {
                                    // There is space left for a new KA
                                    active = true;
                                    keepAlive = true;
                                    father.addThread(this);
                                    //this.client.setKeepAlive(true);
                                } else {
                                    // This is a to-be-closed thread
                                    ro.setHttpHeader(FipaHttpObject.HTTP_CONNECTION, FipaHttpObject.HTTP_CONNECTION_CL);
                                    //this.client.setKeepAlive(false);
                                }
                            }
                        } else {
                            active = false;
                            //this.client.setKeepAlive(false);
                        }
                         */
                        // inform ConnectionManager that this message is delivered
                    } else if(ro.isResponse()){
//System.err.println("Parsed response");
                        // message was response, not for agent communication and thus handled
                        // by ConnectionManager
                        this.cm.handleHttpResponse(this,ro);
                    } else if(ro.isHttpRequest()){
//System.err.println("Parsed httprequest");
                        // this is pure http request, let ConnectionManager handle it
                        this.cm.handleHttpRequest(this,ro);
                    } else {
//System.err.println("Parsed invalidRequest");
                        this.handleInvalidRequest(ro);
                    }
                    
                }
            }   catch(Exception e ) {
                if(logger.isLoggable(Logger.SEVERE)){
                    logger.log(Logger.SEVERE,"Unexpected error while handling parsed input of sender "+this.sender+" ("+e.getClass().getName()+", msg:"+e.getMessage()+")");
                    e.printStackTrace();
                }
            }
            //System.err.println("Thread shutted down");
            shutdown();
        }
        
        
        private void shutdown() {
            if(client != null){
                active = false;
                //System.err.println(System.currentTimeMillis()+": ### HttpServerThread "+Thread.currentThread()+" shutted down");
                this.cm.closeConnections(this);
                this.client = null;
            } else {
                if(logger.isLoggable(Logger.WARNING)){
                    logger.log(Logger.WARNING,"HTTPServer: could not properly close connections when shutting down thread: client was null");
                }
            }
        }
        
        protected void handleUnsupportedEncoding(FipaHttpObject fo) {
            //FIXME This should send FIPA exception (ACL), not HTTP error code
            fo.setErrorMsg("Unsupported envelope encoding:"+fo.getEnvelopePresentation());
            this.sendHttpError(fo);
        }
        
        
        protected void handleInvalidRequest(FipaHttpObject fo) {
            fo.setErrorMsg("Invalid request:"+fo.getErrorMsg());
            this.sendHttpError(fo);
        }
        
        protected void handleInternalError(FipaHttpObject fo) {
            fo.setErrorMsg("Internal error in ServerThread.Run"+fo.getErrorMsg());
            this.sendHttpError(fo);
        }
        
        protected void sendHttpError(FipaHttpObject ro){
            if(logger.isLoggable(Logger.SEVERE))
                logger.log(Logger.SEVERE,"Error in HTTPServer.ServerThread:"+ro.getErrorMsg());
            ro.setStatusCode(HTTPIO.ERROR);
            this.cm.undeliveredMessage(this,ro);
        }
    } //End of ServerThread class
}//End of HTTPServer class
