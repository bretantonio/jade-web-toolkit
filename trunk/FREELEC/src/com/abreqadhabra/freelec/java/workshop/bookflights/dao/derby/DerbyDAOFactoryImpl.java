package com.abreqadhabra.freelec.java.workshop.bookflights.dao.derby;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.abreqadhabra.freelec.java.workshop.bookflights.dao.BookFlightsDAO;
import com.abreqadhabra.freelec.java.workshop.bookflights.dao.DAOFactory;
import com.abreqadhabra.freelec.java.workshop.bookflights.database.ConnectionManager;

public class DerbyDAOFactoryImpl extends DAOFactory {

	// 로그 출력을 위한 선언
	static Logger logger = Logger.getLogger(DerbyDAOFactoryImpl.class
			.getCanonicalName());

	public DerbyDAOFactoryImpl(int derbyDriverType) {

		
			DatabaseManager.initDatabaseEnviroments(derbyDriverType);
		//initConnectionEnviroments(derbyDriverType);
			//DatabaseManager.importSQLScript(conn, in);
			
			
		logger.log(Level.INFO, "EmbeddedDataSource initialized");
		}



	@Override
	public BookFlightsDAO getBookFlightsDAO() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub

	}

}
