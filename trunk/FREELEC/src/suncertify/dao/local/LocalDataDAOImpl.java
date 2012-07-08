/**
 * 
 */
package suncertify.dao.local;

import java.io.IOException;
import java.rmi.RemoteException;

import suncertify.dao.DataDAOImpl;

public class LocalDataDAOImpl extends DataDAOImpl {

    /**
         * @param dbname
         * @throws IOException
         */
    public LocalDataDAOImpl(String dbname) throws IOException {
	super(dbname);
    }

}
