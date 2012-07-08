package suncertify.client;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import suncertify.client.remote.RemoteClientImpl;
import suncertify.dao.DAOFactory;
import suncertify.dao.DataDAO;
import suncertify.dao.remote.RemoteDataDAO;
import suncertify.db.DataInfo;
import suncertify.db.DatabaseException;
import suncertify.db.FieldInfo;

/**
 * @author dskim
 * 
 */
public final class ClientController {

    private String _serviceURL = "//127.0.0.1:1099/";

    private ClientView _gui;

    private DataDAO _dao;

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
         * @throws RemoteException
         */
    protected ClientController() throws RemoteException {
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
    protected void setDAO(final String clientMode) {

	if (clientMode.equals(ClientController.resources
		.getString("GUI.OptionDialog.ClientMode.Local"))) {
	    // create the required DAO Factory
	    DAOFactory factory = DAOFactory.getDAOFactory(DAOFactory.LOCAL);
	    // Create a DAO

	    try {
		_dao = (DataDAO) factory.getDataDAO("db.db");
	    } catch (RemoteException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }

	} else if (clientMode.equals(ClientController.resources
		.getString("GUI.OptionDialog.ClientMode.Remote"))) {

	    RemoteClientImpl remoteClient = null;
	    try {
		remoteClient = new RemoteClientImpl(_gui);
		_dao = remoteClient.connect(this._serviceURL);
		((RemoteDataDAO) _dao).register(remoteClient);
	    } catch (RemoteException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }

	}
    }

    /**
         * @return DataDAO
         */
    protected DataDAO getDAO() {
	return this._dao;
    }

    /**
         * @return String[]
         */
    protected String[] getColumnHeader() {
	String[] columnHeader = null;
	try {
	    FieldInfo[] fieldInfos = (_dao.getRecord(1)).getFields();
	    columnHeader = new String[fieldInfos.length];
	    for (int i = 0; i < fieldInfos.length; i++) {
		columnHeader[i] = fieldInfos[i].getName();
	    }
	} catch (RemoteException e) {
	    // TODO
	} catch (DatabaseException de) {
	    // TODO Auto-generated catch block
	    de.printStackTrace();
	}
	return columnHeader;
    }

    /**
         * @return int
         */
    protected int getRecordCount() {
	int count = 0;
	try {
	    count = _dao.getRecordCount();
	} catch (RemoteException e) {
	    // TODO
	}
	return count;
    }

    /**
         * @param index
         * @return String[]
         */
    protected String[] getRecord(final int index) {
	String[] values = null;
	try {
	    values = (_dao.getRecord(index)).getValues();
	} catch (RemoteException e) {
	    // TODO
	} catch (DatabaseException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return values;
    }

    /**
         * @param flightNumber
         * @return String[]
         */
    public String[] getRecordByFlightNumber(final String flightNumber) {
	String[] values = null;
	try {
	    values = _dao.find(flightNumber).getValues();
	} catch (RemoteException e) {
	    // TODO
	} catch (DatabaseException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return values;
    }

    /**
         * @param flightNumber
         * @return int
         */
    public int getRecordNumberByFlightNumber(final String flightNumber) {
	int recordNumber = 0;
	try {
	    recordNumber = _dao.find(flightNumber).getRecordNumber();
	} catch (RemoteException e) {
	    // TODO
	} catch (DatabaseException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return recordNumber;
    }

    /**
         * @param criteria
         * @return resultSet
         */
    public Object[] criteriaFind(final String criteria) {
	Object[] resultSet = null;
	try {
	    DataInfo[] dataInfo = _dao.criteriaFind(criteria);
	    if (dataInfo != null) {
		resultSet = new Object[dataInfo.length];
		for (int i = 0; i < dataInfo.length; i++) {
		    resultSet[i] = dataInfo[i].getValues();
		}
	    }
	} catch (RemoteException e) {
	    // TODO
	} catch (DatabaseException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
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
	try {
	    _dao.lock(recordNumber);
	    String[] currentRecordValues = this.getRecord(recordNumber);
	    if (Integer.parseInt(currentRecordValues[8]) == Integer
		    .parseInt(availableSeat)) {
		_dao.modify(new DataInfo(recordNumber, _dao.getFieldInfo(),
			updatedRecordValues));
		isBooked = true;
	    }
	    _dao.unlock(recordNumber);

	} catch (RemoteException e) {
	    // TODO
	    return isBooked;
	} catch (DatabaseException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return isBooked;
    }

}
