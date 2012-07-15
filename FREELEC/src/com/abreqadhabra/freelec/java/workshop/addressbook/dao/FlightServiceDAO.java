package com.abreqadhabra.freelec.java.workshop.addressbook.dao;

import com.abreqadhabra.freelec.java.workshop.addressbook.domain.DataInfo;
import com.abreqadhabra.freelec.java.workshop.addressbook.domain.FieldInfo;


public interface FlightServiceDAO {

    int getRecordCount();

    DataInfo getRecord(int id);

    FieldInfo[] getFieldInfos();

    DataInfo findByFlightNumber(String flightNumber);

    DataInfo[] criteriaFind(String criteria);

}
