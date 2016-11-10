package client;

import java.awt.BorderLayout;
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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import client.dialog.CommonPasswordChange;
import client.dialog.CommonTimeTableDialog;
import client.dialog.ProfessorCreateDialog;
import client.dialog.ProfessorSearchDialog;
import client.dialog.ProfessorUpdateDialog;
import common.Data;
import common.vo.Lecture;
import common.vo.Professor;
import common.vo.Student;
import java.awt.FlowLayout;
import java.awt.Dimension;

public class ClientProfessorGUI extends JFrame implements ActionListener {
	
	private Professor professor;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private ClientProfessorGUI gui = this;
	private boolean isCreatable;
	private ArrayList<Lecture> lectureList = null;	//매번 업데이트테이블 할 때마다 최신화된 내 강의정보를 담아둠(여기저기 요긴하게 씀)

	public ClientProfessorGUI(Professor professor, ObjectInputStream ois, ObjectOutputStream oos, boolean isCreatable) {
		this.isCreatable = isCreatable;
		this.professor = professor;
		this.ois = ois;
		this.oos = oos;
		drawGUI();
		setButton(isCreatable);
		new Thread(new ClientProfessorThread(ois, this)).start();
		requestMyTable();
	}
	
	public void setButton(boolean isCreatable){
		if(!isCreatable){
			btn_create.setEnabled(false);
			btn_delete.setEnabled(false);
			btn_update.setEnabled(false);
		}else {
			btn_create.setEnabled(true);
			btn_delete.setEnabled(true);
			btn_update.setEnabled(true);
		}
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

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		
		if(source == btn_create){		//강의개설
			new ProfessorCreateDialog(this, professor);
		}
		if(source == btn_delete){		//폐강
			deleteLecture();
		}
		if(source == btn_update){
			updateLecture();
		}
		if(source == btn_search){
			searchStudentInfo();
		}
		if(source == btn_time){
			getTimeTable();
		}
		if(source == btn_change_pw){
			changePw();
		}
	}
	
	
	public void changePw(){
		cpc = new CommonPasswordChange(this, professor);
		cpc.setVisible(true);
	}
	
	public void getTimeTable(){
		cttd = new CommonTimeTableDialog(this, professor);
	}
	
	public void searchStudentInfo(){
		if(table_lecture.getSelectedRow() >= 0){
			psd = new ProfessorSearchDialog(this, lectureList.get(table_lecture.getSelectedRow()), professor);
		}else {
			JOptionPane.showMessageDialog(this, "조회할 과목을 선택하세요");
		}
	}
	
	public void updateLecture(){
		if(table_lecture.getSelectedRow() >= 0){
			new ProfessorUpdateDialog(this, lectureList.get(table_lecture.getSelectedRow()), professor);
		}else {
			JOptionPane.showMessageDialog(this, "수정할 과목을 선택하세요");
		}
	}
	
	public void deleteLecture(){		
		if(table_lecture.getSelectedRow() >= 0) {			//선택된게 있으면
			int a = JOptionPane.showConfirmDialog(this, table_lecture.getValueAt(table_lecture.getSelectedRow(), 1) + "과목을 정말 폐강시키시겠습니까?", "폐강", JOptionPane.YES_NO_OPTION);
			if(a == JOptionPane.YES_OPTION){		//yes 선택했으면
				Data data = new Data(Data.P_DELETE_LECTURE);
				data.setLecture(lectureList.get(table_lecture.getSelectedRow()));
				sendData(data);
			}
		}else {
			JOptionPane.showMessageDialog(this, "폐강할 과목을 선택하세요");
		}
	}
	
	
	public void updateStudentTable(ArrayList<Student> sList){
		psd.updateTable(sList);
		psd.setVisible(true);
	}
	
	public void setTimeTable(ArrayList<Lecture> lList){
		cttd.setTimeTable(lList);
		cttd.setVisible(true);
	}
	
	public void disposePassword(boolean b){
		if(b) {
			cpc.dispose();
			JOptionPane.showMessageDialog(cpc, "성공적으로 변경되었습니다.");
		}else {
			JOptionPane.showMessageDialog(cpc, "어떤 상황이었는지 개발자에게 알려주세요");
		}
	}
	
	
	
	//이 아래는 drawGUI() // getter
	
	private JPanel contentPane;
	private JPanel p_main;
	private JPanel p_north;
	private JPanel p_center;
	private JPanel p_east;
	private JLabel lbl_myLecture;
	private JScrollPane sp_lecture;
	private JTable table_lecture;
	private JButton btn_create;
	private JButton btn_search;
	private JButton btn_delete;
	private JButton btn_update;
	private JButton btn_time;
	
	private Object [][] tableData = new Object[0][0];
	private String [] columnNames = {"과목코드", "과목명", "시간", "강의실"};
	private TableModel tm;
	private ProfessorSearchDialog psd;
	private CommonTimeTableDialog cttd;
	private JButton btn_change_pw;
	private CommonPasswordChange cpc;
	
	
	class TableModel extends DefaultTableModel {							//Table을 다루기 위한 inner class
		public TableModel(Object [][] defaultRowData, Object [] defaultColumnNames) {
			super.setDataVector(defaultRowData, defaultColumnNames);
		}
		public boolean isCellEditable(int rowIndex, int columnIndex){		//셀 수정 못하게
			return false;
		}
	}
	
	class TableMouseEventHandler extends MouseAdapter {
		public void mouseClicked(MouseEvent me){
			if(me.getClickCount() == 2){
				if(me.getComponent() == table_lecture){
					searchStudentInfo();
				}
			}
		}//mouserClicked()
	}//mouseAdapter innerClass
	
	public void requestMyTable(){
		Data data = new Data(Data.P_GET_ALL_LECTURE_LIST);
		data.setHuman(professor);
		try {
			oos.writeObject(data);
			oos.reset();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("ClientProfessorGUI 222라인 에러");
		}
	}
	
	
	/**
	 * 교수GUI가 가지고 있는 내 시간표 갱신 메소드
	 * @param lectureList
	 */
	public void updateTable(ArrayList<Lecture> lectureList){
		this.lectureList = lectureList;
		System.out.println("업데이트 테이블 메소드 실행");
		Object[][] tableData = new Object[lectureList.size()][4];			//2차원배열 생성 (테이블 내용물)
		for (int i = 0; i < tableData.length; i++) {						//for문 돌면서 초기화
			tableData[i][0] = lectureList.get(i).getSubjectCode();
			tableData[i][1] = lectureList.get(i).getSubject();
			tableData[i][2] = lectureList.get(i).getTime();
			tableData[i][3] = lectureList.get(i).getRoom();
		}
		
		tm = new TableModel(tableData, columnNames);
		table_lecture= new JTable(tm);
		table_lecture.setRowHeight(30);
		table_lecture.setFont(new Font("맑은 고딕", Font.PLAIN, 20));
		table_lecture.addMouseListener(new TableMouseEventHandler());
		table_lecture.setFillsViewportHeight(true);
		table_lecture.getColumnModel().getColumn(0).setPreferredWidth(120);
		table_lecture.getColumnModel().getColumn(1).setPreferredWidth(300);
		table_lecture.getColumnModel().getColumn(2).setPreferredWidth(100);
		table_lecture.getColumnModel().getColumn(3).setPreferredWidth(100);
		table_lecture.getColumnModel().getColumn(0).setResizable(false);
		table_lecture.getColumnModel().getColumn(1).setResizable(false);
		table_lecture.getColumnModel().getColumn(2).setResizable(false);
		table_lecture.getColumnModel().getColumn(3).setResizable(false);
		
		(table_lecture.getTableHeader()).setReorderingAllowed(false);
		sp_lecture.setViewportView(table_lecture);
	}
	
	
	public void drawGUI(){
		setTitle("\uC218\uAC15\uC2E0\uCCAD - \uAD50\uC218\uC6A9");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 827, 414);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				int a = JOptionPane.showConfirmDialog(gui, "종료하시겠습니까?", "종료", JOptionPane.YES_NO_OPTION);
				if(a == JOptionPane.OK_OPTION){
					Data data = new Data(Data.C_LOG_OUT);
					data.setHuman(professor);
					sendData(data);
					System.exit(0);
				}
			}
		});
		
		p_main = new JPanel();
		contentPane.add(p_main, BorderLayout.CENTER);
		p_main.setLayout(new BorderLayout(0, 0));
		
		p_north = new JPanel();
		p_main.add(p_north, BorderLayout.NORTH);
		
		lbl_myLecture = new JLabel("\uB0B4 \uAC15\uC758 \uBAA9\uB85D");
		lbl_myLecture.setFont(new Font("맑은 고딕", Font.PLAIN, 22));
		p_north.add(lbl_myLecture);
		
		p_center = new JPanel();
		p_main.add(p_center);
		p_center.setLayout(new BorderLayout(0, 0));
		
		sp_lecture = new JScrollPane();
		p_center.add(sp_lecture);
		
		tm = new TableModel(tableData, columnNames);
		table_lecture= new JTable(tm);
		table_lecture.addMouseListener(new TableMouseEventHandler());
		table_lecture.setFillsViewportHeight(true);
		table_lecture.getColumnModel().getColumn(0).setPreferredWidth(120);
		table_lecture.getColumnModel().getColumn(1).setPreferredWidth(200);
		table_lecture.getColumnModel().getColumn(2).setPreferredWidth(100);
		table_lecture.getColumnModel().getColumn(3).setPreferredWidth(100);
		table_lecture.getColumnModel().getColumn(0).setResizable(false);
		table_lecture.getColumnModel().getColumn(1).setResizable(false);
		table_lecture.getColumnModel().getColumn(2).setResizable(false);
		table_lecture.getColumnModel().getColumn(3).setResizable(false);
		
		(table_lecture.getTableHeader()).setReorderingAllowed(false);
		sp_lecture.setViewportView(table_lecture);
		
		p_east = new JPanel();
		p_east.setPreferredSize(new Dimension(170, 10));
		p_main.add(p_east, BorderLayout.EAST);
		
		btn_create = new JButton("\uAC15\uC758\uAC1C\uC124");
		btn_create.setPreferredSize(new Dimension(150, 40));
		btn_create.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		btn_create.addActionListener(this);
		p_east.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		p_east.add(btn_create);
		
		btn_search = new JButton("\uAC15\uC758\uC870\uD68C");
		btn_search.setPreferredSize(new Dimension(150, 40));
		btn_search.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		btn_search.addActionListener(this);
		p_east.add(btn_search);
		
		btn_delete = new JButton("\uD3D0\uAC15");
		btn_delete.setPreferredSize(new Dimension(150, 40));
		btn_delete.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		btn_delete.addActionListener(this);
		p_east.add(btn_delete);
		
		btn_update = new JButton("\uAC15\uC758\uC815\uBCF4\uC218\uC815");
		btn_update.setPreferredSize(new Dimension(150, 40));
		btn_update.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		btn_update.addActionListener(this);
		p_east.add(btn_update);
		
		btn_time = new JButton("\uC2DC\uAC04\uD45C\uC870\uD68C");
		btn_time.setPreferredSize(new Dimension(150, 40));
		btn_time.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		btn_time.addActionListener(this);
		p_east.add(btn_time);
		
		btn_change_pw = new JButton("\uBE44\uBC00\uBC88\uD638 \uBCC0\uACBD");
		btn_change_pw.setPreferredSize(new Dimension(150, 40));
		btn_change_pw.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		btn_change_pw.addActionListener(this);
		p_east.add(btn_change_pw);
		
		setLocationRelativeTo(null);
		setVisible(true);
	}

}
