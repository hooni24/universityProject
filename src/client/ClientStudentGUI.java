package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import client.dialog.CommonPasswordChange;
import client.dialog.CommonTimeTableDialog;
import common.ChatData;
import common.Data;
import common.vo.Lecture;
import common.vo.Professor;
import common.vo.Student;
import java.awt.FlowLayout;

public class ClientStudentGUI extends JFrame implements ActionListener {

	private Student student;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private CommonPasswordChange cpc;
	private boolean isRegisterable;
	private ClientStudentGUI gui = this;

	public ClientStudentGUI(Student student, ObjectInputStream ois, ObjectOutputStream oos, boolean isRegisterable) {
		this.isRegisterable = isRegisterable;
		this.student = student;
		this.ois = ois;
		this.oos = oos;
		drawGUI();
		setButton(isRegisterable);
		cst = new ClientStudentThread(this, ois);
		new Thread(cst).start();
		Data data = new Data(Data.S_SEARCH_MY_LECTURE);
		data.setHuman(student);
		sendData(data);
	}
	
	public void setButton(boolean isRegisterable){
		if(!isRegisterable){
			btn_register.setEnabled(false);
			btn_drop.setEnabled(false);
		}else {
			btn_register.setEnabled(true);
			btn_drop.setEnabled(true);
		}
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if(source == btn_search || source == tf_pname || source == tf_subject){		//검색
			Data data = new Data(Data.S_SEARCH_LECTURE);
			Lecture l = new Lecture();
			l.setSubject(tf_subject.getText().trim());
			l.setMajor((String) combo_major.getSelectedItem());
			l.setGrade((String) combo_grade.getSelectedItem());
			l.setType((String) combo_type.getSelectedItem());
			l.setProfessor(new Professor(tf_pname.getText().trim(), null, null));
			data.setLecture(l);
			sendData(data);
		}
		if(source == btn_register){
			if(table_subject.getSelectedRow() >= 0){
				Lecture l = new Lecture();
				l.setSubjectCode((String)table_subject.getValueAt(table_subject.getSelectedRow(), 0));
				String[] countArray = String.valueOf(table_subject.getValueAt(table_subject.getSelectedRow(), 6)).split("/");
				l.setVacantStudentNum(Integer.parseInt(countArray[0]));
				l.setTime((String) table_subject.getValueAt(table_subject.getSelectedRow(), 8));
				Data d = new Data(Data.S_REGISTER_LECTURE);
				student.setNowCredit(Integer.parseInt(lbl_credit.getText().substring(lbl_credit.getText().length()-2).trim()));
				d.setHuman(student);
				d.setLecture(l);
				sendData(d);
				btn_search.doClick();
				
				Data data = new Data(Data.S_SEARCH_MY_LECTURE);
				data.setHuman(student);
				sendData(data);
				setCredit();
			}else {
				JOptionPane.showMessageDialog(this, "신청할 과목을 선택하세요");
			}
		}
		if(source == btn_drop){
			if(table_my.getSelectedRow() >= 0){
				Data data = new Data(Data.S_DROP_LECTURE);
				Lecture lecture = new Lecture();
				lecture.setSubjectCode((String) table_my.getValueAt(table_my.getSelectedRow(), 0));
				data.setLecture(lecture);
				data.setHuman(student);
				sendData(data);
				btn_refresh.doClick();
				setCredit();
			}else {
				JOptionPane.showMessageDialog(this, "취소할 과목을 선택하세요");
			}
		}
		
		if(source == btn_timeTable){
			cttd = new CommonTimeTableDialog(this, student);
		}
		
		if(source == btn_refresh){
			Data data = new Data(Data.S_SEARCH_MY_LECTURE);
			data.setHuman(student);
			sendData(data);
			setCredit();
		}
		
		if(source == btn_changepw){
			cpc = new CommonPasswordChange(this, student);
			cpc.setVisible(true);
		}
		if(source == btn_plan){
			if(table_subject.getSelectedRow() >= 0){
				Data data = new Data(Data.S_GET_A_LECTURE_WITH_PLAN);
				Lecture l = new Lecture();
				l.setSubjectCode((String) table_subject.getValueAt(table_subject.getSelectedRow(), 0));
				l.setSubject((String) table_subject.getValueAt(table_subject.getSelectedRow(), 1));
				l.setProfessor(new Professor((String) table_subject.getValueAt(table_subject.getSelectedRow(), 7), null, null));
				data.setLecture(l);
				sendData(data);
			}else {
				JOptionPane.showMessageDialog(this, "과목을 선택하세요");
			}
		}
	}
	
	public void disposePassword(boolean b){
		if(b) {
			cpc.dispose();
			JOptionPane.showMessageDialog(cpc, "성공적으로 변경되었습니다.");
		}else {
			JOptionPane.showMessageDialog(cpc, "어떤 상황이었는지 개발자에게 알려주세요");
		}
	}
	
	/**
	 * 학생GUI에서 학점 세팅요청하는 메소드
	 */
	public void setCredit(){
		Data data = new Data(Data.S_GET_MY_CREDIT);
		data.setHuman(student);
		sendData(data);
	}
	
	/**
	 * 학생GUI에서 학점 세팅요청해서 받아온 학점을 실제 세팅하는 메소드 
	 * @param credit
	 */
	public void setCredit(int credit){
		lbl_credit.setText("현재 수강학점 : " + credit);
	}
	
	/**
	 * 스레드에서도 쓰고 GUI도 쓰는 sendData
	 * 발송전용
	 * @param data
	 */
	public void sendData(Data data){
		try {
			oos.writeObject(data);
			oos.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setTimeTable(ArrayList<Lecture> lList){
		cttd.setTimeTable(lList);
		cttd.setVisible(true);
	}
	
	public void setLectureTable(ArrayList<Lecture> lectureList){
		Object[][] tableData = new Object[lectureList.size()][10];			//2차원배열 생성 (테이블 내용물)
		for (int i = 0; i < tableData.length; i++) {						//for문 돌면서 초기화
			tableData[i][0] = lectureList.get(i).getSubjectCode();
			tableData[i][1] = lectureList.get(i).getSubject();
			tableData[i][2] = lectureList.get(i).getMajor();
			tableData[i][3] = lectureList.get(i).getType();
			tableData[i][4] = lectureList.get(i).getGrade();
			tableData[i][5] = lectureList.get(i).getCredit();
			tableData[i][6] = lectureList.get(i).getMaxStudentNum() - lectureList.get(i).getNowStudentNum() + "/" + lectureList.get(i).getMaxStudentNum();
			tableData[i][7] = lectureList.get(i).getProfessor().getName();
			tableData[i][8] = lectureList.get(i).getTime();
			tableData[i][9] = lectureList.get(i).getRoom();
		}
		
		tm = new TableModel(tableData, columnNames);
		table_subject= new JTable(tm);
		table_subject.setRowHeight(30);
		table_subject.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
		table_subject.addMouseListener(new TableMouseEventHandler());
		table_subject.setFillsViewportHeight(true);
		table_subject.getColumnModel().getColumn(0).setPreferredWidth(120);
		table_subject.getColumnModel().getColumn(1).setPreferredWidth(250);
		table_subject.getColumnModel().getColumn(2).setPreferredWidth(100);
		table_subject.getColumnModel().getColumn(3).setPreferredWidth(100);
		table_subject.getColumnModel().getColumn(4).setPreferredWidth(40);
		table_subject.getColumnModel().getColumn(5).setPreferredWidth(40);
		table_subject.getColumnModel().getColumn(0).setResizable(false);
		table_subject.getColumnModel().getColumn(1).setResizable(false);
		table_subject.getColumnModel().getColumn(2).setResizable(false);
		table_subject.getColumnModel().getColumn(3).setResizable(false);
		table_subject.getColumnModel().getColumn(4).setResizable(false);
		table_subject.getColumnModel().getColumn(5).setResizable(false);
		table_subject.getColumnModel().getColumn(6).setResizable(false);
		table_subject.getColumnModel().getColumn(7).setResizable(false);
		table_subject.getColumnModel().getColumn(8).setResizable(false);
		table_subject.getColumnModel().getColumn(9).setResizable(false);
		
		(table_subject.getTableHeader()).setReorderingAllowed(false);
		sp_subject.setViewportView(table_subject);
	}
	
	public void setMyLectureTable(ArrayList<Lecture> lectureList){
		Object[][] tableData = new Object[lectureList.size()][10];			//2차원배열 생성 (테이블 내용물)
		for (int i = 0; i < tableData.length; i++) {						//for문 돌면서 초기화
			tableData[i][0] = lectureList.get(i).getSubjectCode();
			tableData[i][1] = lectureList.get(i).getSubject();
			tableData[i][2] = lectureList.get(i).getMajor();
			tableData[i][3] = lectureList.get(i).getType();
			tableData[i][4] = lectureList.get(i).getGrade();
			tableData[i][5] = lectureList.get(i).getCredit();
			tableData[i][6] = lectureList.get(i).getMaxStudentNum() - lectureList.get(i).getNowStudentNum() + "/" + lectureList.get(i).getMaxStudentNum();
			tableData[i][7] = lectureList.get(i).getProfessor().getName();
			tableData[i][8] = lectureList.get(i).getTime();
			tableData[i][9] = lectureList.get(i).getRoom();
		}
		
		tm = new TableModel(tableData, columnNames);
		table_my = new JTable(tm);
		table_my.setRowHeight(30);
		table_my.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
		table_my.addMouseListener(new TableMouseEventHandler());
		table_my.setFillsViewportHeight(true);
		table_my.getColumnModel().getColumn(0).setPreferredWidth(120);
		table_my.getColumnModel().getColumn(1).setPreferredWidth(250);
		table_my.getColumnModel().getColumn(2).setPreferredWidth(100);
		table_my.getColumnModel().getColumn(3).setPreferredWidth(100);
		table_my.getColumnModel().getColumn(4).setPreferredWidth(40);
		table_my.getColumnModel().getColumn(5).setPreferredWidth(40);
		table_my.getColumnModel().getColumn(0).setResizable(false);
		table_my.getColumnModel().getColumn(1).setResizable(false);
		table_my.getColumnModel().getColumn(2).setResizable(false);
		table_my.getColumnModel().getColumn(3).setResizable(false);
		table_my.getColumnModel().getColumn(4).setResizable(false);
		table_my.getColumnModel().getColumn(5).setResizable(false);
		table_my.getColumnModel().getColumn(6).setResizable(false);
		table_my.getColumnModel().getColumn(7).setResizable(false);
		table_my.getColumnModel().getColumn(8).setResizable(false);
		table_my.getColumnModel().getColumn(9).setResizable(false);
		
		(table_my.getTableHeader()).setReorderingAllowed(false);
		sp_my.setViewportView(table_my);
	}

	// 이 아래는 drawGUI()

	private JPanel contentPane;
	private JPanel p_main;
	private JPanel p_north;
	private JComboBox combo_major;
	private JComboBox combo_grade;
	private JComboBox combo_type;
	private JLabel lbl_major;
	private JLabel lbl_grade;
	private JLabel lbl_type;
	private JPanel p_center;
	private JPanel p_c_c_center;
	private JPanel p_c_east;
	private JLabel lbl_professor;
	private JTextField tf_pname;
	private JButton btn_search;
	private JScrollPane sp_subject;
	private JTable table_subject;
	private JPanel p_c_center;
	private JPanel p_c_c_south;
	private JScrollPane sp_my;
	private JTable table_my;
	private JPanel p_e_top;
	private JPanel p_e_bottom;
	private JButton btn_register;
	private JButton btn_plan;
	private JButton btn_refresh;
	private JButton btn_drop;
	private JButton btn_timeTable;
	private JLabel lbl_credit;

	private Object[][] tableData = new Object[0][0];
	private String[] columnNames = { "과목코드", "과목명", "학과", "전공여부", "학년", "학점", "여석", "교수명", "시간", "강의실" };
	private TableModel tm;
	private JLabel lbl_subject;
	private JTextField tf_subject;
	private CommonTimeTableDialog cttd;
	private JButton btn_changepw;
	private JPanel p_east;
	private JScrollPane scrollPane;
	private JLabel lbl_chat;
	private JList list;
	private String[] roomName = {"간호학과", "건축학과", "경영학과", "일어일문학과", "컴퓨터공학과"};
	private ClientStudentChat csc;
	private ClientStudentThread cst;
	private JPanel panel;

	class TableModel extends DefaultTableModel { // Table을 다루기 위한 inner class
		public TableModel(Object[][] defaultRowData, Object[] defaultColumnNames) {
			super.setDataVector(defaultRowData, defaultColumnNames);
		}
		public boolean isCellEditable(int rowIndex, int columnIndex) { // 셀 수정
			return false;
		}
	}

	class TableMouseEventHandler extends MouseAdapter {
		public void mouseClicked(MouseEvent me) {
			if(btn_register.isEnabled()){
				if(me.getClickCount() == 2){
					if(me.getComponent() == table_subject){
						btn_register.doClick();
					}
				}
			}//테이블
			
			if(me.getClickCount() == 2){
				if(me.getComponent() == list){
					if(csc == null){
						csc = new ClientStudentChat(gui, (String)list.getSelectedValue(), student.getName(), student.getRegNo());
						JOptionPane.showMessageDialog(csc, "어서오세요");
						Data data = new Data(Data.S_CHAT_JOIN);
						ChatData chatData = new ChatData();
						chatData.setName(student.getName());
						chatData.setTitle((String)list.getSelectedValue());
						chatData.setRegNo(student.getRegNo());
						data.setChatData(chatData);
						sendData(data);
					}
				}
			}//리스트
			
		}// mouserClicked()
	}// mouseAdapter innerClass
	
	public void setCSCNULL(){
		csc = null;
	}
	
	public void cscUserList(ArrayList userList){
		csc.setUserList(userList);
	}
	
	public void appendChat(String name, String message){
		csc.appendChat(name, message);
	}
	
	public void setName(String name){
		csc.setName(name);
	}

	public void drawGUI() {
		setTitle("\uC218\uAC15\uC2E0\uCCAD - \uD559\uC0DD\uC6A9");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 1290, 834);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				int a = JOptionPane.showConfirmDialog(gui, "종료하시겠습니까?", "종료", JOptionPane.YES_NO_OPTION);
				if(a == JOptionPane.OK_OPTION){
					Data data = new Data(Data.C_LOG_OUT);
					data.setHuman(student);
					sendData(data);
					System.exit(0);
				}
			}
		});

		p_main = new JPanel();
		contentPane.add(p_main, BorderLayout.CENTER);
		p_main.setLayout(new BorderLayout(0, 0));

		p_north = new JPanel();
		FlowLayout flowLayout = (FlowLayout) p_north.getLayout();
		flowLayout.setHgap(15);
		p_main.add(p_north, BorderLayout.NORTH);

		lbl_major = new JLabel("\uD559\uACFC");
		lbl_major.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		p_north.add(lbl_major);

		combo_major = new JComboBox();
		combo_major.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		combo_major.setModel(new DefaultComboBoxModel(new String[] {"전체", "간호학과", "건축학과", "경영학과", "일어일문학과", "컴퓨터공학과"}));
		p_north.add(combo_major);

		lbl_grade = new JLabel("\uD559\uB144");
		lbl_grade.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		p_north.add(lbl_grade);

		combo_grade = new JComboBox();
		combo_grade.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		combo_grade.setModel(new DefaultComboBoxModel(new String[] {"\uC804\uCCB4", "1", "2", "3", "4"}));
		p_north.add(combo_grade);

		lbl_type = new JLabel("\uC804\uACF5\uC5EC\uBD80");
		lbl_type.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		p_north.add(lbl_type);

		combo_type = new JComboBox();
		combo_type.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		combo_type.setModel(new DefaultComboBoxModel(new String[] {"\uC804\uCCB4", "\uC804\uACF5", "\uAD50\uC591"}));
		p_north.add(combo_type);

		lbl_professor = new JLabel("\uAD50\uC218\uBA85");
		lbl_professor.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		p_north.add(lbl_professor);

		tf_pname = new JTextField();
		tf_pname.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		tf_pname.addActionListener(this);
		p_north.add(tf_pname);
		tf_pname.setColumns(10);

		btn_search = new JButton("\uAC80\uC0C9");
		btn_search.setPreferredSize(new Dimension(70, 40));
		btn_search.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		btn_search.addActionListener(this);
		
		lbl_subject = new JLabel("\uACFC\uBAA9\uBA85");
		lbl_subject.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		p_north.add(lbl_subject);
		
		tf_subject = new JTextField();
		tf_subject.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		tf_subject.addActionListener(this);
		p_north.add(tf_subject);
		tf_subject.setColumns(10);
		p_north.add(btn_search);

		lbl_credit = new JLabel("현재 수강학점 : " + student.getNowCredit());
		lbl_credit.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		p_north.add(lbl_credit);

		p_center = new JPanel();
		p_main.add(p_center, BorderLayout.CENTER);
		p_center.setLayout(new BorderLayout(0, 0));

		p_c_east = new JPanel();
		p_center.add(p_c_east, BorderLayout.EAST);
		p_c_east.setLayout(new GridLayout(0, 1, 0, 0));

		p_e_top = new JPanel();
		p_e_top.setPreferredSize(new Dimension(160, 10));
		p_c_east.add(p_e_top);

		btn_register = new JButton("\uC218\uAC15\uC2E0\uCCAD");
		btn_register.setPreferredSize(new Dimension(150, 40));
		btn_register.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		btn_register.addActionListener(this);
		p_e_top.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		p_e_top.add(btn_register);

		btn_plan = new JButton("\uAC15\uC758\uACC4\uD68D\uC11C");
		btn_plan.setPreferredSize(new Dimension(150, 40));
		btn_plan.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		btn_plan.addActionListener(this);
		p_e_top.add(btn_plan);
		
		btn_changepw = new JButton("\uBE44\uBC00\uBC88\uD638 \uBCC0\uACBD");
		btn_changepw.setPreferredSize(new Dimension(150, 40));
		btn_changepw.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		btn_changepw.addActionListener(this);
		p_e_top.add(btn_changepw);

		p_e_bottom = new JPanel();
		p_e_bottom.setPreferredSize(new Dimension(130, 10));
		p_c_east.add(p_e_bottom);
		p_e_bottom.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		btn_drop = new JButton("\uC218\uAC15\uCDE8\uC18C");
		btn_drop.setPreferredSize(new Dimension(140, 40));
		btn_drop.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		btn_drop.addActionListener(this);
		
		panel = new JPanel();
		panel.setPreferredSize(new Dimension(130, 60));
		p_e_bottom.add(panel);
		p_e_bottom.add(btn_drop);

		btn_timeTable = new JButton("\uC2DC\uAC04\uD45C\uC870\uD68C");
		btn_timeTable.setPreferredSize(new Dimension(140, 40));
		btn_timeTable.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		btn_timeTable.addActionListener(this);
		p_e_bottom.add(btn_timeTable);
		
		btn_refresh = new JButton("\uC0C8\uB85C\uACE0\uCE68");
		btn_refresh.setPreferredSize(new Dimension(140, 40));
		btn_refresh.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		p_e_bottom.add(btn_refresh);
		btn_refresh.addActionListener(this);

		p_c_center = new JPanel();
		p_center.add(p_c_center, BorderLayout.CENTER);
		p_c_center.setLayout(new BorderLayout(0, 30));

		p_c_c_center = new JPanel();
		p_c_center.add(p_c_c_center, BorderLayout.NORTH);
		p_c_c_center.setLayout(new BorderLayout(0, 0));

		sp_subject = new JScrollPane();
		p_c_c_center.add(sp_subject);

		tm = new TableModel(tableData, columnNames);
		table_subject= new JTable(tm);
		table_subject.setRowHeight(30);
		table_subject.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
		table_subject.addMouseListener(new TableMouseEventHandler());
		table_subject.setFillsViewportHeight(true);
		table_subject.getColumnModel().getColumn(0).setPreferredWidth(120);
		table_subject.getColumnModel().getColumn(1).setPreferredWidth(250);
		table_subject.getColumnModel().getColumn(2).setPreferredWidth(100);
		table_subject.getColumnModel().getColumn(3).setPreferredWidth(100);
		table_subject.getColumnModel().getColumn(4).setPreferredWidth(40);
		table_subject.getColumnModel().getColumn(5).setPreferredWidth(40);
		table_subject.getColumnModel().getColumn(0).setResizable(false);
		table_subject.getColumnModel().getColumn(1).setResizable(false);
		table_subject.getColumnModel().getColumn(2).setResizable(false);
		table_subject.getColumnModel().getColumn(3).setResizable(false);
		table_subject.getColumnModel().getColumn(4).setResizable(false);
		table_subject.getColumnModel().getColumn(5).setResizable(false);
		table_subject.getColumnModel().getColumn(6).setResizable(false);
		table_subject.getColumnModel().getColumn(7).setResizable(false);
		table_subject.getColumnModel().getColumn(8).setResizable(false);
		table_subject.getColumnModel().getColumn(9).setResizable(false);

		(table_subject.getTableHeader()).setReorderingAllowed(false);
		sp_subject.setViewportView(table_subject);

		p_c_c_south = new JPanel();
		p_c_center.add(p_c_c_south, BorderLayout.CENTER);
		p_c_c_south.setLayout(new BorderLayout(0, 0));

		sp_my = new JScrollPane();
		p_c_c_south.add(sp_my);
		tm = new TableModel(tableData, columnNames);
		table_my = new JTable(tm);
		table_my.setRowHeight(30);
		table_my.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
		table_my.addMouseListener(new TableMouseEventHandler());
		table_my.setFillsViewportHeight(true);
		table_my.getColumnModel().getColumn(0).setPreferredWidth(120);
		table_my.getColumnModel().getColumn(1).setPreferredWidth(250);
		table_my.getColumnModel().getColumn(2).setPreferredWidth(100);
		table_my.getColumnModel().getColumn(3).setPreferredWidth(100);
		table_my.getColumnModel().getColumn(4).setPreferredWidth(40);
		table_my.getColumnModel().getColumn(5).setPreferredWidth(40);
		table_my.getColumnModel().getColumn(0).setResizable(false);
		table_my.getColumnModel().getColumn(1).setResizable(false);
		table_my.getColumnModel().getColumn(2).setResizable(false);
		table_my.getColumnModel().getColumn(3).setResizable(false);
		table_my.getColumnModel().getColumn(4).setResizable(false);
		table_my.getColumnModel().getColumn(5).setResizable(false);
		table_my.getColumnModel().getColumn(6).setResizable(false);
		table_my.getColumnModel().getColumn(7).setResizable(false);
		table_my.getColumnModel().getColumn(8).setResizable(false);
		table_my.getColumnModel().getColumn(9).setResizable(false);

		(table_my.getTableHeader()).setReorderingAllowed(false);
		sp_my.setViewportView(table_my);
		
		p_east = new JPanel();
		p_east.setPreferredSize(new Dimension(150, 10));
		p_main.add(p_east, BorderLayout.EAST);
		p_east.setLayout(new BorderLayout(0, 0));
		
		scrollPane = new JScrollPane();
		p_east.add(scrollPane, BorderLayout.CENTER);
		
		list = new JList();
		list.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		list.addMouseListener(new TableMouseEventHandler());
		scrollPane.setViewportView(list);
		
		list.setListData(roomName);
		
		lbl_chat = new JLabel("\uC0C1\uC124 \uCC44\uD305\uBC29");
		lbl_chat.setPreferredSize(new Dimension(75, 30));
		lbl_chat.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		lbl_chat.setHorizontalAlignment(SwingConstants.CENTER);
		p_east.add(lbl_chat, BorderLayout.NORTH);

		setVisible(true);
	}

}
