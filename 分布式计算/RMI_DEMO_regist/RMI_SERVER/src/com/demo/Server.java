package com.demo;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;


/*
 * 1.注册一个端口  
 * 2.在注册端口绑定taskName和implementClass  （taskName在本例中指rmi://localhost:8888/Register中的Register，implementClass指register
 * 3.客户端就可以通过url和taskName来找到implementClass。 
 */
public class Server {
	public static void main(String[] args) {
		try {
			//创建实例，注意new的对象
			Register register = new RegisterUtil();

			LocateRegistry.createRegistry(8888);//注册端口

			Naming.bind("rmi://localhost:8888/Register", register);

			System.out.println(">>>>>server is running");
			
	} catch (RemoteException e) {
		System.out.println("对应的出错处理。。。");
		e.printStackTrace();
	} catch (AlreadyBoundException e) {
		System.out.println("对应的出错处理。。。");
		e.printStackTrace();
	} catch (MalformedURLException e) {
		System.out.println("对应的出错处理。。。");
		e.printStackTrace();
	}
}
}