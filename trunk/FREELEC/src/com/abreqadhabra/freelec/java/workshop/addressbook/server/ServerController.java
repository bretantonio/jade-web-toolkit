package com.abreqadhabra.freelec.java.workshop.addressbook.server;

import java.io.IOException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import suncertify.client.remote.RemoteClient;
import suncertify.dao.DataDAO;
import suncertify.dao.remote.RemoteDataDAOImpl;

/**
 * This class implements the controller for sever application.
 * <p>
 * 
 * @see suncertify.client.remote.RemoteClient
 * @see suncertify.dao.DataDAO
 * @see suncertify.dao.remote.RemoteDataDAOImpl
 * @author Dongsup Kim
 * @version 0.8 19-Dec-2006
 */
public class ServerController {

    private final static String BASENAME = "com.abreqadhabra.freelec.java.workshop.addressbook.resources.ServerApplication";

    private final static String PROPERTIES_NOT_FOUND = "com/abreqadhabra/freelec/java/workshop/addressbook/resources/ServerApplication.properties not found";

    private Logger _logger = Logger.getLogger(this.getClass().getName());

    private int _port;

    private Vector _clients = new Vector();

    private Registry _registry;


    private RemoteDataDAOImpl _remoteDataDAOImpl;

    public static ResourceBundle resources;

    static {
	try {
	    resources = ResourceBundle.getBundle(BASENAME, Locale.getDefault());
	} catch (MissingResourceException mre) {
	    throw new RuntimeException(PROPERTIES_NOT_FOUND);
	}
    }

    /**
         * This constructor create the instance of <code>ServerView</code> for
         * Graphical User Interface.
         * 
         * @param port
         *                The port of naming service like rmi registry.
         */
    protected ServerController(int port) {
	this._port = port;
        new ServerView(this);
    }

    /**
         * Registers a new database service or attaches it to an existing.
         * 
         * @param dbname
         *                The name of the database file to open.
         */
    protected final void startServer(final String dbname) {
	if (System.getSecurityManager() == null) {
	    System.setSecurityManager(new RMISecurityManager());
	}
	try {
	    this._registry = LocateRegistry.createRegistry(_port);
	    _remoteDataDAOImpl = new RemoteDataDAOImpl(dbname);
	    _clients = _remoteDataDAOImpl.getClients();
	    this._registry.rebind(DataDAO.SERVICENAME, _remoteDataDAOImpl);
	    _logger.log(Level.INFO, ServerController.resources
		    .getString("ServerController.INFO.D")
		    + this._port);
	} catch (ExportException e) {
	    _logger.log(Level.WARNING, e.getMessage());
	    try {
		this._registry = LocateRegistry.getRegistry(this._port);
		_logger.log(Level.INFO, ServerController.resources
			.getString("ServerController.INFO.F"));
		boolean serviceAlreadyBound = false;
		String[] service = this._registry.list();
		for (int i = 0; i < service.length; i++) {
		    _logger.log(Level.INFO, service[i]);
		    if (service[i].equals(DataDAO.SERVICENAME)) {
			serviceAlreadyBound = true;
		    }
		    if (serviceAlreadyBound) {
			_logger.log(
				Level.INFO,
				ServerController.resources
					.getString("ServerController.INFO.G")
					+ this._port);
		    }
		}
		throw new RuntimeException(e.getMessage());
	    } catch (RemoteException e1) {
		_logger.log(Level.WARNING, e.getMessage());
		throw new RuntimeException(e.getMessage());
	    }
	} catch (RemoteException e) {
	    _logger.log(Level.WARNING, e.getMessage());
	    throw new RuntimeException(e.getMessage());
	} catch (IOException e) {
	    _logger.log(Level.WARNING, e.getMessage());
	    throw new RuntimeException(e.getMessage());
	}
    }

    /**
         * Notification to all clients's GUI before shutting down.
         */
    protected final void shuttingDownServer() {
	_logger.log(Level.INFO, ServerController.resources
		.getString("ServerController.INFO.A"));
	if (_clients.size() == 0) {
	    _logger.log(Level.INFO, ServerController.resources
		    .getString("ServerController.INFO.E"));
	} else {
	    _logger.log(Level.INFO, ServerController.resources
		    .getString("ServerController.INFO.B"));
	    Vector v = (Vector) _clients.clone();
	    RemoteClient remoteclient = null;
	    for (int i = v.size() - 1; i >= 0; i--) {
		remoteclient = (RemoteClient) v.elementAt(i);
		try {
		    remoteclient.serverShuttingDown();
		} catch (RemoteException e) {
		    _logger.log(Level.WARNING, e.getMessage());
		}
	    }
	}
	_logger.log(Level.INFO, ServerController.resources
		.getString("ServerController.INFO.C"));
	System.exit(0);
    }
    
    /**
     * @param input
     * @return boolean
     */
    public static boolean portValidation(final String input) {
	int port = 0;
	boolean flag = false;
	Pattern pattern = Pattern.compile("[0-9]");
	Matcher matcher = pattern.matcher(input);
	flag = matcher.find();
	if (flag) {
	    port = Integer.parseInt(input);
	    if (port <= 1024 && port >= 65535) {
		flag = false;
	    }
	}
	return flag;
    }
}
