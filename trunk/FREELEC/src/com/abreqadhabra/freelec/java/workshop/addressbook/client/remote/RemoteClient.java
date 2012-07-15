package com.abreqadhabra.freelec.java.workshop.addressbook.client.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteClient extends Remote {
    public void serverShuttingDown() throws RemoteException;
}
