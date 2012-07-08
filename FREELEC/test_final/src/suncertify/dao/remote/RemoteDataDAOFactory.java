package suncertify.dao.remote;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

import suncertify.dao.DAOFactory;
import suncertify.dao.DataDAO;

/**
 * Implements class DAO Factory for remote data. 
 * <p>
 * 
 * @author Dongsup Kim
 * @version 0.8 10-Jan-2007
 */
public class RemoteDataDAOFactory extends DAOFactory {
    
    private Logger _logger = Logger.getLogger(this.getClass().getName());
    
    public final Object getDataDAO(String serviceURL) {
        Remote dao = null;
        try {
            if (System.getSecurityManager() == null) {
                System.setSecurityManager(new SecurityManager());
            }else{
                _logger.log(Level.INFO, "SecurityManager not found.");
            }
            _logger.log(Level.INFO, "Binding " + serviceURL + DataDAO.SERVICENAME);
            dao = Naming.lookup(serviceURL + DataDAO.SERVICENAME);
        } catch (MalformedURLException me) {
            _logger.log(Level.WARNING, me.getMessage());
        } catch (RemoteException re) {
            _logger.log(Level.WARNING, re.getMessage());
            re.printStackTrace();
        } catch (NotBoundException ne) {
            _logger.log(Level.WARNING, ne.getMessage());
        }
        return dao;
    }
    
}
