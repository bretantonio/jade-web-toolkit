package com.abreqadhabra.freelec.java.workshop.addressbook.common.constants;

public class Constants {

    public static final class DERBY_DATABASE {

	public static final String STRING_FRAMEWORK = "embedded";
	public static final String STRING_DERBY_EMBEDED_DRIVER_NAME = "org.apache.derby.jdbc.EmbeddedDriver";
	public static final String STRING_DERBY_CLIENT_DRIVER_NAME = "org.apache.derby.jdbc.ClientDriver";
	// network server control specific
	public static final int STRING_DERBY_NETWORK_SERVER_PORT = 1621;

	// Derby Connect URL
	public static final String STRING_PROTOOL = "jdbc:derby:";
	public static final String STRING_DB_USER = "freelec";
	public static final String STRING_DB_PASSWORD = "freelec!@#123";
	public static final String STRING_DATABASE_NAME = "FREELEC";
	public static final String STRING_DATASOURCE_NAME = "jdbc/datasource";

    }

    public static final class DERBY_ADDRESS_DAO {

	public static final String STR_FRAMEWORK = "embedded";
	public static final String STR_DRIVER_NAME = "org.apache.derby.jdbc.EmbeddedDriver";

	public static final String STR_SQL_CREATE_ADDRESS_TABLE = "create table FREELEC.ADDRESS ("
		+ "    ID          INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),"
		+ "    LASTNAME    VARCHAR(30), "
		+ "    FIRSTNAME   VARCHAR(30), "
		+ "    MIDDLENAME  VARCHAR(30), "
		+ "    PHONE       VARCHAR(20), "
		+ "    EMAIL       VARCHAR(30), "
		+ "    ADDRESS1    VARCHAR(30), "
		+ "    ADDRESS2    VARCHAR(30), "
		+ "    CITY        VARCHAR(30), "
		+ "    STATE       VARCHAR(30), "
		+ "    POSTALCODE  VARCHAR(20), "
		+ "    COUNTRY     VARCHAR(30) " + ")";

	public static final String STR_SQL_SAVE_ADDRESS = "INSERT INTO FREELEC.ADDRESS "
		+ "   (LASTNAME, FIRSTNAME, MIDDLENAME, PHONE, EMAIL, ADDRESS1, ADDRESS2, "
		+ "    CITY, STATE, POSTALCODE, COUNTRY) "
		+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	public static final String STR_GET_LIST_ENTRIES = "SELECT ID, LASTNAME, FIRSTNAME, MIDDLENAME FROM FREELEC.ADDRESS "
		+ "ORDER BY LASTNAME ASC";

	public static final String STR_SQL_GET_ADDRESS_LIST = "SELECT ID, LASTNAME, FIRSTNAME, MIDDLENAME, PHONE, EMAIL, ADDRESS1, ADDRESS2, CITY, STATE, POSTALCODE, COUNTRY FROM FREELEC.ADDRESS "
		+ "ORDER BY LASTNAME ASC";

	public static final String STR_GET_ADDRESS = "SELECT * FROM FREELEC.ADDRESS "
		+ "WHERE ID = ?";

	public static final String STR_EDIT_ADDRESS = "UPDATE FREELEC.ADDRESS "
		+ "SET LASTNAME = ?, " + "    FIRSTNAME = ?, "
		+ "    MIDDLENAME = ?, " + "    PHONE = ?, "
		+ "    EMAIL = ?, " + "    ADDRESS1 = ?, "
		+ "    ADDRESS2 = ?, " + "    CITY = ?, " + "    STATE = ?, "
		+ "    POSTALCODE = ?, " + "    COUNTRY = ? " + "WHERE ID = ?";

	public static final String STR_REMOVE_ADDRESS = "DELETE FROM FREELEC.ADDRESS "
		+ "WHERE ID = ?";

    }

    public static final class DERBY_flight_DAO {

	public static final String STR_SQL_CREATE_flight_TABLE = "CREATE TABLE FREELEC.flight ("
		+ "id INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
		+ "flight_number VARCHAR(10)  NOT NULL, "
		+ "origin_airport VARCHAR(10)  NOT NULL, "
		+ "destination_airport VARCHAR(10)  NOT NULL, "
		+ "carrier VARCHAR(20) NOT NULL, "
		+ "price INTEGER, "
		+ "departure TIMESTAMP, "
		+ "flight_type VARCHAR(8), "
		+ "available_seats INTEGER " 
		+ ")";

	public static final String STR_SQL_INSERT_DUMMY_DATA = "INSERT INTO FREELEC.flight ("
		+ "flight_number, origin_airport, destination_airport, carrier, price, departure, flight_type, available_seats"
		+ ") VALUES "
		+ "('OZ8921', '김포', '제주', '아시아나항공', 400, TIMESTAMP('2012-09-23 12:00:00'), '국내선', 50), "
		+ "('MU512', '김포', '상해(홍차오)', '중국동방항공', 2000, TIMESTAMP('2012-09-23 12:00:00'), '국제선', 22), "
		+ "('JL92', '김포', '도쿄/하네다', '일본항공', 100, TIMESTAMP('2012-09-23 12:05:00'), '국제선', 37), "
		+ "('7C111', '김포', '제주', '제주항공', 100, TIMESTAMP('2012-09-23 12:05:00'), '국내선', 0), "
		+ "('KE1335', '김포', '여수', '대한항공', 800, TIMESTAMP('2012-09-23 12:20:00'), '국내선', 14), "
		+ "('OZ8813', '김포', '김해(부산)', '아시아나항공', 800, TIMESTAMP('2012-09-28 12:30:00'), '국내선', 4), "
		+ "('BX8813', '김포', '김해(부산)', '에어부산', 700, TIMESTAMP('2012-09-28 12:30:00'), '국내선', 97), "
		+ "('OZ8923', '김포', '제주', '아시아나항공', 700, TIMESTAMP('2012-09-30 12:30:00'), '국내선', 75), "
		+ "('CZ318', '김포', '북경', '중국남방항공', 756, TIMESTAMP('2012-09-30 12:30:00'), '국제선', 43), "
		+ "('NH1162', '김포', '도쿄/하네다', '전일본공수', 756, TIMESTAMP('2012-09-30 12:40:00'), '국제선', 28), "
		+ "('JL972', '김포', '오사카(간사이)', '일본항공', 536, TIMESTAMP('2012-09-30 12:45:00'), '국제선', 78), "
		+ "('OZ8927', '김포', '제주', '아시아나항공', 536, TIMESTAMP('2012-09-30 12:45:00'), '국내선', 21), "
		+ "('OZ8929', '김포', '제주', '아시아나항공', 700, TIMESTAMP('2012-09-30 12:50:00'), '국내선', 120), "
		+ "('KE1609', '김포', '울산', '대한항공', 700, TIMESTAMP('2012-10-06 12:50:00'), '국내선', 99), "
		+ "('TW711', '김포', '제주', '티웨이항공', 800, TIMESTAMP('2012-10-06 12:55:00'), '국내선', 43), "
		+ "('KE1113', '김포', '김해(부산)', '대한항공', 800, TIMESTAMP('2012-10-06 13:00:00'), '국내선', 95), "
		+ "('ZE215', '김포', '제주', '이스타항공', 536, TIMESTAMP('2012-10-06 13:00:00'), '국내선', 5), "
		+ "('LJ335', '김포', '제주', '진에어', 536, TIMESTAMP('2012-10-06 13:10:00'), '국내선', 5), "
		+ "('BX8815', '김포', '김해(부산)', '에어부산', 756, TIMESTAMP('2012-10-11 13:30:00'), '국내선', 7), "
		+ "('OZ8815', '김포', '김해(부산)', '아시아나항공', 756, TIMESTAMP('2012-10-11 13:30:00'), '국내선', 11), "
		+ "('KE1611', '김포', '울산', '대한항공', 387, TIMESTAMP('2012-10-11 13:30:00'), '국내선', 50), "
		+ "('KE1229', '김포', '제주', '대한항공', 1645, TIMESTAMP('2012-10-14 13:40:00'), '국내선', 22), "
		+ "('261', '김포', '쑹산', '중화항공', 99, TIMESTAMP('2012-10-14 13:45:00'), '국제선', 7), "
		+ "('7C113', '김포', '제주', '제주항공', 99, TIMESTAMP('2012-10-14 13:45:00'), '국내선', 120), "
		+ "('OZ8931', '김포', '제주', '아시아나항공', 100, TIMESTAMP('2012-10-15 13:55:00'), '국내선', 99), "
		+ "('KE1231', '김포', '제주', '대한항공', 101, TIMESTAMP('2012-10-16 14:00:00'), '국내선', 43), "
		+ "('KE1115', '김포', '김해(부산)', '대한항공', 102, TIMESTAMP('2012-10-17 14:00:00'), '국내선', 95)";

	public static final String STR_SQL_GET_COLUMN_NAMES = "SELECT c.columnnumber, c.columnname FROM sys.systables t, sys.syscolumns c  WHERE t.tableid = c.referenceid AND t.tablename = 'flight' ORDER BY c.columnnumber";
	public static final String STR_SQL_GET_flight = "SELECT * FROM FREELEC.flight";

	public static final String STR_SQL_GET_RECORD_COUNT = "SELECT COUNT(*) cnt FROM FREELEC.flight";

	public static final String STR_SQL_GET_RECORD_BY_ID = "SELECT * FROM FREELEC.flight WHERE id = ?";

	public static final String STR_SQL_FIND_BY_FLIGHT_NUMBER = "SELECT * FROM FREELEC.flight WHERE flight_number = ?";
    }
}
