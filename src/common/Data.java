package common;

import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import common.vo.Human;
import common.vo.Lecture;

public class Data implements Serializable{
	private static final long serialVersionUID = 1L;
	private int command;
	private Object result;
	private Human human;
	private transient ObjectOutputStream oos;
	private ArrayList<Lecture> lectureList;
	private Lecture lecture;
	private HashMap<String, Boolean> serverStatus = new HashMap<>();
	private boolean isConnected;
	
	private ChatData chatData;
	
	public static final int P_LOG_IN = -1;
	public static final int P_LOG_OUT = -2;
	public static final int P_CREATE_LECTURE = -3;
	public static final int P_UPDATE_LECTURE = -4;
	public static final int P_DELETE_LECTURE = -5;
	public static final int P_GET_A_LECTURE = -6;
	public static final int P_GET_ALL_LECTURE_LIST = -7;
	public static final int P_GET_ALL_LECTURE_LIST_FOR_TIME = -8;
	public static final int P_CHANGE_PW = -9;
	
	
	public static final int S_LOG_IN = 1;
	public static final int S_LOG_OUT = 2;
	public static final int S_REGISTER_LECTURE = 3;
	public static final int S_SEARCH_LECTURE = 4;
	public static final int S_DROP_LECTURE = 5;
	public static final int S_SEARCH_MY_LECTURE = 6;
	public static final int S_GET_MY_CREDIT = 7;
	public static final int S_GET_ALL_LECTURE_LIST_FOR_TIME = 8;
	public static final int S_CHANGE_PW = 9;
	public static final int S_GET_A_LECTURE_WITH_PLAN = 10;

	public static final int S_CHAT_MESSAGE = 50;
	public static final int S_CHAT_JOIN = 51;
	public static final int S_CHAT_EXIT = 52;
	
	
	public static final int C_LOG_OUT = 0;
	
	
	
	public static final int A_SERVER_NOTICE = 100;

	
	public Data(int command) {
		this.command = command;
		serverStatus.put("isOpen", true);
		serverStatus.put("isCreatable", false);
		serverStatus.put("isRegisterable", false);
	}
	
	
	
	
	
	public boolean isConnected() {
		return isConnected;
	}





	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}





	public ChatData getChatData() {
		return chatData;
	}

	public void setChatData(ChatData chatData) {
		this.chatData = chatData;
	}

	public void putServerStatus(String key, boolean value){
		serverStatus.put(key, value);
	}
	
	public HashMap<String, Boolean> getServerStatus() {
		return serverStatus;
	}

	public void setServerStatus(HashMap<String, Boolean> serverStatus) {
		this.serverStatus = serverStatus;
	}

	public Lecture getLecture() {
		return lecture;
	}

	public void setLecture(Lecture lecture) {
		this.lecture = lecture;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public int getCommand() {
		return command;
	}

	public Human getHuman() {
		return human;
	}

	public void setHuman(Human human) {
		this.human = human;
	}

	public ObjectOutputStream getOos() {
		return oos;
	}

	public void setOos(ObjectOutputStream oos) {
		this.oos = oos;
	}

	public ArrayList<Lecture> getLectureList() {
		return lectureList;
	}

	public void setLectureList(ArrayList<Lecture> lectureList) {
		this.lectureList = lectureList;
	}

}
