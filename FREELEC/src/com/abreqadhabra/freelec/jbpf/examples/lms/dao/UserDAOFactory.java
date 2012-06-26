package com.abreqadhabra.freelec.jbpf.examples.lms.dao;

import java.io.IOException;
import java.sql.SQLException;

public final class UserDAOFactory {	
    
	private static UserDAOFactory _instance = null;
    public static int DATABASE_DERBY = 1;    
	private UserDAOFactory(){}
    
    public static synchronized UserDAOFactory instance(){
        if(_instance == null ){
            _instance = new UserDAOFactory();
        }

        return _instance;
    }
    
    public IUserDAO getUserDAO(int dbType) throws SQLException, IOException{
        if(dbType ==DATABASE_DERBY){
           return(new UserDAOlmplDerby());

        }else{
            	return null;
        }
    }

}//class UserDAOFactory