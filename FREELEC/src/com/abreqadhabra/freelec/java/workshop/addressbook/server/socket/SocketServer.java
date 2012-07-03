package com.abreqadhabra.freelec.java.workshop.addressbook.server.socket;

import java.io.IOException;
import java.net.ServerSocket;

public class SocketServer {

	// http://docs.oracle.com/javase/tutorial/networking/sockets/index.html
	// http://www.oracle.com/technetwork/java/socket-140484.html

	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = null;
		boolean listening = true;

		try {
			serverSocket = new ServerSocket(4444);
		} catch (IOException e) {
			System.err.println("Could not listen on port: 4444.");
			System.exit(-1);
		}

		while (listening) {
			
			new MultiServerThread(serverSocket.accept()).start();
		}

		serverSocket.close();
	}
}
