package com.abreqadhabra.freelec.java.workshop.addressbook.client;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import suncertify.db.DatabaseException;

import com.abreqadhabra.freelec.java.workshop.addressbook.client.data.DatabaseModeFactory;
import com.abreqadhabra.freelec.java.workshop.addressbook.client.remote.RemoteClientImpl;
import com.abreqadhabra.freelec.java.workshop.addressbook.dao.DAOFactory;
import com.abreqadhabra.freelec.java.workshop.addressbook.dao.FlightServiceDAO;
import com.abreqadhabra.freelec.java.workshop.addressbook.domain.DataInfo;
import com.abreqadhabra.freelec.java.workshop.addressbook.domain.FieldInfo;
import com.abreqadhabra.freelec.java.workshop.addressbook.server.db.JavaDBServerThread;

/**
 * @author dskim
 * 
 */
public final class ClientController {

    private String _serviceURL = "//127.0.0.1:1099/";

    private ClientView _gui;

    private FlightServiceDAO _dao;

    public static ResourceBundle resources;

    private static final String BASENAME = "resources.ClientApplication";

    static {
	try {
	    resources = ResourceBundle.getBundle(BASENAME, Locale.getDefault());
	} catch (MissingResourceException mre) {
	    throw new RuntimeException("resources/ClientApplication.properties not found");
	} finally {
	}
    }

    /**
         * @
         */
    protected ClientController()  {
	this._gui = new ClientView(this);
    }

    /**
         * @return the serviceURL
         */
    protected String getServiceURL() {
	return _serviceURL;
    }

    /**
         * @param serviceURL
         *                the serviceURL to set
         */
    protected void setServiceURL(String serviceURL) {
	this._serviceURL = serviceURL;
    }

    /**
         * @param clientMode
         */
    protected void setDatabaseMode(final String clientMode) {

	if (clientMode.equals(ClientController.resources
		.getString("GUI.OptionDialog.ClientMode.Local"))) {
	    // create the required DAO Factory
	    DatabaseModeFactory factory = DatabaseModeFactory.getDatabaseFactory(DatabaseModeFactory.EMBEDED_MODE);
	    // Create a DAO

		
		    _dao = (FlightServiceDAO) factory.getDAOFactory(DatabaseModeFactory.EMBEDED_MODE, DAOFactory.DERBY).getFlightServiceDAO();

	
	} else if (clientMode.equals(ClientController.resources
		.getString("GUI.OptionDialog.ClientMode.Remote"))) {

	    RemoteClientImpl remoteClient = null;
/*	    try {
		remoteClient = new RemoteClientImpl(_gui);
		_dao = remoteClient.connect(this._serviceURL);
		((RemoteDataDAO) _dao).register(remoteClient);
	    } catch (RemoteException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
*/
	}
    }

    /**
         * @return FlightServiceDAO
         */
    protected FlightServiceDAO getDAO() {
	return this._dao;
    }

    /**
         * @return String[]
         */
    protected String[] getColumnHeader() {
	String[] columnHeader = null;
	    FieldInfo[] fieldInfos = _dao.getFieldInfos();
	    fieldInfos[1].setName("편명");
	    fieldInfos[2].setName("출발지");
	    fieldInfos[3].setName("도착지");
	    fieldInfos[4].setName("항공사");
	    fieldInfos[5].setName("가격");
	    fieldInfos[6].setName("예정시각");
	    fieldInfos[7].setName("구분");
	    fieldInfos[8].setName("예약가능좌석");
	    columnHeader = new String[fieldInfos.length];
	    for (int i = 0; i < fieldInfos.length; i++) {
		columnHeader[i] = fieldInfos[i].getName();
	    }

	return columnHeader;
    }

    /**
         * @return int
         */
    protected int getRecordCount()  {
	int count = 0;
	count = _dao.getRecordCount();
	return count;
    }

    /**
         * @param id
         * @return String[]
         */
    protected String[] getRecord(final int id)  {
	String[] values = null;
	values = (_dao.getRecord(id)).getValues();
	return values;
    }

    /**
         * @param flightNumber
         * @return String[]
         */
    public String[] getRecordByFlightNumber(final String flightNumber) {
	String[] values = null;
	    values = _dao.findByFlightNumber(flightNumber).getValues();
	return values;
    }

    /**
         * @param flightNumber
         * @return int
         */
    public int getRecordNumberByFlightNumber(final String flightNumber) {
	int recordNumber = 0;

	    recordNumber = _dao.findByFlightNumber(flightNumber).getRecordNumber();

	return recordNumber;
    }

    /**
         * @param criteria
         * @return resultSet
         */
    public Object[] criteriaFind(final String criteria) {
	Object[] resultSet = null;
	    DataInfo[] dataInfo = _dao.criteriaFind(criteria);
	    if (dataInfo != null) {
		resultSet = new Object[dataInfo.length];
		for (int i = 0; i < dataInfo.length; i++) {
		    resultSet[i] = dataInfo[i].getValues();
		}
	    }
	return resultSet;
    }

    /**
         * @param recordNumber
         * @param updatedRecordValues
         * @param availableSeat
         * @return boolean
         */
    public boolean requestBook(final int recordNumber,
	    final String[] updatedRecordValues, final String availableSeat) {
	boolean isBooked = false;

	//미구현
//	    _dao.lock(recordNumber);
//	    String[] currentRecordValues = this.getRecord(recordNumber);
//	    if (Integer.parseInt(currentRecordValues[8]) == Integer
//		    .parseInt(availableSeat)) {
//		_dao.modify(new DataInfo(recordNumber, _dao.getFieldInfo(),
//			updatedRecordValues));
//		isBooked = true;
//	    }
//	    _dao.unlock(recordNumber);

	return isBooked;
    }

}
