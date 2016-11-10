package client.dialog;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import client.ClientProfessorGUI;
import common.Data;
import common.vo.Lecture;
import common.vo.Professor;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.Dimension;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

public class ProfessorUpdateDialog extends JDialog implements ActionListener {

	private final JPanel contentPanel = new JPanel();
	private JTextField tf_subject;
	private JButton btn_cancel;
	private JButton btn_update;
	private ClientProfessorGUI gui;
	private Lecture l;
	private JComboBox<String> combo_time;
	private JComboBox<String> combo_room;
	private JComboBox<String> combo_credit;
	private JComboBox<String> combo_maxNum;
	private JComboBox<String> combo_weekday;
	private JTextArea ta_plan;

	public ProfessorUpdateDialog(ClientProfessorGUI gui, Lecture l, Professor p) {
		this.gui = gui;
		l.setProfessor(p);
		this.l = l;	
		drawGUI();
		makeItPosition();
	}
	
	
	
	private void makeItPosition() {		//정원 요일 교시 강의실 학점(어차피3)
		tf_subject.setText(l.getSubject());
		ta_plan.setText(l.getPlan());
		combo_maxNum.setSelectedItem(String.valueOf(l.getMaxStudentNum()));
		combo_weekday.setSelectedItem(l.getTime().substring(0, 1));
		combo_time.setSelectedItem(l.getTime().substring(2, 7));
		combo_room.setSelectedItem(l.getRoom());
		setVisible(true);
	}



	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		
		if(source == btn_cancel){
			dispose();
		}else if(source == btn_update){
			if(!tf_subject.getText().trim().equals("")){
				if(tf_subject.getText().length() < 1000){
					Data data = new Data(Data.P_UPDATE_LECTURE);
					Date date = new Date();
					SimpleDateFormat sdf = new SimpleDateFormat("hhmmss");
					String seed = sdf.format(date);
					String subjectCode = l.getProfessor().getName().substring(0, 1) + tf_subject.getText().trim().substring(0, 1) + seed;
					l.setOldCode(l.getSubjectCode());
					l.setSubjectCode(subjectCode);
					l.setSubject(tf_subject.getText().trim());
					l.setMaxStudentNum(Integer.parseInt((String) combo_maxNum.getSelectedItem()));
					l.setRoom((String) combo_room.getSelectedItem());
					l.setTime((String)combo_weekday.getSelectedItem() + "-" + combo_time.getSelectedItem());
					l.setPlan(ta_plan.getText());
					data.setLecture(l);
					gui.sendData(data);
					dispose();
				}else {
					JOptionPane.showMessageDialog(this, "강의계획서는 1000자까지 입력가능합니다.\n현재 : " + ta_plan.getText().length() + "자");
				}
			}else {
				JOptionPane.showMessageDialog(this, "강의명은 꼭 입력하세요");
			}
		}
	}
	

	private void drawGUI() {
		setTitle("강의 수정");
		setBounds(100, 100, 674, 337);
		setLocationRelativeTo(gui);
		setModal(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		JPanel p_main = new JPanel();
		contentPanel.add(p_main, BorderLayout.CENTER);
		p_main.setLayout(new GridLayout(0, 1, 0, 0));
		JPanel p_subject = new JPanel();
		FlowLayout flowLayout = (FlowLayout) p_subject.getLayout();
		flowLayout.setHgap(15);
		p_main.add(p_subject);
		JLabel lbl_subject = new JLabel("\uACFC\uBAA9\uBA85");
		lbl_subject.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		p_subject.add(lbl_subject);
		tf_subject = new JTextField();
		tf_subject.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		p_subject.add(tf_subject);
		tf_subject.setColumns(15);
		JPanel p_maxNum = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) p_maxNum.getLayout();
		flowLayout_1.setHgap(15);
		p_main.add(p_maxNum);
		JLabel lbl_maxNum = new JLabel("\uC815\uC6D0");
		lbl_maxNum.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		p_maxNum.add(lbl_maxNum);
		combo_maxNum = new JComboBox<>();
		combo_maxNum.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		combo_maxNum.setModel(new DefaultComboBoxModel(new String[] {"10", "30", "50", "100"}));
		p_maxNum.add(combo_maxNum);
		JPanel p_time = new JPanel();
		FlowLayout flowLayout_2 = (FlowLayout) p_time.getLayout();
		flowLayout_2.setHgap(15);
		p_main.add(p_time);
		JLabel lbl_weekday = new JLabel("\uC694\uC77C");
		lbl_weekday.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		p_time.add(lbl_weekday);
		combo_weekday = new JComboBox<>();
		combo_weekday.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		combo_weekday.setModel(new DefaultComboBoxModel(new String[] {"\uC6D4", "\uD654", "\uC218", "\uBAA9", "\uAE08"}));
		p_time.add(combo_weekday);
		
		JLabel lbl_time = new JLabel("\uAD50\uC2DC");
		lbl_time.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		p_time.add(lbl_time);
		
		combo_time = new JComboBox<>();
		combo_time.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		combo_time.setModel(new DefaultComboBoxModel(new String[] {"1,2,3", "4,5,6", "7,8,9"}));
		p_time.add(combo_time);
		JPanel p_room = new JPanel();
		FlowLayout flowLayout_3 = (FlowLayout) p_room.getLayout();
		flowLayout_3.setHgap(15);
		p_main.add(p_room);
		JLabel lbl_room = new JLabel("\uAC15\uC758\uC2E4");
		lbl_room.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		p_room.add(lbl_room);
		combo_room = new JComboBox<>();
		combo_room.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		combo_room.setModel(new DefaultComboBoxModel(new String[] {"101", "102", "103", "201", "202", "203", "301", "302", "303"}));
		p_room.add(combo_room);
		JPanel p_credit = new JPanel();
		FlowLayout flowLayout_4 = (FlowLayout) p_credit.getLayout();
		flowLayout_4.setHgap(15);
		p_main.add(p_credit);
		JLabel lbl_credit = new JLabel("\uD559\uC810");
		lbl_credit.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		p_credit.add(lbl_credit);
		combo_credit = new JComboBox<>();
		combo_credit.setPreferredSize(new Dimension(50, 30));
		combo_credit.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		combo_credit.setModel(new DefaultComboBoxModel(new String[] {"3"}));
		p_credit.add(combo_credit);
		JPanel p_buttons = new JPanel();
		FlowLayout flowLayout_5 = (FlowLayout) p_buttons.getLayout();
		flowLayout_5.setHgap(15);
		p_main.add(p_buttons);
		btn_update = new JButton("\uAC15\uC758 \uC218\uC815");
		btn_update.setPreferredSize(new Dimension(115, 40));
		btn_update.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		btn_update.addActionListener(this);
		p_buttons.add(btn_update);
		btn_cancel = new JButton("\uCDE8\uC18C");
		btn_cancel.setPreferredSize(new Dimension(70, 40));
		btn_cancel.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		btn_cancel.addActionListener(this);
		p_buttons.add(btn_cancel);		
		
		JPanel p_east = new JPanel();
		p_east.setPreferredSize(new Dimension(300, 10));
		getContentPane().add(p_east, BorderLayout.EAST);
		p_east.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		p_east.add(scrollPane, BorderLayout.CENTER);
		
		ta_plan = new JTextArea();
		ta_plan.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
		scrollPane.setViewportView(ta_plan);
		
		JLabel lblNewLabel = new JLabel("\uAC15\uC758\uACC4\uD68D\uC11C (1000\uC790 \uC774\uB0B4)");
		lblNewLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		p_east.add(lblNewLabel, BorderLayout.NORTH);
	}

}
