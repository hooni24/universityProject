package client.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import client.ClientStudentGUI;
import common.vo.Lecture;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;

public class StudentShowPlanner extends JDialog implements ActionListener {

	private final JPanel contentPanel = new JPanel();
	private JTextField tf_subject;
	private JTextField tf_professor;
	private Lecture l;
	private JTextArea ta_plan;
	private ClientStudentGUI gui;

	public StudentShowPlanner(Lecture l, ClientStudentGUI gui) {
		this.l = l;
		this.gui = gui;
		drawGUI();
		tf_professor.setText(l.getProfessor().getName());
		tf_subject.setText(l.getSubject());
		ta_plan.setText(l.getPlan());
		setVisible(true);
	}

	private void drawGUI() {
		setTitle("\uAC15\uC758\uACC4\uD68D\uC11C");
		setBounds(100, 100, 539, 372);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModal(true);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setPreferredSize(new Dimension(250, 10));
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(250, 10));
		contentPanel.add(scrollPane, BorderLayout.CENTER);
		ta_plan = new JTextArea();
		ta_plan.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		ta_plan.setEditable(false);
		ta_plan.setLineWrap(true);
		scrollPane.setViewportView(ta_plan);
		JPanel p_south = new JPanel();
		contentPanel.add(p_south, BorderLayout.SOUTH);
		JButton btn_ok = new JButton("\uD655\uC778");
		btn_ok.setPreferredSize(new Dimension(70, 40));
		btn_ok.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		btn_ok.addActionListener(this);
		p_south.add(btn_ok);
		JPanel p_north = new JPanel();
		contentPanel.add(p_north, BorderLayout.NORTH);
		JLabel lbl_subject = new JLabel("\uACFC\uBAA9\uBA85");
		lbl_subject.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		p_north.add(lbl_subject);
		tf_subject = new JTextField();
		tf_subject.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		tf_subject.setDisabledTextColor(Color.WHITE);
		tf_subject.setEditable(false);
		p_north.add(tf_subject);
		tf_subject.setColumns(15);
		JLabel lbl_professor = new JLabel("\uAD50\uC218\uBA85");
		lbl_professor.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		p_north.add(lbl_professor);
		tf_professor = new JTextField();
		tf_professor.setFont(new Font("맑은 고딕", Font.PLAIN, 17));
		tf_professor.setDisabledTextColor(Color.WHITE);
		tf_professor.setEditable(false);
		tf_professor.setColumns(10);
		p_north.add(tf_professor);		
		setLocationRelativeTo(gui);
	}

	public void actionPerformed(ActionEvent arg0) {
		dispose();
	}
}
