package server.dialog;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import common.Data;
import server.ServerGUI;
import server.ServerThread;

import javax.swing.SwingConstants;
import java.awt.Dimension;
import java.awt.Font;

public class AdminDialog extends JDialog implements ActionListener{

	private final JPanel p_main = new JPanel();
	private JButton btn_creatable;
	private JButton btn_registerable;
	private JButton btn_allclose;
	private ServerGUI gui;
	private JLabel lbl_title;
	private JLabel lbl_open;
	private JLabel lbl_register;
	private ServerThread st;

	public AdminDialog(ServerGUI gui, ServerThread st) {
		this.st = st;
		this.gui = gui;
		drawGUI();
	}

	private void drawGUI() {
		setTitle("학사관리");
		setBounds(100, 100, 245, 278);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setAlwaysOnTop(true);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				gui.setAd(null);
				dispose();
			}
		});
		getContentPane().setLayout(new BorderLayout());
		p_main.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(p_main, BorderLayout.CENTER);
		p_main.setLayout(new GridLayout(0, 1, 0, 0));
		JPanel p_title = new JPanel();
		p_main.add(p_title);
		p_title.setLayout(new GridLayout(0, 1, 0, 0));
		lbl_title = new JLabel("<현재상태>");
		lbl_title.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		lbl_title.setHorizontalAlignment(SwingConstants.CENTER);
		p_title.add(lbl_title);
		
		if(gui.isCreatable()){
			lbl_open = new JLabel("강의개설 : 열림");
			btn_creatable = new JButton("강의 개설 기간 닫기");
		}else {
			lbl_open = new JLabel("강의개설 : 닫힘");
			btn_creatable = new JButton("강의 개설 기간 열기");
		}
		lbl_open.setHorizontalAlignment(SwingConstants.CENTER);
		p_title.add(lbl_open);
		
		if(gui.isRegisterable()){
			lbl_register = new JLabel("수강신청 : 열림");
			btn_registerable = new JButton("수강 신청 기간 닫기");
		}else {
			lbl_register = new JLabel("수강신청 : 닫힘");
			btn_registerable = new JButton("수강 신청 기간 열기");
		}
		lbl_register.setHorizontalAlignment(SwingConstants.CENTER);
		p_title.add(lbl_register);
		JPanel p_open = new JPanel();
		p_main.add(p_open);
		btn_creatable.setPreferredSize(new Dimension(159, 40));
		btn_creatable.addActionListener(this);
		p_open.add(btn_creatable);
		JPanel p_register = new JPanel();
		p_main.add(p_register);
		btn_registerable.setPreferredSize(new Dimension(159, 40));
		btn_registerable.addActionListener(this);
		p_register.add(btn_registerable);
		JPanel p_allclose = new JPanel();
		p_main.add(p_allclose);
		btn_allclose = new JButton("모두 닫기");
		btn_allclose.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		btn_allclose.setPreferredSize(new Dimension(110, 40));
		btn_allclose.addActionListener(this);
		p_allclose.add(btn_allclose);		
		setLocation(gui.getX() + 890, gui.getY());
		
		btn_registerable.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		btn_registerable.setPreferredSize(new Dimension(200, 40));
		btn_creatable.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		btn_creatable.setPreferredSize(new Dimension(200, 40));
		lbl_open.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		lbl_register.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		
		
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if(source == btn_creatable){
			if(gui.isCreatable()){
				gui.setCreatable(false);
				lbl_open.setText("강의개설 : 닫힘");
				btn_creatable.setText("강의 개설 기간 열기");
				JOptionPane.showMessageDialog(this, "강의개설기간 종료");
				noticeToProfessor();
				gui.appendLog("강의개설기간 닫았음");
			}else if(!gui.isCreatable() && !gui.isRegisterable()){
				gui.setCreatable(true);
				lbl_open.setText("강의개설 : 열림");
				btn_creatable.setText("강의 개설 기간 닫기");
				JOptionPane.showMessageDialog(this, "강의개설기간 시작");
				noticeToProfessor();
				gui.appendLog("강의개설기간 열었음");
			}else {
				JOptionPane.showMessageDialog(this, "수강신청 기간입니다.\n먼저 수강신청기간을 닫아주세요.");
			}
		}else if (source == btn_registerable){
			if(gui.isRegisterable()){
				gui.setRegisterable(false);
				lbl_register.setText("수강신청 : 닫힘");
				btn_registerable.setText("수강 신청 기간 열기");
				JOptionPane.showMessageDialog(this, "수강신청기간 종료");
				noticeToStudent();
				gui.appendLog("수강신청기간 닫았음");
			}else if (!gui.isCreatable() && !gui.isRegisterable()){
				gui.setRegisterable(true);
				lbl_register.setText("수강신청 : 열림");
				btn_registerable.setText("수강 신청 기간 닫기");
				JOptionPane.showMessageDialog(this, "수강신청기간 시작");
				noticeToStudent();
				gui.appendLog("수강신청기간 열었음");
			}else {
				JOptionPane.showMessageDialog(this, "강의개설 기간입니다.\n먼저 강의개설기간을 닫아주세요.");
			}
		}
		
		if (source == btn_allclose){
			gui.setRegisterable(false);
			gui.setCreatable(false);
			lbl_register.setText("수강신청 : 닫힘");
			btn_registerable.setText("수강 신청 기간 열기");
			lbl_open.setText("강의개설 : 닫힘");
			btn_creatable.setText("강의 개설 기간 열기");
			JOptionPane.showMessageDialog(this, "모든 기간 종료");
			noticeToStudent();
			noticeToProfessor();
			gui.appendLog("모두 닫았음");
		}
		
	}
	
	public void noticeToStudent(){
		if(st != null){
			st.broadCastToStudent();
		}
	}
	
	public void noticeToProfessor(){
		if(st != null){
			st.broadCastToProfessor();
		}
	}

	public void setSt(ServerThread st) {
		this.st = st;
	}
	
	

}
