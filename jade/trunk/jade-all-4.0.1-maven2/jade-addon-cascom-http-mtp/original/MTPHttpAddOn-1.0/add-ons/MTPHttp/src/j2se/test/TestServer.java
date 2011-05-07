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

package test;
import java.net.*;
import java.io.*;
import cascom.fipa.util.*;


/**
 * 
 * @author Ahti Syreeni - TeliaSonera
 */
public class TestServer {
    ServerSocket server;
    boolean active = true;
    
    /** Creates a new instance of TestServer */
    public TestServer(int port) {
        try {
            this.server = new java.net.ServerSocket(port);
            System.out.println("Loopback server activated");
            
            while(active) {  //Accept the input connections
                Socket client = server.accept();
                new ServerThread(client).start();
            }
        } catch( Exception e ) {e.printStackTrace();}
    }   
        
    public static class ServerThread extends Thread {
        boolean active = true;
        public ServerThread(Socket client){
            cascom.fipa.util.BufferedInputStream in = null;
            cascom.fipa.util.BufferedOutputStream out = null;
            //OutputStream out = null;
            
            try {
                    
                    in = new cascom.fipa.util.BufferedInputStream(client.getInputStream());
                    in.setNextEOS(1);
                    //in  = client.getInputStream();                                       
                    out = new cascom.fipa.util.BufferedOutputStream(client.getOutputStream());
                    //out = client.getOutputStream();
                    int read=0;
                while(active){
                    while(active){
                        read = in.read();
                        System.out.print((char)read);
                        out.write(read);
                        out.flush();
                    }                                
                }
            } catch (Exception e){
                if(in != null){
                    try {
                        in.close();
                    } catch (Exception io2){
                    }
                }
                
                if(out != null){
                    try{
                        out.close();
                    } catch (Exception ioe4){
                    }
                }
                try {
                    client.close();
                } catch (Exception e23){
                }
                e.printStackTrace();                
            }
        }
        
        
    }
            
    public static void main(String[] args){
        try {
            TestServer ts = new TestServer(Integer.parseInt(args[0]));
        } catch (Exception e){
            System.err.println("Could not start server, exception:"+e.getMessage());
        }
        
    }    
}
