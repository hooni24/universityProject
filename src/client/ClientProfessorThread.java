package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import common.Data;
import common.vo.Lecture;
import common.vo.Student;

public class ClientProfessorThread implements Runnable {

	private ObjectInputStream ois;
	private ClientProfessorGUI gui;
	private boolean isOn = true;
	
	public ClientProfessorThread(ObjectInputStream ois, ClientProfessorGUI gui) {
		this.ois = ois;
		this.gui = gui;
	}

	@Override
	public void run() {
		try {
			while(isOn){
				Data data = (Data) ois.readObject();
				
				switch(data.getCommand()){
				case Data.P_CREATE_LECTURE:{
					int result = (int) data.getResult();
					if(result == 1){
						JOptionPane.showMessageDialog(gui, "강의가 개설되었습니다.");
						gui.requestMyTable();
					}else if(result == -1){
						JOptionPane.showMessageDialog(gui, "그 시간에 이미 사용중인 강의실입니다.");
					}else if(result == 0){
						JOptionPane.showMessageDialog(gui, "SQLException발생. 개발자에게 문의하세요");
					}else if(result == -2){
						JOptionPane.showMessageDialog(gui, "잠시 후 다시 시도해 주세요");
					}else {
						JOptionPane.showMessageDialog(gui, "몸은 하나. 그 시간엔 다른 수업이 있으십니다.");
					}
					break;}
				case Data.P_GET_ALL_LECTURE_LIST:{
					gui.updateTable(data.getLectureList());
					break;}
				case Data.P_DELETE_LECTURE:{
					if((boolean) data.getResult()){
						JOptionPane.showMessageDialog(gui, "폐강에 성공하였습니다.");
						gui.requestMyTable();
					}else {
						JOptionPane.showMessageDialog(gui, "이 창이 떳다면 어떤 상황이었는지 개발자에게 알려주세요");
					}
					break;}
				case Data.P_UPDATE_LECTURE:{
					int result = (int) data.getResult();
					if(result == 1){
						JOptionPane.showMessageDialog(gui, "수정에 성공하였습니다.");
						gui.requestMyTable();
					}else if(result == -1){
						JOptionPane.showMessageDialog(gui, "그 시간에 이미 사용중인 강의실입니다.");
					}else if(result == 0){
						JOptionPane.showMessageDialog(gui, "SQLException발생. 어떤 상황이었는지 개발자에게 알려주세요");
					}else if(result == -2){
						JOptionPane.showMessageDialog(gui, "잠시 후 다시 시도해 주세요");
					}else if(result == -3){
						JOptionPane.showMessageDialog(gui, "몸은 하나. 그 시간엔 다른 수업이 있으십니다.");
					}
					break;}
				case Data.P_GET_A_LECTURE:{
					ArrayList<Student> sList = (ArrayList<Student>) data.getResult();
					System.out.println("ClientProfessorTHread> " + sList);
					gui.updateStudentTable(sList);
					break;}
				case Data.P_GET_ALL_LECTURE_LIST_FOR_TIME:{
					ArrayList<Lecture> lectureList = data.getLectureList();
					gui.setTimeTable(lectureList);
					break;}
				case Data.P_CHANGE_PW:{
					gui.disposePassword((boolean) data.getResult());
					break;}
				case Data.A_SERVER_NOTICE:{
					if(data.getServerStatus().get("isOpen")){
						if(data.getServerStatus().get("isCreatable")){		//수강신청 가능으로 바뀌면
							JOptionPane.showMessageDialog(gui, "강의개설 기간이 시작되었습니다");
							gui.setButton(data.getServerStatus().get("isCreatable"));
						}else {													//수강신청 불가능으로 바뀌면
							JOptionPane.showMessageDialog(gui, "강의개설 기간이 끝났습니다.");
							gui.setButton(data.getServerStatus().get("isCreatable"));
						}
					}else {
						JOptionPane.showMessageDialog(gui, "서버가 닫혔습니다.");
						System.exit(0);
					}
					break;}
				case Data.C_LOG_OUT:{
					isOn = false;
					break;}
				}
			}//while loop
			System.out.println("ClientProfessorThread> 스레드 종료되었음.");
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}
}
