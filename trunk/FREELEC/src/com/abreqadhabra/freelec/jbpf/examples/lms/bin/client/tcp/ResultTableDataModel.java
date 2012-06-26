package com.abreqadhabra.freelec.jbpf.examples.lms.bin.client.tcp;

import com.abreqadhabra.freelec.jbpf.examples.lms.user.*;

import javax.swing.table.*;
import java.util.*;

 public  class ResultTableDataModel extends AbstractTableModel {

	protected String[] userNo;
	protected String[] name;
	protected String[] age;
	protected String[] ssn;
	protected String[] address;
	protected String[] studentNo;
	protected String[] course;
	protected String[] department;	
	protected String[] type;	
	
	private boolean DEBUG = false;

	private  String[] names = {"회원번호", "이름 ","나이", "주민등록번호","주소", "학번","교과목","부서", "구분"};
	int rowLength;

   public ResultTableDataModel(ArrayList tdm) {
		rowLength =  tdm.size();
        Object [] tdmKeys = tdm.toArray();
		userNo = new String[tdmKeys.length];
		name = new String[tdmKeys.length];
		age = new String[tdmKeys.length];
		ssn = new String[tdmKeys.length];
		address = new String[tdmKeys.length];
		studentNo = new String[tdmKeys.length];
		course = new String[tdmKeys.length];
		department = new String[tdmKeys.length];
		type = new String[tdmKeys.length];
		
		
		UserImpl  temp =null;
		for(int i = 0; i<rowLength;i++){
				temp = (UserImpl) tdm.get(i);		
				userNo[i] = temp.getUserNo();
				name[i] = temp.getName();
				age[i] = temp.getAge();	
				ssn[i] = temp.getSsn(	);	
				address[i] = temp.getAddress();	
				type[i] = temp.getType();	
				if("lms_student".equals(type[i])){
					studentNo[i] = ((Student) temp).getStudentNo();
					type[i] = "학생";	
				}else	if("lms_professor".equals(type[i])){
					course[i] = ((Professor) temp).getCourse();
					type[i]  = "교수";	
				}else	if("lms_employee".equals(type[i])){
					department[i] = ((Employee)  temp).getDepartment();
					type[i]  = "교직원";	
				}
		}
	}

  public int getColumnCount() {
    return names.length;
  }

  public int getRowCount() {
    return rowLength;
  }

  public Object getValueAt(int row, int col) {
    switch (col){
		case 0:
				return userNo [row];
		case 1:
				return name [row];
		case 2:
				return age [row];
		case 3:
				return ssn [row];
		case 4:
				return address [row];
		case 5:
				return studentNo [row];
		case 6:
				return course [row];		
		case 7:
				return department [row];
		case 8:
				return type [row];
		}

    return null;
  }

  public String getColumnName(int col) {
    return names[col];
  }

}