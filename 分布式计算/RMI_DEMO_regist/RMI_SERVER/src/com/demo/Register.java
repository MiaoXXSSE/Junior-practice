package com.demo;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Register extends Remote {
	
    public boolean register(String username, String password) throws RemoteException; 

    
}