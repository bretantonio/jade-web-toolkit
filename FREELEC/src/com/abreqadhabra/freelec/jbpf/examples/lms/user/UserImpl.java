package com.abreqadhabra.freelec.jbpf.examples.lms.user;

public class UserImpl implements IUser, java.io.Serializable{

	protected String userNo;
	protected String name;
	protected String age;
	protected String ssn;
	protected String address;
	protected String type;

	public UserImpl(){}

	public UserImpl(String userNo, String name, 
								   String age,	String ssn, String address){
		this.userNo = userNo;
		this.name = name;
		this.age = age;
		this.ssn = ssn;
		this.address = address;
	}
	
	public String getUserNo() {
		return userNo;
	}
	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}

	public String getSsn() {
		return ssn;
	}
	public void setSsn(String ssn) {
		this.ssn = ssn;
	}

	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public String toString(){
		StringBuffer string = new StringBuffer();
		string.append(type + ",  ");
		string.append(userNo);
		string.append(",  ");
		string.append(name);
		string.append(",  ");
		string.append(age);
		string.append(",  ");
		string.append(ssn);
		string.append(",  ");
		string.append(address);

		return string.toString();
	}	

}//class UserImpl
