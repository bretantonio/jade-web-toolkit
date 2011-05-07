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
 * Functionality of ServerSocket for J2SE and MIDP. 
 *
 * @author Ahti Syreeni - TeliaSonera
 */
public class ServerSocket {
    java.net.ServerSocket socket;
    public ServerSocket() throws IOException{
        this.socket = new java.net.ServerSocket();
    }
    public ServerSocket(java.net.ServerSocket socket){
        this.socket = socket;
    }
    
    public cascom.net.Socket accept() throws IOException {
        return new cascom.net.Socket(this.socket.accept());
    }
    
    public void close(){
        try {
            this.socket.close();
            this.socket = null;
        } catch (IOException io){
            
        }
    }
    
    public int getLocalPort(){
        return this.socket.getLocalPort();
    }
    
    protected void finalize(){
        if(this.socket != null){
            try {
                this.socket.close();
            } catch (Exception e){}
        }
    }
}
