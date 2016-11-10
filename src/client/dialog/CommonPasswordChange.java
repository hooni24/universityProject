package client.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import client.ClientProfessorGUI;
import client.ClientStudentGUI;
import common.Data;
import common.vo.Professor;
import common.vo.Student;
import java.awt.Font;

public class CommonPasswordChange extends JDialog implements ActionListener{

	private final JPanel contentPanel = new JPanel();
	private JTextField tf_pw;
	private JTextField tf_confirm;
	private ClientProfessorGUI pgui;
	private JButton okButton;
	private JButton cancelButton;
	private Professor p;
	private ClientStudentGUI sgui;
	private Student s;

	/**
	 * @wbp.parser.constructor
	 */
	public CommonPasswordChange(ClientProfessorGUI pgui, Professor p) {
		this.pgui = pgui;
		this.p = p;
		drawGUI();
	}
	
	public CommonPasswordChange(ClientStudentGUI sgui, Student s){
		this.sgui = sgui;
		this.s = s;
		drawGUI();
	}

	private void drawGUI() {
		setTitle("\uBE44\uBC00\uBC88\uD638 \uBCC0\uACBD");
		setBounds(100, 100, 311, 161);
		setModal(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		JLabel lbl_pw = new JLabel("\uC0C8 \uBE44\uBC00\uBC88\uD638");
		lbl_pw.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		lbl_pw.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_pw.setPreferredSize(new Dimension(100, 18));
		contentPanel.add(lbl_pw);
		tf_pw = new JPasswordField();
		tf_pw.setFont(new Font("굴림", Font.PLAIN, 17));
		contentPanel.add(tf_pw);
		tf_pw.setColumns(9);
		tf_pw.addActionListener(this);
		JLabel lbl_confirm = new JLabel("\uBE44\uBC00\uBC88\uD638 \uD655\uC778");
		lbl_confirm.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		contentPanel.add(lbl_confirm);
		tf_confirm = new JPasswordField();
		tf_confirm.setFont(new Font("굴림", Font.PLAIN, 17));
		contentPanel.add(tf_confirm);
		tf_confirm.setColumns(9);
		tf_confirm.addActionListener(this);
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		okButton = new JButton("\uBCC0\uACBD");
		okButton.setPreferredSize(new Dimension(70, 40));
		okButton.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		okButton.setActionCommand("OK");
		okButton.addActionListener(this);
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
		cancelButton = new JButton("\uCDE8\uC18C");
		cancelButton.setPreferredSize(new Dimension(70, 40));
		cancelButton.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		cancelButton.setActionCommand("Cancel");
		cancelButton.addActionListener(this);
		buttonPane.add(cancelButton);		
		setLocationRelativeTo(pgui);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if(source == cancelButton){
			dispose();
		}else {
			if(!tf_pw.getText().trim().equals("") && !tf_pw.getText().trim().equals(" ")){
				if(p != null){
					if(tf_confirm.getText().equals(tf_pw.getText())){
						Data data = new Data(Data.P_CHANGE_PW);
						p.setPw(tf_pw.getText());
						data.setHuman(p);
						pgui.sendData(data);
					}else {
						JOptionPane.showMessageDialog(this, "확인 암호가 틀렸습니다.");
					}
				}else if (s != null){
					if(tf_confirm.getText().equals(tf_pw.getText())){
						Data data = new Data(Data.S_CHANGE_PW);
						s.setPw(tf_pw.getText());
						data.setHuman(s);
						sgui.sendData(data);
					}else {
						JOptionPane.showMessageDialog(this, "확인 암호가 틀렸습니다.");
					}
				}
			}else {
				JOptionPane.showMessageDialog(this, "비밀번호는 1글자 이상이어야 합니다.\n비밀번호에 공백을 포함할 수 있지만, 공백만 사용할 수는 없습니다.");
			}
		}
	}
	
	

}
