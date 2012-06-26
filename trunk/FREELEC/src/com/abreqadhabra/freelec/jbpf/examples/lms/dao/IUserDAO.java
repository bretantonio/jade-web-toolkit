package com.abreqadhabra.freelec.jbpf.examples.lms.dao;

import com.abreqadhabra.freelec.jbpf.examples.lms.user.*;

import java.util.*;

public interface IUserDAO {
	public String getSequence(String type);
	public String createUser(String type, String [] root, String [] child); 
	public String dropUser(String criteria);
	public ArrayList findUser(String parameter, String criteria);
	public String modifyUser(String category, String sequenceCategory, String [] root, String [] child, String userNo);
	public ArrayList listUser();
}//interface UserDAO
