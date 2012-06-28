package com.abreqadhabra.freelec.java.workshop.addressbook.javadb;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.abreqadhabra.freelec.java.workshop.addressbook.common.constants.Constants;

public class JavaDBControl {
	public static void checkAndCreateSchema(Connection connection){
		Statement statement = null;
		ResultSet resultset = null;
		try {
			statement = connection.createStatement();
			resultset = statement.executeQuery("select SCHEMA-NAME from sys.SYSSCHEMAS  where SCHEMA-NAME = '"+Constants.DERBY_DATABASE.STRING_DB_SCHEMA_NAME+"'");
			if(resultset.next()){
				System.out.println("Schema '"+Constants.DERBY_DATABASE.STRING_DB_SCHEMA_NAME+"' already exists; no need to create schema again.");
			}else{
				System.out.println("Schema '"+Constants.DERBY_DATABASE.STRING_DB_SCHEMA_NAME+"' 가 존재하지 않습니다.");
			}
				
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
