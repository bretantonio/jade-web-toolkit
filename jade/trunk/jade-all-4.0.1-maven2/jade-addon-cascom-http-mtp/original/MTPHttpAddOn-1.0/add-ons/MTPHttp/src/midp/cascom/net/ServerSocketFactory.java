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
import cascom.net.ServerSocket;
import javax.microedition.io.ServerSocketConnection;
import javax.microedition.io.Connector;
import java.io.IOException;

/**
 * ServerSocketFactory for J2ME and J2SE. Creates ServerSocket -objects.
 *
 * @author Ahti Syreeni - TeliaSonera
 */
public class ServerSocketFactory {
    private static ServerSocketFactory sf = new ServerSocketFactory();
    
    /** Creates a new instance of ServerSocketFactory */
    private ServerSocketFactory() {
    }
    

    /**
     * Creates ServerSocket. If port number < 1, let system get free port.
     */
    public ServerSocket createServerSocket(int port, boolean timeouts) throws IOException {
        try {
            if(port > 0){
                return new ServerSocket((ServerSocketConnection) Connector.open("socket://:"+port,Connector.READ_WRITE,timeouts));
            } else {
                // let the system allocate free port
                return new ServerSocket((ServerSocketConnection) Connector.open("socket://:",Connector.READ_WRITE,timeouts));                
            }
        } catch (IOException io){
            throw new IOException("Error creating ServerSocket:"+io.getMessage());
        }
        
    }
    
    public static ServerSocketFactory getDefault(){
        return sf;
    }
    
}
