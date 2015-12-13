package com.bean;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JOptionPane;

import com.gui.MainActivity;
import com.gui.Regist;

public class LoginConnection
{
	private String serverAddress = "localhost";
	private int port = 5000;
	//变量定义
	private String userName;
	private String passwd;
	private InputStream input;
	private BufferedReader br;
	private OutputStream output;
	private PrintWriter pw;
	private Socket socket;
	private Regist regist;//引用初始化的Regist，使得可以弹出窗口，或者进入下一级窗口
	private int stat;//查看是哪个按钮被点击,0是左边，1是右边

	public LoginConnection(String userName,String passwd,int stat,Regist regist)
	{
		this.userName = userName;
		this.passwd = passwd;
		this.stat = stat;
		this.regist = regist;
		if(connectToServer()) {
			excute();
		}
		else {
			regist.setVisible(true);
			JOptionPane.showMessageDialog(this.regist, "网络连接失败，请检查网络连接","网络错误",JOptionPane.WARNING_MESSAGE);
		}
	}
	//登录函数
	private void excute()
	{
		System.out.println("excute");
		try {
			System.out.println("test1");
			pw.println(userName);
			pw.flush();
			System.out.println(userName);
			//socket.shutdownOutput();
			String results = null;
			System.out.println("test2");
			results = br.readLine();
			System.out.println("test3" + results);
			boolean resut;
			if(results.equals("true")) {
				resut = true;
			} else resut = false;
			
			System.out.println( stat + ":" + resut);
			//如果服务器中没有这个用户名,且为登录选择的话
			if(stat == 1 && !resut) {
				pw.println(10 + "");
				pw.flush();
				regist.setVisible(true);
				JOptionPane.showMessageDialog(this.regist, "用户名不存在，请重新输入","登录结果",JOptionPane.WARNING_MESSAGE);
			}
			//如果服务器已经有这个用户名且为注册选项时
			else if(stat == 0 && resut) {
				pw.println(01 + "");
				pw.flush();
				regist.setVisible(true);
				JOptionPane.showMessageDialog(this.regist, "用户名已经存在，请重新输入","登录结果",JOptionPane.WARNING_MESSAGE);
			}
			//如果服务器中没有这个用户且为注册选项的话
			else if(stat == 0 && !resut) {
				pw.println(00 + "");
				pw.flush();
				System.out.println(passwd);
				pw.println(passwd + "");
				pw.flush();
				this.regist.setVisible(false);
				new MainActivity(socket,serverAddress);
			}

			//登录验证
			else {
				pw.println(11 + "");
				pw.flush();
				pw.println(passwd);
				pw.flush();
				String isOK = br.readLine();
				//如果验证 成功
				if(isOK.equals("true")) {
					this.regist.setVisible(false);
					new MainActivity(socket,serverAddress);
				}
				else {
					regist.setVisible(true);
					JOptionPane.showMessageDialog(this.regist, "密码输入错误","登录结果",JOptionPane.WARNING_MESSAGE);
				}
			}
		} catch(IOException e) {
			System.err.println(e);
		}
	}

	//socket连接函数
	private boolean connectToServer()
	{
		try {
			this.socket = new Socket(serverAddress, port);
			input = socket.getInputStream();
			br=new BufferedReader(new InputStreamReader(input));  
			output = socket.getOutputStream();
			pw=new PrintWriter(output);
			return true;
		} catch(Exception e) {
			JOptionPane.showMessageDialog(regist, "服务器没有响应","无法连接到服务器",JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}
}
