package com.abreqadhabra.freelec.jbpf.examples.lms.user;

public class Professor extends UserImpl{

	protected String course;

	public Professor(){}

	public Professor(String userNo,	String name,	 String age,
								    String ssn, String address, String  course){
		super.userNo = userNo;
		super.name = name;
		super.age = age;
		super.ssn = ssn;
		super.address = address;
		this.course = course;
	}

	public void setCourse(String course){
		this.course = course;
	}

	public String getCourse(){
		return this.course;
	}

	public String toString()	{
		StringBuffer string = new StringBuffer();
		string.append("lms_professor,  ");
		string.append(super.userNo);
		string.append(",  ");
		string.append(super.name);
		string.append(",  ");
		string.append(super.age);
		string.append(",  ");
		string.append(super.ssn);
		string.append(",  ");
		string.append(super.address);
		string.append(",  ");
		string.append(this.course);

		return string.toString();
	}

	public String [] getDefaultInfo(){
		String [] defaultInfo = new String [4] ;
		defaultInfo[0] = super.getName();
		defaultInfo[1] = super.getAge();
		defaultInfo[2] = super.getAddress();
		defaultInfo[3] = super.getSsn();
		return defaultInfo;
	}

	public String [] getAddInfo(){
		String [] addInfo = new String [2] ;
		addInfo[0] = this.getCourse();
		addInfo[1] = super.getSsn();
		return addInfo;
	}

}//class Professor