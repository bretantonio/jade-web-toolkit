package com.abreqadhabra.freelec.java.workshop.sample;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.ResultSet;

public class FirstQuery {
	private static final String driver = "org.apache.derby.jdbc.EmbeddedDriver" ;
	private static final String url = "jdbc:derby:test" ;
	private static final String qry = 
		"SELECT itemNumber, price, stockDate, description FROM bigdog.products" ;	
	
	static void printSQLException(SQLException se) {
		while(se != null) {

			System.out.print("SQLException: State:   " + se.getSQLState());
			System.out.println("Severity: " + se.getErrorCode());
			System.out.println(se.getMessage());			
			
			se = se.getNextException();
		}
	}
		
	static void printSQLWarning(SQLWarning sw) {
		while(sw != null) {

			System.out.print("SQLWarning: State=" + sw.getSQLState()) ;
			System.out.println(", Severity = " + sw.getErrorCode()) ;
			System.out.println(sw.getMessage()); 
			
			sw = sw.getNextWarning();
		}
	}
	
	static void doQuery(Connection con) throws SQLException {
		
		SQLWarning swarn = null ;
			
		Statement stmt = con.createStatement() ;
		
		ResultSet rs = stmt.executeQuery(qry) ;
		
		while (rs.next()) {
			
    		System.out.println("Item Number: " + rs.getString("itemNumber")) ;
	    	System.out.println("Item Price:  " + rs.getString("price")) ;
			System.out.println("Stock Date:  " + rs.getString("stockDate")) ;
			System.out.println("Description: " + rs.getString("description") + '\n') ;
			
			swarn = rs.getWarnings() ;
			
			if(swarn != null){
				printSQLWarning(swarn) ;
			}
		}
		rs.close() ;
		stmt.close() ;
	}

	public static void main(String[] args) {
		Connection con = null ;

		try {
			Class.forName(driver) ;
			con = DriverManager.getConnection(url);

			SQLWarning swarn = con.getWarnings() ;
						
			if(swarn != null){
				printSQLWarning(swarn) ;
			}
			
			doQuery(con) ;
			
		} catch (SQLException se) {
			printSQLException(se) ;
		} catch(ClassNotFoundException e){
			System.out.println("JDBC Driver " + driver + " not found in CLASSPATH") ;
		}
		finally {
			if(con != null){
				try{
					con.close() ;
				} catch(SQLException se){
					printSQLException(se) ;
				}
			}
		}
	}
}
