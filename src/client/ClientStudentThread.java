package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import client.dialog.StudentShowPlanner;
import common.Data;
import common.vo.Lecture;

public class ClientStudentThread implements Runnable{

	private ObjectInputStream ois;
	private ClientStudentGUI gui;
	private boolean isOn = true;
	
	public ClientStudentThread(ClientStudentGUI gui, ObjectInputStream ois) {
		this.gui = gui;
		this.ois = ois;
	}

	@Override
	public void run() {
		try {
			while(isOn){
				Data data = (Data) ois.readObject();
				switch(data.getCommand()){
					case Data.S_SEARCH_LECTURE:{
						ArrayList<Lecture> result = (ArrayList<Lecture>) data.getResult();
						gui.setLectureTable(result);
						break;}
					case Data.S_REGISTER_LECTURE:{
						int result = (int)data.getResult();
						switch(result){
							case 1:	JOptionPane.showMessageDialog(gui, "수강신청 성공!");
								break;
							case -1: JOptionPane.showMessageDialog(gui, "이미 수강신청한 수업입니다.");
								break;
							case -2: JOptionPane.showMessageDialog(gui, "수강학점 초과");
								break;
							case -3: JOptionPane.showMessageDialog(gui, "정원초과");
								break;
							case -4: JOptionPane.showMessageDialog(gui, "해당 시간에 이미 수업이 있습니다.");
								break;
							case 0: JOptionPane.showMessageDialog(gui, "뭐지 0날아옴");
						}
						break;}
					case Data.S_SEARCH_MY_LECTURE:{
						gui.setMyLectureTable((ArrayList<Lecture>) data.getResult());
						break;}
					case Data.S_DROP_LECTURE:{
						if((int) data.getResult() == 3) JOptionPane.showMessageDialog(gui, "취소성공!");
						break;}
					case Data.S_GET_MY_CREDIT:{
						gui.setCredit((int) data.getResult()); 
						break;}
					case Data.S_GET_ALL_LECTURE_LIST_FOR_TIME:{
						gui.setTimeTable((ArrayList<Lecture>) data.getResult());
						break;}
					case Data.S_CHANGE_PW:{
						gui.disposePassword((boolean) data.getResult());
						break;}
					case Data.S_GET_A_LECTURE_WITH_PLAN:{
						new StudentShowPlanner((Lecture) data.getResult(), gui);
						break;}
					
					case Data.A_SERVER_NOTICE:{
						if(data.getServerStatus().get("isOpen")){
							if(data.getServerStatus().get("isRegisterable")){		//수강신청 가능으로 바뀌면
								JOptionPane.showMessageDialog(gui, "수강신청 기간이 시작되었습니다");
								gui.setButton(data.getServerStatus().get("isRegisterable"));
							}else {
								JOptionPane.showMessageDialog(gui, "수강신청 기간이 끝났습니다.");
								gui.setButton(data.getServerStatus().get("isRegisterable"));
							}
					}else {
						JOptionPane.showMessageDialog(gui, "서버가 닫혔습니다.");
						System.exit(0);
					}
						break;}
					case Data.S_CHAT_JOIN:{
						gui.cscUserList(data.getChatData().getUserList());
						gui.setName(data.getChatData().getName());
						gui.appendChat("< <" + data.getChatData().getName(), "> 님이 입장하셨습니다 ^^");
						break;}
					case Data.S_CHAT_MESSAGE:{
						gui.appendChat(data.getChatData().getName(), data.getChatData().getMessage());
						break;}
					case Data.S_CHAT_EXIT:{
						gui.cscUserList(data.getChatData().getUserList());
						gui.appendChat("< <" + data.getChatData().getName(), "> 님이 퇴장하셨습니다 ㅠㅠ");
						break;}
					case Data.C_LOG_OUT:{
						isOn = false;
						break;}
				}
			}//while loop
			System.out.println("ClientStudentThread> 스레드 종료되었음");
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}

}
