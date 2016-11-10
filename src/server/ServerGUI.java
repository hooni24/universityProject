package server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import common.Data;
import common.vo.Human;
import common.vo.Professor;
import common.vo.Student;
import server.dialog.AdminDialog;

@SuppressWarnings("serial")
public class ServerGUI extends JFrame implements ActionListener, Runnable{
	private ServerDB db;
	private ServerSocket server;
	private boolean isOpen = true;
	private boolean isRegisterable;
	private boolean isCreatable;
	private JPanel p_w_north;
	private JButton btn_serverOpen;
	private JLabel lbl_now;
	private String serverStatus = "stop"; //start 또는 stop
	private String condition;
	private ButtonGroup bg;
	private JPanel p_east;
	private JScrollPane sp;
	private JTable table;
	private ServerGUI gui = this;
	
	public ServerGUI(){
		this.db = new ServerDB();  //ServerDB 생성 후 전역변수 초기화
		drawGUI();
		System.out.println("ServerGUI> DB객체 생성");
	}//const
	
	
	/**
	 * 버튼 활성화 전환 스위치
	 */
	public void buttonSwitch(){
		
		boolean b = btn_ok.isEnabled();
		btn_insert.setEnabled(b);
		btn_search.setEnabled(b);
		btn_update.setEnabled(b);
		btn_delete.setEnabled(b);
		btn_all.setEnabled(b);
		radio_professor.setEnabled(b);
		radio_student.setEnabled(b);
		
		btn_cancel.setEnabled(!b);
		btn_ok.setEnabled(!b);
	}//buttonSwitch()
	
	/**
	 * 전체 텍스트필드 disable시킴
	 */
	
	public void allDisable(){
		tf_grade.setEnabled(false);		tf_grade.setText("");
		tf_major.setEnabled(false);		tf_major.setText("");
		tf_name.setEnabled(false);		tf_name.setText("");
		tf_regNo.setEnabled(false);		tf_regNo.setText("");
	}//allDisable()
	
	/**
	 * 전체 텍스트필드 비우기
	 */
	public void allClear(){
		tf_grade.setText("");
		tf_major.setText("");
		tf_name.setText("");
		tf_regNo.setText("");
	}
	
	/**
	 * 받은 메세지를 Log창에 append
	 * @param message
	 */
	public void appendLog(String str){
		String message = String.format("[%tY-%<tm-%<td %<tH:%<tM:%<tS] %s%n", new Date(), str);
		ta_log.append(message);
		sp_log.getVerticalScrollBar().setValue(sp_log.getVerticalScrollBar().getMaximum());
	}//appendLog()
	
	/**
	 * 액션이벤트 처리
	 */
	public void actionPerformed(ActionEvent e){
		Object source = e.getSource();
		
		if(source == btn_serverOpen){						//서버 온오프 관련
			serverOnOff();
		}
		
		if(source == radio_professor){						//라디오버튼 관련
			lbl_regNo.setText("교수번호");
		}else if (source == radio_student){
			lbl_regNo.setText("학번");
		}
		
		if(source == btn_insert){							//CRUD버튼 관련
			truncateTF();
			whenClickInsert();
			tf_name.requestFocus();
		}else if (source == btn_delete){
			int a = JOptionPane.showConfirmDialog(this, table.getValueAt(table.getSelectedRow(), 1) + "님을 삭제하시겠습니까?", "삭제", JOptionPane.YES_NO_OPTION);
			if(a == JOptionPane.YES_OPTION){
				whenClickDelete();
				tf_name.setText("");
				tf_grade.setText("");
				tf_major.setText("");
			}
		}else if (source == btn_search){
			truncateTF();
			condition = "search";
			buttonSwitch();
			tf_regNo.setEnabled(true);
			tf_regNo.requestFocus();
		}else if (source == btn_update){
			whenClickUpdate();
			tf_name.requestFocus();
		}else if (source == btn_all){
			ArrayList<Human> hList = db.searchAllHuman();
			updateTable(hList);
		}
		
		if(source == btn_ok || source == tf_grade || source == tf_major || source == tf_name || source == tf_regNo){						//OK-CANCEL 버튼 관련
			if(condition.equals("insert")){				//insert였다면
				try{
					if(radio_student.isSelected()){
						if(Integer.parseInt(tf_grade.getText().trim()) < 7 && Integer.parseInt(tf_grade.getText().trim()) > 0){
							okWhenInsert();
						}else {
							JOptionPane.showMessageDialog(this, "6학년 이하로만 입력가능합니다.");
						}
					}else {
						okWhenInsert();
					}
				}catch (NumberFormatException nfe){
					JOptionPane.showMessageDialog(this, "학년은 정수1~6만 입력하세요");
				}
			}else if (condition.equals("search")){		//search였다면
				okWhenSearch();
			}else if (condition.equals("update")){		//update였다면
				okWhenUpdate();
			}
		}else if (source == btn_cancel){			//Cancel
			buttonSwitch();
			allDisable();
			if(radio_student.isSelected()){
				lbl_regNo.setText("학번");
			}else {
				lbl_regNo.setText("교수번호");
			}
		}else if (source == btn_admin){
			if(ad == null){
				ad = new AdminDialog(this, serverThread);
			}else {
				JOptionPane.showMessageDialog(this, "이미 학사관리창이 열려 있습니다.");
			}
		}
	}//actionPerformed()
	
	
	private void okWhenUpdate() {
		if(table.getValueAt(table.getSelectedRow(), 2).equals("교수")){
			Human h = new Professor(tf_name.getText().trim(), tf_regNo.getText().trim(), tf_major.getText().trim());
			boolean b = db.updateHuman(h);
			if(b){
				JOptionPane.showMessageDialog(this, "수정성공");
				updateTable(h);
				allDisable();
				buttonSwitch();
			}else {
				JOptionPane.showMessageDialog(this, "수정실패. 없는 사람입니다.");
			}
		}else {
			Human h = new Student(tf_name.getText().trim(), tf_regNo.getText().trim(), tf_major.getText().trim(), tf_grade.getText().trim(), 0);
			boolean b = db.updateHuman(h);
			if(b){
				JOptionPane.showMessageDialog(this, "수정성공");
				updateTable(h);
				allDisable();
				buttonSwitch();
			}else {
				JOptionPane.showMessageDialog(this, "수정실패. 없는 사람입니다.");
			}
		}		
	}
	
	public void truncateTF(){
		tf_grade.setText("");
		tf_major.setText("");
		tf_name.setText("");
		tf_regNo.setText("");
	}


	private void okWhenSearch() {
		if(!tf_regNo.getText().trim().equals("")){
			Human h;
			if(radio_professor.isSelected()){
				 h = db.searchHuman(tf_regNo.getText().trim(), new String("교수"));
			}else {
				h = db.searchHuman(tf_regNo.getText().trim(), new String("학생"));
			}
			if(h != null){
				updateTable(h);
				buttonSwitch();
				tf_regNo.setEnabled(false);
				allDisable();
			}else {
				JOptionPane.showMessageDialog(this, "검색실패. 교수번호/학번을 확인하세요");
			}
		}else {
			JOptionPane.showMessageDialog(this, "교수번호 및 학번을 입력하세요");
		}		
	}


	private void okWhenInsert() {
		if(radio_professor.isSelected()){	//교수라면
			if(!tf_regNo.getText().trim().equals("") && !tf_major.getText().trim().equals("") && !tf_name.getText().trim().equals("")){
				Professor p = new Professor(tf_name.getText().trim(), tf_regNo.getText().trim(), tf_major.getText().trim());
				boolean b = db.insertHuman(p);
				if(b){
					JOptionPane.showMessageDialog(this, "입력성공");
					allClear();
				}else {
					JOptionPane.showMessageDialog(this, "입력실패. 교수번호를 확인하세요");
				}
			}else {
				JOptionPane.showMessageDialog(this, "항목을 모두 입력하세요");
			}
		}else {
			if(!tf_regNo.getText().trim().equals("") && !tf_major.getText().trim().equals("") && !tf_name.getText().trim().equals("") && !tf_grade.getText().trim().equals("")){
				Student s = new Student(tf_name.getText().trim(), tf_regNo.getText().trim(), tf_major.getText().trim(), tf_grade.getText().trim(), 0);
				boolean b = db.insertHuman(s);
				if(b){
					JOptionPane.showMessageDialog(this, "입력성공");
					allClear();
				}else {
					JOptionPane.showMessageDialog(this, "입력실패. 학번을 확인하세요");
				}
			}else {
				JOptionPane.showMessageDialog(this, "항목을 모두 입력하세요");
			}
		}
	}


	private void whenClickUpdate() {
		if(table.getValueAt(table.getSelectedRow(), 2).equals("교수")){
			lbl_regNo.setText("교수번호");
		}else {
			lbl_regNo.setText("학번");
		}
		tf_regNo.setText((String) table.getValueAt(table.getSelectedRow(), 0));
		tf_name.setText((String) table.getValueAt(table.getSelectedRow(), 1));
		tf_grade.setText((String) table.getValueAt(table.getSelectedRow(), 2));
		tf_major.setText((String) table.getValueAt(table.getSelectedRow(), 3));
		try{
			targetNo = (String) table.getValueAt(table.getSelectedRow(), 0);
			if(targetNo != null){
				condition = "update";
				buttonSwitch();
				tf_name.setEnabled(true);
				tf_major.setEnabled(true);
				if(!table.getValueAt(table.getSelectedRow(), 2).equals("교수")) tf_grade.setEnabled(true);
				if(targetNo.charAt(0) == 'S'){
					tf_grade.setEnabled(true);
				}
			}
		}catch (ArrayIndexOutOfBoundsException e1){
			JOptionPane.showMessageDialog(this, "테이블에서 수정할 대상을 선택하세요");
		}
	}


	private void whenClickDelete() {
		try{
			targetNo = (String) table.getValueAt(table.getSelectedRow(), 0);
			if(targetNo != null){
				boolean b = db.deleteHuman((String) table.getValueAt(table.getSelectedRow(), 0), 
											(String) table.getValueAt(table.getSelectedRow(), 2));
				if(b){
					JOptionPane.showMessageDialog(this, "삭제완료");
					allDisable();
					btn_all.doClick();
//					truncateTable();
				}else {
					JOptionPane.showMessageDialog(this, "삭제 실패. 고유번호를 확인하세요");
				}
			}
		}catch (ArrayIndexOutOfBoundsException e1){
			JOptionPane.showMessageDialog(this, "테이블에서 삭제할 대상을 선택하세요");
		}		
	}


	private void whenClickInsert() {
		condition = "insert"; //구분자
		buttonSwitch();
		if(radio_professor.isSelected()){
			tf_name.setEnabled(true);
			tf_regNo.setEnabled(true);
			tf_major.setEnabled(true);
		}else {
			tf_name.setEnabled(true);
			tf_regNo.setEnabled(true);
			tf_major.setEnabled(true);
			tf_grade.setEnabled(true);
		}		
	}


	public void serverOnOff(){
		if(serverStatus .equals("stop")){
			isOpen = true;
			startServer();
			appendLog("서버 시작됨");
			lbl_now.setText("현재 : ON");
			btn_serverOpen.setText("서버 중지");
			serverStatus = "start";
		} else {
			try {
				if(ServerThread.getCurrentUserCount() > 0){
					Data data = new Data(Data.A_SERVER_NOTICE);
					data.putServerStatus("isOpen", false);
					serverThread.broadCasting(data);
					ServerThread.resetList();
				}
				server.close();
				appendLog("서버 중지됨");
				lbl_now.setText("현재 : OFF");
				serverStatus = "stop";
				isOpen = false;
			} catch (IOException e1) {
				e1.printStackTrace();
			}//catch
			btn_serverOpen.setText("서버 시작");
		}
	}
	
	public void updateTable(Human h){
		Object[][] tableData = new Object[1][4];
		if(h instanceof Professor){
			Professor p = (Professor) h;
			tableData[0][0] = p.getRegNo();
			tableData[0][1] = p.getName();
			tableData[0][2] = "교수";
			tableData[0][3] = p.getMajor();
		}else if(h instanceof Student){
			Student s = (Student) h;
			tableData[0][0] = s.getRegNo();
			tableData[0][1] = s.getName();
			tableData[0][2] = s.getGrade();
			tableData[0][3] = s.getMajor();
		}
		tm = new TableModel(tableData, columnNames);
		table.setModel(tm);
		table.addMouseListener(new TableMouseEventHandler());
		table.setFillsViewportHeight(true);
		table.getColumnModel().getColumn(0).setPreferredWidth(100);
		table.getColumnModel().getColumn(1).setPreferredWidth(120);
		table.getColumnModel().getColumn(2).setPreferredWidth(60);
		table.getColumnModel().getColumn(3).setPreferredWidth(200);
		table.getColumnModel().getColumn(0).setResizable(false);
		table.getColumnModel().getColumn(1).setResizable(false);
		table.getColumnModel().getColumn(2).setResizable(false);
		table.getColumnModel().getColumn(3).setResizable(false);
		
		(table.getTableHeader()).setReorderingAllowed(false);
		sp.setViewportView(table);
	}
	
	public void updateTable(ArrayList<Human> hList){
		Object[][] tableData = new Object[hList.size()][4];			//2차원배열 생성
		for (int i = 0; i < tableData.length; i++) {			//2중for문으로 2차원배열 초기화
			for (int j = 0; j < 4; j++) {
				if(hList.get(i) instanceof Professor){
					if(j ==0) tableData[i][j] = hList.get(i).getName();
					if(j ==1) tableData[i][j] = hList.get(i).getRegNo();
					if(j ==2) tableData[i][j] = "교수";
					if(j ==3) tableData[i][j] = hList.get(i).getMajor();
				}else if (hList.get(i) instanceof Student){
					Student s = (Student) hList.get(i);
					if(j ==0) tableData[i][j] = s.getRegNo();
					if(j ==1) tableData[i][j] = s.getName();
					if(j ==2) tableData[i][j] = s.getGrade();
					if(j ==3) tableData[i][j] = s.getMajor();
				}
			}
		}
		tm = new TableModel(tableData, columnNames);			//테이블모델 생성
		table.setModel(tm);								//테이블모델 적용
		table.setRowHeight(30);
		table.setFont(new Font("맑은 고딕", Font.PLAIN, 20));
		table.addMouseListener(new TableMouseEventHandler());
		table.setFillsViewportHeight(true);
		table.getColumnModel().getColumn(0).setPreferredWidth(100);
		table.getColumnModel().getColumn(1).setPreferredWidth(120);
		table.getColumnModel().getColumn(2).setPreferredWidth(70);
		table.getColumnModel().getColumn(3).setPreferredWidth(200);
		table.getColumnModel().getColumn(0).setResizable(false);
		table.getColumnModel().getColumn(1).setResizable(false);
		table.getColumnModel().getColumn(2).setResizable(false);
		table.getColumnModel().getColumn(3).setResizable(false);
		
		(table.getTableHeader()).setReorderingAllowed(false);
		sp.setViewportView(table);
	}
	
	public void truncateTable(){
		tm = new TableModel(tableData, columnNames);
		table.setModel(tm);
	}
	
	class TableMouseEventHandler extends MouseAdapter {
		public void mouseClicked(MouseEvent me){
			if(me.getClickCount() == 1){
				if(me.getComponent() == table){
					try{
						if(table.getValueAt(table.getSelectedRow(), 0) != null && !btn_ok.isEnabled()){
							if(table.getValueAt(table.getSelectedRow(), 2).equals("교수")){
								lbl_regNo.setText("교수번호");
							}else {
								lbl_regNo.setText("학번");
							}
							tf_regNo.setText((String) table.getValueAt(table.getSelectedRow(), 0));
							tf_name.setText((String) table.getValueAt(table.getSelectedRow(), 1));
							tf_grade.setText((String) table.getValueAt(table.getSelectedRow(), 2));
							tf_major.setText((String) table.getValueAt(table.getSelectedRow(), 3));
							}
						}catch (ArrayIndexOutOfBoundsException e){
					}
				}
			}
		}//mouserClicked()
	}//mouseAdapter innerClass
	
	public void startServer(){
		try {
			server = new ServerSocket(33333);
			Thread t = new Thread(this);
			t.setName("서버스레드");
			t.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}//startServer()
	

	public void run() {
		while(isOpen){
			try {
				Socket client = server.accept();
				appendLog(client.getInetAddress().getHostAddress()+" 클라이언트 접속");
				serverThread = new ServerThread(client, db, this);
				Thread t = new Thread(serverThread);
				t.start();
				if(ad != null) {
					ad.setSt(serverThread);
				}
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}//while
	}
	
	
	
	//이 아래는 getter/setter/drawGUI()/main()
	
	public boolean isCreatable() {
		return isCreatable;
	}

	public void setCreatable(boolean isCreatable) {
		this.isCreatable = isCreatable;
	}

	public boolean isOpen() {
		return isOpen;
	}

	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

	public boolean isRegisterable() {
		return isRegisterable;
	}

	public void setRegisterable(boolean isRegisterable) {
		this.isRegisterable = isRegisterable;
	}
	public void setAd(AdminDialog ad) {
		this.ad = ad;
	}



	private JPanel contentPane;
	private JPanel p_main;
	private JPanel p_center;
	private JPanel p_west;
	private JPanel p_south;
	private JScrollPane sp_log;
	private JTextArea ta_log;
	private JPanel p_name;
	private JPanel p_regNo;
	private JPanel p_major;
	private JPanel p_grade;
	private JLabel lbl_name;
	private JTextField tf_name;
	private JLabel lbl_regNo;
	private JTextField tf_regNo;
	private JLabel lbl_major;
	private JTextField tf_major;
	private JLabel lbl_grade;
	private JTextField tf_grade;
	private JButton btn_insert;
	private JButton btn_update;
	private JButton btn_ok;
	private JButton btn_admin;
	private JButton btn_cancel;
	private JButton btn_search;
	private JButton btn_delete;
	private JPanel p_s_center;
	private JPanel p_s_north;
	private JRadioButton radio_professor;
	private JRadioButton radio_student;
	private JPanel p_s_c_center;
	
	private Object [][] tableData = new Object[0][0];
	private String [] columnNames = {"고유번호", "이름", "학년", "학과"};
	private TableModel tm;
	private JButton btn_all;
	private String targetNo;
	private ServerThread serverThread;
	private AdminDialog ad;
	private JPanel p_s_n_west;
	private JPanel p_s_n_east;
	private JLabel lbl_status;
	private JPanel panel_1;
	

	class TableModel extends DefaultTableModel {							//Table을 다루기 위한 inner class
		public TableModel(Object [][] defaultRowData, Object [] defaultColumnNames) {
			super.setDataVector(defaultRowData, defaultColumnNames);
		}
		public boolean isCellEditable(int rowIndex, int columnIndex){		//셀 수정 못하게
			return false;
		}
	}
	
	/**
	 * GUI 그리는 메소드
	 */
	private void drawGUI() {
		setTitle("\uC218\uAC15\uC2E0\uCCAD \uC11C\uBC84");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 903, 678);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				int a = JOptionPane.showConfirmDialog(gui, "서버를 닫고 프로그램을 종료하시겠습니까?", "종료", JOptionPane.YES_NO_OPTION);
				if(a == JOptionPane.YES_OPTION){
					if(ServerThread.getCurrentUserCount() > 0){
						Data data = new Data(Data.A_SERVER_NOTICE);
						data.putServerStatus("isOpen", false);
						serverThread.broadCasting(data);
						ServerThread.resetList();
					}
					System.exit(0);
				}
			}
		});

		p_main = new JPanel();
		contentPane.add(p_main, BorderLayout.CENTER);
		p_main.setLayout(new BorderLayout(0, 0));

		p_center = new JPanel();
		p_main.add(p_center, BorderLayout.CENTER);
		p_center.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		p_name = new JPanel();
		p_center.add(p_name);

		lbl_name = new JLabel("\uC774\uB984");
		lbl_name.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		lbl_name.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_name.setHorizontalTextPosition(SwingConstants.CENTER);
		lbl_name.setPreferredSize(new Dimension(90, 30));
		p_name.add(lbl_name);

		tf_name = new JTextField();
		tf_name.setHorizontalAlignment(SwingConstants.CENTER);
		tf_name.setPreferredSize(new Dimension(5, 30));
		tf_name.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		tf_name.setEnabled(false);
		tf_name.addActionListener(this);
		p_name.add(tf_name);
		tf_name.setColumns(9);

		p_regNo = new JPanel();
		p_center.add(p_regNo);

		lbl_regNo = new JLabel("\uAD50\uC218\uBC88\uD638");
		lbl_regNo.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		lbl_regNo.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_regNo.setPreferredSize(new Dimension(90, 30));
		p_regNo.add(lbl_regNo);

		tf_regNo = new JTextField();
		tf_regNo.setHorizontalAlignment(SwingConstants.CENTER);
		tf_regNo.setPreferredSize(new Dimension(5, 30));
		tf_regNo.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		tf_regNo.setEnabled(false);
		tf_regNo.addActionListener(this);
		tf_regNo.setColumns(9);
		p_regNo.add(tf_regNo);

		p_major = new JPanel();
		p_center.add(p_major);

		lbl_major = new JLabel("\uC804\uACF5");
		lbl_major.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		lbl_major.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_major.setHorizontalTextPosition(SwingConstants.CENTER);
		lbl_major.setPreferredSize(new Dimension(90, 30));
		p_major.add(lbl_major);

		tf_major = new JTextField();
		tf_major.setHorizontalAlignment(SwingConstants.CENTER);
		tf_major.setPreferredSize(new Dimension(5, 30));
		tf_major.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		tf_major.setEnabled(false);
		tf_major.addActionListener(this);
		tf_major.setColumns(9);
		p_major.add(tf_major);

		p_grade = new JPanel();
		p_center.add(p_grade);

		lbl_grade = new JLabel("\uD559\uB144");
		lbl_grade.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		lbl_grade.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_grade.setHorizontalTextPosition(SwingConstants.CENTER);
		lbl_grade.setPreferredSize(new Dimension(90, 30));
		p_grade.add(lbl_grade);

		tf_grade = new JTextField();
		tf_grade.setHorizontalAlignment(SwingConstants.CENTER);
		tf_grade.setPreferredSize(new Dimension(5, 30));
		tf_grade.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		tf_grade.addActionListener(this);
		tf_grade.setEnabled(false);
		tf_grade.setColumns(9);
		p_grade.add(tf_grade);

		p_south = new JPanel();
		p_main.add(p_south, BorderLayout.SOUTH);
		p_south.setLayout(new BorderLayout(0, 0));

		bg = new ButtonGroup();
		
		panel_1 = new JPanel();
		p_south.add(panel_1, BorderLayout.NORTH);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		p_s_center = new JPanel();
		panel_1.add(p_s_center, BorderLayout.CENTER);
		p_s_center.setLayout(new BorderLayout(0, 0));
		
		p_s_c_center = new JPanel();
		p_s_center.add(p_s_c_center);
				
		btn_insert = new JButton("\uB4F1\uB85D");
		btn_insert.setPreferredSize(new Dimension(70, 40));
		btn_insert.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		btn_insert.addActionListener(this);
		p_s_c_center.add(btn_insert);
						
		btn_update = new JButton("\uC218\uC815");
		btn_update.setPreferredSize(new Dimension(70, 40));
		btn_update.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		btn_update.addActionListener(this);
		p_s_c_center.add(btn_update);
								
		btn_delete = new JButton("\uC0AD\uC81C");
		btn_delete.setPreferredSize(new Dimension(70, 40));
		btn_delete.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		btn_delete.addActionListener(this);
		p_s_c_center.add(btn_delete);
		
		btn_search = new JButton("\uAC80\uC0C9");
		btn_search.setPreferredSize(new Dimension(70, 40));
		btn_search.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		btn_search.addActionListener(this);
		p_s_c_center.add(btn_search);
		
		btn_ok = new JButton("\uD655\uC778");
		btn_ok.setPreferredSize(new Dimension(70, 40));
		btn_ok.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		btn_ok.setEnabled(false);
		btn_ok.addActionListener(this);
		
		btn_all = new JButton("\uC804\uCCB4\uC870\uD68C");
		btn_all.setPreferredSize(new Dimension(105, 40));
		btn_all.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		btn_all.addActionListener(this);
		p_s_c_center.add(btn_all);
		p_s_c_center.add(btn_ok);
														
		btn_cancel = new JButton("\uCDE8\uC18C");
		btn_cancel.setPreferredSize(new Dimension(70, 40));
		btn_cancel.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		btn_cancel.setEnabled(false);
		btn_cancel.addActionListener(this);
		p_s_c_center.add(btn_cancel);
	
		btn_admin = new JButton("\uD559\uC0AC\uAD00\uB9AC");
		btn_admin.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		btn_admin.addActionListener(this);
		p_s_center.add(btn_admin, BorderLayout.EAST);
			
		p_s_north = new JPanel();
		panel_1.add(p_s_north, BorderLayout.NORTH);
		p_s_north.setLayout(new BorderLayout(0, 0));
		
		p_s_n_west = new JPanel();
		p_s_north.add(p_s_n_west, BorderLayout.WEST);
		
		radio_professor = new JRadioButton("\uAD50\uC218");
		radio_professor.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		p_s_n_west.add(radio_professor);
		radio_professor.addActionListener(this);
		bg.add(radio_professor);
		radio_professor.setSelected(true);
		
		radio_student = new JRadioButton("\uD559\uC0DD");
		radio_student.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		p_s_n_west.add(radio_student);
		radio_student.addActionListener(this);
		bg.add(radio_student);
		
		p_s_n_east = new JPanel();
		p_s_north.add(p_s_n_east, BorderLayout.EAST);
					
		p_west = new JPanel();
		p_south.add(p_west, BorderLayout.CENTER);
		p_west.setPreferredSize(new Dimension(230, 250));
		p_west.setLayout(new BorderLayout(0, 0));
							
		ta_log = new JTextArea();
		ta_log.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
		ta_log.setBackground(Color.BLACK);
		ta_log.setForeground(new Color(255, 204, 51));
		ta_log.setEditable(false);
		
		sp_log = new JScrollPane();
		sp_log.setViewportView(ta_log);
		p_west.add(sp_log);
		
		p_w_north = new JPanel();
		p_south.add(p_w_north, BorderLayout.SOUTH);
		
		btn_serverOpen = new JButton("\uC11C\uBC84 \uC2DC\uC791");
		btn_serverOpen.setPreferredSize(new Dimension(120, 40));
		btn_serverOpen.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		btn_serverOpen.addActionListener(this);
		p_w_north.add(btn_serverOpen);
		
		lbl_now = new JLabel("\uD604\uC7AC : OFF");
		lbl_now.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		p_w_north.add(lbl_now);
		
		p_east = new JPanel();
		p_east.setPreferredSize(new Dimension(600, 10));
		p_main.add(p_east, BorderLayout.EAST);
		p_east.setLayout(new BorderLayout(0, 0));
		
		sp = new JScrollPane();
		p_east.add(sp);
		
		tm = new TableModel(tableData, columnNames);
		table = new JTable(tm);
		table.addMouseListener(new TableMouseEventHandler());
		table.setFillsViewportHeight(true);
		table.getColumnModel().getColumn(0).setPreferredWidth(100);
		table.getColumnModel().getColumn(1).setPreferredWidth(120);
		table.getColumnModel().getColumn(2).setPreferredWidth(60);
		table.getColumnModel().getColumn(3).setPreferredWidth(200);
		table.getColumnModel().getColumn(0).setResizable(false);
		table.getColumnModel().getColumn(1).setResizable(false);
		table.getColumnModel().getColumn(2).setResizable(false);
		table.getColumnModel().getColumn(3).setResizable(false);
		
		(table.getTableHeader()).setReorderingAllowed(false);
		sp.setViewportView(table);
		
		setLocationRelativeTo(null);
		setVisible(true);
	}// drawGUI()
	
	public static void main(String[] args) throws Exception{
		UIManager.setLookAndFeel("com.jtattoo.plaf.aluminium.AluminiumLookAndFeel");
		new ServerGUI();
	}//main

}// class
