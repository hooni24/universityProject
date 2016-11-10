package client.dialog;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import client.ClientProfessorGUI;
import client.ClientStudentGUI;
import common.Data;
import common.vo.Lecture;
import common.vo.Professor;
import common.vo.Student;

public class CommonTimeTableDialog extends JDialog{

	private ClientProfessorGUI gui;
	private ClientStudentGUI sgui;
	private CommonTimeTableDialog cttd = this;

	/**
	 * @wbp.parser.constructor
	 */
	public CommonTimeTableDialog(ClientProfessorGUI gui, Professor p) {
		this.gui = gui;
		drawGUI();
		Data data = new Data(Data.P_GET_ALL_LECTURE_LIST_FOR_TIME);
		data.setHuman(p);
		gui.sendData(data);
	}
	
	public CommonTimeTableDialog(ClientStudentGUI gui, Student s) {
		this.sgui = gui;
		drawGUI();
		Data data = new Data(Data.S_GET_ALL_LECTURE_LIST_FOR_TIME);
		data.setHuman(s);
		gui.sendData(data);
	}
	
	
	public void setTimeTable(ArrayList<Lecture> lList){
		Object[][] tableData = new Object[9][6];			//2차원배열 생성 (테이블 내용물)
		for (int i = 0; i < 9; i++) {						//for문 돌면서 초기화
			tableData[i][0] = i+1;							//교시 초기화
		}
		
		for(int k = 0; k < lList.size(); k++){			//리스트에 있는 과목당
			time : for(int i = 0; i < 9; i+= 3){				//교시를 돌면서 1모듈(123) 4모듈(456) 7모듈(789)
				for (int j = 1; j < 6; j++){			//요일을 돌아본다.
					if(lList.get(k).getTime_index().equals(String.valueOf(j)) && lList.get(k).getModule().equals(String.valueOf(i+1))){
						tableData[i][j] = "<html>" + lList.get(k).getSubject() + "<br><br>" + lList.get(k).getRoom() + "</html>";
						tableData[i+1][j] = "<html>" + lList.get(k).getSubject() + "<br><br>" + lList.get(k).getRoom() + "</html>";
						tableData[i+2][j] = "<html>" + lList.get(k).getSubject() + "<br><br>" + lList.get(k).getRoom() + "</html>";
						break time;
					}
				}
			}
		}
		
		tm = new TableModel(tableData, columnNames);
		table= new JTable(tm);
		table.setRowHeight(80);
		table.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		table.setFillsViewportHeight(true);
		table.getColumnModel().getColumn(0).setPreferredWidth(10);
		table.getColumnModel().getColumn(0).setResizable(false);
		table.getColumnModel().getColumn(1).setResizable(false);
		table.getColumnModel().getColumn(2).setResizable(false);
		table.getColumnModel().getColumn(3).setResizable(false);
		table.getColumnModel().getColumn(4).setResizable(false);
		table.getColumnModel().getColumn(5).setResizable(false);
		table.setEnabled(false);
		
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				dispose();
			}
		});
		
		(table.getTableHeader()).setReorderingAllowed(false);
		sp.setViewportView(table);
	}
	
	
	//이하 drawGUI 및 테이블세팅
	
	private final JPanel contentPanel = new JPanel();
	private JTable table;
	private TableModel tm;
	private JScrollPane sp;
	private Object [][] tableData = new Object[0][0];
	private String [] columnNames = {"교시", "월", "화", "수", "목", "금"};
	private int j;
	private final JPanel panel_1 = new JPanel();
	private final JLabel lbl = new JLabel("\uB2EB\uC73C\uB824\uBA74 \uC544\uBB34\uACF3\uC774\uB098 \uD074\uB9AD\uD558\uC138\uC694 (\uC2A4\uD06C\uB9B0\uC0F7 : F12)");
	
	class TableModel extends DefaultTableModel {							//Table을 다루기 위한 inner class
		public TableModel(Object [][] defaultRowData, Object [] defaultColumnNames) {
			super.setDataVector(defaultRowData, defaultColumnNames);
		}
		public boolean isCellEditable(int rowIndex, int columnIndex){		//셀 수정 못하게
			return false;
		}
	}

	private void drawGUI() {
		setTitle("\uC2DC\uAC04\uD45C \uC870\uD68C");
		setBounds(100, 100, 1191, 857);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setAlwaysOnTop(true);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		JPanel panel = new JPanel();
		contentPanel.add(panel);
		panel.setLayout(new BorderLayout(0, 0));
		sp = new JScrollPane();
		panel.add(sp, BorderLayout.CENTER);
		
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				dispose();
			}
		});
		
		addKeyListener(new KeyAdapter() { // 키 리스너 추가
			public void keyPressed(KeyEvent e) { // 키를 누를시 이벤트 시작
				String s = e.getKeyText(e.getKeyCode()); // 누른 키의 값 저장
				if (e.getKeyCode() == KeyEvent.VK_F12) { // F11 누르면 다음 메소드 동작
					cttd.setOpacity(1f);
					capture();
					cttd.setOpacity(0.9f);
				}
			}
		});
		
		contentPanel.add(panel_1, BorderLayout.NORTH);
		lbl.setFont(new Font("맑은 고딕", Font.PLAIN, 30));
		
		panel_1.add(lbl);
		
		setUndecorated(true);
		setOpacity(0.9f);
		setLocationRelativeTo(gui);
	}
	
	private void capture() {
		try {
			Robot rb = new Robot();
			Rectangle rect = new Rectangle(this.getX(), this.getY() + 80, this.getWidth(), this.getHeight() - 80);		//시간표 화면 사이즈
			BufferedImage img = rb.createScreenCapture(rect);	//시간표 화면 사이즈만큼 캡쳐
			
			JFileChooser fc = new JFileChooser("C:");
			FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG 파일", "jpg");
			fc.setFileFilter(filter);
			int a = fc.showSaveDialog(this);
			if(a != JFileChooser.APPROVE_OPTION){	//아무것도 선택하지 않았다면
				JOptionPane.showMessageDialog(this, "경로를 선택하지 않았습니다");
				return;
			}
			String filePath = fc.getSelectedFile().getParent();
			String inputFileName = fc.getSelectedFile().getName();
			String filename = inputFileName;			//.jpg입력 안했으면 ok 그냥 그대로 사용
			if(inputFileName.length() > 4){
				if(inputFileName.substring(inputFileName.length() - 4, inputFileName.length()).equals(".jpg")){	//.jpg입력했으면 제거
					filename = inputFileName.substring(0, inputFileName.length() - 4);
				}
			}
			
			File folder = new File(filePath);		//폴더 경로 설정 File객체
			if(!folder.exists()){			//폴더가 존재하지 않으면
				folder.mkdir();				//폴더 생성
			}
			
			String[] files = folder.list();	//folder 안에 들어있는 파일명을 String[] 로 받아옴
			
			
			int fileNum = 1;	//초기 넘버링
			String fileFullName = null;
			
			ArrayList<String> result = new ArrayList<>();
			
			for (int i = 0; i < files.length; i++) {
				result.add(files[i]);		//전체 파일이름을 result에 저장함
			}
			
			System.out.println("전체 파일 목록 : " + result);
			
			for (int i = 0; i < result.size(); i++) {		//돌면서 중복되면 넘버링만 1씩 증가시키면서 파일명 생성함 (확장자 없는)
				fileFullName = filename + fileNum + ".jpg";
				if(result.get(i).equals(fileFullName)){
					fileNum++;
				}
			}
			
			fileFullName = filename + fileNum + ".jpg";
			
			File file = new File(filePath, fileFullName);
			
			ImageIO.write(img, "jpg", file);
			JOptionPane.showMessageDialog(this, "저장 성공!");
			
		} catch (AWTException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "저장 실패!");
		}
	}

}
