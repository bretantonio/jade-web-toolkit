/**

  자바 데이터베이스 서버 샘플 프로그램은 Derby Network Server와 상호작용 하는 간단한 JDBC 애플리케이션입니다.
  
 이 프로그램은 다음과 같은 동작을 합니다.
 
 
 
 1) Java DB의 구동환경을 설정합니다. 
 a Derby Network Server를 사용하기 위해 derby.drda.startNetworkServer 시스템 프로퍼티를 사용(true)으로 등록합니다. 
 b Java DB가 사용하는 시스템 디렉토리를 설정합니다. 만일 대상 디렉토리가 존재하지 않다면 새롭게 생성합니다.
 c JDBC드라이버를 로드합니다.
 
 2) Embedded용 JDBC 드라이버를 로드합니다.
 * 
 * 

 1.	Derby Network Server를 시작합니다.
 2.	클라이언트용 JDBC 드라이버를 적재합니다.
 3. creates the database if not already created
 4. checks to see if the schema is already created, and if not,
 5. creates the schema which includes the table SAMPLETBL and corresponding indexes.
 6. connects to the database
 7. loads the schema by inserting data
 8. starts client threads to perform database related operations
 9. has each of the clients perform DML operations (select, insert, delete, update) using JDBC calls,
    i)	 one client opens an embedded connection to perform database operations
         You can open an embedded connection in the same JVM that starts the Derby Network
         Server.
    ii)  one client opens a client connection to the Derby Network Server to perform database operations.
 10.waits for the client threads to finish the tasks
 11.shuts down the Derby Network Server at the end of the demo

 <P>
 Usage: java nserverdemo.NsSample
 <P>
 Please note, a file derby.log is created in the directory you run this program.
 This file contains the logging of connections made with the derby network server
 */

package com.abreqadhabra.freelec.java.workshop.addressbook.javadb;

import java.io.File;
import java.util.logging.Level;

import com.abreqadhabra.freelec.java.workshop.addressbook.common.constants.Constants;

public class JavaDBServer {

	public JavaDBServer() {

		initJavaDBEnviroments();

	}

	private void initJavaDBEnviroments() {
		// Derby Network Server를 사용하기 위해 derby.drda.startNetworkServer 시스템 프로퍼티를 사용(true)으로 등록합니다.
		System.setProperty("derby.drda.startNetworkServer", "true");
		//Java DB가 사용하는 시스템 디렉토리를 설정합니다. 만일 대상 디렉토리가 존재하지 않다면 새롭게 생성합니다.
		setJavaDBSystemDirectory();
		//JDBC드라이버를 로드합니다.
		loadJDBCDriver();
	}
	
	public void setJavaDBSystemDirectory() {
		// decide on the db system directory
		String userHomeDir = System.getProperty("user.home", ".");
		String systemDir = userHomeDir + "/."
				+ Constants.DERBY_DATABASE.STRING_DB_NAME;
		System.setProperty("derby.system.home", systemDir);
		// create the db system directory
		File fileSystemDir = new File(systemDir);
		fileSystemDir.mkdir();
	}

	public void loadJDBCDriver() {
		// Booting derby

		// load Derby driver
		try {
			Class.forName(
					Constants.DERBY_DATABASE.STRING_DERBY_EMBEDED_DRIVER_NAME)
					.newInstance();
			// Get a connection
		} catch (ClassNotFoundException cnfe) {
			System.err
					.println("\nUnable to load the JDBC driver "
							+ Constants.DERBY_DATABASE.STRING_DERBY_EMBEDED_DRIVER_NAME);
			System.err.println("Please check your CLASSPATH.");
			cnfe.printStackTrace(System.err);
		} catch (InstantiationException ie) {
			System.err
					.println("\nUnable to instantiate the JDBC driver "
							+ Constants.DERBY_DATABASE.STRING_DERBY_EMBEDED_DRIVER_NAME);
			ie.printStackTrace(System.err);
		} catch (IllegalAccessException iae) {
			System.err
					.println("\nNot allowed to access the JDBC driver "
							+ Constants.DERBY_DATABASE.STRING_DERBY_EMBEDED_DRIVER_NAME);
			iae.printStackTrace(System.err);
		}
	}
	
	public static void main(String[] args) {
		JavaDBServer dbServer = new JavaDBServer();
	}

}
