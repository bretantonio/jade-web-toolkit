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
import javax.net.*;
import java.net.*;
import java.io.*;

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
    public cascom.net.Socket createSocket(String host, int port, boolean timeouts) throws IOException {
        // timeouts is only for MIDP
        cascom.net.Socket s = new cascom.net.Socket(new java.net.Socket(host,port));
        if(!timeouts){
            try {
            s.setSoTimeout(0);
            } catch (Exception e){ e.printStackTrace();}
        }
        return s;
    }
    public Socket createSocket(String host, int port, String dest, int outport, boolean timeouts) throws IOException {
        cascom.net.Socket s = new cascom.net.Socket(new java.net.Socket(host,port, java.net.InetAddress.getByName(dest), outport));
        if(!timeouts){
            try {
            s.setSoTimeout(0);
            } catch (Exception e){  e.printStackTrace(); }
        }
        return s;
    }
    public static SocketFactory getDefault(){
        return sf;
    }
    
}
