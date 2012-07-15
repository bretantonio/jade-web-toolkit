/**
 * 
 */
package com.abreqadhabra.freelec.java.workshop.addressbook.client.data.local;

import java.io.IOException;
import java.rmi.RemoteException;

import com.abreqadhabra.freelec.java.workshop.addressbook.client.data.DatabaseModeFactory;
import com.abreqadhabra.freelec.java.workshop.addressbook.dao.DAOFactory;
import com.abreqadhabra.freelec.java.workshop.addressbook.server.db.JavaDBServerThread;

public class LocalDatabaseFactoryImpl extends DatabaseModeFactory {

    /**
         * @param dao mode
         * @throws IOException
         */

    @Override
    public DAOFactory getDAOFactory(int whichDatabaseFactory, int whichDAOFactory) {
	Thread t = new Thread(new JavaDBServerThread());
        t.start();

	return DAOFactory.getDAOFactory(whichDAOFactory); //Derby용 DAO를 취득한다
    }

}
