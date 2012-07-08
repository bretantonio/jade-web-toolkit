/*
 * DataDAOImpl.java
 *
 * Created on 2006/12/21, 15:51
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package suncertify.dao;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import suncertify.db.Data;
import suncertify.db.DataInfo;
import suncertify.db.DatabaseException;
import suncertify.db.FieldInfo;

/**
 * 
 * @author dskim
 */
public class DataDAOImpl extends UnicastRemoteObject implements DataDAO {

    private Data _data;

    /**
         * This constructor opens an existing database given the name of the
         * disk file containing it.
         * 
         * @param dbname
         *                The name of the database file to open.
         * @throws IOException
         */
    public DataDAOImpl(String dbname) throws IOException {

	this._data = new Data(dbname);

    }

    public void add(String[] newData) throws DatabaseException, RemoteException {
	this._data.add(newData);
    }

    public void close() throws RemoteException {
	this._data.close();

    }

    public DataInfo[] criteriaFind(String criteria) throws DatabaseException,
	    RemoteException {
	return this._data.criteriaFind(criteria);
    }

    public void delete(DataInfo toDelete) throws DatabaseException,
	    RemoteException {
	this._data.delete(toDelete);
    }

    public DataInfo find(String toMatch) throws DatabaseException,
	    RemoteException {
	return this._data.find(toMatch);
    }

    public FieldInfo[] getFieldInfo() throws RemoteException {
	return this._data.getFieldInfo();
    }

    public DataInfo getRecord(int recno) throws DatabaseException,
	    RemoteException {
	return this._data.getRecord(recno);
    }

    public int getRecordCount() throws RemoteException {
	return this._data.getRecordCount();
    }

    public void lock(int recno) throws DatabaseException, RemoteException {
	this._data.lock(recno);
    }

    public void modify(DataInfo newData) throws DatabaseException,
	    RemoteException {
	this._data.modify(newData);
    }

    public void unlock(int recno) throws RemoteException {
	this._data.unlock(recno);
    }

}
