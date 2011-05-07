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

/**
 * Class for storing Sockets, used for persistent connections.
 *
 * @author Jose Antonio Exposito
 * @author MARISM-A Development group ( marisma-info@ccd.uab.es )
 * @version 0.1
 * @author Nicolas Lhuillier (Motorola Labs)
 * @version 1.0
 * @author Ahti Syreeni - TeliaSonera
 * @version 1.1
 */


package jade.mtp.http2;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Vector;
import cascom.net.Socket;
import cascom.net.URL;

import jade.util.Logger;
import java.util.Enumeration;
import cascom.fipa.util.BufferedInputStream;
import cascom.fipa.util.BufferedOutputStream;


/**
 * This class represents a connection to a remote server
 */
public class KeepAlive {
    private static Logger logger = Logger.getMyLogger("KeepAlive");
    
    /**
     * Inner structure to contain all connection information
     */
    public static class KAConnection {
        private Socket         socket;
        private OutputStream   out;
        private BufferedInputStream    in;
        private HTTPAddress    address;
        long lastUsed = 0;
        boolean inUse = false;
        //private Vector         connections;
        
        public KAConnection(HTTPAddress a){
            address = a;
        }
        
        public KAConnection(Socket s, HTTPAddress a) throws IOException {
            address = a;
            this.setSocket(s);
        }
        
        public void setSocket(Socket s) throws IOException {
            socket = s;
            out = new BufferedOutputStream(socket.getOutputStream());
            in =  new BufferedInputStream(socket.getInputStream());
            //in.setNextEOS(1);
            if(in == null){
                System.err.println("Input was null, from Socket!!");
            }
            if(out == null){
                System.err.println("Output was null, from Socket!!");
            }
        }
        
        public synchronized OutputStream getOut() {
            return out;
        }
        
        public synchronized void setOut(OutputStream out) {
            this.out = out;
        }
        
        
        public synchronized BufferedInputStream getIn() {
            return in;
        }
        
        public synchronized HTTPAddress getAddress() {
            return address;
        }
        
        public synchronized boolean equals(HTTPAddress a) {
            return address.equals(a);
        }
        
        
        public synchronized void close() {
            try {
                in.close();
            } catch(IOException ioe) {
                /*
                if(logger.isLoggable(Logger.WARNING))
                    logger.log(Logger.WARNING,"Exception while closing KA connection: "+ioe);
                 **/
            }
            
            try {
                out.close();
            } catch(IOException ioe2) {
                /*
                if(logger.isLoggable(Logger.WARNING))
                    logger.log(Logger.WARNING,"Exception while closing KA connection: "+ioe2);
                 **/
            }
            
            try {
                socket.close();
            } catch(Exception ioe3) {
                /*
                if(logger.isLoggable(Logger.WARNING))
                    logger.log(Logger.WARNING,"Exception while closing KA connection: "+ioe3);
                 */
            
            }
        }
        /*
        protected void finalize(){
            this.close();
        }
        */
        public synchronized long getTimeStamp(){
            if(!this.inUse){
                return this.lastUsed;
            } else {
                // this socket is currently used
                return System.currentTimeMillis();
            }
        }
        
        public synchronized void setTimeStamp(){
            this.lastUsed = System.currentTimeMillis();
        }        
        
        public synchronized void setBusy(boolean busy){
            this.inUse = busy;
        }               
    } // End of KAConnection inner class
    
    private Vector connections;
    private int    dim;
    
    /** Constructor */
    public  KeepAlive(int dim) {
        connections = new Vector(dim);
        this.dim = dim;
    }
    
    /** add a new connection */
    public synchronized void add(KAConnection c) {
        try {
            //The vectors are full.
            if (connections.size() == dim) {
                remove(0); //Remove the first element of vectors, is the older element
            }
            connections.addElement(c);
            //System.out.println("DEBUG: Added Ka conn: "+connections.size()+"/"+dim+" with "+c.getAddress().getPortNo());
        } catch(Exception ioe) {
            if(logger.isLoggable(Logger.WARNING))
                logger.log(Logger.WARNING,ioe.getMessage());
        }
    }
    
    /** delete an exisiting connection, based on position */
    private void remove(int pos) {
        try {
            KAConnection old = getConnection(pos);
            connections.removeElementAt(pos);
            old.close();
        } catch(Exception ioe) {
            if(logger.isLoggable(Logger.WARNING))
                logger.log(Logger.WARNING,ioe.getMessage());
        }
    }
    
    /** delete an exisiting connection, based on its address */
    public synchronized void remove(HTTPAddress addr) {
        connections.removeElement(search(addr));
    }
    
    /**
     *    Close and remove all connections.
     */
    public synchronized void closeConnections(){
        Enumeration elements = connections.elements();
        KeepAlive.KAConnection con;
        while(elements.hasMoreElements()){
            con = (KeepAlive.KAConnection)elements.nextElement();
            try {
                con.close();
            } catch(Exception e){}
        }
        this.connections.removeAllElements();
    }
    
    /** delete an exisiting connection*/
    public synchronized void remove(KAConnection ka) {
        connections.removeElement(ka);
    }
    
    
    
    /** get the socket of the connection when addr make matching */
    private KAConnection getConnection(int pos) {
        return (KAConnection)connections.elementAt(pos);
    }
    
    private KAConnection search(HTTPAddress addr) {
        if (addr != null) {
            KAConnection c;
            for(int i=(connections.size()-1); i >= 0; i--) {
                if ((c=(KAConnection)getConnection(i)).equals(addr)) {
                    return c;
                }
            }
        }
        return null;
    }
    
    /** get the socket of the connection when addr make matching */
    public synchronized KAConnection getConnection(HTTPAddress addr) {
        return search(addr);
    }
    
    /** get the dimension of Vectors */
    public synchronized int getDim(){
        return dim;
    }
    
    public synchronized Enumeration elements(){
        return this.connections.elements();
    }
    
    
    /** get the capacity of Vectors */
    public synchronized int capacity() {
        //System.out.println("DIMENSION: "+dim+"  "+"TAMVECT: "+addresses.size());
        return (dim - connections.size());
    }
    
    /**
     * Returns current size
     */
    public synchronized int size(){
        return connections.size();
    }
    
    /** Search the last pos of addr in the connection vector*/
  /*
    public int search(String addr) {
    int pos = -1;
    if (addr != null) {
    for(int i= (connections.size()-1); i >= 0; i--) {
    if (addr.equals(getAddress(i))) {
    pos = i;
    break;
    }
    }
    }
    return pos;
    }
   */
    
    public synchronized void swap(KAConnection c) {
        try {
            //if only have 1 socket isn't necessary make swap function
            if ((dim > 1)&&(!(connections.indexOf(c)==(connections.size()-1)))) {
                //remove the elements at former position
                connections.removeElement(c);
                //put the elements at the end
                connections.addElement(c);
            }
        } catch(Exception ioe) {
            if(logger.isLoggable(Logger.WARNING))
                logger.log(Logger.WARNING,ioe.getMessage());
        }
    }
    
} //End of class KeepAlive
