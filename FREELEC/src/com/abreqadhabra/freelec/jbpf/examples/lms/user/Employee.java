package com.abreqadhabra.freelec.jbpf.examples.lms.user;

public class Employee extends UserImpl{

	protected String department;

	public Employee(){}

	public Employee(String userNo,	String name,	 String age,
								    String ssn, String address, String  department){
		super.userNo = userNo;
		super.name = name;
		super.age = age;
		super.ssn = ssn;
		super.address = address;
		this.department = department;
	}

	public void setDepartment(String department){
		this.department = department;
	}

	public String getDepartment(){
		return this.department;
	}

	public String toString()	{
		StringBuffer string = new StringBuffer();
		string.append("lms_department,  ");
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
		string.append(this.department);

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
		addInfo[0] = this.getDepartment();
		addInfo[1] = super.getSsn();
		return addInfo;
	}

}//class Employee