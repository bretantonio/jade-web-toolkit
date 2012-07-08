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


일반기능
로그를 출력합니다.

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

package com.abreqadhabra.freelec.java.workshop.addressbook.server.db;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.derby.drda.NetworkServerControl;

import com.abreqadhabra.freelec.java.workshop.addressbook.common.constants.Constants;
import com.abreqadhabra.freelec.java.workshop.addressbook.server.db.common.JavaDBUtil;

public class JavaDBServer {

	// 로그 출력을 위한 선언
	Logger logger = Logger.getLogger(this.getClass().getCanonicalName());

	JavaDBServerControl serverControl = null;

			
	Connection dbConnection =null;
	
	// 생성자
	public JavaDBServer() {

		// 로거 초기화
		initLogger();
		// 네트워크서버 환경 초기화
		initNetworkServerEnviroments();

	}

	// 로거 초기화
	private void initLogger() {

		Handler handler = null;
		try {
			handler = new FileHandler(this.getClass().getName() + ".log");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Logger.getLogger("").addHandler(handler);

	}

	// 네트워크서버 환경 초기화
	private void initNetworkServerEnviroments() {
		logger.log(
				Level.INFO,
				"Derby Network Server를 사용하기 위해 derby.drda.startNetworkServer 시스템 프로퍼티를 사용(true)으로 등록합니다.");
		System.setProperty("derby.drda.startNetworkServer", "true");
		System.setProperty("derby.drda.logConnections", "true");

		logger.log(Level.INFO,
				"Derby Network Server가 사용하는 시스템 디렉토리를 설정합니다. 만일 대상 디렉토리가 존재하지 않다면 새롭게 생성합니다.");
		setDBSystemDirectory();
		logger.log(Level.INFO, "JDBC드라이버를 로드합니다.");
		JavaDBUtil.loadJDBCDriver();
	}

	// DB 시스템 디렉토리 설정
	private void setDBSystemDirectory() {
		// decide on the db system directory
		String userHomeDirectory = System.getProperty("user.home", ".");
		String systemDirectory = userHomeDirectory + "/."
				+ Constants.DERBY_DATABASE.STRING_DB_SCHEMA_NAME;
		System.setProperty("derby.system.home", systemDirectory);
		// create the db system directory
		File fileSystemDir = new File(systemDirectory);
		fileSystemDir.mkdir();
	}

	// 데이터베이스 환경 초기화
	private void initDatabaseEnviroments() {
		// DB프로퍼티설정
		Properties dbProperties = getDBProperties();
		String url = getDatabaseUrl();

		dbConnection = serverControl.getConnection(dbProperties);
		serverControl.testConnection(dbConnection);
	}

	private Properties getDBProperties() {
		Properties properties = new Properties();
		//데이터베이스 FREELEC에서 사용되는 사용자와 패스워드 설정
		properties.put("user", Constants.DERBY_DATABASE.STRING_DB_USER);
		properties.put("password", Constants.DERBY_DATABASE.STRING_DB_PASSWORD);
		logger.log(Level.INFO, "dbProperties" + properties);
		// providing a user name and password is optional in the embedded
		// and derbyclient frameworks
		return properties;
	}
	
	public String getDatabaseUrl() {
		String dbUrl = Constants.DERBY_DATABASE.STRING_PROTOOL
				+ Constants.DERBY_DATABASE.STRING_DB_SCHEMA_NAME;
		return dbUrl;
	}

	public static void main(String[] args) {
		JavaDBServer dbServer = new JavaDBServer();
		try {
			dbServer.start();
			// 데이터베이스 환경 초기화
			dbServer.initDatabaseEnviroments();
			
			//dbServer.waitForConnection();
			//dbServer.shutdown();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void shutdown() throws Exception {
		serverControl.shutdown();
	}

	private void start() throws Exception {
		serverControl = new JavaDBServerControl(1621);
		serverControl.start();
		
		//serverControl.testForConnection();
		//serverControl.logConnection(true);동작불능?
		//serverControl.trace(true);
	}
	

	
	/**
	 * Tries to check if the Network Server is up and running by calling ping If
	 * successful, then it returns else tries for 50 seconds before giving up
	 * and throwing an exception.
	 * 
	 * @throws Exception
	 *             when there is a problem with testing if the Network Server is
	 *             up and running
	 */
	private static void waitForConnection() throws Exception {

		// Server instance for testing connection
		NetworkServerControl server = null;

		// Use NetworkServerControl.ping() to wait for
		// NetworkServer to come up. We could have used
		// NetworkServerControl to start the server but the property is
		// easier.
		server = new NetworkServerControl();
		System.out.println(server.getSysinfo());
		System.out.println("Testing if Network Server is up and running!");
		int i = 0;
		// for (int i = 0; i < 10; i++) {
		while (true) {
			try {

				server.ping();
				server.logConnections(true);
				System.out.println(server.getRuntimeInfo());

				Thread.currentThread().sleep(5000);

				System.out.println("Derby Network Server now running");

			} catch (Exception e) {
				System.out.println("Try #" + i + " " + e.toString());
				if (i == 9) {
					System.out
							.println("Giving up trying to connect to Network Server!");
					e.printStackTrace();
				}
			}
		}

	}

}
