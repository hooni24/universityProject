package client.dialog;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import client.ClientProfessorGUI;
import common.Data;
import common.vo.Lecture;
import common.vo.Professor;
import common.vo.Student;
import java.awt.Dimension;

public class ProfessorSearchDialog extends JDialog implements ActionListener {
	
	private ClientProfessorGUI gui;
	private Lecture l;
	private Professor p;

	public ProfessorSearchDialog(ClientProfessorGUI gui, Lecture l, Professor p) {
		System.out.println("ProfessorSearchDialog> 강의정보: " + l );
		this.gui = gui;
		this.l = l;
		this.p = p;
		drawGUI();
		Data data = new Data(Data.P_GET_A_LECTURE);
		data.setLecture(l);
		gui.sendData(data);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		dispose();
	}
	
	
	/**
	 * 해당 과목의 학생 전체 정보를 요청(Lecture객체에 studentList로 받아옴)하고 
	 * 받아서 테이블에 세팅함
	 * 교수가 학생조회할때 씀
	 * @param lectureList
	 */
	public void updateTable(ArrayList<Student> sList){
		
		Object[][] tableData = new Object[sList.size()][4];			//2차원배열 생성 (테이블 내용물)
		for (int i = 0; i < tableData.length; i++) {						//for문 돌면서 초기화
			tableData[i][0] = sList.get(i).getRegNo();
			tableData[i][1] = sList.get(i).getGrade();
			tableData[i][2] = sList.get(i).getMajor();
			tableData[i][3] = sList.get(i).getName();
		}
		System.out.println("ProfessorSearchDialog> 테이블 다 만듬");
		
		tm = new TableModel(tableData, columnNames);
		table= new JTable(tm);
		table.setRowHeight(30);
		table.setFont(new Font("맑은 고딕", Font.PLAIN, 20));
		table.addMouseListener(new TableMouseEventHandler());
		table.setFillsViewportHeight(true);
		table.getColumnModel().getColumn(0).setPreferredWidth(80);
		table.getColumnModel().getColumn(1).setPreferredWidth(60);
		table.getColumnModel().getColumn(2).setPreferredWidth(150);
		table.getColumnModel().getColumn(3).setPreferredWidth(100);
		table.getColumnModel().getColumn(0).setResizable(false);
		table.getColumnModel().getColumn(1).setResizable(false);
		table.getColumnModel().getColumn(2).setResizable(false);
		table.getColumnModel().getColumn(3).setResizable(false);
		
		(table.getTableHeader()).setReorderingAllowed(false);
		sp.setViewportView(table);
	}
	
	//이 아래는 그리기 및 테이블 관련
	
	private final JPanel contentPanel = new JPanel();
	private JTable table;
	private Object [][] tableData = new Object[0][0];
	private String [] columnNames = {"학번", "학년", "학과", "이름"};
	private TableModel tm;
	private JScrollPane sp;
	
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
			
		}//mouserClicked()
	}//mouseAdapter innerClass

	private void drawGUI() {
		setTitle("\uC218\uAC15\uC815\uBCF4\uC870\uD68C");
		setBounds(100, 100, 542, 480);
		setModal(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		JPanel p_main = new JPanel();
		contentPanel.add(p_main);
		p_main.setLayout(new BorderLayout(0, 0));
		JLabel lbl_title = new JLabel("강의명 : " + l.getSubject());
		lbl_title.setFont(new Font("맑은 고딕", Font.PLAIN, 20));
		p_main.add(lbl_title, BorderLayout.NORTH);
		JPanel p_center = new JPanel();
		p_main.add(p_center, BorderLayout.CENTER);
		p_center.setLayout(new BorderLayout(0, 0));
		sp = new JScrollPane();
		p_center.add(sp);
		
		tm = new TableModel(tableData, columnNames);
		table= new JTable(tm);
		table.addMouseListener(new TableMouseEventHandler());
		table.setFillsViewportHeight(true);
		table.getColumnModel().getColumn(0).setPreferredWidth(80);
		table.getColumnModel().getColumn(1).setPreferredWidth(60);
		table.getColumnModel().getColumn(2).setPreferredWidth(150);
		table.getColumnModel().getColumn(3).setPreferredWidth(100);
		table.getColumnModel().getColumn(0).setResizable(false);
		table.getColumnModel().getColumn(1).setResizable(false);
		table.getColumnModel().getColumn(2).setResizable(false);
		table.getColumnModel().getColumn(3).setResizable(false);
		
		(table.getTableHeader()).setReorderingAllowed(false);
		sp.setViewportView(table);
		
		JPanel p_south = new JPanel();
		p_main.add(p_south, BorderLayout.SOUTH);
		JButton btn_ok = new JButton("\uD655\uC778");
		btn_ok.setPreferredSize(new Dimension(70, 40));
		btn_ok.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		btn_ok.addActionListener(this);
		p_south.add(btn_ok);
		setLocationRelativeTo(gui);
	}
}
