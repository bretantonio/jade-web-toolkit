/**
 * 
 */
package suncertify.dao.local;

import java.io.IOException;

import suncertify.dao.DAOFactory;
import suncertify.dao.DataDAO;

public class LocalDataDAOFactory extends DAOFactory {

    /*
     * (non-Javadoc)
     * 
     * @see suncertify.dao.DAOFactory#getDataDAO(java.lang.String)
     */
    public Object getDataDAO(String option) throws IOException {
	DataDAO dao = null;
	// LocalDatabaseDAOImpl implements DatabaseDAO
	dao = new LocalDataDAOImpl(option);
	return dao;
    }

}
