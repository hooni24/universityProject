package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import common.ChatData;
import common.Data;
import common.vo.Human;
import common.vo.Lecture;
import common.vo.Professor;
import common.vo.Student;

public class ServerThread implements Runnable {

	private Socket client;
	private ServerDB db;
	private ServerGUI gui;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private static ArrayList<Data> currentUserList = new ArrayList<>();	//안에 담긴 Data에는 Result에 누구인지 해당객체 전체정보, oos가 담겨있음
	private static HashMap<String, String> businessList = new HashMap<>();	//경영대 채팅방 참가자 regNo, name 목록
	private static HashMap<String, String> constructList = new HashMap<>();	//건축과 채팅방 참가자 regNo, name 목록
	private static HashMap<String, String> nursingList = new HashMap<>();	//간호과 채팅방 참가자 regNo, name 목록
	private static HashMap<String, String> japaneseList = new HashMap<>();	//일문과 채팅방 참가자 regNo, name 목록
	private static HashMap<String, String> computerList = new HashMap<>();	//컴공과 채팅방 참가자 regNo, name 목록
	private boolean isConnected = true;
	
	public ServerThread(Socket client, ServerDB db, ServerGUI gui) {
		this.client = client;
		this.db = db;
		this.gui = gui;
		try {
			oos = new ObjectOutputStream(client.getOutputStream());
			ois = new ObjectInputStream(client.getInputStream());
			System.out.println("ServerThread> 스트림 개통");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}//const

	@Override
	public void run() {
		while(isConnected){
			try {
				Data data = (Data) ois.readObject();
				if(data.getCommand() < 0){				//Professor가 보낸 명령 
					switch(data.getCommand()){
					case Data.P_LOG_IN:{
						Human result = db.logInCheck(data.getHuman());
						if(result != null){
							if(connectedCheck(data.getHuman())){
								data.setConnected(true);
								sendData(data);
								gui.appendLog(client.getInetAddress().getHostName() + " 에서 교수 로그인 시도(실패) : " + data.getHuman().getName());
							}else {
								data.setResult(result);
								data.setHuman(result);
								data.putServerStatus("isOpen", gui.isOpen());
								data.putServerStatus("isCreatable", gui.isCreatable());
								data.putServerStatus("isRegisterable", gui.isRegisterable());
								data.setOos(oos);
								data.setConnected(false);
								currentUserList.add(data);
								sendData(data);
								gui.appendLog(client.getInetAddress().getHostName() + " 에서 교수 로그인 시도(성공) : " + data.getHuman().getName());
							}
						}else {
							data.setResult(null);
							sendData(data);
							gui.appendLog(client.getInetAddress().getHostName() + " 에서 교수 로그인 시도(실패) : " + data.getHuman().getName());
						}
						break;}
					case Data.P_CREATE_LECTURE:{
						int result = db.createLecture((Professor)data.getHuman(), data.getLecture());
						data.setResult(result);
						sendData(data);
						break;}
					case Data.P_GET_ALL_LECTURE_LIST:{
						ArrayList<Lecture> result = db.getAllLecture((Professor) data.getHuman());
						data.setLectureList(result);
						sendData(data);
						break;}
					case Data.P_DELETE_LECTURE:{
						boolean result = db.deleteLecture(data.getLecture());
						data.setResult(result);
						sendData(data);
						break;}
					case Data.P_UPDATE_LECTURE:{
						int result = db.updateLecture(data.getLecture());
						data.setResult(result);
						sendData(data);
						break;}
					case Data.P_GET_A_LECTURE:{
						ArrayList<Student> result = db.getStudentList(data.getLecture());
						data.setResult(result);
						sendData(data);
						break;}
					case Data.P_GET_ALL_LECTURE_LIST_FOR_TIME:{
						ArrayList<Lecture> result = db.getAllLecture((Professor) data.getHuman());
						data.setLectureList(result);
						sendData(data);
						break;}
					case Data.P_CHANGE_PW:{
						boolean result = db.changePw(data.getHuman());
						data.setResult(result);
						sendData(data);
						break;}
					}//switch
				}else if(data.getCommand() > 0){		//Student가 보낸 명령
					switch(data.getCommand()){
					case Data.S_LOG_IN:{
						Human result = db.logInCheck(data.getHuman());
						if(result != null){
							if(connectedCheck(data.getHuman())){
								data.setConnected(true);
								data.setResult(result);
								sendData(data);
								gui.appendLog(client.getInetAddress().getHostName() + " 에서 학생 로그인 시도(실패) : " + data.getHuman().getName());
							}else {
								data.setResult(result);
								data.setHuman(result);
								data.putServerStatus("isOpen", gui.isOpen());
								data.putServerStatus("isCreatable", gui.isCreatable());
								data.putServerStatus("isRegisterable", gui.isRegisterable());
								data.setOos(oos);
								data.setConnected(false);
								currentUserList.add(data);
								sendData(data);
								gui.appendLog(client.getInetAddress().getHostName() + " 에서 학생 로그인 시도(성공) : " + data.getHuman().getName());
							}
						}else {
							data.setResult(null);
							data.setConnected(false);
							sendData(data);
							gui.appendLog(client.getInetAddress().getHostName() + " 에서 학생 로그인 시도(실패) : " + data.getHuman().getName());
						}
						break;}
					case Data.S_SEARCH_LECTURE:{
						ArrayList<Lecture> result = db.searchLecture(data.getLecture());
						data.setResult(result);
						sendData(data);
						break;}
					case Data.S_REGISTER_LECTURE:{
						int result = db.registerLecture(data.getLecture(),(Student)data.getHuman());
						data.setResult(result);
						sendData(data);
						break;
					}
					case Data.S_SEARCH_MY_LECTURE:{
						ArrayList<Lecture> result = db.getAllLectureForMyTable((Student) data.getHuman());
						data.setResult(result);
						sendData(data);
						break;}
					case Data.S_DROP_LECTURE:{
						int result = db.dropLecture((Student) data.getHuman(), data.getLecture());
						data.setResult(result);
						sendData(data);
						break;}
					case Data.S_GET_MY_CREDIT:{
						int result = db.getCredit((Student) data.getHuman());
						data.setResult(result);
						sendData(data);
						break;}
					case Data.S_GET_ALL_LECTURE_LIST_FOR_TIME:{
						ArrayList<Lecture> result = db.getAllLecture((Student) data.getHuman());	
						data.setResult(result);
						sendData(data);
						break;}
					case Data.S_CHANGE_PW:{
						boolean result = db.changePw(data.getHuman());
						data.setResult(result);
						sendData(data);
						break;}
					case Data.S_GET_A_LECTURE_WITH_PLAN:{
						Lecture result = db.getLectureWithPlan(data.getLecture());
						data.setResult(result);
						sendData(data);
						break;}
					
					//////////////////////////////이 이하는 학생들의 채팅
					case Data.S_CHAT_JOIN:{
						ChatData chatData = data.getChatData();
						switch(chatData.getTitle()){
						case "경영학과":
							joinUser(chatData, businessList);
							broadcastToMajor(data, businessList);
							break;
						case "건축학과":
							joinUser(chatData, constructList);
							broadcastToMajor(data, constructList);
							break;
						case "간호학과":
							joinUser(chatData, nursingList);
							broadcastToMajor(data, nursingList);
							break;
						case "일어일문학과":
							joinUser(chatData, japaneseList);
							broadcastToMajor(data, japaneseList);
							break;
						case "컴퓨터공학과":
							joinUser(chatData, computerList);
							broadcastToMajor(data, computerList);
							break;
						}
						break;}
					
					case Data.S_CHAT_MESSAGE:{
						ChatData chatData = data.getChatData();
						switch(chatData.getTitle()){
						case "경영학과":
							chatData.setName(businessList.get(chatData.getRegNo()));
							broadcastToMajor(data, businessList);
							break;
						case "건축학과":
							chatData.setName(constructList.get(chatData.getRegNo()));
							broadcastToMajor(data, constructList);
							break;
						case "간호학과":
							chatData.setName(nursingList.get(chatData.getRegNo()));
							broadcastToMajor(data, nursingList);
							break;
						case "일어일문학과":
							chatData.setName(japaneseList.get(chatData.getRegNo()));
							broadcastToMajor(data, japaneseList);
							break;
						case "컴퓨터공학과":
							chatData.setName(computerList.get(chatData.getRegNo()));
							broadcastToMajor(data, computerList);
							break;
						}
						break;}
					
					case Data.S_CHAT_EXIT:{
						ChatData chatData = data.getChatData();
						switch(chatData.getTitle()){
						case "경영학과":
							chatData.setName(businessList.get(chatData.getRegNo()));
							businessList.remove(chatData.getRegNo());
							broadcastToMajor(data, businessList);
							break;
						case "건축학과":
							chatData.setName(constructList.get(chatData.getRegNo()));
							constructList.remove(chatData.getRegNo());
							broadcastToMajor(data, constructList);
							break;
						case "간호학과":
							chatData.setName(nursingList.get(chatData.getRegNo()));
							nursingList.remove(chatData.getRegNo());
							broadcastToMajor(data, nursingList);
							break;
						case "일어일문학과":
							chatData.setName(japaneseList.get(chatData.getRegNo()));
							japaneseList.remove(chatData.getRegNo());
							broadcastToMajor(data, japaneseList);
							break;
						case "컴퓨터공학과":
							chatData.setName(computerList.get(chatData.getRegNo()));
							computerList.remove(chatData.getRegNo());
							broadcastToMajor(data, computerList);
							break;
						}
						break;}
					
					}//switch
				}//if-else(Professor냐 Student냐)
				
				if(data.getCommand() == Data.C_LOG_OUT){		//공통 로그아웃
					for (int i = 0; i < currentUserList.size(); i++) {
						Data d = currentUserList.get(i);
						if(d.getHuman().getRegNo().equals(data.getHuman().getRegNo())){
							currentUserList.remove(i);
							break;
						}
					}
					oos.writeObject(data);
					oos.reset();
					gui.appendLog(client.getInetAddress().getHostName() + " 에서 로그아웃 : " + data.getHuman().getName());
				}
			} catch (ClassNotFoundException | IOException e) {
				isConnected = false;
				System.out.println("접속종료");
				gui.appendLog(client.getInetAddress().getHostName() + " 접속종료");
			}//t-c
		}
	}//run()
	
	
	/**
	 * 해당 regno주인이 이미 접속중인지 판별
	 * 접속중이면 true, 접속중 아니면 false반환
	 * @return
	 */
	private boolean connectedCheck(Human h) {
		if(h instanceof Professor){
			for (int i = 0; i < currentUserList.size(); i++) {
				if(currentUserList.get(i).getHuman().getRegNo().equals(h.getRegNo())){
					return true;
				}
			}
		}else {
			for (int i = 0; i < currentUserList.size(); i++) {
				if(currentUserList.get(i).getHuman().getRegNo().equals(h.getRegNo())){
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 이름중복 검사하고 동일이름에 넘버링
	 * @param chatData
	 */
	
	private int num;
	
	public void joinUser(ChatData chatData, HashMap<String, String> someMap){
		while(true){
			if(someMap.containsValue(chatData.getName() + num)){
				num++;
			}else {
				someMap.put(chatData.getRegNo(), chatData.getName() + num);
				chatData.setName(chatData.getName() + num);		//넘버링된 이름 새로 넣음
				break;
			}
		}
	}
	
	public void sendData(Data data){
		try {
			oos.writeObject(data);
			oos.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void resetList(){
		currentUserList = new ArrayList<>();
	}
	
	
	public static int getCurrentUserCount() {
		return currentUserList.size();
	}

	/**
	 * Data객체를 접속중인 유저중 학생에게 날림
	 * 서버상태 변경 공지용
	 * @param d
	 */
	public void broadCastToStudent(){
		Data d = new Data(Data.A_SERVER_NOTICE);
		d.putServerStatus("isOpen", gui.isOpen());
		d.putServerStatus("isCreatable", gui.isCreatable());
		d.putServerStatus("isRegisterable", gui.isRegisterable());
		if(currentUserList.size() > 0){
			for (Data data : currentUserList) {
				try {
					if(data.getHuman() instanceof Student){
						data.getOos().writeObject(d);
						data.getOos().reset();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}//broadCastToStudent()
	
	/**
	 * Data객체를 접속중인 유저중 교수에게 날림
	 * 서버상태 변경 공지용
	 * @param d
	 */
	public void broadCastToProfessor(){
		Data d = new Data(Data.A_SERVER_NOTICE);
		d.putServerStatus("isOpen", gui.isOpen());
		d.putServerStatus("isCreatable", gui.isCreatable());
		d.putServerStatus("isRegisterable", gui.isRegisterable());
		if(currentUserList.size() > 0){
			for (Data data : currentUserList) {
				try {
					if(data.getHuman() instanceof Professor){
						data.getOos().writeObject(d);
						data.getOos().reset();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}//broadCastToStudent()
	
	/**
	 * 전체 접속 유저에게 Data를 날림
	 * @param d
	 */
	public void broadCasting(Data d){
		for (Data data : currentUserList) {
			try {
				data.getOos().writeObject(d);
				data.getOos().reset();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void broadcastToMajor(Data d, HashMap<String, String> majorList){
		Object[] regNoArray = majorList.keySet().toArray();
		Collection userCol = majorList.values();
		ArrayList userList = new ArrayList<>();
		for (Object object : userCol) {			//유저 이름만 뽑아 만드는 어레이리스트
			userList.add(object);
		}
		d.getChatData().setUserList(userList);		//챗데이터에 현재 접속중인 사람 이름만 담아서 보냄
		
		for (Data data : currentUserList) {
			for(int i = 0; i < majorList.size(); i++){
				if(data.getHuman().getRegNo().equals(regNoArray[i])){
					try {
						data.getOos().writeObject(d);
						data.getOos().reset();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
}//class