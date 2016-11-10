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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Dimension;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.FlowLayout;

public class ProfessorCreateDialog extends JDialog implements ActionListener {

	private final JPanel contentPanel = new JPanel();
	private JTextField tf_subject;
	private ClientProfessorGUI gui;
	private JComboBox<String> combo_grade;
	private JComboBox<String> combo_type;
	private JComboBox<String> combo_maxNum;
	private JButton btn_cancel;
	private JButton btn_create;
	private JComboBox<String> combo_credit;
	private JComboBox<String> combo_room;
	private JComboBox<String> combo_time;
	private JComboBox<String> combo_weekday;
	private Professor p;
	private JComboBox<String> combo_major;
	private JTextArea ta_plan;

	public ProfessorCreateDialog(ClientProfessorGUI gui, Professor p) {
		this.gui = gui;
		this.p = p;
		drawGUI();
	}
	
	public void actionPerformed(ActionEvent arg0) {
		Object source = arg0.getSource();
		if(source == btn_create){
			if(!tf_subject.getText().trim().equals("")){
				Lecture l = new Lecture();
				Date date = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("hhmmss");
				String seed = sdf.format(date);
				String subjectCode = p.getName().substring(0, 1) + tf_subject.getText().trim().substring(0, 1) + seed;
				l.setSubjectCode(subjectCode);
				l.setSubject(tf_subject.getText().trim());
				l.setMajor((String) combo_major.getSelectedItem());
				l.setType((String) combo_type.getSelectedItem());
				l.setGrade((String) combo_grade.getSelectedItem());
				l.setMaxStudentNum(Integer.parseInt((String) combo_maxNum.getSelectedItem()));
				l.setNowStudentNum(0);
				l.setTime((String)combo_weekday.getSelectedItem() + "-" + combo_time.getSelectedItem());
				l.setRoom((String) combo_room.getSelectedItem());
				l.setPlan(ta_plan.getText());
				
				Data data = new Data(Data.P_CREATE_LECTURE);
				data.setLecture(l);
				data.setHuman(p);
				gui.sendData(data);
				dispose();
			}else {
				JOptionPane.showMessageDialog(this, "강의명을 입력하세요");
			}
		}else if (source == btn_cancel){
			dispose();
		}
	}

	private void drawGUI() {
		setTitle("\uAC15\uC758\uAC1C\uC124");
		setBounds(100, 100, 653, 427);
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
		JPanel p_grade = new JPanel();
		FlowLayout flowLayout = (FlowLayout) p_grade.getLayout();
		flowLayout.setHgap(15);
		p_main.add(p_grade);
		
		JLabel lbl_major = new JLabel("\uD559\uACFC");
		lbl_major.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		p_grade.add(lbl_major);
		
		combo_major = new JComboBox<>();
		combo_major.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		combo_major.setModel(new DefaultComboBoxModel(new String[] {"\uAC04\uD638\uD559\uACFC", "\uAC74\uCD95\uD559\uACFC", "\uACBD\uC601\uD559\uACFC", "\uC77C\uC5B4\uC77C\uBB38\uD559\uACFC", "\uCEF4\uD4E8\uD130\uACF5\uD559\uACFC"}));
		p_grade.add(combo_major);
		JLabel lbl_grade = new JLabel("\uD559\uB144");
		lbl_grade.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		p_grade.add(lbl_grade);
		combo_grade = new JComboBox<>();
		combo_grade.setPreferredSize(new Dimension(50, 30));
		combo_grade.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		combo_grade.setModel(new DefaultComboBoxModel(new String[] {"1", "2", "3", "4"}));
		p_grade.add(combo_grade);
		JPanel p_type = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) p_type.getLayout();
		flowLayout_1.setHgap(15);
		p_main.add(p_type);
		JLabel lbl_type = new JLabel("\uC804\uACF5\uC5EC\uBD80");
		lbl_type.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		p_type.add(lbl_type);
		combo_type = new JComboBox<>();
		combo_type.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		combo_type.setModel(new DefaultComboBoxModel(new String[] {"\uC804\uACF5", "\uAD50\uC591"}));
		p_type.add(combo_type);
		JPanel p_subject = new JPanel();
		FlowLayout flowLayout_2 = (FlowLayout) p_subject.getLayout();
		flowLayout_2.setHgap(15);
		p_main.add(p_subject);
		JLabel lbl_subject = new JLabel("\uACFC\uBAA9\uBA85");
		lbl_subject.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		p_subject.add(lbl_subject);
		tf_subject = new JTextField();
		tf_subject.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		p_subject.add(tf_subject);
		tf_subject.setColumns(15);
		JPanel p_maxNum = new JPanel();
		FlowLayout flowLayout_3 = (FlowLayout) p_maxNum.getLayout();
		flowLayout_3.setHgap(15);
		p_main.add(p_maxNum);
		JLabel lbl_maxNum = new JLabel("\uC815\uC6D0");
		lbl_maxNum.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		p_maxNum.add(lbl_maxNum);
		combo_maxNum = new JComboBox<>();
		combo_maxNum.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		combo_maxNum.setModel(new DefaultComboBoxModel(new String[] {"10", "30", "50", "100"}));
		p_maxNum.add(combo_maxNum);
		JPanel p_time = new JPanel();
		FlowLayout flowLayout_4 = (FlowLayout) p_time.getLayout();
		flowLayout_4.setHgap(15);
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
		FlowLayout flowLayout_5 = (FlowLayout) p_room.getLayout();
		flowLayout_5.setHgap(15);
		p_main.add(p_room);
		JLabel lbl_room = new JLabel("\uAC15\uC758\uC2E4");
		lbl_room.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		p_room.add(lbl_room);
		combo_room = new JComboBox<>();
		combo_room.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		combo_room.setModel(new DefaultComboBoxModel(new String[] {"101", "102", "103", "201", "202", "203", "301", "302", "303"}));
		p_room.add(combo_room);
		JPanel p_credit = new JPanel();
		FlowLayout flowLayout_6 = (FlowLayout) p_credit.getLayout();
		flowLayout_6.setHgap(15);
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
		FlowLayout flowLayout_7 = (FlowLayout) p_buttons.getLayout();
		flowLayout_7.setHgap(15);
		p_main.add(p_buttons);
		btn_create = new JButton("\uAC15\uC758 \uAC1C\uC124");
		btn_create.setPreferredSize(new Dimension(110, 40));
		btn_create.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		btn_create.addActionListener(this);
		p_buttons.add(btn_create);
		btn_cancel = new JButton("\uCDE8\uC18C");
		btn_cancel.setPreferredSize(new Dimension(70, 40));
		btn_cancel.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		btn_cancel.addActionListener(this);
		p_buttons.add(btn_cancel);		
		
		JPanel p_south = new JPanel();
		p_south.setPreferredSize(new Dimension(300, 10));
		contentPanel.add(p_south, BorderLayout.EAST);
		p_south.setLayout(new BorderLayout(0, 0));
		
		ta_plan = new JTextArea();
		ta_plan.setFont(new Font("함초롬돋움", Font.PLAIN, 17));
		ta_plan.setLineWrap(true);
		JScrollPane sp = new JScrollPane();
		sp.setViewportView(ta_plan);
		p_south.add(sp);
		
		JLabel lbl_plan = new JLabel("\uAC15\uC758\uACC4\uD68D\uC11C (1000\uC790 \uC774\uB0B4)");
		lbl_plan.setPreferredSize(new Dimension(166, 30));
		lbl_plan.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		lbl_plan.setHorizontalAlignment(SwingConstants.CENTER);
		p_south.add(lbl_plan, BorderLayout.NORTH);
		
		
		combo_major.setSelectedItem(p.getMajor());
		combo_major.setEnabled(false);
		
		setVisible(true);
	}

}
