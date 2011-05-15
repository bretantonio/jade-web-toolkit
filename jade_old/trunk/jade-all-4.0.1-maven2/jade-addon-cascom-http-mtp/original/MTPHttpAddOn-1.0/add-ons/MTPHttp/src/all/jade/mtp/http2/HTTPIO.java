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

import jade.domain.FIPAAgentManagement.Envelope;
import jade.util.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.EOFException;
import java.io.InputStream;
import java.io.OutputStream;
import cascom.fipa.util.BufferedInputStream;
//import java.util.NoSuchElementException;
//import java.util.StringTokenizer;
import cascom.util.StringTokenizer;
import cascom.util.NoSuchElementException;
import java.util.Enumeration;
//import cascom.jade.mtp.http.CascomGWConnectionManager;


/**
 * HTTPIO.java. Http parsing class for JADE HTTP MTP.
 *
 * @author Jose Antonio Exposito
 * @author MARISM-A Development group ( marisma-info@ccd.uab.es )
 * @version 0.1
 * @author Nicolas Lhuillier (Motorola Labs)
 * @version 1.0
 * @author Ahti Syreeni - TeliaSonera
 * @version 1.1
 */
public class HTTPIO {
    
    // Response codes
    public static final String OK    = "200 OK";
    public static final String ERROR = "406 Not Acceptable";
    public static final String ERROR_NOTFOUND = "404 Not Found";
    //private static final String UNAV  = "503 Service Unavailable";
    // HTTP constants
    private static final String HTTP1 = "HTTP/1.";
    private static final byte[] PROXY = {(byte) 'P',(byte) 'r',(byte) 'o',(byte) 'x',(byte) 'y',(byte) '-',(byte) 'C',(byte) 'o',(byte) 'n',(byte) 'n',(byte) 'e',(byte) 'c',(byte) 't',(byte) 'i',(byte) 'o',(byte) 'n',(byte) ':',(byte) ' '};
    private static final String PROXY_STR = "Proxy-Connection: ";
    private static final byte CR = (byte) '\r';
    private static final byte LF = (byte) '\n';
    private static final byte[] CRLF = {(byte) CR,(byte) LF};
    private static final byte[] POST = {(byte) 'P',(byte) 'O',(byte) 'S',(byte) 'T'};
    private static final String POST_STR = "POST";
    private static final byte[] CONTENT = {(byte) 'C',(byte) 'o',(byte) 'n',(byte) 't',(byte) 'e',(byte) 'n',(byte) 't',(byte) '-',(byte) 'T',(byte) 'y',(byte) 'p',(byte) 'e',(byte) ':',(byte) ' '};
    private static final String CONTENT_STR = "Content-Type: ";
    private static final byte[] CLENGTH = {(byte) 'C',(byte) 'o',(byte) 'n',(byte) 't',(byte) 'e',(byte) 'n',(byte) 't',(byte) '-',(byte) 'L',(byte) 'e',(byte) 'n',(byte) 'g',(byte) 't',(byte) 'h',(byte) ':',(byte) ' '};
    private static final byte[] MM = {(byte) 'm',(byte) 'u',(byte) 'l',(byte) 't',(byte) 'i',(byte) 'p',(byte) 'a',(byte) 'r',(byte) 't',(byte) '/',(byte) 'm',(byte) 'i',(byte) 'x',(byte) 'e',(byte) 'd'};
    private static final String MM_STR = "multipart/mixed";
    private static final byte[] BND = {(byte) 'b',(byte) 'o',(byte) 'u',(byte) 'n',(byte) 'd',(byte) 'a',(byte) 'r',(byte) 'y'};
    private static final String BND_STR = "boundary";
    private static final byte[] APPLI = {(byte) 'a',(byte) 'p',(byte) 'p',(byte) 'l',(byte) 'i',(byte) 'c',(byte) 'a',(byte) 't',(byte) 'i',(byte) 'o',(byte) 'n',(byte) '/'};
    private static final byte[] CONN = {(byte) 'C',(byte) 'o',(byte) 'n',(byte) 'n',(byte) 'e',(byte) 'c',(byte) 't',(byte) 'i',(byte) 'o',(byte) 'n',(byte) ':',(byte) ' '};
    private static final String CONN_STR = "Connection: ";
    public static final String CLOSE   = "close";
    public static final String KA      = "Keep-Alive";
    private static final byte[] HTTP = {(byte) 'H',(byte) 'T',(byte) 'T',(byte) 'P',(byte) '/',(byte) '1',(byte) '.',(byte) '1'};
    private static final byte[] CACHE =
    {(byte) 'C',(byte) 'a',(byte) 'c',(byte) 'h',(byte) 'e',(byte) '-',(byte) 'C',(byte) 'o',(byte) 'n',(byte) 't',(byte) 'r',(byte) 'o',(byte) 'l',(byte) ':',(byte) ' ',(byte) 'n',(byte) 'o',(byte) '-',(byte) 'c',(byte) 'a',(byte) 'c',(byte) 'h',(byte) 'e'};
    private static final byte[] MIME = {(byte) 'M',(byte) 'i',(byte) 'm',(byte) 'e',(byte) '-',(byte) 'V',(byte) 'e',(byte) 'r',(byte) 's',(byte) 'i',(byte) 'o',(byte) 'n',(byte) ':',(byte) ' ',(byte) '1',(byte) '.',(byte) '0'};
    private static final byte[] HOST = {(byte) 'H',(byte) 'o',(byte) 's',(byte) 't',(byte) ':',(byte) ' '};
    private static final String HOST_STR = "Host: ";
    private static final byte[] DL = {(byte) '-',(byte) '-'};
    private static final String DL_STR = "--";
    private static final String BLK     = "";
    private static final byte[] MIME_MULTI_PART_HEADER =
    {(byte) 'T',(byte) 'h',(byte) 'i',(byte) 's',(byte) ' ',(byte) 'i',(byte) 's',(byte) ' ',(byte) 'n',(byte) 'o',(byte) 't',(byte) ' ',(byte) 'p',(byte) 'a',(byte) 'r',(byte) 't',(byte) ' ',(byte) 'o',(byte) 'f',(byte) ' ',(byte) 't',(byte) 'h',(byte) 'e',(byte) ' ',
             (byte) 'M',(byte) 'I',(byte) 'M',(byte) 'E',(byte) ' ',(byte) 'm',(byte) 'u',(byte) 'l',(byte) 't',(byte) 'i',(byte) 'p',(byte) 'a',(byte) 'r',(byte) 't',(byte) ' ',(byte) 'e',(byte) 'n',(byte) 'c',(byte) 'o',(byte) 'd',(byte) 'e',(byte) 'd',(byte) ' ',
             (byte) 'm',(byte) 'e',(byte) 's',(byte) 's',(byte) 'a',(byte) 'g',(byte) 'e',(byte) '.'};
             
             private static final byte[] CHARSET = {(byte) ';',(byte) ' ',(byte) 'c',(byte) 'h',(byte) 'a',(byte) 'r',(byte) 's',(byte) 'e',(byte) 't',(byte) '='};
             private static final byte[] TEXT = {(byte) 't',(byte) 'e',(byte) 'x',(byte) 't'};
             private static final byte[] TEXT_HTML = {(byte) 't',(byte) 'e',(byte) 'x',(byte) 't',(byte) '/',(byte) 'h',(byte) 't',(byte) 'm',(byte) 'l'};
             private static final byte[] HTML_BEGIN = {(byte) '<',(byte) 'h',(byte) 't',(byte) 'm',(byte) 'l',(byte) '>',(byte) '<',(byte) 'b',(byte) 'o',(byte) 'd',(byte) 'y',(byte) '>',(byte) '<',(byte) 'h',(byte) '1',(byte) '>'};
             private static String HTML_BEGIN_STR = new String(HTML_BEGIN);
             private static final byte[] HTML_END = {(byte) '<',(byte) '/',(byte) 'h',(byte) '1',(byte) '>',(byte) '<',(byte) '/',(byte) 'b',(byte) 'o',(byte) 'd',(byte) 'y',(byte) '>',(byte) '<',(byte) '/',(byte) 'h',(byte) 't',(byte) 'm',(byte) 'l',(byte) '>'};
             
             private static Logger logger = Logger.getMyLogger("HTTPIO");
             private static byte[] finalBoundary = null;
             
             private static boolean useHtmlResponses = true;
             
             // Calculate a basic boundary to be used in all HTTP messages
             static {
                 // prepare the HTTP boundary
                 StringBuffer boundary = new StringBuffer(100);
                 for( int i=0 ; i < 31 ; i++ ) {
                     boundary.append(Integer.toString(15,16));
                 }
                 try {
                     finalBoundary = boundary.toString().getBytes("ISO-8859-1");
                 } catch (Exception e){
                     finalBoundary = "eerqwrioeirprou143409f2f".getBytes();
                 }
                 if(finalBoundary == null){
                     //System.err.println("FinalBoundaryNull");
                 }
             }
             

             /**
              * Sets whether HTML-response messages should be added
              * to HTTP responses. This is optional in FIPA specification.
              * 
              * @param use If set true, HTML-messages will be added to 
              * all HTTP-responses.
              *
              */             
             public static void useHtmlResponses(boolean use){
                 useHtmlResponses = use;
             }
             
             
             
  /* ***********************************************
   *                 WRITE METHODS
   * ***********************************************/
        
             
             
             /**
              * Write the message to the OutputStream associated to the Sender
              */
             public static void writeAll(OutputStream output, byte[] message) throws IOException {
                 
                 /*
                     int mtu = 1024-1;
                 if(message.length > mtu){
                     // for MIDP devices where there can be MTU
                     int sent = 0;
                     while(sent < message.length){
                         if(message.length - sent >= mtu){
                             output.write(message, sent, mtu);
                         } else {
                             output.write(message, sent, message.length - sent);
                         }
                         output.flush();
                         sent += mtu;
                     }
                     output.write(CRLF);
                     output.flush();
                 } else {
                  */
                 /*
                 System.out.println("#######################");
                 for(int i=0; i < message.length; i++){
                     System.out.print((char)message[i]);
                 }
                  
                 System.out.println("#######################");
                  */
                 
                 output.write(message);
                 output.write(CRLF);
                 //System.err.println("Flush start");
                 output.flush();
                 //System.err.println("  Flush end ok");
                 //}
             }
             
             /**
              * Create a generic message of HTTP with the input msgCode
              * and type of connection (close or Keep-Alive)
              */
             public static byte[] createHTTPResponse(FipaHttpObject fho) {
                 ByteArrayOutputStream message = new ByteArrayOutputStream(fho.sizeOfHeaders());
                 try {
                     message.write(HTTP);
                     message.write(' ');
                     writeLowBytes(message,fho.getStatusCode());
                     message.write(CRLF);
                     message.write(CONTENT);
                     message.write(TEXT_HTML);
                     message.write(CRLF);
                     message.write(CACHE);
                     message.write(CRLF);
                     message.write(CONN);
                     writeLowBytes(message, fho.getConnectionType());
                     message.write(CRLF);
                     if(fho.getHeaderNames() != null){
                         Enumeration keys = fho.getHeaderNames();
                         String header1;
                         while(keys.hasMoreElements()) {
                             header1 = (String) keys.nextElement();
                             if(header1.startsWith(FipaHttpObject.USER_HEADER_PREFIX)){
                                 writeLowBytes(message,header1);
                                 message.write(':');
                                 message.write(' ');
                                 writeLowBytes(message,fho.getHttpHeader(header1));
                                 message.write(CRLF);
                             }
                         }
                     }
                     
                     /**
                      * Add html text only if user wants. It is not 
                      * necessary and not recommendable.
                      */
                     if(useHtmlResponses){
                        message.write(CRLF);
                        message.write(HTML_BEGIN);
                        writeLowBytes(message,""+fho.getStatusCode());
                        message.write(HTML_END);
                     }
                     
                 } catch (IOException exception) {
                     exception.printStackTrace();
                 }
                 return message.toByteArray();
             }
             
             /**
              * Prepare the HTML header
              */
             public static byte[] createHTTPHeader(HTTPAddress host, FipaHttpObject fho, int length, boolean proxy) {
                 //Put the header
                 
                 ByteArrayOutputStream header = null; 
                 
                 try {
                     header = new ByteArrayOutputStream(100+fho.sizeOfHeaders());
                     header.write(POST);
                     header.write(' ');
                     
                     writeLowBytes(header,host.toString());
                     
                     header.write(' ');
                     header.write(HTTP);
                     header.write(CRLF);
                     header.write(CACHE);
                     header.write(CRLF);
                     header.write(MIME);
                     header.write(CRLF);
                     header.write(HOST);
                     
                     writeLowBytes(header,host.getHost());
                     header.write(':');
                     writeLowBytes(header,""+host.getPort());
                     header.write(CRLF);
                     header.write(CONTENT);
                     header.write(MM);
                     header.write(' ');
                     header.write(';');
                     header.write(' ');
                     header.write(BND);
                     header.write('=');
                     header.write('\"');
                     header.write(finalBoundary);
                     header.write('\"');
                     header.write(CRLF);
                     //put the Content-Length
                     header.write(CLENGTH);
                     writeLowBytes(header,Integer.toString(length));
                     header.write(CRLF);
                     //put the Connection policy
                     if (fho.getProxyConnection() != null) {
                         header.write(PROXY);
                         writeLowBytes(header,fho.getProxyConnection());
                         header.write(CRLF);
                     } else if(fho.getConnectionType()!= null){
                         header.write(CONN);
                         writeLowBytes(header, fho.getConnectionType());
                         header.write(CRLF);
                     }
                     
                     //insert user defined headers if such exist
                     // add user defined headers, those should be in proper format
                     if(fho.getHeaderNames() != null){
                         Enumeration keys = fho.getHeaderNames();
                         String header1;
                         while(keys.hasMoreElements()) {
                             header1 = (String) keys.nextElement();
                             if(header1 != null && header1.startsWith(FipaHttpObject.USER_HEADER_PREFIX)){
                                 writeLowBytes(header,header1);
                                 header.write(':');
                                 header.write(' ');
                                 if(fho.getHttpHeader(header1) != null){
                                    writeLowBytes(header,fho.getHttpHeader(header1));
                                 } else {
                                    writeLowBytes(header,"");                                     
                                 }
                                 header.write(CRLF);
                             }
                         }
                     }
                     header.write(CRLF);
                     ////System.err.println("Writing user headers ended, flushing");
                     header.flush();
                 } catch (IOException exception) {
                     exception.printStackTrace();
                 } catch (Exception e){
                     String errstr = "Host:"+(host != null)+", fho:"+(fho != null);
                     if(host != null){
                         errstr+=", host.host:"+host.getHost()+", host.tostring:"+host.toString();
                     } else {
                         System.err.println("Host cannot be null when writing HTTP-header!!");                         
                     }
                     System.out.println(errstr);
                     e.printStackTrace();
                 }
                 return header.toByteArray();
             }
             
             
             /**
              * Prepare the HTML body
              */
             public static byte[] createHTTPBody(byte[] env, byte[] payload, String aclPresentation, String payloadEncoding) {
                 ByteArrayOutputStream body = new ByteArrayOutputStream(payload.length+env.length + 100);
                 
                 try {
                     //PREPARE BODY
                     body.write(MIME_MULTI_PART_HEADER);
                     body.write(CRLF);
                     body.write(DL);
                     body.write(finalBoundary);
                     body.write(CRLF);
                     //Insert The envelope
                     // Put the Content-Type
                     body.write(CONTENT);
                     body.write(APPLI);
                     writeLowBytes(body,aclPresentation);
                     body.write(CRLF);
                     body.write(CRLF); //An empty line
                     body.write(env);
                     
                     //Put the boundary delimit.
                     body.write(CRLF);
                     body.write(DL);
                     body.write(finalBoundary);
                     body.write(CRLF);
                     //Insert the ACL message
                     //Put the Content-Type
                     if ((payloadEncoding != null) && (payloadEncoding.length() > 0)) {
                         body.write(CONTENT);
                         body.write(APPLI);
                         writeLowBytes(body,aclPresentation);
                     } else {
                         body.write(CONTENT);
                         body.write(APPLI);
                         body.write(TEXT);
                     }
                     body.write(CRLF);
                     body.write(CRLF);
                     //ACL part
                     //Insert the ACL payload
                     body.write(payload);
                     body.write(CRLF);
                     //Put the final boundary
                     body.write(DL);
                     body.write(finalBoundary);
                     body.write(DL);
                     body.write(CRLF);
                     
                     body.flush();
                 } catch (IOException exception) {
                     exception.printStackTrace();
                 }
                 return body.toByteArray();
             }
             
             
  /* ***********************************************
   *             READS METHODS
   * ***********************************************/
             
             /**
              * Blocks on read until something is available on the stream
              * or the stream is closed
              */
  /*
    public static String blockOnRead(BufferedReader br) throws IOException {
    //Skip empty lines
    String line = null;
    while(BLK.equals(line=br.readLine()));
    return line;
    }
   */
             
             /**
              * Parse the input message, this message is received from the master server
              *
              * @param input The entire unprocessed http-request as input stream
              * @return FipaHttpObject results of parsing or null if there was nothing to be parsed. That is, if
              * IOError (time out) occured before starting parsing. If the IOException is got during the
              * actual parsing, the IOException will be thrown.
              */
             public static void parseRequest(FipaHttpObject ro, BufferedInputStream input) throws IOException {
                 //make sure the buffer will not cause unnecessary blocking
                 input.setNextEOS(1);
                 
                 //FipaHttpObject ro = new FipaHttpObject();
                 
                 //For the Control of sintaxis
                 String  host = null;
                 //boolean foundMime       = false;
                 boolean foundBoundary   = false;
                 //boolean findContentType = false;
                 String  boundary = null;
                 String line;
                 //while(BLK.equals(line=readLineFromInputStream(input))); // skip empty lines
                 
//System.err.println("Now started parsing request");
                 // very dirty fix for the case when previous HTTP-response HTML content
                 // is somehow still unprocessed and it must be skipped.
//System.err.println("HTTPIO.parseRequest 3");
                 
                 //try {
                 line=readLineFromInputStream(input);
                 //} catch (IOException ioe){
                 //    return null;
                 //}
                 
                 //try {
//System.err.println("HTTPIO.parseRequest 41");
                 
                 /** Ignore empty lines, those can be ping requests */
                 while (line.trim().equals("")){
                     line=readLineFromInputStream(input);
                 }
                 
                 
                 
                 
//System.err.println("### :"+line);
                 //} catch (IOException ioe){
                 //                 return null;
                 //           }
                 
                 StringTokenizer st = new StringTokenizer(line);
//System.err.println("HTTPIO.parseRequest 4");
                 try {
                     String token = st.nextToken();
                     
                     // Dirty hack to preserve compliance with old version of HTTP-MTP:
                     // this is the HTML-end of the previous HTTP-response, remove it
                     if (token.toLowerCase().startsWith(HTML_BEGIN_STR)){
                         //System.err.println("Removing html");
                         line = readLineFromInputStream(input);
                         while(line.trim().equals("")){
                             line = readLineFromInputStream(input);
                         }
                         st = new StringTokenizer(line);
                         token = st.nextToken();
                     }
                     
                     if(token.startsWith(HTTPIO.HTTP1)){
                         //this is a http response
                         //System.err.println("This is response");
                         ro.setIsResponse(true);
                         try{
                             ro.setStatusCode(st.nextToken().trim());
                         } catch (Exception e){
                             ro.setErrorMsg("Malformed HTTP/1.1, was not http request and not response");
                             return;
                         }
                     } else if(token.toLowerCase().equals(POST_STR.toLowerCase())) {
                         ro.setResponseExpected(true);
                     } else {
                         //System.err.println("Not request or response");
                         ro.setResponseExpected(true);
                         ro.setErrorMsg("Malformed input, was neither valid HTTP request nor response, token:"+token);
                         return;
                     }
                     
                     //System.err.println("HTTPIO.parseRequest 5");
                     
                     // if this is a request, then the HTTP version must be 1.1
                     if(!ro.isResponse()){
                         try {
                             ro.setHostAddr(new HTTPAddress(st.nextToken().trim()));
                         } catch (Exception e){}
                         if(!st.nextToken().toUpperCase().startsWith(HTTPIO.HTTP1.toUpperCase())){
                             ro.setErrorMsg("Malformed HTTP/1.1 ");
                             return;
                         }
                     }
                     
                 } catch(NoSuchElementException nsee) {
                     ro.setErrorMsg("Malformed start line !: "+line);
                     return;
                 }
//Process rest of header
                 
//System.err.println("HTTPIO.parseRequest 6");
                 
                 while (!BLK.equals(line=readLineFromInputStream(input))) {
                     String lowerCaseLine = line.toLowerCase();
//System.err.println("HTTPIO.parseRequest 7:"+line);
                     if (lowerCaseLine.startsWith(HOST_STR.toLowerCase())) {
                         host = processLine(line); //De momento solo controlamos que este
                         
      /* // NL do not test MIME version for interoperability with other MTP
         if (line.toLowerCase().startsWith(MIME.toLowerCase())) {
         foundMime = true;
         }
       */
                         
                     } else if (lowerCaseLine.startsWith(CONN_STR.toLowerCase())) {
                         ro.setHttpHeader(ro.HTTP_CONNECTION,processLine(line));
                         
                         
                     } else if (lowerCaseLine.startsWith(CONTENT_STR.toLowerCase())) {
                         //Process the left part
                         
                         if (!ro.isResponse() && !(processLine(line).toLowerCase().startsWith(MM_STR))){
                             ro.setErrorMsg("Invalide content type in request: MULTIPART/MIXED");
                             return;
                         }
                         
                         if(!ro.isResponse()){
                             //Process the right part
                             int pos = line.indexOf(BND_STR);
                             if (pos == -1) {
                                 // Boundary on next line
                                 line=readLineFromInputStream(input);
                                 if ((pos = line.indexOf(BND_STR)) == -1){
                                     ro.setErrorMsg("MIME boundary not found");
                                     // Bounday not found
                                     
                                     return;
                                 }
                             }
                             line = line.substring(pos+BND_STR.length());
                             pos = line.indexOf("\"")+1;
                             boundary = DL_STR+line.substring(pos,line.indexOf("\"",pos));
                             foundBoundary = true;
                             
                         }
                     } else {
                         int index = line.indexOf(":");
                         if(index > -1){
                             String key = line.substring(0, index).trim();
                             String value = processLine(line).trim();
//System.err.println("Setting user header:"+key+":"+value);
                             ro.setHttpHeader(key,value);
                         }
                     }
//System.err.println("Loop loopt...");
                 }//end while
                 
//System.err.println("end of loop");
                 if (ro.getConnectionType() == null) {
                     ro.setHttpHeader(ro.HTTP_CONNECTION,ro.HTTP_CONNECTION_KA); //Default Connection
                 }
//System.err.println("HTTPIO.parseRequest 8");
                 
                 // at this point all the headers of response are parsed and object can be returned
                 if(ro.isResponse()){
//System.err.println("Returning response");
                     return;
                 }
                 
                 if(ro.getHttpHeader("Content-Length") == null || "0".equals(ro.getHttpHeader("Content-Length"))){
                     //System.err.println("Is http-request");
                     ro.setIsHttpRequest(true);
                     return;
                 }
                 
                 
                 // Now, we know the number of bytes that is comming and we can set
                 // buffering on.
                 
                 try {
                     input.setNextEOS(Integer.parseInt(ro.getHttpHeader("Content-Length"))-5);
                     //System.err.println("Content-length set");
                 } catch(Exception e){
                     //e.printStackTrace();
                     ro.setErrorMsg("Invalid content length:"+ro.getHttpHeader("Content-Length")+":"+e.getMessage());
                     return;
                 }
                 //if( !foundBoundary || !foundMime) {
//System.err.println("HTTPIO.parseRequest 9");
                 
                 if(!foundBoundary) {
                     //System.err.println("HTTPIO.parseRequest 10");
                     while(((line=readLineFromInputStream(input))!=null)&&(!line.equals(BLK))) ;
                     ro.setErrorMsg("Boundary not found");
                     return;
                 }
                 
                 //jump to first  "--Boundary"
                 
                 while(BLK.equals(line=readLineFromInputStream(input))) ; // skip empty lines
                 
                 do {
                     if (line.startsWith(boundary)) {
                         break;
                     }
                     
                     
                 } while(!BLK.equals(line=readLineFromInputStream(input)));
                 
                 while(BLK.equals(line=readLineFromInputStream(input))) ; // skip empty lines
                 
                 // Read the content-type for Envelope and assign it to given parameter
//System.err.println("HTTPIO.parseRequest 10bn");
                 do {
                     if(line.toLowerCase().startsWith(CONTENT_STR.toLowerCase())) {
                         String envelopeACLpresentation = line.substring(CONTENT_STR.length(), line.length()).trim();
                         //strip the application/ off
                         int indexOfSlash = envelopeACLpresentation.indexOf('/');
                         if(indexOfSlash < 0){
                             ro.setErrorMsg("Malformed Content-Type");
                             return;
                         }
                         ro.setEnvelopePresentation(envelopeACLpresentation.substring(indexOfSlash + 1, envelopeACLpresentation.length()).trim().toLowerCase());
                         break;
                     }
                 } while(!BLK.equals(line=readLineFromInputStream(input)));
                 
                 if(ro.getEnvelopePresentation() == null){
                     while(((line=readLineFromInputStream(input))!=null)&&(!line.equals(BLK))) ;
                     ro.setErrorMsg("Content-Type for envelope encoding is missing from http request");
                     return;
                 }
                 
                 
                 
                 ByteArrayOutputStream boundaryPattern = new ByteArrayOutputStream(boundary.length()+6);
                 boundaryPattern.write(CRLF);
                 boundaryPattern.write(boundary.getBytes("ISO-8859-1"));
                 boundaryPattern.write(CRLF);
                 boundaryPattern.flush();
                 //Capture the message envelope
                 
                 int curCharacter = input.read();
                 
                 while(((curCharacter = input.read()) == CR ) || (curCharacter == LF)) ;  // Dirty hack: Skip leading blank lines.
                 ByteArrayOutputStream envBytes = new ByteArrayOutputStream();
                 if (curCharacter >= 0) {
                     envBytes.write(curCharacter);
                     envBytes = readBytesUpTo(input, envBytes, boundaryPattern.toByteArray());
                 }   else {
                     ro.setErrorMsg("Missing envelope");
                     return;
                 }
                 
//System.err.println("HTTPIO.parseRequest 11");
                 
                 ro.setEnvelope(envBytes.toByteArray());
                 if(ro.getEnvelope() == null) {
                     ro.setErrorMsg("Envelope missing");
                     return;
                 }
                 
//System.err.println("HTTPIO.parseRequest 12");
                 //Capture the ACL part
                 //JMP to ACLMessage
                 while(BLK.equals(line=readLineFromInputStream(input))) ; // skip empty lines
                 // Read content-type
//System.err.println("HTTPIO.parseRequest 13");
                 do {
//System.err.println("HTTPIO.parseRequest 13A");
                     if(line.toLowerCase().startsWith(CONTENT_STR.toLowerCase())) {
//System.err.println("HTTPIO.parseRequest 13B");
                         String aclPresentation = line.substring(CONTENT_STR.length(), line.length()).trim();
                         int indexOfSlash = aclPresentation.indexOf('/');
                         if(indexOfSlash < 0){
                             ro.setErrorMsg("Malformed content-type");
                             return;
                         }
                         ro.setACLMsgPresentation(aclPresentation.substring(indexOfSlash + 1, aclPresentation.length()).trim().toLowerCase());
                     }
                     
                     
                 } while(!BLK.equals(line=readLineFromInputStream(input)));
                 /*
                 if(ro.getACLMsgPresentation() == null){
                     ro.setErrorMsg("Content-Type for acl encoding is missing from http request");
                     return;
                 }
                  */
                 
                 
                 
                 //Create last boundary for capture the ACLMessage
                 boundaryPattern = new ByteArrayOutputStream(boundary.length()+6);
                 boundaryPattern.write(CRLF);
                 boundaryPattern.write(boundary.getBytes("ISO-8859-1"));
                 boundaryPattern.write(DL);
                 
                 //System.err.println("HTTPIO.parseRequest 15");
                 
                 //Capture the acl part.
                 int character = -1;
                 
                 //ByteArrayOutputStream acl = new ByteArrayOutputStream();
                 //System.err.println("HTTPIO.parseRequest 16");
                 
                 while(((character = input.read()) == CR ) || (character == LF)) ;  // Dirty hack: Skip leading blank lines.
                 //System.err.println("HTTPIO.parseRequest 17");
                 if (character >= 0) {
                     //acl.write(character);
                     ////FIXME: this is very time-consuming task if the ACL message is huge, e.g 1MB
                     //acl = readBytesUpTo(input,acl,boundaryPattern.toByteArray());
                     //ro.setACLMessage(acl.toByteArray());
                     
                     ByteArrayOutputStream acl = new ByteArrayOutputStream();
                     acl.write(character);
                     acl = readBytesUpTo(input,acl,boundaryPattern.toByteArray());
                     ro.setACLMessage(acl.toByteArray());
                 }
                 
                 ro.setIsFipaRequest(true);
                 
                 /*
                  //System.err.println("ACLMessage was parsed from request:");
                  byte[] temp = ro.getACLMessage().toByteArray();
                  for(int i=0; i < temp.length; i++){
                        System.err.print(Integer.toHexString((int)temp[i])+" ");
                  }
                  //System.err.println("=== Size:"+temp.length);
                  */
                 
                 // set the buffering off
                 input.setNextEOS(1);
                 return;
             }
             
             
             
             /**
              * Capture and return the code of response message, this message is received from client
              */
             public static FipaHttpObject getResponse(InputStream input)
             throws IOException {
                 FipaHttpObject fho = new FipaHttpObject();
                 fho.setIsResponse(true);
                 try {
                     String line = null;
                     //Capture and process the response message
                     while (!(line=readLineFromInputStream(input)).startsWith(HTTP1));
                     //capture the response code
                     fho.setStatusCode(processLine(line));
                     //Read all message
                     while(((line=readLineFromInputStream(input))!=null)&&(!line.equals(BLK))) {
                         if (line.toLowerCase().startsWith(CONN_STR.toLowerCase())) {
                             fho.setHttpHeader(fho.HTTP_CONNECTION,processLine(line));
                         } else if (line.toLowerCase().startsWith(PROXY_STR.toLowerCase())) {
                             fho.setHttpHeader(fho.HTTP_CONNECTION,processLine(line));
                         } else if(line.startsWith(FipaHttpObject.USER_HEADER_PREFIX)){
                             int index = line.indexOf(":");
                             if(index > -1){
                                 // support for additional user defined headers
                                 fho.setHttpHeader(line.substring(0,index), processLine(line));
                             }
                         }
                     }
                     if (fho.getConnectionType() == null) {
                         fho.setHttpHeader(fho.HTTP_CONNECTION, fho.HTTP_CONNECTION_KA); //Default Connection type
                     }
                     //System.err.println("Get response 4");
                     //fho.setIsFipaRequest(true);
                     return fho;
                 } catch(Exception e) {
                     // Connection has been closed before we receive confirmation.
                     // We do cannot know if message has been received
                     fho.setHttpHeader(fho.HTTP_CONNECTION, fho.HTTP_CONNECTION_CL);
                     return fho; // NOT OK
                 }
             }
             
             /*
              * Writes FipaHttpRequest to outputStream
              */
             public static void writeHttpRequest(OutputStream out,FipaHttpObject httpObject, HTTPAddress host, boolean useProxy) throws IOException {
                 byte[] body;
                 byte[] header;
                 ByteArrayOutputStream requestStream;
                 
                 if(host == null){
                     throw new IOException("Destination host cannot be null");
                 }
                 
                 if(httpObject.isFipaRequest()){
                     //System.err.println("HTTPIO writing fipa http request msg "+httpObject.getHttpHeader(CascomGWConnectionManager.MSGNUMBER_HEADER));;
                     body = HTTPIO.createHTTPBody(httpObject.getEnvelope(), httpObject.getACLMessage(),
                             httpObject.getEnvelopePresentation().trim(), httpObject.getPayloadEncoding());
                     header = HTTPIO.createHTTPHeader(host,httpObject,body.length, useProxy);
                     requestStream = new ByteArrayOutputStream(header.length+body.length);
                     requestStream.write(header);
                     requestStream.write(body);
                     
                 } else {
                     //System.err.println("HTTPIO writing http request msg "+httpObject.getHttpHeader(CascomGWConnectionManager.MSGNUMBER_HEADER));;
                     header = HTTPIO.createHTTPHeader(host,httpObject,0, useProxy);
                     requestStream = new ByteArrayOutputStream(header.length);
                     requestStream.write(header);
                 }
                 requestStream.flush();
                 requestStream.close();
                 byte[] request = requestStream.toByteArray();
                 
                 writeAll(out, request);
                 /*
                 out.write(request);
                 out.write(CRLF);
                 out.flush();
                  */
             }
             
             /**
              * Writes given FipaHttpObject to given outputstream.
              */
             public static void writeHttpResponse(OutputStream out, FipaHttpObject fho) throws IOException {
                 writeAll(out, createHTTPResponse(fho));
             }
             
             /**
              * return the next information of search in the line
              */
             private static String processLine(String line)
             throws IOException {
                 StringTokenizer st = new StringTokenizer(line);
                 try {
                     st.nextToken(); // Consumme first token
                     return st.nextToken();
                 } catch(NoSuchElementException nsee) {
                     throw new IOException("Malformed line !: "+line);
                 }
             }
             
             /**
              * Reads byte sequence from specified input stream into specified output stream up to specified
              * byte sequence pattern is occurred. The output byte sequence does not contains any bytes matched
              * with pattern. If the specified pattern was not found until the input stream reaches at end, output
              * all byte sequence up to end of input stream and returns false.
              *
              * @param input specified input stream.
              * @param output specified output stream.
              * @param pattern specified pattern byte seqence.
              * @return ByteArrayOutputStream the copied bytes.
              * @throws IOException  If an I/O error occurs.
              * @throws IllegalArgumentException If pattern is null or pattern is empty.
              * @author mminagawa
              */
             private static ByteArrayOutputStream readBytesUpTo(InputStream input, ByteArrayOutputStream output, byte[] pattern) throws IOException {
                 if ((pattern == null) || (pattern.length == 0)) {
                     throw new IllegalArgumentException("Specified pattern is null or empty.");
                 }
                 
/*
System.out.println("Pattern:");
for(int i=0;i<pattern.length;i++){
    System.err.print(Integer.toHexString((int)pattern[i]));
}
System.out.println("==");
 */
                 
                 int patternIndex = 0;
                 boolean matched = false;
                 boolean atEnd = false;
                 while ((!matched) && (!atEnd)) {
                     int readByte = input.read();
//System.out.print((char)readByte);
//System.out.print(".");
                     if (readByte < 0) {
                         atEnd = true;
                         if (patternIndex != 0) {
                             output.write(pattern,0,patternIndex);
                             patternIndex = 0;
                         }
                     } else {
                         if (readByte == pattern[patternIndex]) {
                             patternIndex++;
                             if (patternIndex >= pattern.length) {
                                 matched = true;
                             }
                         } else {
                             if (patternIndex != 0) {
                                 output.write(pattern,0,patternIndex);
                                 patternIndex = 0;
                             }
                             output.write(readByte);
                         }
                     }
                 }
                 return output;
             }
             
             /**
              * Read a line of text from specified input stream.  A line is considered to be
              * terminated by a carriage return ('\r') followed immediately by a linefeed ('\n').
              *
              * @param input specified input stream to read from.
              * @return A String containing the contents of the line, not including any line-termination
              *          characters
              * @throws IOException  If an I/O error occurs or end of line is reached
              * @author mminagawa
              */
             private static String readLineFromInputStream(InputStream input) throws IOException {
                 StringBuffer buffer = new StringBuffer(256);
                 int characterByte;
                 boolean justBeforeCR = false;
                 boolean terminated = false;
                 boolean entered = false;
                 while ((!terminated) && ((characterByte = input.read()) >= 0)) {
                     entered = true;
                     switch (characterByte) {
                         case CR :
                             if (justBeforeCR) {
                                 buffer.append((char)CR);
                             } else {
                                 justBeforeCR = true;
                             }
                             break;
                         case LF :
                             if (justBeforeCR) {
                                 terminated = true;
                             } else {
                                 buffer.append((char)LF);
                             }
                             justBeforeCR = false;
                             break;
                         default :
                             if (justBeforeCR) { buffer.append((char)CR); }
                             buffer.append((char)characterByte);
                             justBeforeCR = false;
                     }
                 }
                 //if (!entered) { return null; }
                 if (!entered) { throw new EOFException("End of stream"); }
                 if ((!terminated) && (justBeforeCR)) {
                     buffer.append((char)CR);
                 }
                 
                 //logger.log(Logger.INFO,buffer.toString());
                 return buffer.toString();
             }
             
             /**
              * Write characters contained specified string to specified output stream.<br />
              * These characters must be 7-bit character, and stored only low-byte of each code.
              *
              * @param output specified output stream.
              * @param string specified string to output.
              * @throws IOException  If an I/O error occurs.
              * @author mminagawa
              */
             private static void writeLowBytes(OutputStream output, String string) throws IOException {
                 for (int i = 0 ; i < string.length() ; i++ ) {
                     output.write(string.charAt(i));
                 }
             }
             
             
             /**
              * Tests the output stream by writing a few bytes so that
              * can be sured that writing to the output does not cause exceptions.
              * The bytes are actually an empty line so that parsing will not be
              * disturbed at receiving parse-method.
              *
              * @throws IOException If such occures while writing to output.
              */
             public static void testOutput(OutputStream out) throws IOException {
                 out.write(CRLF);
                 out.flush();
             }
             
             
} // End of HTTPIO class

