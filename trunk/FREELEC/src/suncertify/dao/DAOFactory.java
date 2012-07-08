package suncertify.dao;

import java.io.IOException;
import java.rmi.RemoteException;

import suncertify.dao.local.LocalDataDAOFactory;
import suncertify.dao.remote.RemoteDataDAOFactory;

/**
 * Abstract class DAO Factory. 
 * <p>
 * 
 * @author Dongsup Kim
 * @version 0.8 10-Jan-2007
 */
public abstract class DAOFactory {
    
    // List of DAO types supported by the factory
    public static final int LOCAL = 0;
    
    public static final int REMOTE = 1;
    
    /**
     * This method instantiates the DAO class that is used in this
     * applications deployment environment to access the data.
     */
    public abstract Object getDataDAO(String option) throws IOException,
            RemoteException;
    
    public static DAOFactory getDAOFactory(int whichFactory) {
        switch (whichFactory) {
            case LOCAL:
                return new LocalDataDAOFactory();
            case REMOTE:
                return new RemoteDataDAOFactory();
            default:
                return null;
        }
    }
    
}
