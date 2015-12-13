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
	
	//����panel
	private JPanel panel_left;
	private JPanel panel_right;
	private JPanel send_panel;
	private JPanel textArea;//��һ���ָ��ߵ�panel
	private JPanel left_up_panel;
	
	//��ߵ����
	private JTextArea jtextArea;
	private JSeparator jseparator;//�ָ���
	private JTextField input_field;
	private JButton send_button;
	
	private JButton addUser;
	//�ұߵ����
	private JList jlist;
	private JList jlist_talking;
	
	//�б�����ݶ���
	DefaultListModel userList;
	DefaultListModel talkingList;
	
	
	private Socket socket;
	private String serverAddress;
	
	private String friendID = null;
	
	private String temName;
	//���������
	InputStream is;
	BufferedReader br;
	//��������  
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
		frame.setTitle("���촰��");
		frame.setBounds(100, 100, 597, 497);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());
		
		createStream();
		
		//���panel�Ĵ�����������panel����һ��panel��������ںͷָ��ߣ��ڶ���panel�����봰�ںͷ��Ͱ�ť
		panel_left = new JPanel();
		panel_left.setLayout(new BorderLayout());
		
		//����
		textArea = new JPanel(new BorderLayout());
		jtextArea = new JTextArea();
		jtextArea.setEnabled(false);//����textareaֻ��
		jseparator = new JSeparator();
		textArea.add(jtextArea, BorderLayout.CENTER);
		textArea.add(jseparator, BorderLayout.SOUTH);
		panel_left.add(textArea, BorderLayout.CENTER);
		
		//����
		send_panel = new JPanel(new BorderLayout());//��panel�������ͷ��Ͱ�ť	
		input_field = new JTextField();
		send_button = new JButton("send");
		send_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendData();//��Ӻ���
			}
		});
		//�������ͷ��Ͱ�ť��ӵ�panel��
		send_panel.add(input_field, BorderLayout.CENTER);
		send_panel.add(send_button, BorderLayout.SOUTH);
		panel_left.add(send_panel, BorderLayout.SOUTH);
		
		
		//�ұ�panel�Ĵ���
		panel_right = new JPanel();
		panel_right.setPreferredSize(new Dimension(150, 0));
		panel_right.setLayout(new GridLayout(2,1));
		
		//Ϊlist���Ԫ�أ�����list�ӵ�frame��
		
		System.out.println("testing1");
		
		setList();//��ʼ���б�
		LoadingFriend();//��ʼ�������б�
		System.out.println("testing2");
		//���õ�ѡģʽ
		jlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		//��Ӽ���
		jlist.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				addTalkUser(e);//��ӵ�ǰ�������Ա
			}
			
		});
		panel_right.add(jlist);
		
		left_up_panel = new JPanel();
		left_up_panel.setLayout(new BorderLayout());		
		addUser = new JButton("Add new Friend");
		addUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addusr();//��Ӻ���
			}
		});
		addUser.setPreferredSize(new Dimension(0,20));
		left_up_panel.add(addUser, BorderLayout.SOUTH);
		left_up_panel.add(jlist_talking,BorderLayout.CENTER);
		
		//ǰ����Ҫ���һ���û�list
		panel_right.add(left_up_panel);
		
		//��������frame
		frame.getContentPane().add(panel_left,BorderLayout.CENTER);
		frame.getContentPane().add(panel_right, BorderLayout.EAST);
		
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		
		
	}
	//������Ϣ
	private void sendData() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//���Է�����޸����ڸ�ʽ
		String tmpinfor = input_field.getText();
		jtextArea.append(dateFormat.format(new Date()) + "\n" + tmpinfor + "\n");
		input_field.setText("");
		pw.println(101 + "");
		pw.flush();
		pw.println(tmpinfor);
		pw.flush();
	}
	
	
	//����������۵��������Ա
	private void addTalkUser(ListSelectionEvent e) {
		//Object obj=((JList)e.getSource()).getSelectedValue();.
		temName = (String)((JList)e.getSource()).getSelectedValue();
		
		//�ж�list���Ƿ��Ѿ������Ԫ��
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
	
	
	//��ʼ�������б�
	private void LoadingFriend() {
		pw.println(001 + "");
		pw.flush();
	}
	
	//��Ӻ���
	private void addusr() {
		friendID = JOptionPane.showInputDialog("Please input your friend's ID");
		if(friendID != null) {
			pw.println(011 + "");
			pw.flush();
			pw.println(friendID);
			pw.flush();
		}
	}
	
	//��ʼ���û�list
	private void setList() {
		userList = new DefaultListModel();
		jlist =  new JList(userList);
		//��ǰ�����û��б�
		talkingList = new DefaultListModel();
		jlist_talking = new JList(talkingList);
	}
	
	//��������������
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
	
	//������Ϣ���߳�
	class GetData implements Runnable {
		
		private boolean isConnect = false;

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//���Է�����޸����ڸ�ʽ 

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
							JOptionPane.showMessageDialog(frame, "�ú��Ѳ����ߣ����ʧ��","���",JOptionPane.WARNING_MESSAGE);
						}
						break;
					//�յ���Ӻ���
					case 000:
						String newName = "";
						newName = br.readLine();
						//ȷ����ӷ����� 0�� ȡ��ѡ�� 1
						int isAdd = JOptionPane.showConfirmDialog(null,newName + " ���������Ϊ���ѣ���ȷ�ϻ���ȡ��","�յ���������",JOptionPane.YES_NO_OPTION);
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
					//��ȡ�����б�
					case 001:
						String tmp = " ";
						while(tmp != null) {
							tmp = br.readLine();
							System.out.println("tmp" + tmp);
							//���tmp�ǽ�����������ѭ��
							if(tmp.equals("EOF")) {
								tmp = null;
							}
							else {
								userList.addElement(tmp);
							}
						}
						break;
					case 010://����������Ա�����ܵ��󣬸���ǰ�б���ӣ�Ȼ�󷵻ظ����������������Ѹ����ֺ�socket����ischating�б���
						String addUsr = br.readLine();
						System.out.println("����ظ�����");
						talkingList.addElement(addUsr);
						pw.println(010 + "");
						pw.flush();
						pw.println(addUsr);
						pw.flush();
						break;
					case 011://��Ӻ���
						String addStat = br.readLine();
						if(addStat.equals("true")) {//��Ӻ��ѳɹ�
							userList.addElement(friendID);
						}
						else {
							JOptionPane.showMessageDialog(frame, "���ڸ��û���ͬ������ߣ���ĸ��û������ߣ����ʧ�ܣ������ԣ�","��Ӻ���",JOptionPane.WARNING_MESSAGE);
						}
						break;
					case 101://���Ϊ��ͨ��Ϣ
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

