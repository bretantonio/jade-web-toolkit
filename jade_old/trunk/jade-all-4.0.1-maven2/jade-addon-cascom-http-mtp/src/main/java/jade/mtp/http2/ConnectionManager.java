/**
 * ***************************************************************
 * JADE - Java Agent DEvelopment Framework is a framework to develop
 * multi-agent systems in compliance with the FIPA specifications.
 * Copyright (C) 2000 CSELT S.p.A.
 *
 * GNU Lesser General Public License
 *
 * This library is free software; you can redistribute it and/or
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
 * **************************************************************
 */

package jade.mtp.http2;
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
import jade.util.leap.Iterator;
import jade.core.Profile;
import jade.core.ProfileException;
import jade.core.Specifier;
import jade.lang.acl.*;
import cascom.fipa.acl.*;
import cascom.fipa.envelope.EnvelopeCodec;
import jade.util.Logger;
import cascom.net.InetAddress;
import cascom.net.SocketFactory;
import cascom.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

/**
 * ConnectionManager is a class for handling issues concerning HTTP connections.
 * The class offers funtionality for delivering messages to HTTP connection and
 * handling HTTP messages which are used for HTTP connection maintaining. Sub classes
 * may offer more advanced features such as message buffering or proxy behaviour.
 *
 * ConnectionManager can read following parameters from jade.core.Profile in init-method.
 * Parameters must be prefixed with prefix jade_mtp_http
 *
 * <ul>
 * <li><b>response-timeout</b>: Timeout for request to be responsed, otherwise the server is considered dead</li>
 * <li><b>keepalive-timeout</b>: Timeout for keep-alive connections. This client will request the connection to be closed after that timeout.</li>
 * <li><b>linger</b>: time for Socket to deliver possible pending output after the socket is closed. </li>
 * <li><b>proxy</b>: address of proxy (Not tested, should not be used, use CASCOM Messaging Gateway instead)</li>
 * <li><b>outPort</b>: use specific local out port </li>
 * </ul>
 *
 * @author Ahti Syreeni - TeliaSonera
 */
public class ConnectionManager {
    /** Prefix for properties related to this mtp in Profile */
    protected static final String PREFIX = "jade_mtp_http_";
    
    /** Timeout for server response **/
    protected int responseTimeout = 15000;
    
    /** Timeout for persistant connection to be open **/
    private int connectionOpenTime = 72000;
    
    /** Max number of open connections **/
    protected int maxOpenConnections = 1024;
    
    /** The linger time for socket */
    protected int linger = 1000;
    
    
    /** Policy **/
    protected boolean aggressivePolicy = false;
    
    /** Persistent connections are default in FIPA HTTP (based on HTTP 1.1) **/
    protected boolean usePersistentConnections = true;
    
    /** Http address of proxy */
    protected HTTPAddress proxy = null;
    
    /** Proxy should be used or not **/
    protected boolean useProxy = false;
    
    /** Port in local socket **/
    protected int outPort = -1;
    
    /** JADE dispatcher used to deliver messages to platform **/
    protected InChannel.Dispatcher disp = null;
    
    /** Logger **/
    protected static Logger logger = Logger.getMyLogger("ConnectionManager");
    private KeepAlive persistentConnections = null;
    private Hashtable persistantServerThreads = null;
    private Object outLock = new Object();
    private Object duplicatSocketLock = new Object();
    private Timer closeTimer = null;
    
    
    
    
    
    /**
     * Closes keep-alive connections after a while so those will not be
     * left open forever.
     */
    protected class CloseConnections extends TimerTask {
        public void run(){
            if(usePersistentConnections && persistentConnections != null){
                Enumeration cons = persistentConnections.elements();
                KeepAlive.KAConnection ka;
                long time = System.currentTimeMillis() - connectionOpenTime;
                while(cons.hasMoreElements()){
                    ka = (KeepAlive.KAConnection) cons.nextElement();
                    if(ka != null && ka.getTimeStamp() < time){
                        FipaHttpObject fo = new FipaHttpObject();
                        fo.setIsHttpRequest(true);
                        fo.setHttpHeader(FipaHttpObject.HTTP_CONNECTION, FipaHttpObject.HTTP_CONNECTION_CL);
                        fo.setHttpHeader(fo.HTTP_HOST, ka.getAddress().toString());
                        fo.setHostAddr(ka.getAddress());
                        try {
                            /*
                            System.err.println(System.currentTimeMillis()+":ConnectionManager.CloseConnections now shutting down persistent thread");
                            deliver(ka, ka.getAddress(), fo);
                             */
                        } catch (Exception e){
                            System.err.println(System.currentTimeMillis()+": Error closing idle persistant ka:"+e.getMessage());
                            ka.close();
                            persistentConnections.remove(ka);
                        }
                    }
                }
            }
        }
    }
    
    
    
    /**
     * Inits the ConnectionManager
     *
     * @param p Profile
     * @param addr Address of local HTTPServer
     * @param disp Dispatcher for platform
     * @param envelopeCodecs EnvelopeCodecs used to encode/decode envelopes
     * @throws MTPException if arguments found in Profile are not properly set
     */
    public void init(Profile p, HTTPAddress addr, InChannel.Dispatcher disp, Hashtable envelopeCodecs) throws MTPException {
        // use persistent connections as default, default in HTTP 1.1
        if("false".equals(p.getParameter(PREFIX+"persistentConnections","true"))){
            this.usePersistentConnections = false;
        } else {
            this.usePersistentConnections = true;
            this.persistentConnections = new KeepAlive(this.maxOpenConnections);
            this.closeTimer = new Timer();
            //this.closeTimer.schedule(new CloseConnections(), this.connectionOpenTime, this.connectionOpenTime);
            
            if(p.getParameter(PREFIX+"policy", "none").equals("aggressive")){
                this.aggressivePolicy = true;
            }
        }
        
        try {
            this.responseTimeout = Integer.parseInt(p.getParameter(PREFIX+"response-timeout", ""+this.responseTimeout));
        } catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("Connection manager response timeout:"+this.responseTimeout);
        
        try {
            this.connectionOpenTime = Integer.parseInt(p.getParameter(PREFIX+"keepalive-timeout", ""+this.connectionOpenTime));
        } catch (Exception e){
            e.printStackTrace();
        }
        
        try {
            this.linger = Integer.parseInt(p.getParameter(PREFIX+"linger", ""+this.linger));
        } catch (Exception e){
            logger.log(Logger.WARNING,"Error setting linger");
            e.printStackTrace();
        }
        
        
        String proxyaddr = p.getParameter(PREFIX+"proxy",null);
        if(proxyaddr != null){
            // decide address
            try {
                if(p.getParameter(PREFIX+"proxyPort",null) != null){
                    this.proxy = new HTTPAddress(proxyaddr,Integer.parseInt(p.getParameter(PREFIX+"proxyPort","")), false);
                } else {
                    this.proxy = new HTTPAddress(proxyaddr); //false=>do_not_use HTTPS with the proxy
                }
            } catch(Exception e){
                throw new MTPException("Invalid argument for proxy address or proxyPort");
            }
            this.useProxy = true;
        }
        
        if(p.getParameter(PREFIX+"outPort", null) != null){
            try {
                this.outPort   = Integer.parseInt(p.getParameter(PREFIX+"outPort", "-1"));
            } catch (Exception e4){
                throw new MTPException("invalid outport specified");
            }
        }
        
        this.disp = disp;
        this.persistantServerThreads = new Hashtable(this.maxOpenConnections);
    }
    
    /**
     * Returns the address of gateway used by this ConnectionManger. Allways null
     * as this connection manager does not use gateway.
     * @return HTTPAddress always null
     */
    public HTTPAddress getGatewayAddress(){
        return null;
    }
    
    
    /**
     * Delivers the FipaHttpObject to given destination address
     * @param host The address of destination host
     * @param httpObject The message to be sent.
     * @throws MTPException if error occures during transmission
     */
    public void deliver(HTTPAddress host, FipaHttpObject httpObject) throws MTPException {
        //System.err.println("CM deliver");
        //boolean isNewConnection = false;
        httpObject.setHttpHeader(FipaHttpObject.HTTP_HOST, host.getHost()+":"+host.getPortNo());
        KeepAlive.KAConnection kac = null;
        
        //System.err.println("Waiting for open 0");
        
        // use address of proxy instead real address
        if(this.useProxy){
            host = this.proxy;
        }
        
        if(this.usePersistentConnections){
            // Get the keep alive -connection or create new socket for connection
            //System.err.println("Waiting for open");
            synchronized(this.duplicatSocketLock){
                //System.err.print("|Waiting for open 2");
                kac = this.persistentConnections.getConnection(host);
                //System.err.println("|Waiting for open 3");
                if(kac == null){
                    try {
                        //System.err.println("Get KA connection "+host.toString());
                        //System.err.println("Creating socket 1");
                        kac = this.createConnection(new KeepAlive.KAConnection(host), true);
                        //System.err.println("Adding KA connection "+host.toString());
                        this.persistentConnections.add(kac);
                        //System.err.println("Creating socket 3");
                    } catch (Exception ioe){
                        ioe.printStackTrace();
                        //logger.log(Logger.WARNING,"[1] Unable to connect to "+host.toString()+" ("+ioe.getMessage()+")");
                        throw new MTPException(System.currentTimeMillis()+": Unable to connect to "+host.toString()+":"+ioe.getMessage()+" while creating new socket for new KA-connection (ConnectionManager)");
                    }
                }
            }
            if(httpObject.getConnectionType() == null){
                httpObject.setHttpHeader(FipaHttpObject.HTTP_CONNECTION,
                        FipaHttpObject.HTTP_CONNECTION_KA);
                
            }
        } else {
            //System.err.println("not using persistent connections?");
            httpObject.setHttpHeader(FipaHttpObject.HTTP_CONNECTION,
                    FipaHttpObject.HTTP_CONNECTION_CL);
            try {
                kac = this.createConnection(new KeepAlive.KAConnection(host), false);
            } catch (Exception ioe){
                ioe.printStackTrace();
                //logger.log(Logger.WARNING,"[2] Unable to connect to "+host.toString()+" ("+ioe.getMessage()+")");
                throw new MTPException(System.currentTimeMillis()+":Unable to connect to "+host.toString()+":"+ioe.getMessage()+" while creating socket for new connection (not KA, ConnectionManager)");
            }
        }
        
        
        /*
         * Try to deliver. This is synchronized according to keep-alive connections
         * so that if one destination platform does not respond this MTP can still
         * send messages to other platforms at the same time.
         */
        
        synchronized(kac){
            try {
                if(this.deliver(kac, host, httpObject).getStatusCodeInt() == 200){
                    return;
                } else {
                    logger.log(Logger.WARNING, System.currentTimeMillis()+":Warning: http response not OK, retrying");
                }
            } catch (Exception io){
                io.printStackTrace();
                logger.log(Logger.WARNING, System.currentTimeMillis()+": Error delivering message:"+io.getMessage()+"ConnectionManager retrying, closing dead socke");
                //io.printStackTrace();
            }
            
            //retry, close the current socket and open new socket for connection
            kac.close();
            
            try {
                // only create new socket for host
                this.createConnection(kac, this.usePersistentConnections);
                kac = this.createConnection(new KeepAlive.KAConnection(host), true);
                System.err.println("Adding KA connection "+host.toString());
                this.persistentConnections.add(kac);
            } catch (Exception e1){
                kac.setBusy(false);
                kac.close();
                e1.printStackTrace();
                //e1.printStackTrace();
                //System.err.println("Sending mtperror unable to connect 1");
                //logger.log(Logger.WARNING,"[3] Unable to connect to "+host.toString()+" ("+e1.getMessage()+")");
                throw new MTPException(System.currentTimeMillis()+": Unable to connect to "+host.toString()+":"+e1.getMessage()+" while creating new socket for existing KA-connection");
            }
            
            String errormessage = null;
            try {
                //System.err.println("Deliver again 5");
                if(this.deliver(kac, host, httpObject).getStatusCodeInt() == 200){
                    //System.err.println("Deliver again 6");
                    return;
                } else {
                    errormessage ="HTTP response not OK";
                }
            } catch (Exception io3){
                io3.printStackTrace();
                errormessage = System.currentTimeMillis()+": Exception while delivering (3):"+io3.getMessage();
                //io3.printStackTrace();
            }
            kac.setBusy(false);
            kac.close();
            //System.err.println("Socket closed finally");
            logger.log(Logger.WARNING,System.currentTimeMillis()+": [4] Unable to deliver message to "+host.toString()+" ("+errormessage+")");
            throw new MTPException(System.currentTimeMillis()+":Unable to deliver message to "+host.toString()+" ("+errormessage+")");
        }
    }
    
        /*
                    this.persistentConnections.remove(kac);
                }
                kac = null;
                if(logger.isLoggable(Logger.WARNING)){
                    logger.log(Logger.WARNING, "ConnectionManager closing dead socket");
                }
                // This connection was closed or first attempt did not work, must reconnect
                // It will be done because kac == null
         
            }
        } else {
            httpObject.setHttpHeader(FipaHttpObject.HTTP_CONNECTION,
                    FipaHttpObject.HTTP_CONNECTION_CL);
        }
         
        if(this.usePersistentConnections){
            // we cannot be sure if the socket is already being created for this host
            synchronized(this.duplicatSocketLock){
                kac = this.persistentConnections.getConnection(host);
            if(httpObject.getConnectionType() == null){
                httpObject.setHttpHeader(FipaHttpObject.HTTP_CONNECTION,
                        FipaHttpObject.HTTP_CONNECTION_KA);
         
            }
            }
         
            if(kac != null){
                synchronized(kac){
                    try {
                        kac.setBusy(true);
                        //System.err.println("Now sending using ConnectionManager");
                        if(this.deliver(kac, host, httpObject).getStatusCodeInt() == 200){
                            kac.setBusy(false);
                            kac.setTimeStamp();
                            //System.err.println("delivery was succesfull using ka connection");
                            return;
                        }
                        kac.setBusy(false);
         
         
                    } catch (Exception io){
                        //System.err.println("Dead socket recognized");
                        kac.setBusy(false);
                        //io.getMessage();
                    }
                    kac.close();
                    this.persistentConnections.remove(kac);
                }
                kac = null;
                if(logger.isLoggable(Logger.WARNING)){
                    logger.log(Logger.WARNING, "ConnectionManager closing dead socket");
                }
                // This connection was closed or first attempt did not work, must reconnect
                // It will be done because kac == null
         
            }
        } else {
            httpObject.setHttpHeader(FipaHttpObject.HTTP_CONNECTION,
                    FipaHttpObject.HTTP_CONNECTION_CL);
        }
         
        try {
            if(kac == null){
                if(this.usePersistentConnections){
                    synchronized(this.duplicatSocketLock){
                        kac = this.createConnection(host);
                        s.setKeepAlive(true);
                        this.persistentConnections.add(kac);
                    }
                } else {
                        kac = this.createConnection(host);
                }
            }
         
            synchronized(kac){
                //System.err.println("Sending with new connection");
                kac.setBusy(true);
                if(this.deliver(kac, host, httpObject).getStatusCodeInt() != 200){
                    kac.setBusy(false);
                    // todo should be take some actions according to HTTP error codes
                    throw new MTPException("Response message not 200 (OK), it was "+httpObject.getStatusCode());
                }
                kac.setBusy(false);
                kac.setTimeStamp();
            }
            //System.err.println("Sending with new connection finished");
        } catch (IOException ioe){
            //System.err.println("Error when sending request, connection will now close");
            //ioe.printStackTrace();
            if(kac != null){
                synchronized(kac){
                    kac.setBusy(false);
                    kac.close();
                    if(this.usePersistentConnections){
                        this.persistentConnections.remove(kac);
                    }
                }
            }
            throw new MTPException("Unable to connect to "+host.toString()+" ("+ioe.getMessage()+")");
        }
    }
         */
    
    private KeepAlive.KAConnection createConnection(KeepAlive.KAConnection kac, boolean keepAlive) throws IOException {
        SocketFactory sf = SocketFactory.getDefault();
        HTTPAddress host = kac.getAddress();
        Socket s = null;
        
        if (this.outPort > -1) {
            s = sf.createSocket(host.getHost(),host.getPortNo(),InetAddress.getLocalHost(),this.outPort, true);
        } else {
            s = sf.createSocket(host.getHost(),host.getPortNo(), true);
        }
        
        try {
            // has no effect in MIDP as timeout values can not be set,
            // if JVM supports timeouts, the default timeout will be used
            
            s.setSoTimeout(this.responseTimeout);
            s.setLinger(this.linger);
            s.setKeepAlive(keepAlive);
        } catch (Exception ioe){ioe.printStackTrace();}
        
        kac.setSocket(s);
        
        return kac;
    }
    
    /**
     * Handlses the given pure HTTP-request (not containing FIPA message). This
     * ConnectionManager only sends HTTP-response back to sender.
     * @param sth The ServerThread that got the request.
     * @param fho The original received HTTP-request that needs to be handled.
     *
     */
    public void handleHttpRequest(HTTPServer.ServerThread sth, FipaHttpObject fho){
        this.messageDelivered(sth, fho);
    }
    
    
    /**
     * Delivers the given message.
     */
    private FipaHttpObject deliver(KeepAlive.KAConnection kac, HTTPAddress host, FipaHttpObject fho) throws IOException  {
        //System.err.println("Delivering out");
        FipaHttpObject res = new FipaHttpObject();
        kac.setBusy(true);
        //synchronized(kac){
        HTTPIO.writeHttpRequest(kac.getOut(), fho, host, this.useProxy);
        //System.err.println("  Delivering out ended");
        //kac.getIn().setNextEOS(1);
        HTTPIO.parseRequest(res, kac.getIn());
        //System.err.println("    Respond received");
        if (res.getConnectionType() != null && fho.HTTP_CONNECTION_CL.equals(res.getConnectionType())) {
            // Close the connection
            kac.close();
            if (this.usePersistentConnections) {
                this.persistentConnections.remove(kac);
                //logger.log(Logger.INFO, "ConnectionManager has closed output connection now, there are now "+this.persistentConnections.size()+" output connections");
            }
        }
        kac.setTimeStamp();
        kac.setBusy(false);
        //}
        return res;
    }
    
    
    /**
     * Handles the configuration message addressed to this MTP. This basic ConnectionManager
     * does not support dynamic configuration of MTP so MTPException is thrown if this
     * method is tried to be used.
     *
     * @throws MTPException If this method is called.
     */
    public void handleConfigurationRequest(Envelope env, byte[] request) throws MTPException {
        throw new MTPException("Dynamic configuration not allowed in basic ConnectionManager");
    }
    
    
    /**
     * Handles HTTP-response. If the response contains command to close the connection, it
     * will be closed.
     *
     * @param sth The ServerThread that received the response.
     * @param ro The response.
     */
    public void handleHttpResponse(HTTPServer.ServerThread sth, FipaHttpObject ro) {
        if(FipaHttpObject.HTTP_CONNECTION_CL.equals(ro.getHttpHeader(FipaHttpObject.HTTP_CONNECTION))){
            sth.setActive(false);
        }
    }
    
    
    /**
     * Handles the situation when Exception is occured in HTTPServer while
     * reading input from client. This ConnectionManager just shuts down
     * the ServerThread.
     * @param st The ServerThread that is calling this method
     * @param ro The object that was parsed while the exception happened
     * @param ioe The Exception that occured.
     *
     */
    public void readError(HTTPServer.ServerThread st, FipaHttpObject ro, Exception ioe){
        if(ioe instanceof EOFException){
            // Stream is closed, we cannot send response anyway
            st.setActive(false);
        } else if(ro.getHost() != null){
            // Response is expected, this was not just timeout
            // Try to send response.
            this.undeliveredMessage(st, ro);
        } else {
            st.setActive(false);
        }
    }
    
    
    /**
     * Sends HTTP-request requesting connection to be closed.
     */
    private void sendCloseRequest(HTTPServer.ServerThread st) {
        try {
            FipaHttpObject req = new FipaHttpObject();
            req.setIsHttpRequest(true);
            req.setHostAddr(new HTTPAddress(InetAddress.getLocalHost()));
            req.setHttpHeader(req.HTTP_CONNECTION, req.HTTP_CONNECTION_CL);
            HTTPIO.writeHttpRequest(st.getOutputStream(), req, new HTTPAddress(req.getHost()),false);
        } catch (Exception io){
            io.printStackTrace();
            st.setActive(false);
        }
    }
    
    /**
     * Sends positive HTTP-response for given request.
     * @param sth the ServerThread calling this method.
     * @param fho the initial request
     *
     */
    public void messageDelivered(HTTPServer.ServerThread sth, FipaHttpObject fho){
        // send the response
        FipaHttpObject res = new FipaHttpObject();
        res.setStatusCode(HTTPIO.OK);
        res.setHttpHeader(fho.HTTP_CONNECTION,fho.getConnectionType());
        res.setIsResponse(true);
        try {
            this.sendResponse(sth,res);
        } catch (Exception io){
            io.printStackTrace();
            sth.setActive(false);
        }
    }
    
    /**
     * Sends error response for given request.
     * @param sth the ServerThread calling this method
     * @param ro the request that cannot be fullfilled.
     *
     */
    public void undeliveredMessage(HTTPServer.ServerThread sth,FipaHttpObject ro){
        FipaHttpObject res = new FipaHttpObject();
        res.setIsResponse(true);
        res.setStatusCode(ro.getStatusCode());
        res.setErrorMsg(ro.getErrorMsg());
        if(ro.getConnectionType() != null){
            res.setHttpHeader(ro.HTTP_CONNECTION,ro.getConnectionType());
        } else {
            res.setHttpHeader(ro.HTTP_CONNECTION,HTTPIO.KA);
        }
        try{
            this.sendResponse(sth, res);
        } catch (Exception io){
            if(this.logger.isLoggable(Logger.INFO)){
                this.logger.log(Logger.INFO,"Did not send HTTP error to host "+ro.getHost()+", connection was already closed ("+io.getMessage()+")");
            }
            sth.setActive(false);
        }
    }
    
    /**
     * Sends given HTTP-response.
     * @param sth the ServerThread calling this method
     * @param fho the response.
     */
    public void sendResponse(HTTPServer.ServerThread sth,FipaHttpObject fho) throws IOException {
        OutputStream out = null;
        fho.setIsResponse(true);
        out = sth.getOutputStream();
        //System.err.println("Sending response");
        if(!fho.HTTP_CONNECTION_CL.equals(fho.getConnectionType())){
            synchronized(this.persistantServerThreads){
                if(!this.persistantServerThreads.containsKey((Object)sth)){
                    if(this.persistantServerThreads.size() < this.maxOpenConnections){
                        this.persistantServerThreads.put((Object)sth, (Object)sth);
                        fho.setHttpHeader(fho.HTTP_CONNECTION,fho.HTTP_CONNECTION_KA);
                    } else {
                        fho.setHttpHeader(fho.HTTP_CONNECTION,fho.HTTP_CONNECTION_CL);
                    }
                } else {
                    fho.setHttpHeader(fho.HTTP_CONNECTION,fho.HTTP_CONNECTION_KA);
                }
            }
        } else {
            fho.setHttpHeader(fho.HTTP_CONNECTION, fho.HTTP_CONNECTION_CL);
        }
        
        //System.err.println("Sending response2");
        try {
            HTTPIO.writeHttpResponse(out,fho);
            //System.err.println("Sending response, response sent");
        } catch (Exception io){
            sth.setActive(false);
            if(this.logger.isLoggable(Logger.INFO)){
                this.logger.log(Logger.INFO,"Did not send HTTP response to host "+fho.getHost()+", connection was already closed ("+io.getMessage()+")");
            }
            return;
        }
        
        //System.err.println("Sending response3");
        if(HTTPIO.CLOSE.equals(fho.getConnectionType())){
            //System.err.println("Closing connection");
            sth.setActive(false);
        }
    }
    
    /**
     * Method for ServerThread to request that the connections it uses should
     * been closed. ServerThread will call this when it is shutting down.
     *
     * @param st the ServerThread calling this method.
     */
    public void closeConnections(HTTPServer.ServerThread st){
        if(this.usePersistentConnections && this.persistantServerThreads.containsKey((Object)st)){
            synchronized(this.persistantServerThreads){
                this.persistantServerThreads.remove((Object)st);
                this.persistantServerThreads.notifyAll();
            }
        }
        //System.err.println("ConnectionManager closeConnections()");
        /*
        try {
            // dirty fix: do not close too early, for some reason LINGER is not working...
            synchronized(st){
                st.wait(3000);
            }
        } catch (Exception e){}
         */
        //System.err.println("closing thread connections");
        try{
            st.getInputStream().close();
        } catch (Exception e){}
        
        try {
            st.getOutputStream().flush();
        } catch (Exception e){}
        
        try{
            st.getOutputStream().close();
        } catch (Exception e){}
        
        try {
            st.getSocket().close();
        } catch (Exception e) {e.printStackTrace();}
        //nothing we can do
        //System.err.println("ConnectionManager now closed connections, there are "+ this.persistantServerThreads.size()+" threads still running");
        
    }
    
    /**
     * Closes all the connections this ConnectionManager currently has
     * open.
     */
    public void closeAllConnections(){
        Enumeration keys;
        HTTPServer.ServerThread current;
        boolean kill = false;
        
        if(this.persistantServerThreads != null){
            keys = this.persistantServerThreads.keys();
            while(keys.hasMoreElements()){
                current = ((HTTPServer.ServerThread)keys.nextElement());
                this.sendCloseRequest(current);
            }
            long time = System.currentTimeMillis();
            synchronized(this.persistantServerThreads){
                while(this.persistantServerThreads.size() > 0 && !kill){
                    try{
                        this.persistantServerThreads.wait(1000);
                    } catch (InterruptedException e){}
                    if((System.currentTimeMillis() - time) > 5000){
                        kill = true;
                    }
                }
            }
        }
        
        if(kill){
            if(logger.isLoggable(Logger.WARNING)){
                logger.log(Logger.WARNING,"There are still "+this.persistantServerThreads.size()+" server threads running, intterupting them");
            }
            keys = this.persistantServerThreads.keys();
            while(keys.hasMoreElements()){
                current = ((HTTPServer.ServerThread)keys.nextElement());
                current.setActive(false);
                try {
                    current.interrupt();
                } catch (Exception e){}
            }
        } else {
            /*
            if(logger.isLoggable(Logger.INFO)){
                logger.log(Logger.INFO,"All ServerThreads ended succesfully");
            }
             */
        }
        
        this.persistantServerThreads = null;
        
        // close all outgoing connections which haven't been already closed
        if(this.usePersistentConnections){
            this.closeTimer.cancel();
            this.persistentConnections.closeConnections();
        }
    }
}