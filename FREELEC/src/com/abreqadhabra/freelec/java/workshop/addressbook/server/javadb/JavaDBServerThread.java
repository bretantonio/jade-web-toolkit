package com.abreqadhabra.freelec.java.workshop.addressbook.server.javadb;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.abreqadhabra.freelec.java.workshop.addressbook.common.constants.Constants;
import com.abreqadhabra.freelec.java.workshop.addressbook.common.util.DateUtils;
import com.abreqadhabra.freelec.java.workshop.addressbook.server.javadb.common.JavaDBUtil;

public class JavaDBServerThread extends Thread {

	// 로그 출력을 위한 선언
	Logger logger = Logger.getLogger(this.getClass().getCanonicalName());

	JavaDBServerControl serverControl = null;

	Connection dbConnection = null;

	public void run() {
		JavaDBServerControl serverControl = new JavaDBServerControl(1527);
		try {
			serverControl.start();

			while (true) {

				logger.log(Level.INFO, DateUtils.now());
				serverControl.waitForConnection();
				JavaDBServerThread.sleep(5000);

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public JavaDBServerThread() {
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
		// 데이터베이스 FREELEC에서 사용되는 사용자와 패스워드 설정
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

}
