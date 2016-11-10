package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import common.ChatData;
import common.Data;
import java.awt.Font;

public class ClientStudentChat extends JFrame implements ActionListener{

	private static final long serialVersionUID = 1665114227168566122L;
	private JPanel contentPane;
	private JPanel p_center;
	private JPanel p_south;
	private JPanel p_east;
	private JScrollPane sp_list;
	private JList list_user;
	private JScrollPane sp_message;
	private JTextArea ta_message;
	private JTextField tf_message;
	private JPanel p_north;
	private JLabel lbl_title;
	private ClientStudentGUI gui;
	private String title;
	private String name;
	private String regNo;
	private ClientStudentChat chatGUI;
	private JLabel lbl_id;

	public ClientStudentChat(ClientStudentGUI gui, String title, String name, String regNo) {
		this.chatGUI = this;
		this.gui = gui;
		this.title = title;
		this.name = name;
		this.regNo = regNo;
		drawGUI();
		setVisible(true);
	}
	
	public void setUserList(ArrayList userList){
		list_user.setListData(userList.toArray());
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == tf_message){
//			입력한 메세지와 내 아이디를 담아 서버로 쏜다.
//			Data.S_CHAT_MESSAGE 사용..
			
			Data data = new Data(Data.S_CHAT_MESSAGE);
			ChatData cData = new ChatData();
			cData.setMessage(tf_message.getText());
			cData.setName(name);
			cData.setRegNo(regNo);
			cData.setTitle(title);
			data.setChatData(cData);
			gui.sendData(data);
			tf_message.setText("");
		}
	}
	
	public void appendChat(String name, String message){
		ta_message.append(String.format("%s> %s\n", name, message));
		sp_message.getVerticalScrollBar().setValue(sp_message.getVerticalScrollBar().getMaximum());
	}
	
	public void setName(String name){
		lbl_id.setText("내 이름 : " + name);
	}

	private void drawGUI() {
		setTitle(title + " 채팅방");
		setBounds(100, 100, 537, 579);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				int a = JOptionPane.showConfirmDialog(chatGUI, "채팅방을 나가시겠습니까?", "채팅종료", JOptionPane.YES_NO_OPTION);
				if(a == JOptionPane.OK_OPTION){
					Data data = new Data(Data.S_CHAT_EXIT);
					ChatData cData = new ChatData();
					cData.setName(name);
					cData.setRegNo(regNo);
					cData.setTitle(title);
					data.setChatData(cData);
					gui.sendData(data);
					gui.setCSCNULL();
					dispose();
				}
			}
		});
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		p_center = new JPanel();
		contentPane.add(p_center, BorderLayout.CENTER);
		p_center.setLayout(new BorderLayout(0, 0));
		
		sp_message = new JScrollPane();
		p_center.add(sp_message);
		
		ta_message = new JTextArea();
		ta_message.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		ta_message.setLineWrap(true);
		ta_message.setEditable(false);
		sp_message.setViewportView(ta_message);
		
		p_south = new JPanel();
		contentPane.add(p_south, BorderLayout.SOUTH);
		p_south.setLayout(new GridLayout(0, 1, 0, 0));
		
		tf_message = new JTextField();
		tf_message.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		tf_message.addActionListener(this);
		p_south.add(tf_message);
		tf_message.setColumns(10);
		
		p_east = new JPanel();
		p_east.setPreferredSize(new Dimension(100, 10));
		contentPane.add(p_east, BorderLayout.EAST);
		p_east.setLayout(new BorderLayout(0, 0));
		
		sp_list = new JScrollPane();
		p_east.add(sp_list, BorderLayout.CENTER);
		
		list_user = new JList<>();
		list_user.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		sp_list.setViewportView(list_user);
		
		p_north = new JPanel();
		contentPane.add(p_north, BorderLayout.NORTH);
		
		lbl_title = new JLabel("<" + title + " 채팅방>");
		lbl_title.setFont(new Font("맑은 고딕", Font.PLAIN, 20));
		p_north.add(lbl_title);
		
		lbl_id = new JLabel();
		lbl_id.setFont(new Font("맑은 고딕", Font.PLAIN, 20));
		p_north.add(lbl_id);
		
		setLocation(gui.getX() + 1024, gui.getY());
	}
	
	
}
