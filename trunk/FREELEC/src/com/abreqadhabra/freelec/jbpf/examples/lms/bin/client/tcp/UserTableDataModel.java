package com.abreqadhabra.freelec.jbpf.examples.lms.bin.client.tcp;

import com.abreqadhabra.freelec.jbpf.examples.lms.user.*;

import javax.swing.table.*;
import java.util.*;

 public  class UserTableDataModel extends AbstractTableModel {

	protected String[] userNo;
	protected String[] name;
	protected String[] ssn;
    private boolean DEBUG = false;

	private  String[] names = {"회원번호", "이름", "주민등록번호"};
	int rowLength;

   public UserTableDataModel(ArrayList tdm) {
		rowLength =  tdm.size();
        Object [] tdmKeys = tdm.toArray();
		userNo = new String[tdmKeys.length];
		name = new String[tdmKeys.length];
		ssn = new String[tdmKeys.length];
		UserImpl  temp =null;
		for(int i = 0; i<rowLength;i++){
				temp = (UserImpl) tdm.get(i);		
				userNo[i] = temp.getUserNo();
				name[i] = temp.getName();
				ssn[i] = temp.getSsn(	);	
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
				return ssn [row];
	}

    return null;
  }

  public String getColumnName(int col) {
    return names[col];
  }

}