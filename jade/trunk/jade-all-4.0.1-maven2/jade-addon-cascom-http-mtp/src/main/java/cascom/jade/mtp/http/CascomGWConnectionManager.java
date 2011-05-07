package cascom.jade.mtp.http;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import jade.mtp.MTPException;
import jade.mtp.http2.*;
import jade.util.Logger;
import java.io.EOFException;

import java.lang.Thread;
import java.lang.InterruptedException;
import jade.util.leap.LinkedList;
import java.util.TimerTask;
import java.util.Timer;

import jade.mtp.http2.TimerDebugger;

/**
 * Class for handling buffered connection between CASCOM Messaging
 * Gateway and CASCOM Agent Platform. This class handles HTTP related issues
 * related to the CASCOM message delivery between client and the gateway. Those
 * issues concern message numbering, buffering, error-detection and closing the
 * connection properly. Class will inform attached ConnectionListener when connection
 * is closed.
 *
 * @author Ahti Syreeni - TeliaSonera
 */
public  class CascomGWConnectionManager {
    public static final String STATUS_HEADER="X-CASCOM-STATUS";
    public static final String STATUS_CONTINUE="continue";
    public static final String STATUS_NEW="new";
    public static final String ID_HEADER="X-CASCOM-ID";
    public static final String MSGNUMBER_HEADER="X-CASCOM-MSG";
    public static final String MSGACK_HEADER="X-CASCOM-ACK";
    private int timeout= 15000; // timeout for response in milliseconds
    private int idleTimeout = Integer.MAX_VALUE; // timeout for persistent connection closing in seconds
    private int pingTimeout = 10;
    protected int maxRetries = 3;
    
    protected OutputStream output = null;
    protected LinkedList mBuffer = null;
    
    
    private ConnectionListener listener = null;
    protected HTTPAddress defaultHost = null;
    
    
    protected boolean connectionClosed = true;
    
    // indicates that data is being sent at the moment
    private boolean isSending = false;
    
    
    // true when timeout has occured and gateway
    // has sent keep alive -query for client.
    //private boolean waitingForKeepAliveResponse = false;
    //private boolean writeTimeout = false;
    
    /**
     * This counter is the message number for latest received
     * message.
     */
    private int receivedMsgCounter = -1;
    
    private int latestMessageSent = -1;
    private int latestMessageAcked = -1;
    
    private  Object bufferLock = new Object();
    private  Object closeLock = new Object();
    private  Object ackLock = new Object();
    private  Logger logger = Logger.getMyLogger(getClass().getName());
    
    protected boolean useProxy = false;
    
    
    public class SenderThread extends Thread {
        public boolean active = true;
        //private FipaHttpObject response = null;
        private LinkedList responses = new LinkedList();
        //private Object resLock = new Object();
        //boolean cont = true;
        
        int resTimeOut = timeout / 1000;
                
        public void inactivate(){	
            this.active = false;
            synchronized(bufferLock){
                bufferLock.notifyAll();
            }		
        }

        public void removeUnBuffered(){
            this.responses.clear();
        }
        
        public void sendUnbufferedMsg(FipaHttpObject response){
            synchronized(bufferLock){
                this.responses.addLast((Object)response);
                bufferLock.notifyAll();
            }
        }
        
        public void run(){
            FipaHttpObject toBeSent = null;
            int msgNumber = -1;
            int timeCounter = 0;
            //System.err.println("Sender started");
            while(this.active){
                toBeSent = null;
                //synchronized(bufferLock){
                //System.err.println("run sLock passed and wait()");
                //try {
                    /*
                        isSending = false;
                        while(this.active && (connectionClosed || this.responses.size() < 1
                              && mBuffer.size() < 1)
                              && (timeCounter < idleTimeout)){
                            if(connectionClosed){
                                bufferLock.wait();
                            } else {
                                bufferLock.wait(1000);
                            }
                            timeCounter++;
                        }
                            isSending = true;
                     */
                //System.err.println("wakeup"+this.responses.size()+":"+connectionClosed);
                //System.err.println("del size "+this.responses.size());
                while(toBeSent == null) {                    
		    if(!this.active){
                        //System.err.println("%% Not active");
                        return;
                    } else if(connectionClosed){
                        // Wait for connection to be opened
                        //System.err.println("%% Connection closed");
                        synchronized(bufferLock){
                            try {
                                timeCounter = 0;
                                
                                //TimerDebugger tt =new TimerDebugger(toBeSent,System.currentTimeMillis()+": GwConnectionManager sender thread waiting quite long");
                                //java.util.Timer timer = new Timer();
                                //timer.schedule(tt,5000);
                                bufferLock.wait();
                                //timer.cancel();
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    } else if(this.responses.size() > 0){
                        //System.err.println("%% Send response");
                        
                        // Send http-responses and http-requests first, those are
                        // on higher priority than FIPA-requests
                        
                        toBeSent = (FipaHttpObject)this.responses.removeFirst();
                        msgNumber = -1;
                    } else if(latestMessageAcked == latestMessageSent && mBuffer.size() > 0){
                        //System.err.println("%% Send request");
                        
                        // Send next FIPA-request from buffer if such exist
                        // and previous has been acknowledged
                        
                        latestMessageSent++;
                        FipaHttpObject fo = (FipaHttpObject)mBuffer.get(0);
                        fo.setHttpHeader(fo.HTTP_CONNECTION,fo.HTTP_CONNECTION_KA);
                        fo.setHttpHeader(fo.HTTP_HOST, fo.getHostAddr().getHost()+":"+fo.getHostAddr().getPort());
                        msgNumber = latestMessageSent;
                        toBeSent = fo;
                    
                    } else {
                        // Timer dependent options
                        
                        if(idleTimeout < Integer.MAX_VALUE && timeCounter >= idleTimeout && useProxy) {

                            // If idleTimeout is set, no traffic and this is client side, ask
                            // for closing the connection to the gateway
                            
                            logger.log(Logger.INFO,"Request connection to be closed");
                            requestConnectionToBeClosed();
                        } else if (useProxy &&  timeCounter >= pingTimeout && (timeCounter % pingTimeout) == 0){

                            // If there is nothing to be sent and the ping timer
                            // is off, do the ping

                            this.ping();
                        }
                                                
                        //System.err.println("%% Wait 1s: "+mBuffer.size()+":"+responses.size());
                        
                        // Finally, there is nothing to be sent. Wait for
                        // new messages and check for timeout-conditions
                        // after one second if no traffic
                        synchronized(bufferLock){
                            try {
                                bufferLock.wait(1000);
                            } catch (Exception e){}
                        }
                        timeCounter ++;
                    }
                }
                        /*
                    } catch (Exception e){
                        e.printStackTrace();
                        if(!this.active){
                            break;
                        }
                    }
                         */
                //}
                
                // Deliver the outgoing message. It can be request or response.
                // Reset the traffic timer.
                
                //System.err.println("%% Now sending");
                
                
                timeCounter = 0;
                //if(toBeSent != null){
                this.deliverOut(toBeSent,msgNumber);
                if(FipaHttpObject.HTTP_CONNECTION_CL.equals(toBeSent.getHttpHeader(FipaHttpObject.HTTP_CONNECTION))){
                    connectionClosed = true;
                    if(toBeSent.isResponse()){
                        // simply close output;
                        // System.err.println("%%% Response: close output now");
                        closeOutput();
                    } else if (useProxy) {
                        // if this is client side, make sure connection
                        // will be closed even if
                        // gateway is no longer responding to close request
                        
                        //System.err.println("%%% Request: Wait closing");
                        synchronized(closeLock){
                            try {
                                closeLock.wait(timeout);
                            } catch (InterruptedException iu){}
                        }
                        /*
                        if(output != null && connectionClosed   ){
                            closeOutput("Gateway not responding for close request");
                        } 
                         */
                    }
                }
                toBeSent = null;
                //}
            }
        }
        
        
        private void ping(){
            try {
                isSending = true;
                HTTPIO.testOutput(output);
                isSending = false;
            } catch (Exception io){
                System.out.println("Pinging failed in CascomGWConnectionManager.ping:"+io.getMessage());
                isSending = false;
                closeOutput(io.getMessage());
            }
        }
        
        
        /**
         *  Delivers out the given object
         */
        private void deliverOut(FipaHttpObject fo, int msgNmb){
            try {
                //TimerDebugger td = new TimerDebugger(fo,System.currentTimeMillis()+": GWConnectionManager deliverOut taking too long");
                //java.util.Timer timer = new Timer();
                //timer.schedule(td,5000);
                
                
                isSending = true;
                if(fo.isResponse()){
                    HTTPIO.writeHttpResponse(output, fo);
                    //logger.log(Logger.INFO,"http-response out with msg-Ack "+fo.getHttpHeader(MSGACK_HEADER));
                } else {
                    if(msgNmb > -1){
                        fo.setHttpHeader(MSGNUMBER_HEADER,""+msgNmb);
                    }
                    //logger.log(Logger.INFO,"http-request out with msg-nmb "+msgNmb);
                    HTTPIO.writeHttpRequest(output, fo, fo.getHostAddr(), useProxy);
                } 
                
                
                isSending = false;
                //timer.cancel();
            } catch (Exception io){
                //logger.log(Logger.WARNING,"delO:"+io.getMessage());
                TimerDebugger.printError(fo,io, System.currentTimeMillis()+": error delivering out message in CascomGWConnectionManager:");
                isSending = false;
                closeOutput(io.getMessage());
            }
            
        }
    }
    
    
    
    
    private SenderThread sender = null;
    
    private CascomGWConnectionManager(){}
    
    
    /**
     * Creates a new instance of CascomGWConnectionManager for client side. The input and outputs
     * must be opened, this class does not deal with opening the streams. However,
     * it notifies the attached ConnectionListener if the streams are closed because
     * of an error, also the streams can be requested to be closed with closeStreams()
     * method.
     */
    public CascomGWConnectionManager(int bufferSize, boolean isClient) {
        //save bandwidth
        HTTPIO.useHtmlResponses(false);
        
        //System.err.println("%%%%%%%%%% CascomGWConnectionManager initiated, isClient:"+isClient);
        this.mBuffer = new LinkedList();
        this.useProxy = isClient;
        //this.timeoutTimer = new TimeoutTimer();
        //this.timeoutTimer.suspend();
        // for timeout
        
        
        //this.timer = new Timer();
        
        this.sender = new SenderThread();
        this.sender.start();
        
        //this.timer.schedule(this.timeoutTimer,0,1000);
    }
    
    /**
     * Sets timeout for server response. If the receiver does
     * not respond before that timeout, the sender will assume
     * that receiver is dead.
     *
     * @param timeout The timeout in milliseconds.
     */
    public void setResponseTimeOut(int timeout){
        this.timeout = timeout;
    }
    
    /**
     * If this is the client side (not the gateway side), this is
     * the way to change the timeout for closing the connection
     * to the CASCOM Messaging gateway when there haven't been
     * any transmissions between client and gateway for a while. By default,
     * this functionality is off. If the timeout is set to Integer.MAX_VALUE,
     * the options is turned off.
     *
     * @param timeout The timeout for closing connection to the gateway in SECONDS.
     */
    public void setClosingTimeout(int timeout){
        this.idleTimeout = timeout;
    }
    
    
    /**
     * Sets pinging timeout. After timeout one byte will be sent to the output
     * to make sure that connection is not broken if there is not
     * traffic. This is the only way to check that connection is really alive.
     *
     * @param timeout the Timeout for pinging in SECONDS.
     */
    public void setPingingTimeout(int timeout){
        this.pingTimeout = timeout;
    }
    
    
    /**
     * Returns the ack number for given message number. It is usually the same
     * as the message number but if the connection has already received more packages,
     * the ack number is greater. Calling this method will register the given message
     * number as received. Note that the incoming messages are not buffered.
     */
    public int getAckNumber(int receivedMsgNumber){
        synchronized(ackLock){
            if(receivedMsgNumber != (this.receivedMsgCounter +1)){
                return this.receivedMsgCounter;
            } else {
                this.receivedMsgCounter++;
                return receivedMsgNumber;
            }
        }
    }
    
    
    /**
     * Delivers requests, should not be used for responses or other objects
     * that are not meant to be buffered
     */
    public void deliver(HTTPAddress host, FipaHttpObject httpObject) throws MTPException {
        httpObject.setHostAddr(host);
        // Only FIPA requests should be put to buffer, responses and connection
        // related HTTP requests have no meaning if connection is closed.
        if(httpObject.isFipaRequest()){
            synchronized(bufferLock){
                this.mBuffer.addLast((Object)httpObject);
                //logger.log(Logger.INFO,"Adding to client buffer, msgs:"+this.mBuffer.size());
            }
        }
        
        if(!this.connectionClosed){
            this.sendNextFromBuffer();
        }
        
        /*
        else {
           System.err.println("Connection was closed... just buffering if request");
        }
         */
    }
    
    /**
     * Returns true if there is transmission going on to from this
     * side to the other side of the connection.
     */
    public boolean isSending(){
        return this.isSending;
    }
    
    private void requestConnectionToBeClosed(){
        FipaHttpObject fo = new FipaHttpObject();
        fo.setIsHttpRequest(true);
        fo.setHttpHeader(FipaHttpObject.HTTP_CONNECTION, FipaHttpObject.HTTP_CONNECTION_CL);
        fo.setHttpHeader(fo.HTTP_HOST, this.defaultHost.getHost());
        fo.setHostAddr(this.defaultHost);
        // no message number for closing request
        //System.err.println("Now requesting connection to be closed");
        this.sender.sendUnbufferedMsg(fo);
    }
    
    
    private void sendNextFromBuffer(){
        //System.err.println("sendNext wait for sLock");
        synchronized(bufferLock){
            bufferLock.notifyAll();
        }
    }
    
    /**
     *  Delivers the message using the default host specified with setDefaultHost -method.
     */
    public void deliver(FipaHttpObject httpObject) throws MTPException {
        if(this.defaultHost != null){
            this.deliver(this.defaultHost, httpObject);
        } else {
            throw new MTPException("No default destinanation host address set for CascomGWConnectionManager");
        }
    }
    
    /**
     * Specifies the default host which will be used as destination host if
     * no other host is specified when delivering the message.
     */
    public void setDefaultHost(HTTPAddress hta){
        this.defaultHost = hta;
    }
    
    
    
    /**
     * Handles the connection related HTTP requests. If this is the server side of the connection,
     * the server will send HTTP-response with connection-close to client and closes the
     * output connection or only sends HTTP-response, if the initial request is not request to
     * close the connection. If this is the client side of the connection, the client refuses
     * to close the connection even if the server asks because server is just checking if
     * the client is still alive.
     */
    public void handleHttpRequest(FipaHttpObject fho){
        //System.err.println("CascomGW.handleHttpRequest, got pure HTTP request, sending response");
        // message is delivered as no further action is needed by this connection manager
        
        
        // HTTP responses and requests are not numbered as those are not buffered.
        fho.removeHttpHeader(MSGNUMBER_HEADER);
        
        // if this is the server side, close the connection when client requests.
        // if this the client, refuse to close the connection if server is checking, if
        // this client is still alive.
        if(!this.useProxy){
            this.messageDelivered(fho);
        } else {
            fho.setHttpHeader(FipaHttpObject.HTTP_CONNECTION,FipaHttpObject.HTTP_CONNECTION_KA);
            this.messageDelivered(fho);
        }
    }
    
    
    public HTTPAddress getDefaultHost(){
        return this.defaultHost;
    }
    
    
    public void handleHttpResponse(FipaHttpObject ro)  {
        /*
         * This end has requested for the other end for closing the connection and
         * this is response for that request. No further messages should be delivered
         * to the other end.
         */
        if(ro.HTTP_CONNECTION_CL.equals(ro.getConnectionType())){
            this.connectionClosed = true;
        }
        
        if(ro.getHttpHeader(MSGACK_HEADER)!=null){
            int i = -1;
            try {
                i = Integer.parseInt(ro.getHttpHeader(MSGACK_HEADER));
            } catch (Exception e){
                /*
                 * Resend message, the msg ack number is propably corrupted. The client
                 * and gateway should check at startup (client) or while receiving
                 * connection request (gw) that the other partner sends proper CASCOM
                 * headers so we don't have to think that possibility here.
                 */
                if(logger.isLoggable(Logger.SEVERE)){
                    logger.log(Logger.SEVERE,"Number cannot be parsed,latestMessageSent--");
                }
                synchronized(bufferLock){
                    this.latestMessageSent--;
                }
                return;
            }
            
            if(ro.getStatusCodeInt() == 200){
                synchronized(bufferLock){
                    //System.err.println("CascomGW.handleHttpResponse: removing from buffer received messages up to ack:"+i);
                    // only one message can be received at the time
                    if(i > -1 && i == this.latestMessageAcked+1 && this.mBuffer.size() > 0){
                        //System.err.println("Removing from buffer according to http-response:"+i);
                        this.mBuffer.removeFirst();
                        //logger.log(Logger.INFO,"Msg removed: Msgbuffer size now:"+this.mBuffer.size());
                    } else if(i==latestMessageAcked){
                        //resend the message
                        this.latestMessageSent = i;
                    } else if(i < latestMessageAcked){
                        if(logger.isLoggable(Logger.SEVERE)){
                            logger.log(Logger.SEVERE,"Error in counting.."+this.latestMessageAcked+" got i:"+i);
                        }
                    }
                    this.latestMessageAcked = i;
                    bufferLock.notifyAll();
                }
            }
        }
        
        if(this.connectionClosed){
            this.closeOutput();
        }
    }
    
    /**
     * Inform sender with HTTP response that the message is parsed
     * and delivered to the agent platform without errors.
     */
    public void messageDelivered(FipaHttpObject fho){
        // send the response
        int i = -1;
        FipaHttpObject res = new FipaHttpObject();
        try {
            if(fho.getHttpHeader(MSGNUMBER_HEADER) != null){
                i = Integer.parseInt(fho.getHttpHeader(MSGNUMBER_HEADER));
                res.setHttpHeader(this.MSGACK_HEADER, ""+this.getAckNumber(i));
                res.removeHttpHeader(MSGNUMBER_HEADER);
            }
            res.setStatusCode(HTTPIO.OK);
            res.setHttpHeader(res.HTTP_CONNECTION,fho.getConnectionType());
            res.setIsResponse(true);
            this.sender.sendUnbufferedMsg(res);
        } catch (Exception e){e.printStackTrace();}
    }
    
    /**
     * The specified message cannot be delivered because it contains error
     * specified in FipaHttpObject.getErrorMsg() - string.
     */
    public void undeliveredMessage(FipaHttpObject ro){
        FipaHttpObject res = new FipaHttpObject();
        res.setIsResponse(true);
        res.setStatusCode(ro.getStatusCode());
        res.setErrorMsg(ro.getErrorMsg());
        if(ro.getConnectionType() != null){
            res.setHttpHeader(ro.HTTP_CONNECTION,ro.getConnectionType());
        } else {
            res.setHttpHeader(ro.HTTP_CONNECTION,HTTPIO.KA);
        }
        res.setHttpHeader(this.MSGACK_HEADER,""+(this.receivedMsgCounter));
        res.removeHttpHeader(MSGNUMBER_HEADER);
        
        // send ack for last received message, which is the previous from the
        // first message currently in buffer
        //System.err.println("undelivered message, sending ack for last received:"+this.receivedMsgCounter);
        this.sender.sendUnbufferedMsg(res);
    }
    
    
    
    /**
     * This method can be requested by the ConnectionManager, to close the
     * current connection clean, that is, the http-request close is sent
     * to the other end of the connection. The output direction will be immediately
     * closed after sending the request.
     */
    public synchronized void closeConnection(){
        if(!this.connectionClosed){
            this.requestConnectionToBeClosed();
        }
    }
    
    
    
   /*
    * Closes the OutputStream and informs the
    * attached listener that connection have been closed due
    * to specified error string.
    */
    protected void closeOutput(String msg){
        //System.err.println("Closing buffered output connection, error:"+msg);
        this.connectionClosed = true;
        if(this.output != null){
            try {
                this.output.close();
                this.output = null;
                synchronized(this.closeLock){
                    this.closeLock.notifyAll();
                }
            } catch (Exception e){}
        }
        if(this.listener != null){
            this.listener.connectionClosedByError(msg);
            this.listener = null;
        }
	if(this.sender != null){
        	this.sender.removeUnBuffered();
	}
    }
    
    
    public boolean connectionClosed(){
        return this.connectionClosed;
    }
    
   /*
    * Closes the OutputStream and informs the
    * attached listener that connection have been closed.
    */
    protected void closeOutput(){
        this.connectionClosed = true;
        if(this.listener != null){
            this.listener.connectionClosed();
            this.listener = null;
        }
        if(this.output != null){
            try {
                this.output.flush();
            } catch (Exception e){}
            try {
                this.output.close();
                this.output = null;
                synchronized(this.closeLock){
                    this.closeLock.notifyAll();
                }
            } catch (Exception e){
                System.err.println("Error closing output in CascomGWConnectionMaanger");
                e.printStackTrace();
            }
        }
        //System.err.println("Output to gateway is now closed");
	if(this.sender != null){
        	this.sender.removeUnBuffered();
	}
    }
    
    
    
    /**
     * Sets new InputStream and OutputStream to be used with this manager.
     * Streams must be open for reading and writing. This method will also
     * try to send next message in buffer if such exist.
     */
    public boolean startSending(OutputStream output, ConnectionListener cl, FipaHttpObject initRequest){
        this.output = output;
        this.listener = cl;
        
        //this.timeoutTimer.start();
        // the latestAck -1 means that other side have not received any messages yet.
        int latestAck = -1;
        if(initRequest.getHttpHeader(CascomGWConnectionManager.MSGACK_HEADER) != null){
            try {
                latestAck = Integer.parseInt(initRequest.getHttpHeader(CascomGWConnectionManager.MSGACK_HEADER).trim());
                // Not documented: client can send ack for latest received message with
                // the opening request.
                initRequest.removeHttpHeader(CascomGWConnectionManager.MSGACK_HEADER);
                //System.err.println("Message ack in init request:"+latestAck);
            } catch (Exception e){
                if(logger.isLoggable(Logger.WARNING)){
                    logger.log(Logger.WARNING,"Error occured when parsing message number from request:"+e.getMessage());
                }
            }
        }
        // make sure that CascomGWConnectionManager will send ack for latest
        // received message in messageDelivered()-method.
        initRequest.setHttpHeader(CascomGWConnectionManager.MSGNUMBER_HEADER,"-1");
        
        
        // now, open connection and set the numbering right
        synchronized(bufferLock){
            /*
            if(logger.isLoggable(Logger.INFO)){
                logger.log(Logger.INFO,"Startsend: latestAcked"+this.latestMessageAcked+" sent:"+this.latestMessageSent+" initAck:"+latestAck+" bsize "+this.mBuffer.size());
            }
             */
	   
	    // Check if there are unsent or already received messages
            if(this.latestMessageAcked != this.latestMessageSent){
                if(this.latestMessageAcked+1 == this.latestMessageSent && this.hasBufferedMessages()){
                    if(latestAck > -1 && latestAck == this.latestMessageSent){
                        // latest message was received but ack was lost so this can be removed from buffer
                        if(logger.isLoggable(Logger.WARNING)){
                            logger.log(Logger.WARNING,"Removing already received message:"+this.latestMessageAcked+" sent:"+this.latestMessageSent+" initAck:"+latestAck);
                        }
                        this.mBuffer.removeFirst();
                        
                    } else if(latestAck == this.latestMessageSent -1){
                        // the latest message sent is not received, resend it.
                        if(logger.isLoggable(Logger.WARNING)){
                            logger.log(Logger.WARNING,"Resending message:"+this.latestMessageAcked+" sent:"+this.latestMessageSent+" initAck:"+latestAck);
                        }
                        this.latestMessageSent--;
                        latestAck = this.latestMessageSent;
                        this.latestMessageAcked = this.latestMessageSent;
                    } else if(this.latestMessageSent > -1 && latestAck == -1){

			// There is outgoing message but we cannot know if it was received so we must resend it just
			// for sure.

                        if(logger.isLoggable(Logger.WARNING)){
                            logger.log(Logger.WARNING,"Error: Other side have been resetted, resending outgoing unsent message and restarting numbering");
                        }

                        this.latestMessageSent = -1;                        
                        this.latestMessageAcked = this.latestMessageSent;
	                latestAck = -1;
                        
                    } else {
                        if(logger.isLoggable(Logger.WARNING)){
                            logger.log(Logger.WARNING,"Unexpected error in numbering: sent:"+this.latestMessageSent+", acked:"+this.latestMessageAcked+". But now received ack for "+latestAck);
                        }
                        this.latestMessageSent = -1;                        
                        this.latestMessageAcked = this.latestMessageSent;
	                latestAck = -1;
                    }
                }



	    // There are no unsent message, just check that numbering is right

            } else if(this.latestMessageAcked > -1 && latestAck < 0){
                if(logger.isLoggable(Logger.WARNING)){
                    logger.log(Logger.WARNING,"Other side have been resetted, incomming packets may be lost, restarting numbering");
                }
                this.latestMessageAcked = -1;
                this.latestMessageSent = -1;
                latestAck = -1;
            } else if(this.latestMessageAcked > latestAck) {
       	        if(logger.isLoggable(Logger.WARNING)){
               	    logger.log(Logger.WARNING,"Other side argues that it hasn't received packets that it previously has acknowleged: latestMsgSent:"+this.latestMessageSent+" latestMsgAcked:"+this.latestMessageAcked+" but ack got:"+latestAck);
                }
            } else if(this.latestMessageAcked+1 < latestAck){
                if(logger.isLoggable(Logger.WARNING)){
                    logger.log(Logger.WARNING,"This host has been resetted during session.");
                }
            }

            //  all error cases handled, catch up the numbering anycase
            if(latestAck > -1){
                this.latestMessageAcked = latestAck;
                this.latestMessageSent = latestMessageAcked;
            }
            
            this.connectionClosed = false;
            
            //remove responses from the buffer so possible buffered responses will not 
            //be sent..
            this.sender.removeUnBuffered();
            
            
            // we still have the bufferLock, so other Thread cannot send from buffer yet
            // even if the connectionClosed was set to false.
            
            // if the initRequest is HTTP request (this the server side), we
            // must send response for the request.
            if(initRequest.isHttpRequest()){
                this.messageDelivered(initRequest);
            }
        }
        //start sending messages.
        this.sendNextFromBuffer();
        return true;
        
    }
    
    public int getLatestMessageSentIndex(){
        return this.latestMessageSent;
    }
    
    /*
    public void setMessageCounter(int i){
        this.messageCounter = i;
        this.latestMessageSent = i-1;
    }*/
    
    
    /**
     * Returns true if there are one or more messages waiting in buffer to
     * be delivered.
     */
    public boolean hasBufferedMessages(){
        return this.mBuffer.size() > 0;
    }
    
    /**
     * Method for server side to inform this connection manager that
     * IOException has occured. This connection manager reacts by
     * trying to close the duplex socket if the connection is closed or
     * sends error message back if the response is expected.
     */
    public void readError(FipaHttpObject fho, IOException e){
        // End of file means the connection is closed.
        //logger.log(Logger.WARNING,"CWG.readError:"+e.getMessage());
        if (fho.responseExpected()) {
            this.undeliveredMessage(fho);
        } else {
            this.closeOutput(e.getMessage());
        }
    }
    
    
    /**
     * Release resources, this instance should not be used after calling this method.
     */
    public void releaseResources(){
        //this.timer.cancel();
        //this.timer = null;
        //this.timeoutTimer = null;
	this.closeOutput();
        this.sender.inactivate();
        this.sender = null;
    }
}