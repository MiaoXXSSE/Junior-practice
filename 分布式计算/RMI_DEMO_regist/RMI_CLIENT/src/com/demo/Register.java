package com.demo;

import java.rmi.Remote;
import java.rmi.RemoteException;

//创建远程对象接口
public interface Register extends Remote {
	
    public boolean register(String username, String password) throws RemoteException; 

    
}