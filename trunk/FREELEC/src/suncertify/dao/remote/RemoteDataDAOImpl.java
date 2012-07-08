/**
 *
 */
package suncertify.dao.remote;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import suncertify.client.remote.RemoteClient;
import suncertify.dao.DataDAOImpl;

/**
 * @author dskim
 *
 */
public class RemoteDataDAOImpl extends DataDAOImpl implements RemoteDataDAO {
    
    private Vector _clients;
    
    private Logger _logger = Logger.getLogger(this.getClass().getName());
    
    /**
     * @param dbname
     * @throws RemoteException
     * @throws IOException
     */
    public RemoteDataDAOImpl(String dbname) throws RemoteException, IOException {
        super(dbname);
        _clients = new Vector();
    }
    
    public Vector getClients() throws RemoteException {
        _logger.log(Level.INFO, this._clients.toString());
        return this._clients;
    }
    
    public synchronized void register(RemoteClient remoteClient)
    throws RemoteException {
        if (!(_clients.contains(remoteClient))) {
            _clients.addElement(remoteClient);
            _logger.log(Level.INFO, "registered client: " + remoteClient.toString());
        }else{
            _logger.log(Level.INFO, "duplicated client:" + remoteClient.toString());
        }
    }
    
    public synchronized void unregister(RemoteClient remoteClient)
    throws RemoteException {
        if (_clients.removeElement(remoteClient)) {
            _logger.log(Level.INFO, "unregistered client:" + remoteClient);
        } else {
            _logger.log(Level.INFO, "non-existed client:" + remoteClient);
        }
    }
    
}
