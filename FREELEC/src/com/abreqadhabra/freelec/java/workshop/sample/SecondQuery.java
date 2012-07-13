package com.abreqadhabra.freelec.java.workshop.sample;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.ResultSet;

import java.math.BigDecimal;
import java.sql.Date;

public class SecondQuery {
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
		
		BigDecimal threshold = new BigDecimal(40.00) ;
		
		SQLWarning swarn = null ;
		Statement stmt = con.createStatement() ;
		ResultSet rs = stmt.executeQuery(qry) ;
		
		while (rs.next()) {
			
			itemNumber = rs.getInt("itemNumber") ;
			price = rs.getBigDecimal("price") ;
			stockDate = rs.getDate("stockDate") ;
			description = rs.getString("description") ;
			
			swarn = rs.getWarnings() ;
			
			if(swarn != null){
				printSQLWarning(swarn) ;
			}else{
				if((itemNumber < 6)&&(price.compareTo(threshold) > 0)){
				    System.out.println("Item Number: " + itemNumber) ;
				    System.out.println("Item Price:  " + price) ;
				    System.out.println("Stock Date:  " + stockDate) ;
				    System.out.println("Description: " + description + '\n') ;
				}
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
