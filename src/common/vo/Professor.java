package common.vo;

import java.io.Serializable;

public class Professor extends Human implements Serializable {
	
	public Professor(){
		
	}
	public Professor(String name, String regNo, String major) {
		super(name, regNo, major);
	}
	@Override
	public String toString() {
		return super.toString();
	}
	
	

	
}