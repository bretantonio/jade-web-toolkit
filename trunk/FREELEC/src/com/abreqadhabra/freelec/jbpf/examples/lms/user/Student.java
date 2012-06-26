package com.abreqadhabra.freelec.jbpf.examples.lms.user;

public class Student  extends UserImpl{

	protected String  studentNo;

	public Student(){}

	public Student(String userNo,	String name,	 String age,
							   String ssn, String address, String  studentNo){
		super.userNo = userNo;
		super.name = name;
		super.age = age;
		super.ssn = ssn;
		super.address = address;
		this.studentNo = studentNo;
	}

	public void setStudentNo(String studentNo){
		this.studentNo = studentNo;
	}

	public String getStudentNo(){
		return this.studentNo;
	}

	public String toString()	{
		StringBuffer string = new StringBuffer();
		string.append("lms_student,  ");
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
		string.append(this.studentNo);

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
		addInfo[0] = this.getStudentNo();
		addInfo[1] = super.getSsn();
		return addInfo;
	}


}//class Student