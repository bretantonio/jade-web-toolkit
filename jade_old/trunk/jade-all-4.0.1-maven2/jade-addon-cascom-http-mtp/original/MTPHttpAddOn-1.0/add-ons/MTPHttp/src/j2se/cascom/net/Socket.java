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
import java.net.*;
import java.io.*;


/**
 * Socket for J2ME and J2SE. Functionality is subset of java.net.Socket.
 *
 * @author Ahti Syreeni - TeliaSonera
 */
public class Socket {
    private java.net.Socket sc;
       /** Creates a new instance of Socket */
    private Socket() {
        //System.err.println("Warning: sonera.net.Socket was initialized without actual socket");
    }
    public Socket (java.net.Socket sc){
        this.sc = sc;
        try {
            this.sc.setSoLinger(true,5);
        } catch (Exception io){
            io.printStackTrace();
        }
    }    
    
    public void close() throws IOException {
        this.sc.close();
        this.sc = null;
    }
    
    public InputStream getInputStream() throws IOException {        
        return this.sc.getInputStream();
    }
    
    public OutputStream getOutputStream() throws IOException {
        return this.sc.getOutputStream();
    }
    
    
    public void setSoTimeout(int time) throws SocketException {
        try {
            this.sc.setSoTimeout(time);
            
        } catch  (java.net.SocketException e){
            throw new cascom.net.SocketException(e.getMessage());
        }
    }    
    
    public String getLocalAddress() throws IOException {
        if(this.sc != null){
            return this.sc.getLocalAddress().toString();
        } else {
            return null;
        }
    }
    
    public String getLocalPort() throws IOException {
        if(this.sc != null){
            return ""+this.sc.getLocalPort();            
        } else {
            return null;
        }
    }
    
    public void setLinger(int time){
        try {
            sc.setSoLinger(true,time);
        } catch (java.net.SocketException e){}
    }

    public void setKeepAlive(boolean on){
        try {
        sc.setKeepAlive(on);
        } catch (java.net.SocketException e){}
    }
    
    public String getAddress() throws IOException {
        return this.sc.getInetAddress().getHostAddress();
    }

    // never leave socket open
    protected void finalize(){
        if(this.sc != null){
            try {
                this.sc.close();
            } catch (Exception e){}
        }
    }

}
