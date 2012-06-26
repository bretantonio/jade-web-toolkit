package com.abreqadhabra.freelec.jbpf.examples.lms.bin.server.db;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSetMetaData;

public class DerbyEmbeddedTest {
  // Derby Driver
  private static String driver = "org.apache.derby.jdbc.EmbeddedDriver";

   // Derby Connect URL
  private static String dbURL = "jdbc:derby:sampleDB;create=true";

   // jdbc Connection
  private static Connection conn = null;

   private static Statement stmt = null;

   // main method
  public static void main(String[] args) throws Exception {
    System.out.println("Connect..");
    createConnection();

     if (!existTable()) {
      System.out.println("Create Table..");
      createTable();
    }

     System.out.println("Insert 00000001..");
    insertRestaurants("00000001", "1111", "EMPLOYEE_01");

     System.out.println("Insert 00000002..");
    insertRestaurants("00000002", "2222", "EMPLOYEE_02");

     System.out.println("Insert 00000003..");
    insertRestaurants("00000003", "3333", "EMPLOYEE_03");

     System.out.println("Select..");
    selectRestaurants();

     System.out.println("Disconnect..");
    disconnect();
  }

   private static void createTable() {
    try {
      stmt = conn.createStatement();
      stmt.execute("CREATE TABLE T_EMPLOYEE (" + "ID CHAR(8) NOT NULL,"
          + "PWD VARCHAR(10)," + "NAME VARCHAR(20),"
          + "DATE TIMESTAMP" + ")");
      stmt.close();
    } catch (SQLException sqlExcept) {
      sqlExcept.printStackTrace();
    }
  }

   private static boolean existTable() {
    try {
      stmt = conn.createStatement();
      ResultSet results = stmt
          .executeQuery("select tablename from sys.systables WHERE tablename='T_EMPLOYEE'");
      boolean isExist = results.next();
      results.close();
      stmt.close();

       return isExist;
    } catch (SQLException sqlExcept) {
      sqlExcept.printStackTrace();
    }

     return false;
  }

   private static void createConnection() {
    try {
      Class.forName(driver).newInstance();
      // Get a connection
      conn = DriverManager.getConnection(dbURL);
    } catch (Exception except) {
      except.printStackTrace();
    }
  }

   private static void insertRestaurants(String id, String password,
      String name) {
    try {
      stmt = conn.createStatement();
      stmt.execute("insert into T_EMPLOYEE values ('" + id + "','"
          + password + "','" + name + "', CURRENT TIMESTAMP)");
      stmt.close();
    } catch (SQLException sqlExcept) {
      sqlExcept.printStackTrace();
    }
  }

   private static void selectRestaurants() {
    try {
      stmt = conn.createStatement();
      ResultSet results = stmt.executeQuery("select * from T_EMPLOYEE");
      ResultSetMetaData rsmd = results.getMetaData();
      int numberCols = rsmd.getColumnCount();
      for (int i = 1; i <= numberCols; i++) {
        // print Column Names
        System.out.print(rsmd.getColumnLabel(i) + "	 ");
      }

       System.out
          .println(" -------------------------------------------------------------");

       while (results.next()) {
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
          System.out.print(results.getString(i) + "	 ");
        }

         System.out.println();
      }
      results.close();
      stmt.close();
    } catch (SQLException sqlExcept) {
      sqlExcept.printStackTrace();
    }
  }

   private static void disconnect() {
    try {
      if (stmt != null) {
        stmt.close();
      }
      if (conn != null) {
        DriverManager.getConnection(dbURL + ";shutdown=true");
        conn.close();
      }
    } catch (SQLException sqlExcept) {
    }
  }
}