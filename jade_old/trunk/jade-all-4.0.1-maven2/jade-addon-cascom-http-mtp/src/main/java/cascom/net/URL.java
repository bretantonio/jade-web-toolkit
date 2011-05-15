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

package cascom.net;

/**
 * Class implementing basic cases of URL. 
 * This class does not use java J2SE library methods.
 * 
 * @author Ahti Syreeni
 * @version 0.1
 */
public class URL {
    private String protocol = null;
    private String host = null;
    private String port = "80";
    private String file = null;
    private String anchor = null;
    
    private URL(){}
    
     /** 
     * Creates a new instance of URL from String. If
     * the protocol is not in the given string, the default protocol HTTP is used
     * for protocol. Default port for http is 80.
     * 
     * The string should be valid URL of form:
     * http://host:port/dir/file#anchor .  The port, file and anchor are not
     * mandatory.        
     * 
     * Note that this implementation does not
     * follow the URL specification completely so you should not use any other 
     * form of url than the example above (eg. queries).
     *
     * @param httpAddress the address of HTTP destination, eg. http://host.com:6060
     */
   public URL(String httpAddress) throws MalformedURLException, IllegalArgumentException {
        if(httpAddress == null || httpAddress.indexOf('?') > -1){
            throw new IllegalArgumentException("Illegal parameter given to HTTPTransportAddress");
        }
        httpAddress = httpAddress.trim();        
        if(httpAddress.length() < 1){
            throw new IllegalArgumentException("Empty String cannot be parameter for jade.mtp.http.HTTPAddress");            
        }
        
        int start = 0;
        //search the protocol
        int index = httpAddress.indexOf("://");
        if(index > 0){
            this.protocol = httpAddress.substring(start, index);
            start = index + 3;
        } else {
            this.protocol = "http";
        }        
        
        //search port
        index = httpAddress.indexOf(':', start);
        if(index > start) {
            this.host = httpAddress.substring(start, index);
            if(this.host.indexOf('/') > -1){
                throw new MalformedURLException("Malformed url string (: allowed only for port) in jade.mtp.http.HTTPAddress:"+httpAddress);
            }
            int index2 = httpAddress.indexOf("/",index);
            if(index2 > index){
                this.port = httpAddress.substring(index + 1, index2).trim();
                start = index2+1; 
            } else if (index + 1  < httpAddress.length() -1){
                this.port = httpAddress.substring(index + 1, httpAddress.length()).trim();
                return; //string contained only host and port
            } else {
                throw new MalformedURLException("Malformed url string (port missing after :) in jade.mtp.http.HTTPAddress:"+httpAddress);                
            }
        } else {  // no port in string, get the host next
            index = httpAddress.indexOf("/", start);
            if(index > 0){
                this.host = httpAddress.substring(start, index);
                if(index + 1 < httpAddress.length()) {
                    start = index + 1;
                } else {
                    return; //string included just host and it was ended with /
                }
            } else {
                this.host = httpAddress.substring(start, httpAddress.length()).trim();
                return; // string included only the host
            }
        } 

        // search for file
        index = httpAddress.indexOf("#");
        if(index <= 0){
            this.file = httpAddress.substring(start, httpAddress.length()).trim();
        } else {
            this.file = httpAddress.substring(start, index).trim();
            if(index + 1 < httpAddress.length()){
                this.anchor = httpAddress.substring(index + 1, httpAddress.length()).trim();
            }
        }                                
    }
   
   
    public URL(String protocol, String addr, int port, String file) throws MalformedURLException {
        if(protocol == null || addr == null || file == null || port < 0){
            throw new MalformedURLException();
        }
        this.protocol = protocol;
        this.host = addr;
        this.port = ""+port;
        this.file = file;        
    }

    // get methods
    
    /**
     * Gets the protocol (http or https)
     */
    public String getProtocol() {return this.protocol;}
    
    
    /*
     * Gets the host name of this address. It can IP address or name.
     */
    public String getHost() {return this.host;}
    
    
    
    /*
     * Gets the port for this address. Default value is port 80. 
     */
    public int getPort() throws NumberFormatException {return Integer.parseInt(this.port);}
    
    /*
     * Gets the file of this address or null if address contains no file.
     */
    public String getFile() {return this.file;}
    
    
    /*
     * Gets the anchor for this address or null if address contains no anchor.
     */
    public  String getRef() {return this.anchor;}    
    
    
    /*
     * Converts given TransportAddress to String presentation. Note, althought
     * the possible anchor will be converted as well, it is hard find any reason
     * why to use it in transport destination.
     *
     * @param addr The address to be converted.
     * @return The converted string presentation.
     */ 
    public static String addrToStr(URL addr) throws MalformedURLException {
        if(addr == null){
            throw new MalformedURLException("sonera.net.URL.addrToStr(): input address cannot be null");
        }
        if(addr.getHost() != null){
            String ret;
            if(addr.getProtocol() != null) {
                if(addr.getProtocol().equals("http") || addr.getProtocol().equals("https")){
                    ret = addr.getProtocol() + "://"+addr.getHost();                    
                } else {
                    throw new MalformedURLException("This is not http address:"+addr);                    
                }
            } else {
                ret = "http://";
            }
            
            if(addr.getPort() > 0){
                ret+=":"+addr.getPort();
            }
            if(addr.getFile() != null && !addr.getFile().equals("")){
                if(addr.getFile().trim().indexOf("/") != 0){                
                    ret+="/"+addr.getFile();
                } else {
                    ret+= addr.getFile();
                }
            }
            if(addr.getRef() != null && !addr.getRef().equals("")){
                ret+="#"+addr.getRef();
            }
            return ret;
        } else {
            throw new MalformedURLException("This is not http address:"+addr);
        }
         
    }
    
    public String toString(){
        try {
            return addrToStr(this);
        } catch (Exception e){
            return null;
        }
    }
}
