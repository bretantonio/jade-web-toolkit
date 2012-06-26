package com.abreqadhabra.freelec.java.workshop.addressbook.common.constants;

public class Constants {

	public static final class DERBY_DATABASE {

		public static final String STRING_FRAMEWORK = "embedded";
		public static final String STRING_DERBY_EMBEDED_DRIVER_NAME = "org.apache.derby.jdbc.EmbeddedDriver";
		public static final String STRING_DERBY_CLIENT_DRIVER_NAME = "org.apache.derby.jdbc.ClientDriver";
		// network server control specific
		public static final int STRING_DERBY_NETWORK_SERVER_PORT =1621;
		
		// Derby Connect URL
		public static final String STRING_PROTOOL = "jdbc:derby:";
		public static final String STRING_DB_USER = "freelec";
		public static final String STRING_DB_PASSWORD = "freelec!@#123";
		public static final String STRING_DB_NAME = "FREELEC";

	}

	public static final class DERBY_ADDRESS_DAO {

		public static final String STR_FRAMEWORK = "embedded";
		public static final String STR_DRIVER_NAME = "org.apache.derby.jdbc.EmbeddedDriver";

	    public static final String STR_SQL_CREATE_ADDRESS_TABLE =
	            "create table FREELEC.ADDRESS (" +
	            "    ID          INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
	            "    LASTNAME    VARCHAR(30), " +
	            "    FIRSTNAME   VARCHAR(30), " +
	            "    MIDDLENAME  VARCHAR(30), " +
	            "    PHONE       VARCHAR(20), " +
	            "    EMAIL       VARCHAR(30), " +
	            "    ADDRESS1    VARCHAR(30), " +
	            "    ADDRESS2    VARCHAR(30), " +
	            "    CITY        VARCHAR(30), " +
	            "    STATE       VARCHAR(30), " +
	            "    POSTALCODE  VARCHAR(20), " +
	            "    COUNTRY     VARCHAR(30) " +
	            ")";
	    
		public static final String STR_SQL_SAVE_ADDRESS = "INSERT INTO FREELEC.ADDRESS "
				+ "   (LASTNAME, FIRSTNAME, MIDDLENAME, PHONE, EMAIL, ADDRESS1, ADDRESS2, "
				+ "    CITY, STATE, POSTALCODE, COUNTRY) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		public static final String STR_GET_LIST_ENTRIES =
	            "SELECT ID, LASTNAME, FIRSTNAME, MIDDLENAME FROM FREELEC.ADDRESS "  +
	            "ORDER BY LASTNAME ASC";
	    
	    public static final String STR_SQL_GET_ADDRESS_LIST = "SELECT ID, LASTNAME, FIRSTNAME, MIDDLENAME, PHONE, EMAIL, ADDRESS1, ADDRESS2, CITY, STATE, POSTALCODE, COUNTRY FROM FREELEC.ADDRESS "
				+ "ORDER BY LASTNAME ASC";
		
		public static final String STR_GET_ADDRESS =
	            "SELECT * FROM FREELEC.ADDRESS " +
	            "WHERE ID = ?";
		
		public static final String STR_EDIT_ADDRESS =
	            "UPDATE FREELEC.ADDRESS " +
	            "SET LASTNAME = ?, " +
	            "    FIRSTNAME = ?, " +
	            "    MIDDLENAME = ?, " +
	            "    PHONE = ?, " +
	            "    EMAIL = ?, " +
	            "    ADDRESS1 = ?, " +
	            "    ADDRESS2 = ?, " +
	            "    CITY = ?, " +
	            "    STATE = ?, " +
	            "    POSTALCODE = ?, " +
	            "    COUNTRY = ? " +
	            "WHERE ID = ?";
	    
		public static final String STR_REMOVE_ADDRESS =
	            "DELETE FROM FREELEC.ADDRESS " +
	            "WHERE ID = ?";
	    
	}
}
