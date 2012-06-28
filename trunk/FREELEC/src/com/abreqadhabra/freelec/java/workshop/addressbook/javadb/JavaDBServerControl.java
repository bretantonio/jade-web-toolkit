package com.abreqadhabra.freelec.java.workshop.addressbook.javadb;

import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.derby.drda.NetworkServerControl;

public class JavaDBServerControl {

	// 로그 출력을 위한 선언
	Logger logger = Logger.getLogger(this.getClass().getCanonicalName());
	
	// Server instance for testing connection
	NetworkServerControl networkServerControl = null;
	
	PrintWriter pw = new PrintWriter(System.out,true);	// to print messages

	
public JavaDBServerControl(){
	
}

public JavaDBServerControl(int port) {
	try {
		networkServerControl = new
			  NetworkServerControl(InetAddress.getByName("localhost"), port);
	} catch (UnknownHostException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	logger.log(Level.INFO, "Derby Network Server 가 생성되었습니다.");
}

	/**
	 * Start Derby Network server
	 * @throws Exception 
	 * 
	 */
	public void start() throws Exception {
			networkServerControl.start(pw);
			logger.log(Level.INFO, networkServerControl.getSysinfo());

		logger.log(Level.INFO, "Derby Network Server 가 시작되었습니다.");
	}
	
    /**
     * Shutdown the NetworkServer
     * @throws Exception 
     */
    public void shutdown() throws Exception {
        	networkServerControl.shutdown();

    }
    
	/**
	 * trace utility of server
	 * @throws Exception 
	 */
	public void trace(boolean onoff) throws Exception {
			networkServerControl.trace(onoff);

	}

	/**
	 * Try to test for a connection Throws exception if unable to get a
	 * connection
	 * @throws Exception 
	 */
	public void testForConnection() throws Exception  {

			networkServerControl.ping();

	}

}
