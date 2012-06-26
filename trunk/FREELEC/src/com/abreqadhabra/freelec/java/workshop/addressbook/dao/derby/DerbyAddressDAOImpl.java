package com.abreqadhabra.freelec.java.workshop.addressbook.dao.derby;

//CloudscapeCustomerDAO implementation of the 
//CustomerDAO interface. This class can contain all
//Cloudscape specific code and SQL statements. 
//The client is thus shielded from knowing 
//these implementation details.

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.abreqadhabra.freelec.java.workshop.addressbook.common.constants.Constants;
import com.abreqadhabra.freelec.java.workshop.addressbook.dao.AddressDAO;
import com.abreqadhabra.freelec.java.workshop.addressbook.domain.Address;
import com.abreqadhabra.freelec.java.workshop.addressbook.domain.ListEntry;

public class DerbyAddressDAOImpl implements AddressDAO {

	Connection dbConnection = null;

    private PreparedStatement preStmtSaveAddress=null;

	private PreparedStatement preStmtGetAddress = null;

	private PreparedStatement preStmtEditAddress = null;

	private PreparedStatement preStmtRemoveAddress = null;

	public DerbyAddressDAOImpl(Connection connection) {
		// initialization
		this.dbConnection = connection;
        try {
			this.preStmtSaveAddress = dbConnection.prepareStatement(Constants.DERBY_ADDRESS_DAO.STR_SQL_SAVE_ADDRESS, Statement.RETURN_GENERATED_KEYS);
			this.preStmtGetAddress  = dbConnection.prepareStatement(Constants.DERBY_ADDRESS_DAO.STR_GET_ADDRESS);
			this.preStmtEditAddress  = dbConnection.prepareStatement(Constants.DERBY_ADDRESS_DAO.STR_EDIT_ADDRESS);
			this.preStmtRemoveAddress  = dbConnection.prepareStatement(Constants.DERBY_ADDRESS_DAO.STR_REMOVE_ADDRESS);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public List<Address> getAddressList() {
		ArrayList<Address> addressList = new ArrayList<Address>();
		Address address = null;
		Statement queryStatement = null;
		ResultSet result = null;

		try {
			queryStatement = dbConnection.createStatement();
			result = queryStatement
					.executeQuery(Constants.DERBY_ADDRESS_DAO.STR_SQL_GET_ADDRESS_LIST);
			while (result.next()) {
				String lastName = result.getString("LASTNAME");
				String firstName = result.getString("FIRSTNAME");
				String middleName = result.getString("MIDDLENAME");
				String phone = result.getString("PHONE");
				String email = result.getString("EMAIL");
				String add1 = result.getString("ADDRESS1");
				String add2 = result.getString("ADDRESS2");
				String city = result.getString("CITY");
				String state = result.getString("STATE");
				String postalCode = result.getString("POSTALCODE");
				String country = result.getString("COUNTRY");
				int id = result.getInt("ID");
				address = new Address(lastName, firstName, middleName, phone,
						email, add1, add2, city, state, postalCode, country, id);
				addressList.add(address);
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
        
        return addressList;

	}

	@Override
	public Address getAddress(int index) {
		Address address = null;
		ResultSet result = null;

		try {
			preStmtGetAddress.clearParameters();
			preStmtGetAddress.setInt(1, index);
	        result = preStmtGetAddress.executeQuery();
			while (result.next()) {
				String lastName = result.getString("LASTNAME");
				String firstName = result.getString("FIRSTNAME");
				String middleName = result.getString("MIDDLENAME");
				String phone = result.getString("PHONE");
				String email = result.getString("EMAIL");
				String add1 = result.getString("ADDRESS1");
				String add2 = result.getString("ADDRESS2");
				String city = result.getString("CITY");
				String state = result.getString("STATE");
				String postalCode = result.getString("POSTALCODE");
				String country = result.getString("COUNTRY");
				int id = result.getInt("ID");
				address = new Address(lastName, firstName, middleName, phone,
						email, add1, add2, city, state, postalCode, country, id);
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
        
        return address;
	}

	
	@Override
	public int saveAddress(Address address) {
	        int id = -1;
	        try {
	        	preStmtSaveAddress.clearParameters();
	            
	        	preStmtSaveAddress.setString(1, address.getLastName());
	        	preStmtSaveAddress.setString(2, address.getFirstName());
	        	preStmtSaveAddress.setString(3, address.getMiddleName());
	        	preStmtSaveAddress.setString(4, address.getPhone());
	        	preStmtSaveAddress.setString(5, address.getEmail());
	        	preStmtSaveAddress.setString(6, address.getAddress1());
	        	preStmtSaveAddress.setString(7, address.getAddress2());
	        	preStmtSaveAddress.setString(8, address.getCity());
	        	preStmtSaveAddress.setString(9, address.getState());
	        	preStmtSaveAddress.setString(10, address.getPostalCode());
	        	preStmtSaveAddress.setString(11, address.getCountry());
	            int rowCount = preStmtSaveAddress.executeUpdate();
	            ResultSet results = preStmtSaveAddress.getGeneratedKeys();
	            if (results.next()) {
	                id = results.getInt(1);
	            }
	            dbConnection.commit();
	        } catch(SQLException sqle) {
	            sqle.printStackTrace();
	        }
	        return id; 

	}

	@Override
	public List<ListEntry> getListEntry() {
		ArrayList<ListEntry> listEntries = new ArrayList<ListEntry>();
		ListEntry listEntry = null;
		Statement queryStatement = null;
		ResultSet result = null;

		try {
			queryStatement = dbConnection.createStatement();
			result = queryStatement
					.executeQuery(Constants.DERBY_ADDRESS_DAO.STR_GET_LIST_ENTRIES);
			while (result.next()) {
                int id = result.getInt(1);
                String lName = result.getString(2);
                String fName = result.getString(3);
                String mName = result.getString(4);
                
                listEntry = new ListEntry(lName, fName, mName, id);
				listEntries.add(listEntry);
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
        
        return listEntries;		
			}

	@Override
	public boolean editAddress(Address address) {
        boolean bEdited = false;
        try {
        	preStmtEditAddress.clearParameters();           
        	preStmtEditAddress.setString(1, address.getLastName());
        	preStmtEditAddress.setString(2, address.getFirstName());
        	preStmtEditAddress.setString(3, address.getMiddleName());
        	preStmtEditAddress.setString(4, address.getPhone());
        	preStmtEditAddress.setString(5, address.getEmail());
        	preStmtEditAddress.setString(6, address.getAddress1());
        	preStmtEditAddress.setString(7, address.getAddress2());
        	preStmtEditAddress.setString(8, address.getCity());
        	preStmtEditAddress.setString(9, address.getState());
        	preStmtEditAddress.setString(10, address.getPostalCode());
        	preStmtEditAddress.setString(11, address.getCountry());
        	preStmtEditAddress.setInt(12, address.getId());
        	preStmtEditAddress.executeUpdate();
            dbConnection.commit();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
            bEdited = true;
            return bEdited;
	}

	@Override
	public boolean removeAddress(int id) {
        boolean bDeleted = false;
        try {
        	preStmtRemoveAddress.clearParameters();
        	preStmtRemoveAddress.setInt(1, id);
        	preStmtRemoveAddress.executeUpdate();
            bDeleted = true;
            dbConnection.commit();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        
        return bDeleted;
		
	}


}
