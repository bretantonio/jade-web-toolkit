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

import jade.domain.FIPAAgentManagement.Envelope;
import jade.util.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Enumeration;
import cascom.util.StringTokenizer;

/**
 * This class wraps the information of FIPA-HTTP request.
 * @author Ahti Syreeni - TeliaSonera
 */
public class FipaHttpObject {
    /** The http-connection header */
    public static final String HTTP_CONNECTION = "Connection";
    /** Keep-Alive value in http-requests */
    public static final String HTTP_CONNECTION_KA = "Keep-Alive";
    /** Close cmd in http-header */
    public static final String HTTP_CONNECTION_CL = "close";
    /** Host string in http-headers */
    public static final String HTTP_HOST = "Host";
    /** Proxy-Connection string in http-headers */
    public static final String PROXY_CONNECTION = "Proxy-Connection";
    /** User header prefix used in HTTP-headers */
    public static final String USER_HEADER_PREFIX = "X-";
    
    private String envelopeACLpresentation = null;
    private String aclPresentation = null;
    private byte[] envelope = null;
    private byte[] acl = null;
    private boolean isValidFipaRequest = false;
    private boolean response = false;
    private String statusCode = "";
    private int statusCodeInt = -1;
    private Hashtable httpHeaders;
    private int headersTotalLength=0;
    private String payloadEncoding;
    private String errorMsg;
    private boolean isHttpRequest = false;
    private HTTPAddress hostAddress = null;
    private boolean responseExpected = false;
    
    // if user defined HTTP-headers are used, they must start with following prefix
    
    /**
     * Creates new FipaHttpObject.
     */
    public FipaHttpObject(){
    }
    
    
    /**
     * Returns true if this object needs response even if the content
     * is not valid, i.e if the content of the message is corrupted
     * during transmission. Usually this flag is set to true right after
     * the first HTTP Request header is received.
     *
     * @return True if response is expected.
     */
    public boolean responseExpected(){
        return this.responseExpected;
    }
    
    /**
     * Sets the value for whether the HTTP object needs response.
     */
    public void setResponseExpected(boolean value){
        this.responseExpected = value;
    }
    
    /**
     * Reset this object to default values
     */
    public void reset(){
        this.envelopeACLpresentation = null;
        this.aclPresentation = null;
        this.envelope = null;
        this.acl = null;
        this.isValidFipaRequest = false;
        this.response = false;
        this.statusCode = "";
        this.statusCodeInt = -1;
        this.httpHeaders = null;
        this.headersTotalLength=0;
        this.payloadEncoding = null;
        this.errorMsg = null;
        this.isHttpRequest = false;
        this.hostAddress = null;
    }
    
    
    /**
     * Gets ACL message representation.
     * @return String the ACL message representation.
     */
    public String getACLMsgPresentation(){
        return this.aclPresentation;
    }
    
    /**
     * Gets the envelope representation
     * @return String the envelope representation
     */
    public String getEnvelopePresentation(){
        return this.envelopeACLpresentation;
    }
    
    /**
     * Gets the message envelope.
     * @return byte[] message envelope
     */
    public byte[] getEnvelope(){
        return this.envelope;
    }
    
    /**
     * Gets the ACL message
     * @return ACL message
     */
    public byte[] getACLMessage(){
        return this.acl;
    }
    
    /**
     * Gets payload encoding
     * @return Payload encoding.
     */
    public String getPayloadEncoding(){
        return this.payloadEncoding;
    }
    
    /**
     * Sets payload encoding
     * @param enc Payload encoding
     */
    public void setPayloadEncoding(String enc){
        this.payloadEncoding = enc;
    }
    
    /**
     * Sets error message associated to this
     * HTTP object
     * @param msg the error message.
     */
    public void setErrorMsg(String msg){
        this.errorMsg = msg;
    }
    
    /**
     * Gets the status code of this object as int
     * @return int HTTP status code
     */
    public int getStatusCodeInt(){
        return this.statusCodeInt;
    }
    
    /**
     * Sets this object to represent HTTP request
     * @param is true or false
     */
    public void setIsHttpRequest(boolean is){
        this.isHttpRequest = is;
    }
    
    /**
     * Returns true if this object is http request
     * @return True if this is http request
     */
    public boolean isHttpRequest(){
        return this.isHttpRequest;
    }
    
    
    /**
     * Gets the host header of this http object.
     *
     * It should be noted that the host string must be in form
     * host:port
     * @return The host.
     */
    public String getHost(){
        return this.getHttpHeader(FipaHttpObject.HTTP_HOST);
    }
    
    
    /**
     * Gets the host address
     * @return Host address
     */
    public HTTPAddress getHostAddr(){
        return this.hostAddress;
    }
    
    /**
     * Sets the host address
     * @param address the address
     */
    public void setHostAddr(HTTPAddress address){
        this.hostAddress = address;
        this.setHttpHeader(this.HTTP_HOST, address.getHost()+":"+address.getPort());
    }
    
    
    /**
     * Gets the proxy connection string
     * @return The proxy connection string
     */
    public String getProxyConnection(){
        return this.getHttpHeader(FipaHttpObject.PROXY_CONNECTION);
    }
    
    /**
     * Gets the connection type String
     * @return the connection type String.
     */
    public String getConnectionType(){
        return this.getHttpHeader(FipaHttpObject.HTTP_CONNECTION);
    }
    
    /**
     * Gets the header names.
     * @return Header names.
     */
    public Enumeration getHeaderNames(){
        if(this.httpHeaders != null) {
            return this.httpHeaders.keys();
        }
        return null;
    }
    
    /**
     * Sets ACL-represntation header
     * @param acp the header
     */
    public void setACLMsgPresentation(String acp){
        this.aclPresentation = acp;
    }
    
    /**
     * Gets error message.
     * @return the error message.
     */
    public String getErrorMsg(){
        return this.errorMsg;
    }
    
    
    /**
     * Sets envelope represntation.
     * @param str Envelope representation.
     */
    public void setEnvelopePresentation(String str){
        this.envelopeACLpresentation = str;
    }
    
    /**
     * Sets envelope.
     *  @param env the envelope.
     */
    public void setEnvelope(byte[] env){
        this.envelope = env;
    }
    
    /**
     * Sets the ACL message.
     * @param acl the ACL Message
     */
    public void setACLMessage(byte[] acl){
        this.acl = acl;
    }
    
    /**
     * Set whether this is response.
     * @param isRes whether this is response.
     */
    public void setIsResponse(boolean isRes){
        this.response = isRes;
        if(isRes){
            this.isValidFipaRequest = false;
            this.isHttpRequest = false;
        }
    }
    
    /**
     * Returns true if this is http-response.
     */
    public boolean isResponse() {
        return this.response;
    }
    
    /**
     * Sets if this is a FIPA http request
     * @param is if FIPA request
     */
    public void setIsFipaRequest(boolean is) {
        this.isValidFipaRequest = is;
        if(is){
            this.isHttpRequest = false;
            this.response = false;
        }
    }
    
    /**
     * Returns true if this is FIPA request
     */
    public boolean isFipaRequest(){
        return this.isValidFipaRequest;
    }
    
    
    /**
     * Removes http header
     * @param header The header to be removed
     */
    public void removeHttpHeader(String header){
        this.httpHeaders.remove((Object)header);
    }
    
    
    /**
     * Sets status code for this http request.
     * @param code the HTTP status code
     */
    public void setStatusCode(String code) {
        this.statusCode = code;
        try {
            int index = code.trim().indexOf(" ");
            
            if(index > 0){
                this.statusCodeInt = Integer.parseInt(code.substring(0,index).trim());
            } else {
                this.statusCodeInt = Integer.parseInt(code.trim());
            }
            
        } catch (NumberFormatException e){
            //System.err.println("Incorrect status code:"+code);
        }
    }
    
    /**
     * GEts the status code of this HTTP object.
     * @return Status code
     */
    public String getStatusCode(){
        return this.statusCode;
    }
    
    /**
     * Sets http header. Overwrite existing header.
     */
    public boolean setHttpHeader(String key, String value){
        if(key==null || value==null){
            return false;
        }
        if(this.httpHeaders == null){
            this.httpHeaders = new Hashtable(4);
        }
        this.httpHeaders.put((Object)key,(Object)value);
        this.headersTotalLength += key.length()+value.length()+1;
        return true;
    }
    
    /**
     * Returns the total length in chars of user defined headers
     */
    public int sizeOfHeaders(){
        return this.headersTotalLength;
    }
    
    /**
     * Gets value of http header.
     * @param key the header name
     * @return the header value
     */
    public String getHttpHeader(String key){
        if(this.httpHeaders != null && this.httpHeaders.containsKey((Object)key)){
            return (String)this.httpHeaders.get((Object)key);
        } else {
            return null;
        }
    }
    
    
    /**
     *  Returns all HTTP headers as one string.
     */
    public String toString(){
        Enumeration keys = this.httpHeaders.keys();
        StringBuffer sb = new StringBuffer(1024);
        while(keys.hasMoreElements()){
            sb.append((String)keys.nextElement());
        }
        return sb.toString();
    }

    /**
     * Returns clone.      
     */
    public Object clone(){
        FipaHttpObject fop = new FipaHttpObject();
        if(this.envelopeACLpresentation != null) fop.envelopeACLpresentation = new String(this.envelopeACLpresentation);
        if(this.aclPresentation != null) fop.aclPresentation = new String(this.aclPresentation);
        
        if(this.envelope != null){ 
            fop.envelope = new byte[this.envelope.length];
            System.arraycopy(this.envelope,0,fop.envelope,0,fop.envelope.length);
        }                        
                
        if(this.acl != null){
            fop.acl = new byte[this.acl.length]; 
            System.arraycopy(this.acl,0,fop.acl,0,fop.acl.length);
        }
        
        fop.isValidFipaRequest = this.isValidFipaRequest;
        fop.response = this.response;
        if(this.statusCode != null) fop.statusCode = new String(this.statusCode);
        fop.statusCodeInt = this.statusCodeInt;
        if(this.httpHeaders != null){
            // Cannot use clone() in MIDP
            fop.httpHeaders = new Hashtable(this.httpHeaders.size());
            Enumeration em = this.httpHeaders.keys();
            String key;
            while(em.hasMoreElements()){
                key = (String)em.nextElement();
                fop.httpHeaders.put(key, new String((String)this.httpHeaders.get(key)));
            }
        }
        
        fop.headersTotalLength = this.headersTotalLength;
        fop.payloadEncoding = this.payloadEncoding;
        if(this.errorMsg != null) fop.errorMsg = new String(this.errorMsg);
        fop.isHttpRequest = this.isHttpRequest;
        try {
        if(this.hostAddress != null) fop.hostAddress = new HTTPAddress(this.hostAddress.toString());
        } catch (Exception e){
            e.printStackTrace();
        }
        fop.responseExpected = this.responseExpected;
        return (Object)fop;
    }
    
}
