package com.demo;

import com.demo.Register;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/*
 * regist username password 3
 * add username password others title startTime endTime 7
 * query username password startTime endTime 5
 * delete username password title 4
 * clear username password 3
 * quit 1
 */

/*
 * 
 */
public class HelloClient {

	public static void main(String[] args) {
		
		String username = "userName";
		
		String password = "passWD";
		
		try {
			 /*得到远程发布的服务， 返回与指定 name 关联的远程对象的引用（一个stub）
			  * 该name与服务器绑定的taskName要相同
			  * */ 
			
			Register regist = (Register) Naming.lookup("rmi://localhost:8888/Register");

			//调用远程方法
			boolean result = regist.register(username, password);

			// ���ע��ɹ�
			if (result) {
				System.out.println("注册成功");
				System.out.println("   注册的用户名为：" + username);
				System.out.println("   注册的密码为：" + password);
			}
			else {
				System.out.println("注册失败！！！");
			}

		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
