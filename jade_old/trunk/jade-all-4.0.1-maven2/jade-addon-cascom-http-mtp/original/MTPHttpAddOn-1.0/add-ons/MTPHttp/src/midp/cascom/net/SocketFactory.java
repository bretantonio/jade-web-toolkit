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

import javax.microedition.io.Connector;
import javax.microedition.io.SocketConnection; 
import cascom.net.URL;
import cascom.net.MalformedURLException;
import java.io.IOException;

/**
 * SocketFactory for J2ME and J2SE. Creates Socket -objects.
 *
 * @author Ahti Syreeni - TeliaSonera
 */
public class SocketFactory {
    private static SocketFactory sf = new SocketFactory();
    /** Creates a new instance of SocketFactory */
    private SocketFactory() {
    }
    
    public Socket createSocket(String host, int port, boolean timeouts) throws IOException {
        try {
            URL url = new URL(host);
            if(url.getHost() == null || url.getHost().equals("")){
                throw new IOException("sonera.net.ServerSocket.createSocket(): host was null or empty. Use ServerSocketFactory, if you need inbound connection.");
            }  
            
            SocketConnection sc = (SocketConnection) Connector.open("socket://"+url.getHost()+":"+port,Connector.READ_WRITE,timeouts);
            
            return new Socket(sc);                
            
        } catch (MalformedURLException e){
            System.err.println("Error: malformed URL at sonera.net.SocketFactory:"+host);
            return null;
        } catch (IllegalArgumentException ioe){
            ioe.printStackTrace();
            return null;
        }
    }
    
    public Socket createSocket(String host, int port, String dest, int outport, boolean timeouts) throws IOException {
        Socket sc = this.createSocket(host,port,timeouts);
        //System.err.println("Warning socket was requested for localaddress "+dest+":"+outport+" but was automatically connected to: "+sc.getLocalAddress()+":"+sc.getLocalPort());
        return sc;
    }
    public static SocketFactory getDefault(){
        return sf;
    }
}
