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
import cascom.net.URL;
import javax.microedition.io.Connector;
import javax.microedition.io.SocketConnection;
import java.io.IOException;

/**
 * Subset of functionality of class java.net.InetAddress for MIDP.
 *
 * @author Ahti Syreeni - TeliaSonera
 */
public class InetAddress  {
    /**
	Sets the test host to be used.	
     */
    private static String testHost = null;
    public static void setTesthost(String address){
	testHost = address;
    }


    /**
     * Returns the name of the device (not full network name even if
     * such exist!).
     */
    public static String getLocalHostName()  {
        if(System.getProperty("microedition.hostname") != null &&
                !System.getProperty("microedition.hostname").equals("")){
            return System.getProperty("microedition.hostname");
        } else {
            return  null;
        }
        
    }
    
    /**
     *  Returns the IP address of localhost. This is done by creating
     *  SocketConnection to address www.sun.com:80 and taken the address
     *  with getLocalAddress() method. If the IP address is not available,
     *  value 127.0.0.1 will be returned.
     */
    public static String getLocalHostAddress()  {
        String localAddress = "127.0.0.1";
	/*
        SocketConnection con;
        // try to get the IP address, this is a messy way.
        // FIXME: find better way when it is possible
		String testAddress = "www.sun.com:80";
		if(testHost != null){
		  	testAddress = testHost;
		}

        	try {
            		con = (SocketConnection) Connector.open("socket://"+testAddress);
            		localAddress = con.getLocalAddress();
            		con.close();
        	} catch (Exception e){}
	*/
        return localAddress;
    }
    
    /**
     * Returns the local name of computer (using getLocalHostName() -method) or if
     * it is not available, return the value returned by getLocalHostAddress()-method.
     */
    public static String getLocalHost() {
        String temp = getLocalHostAddress();
        if(temp.equals("127.0.0.1") && getLocalHostName() != null){
            return getLocalHostName();
        } else {
            return temp;
        }
    }
}
