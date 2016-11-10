package common.vo;

import java.io.Serializable;
import java.util.ArrayList;

public class Lecture implements Serializable{

	private String subject;
	private String subjectCode;		//과목코드 = 교수성+과목명첫글자+ 시분초
	private String oldCode; 		//수정할 때 수정하기 전 원래 과목코드
	private String major;
	private String type;
	private String grade;
	private String credit;
	private String plan;
	private int maxStudentNum;
	private int nowStudentNum;
	private int vacantStudentNum;
	private String time;			//월-1,2,3 형태
	private String time_index; 		//요일구분
	private String module; //교시 모듈
	private String room;
	private Professor professor;
	private ArrayList<Student> studentList;
	
	
	
	

	public String getPlan() {
		return plan;
	}



	public void setPlan(String plan) {
		this.plan = plan;
	}



	public boolean addStudent(Student s) {
		boolean result = false;
		return result;
	}
	
	

	public int getVacantStudentNum() {
		return vacantStudentNum;
	}



	public void setVacantStudentNum(int vacantStudentNum) {
		this.vacantStudentNum = vacantStudentNum;
	}



	public boolean deleteStudent(Student s) {
		boolean result = false;
		return result;
	}

	@Override
	public String toString() {
		return "Lecture [subject=" + subject + ", subjectCode=" + subjectCode + ", major=" + major + ", type=" + type
				+ ", grade=" + grade + ", maxStudentNum=" + maxStudentNum + ", nowStudentNum=" + nowStudentNum
				+ ", time=" + time + ", room=" + room + ", professor=" + professor + ", studentList=" + studentList + ", time_index" + time_index
				+ "]";
	}

	
	
	
	
	
	public String getCredit() {
		return credit;
	}

	public void setCredit(String credit) {
		this.credit = credit;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getTime_index() {
		return time_index;
	}

	public void setTime_index(String time_index) {
		this.time_index = time_index;
	}

	public String getOldCode() {
		return oldCode;
	}

	public void setOldCode(String oldCode) {
		this.oldCode = oldCode;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getSubjectCode() {
		return subjectCode;
	}

	public void setSubjectCode(String subjectCode) {
		this.subjectCode = subjectCode;
	}

	public String getMajor() {
		return major;
	}

	public void setMajor(String major) {
		this.major = major;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public int getMaxStudentNum() {
		return maxStudentNum;
	}

	public void setMaxStudentNum(int maxStudentNum) {
		this.maxStudentNum = maxStudentNum;
	}

	public int getNowStudentNum() {
		return nowStudentNum;
	}

	public void setNowStudentNum(int nowStudentNum) {
		this.nowStudentNum = nowStudentNum;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public Professor getProfessor() {
		return professor;
	}

	public void setProfessor(Professor professor) {
		this.professor = professor;
	}

	public ArrayList<Student> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<Student> studentList) {
		this.studentList = studentList;
	}
	
	

}
