package com.gui;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class MainActivity {

	private JFrame frame;
	
	//左右panel
	private JPanel panel_left;
	private JPanel panel_right;
	private JPanel send_panel;
	private JPanel textArea;//加一条分割线的panel
	private JPanel left_up_panel;
	
	//左边的组件
	private JTextArea jtextArea;
	private JSeparator jseparator;//分割线
	private JTextField input_field;
	private JButton send_button;
	
	private JButton addUser;
	//右边的组件
	private JList jlist;
	private JList jlist_talking;
	
	//列表的数据定义
	DefaultListModel userList;
	DefaultListModel talkingList;
	
	
	private Socket socket;
	private String serverAddress;
	
	private String friendID = null;
	
	private String temName;
	//获得输入流
	InputStream is;
	BufferedReader br;
	//获得输出流  
	OutputStream os;  
	PrintWriter pw;  
	
	private int countss = 0;
	/**
	 * Launch the application.
	 */
	/*
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainActivity window = new MainActivity();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainActivity(Socket socket,String serverAddress) {
		this.socket = socket;
		this.serverAddress = serverAddress;
		initialize();
		
		GetData thread = new GetData();
		new Thread(thread).start();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("聊天窗口");
		frame.setBounds(100, 100, 597, 497);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());
		
		createStream();
		
		//左边panel的处理，包括两个panel，第一个panel放输出窗口和分割线，第二个panel放输入窗口和发送按钮
		panel_left = new JPanel();
		panel_left.setLayout(new BorderLayout());
		
		//左上
		textArea = new JPanel(new BorderLayout());
		jtextArea = new JTextArea();
		jtextArea.setEnabled(false);//设置textarea只读
		jseparator = new JSeparator();
		textArea.add(jtextArea, BorderLayout.CENTER);
		textArea.add(jseparator, BorderLayout.SOUTH);
		panel_left.add(textArea, BorderLayout.CENTER);
		
		//左下
		send_panel = new JPanel(new BorderLayout());//该panel放输入框和发送按钮	
		input_field = new JTextField();
		send_button = new JButton("send");
		send_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendData();//添加好友
			}
		});
		//把输入框和发送按钮添加到panel中
		send_panel.add(input_field, BorderLayout.CENTER);
		send_panel.add(send_button, BorderLayout.SOUTH);
		panel_left.add(send_panel, BorderLayout.SOUTH);
		
		
		//右边panel的处理
		panel_right = new JPanel();
		panel_right.setPreferredSize(new Dimension(150, 0));
		panel_right.setLayout(new GridLayout(2,1));
		
		//为list添加元素，并把list加到frame中
		
		System.out.println("testing1");
		
		setList();//初始化列表
		LoadingFriend();//初始化好友列表
		System.out.println("testing2");
		//设置单选模式
		jlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		//添加监听
		jlist.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				addTalkUser(e);//添加当前讨论组成员
			}
			
		});
		panel_right.add(jlist);
		
		left_up_panel = new JPanel();
		left_up_panel.setLayout(new BorderLayout());		
		addUser = new JButton("Add new Friend");
		addUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addusr();//添加好友
			}
		});
		addUser.setPreferredSize(new Dimension(0,20));
		left_up_panel.add(addUser, BorderLayout.SOUTH);
		left_up_panel.add(jlist_talking,BorderLayout.CENTER);
		
		//前面需要添加一个用户list
		panel_right.add(left_up_panel);
		
		//添加组件到frame
		frame.getContentPane().add(panel_left,BorderLayout.CENTER);
		frame.getContentPane().add(panel_right, BorderLayout.EAST);
		
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		
		
	}
	//发送消息
	private void sendData() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//可以方便地修改日期格式
		String tmpinfor = input_field.getText();
		jtextArea.append(dateFormat.format(new Date()) + "\n" + tmpinfor + "\n");
		input_field.setText("");
		pw.println(101 + "");
		pw.flush();
		pw.println(tmpinfor);
		pw.flush();
	}
	
	
	//添加正在讨论的讨论组成员
	private void addTalkUser(ListSelectionEvent e) {
		//Object obj=((JList)e.getSource()).getSelectedValue();.
		temName = (String)((JList)e.getSource()).getSelectedValue();
		
		//判断list中是否已经有这个元素
		for(int i = 0; i < talkingList.getSize(); i++) {
			if(talkingList.getElementAt(i).equals(temName)) {
				countss = 1;
			}
		}
		if(countss != 1) {
			System.out.println("&&&&&&&&&&&&&&&&&&&&&&&");
			pw.println(100 + "");
			pw.flush();
			pw.println(temName);
			pw.flush();
		}
	}
	
	
	//初始化好友列表
	private void LoadingFriend() {
		pw.println(001 + "");
		pw.flush();
	}
	
	//添加好友
	private void addusr() {
		friendID = JOptionPane.showInputDialog("Please input your friend's ID");
		if(friendID != null) {
			pw.println(011 + "");
			pw.flush();
			pw.println(friendID);
			pw.flush();
		}
	}
	
	//初始化用户list
	private void setList() {
		userList = new DefaultListModel();
		jlist =  new JList(userList);
		//当前聊天用户列表、
		talkingList = new DefaultListModel();
		jlist_talking = new JList(talkingList);
	}
	
	//创建网络数据流
	private void createStream() {
		try {
			is = socket.getInputStream();
			br = new BufferedReader(new InputStreamReader(is));
			os = socket.getOutputStream();
			pw = new PrintWriter(os);
			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	//接受信息的线程
	class GetData implements Runnable {
		
		private boolean isConnect = false;

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//可以方便地修改日期格式 

		GetData() {
		}
		
		@Override
		public void run() {
			isConnect = true;
			try {
				while(isConnect) {
					
					String tmps = br.readLine();
					System.out.println("testing+" + tmps);
					
					int type = Integer.parseInt(tmps);
					
					switch(type) {
					case 111:
						String results = null;
						try {
							results = br.readLine();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
						if(results.equals("true")) {
							System.out.println(temName + "***" + " testing");
							talkingList.addElement(temName);
						}
						else {
							JOptionPane.showMessageDialog(frame, "该好友不在线，添加失败","添加",JOptionPane.WARNING_MESSAGE);
						}
						break;
					//收到添加好友
					case 000:
						String newName = "";
						newName = br.readLine();
						//确认添加返回是 0， 取消选择 1
						int isAdd = JOptionPane.showConfirmDialog(null,newName + " 请求添加你为好友，请确认或者取消","收到好友请求",JOptionPane.YES_NO_OPTION);
						pw.println(000 + "");
						pw.flush();
						if(isAdd == 0) {
							pw.println("true");
							pw.flush();
							pw.println(newName);
							pw.flush();
						}
						else {
							pw.println("false");
							pw.flush();
							pw.println(newName);
							pw.flush();
						}
						break;
					//获取好友列表
					case 001:
						String tmp = " ";
						while(tmp != null) {
							tmp = br.readLine();
							System.out.println("tmp" + tmp);
							//如果tmp是结束，则跳出循环
							if(tmp.equals("EOF")) {
								tmp = null;
							}
							else {
								userList.addElement(tmp);
							}
						}
						break;
					case 010://添加讨论组成员，接受到后，给当前列表添加，然后返回给服务器，服务器把该名字和socket加入ischating列表中
						String addUsr = br.readLine();
						System.out.println("这个重复了吗");
						talkingList.addElement(addUsr);
						pw.println(010 + "");
						pw.flush();
						pw.println(addUsr);
						pw.flush();
						break;
					case 011://添加好友
						String addStat = br.readLine();
						if(addStat.equals("true")) {//添加好友成功
							userList.addElement(friendID);
						}
						else {
							JOptionPane.showMessageDialog(frame, "由于该用户不同意或不在线，或改该用户不在线，添加失败，请重试！","添加好友",JOptionPane.WARNING_MESSAGE);
						}
						break;
					case 101://这个为普通消息
						String infors = br.readLine();
						jtextArea.append(dateFormat.format(new Date()) + "\n" + infors + "\n"); 
						break;
					}
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
	}

}

