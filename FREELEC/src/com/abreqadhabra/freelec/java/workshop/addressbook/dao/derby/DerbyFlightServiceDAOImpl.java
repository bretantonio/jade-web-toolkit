package com.abreqadhabra.freelec.java.workshop.addressbook.dao.derby;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.abreqadhabra.freelec.java.workshop.addressbook.common.constants.Constants;
import com.abreqadhabra.freelec.java.workshop.addressbook.dao.FlightServiceDAO;
import com.abreqadhabra.freelec.java.workshop.addressbook.domain.DataInfo;
import com.abreqadhabra.freelec.java.workshop.addressbook.domain.FieldInfo;

public class DerbyFlightServiceDAOImpl implements FlightServiceDAO {

    Connection dbConnection = null;
    private PreparedStatement preStmtGetFieldInfo = null;
    private PreparedStatement preStmtGetRecordCount = null;
    private PreparedStatement preStmtGetRecordByID = null;
    private PreparedStatement preStmtFindByFlightNumber = null;

    public DerbyFlightServiceDAOImpl(Connection connection) {
	// initialization
	this.dbConnection = connection;
	try {
	    this.preStmtGetFieldInfo = dbConnection
		    .prepareStatement(Constants.DERBY_FLIGHTS_DAO.STR_SQL_GET_FLIGHTS);
	    this.preStmtGetRecordCount = dbConnection
	    .prepareStatement(Constants.DERBY_FLIGHTS_DAO.STR_SQL_GET_RECORD_COUNT);
	    this.preStmtGetRecordByID = dbConnection
	    .prepareStatement(Constants.DERBY_FLIGHTS_DAO.STR_SQL_GET_RECORD_BY_ID);
	    this.preStmtFindByFlightNumber = dbConnection
	    .prepareStatement(Constants.DERBY_FLIGHTS_DAO.STR_SQL_FIND_BY_FLIGHT_NUMBER);
	    
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }
    @Override
    public FieldInfo[] getFieldInfos() {
	FieldInfo[] fieldInfos = null;
	ResultSet result = null;
	try {

	    result = preStmtGetFieldInfo.executeQuery();

	    ResultSetMetaData metaData = result.getMetaData();
	    int columnCount = metaData.getColumnCount();
	    fieldInfos = new FieldInfo[columnCount];
	    for (int i = 0; i < columnCount; i++) {
		String columnName = metaData.getColumnName(i + 1);
		int columnLength = metaData.getColumnDisplaySize(i + 1);
		fieldInfos[i] = new FieldInfo(columnName, columnLength);
	    }

	} catch (SQLException sqle) {
	    sqle.printStackTrace();
	}

	return fieldInfos;
    }
    @Override
    public int getRecordCount() {
	int recordCount = 0;
	ResultSet result = null;
	try {

	    result = preStmtGetRecordCount.executeQuery();
	    while (result.next()) {
		recordCount = result.getInt("cnt");

	    }

	} catch (SQLException sqle) {
	    sqle.printStackTrace();
	}

	return recordCount;
    }
    @Override
    public DataInfo getRecord(int id) {

	FieldInfo[] fieldInfos = null;
	String values[] = null;
	ResultSet result = null;

	try {
	    preStmtGetRecordByID.clearParameters();
	    preStmtGetRecordByID.setInt(1, id);
	    result = preStmtGetRecordByID.executeQuery();
	    ResultSetMetaData metaData = result.getMetaData();
	    int columnCount = metaData.getColumnCount();
	    fieldInfos = new FieldInfo[columnCount];
	    values = new String[columnCount];
	    for (int i = 0; i < columnCount; i++) {
		String columnName = metaData.getColumnName(i + 1);
		int columnLength = metaData.getColumnDisplaySize(i + 1);
		fieldInfos[i] = new FieldInfo(columnName, columnLength);
	    }
	    while (result.next()) {
		String flightNumber = result.getString("flight_number");
		String originAirport = result.getString("origin_airport");
		String destinationAirport = result
			.getString("destination_airport");
		String carrier = result.getString("carrier");
		int price = result.getInt("price");
		Timestamp date = result.getTimestamp("date");
		String flightType = result.getString("flight_type");
		int availableSeats = result.getInt("available_seats");

		values[0] = new Integer(id).toString();
		values[1] = flightNumber;
		values[2] = originAirport;
		values[3] = destinationAirport;
		values[4] = carrier;
		values[5] = new Integer(price).toString();
		values[6] = date.toString();
		values[7] = flightType;
		values[8] = new Integer(availableSeats).toString();
	    }	    
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return new DataInfo(id, fieldInfos, values);

    }
    @Override
    public DataInfo findByFlightNumber(String flightNumber) {
	FieldInfo[] fieldInfos = null;
	String values[] = null;
	ResultSet result = null;
	int id = 0;
	try {
	    preStmtFindByFlightNumber.clearParameters();
	    preStmtFindByFlightNumber.setString(1, flightNumber);
	    result = preStmtFindByFlightNumber.executeQuery();
	    ResultSetMetaData metaData = result.getMetaData();
	    int columnCount = metaData.getColumnCount();
	    fieldInfos = new FieldInfo[columnCount];
	    values = new String[columnCount];
	    for (int i = 0; i < columnCount; i++) {
		String columnName = metaData.getColumnName(i + 1);
		int columnLength = metaData.getColumnDisplaySize(i + 1);
		fieldInfos[i] = new FieldInfo(columnName, columnLength);
	    }
	    while (result.next()) {
		id = result.getInt("id");
		String originAirport = result.getString("origin_airport");
		String destinationAirport = result
			.getString("destination_airport");
		String carrier = result.getString("carrier");
		int price = result.getInt("price");
		Timestamp date = result.getTimestamp("date");
		String flightType = result.getString("flight_type");
		int availableSeats = result.getInt("available_seats");

		values[0] = new Integer(id).toString();
		values[1] = flightNumber;
		values[2] = originAirport;
		values[3] = destinationAirport;
		values[4] = carrier;
		values[5] = new Integer(price).toString();
		values[6] = date.toString();
		values[7] = flightType;
		values[8] = new Integer(availableSeats).toString();
	    }	    
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return new DataInfo(id, fieldInfos, values);
    }
    @Override
    public DataInfo[] criteriaFind(String criteria) {
	// TODO 미구현
	return null;
    }
}
