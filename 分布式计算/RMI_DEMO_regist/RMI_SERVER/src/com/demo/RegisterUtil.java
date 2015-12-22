package com.demo;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/* 
* 远程对象的实现 
* 公开方法的具体实现就是这里定义的 
*/ 
public class RegisterUtil extends UnicastRemoteObject implements Register {

	protected RegisterUtil() throws RemoteException {
		super();//显式的构造函数，并且要抛出一个RemoteException异常 
	}
	
	 public boolean register(String username, String password) throws RemoteException {
		 
		 System.out.println("服务器开始运行注册程序！");
		 System.out.println("服务器开始运行注册程序！");
		 System.out.println("这儿写注册的具体实现，");
		 System.out.println("处理器上把注册的事件处理完成后返回处理结果，是否注册成功。。。");
		 
		 return true;
		 
		 
	 }


}
