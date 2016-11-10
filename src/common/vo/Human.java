package common.vo;

import java.io.Serializable;
import java.util.ArrayList;

public class Human implements Serializable {

	private String name;
	private String pw;
	private String regNo;
	private String major;
	private ArrayList<Lecture> lectureList;
	
	
	public Human(){
		
	}
	
	public Human(String name, String regNo, String major) {
		this.name = name;
		this.regNo = regNo;
		this.major = major;
	}
	
	

	public String getPw() {
		return pw;
	}

	public void setPw(String pw) {
		this.pw = pw;
	}

	public boolean addLecture(Lecture l) {
		boolean result = false;
		return result;
	}

	public boolean deleteLecture(Lecture l) {
		boolean result = false;
		return result;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRegNo() {
		return regNo;
	}

	public void setRegNo(String regNo) {
		this.regNo = regNo;
	}

	public String getMajor() {
		return major;
	}

	public void setMajor(String major) {
		this.major = major;
	}

	public ArrayList<Lecture> getLectureList() {
		return lectureList;
	}

	public void setLectureList(ArrayList<Lecture> lectureList) {
		this.lectureList = lectureList;
	}

	@Override
	public String toString() {
		return "Human [name=" + name + ", pw=" + pw + ", regNo=" + regNo + ", major=" + major + ", lectureList="
				+ lectureList + "]";
	}
	
	
	

}
