package com.abreqadhabra.freelec.java.workshop.addressbook.test;

import com.abreqadhabra.freelec.java.workshop.addressbook.server.javadb.JavaDBServerThread;

public class JavaDBThreadTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Thread t = new Thread(new JavaDBServerThread());
		        t.start();

	}

}
