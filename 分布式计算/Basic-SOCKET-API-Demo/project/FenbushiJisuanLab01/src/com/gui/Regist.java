package com.gui;

import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import java.awt.GridLayout;

import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;

import com.bean.LoginConnection;

public class Regist extends JFrame{

	//�û��������
	private JTextField username;
	private String userName;
	//���������
	private JPasswordField passwd;
	private String password;
	private String confirmPassword;
	//label
	private JLabel img;
	private JLabel name;
	private JLabel pass;
	private JLabel reg;
	private JLabel logi;
	
	private JLabel confirmPasswdx;
	private JPasswordField confirmPasswd;
	
	private JLabel background;
	
	private JButton regis;
	private JButton logins;
	
	private Container cont;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Regist window = new Regist();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Regist() {
		//set title
		this.setTitle("��¼  &&  ע��");
		initialize();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//set absolute layout
		this.setLayout(null);
		//set size
		this.setBounds(0, 0, 355, 265);
		//set icon
		Image image = new ImageIcon("src/images/th_Facebook.png").getImage();
		this.setIconImage(image);
		//set no_change_size
		this.setResizable(false);
		//ju zhong xian shi
		this.setLocationRelativeTo(null);
		//set kejian
		this.setVisible(true);
		
	}

	//ע������ĺ�������
	private void excute(int stat)
	{
		
		this.setVisible(false);
		new LoginConnection(userName, password, stat, this);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		cont = this.getContentPane();
		name = new JLabel();
		cont.setLayout(new GridLayout(2, 1));
		//���õ�¼ע�ᰴť
		regis = new JButton("regist");
		regis.setBounds(120, 190, 70, 20);
		regis.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(confirmPasswdx == null || confirmPasswd == null) {
					confirmPasswdx = new JLabel("confirmPW");
					confirmPasswdx.setBounds(100, 160, 70, 20);
					confirmPasswd = new JPasswordField();
					confirmPasswd.setBounds(175, 160, 150, 20);
					background.add(confirmPasswdx);
					background.add(confirmPasswd);
					background.updateUI();
				}
				userName = username.getText();
				password = String.valueOf(passwd.getPassword());
				confirmPassword = String.valueOf(confirmPasswd.getPassword());
				System.out.println(userName + "+" + password + "+" + confirmPassword);
				if(password.equals(confirmPassword) && !userName.isEmpty()) {
					excute(0);
				}
				else
					JOptionPane.showMessageDialog(null,"����ڶ����������������");
			}
			
		});
		
		logins = new JButton("login");
		logins.setBounds(200, 190, 70, 20);
		logins.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				userName = username.getText();
				password = String.valueOf(passwd.getPassword());
				if(!userName.isEmpty() && !password.isEmpty()) {
					excute(1);
				}
				else
					JOptionPane.showMessageDialog(null,"�û���������Ϊ�գ�����������");
			}
			
		});
		
		//����ɫ����
		Image imag = new ImageIcon("src/images/th_Facebook.png").getImage();
		background = new JLabel();
		background.setIcon(new ImageIcon(imag));
		background.setBounds(0, 0, 355, 265);
		
		//�û������
		username = new JTextField();
		username.setBounds(175, 100, 150, 20);
		//���������
		passwd = new JPasswordField();
		passwd.setBounds(175, 130, 150, 20);
		//�û�����ʾ��
		name = new JLabel("userName");
		name.setBounds(100, 100, 70, 20);
		//������ʾ��
		pass = new JLabel("passWord");
		pass.setBounds(100, 130, 70, 20);
		
		background.add(name);
		background.add(pass);
		
		cont.add(regis);
		cont.add(logins);
		cont.add(background);
		cont.add(username);
		cont.add(passwd);
		
	}

}
