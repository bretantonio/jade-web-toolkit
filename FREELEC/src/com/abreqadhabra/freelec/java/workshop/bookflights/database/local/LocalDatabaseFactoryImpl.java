/**
 * 
 */
package com.abreqadhabra.freelec.java.workshop.bookflights.database.local;

import java.util.logging.Logger;

import com.abreqadhabra.freelec.java.workshop.bookflights.dao.DAOFactory;
import com.abreqadhabra.freelec.java.workshop.bookflights.database.DatabaseFactory;

public class LocalDatabaseFactoryImpl extends DatabaseFactory {

	// 로그 출력을 위한 선언
	private static Logger logger = Logger.getLogger(LocalDatabaseFactoryImpl.class.getCanonicalName());
	
	/**
	 * @param dao
	 *            mode
	 * @throws IOException
	 */

	@Override
	public DAOFactory getDAOFactory(int whichDatabaseFactory,
			int whichDAOFactory) {
		return DAOFactory.getDAOFactory(whichDAOFactory); 
	}

}
