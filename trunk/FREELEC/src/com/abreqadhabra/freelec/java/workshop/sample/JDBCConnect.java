package com.abreqadhabra.freelec.java.workshop.sample;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCConnect {
	private static final String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	private static final String url = "jdbc:derby:test;create=true";

	public static void main(String[] args) {
		Connection con = null;
		DatabaseMetaData dbmd = null;

		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url);
			System.out.println(con.toString());
			// Use the database connection somehow.
			
	
			dbmd = con.getMetaData() ;

			System.out.println("\n----------------------------------------------------") ;
			System.out.println("Database Name    = " + dbmd.getDatabaseProductName()) ;
			System.out.println("Database Version = " + dbmd.getDatabaseProductVersion()) ;
			System.out.println("Driver Name      = " + dbmd.getDriverName()) ;
			System.out.println("Driver Version   = " + dbmd.getDriverVersion()) ;
			System.out.println("Database URL     = " + dbmd.getURL()) ;
			System.out.println("----------------------------------------------------") ;


		} catch (SQLException se) {
			printSQLException(se);
		} catch (ClassNotFoundException e) {
			System.out.println("JDBC Driver " + driver
					+ " not found in CLASSPATH");
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException se) {
					printSQLException(se);
				}
			}
		}
	}
	
	private static void printSQLException(SQLException se) {
		while (se != null) {

			System.out.print("SQLException: State:   " + se.getSQLState());
			System.out.println("Severity: " + se.getErrorCode());
			System.out.println(se.getMessage());

			se = se.getNextException();
		}
	}
}
