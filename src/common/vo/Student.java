package common.vo;

import java.io.Serializable;

public class Student extends Human implements Serializable {

	private String grade;
	private int nowCredit;
	private final int maxCredit = 18;

	
	public Student (){
		
	}
	public Student(String name, String regNo, String major, String grade, int nowCredit) {
		super(name, regNo, major);
		this.grade = grade;
		this.nowCredit = nowCredit;
	}

	@Override
	public String toString() {
		return super.toString()
				+ "Student [grade=" + grade + ", nowCredit=" + nowCredit + ", maxCredit=" + maxCredit + "]";
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public int getNowCredit() {
		return nowCredit;
	}

	public void setNowCredit(int nowCredit) {
		this.nowCredit = nowCredit;
	}

	public int getMaxCredit() {
		return maxCredit;
	}
	
	

}