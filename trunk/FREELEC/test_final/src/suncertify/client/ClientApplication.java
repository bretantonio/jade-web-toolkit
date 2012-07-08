package suncertify.client;

import java.rmi.RemoteException;

/**
 * @author dskim
 * 
 */
public class ClientApplication {

    /**
     * @param args
     */
    public static void main(String[] args) {
	try {
	    new ClientController();
	} catch (RemoteException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }

}
