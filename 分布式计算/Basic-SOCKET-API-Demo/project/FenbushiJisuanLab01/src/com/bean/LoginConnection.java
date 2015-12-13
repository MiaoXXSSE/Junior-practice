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
	//��������
	private String userName;
	private String passwd;
	private InputStream input;
	private BufferedReader br;
	private OutputStream output;
	private PrintWriter pw;
	private Socket socket;
	private Regist regist;//���ó�ʼ����Regist��ʹ�ÿ��Ե������ڣ����߽�����һ������
	private int stat;//�鿴���ĸ���ť�����,0����ߣ�1���ұ�

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
			JOptionPane.showMessageDialog(this.regist, "��������ʧ�ܣ�������������","�������",JOptionPane.WARNING_MESSAGE);
		}
	}
	//��¼����
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
			//�����������û������û���,��Ϊ��¼ѡ��Ļ�
			if(stat == 1 && !resut) {
				pw.println(10 + "");
				pw.flush();
				regist.setVisible(true);
				JOptionPane.showMessageDialog(this.regist, "�û��������ڣ�����������","��¼���",JOptionPane.WARNING_MESSAGE);
			}
			//����������Ѿ�������û�����Ϊע��ѡ��ʱ
			else if(stat == 0 && resut) {
				pw.println(01 + "");
				pw.flush();
				regist.setVisible(true);
				JOptionPane.showMessageDialog(this.regist, "�û����Ѿ����ڣ�����������","��¼���",JOptionPane.WARNING_MESSAGE);
			}
			//�����������û������û���Ϊע��ѡ��Ļ�
			else if(stat == 0 && !resut) {
				pw.println(00 + "");
				pw.flush();
				System.out.println(passwd);
				pw.println(passwd + "");
				pw.flush();
				this.regist.setVisible(false);
				new MainActivity(socket,serverAddress);
			}

			//��¼��֤
			else {
				pw.println(11 + "");
				pw.flush();
				pw.println(passwd);
				pw.flush();
				String isOK = br.readLine();
				//�����֤ �ɹ�
				if(isOK.equals("true")) {
					this.regist.setVisible(false);
					new MainActivity(socket,serverAddress);
				}
				else {
					regist.setVisible(true);
					JOptionPane.showMessageDialog(this.regist, "�����������","��¼���",JOptionPane.WARNING_MESSAGE);
				}
			}
		} catch(IOException e) {
			System.err.println(e);
		}
	}

	//socket���Ӻ���
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
			JOptionPane.showMessageDialog(regist, "������û����Ӧ","�޷����ӵ�������",JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}
}
