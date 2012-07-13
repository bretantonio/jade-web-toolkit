package com.abreqadhabra.freelec.java.workshop.sample;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import java.math.BigDecimal;
import java.sql.Date;

public class ThirdQuery {
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
		int itemNumber = -1 ;
		BigDecimal price = null ;
		Date stockDate = null ;
		String description = null ;
		
		int numRows = 0 ;
		String line = "------------------------------------" ;
		BigDecimal threshold = new BigDecimal(40.00) ;
		
		SQLWarning swarn = null ;
		Statement stmt = con.createStatement() ;
		ResultSet rs = stmt.executeQuery(qry) ;
		ResultSetMetaData rsmd = rs.getMetaData() ;
		
		System.out.printf("%-11s|", rsmd.getColumnName(1)) ;
		System.out.printf("%-8s|", rsmd.getColumnName(2)) ;
		System.out.printf("%-10s|", rsmd.getColumnName(3)) ;
		System.out.printf("%-40s\n", rsmd.getColumnName(4)) ;
		
		System.out.println(line + line);

		while (rs.next()) {
			
			itemNumber = rs.getInt("itemNumber") ;
			price = rs.getBigDecimal("price") ;
			stockDate = rs.getDate("stockDate") ;
			description = rs.getString("description") ;
			
			swarn = rs.getWarnings() ;
			
			if(swarn != null){
				printSQLWarning(swarn) ;
			}else{
				numRows ++ ;
				System.out.printf("%-11s|", itemNumber) ;
				System.out.printf("%-8s|", price) ;
				System.out.printf("%-10s|", stockDate) ;
				System.out.printf("%-40s\n", description) ;
			}
		}
		
		System.out.println("\n" + numRows + " rows selected") ;
		
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
