package com.abreqadhabra.freelec.java.workshop.addressbook.client.data;

import java.io.IOException;
import java.rmi.RemoteException;

import com.abreqadhabra.freelec.java.workshop.addressbook.client.data.local.LocalDatabaseFactoryImpl;
import com.abreqadhabra.freelec.java.workshop.addressbook.dao.DAOFactory;

/**
 * Abstract class DAO Factory. 
 * <p>
 * 
 * @author Dongsup Kim
 * @version 0.8 10-Jan-2007
 */
public abstract class DatabaseModeFactory {
    
    // List of DAO types supported by the factory
    public static final int EMBEDED_MODE = 0;
    
    public static final int REMOTE_SERVICE_MODE = 1;
    
    /**
     * This method instantiates the DAO class that is used in this
     * applications deployment environment to access the data.
     */
    public abstract DAOFactory getDAOFactory(int whichDatabaseFactory, int whichDAOFactory);
    
    public static DatabaseModeFactory getDatabaseFactory(int whichDatabaseFactory) {
        switch (whichDatabaseFactory) {
            case EMBEDED_MODE:
                return (DatabaseModeFactory) new LocalDatabaseFactoryImpl();
            case REMOTE_SERVICE_MODE:
                //return new RemoteDataDAOFactory();
            default:
                return null;
        }
    }
    
}
