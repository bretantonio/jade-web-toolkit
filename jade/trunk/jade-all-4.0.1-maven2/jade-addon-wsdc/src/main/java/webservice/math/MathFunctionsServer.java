package webservice.math;

import java.net.ServerSocket;

import org.apache.axis.client.AdminClient;
import org.apache.axis.transport.http.SimpleAxisServer;
import org.apache.axis.utils.Options;

public class MathFunctionsServer {

	private static final int DEFAULT_PORT = 2000;
	private SimpleAxisServer sas;
	
	
	public MathFunctionsServer() {
		addShutdownHook();
	}

	public void start() throws Exception {
		start(DEFAULT_PORT, true);
	}

	public void start(int webserverPort, boolean daemon) throws Exception {
		// Create socket
		ServerSocket socket = new ServerSocket(webserverPort);
		
		// Create and start Axis Server
		sas = new SimpleAxisServer(); 
		sas.setServerSocket(socket);
		sas.start(daemon);
		
		// Deploy MathFunctionsService
		AdminClient ac = new AdminClient();
		Options options = new Options(new String[] { "-p", Integer.toString(webserverPort)});
		ac.process(options, getClass().getResourceAsStream("/webservice/math/deploy.wsdd"));
		
		System.out.println("MathFunctions server started");
		System.out.println("WSDL url: http://localhost:"+webserverPort+"/axis/services/MathFunctionsPort?wsdl");
	}

	public void stop() {
		sas.stop();
		System.out.println("MathFunctions server stopped");

	}
	
	protected void addShutdownHook() {
	    Runtime.getRuntime().addShutdownHook(new Thread() {
	        public void run() {
	        	sas.stop();
	        }
	      });
	}

	public static void main(String[] args) {
		try {
			MathFunctionsServer mfs = new MathFunctionsServer();
			mfs.start(DEFAULT_PORT, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
