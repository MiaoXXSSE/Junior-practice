package com.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServerBean {
	
	private static int port = 5000;
	
	
	private static Map map;	//在线用户的socket连接表
	
	public static void main(String[] args) {
		
		map = new HashMap();
		
		System.out.println("success");
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			
			while(true) {
				
				System.out.println("success socket1");
				
				Socket socket = serverSocket.accept();
				
				System.out.println("success socket2");
				
				HandleAclient task = new HandleAclient(socket,map);
				
				new Thread(task).start ();
			}
		}
		catch(IOException e) {
			System.err.println(e);
		}
	}
}
//����run���������ͻ��˵�����
class HandleAclient implements Runnable {
	
	private Map map;	//维护一个总的在线socket表
	
	private Map isChating;	//维护一个当前用户的连接表

	private static int port1 = 5001;
	
	private boolean isConnect = false;
	private Socket socket;
	private String userName;
	private String passwd;
	private String friendID;
	File file;
	//获得输入流
	InputStream is;
	BufferedReader br;
	//获得输出流  
	OutputStream os;  
	PrintWriter pw;  
	
	//获得输入流
	InputStream is1;
	BufferedReader br1;
	//获得输出流  
	OutputStream os1;  
	PrintWriter pw1;  

	
	public HandleAclient(Socket socket,Map map) {
		
		this.map = map;
		
		this.socket = socket;
		
		isChating = new HashMap();
		
		
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		
		
		System.out.println("success thread");
		try {
			is = socket.getInputStream();
			br = new BufferedReader(new InputStreamReader(is));
			os = socket.getOutputStream();
			pw = new PrintWriter(os);
			
			boolean stat = false;
			while(!stat) {
				userName = br.readLine();
				System.out.println("userName is receive:" + userName);
				file = new File("infor\\" + userName + ".txt");
				//������û���
				if(file.exists()) {
					pw.println("true");
					pw.flush();
					String Resp = br.readLine();
					System.out.println("Resp" + Resp);
					int resp = Integer.parseInt(Resp);
					//��¼��֤
					if(resp == 11) {
						passwd = br.readLine();
						BufferedReader reader = new BufferedReader(new FileReader(file));
						String filePasswd = reader.readLine();
						//������֤��Ľ������
						if(filePasswd.equals(passwd)){
							pw.println("true");
							pw.flush();
							stat = true;
						}
						
						else {
							pw.println("false");
							pw.flush();
						}
						reader.close();
					}
					//����������Ѿ�������û�����Ϊע��ѡ��ʱ
					//
				}
				//û������û���
				else {
					pw.println("false");
					pw.flush();
					System.out.println("false is send");
					String tmpResp = br.readLine();
					int resp = Integer.parseInt(tmpResp);
					System.out.println("test2");
					//�����������û������û���Ϊע��ѡ��Ļ�
					if(resp == 00) {
						passwd = br.readLine();
						System.out.println(passwd);
						file.createNewFile();
						FileOutputStream out = new FileOutputStream(file, false);//׷�ӵ�ʱ����true
						StringBuffer strbuf = new StringBuffer();
						strbuf.append(passwd + "\n");
						out.write(strbuf.toString().getBytes()); //ת���ַ���
						out.close();
						stat = true;
					}
					//�����������û������û���,��Ϊ��¼ѡ��Ļ�
					//���ø������ͻ�����ʾ��������
				}
			}
			
			map.put(userName, socket);
			
			isConnect = true;
			//主聊天框对应的后台，需要转发消息及处理好友关系
			SendMessage();
		}catch (IOException e) {
			System.err.println(e);
		}
	}
	
	private void SendMessage() {
		try {
//			BufferedReader reader = new BufferedReader(new FileReader(file));
//			FileOutputStream out = new FileOutputStream(file, false);
//			String filePasswd = reader.readLine();
			while(true) {
			
				int type = Integer.parseInt(br.readLine());
				System.out.println(type);
				switch(type) {
				case 000:
					String newName = "";
					int s = 0;
					if(br.readLine().equals("true")) {
						s = 1;
						System.out.println("##########2");
						newName = br.readLine();
						File fileTmp = new File("infor\\" + newName + ".txt");
						if(fileTmp.exists()) {
							FileOutputStream fos = new FileOutputStream(file, true);
							OutputStreamWriter osw = new OutputStreamWriter(fos);
							osw.write(friendID+"\n");
							osw.close();
							fos.close();
						}
					}
						//找到发送请求的服务器，然后返回结果
					for(Object obj : map.keySet()) {
						if(newName.equals((String)obj)) {
							Socket tmp = (Socket)map.get(obj);
							BufferedReader brTmp = new BufferedReader(new InputStreamReader(tmp.getInputStream()));
							PrintWriter pwTmp = new PrintWriter(tmp.getOutputStream());
							pwTmp.println(011 + "");
							pwTmp.flush();
							if(s == 1) {
								pwTmp.println("true");
								pwTmp.flush();
							} else {
								pwTmp.println("false");
								pwTmp.flush();
							}
						}
					}
					break;
				case 001:
					pw.println(001 + "");
					pw.flush();
					int i = 1;//临时计数，跳过一行的密码
					String tmpUser;
					BufferedReader reader = new BufferedReader(new FileReader(file));
					while((tmpUser = reader.readLine()) != null) {
						System.out.println(tmpUser);
						if(i >= 2) {
							pw.println(tmpUser);
							pw.flush();
						} else i++;
						System.out.println("testing1");
					}
					pw.println("EOF");
					pw.flush();
					reader.close();
					break;
				case 010://把该用户加入ischating表中
					String tmpNam = br.readLine();
					for(Object obj : map.keySet()) {
						if(tmpNam.equals((String)obj)) {
							isChating.put(tmpNam, map.get(tmpNam));
							break;
						}
					}
					break;
				case 011://添加好友
					friendID = br.readLine();
					int tmpNum = 0;
					//搜索在线列表是否有改用户
					for(Object obj : map.keySet()) {
						if(friendID.equals((String)obj)) {
							Socket tmp = (Socket)map.get(obj);
							BufferedReader brTmp = new BufferedReader(new InputStreamReader(tmp.getInputStream()));
							PrintWriter pwTmp = new PrintWriter(tmp.getOutputStream());
							pwTmp.println(000 + "");
							pwTmp.flush();
							pwTmp.println(userName);
							pwTmp.flush();
							System.out.println("##########1");
							tmpNum = 1;
							break;
						}
					}
					if(tmpNum == 0) {
						pw.println(011 + "");
						pw.flush();
						pw.println("false");
						pw.flush();
					}
					break;
				case 100://在 在线列表中搜索是否在线，在线的话将其加入当前在线用户列表，同时给在线列表中的每个人发送加入聊天组的人的用户名，添加到用户列表
					String users = br.readLine();
					int sss = 0;
					for(Object obj : map.keySet()) {
						System.out.println( users + " testing " + obj + "\n");
						if(users.equals((String)obj)) {
							pw.println(111 + "");
							pw.flush();
							pw.println("true");
							pw.flush();
							isChating.put(users, map.get(users));
							for(Object obj1 : isChating.keySet()) {
								System.out.println("xxx");
								Socket value1 = (Socket)isChating.get(obj1);
								OutputStream oss1 = value1.getOutputStream();
								PrintWriter pww1 = new PrintWriter(oss1);
								pww1.println(010 + "");
								pww1.flush();
								pww1.println(userName);
								pww1.flush();
							}

							sss = 10;
							break;
						}
					}
					if(sss != 10) {
						pw.println(111 + "");
						pw.flush();
						pw.println("false");
						pw.flush();
					}
					break;
					
				case 101:
					String infor = br.readLine();
					for(Object obj : isChating.keySet()) {
						Socket value = (Socket)isChating.get(obj);
						OutputStream oss = value.getOutputStream();
						PrintWriter pww = new PrintWriter(oss);
						pww.println(101 +"");
						pww.flush();
						pww.println(userName + ":" + infor);
						pww.flush();
					}
					break;
				}
				
			} 
		}catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
}