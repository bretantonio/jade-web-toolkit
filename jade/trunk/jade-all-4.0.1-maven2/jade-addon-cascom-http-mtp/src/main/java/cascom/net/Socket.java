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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import javax.microedition.io.SocketConnection;
import javax.microedition.io.StreamConnection;

/**
 * Socket for J2ME and J2SE. Functionality is subset of java.net.Socket.
 *
 * @author Ahti Syreeni - TeliaSonera
 */

public class Socket {
    private SocketConnection sc;
    private StreamConnection stc;
            
    /** Creates a new instance of Socket */
    private Socket() {
        //System.err.println("Warning: sonera.net.Socket was initialized without connection");
    }
    public Socket (SocketConnection sc){
        this.sc = sc;
        this.stc = (StreamConnection) this.sc;
    }
    public Socket (StreamConnection stc){
        this.stc = stc;        
    }
    
    public void close() throws IOException {
        if(this.sc != null) {
            this.sc.close();
            this.sc = null;
        }
        if(this.stc != null){
            this.stc.close();
            this.stc = null;
        }
    }

    
    public void setLinger(int time){
        if(this.sc != null){
            try {
                this.sc.setSocketOption(SocketConnection.LINGER,time);            
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    
    public InputStream getInputStream() throws IOException {       
        return this.stc.openInputStream();
    }
    public OutputStream getOutputStream() throws IOException {
        //System.err.println(Thread.currentThread().getName()+" calling getOutputStream()");
        return this.stc.openOutputStream();
    }
    public void setSoTimeout(int time){
        //System.err.println("Warning: You cannot set time-outs for sockets in J2ME. Timeouts are off by default in cascom.net.Socket.");
        /*
        try {
            this.sc.setSocketOption(SocketConnection.LINGER,time);
        } catch (Exception e){}
         */
    }    
    public String getLocalAddress() throws IOException {
        if(this.sc != null){
            return this.sc.getLocalAddress();
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

    public void setKeepAlive(boolean on) {
        try {
            if(on){
                this.sc.setSocketOption(SocketConnection.KEEPALIVE,2);
            } else {
                this.sc.setSocketOption(SocketConnection.KEEPALIVE,0);            
            }
        } catch (Exception e){
        }
    }
    
    
    public String getAddress() throws IOException {
        if(this.sc != null){
            return this.sc.getAddress();
        } else {
            return null;
        }
    }

}
