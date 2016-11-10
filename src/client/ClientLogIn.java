package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import common.Data;
import common.vo.Human;
import common.vo.Professor;
import common.vo.Student;
import java.awt.Font;

public class ClientLogIn extends JFrame implements ActionListener {
	private JPanel contentPane;
	private JPanel p_main;
	private JPanel p_center;
	private JPanel p_id;
	private JPanel p_pw;
	private JPanel p_south;
	private JButton btn_ok;
	private JButton btn_exit;
	private JLabel lbl_id;
	private JLabel lbl_pw;
	private JTextField tf_id;
	private JTextField tf_pw;
	
	private Socket socket;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private Data data;
	private JPanel panel;
	private JRadioButton radio_student;
	private JRadioButton radio_professor;
	private boolean isOpen = true;
	
	
	public ClientLogIn() {
		drawGUI();
		try {
			socket = new Socket("localhost", 33333);
			ois = new ObjectInputStream(socket.getInputStream());
			oos = new ObjectOutputStream(socket.getOutputStream());
			System.out.println("ClientLogIn> 서버 연결완료. 스트림 개통");
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "서버가 닫혀 있습니다. 관리자에게 문의하세요.");
			System.exit(0);
		}//t-c
	}//const
	
	
	/**
	 * 액션이벤트 처리
	 */
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if(source == btn_ok || source == tf_id || source == tf_pw){
			if(radio_professor.isSelected()){				//교수 로그인
				Data data = new Data(Data.P_LOG_IN);
				Professor p = new Professor();
				p.setRegNo(tf_id.getText().trim());
				p.setPw(tf_pw.getText().trim());
				data.setHuman(p);
				try {
					oos.writeObject(data);
					oos.reset();
					data = (Data) ois.readObject();
					if(data.isConnected()){
						JOptionPane.showMessageDialog(this, "이미 접속중입니다.");
					}else {
						if(data.getServerStatus().get("isCreatable")){
							if((Human) data.getResult() != null){
								new ClientProfessorGUI((Professor) data.getResult(), ois, oos, true);
								dispose();
							}else {
								JOptionPane.showMessageDialog(this, "ID/PW를 확인하세요");
							}
						}else {
							if((Human) data.getResult() != null){
								new ClientProfessorGUI((Professor) data.getResult(), ois, oos, false);
								dispose();
							}else {
								JOptionPane.showMessageDialog(this, "ID/PW를 확인하세요");
							}
						}
					}
				} catch (IOException | ClassNotFoundException e1) {
					e1.printStackTrace();
					System.err.println("ClientLogIn 110라인 에러");
				}
			}else if(radio_student.isSelected()){			//학생 로그인
				Data data = new Data(Data.S_LOG_IN);
				Student s = new Student();
				s.setRegNo(tf_id.getText().trim());
				s.setPw(tf_pw.getText().trim());
				data.setHuman(s);
				try {
					oos.writeObject(data);
					oos.reset();
					data = (Data) ois.readObject(); 
					if(data.isConnected()){
						JOptionPane.showMessageDialog(this, "이미 접속중입니다.");
					}else {
						if(data.getServerStatus().get("isRegisterable")){
							if((Human) data.getResult() != null){
								new ClientStudentGUI((Student) data.getResult(), ois, oos, true);
								dispose();
							}else {
								JOptionPane.showMessageDialog(this, "ID/PW를 확인하세요");
							}
						}else {
							if((Human) data.getResult() != null){
								new ClientStudentGUI((Student) data.getResult(), ois, oos, false);
								dispose();
							}else {
								JOptionPane.showMessageDialog(this, "ID/PW를 확인하세요");
							}
						}
					}
				} catch (IOException | ClassNotFoundException e1) {
					e1.printStackTrace();
				}
			}
		}else if (source == btn_exit){					//종료
			System.exit(0);
		}//if-else
	}//actionPerformed()
	
	
	//이 아래는 main(), drawGUI()뿐
	
	public static void main(String[] args) throws Exception{
		UIManager.setLookAndFeel("com.jtattoo.plaf.aluminium.AluminiumLookAndFeel");
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientLogIn frame = new ClientLogIn();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}//main
	
	private void drawGUI() {
		setTitle("\uB85C\uADF8\uC778");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 223, 206);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		p_main = new JPanel();
		contentPane.add(p_main, BorderLayout.CENTER);
		p_main.setLayout(new BorderLayout(0, 0));
		
		p_center = new JPanel();
		p_main.add(p_center, BorderLayout.CENTER);
		p_center.setLayout(new GridLayout(0, 1, 0, 0));
		
		p_id = new JPanel();
		p_center.add(p_id);
		
		lbl_id = new JLabel("ID");
		lbl_id.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		lbl_id.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_id.setPreferredSize(new Dimension(24, 18));
		p_id.add(lbl_id);
		
		tf_id = new JTextField();
		tf_id.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		tf_id.addActionListener(this);
		p_id.add(tf_id);
		tf_id.setColumns(8);
		
		p_pw = new JPanel();
		p_center.add(p_pw);
		
		lbl_pw = new JLabel("PW");
		lbl_pw.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		p_pw.add(lbl_pw);
		
		tf_pw = new JPasswordField();
		tf_pw.setFont(new Font("굴림", Font.PLAIN, 17));
		tf_pw.addActionListener(this);
		p_pw.add(tf_pw);
		tf_pw.setColumns(9);
		
		panel = new JPanel();
		p_center.add(panel);
		
		radio_student = new JRadioButton("\uD559\uC0DD");
		radio_student.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		panel.add(radio_student);
		
		radio_professor = new JRadioButton("\uAD50\uC218");
		radio_professor.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		panel.add(radio_professor);
		
		ButtonGroup bg = new ButtonGroup();
		bg.add(radio_professor);
		bg.add(radio_student);
		
		radio_student.setSelected(true);
		
		p_south = new JPanel();
		p_main.add(p_south, BorderLayout.SOUTH);
		p_south.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		btn_ok = new JButton("\uB85C\uADF8\uC778");
		btn_ok.setPreferredSize(new Dimension(85, 40));
		btn_ok.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		btn_ok.addActionListener(this);
		p_south.add(btn_ok);
		
		btn_exit = new JButton("\uC885\uB8CC");
		btn_exit.setPreferredSize(new Dimension(70, 40));
		btn_exit.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		btn_exit.addActionListener(this);
		p_south.add(btn_exit);
		
		setVisible(true);
	}//drawGUI()


}//class
