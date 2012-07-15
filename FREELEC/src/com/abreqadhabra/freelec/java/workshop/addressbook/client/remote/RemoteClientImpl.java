package com.abreqadhabra.freelec.java.workshop.addressbook.client.remote;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import suncertify.client.ClientView;
import suncertify.dao.DAOFactory;
import suncertify.dao.DataDAO;

public class RemoteClientImpl extends UnicastRemoteObject implements
	RemoteClient {

    protected ClientView _gui;

    private DataDAO _dao;

    public RemoteClientImpl(ClientView gui) throws RemoteException {
	super();
	this._gui = gui;
    }

    public DataDAO connect(String serviceURL) {
	// create the required DAO Factory
	DAOFactory factory = DAOFactory.getDAOFactory(DAOFactory.REMOTE);
	// Create a DAO

	try {
	    _dao = (DataDAO) factory.getDataDAO(serviceURL);
	} catch (RemoteException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	return _dao;
    }

    public void serverShuttingDown() throws RemoteException {
	SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
		int rc = JOptionPane.showConfirmDialog(_gui,
			"Do you wish to close client",
			"Server is shutting down", JOptionPane.YES_NO_OPTION);
		if (rc == JOptionPane.YES_OPTION) {
		    System.exit(0);
		}
	    }
	});
    }
}
