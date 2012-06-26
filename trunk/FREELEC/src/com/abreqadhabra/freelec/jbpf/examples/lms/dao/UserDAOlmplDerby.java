package com.abreqadhabra.freelec.jbpf.examples.lms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.sql.DataSource;

import com.abreqadhabra.freelec.jbpf.examples.lms.bin.server.db.EmbeddedJavaDB;

//import org.apache.commons.dbcp.BasicDataSource;

public class UserDAOlmplDerby implements IUserDAO {

	Statement stmt = null;
	PreparedStatement pstmt = null;
	ResultSet rset = null;
	Connection conn = null;

	public UserDAOlmplDerby() {
		System.out
				.println("\n========================================================");
		System.out.println("Setting up data source.");
		// dataSource =
		// setupDataSource("jdbc:oracle:thin:@127.0.0.1:1521:haksa");
		conn = getConnection();
		System.out
				.println("========================================================");
	}

	private Connection getConnection() {

		return EmbeddedJavaDB.createConnection();

	}

	// public static DataSource setupDataSource(String connectURI) {
	// BasicDataSource ds = new BasicDataSource();
	// ds.setDriverClassName("oracle.jdbc.driver.OracleDriver");
	// ds.setUsername("haksa01");
	// ds.setPassword("oracle");
	// ds.setUrl(connectURI);
	// ds.setMaxActive(100);
	// ds.setMaxWait(5000);
	// ds.setDefaultAutoCommit(false);
	// ds.setDefaultReadOnly(false);
	// // ds.setValidationQuery("SELECT * FROM haksadev.tab");
	// return ds;
	// }

	private static void close(Connection conn, PreparedStatement pstmt,
			Statement stmt, ResultSet rs) {

		try {
			if (conn != null) {
				conn.close();
			}
			if (pstmt != null) {
				pstmt.close();
			}
			if (stmt != null) {
				stmt.close();
			}
			if (rs != null) {
				rs.close();
			}

		} catch (java.lang.Exception e) {
			System.out
					.println("Can not close Connection or Statement or ResultSet");
			e.printStackTrace();
		}

	}

	public static void printDataSourceStats(DataSource ds) throws SQLException {
		// BasicDataSource bds = (BasicDataSource) ds;
		// System.out.println("\tNumActive: " + bds.getNumActive());
		// System.out.println("\tNumIdle: " + bds.getNumIdle());
	}

	public static void shutdownDataSource(DataSource ds) throws SQLException {
		// BasicDataSource bds = (BasicDataSource) ds;
		// bds.close();
	}

	public void printResultSet(ResultSet rs) {
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			int numberOfColumns = rsmd.getColumnCount();
			System.out.println(" total column:" + numberOfColumns);
			System.out
					.println("\n========================================================");
			for (int i = 1; i <= numberOfColumns; i++) {
				System.out.print(rsmd.getColumnName(i) + "\t");
			}
			System.out
					.println("\n========================================================");
			while (rs.next()) {
				for (int j = 1; j <= numberOfColumns; j++) {
					System.out.print(rs.getString(j) + "\t");
				}
				System.out
						.println("\n--------------------------------------------------------");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ResultSet preparedStatementQuery(Connection conn,
			String preparedQuery, Object value, Object[] values) {
		try {
			System.out.println("2. Connection stats.");
			// printDataSourceStats(dataSource);
			System.out.println("3. Creating statement.");
			System.out.println("\t" + preparedQuery);
			pstmt = conn.prepareStatement(preparedQuery);
			if (value != null) {
				if (value instanceof String) {
					pstmt.setString(1, (String) value);
				} else if (value instanceof Integer) {
					int val = ((Integer) value).intValue();
					pstmt.setInt(1, val);
				}
			} else if (values != null) {
				for (int i = 0; i < values.length; i++) {
					int idx = i + 1;
					if (values[i] instanceof String) {
						pstmt.setString(idx, (String) values[i]);
					} else if (values[i] instanceof Integer) {
						int val = ((Integer) values[i]).intValue();
						pstmt.setInt(idx, val);
					}
				}
			}
			System.out.println("4. Executing statement.");
			rset = pstmt.executeQuery();
			return rset;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getSequence(String type) {
		Connection conn = null;
		ResultSet result = null;
		int seq = 0;
		try {
			System.out.println("\n1. Creating connection.");
			// conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			StringBuffer getSequenceQuery = new StringBuffer();
			//getSequenceQuery.append("SELECT haksadev." + type + "_seq.nextval FROM DUAL ");
			getSequenceQuery.append("SELECT count(*)  FROM LMS_USER ");
			result = preparedStatementQuery(conn, getSequenceQuery.toString(),
					null, null);
			conn.commit();
			conn.setAutoCommit(true);
			// System.out.print("5. Results:");
			// printResultSet(rset);
			if (result.next()) {
				seq = result.getInt(1);
			}
		} catch (Exception e) {
			try {
				if (conn != null) {
					conn.rollback();
					System.out.println("rollback completely");
				}
			} catch (Exception e2) {
				System.out.println("Can not rollback completely.");
			} finally {
				close(conn, pstmt, null, result);
			}
			e.printStackTrace();
		}
		return new Integer(seq+1).toString();
	}

	public String createUser(String type, String[] root, String[] child) {
		Connection conn = null;
		ResultSet result = null;
		String msg = UserConstants.CREATE_USER_MESSAGE;
		try {
			System.out.println("\n1. Creating connection.");
			conn = EmbeddedJavaDB.createConnection();
			conn.setAutoCommit(false);
			StringBuffer findDuplicateIDQuery = new StringBuffer();
			findDuplicateIDQuery.append("SELECT ");
			findDuplicateIDQuery.append("ssn ");
			findDuplicateIDQuery.append("FROM haksadev.lms_user ");
			findDuplicateIDQuery.append("WHERE ssn = ?");
			result = preparedStatementQuery(conn,
					findDuplicateIDQuery.toString(), root[2], null);
			boolean isDuplicate = result.next();
			if (isDuplicate) {
				msg = UserConstants.DUPLICATE_ERROR_MESSAGE;
				throw new com.abreqadhabra.freelec.jbpf.examples.lms.exception.DuplicateIDException(
						msg);
			} else {
				StringBuffer createRootQuery = new StringBuffer();
				createRootQuery.append("INSERT INTO haksadev.lms_user ");
				createRootQuery.append("VALUES (?,?,?,?)");
				result = preparedStatementQuery(conn,
						createRootQuery.toString(), null, root);
				StringBuffer createChildQuery = new StringBuffer();
				createChildQuery.append("INSERT INTO haksadev." + type + " ");
				createChildQuery.append("VALUES (?,?,?)");
				result = preparedStatementQuery(conn,
						createChildQuery.toString(), null, child);
				conn.commit();
				conn.setAutoCommit(true);
			}
		} catch (Exception e) {
			try {
				if (conn != null) {
					conn.rollback();
					System.out.println("rollback completely");
				}
			} catch (Exception e2) {
				System.out.println("Can not rollback completely.");
			} finally {
				close(conn, pstmt, null, result);
			}
			e.printStackTrace();
		}

		return msg;
	}

	public String dropUser(String criteria) {
		Connection conn = null;
		ResultSet result = null;
		String msg = UserConstants.DROP_USER_MESSAGE;
		try {
			System.out.println("\n1. Creating connection.");
			conn = EmbeddedJavaDB.createConnection();
			conn.setAutoCommit(false);
			StringBuffer findUserIDQuery = new StringBuffer();
			findUserIDQuery.append("SELECT ");
			findUserIDQuery.append("ssn ");
			findUserIDQuery.append("FROM haksadev.lms_user ");
			findUserIDQuery.append("WHERE ssn = ?");
			result = preparedStatementQuery(conn, findUserIDQuery.toString(),
					criteria, null);
			// System.out.print("5. Results:");
			// printResultSet(rset);
			boolean isExist = result.next();
			if (isExist) {
				StringBuffer deleteUserQuery = new StringBuffer();
				deleteUserQuery.append("DELETE FROM haksadev.lms_user ");
				deleteUserQuery.append("WHERE ssn = ? ");
				result = preparedStatementQuery(conn,
						deleteUserQuery.toString(), criteria, null);
				conn.commit();
				conn.setAutoCommit(true);
			} else {
				msg = UserConstants.DROP_USER_ERROR_MESSAGE;
				throw new com.abreqadhabra.freelec.jbpf.examples.lms.exception.RecordNotFoundException(
						msg);
			}
		} catch (Exception e) {
			try {
				if (conn != null) {
					conn.rollback();
					System.out.println("rollback completely");
				}
			} catch (Exception e2) {
				System.out.println("Can not rollback completely.");
			} finally {
				close(conn, pstmt, null, result);
			}
			e.printStackTrace();
		}

		return msg;
	}

	public ArrayList findUser(String parameter, String criteria) {
		Connection conn = null;
		ResultSet result = null;
		String msg = UserConstants.FIND_USER_MESSAGE;
		ArrayList users = null;
		try {
			System.out.println("\n1. Creating connection.");
			conn = EmbeddedJavaDB.createConnection();
			conn.setAutoCommit(false);
			StringBuffer findUserQuery = new StringBuffer();
			findUserQuery.append("SELECT name, age, ssn, adress, '', '', '', '', '', '' from lms_user ");

			/*findUserQuery.append("SELECT u.name, u.age, u.ssn, u.address,  ");
			findUserQuery
					.append("s.user_no, s.student_no,p.user_no, p.course, e.user_no, e.department ");
			findUserQuery
					.append("FROM haksadev.lms_user u, haksadev.lms_student s, haksadev.lms_professor p, haksadev.lms_employee e ");
			findUserQuery
					.append("WHERE u.ssn = s.ssn(+) AND u.ssn = p.ssn(+) AND u.ssn = e.ssn(+) ");*/
			if ("ssn".equals(parameter)) {
				//findUserQuery.append("AND u.ssn = ?");
				findUserQuery.append("where ssn = ?");
			} else if ("name".equals(parameter)) {
				//findUserQuery.append("AND u.name = ?");
				findUserQuery.append("where name = ?");
			}
		
			System.out.println("parameter/criteria--->" + parameter +"/" +criteria);

			result = preparedStatementQuery(conn, findUserQuery.toString(),
					criteria, null);
			// System.out.print("5. Results:");
			// printResultSet(rset);
	//		conn.commit();
//			conn.setAutoCommit(true);
			// boolean isExist = result.next();
			users = new ArrayList();
			users.add(0, msg);
			while (result.next()) {
				String[] temp = new String[10];
				String[] data = new String[7];
				String type = null;
				String uno = null;
				String etc = null;
				for (int i = 0; i < temp.length; i++) {
					temp[i] = result.getString(i + 1);
				}
				if (temp[5] != null) {
					type = "lms_student";
					uno = temp[4];
					etc = temp[5];
				} else if (temp[7] != null) {
					type = "lms_professor";
					uno = temp[6];
					etc = temp[7];
				} else if (temp[9] != null) {
					type = "lms_employee";
					uno = temp[8];
					etc = temp[9];
				}
				data[0] = type;
				data[1] = temp[0];
				data[2] = temp[1];
				data[3] = temp[2];
				data[4] = temp[3];
				data[5] = uno;
				data[6] = etc;
				users.add(data);
			}
			if (users.size() == 1) {
				msg = UserConstants.FIND_USER_ERROR_MESSAGE;
				users.clear();
				users.add(0, msg);
				throw new com.abreqadhabra.freelec.jbpf.examples.lms.exception.RecordNotFoundException(
						msg);
			}
		} catch (Exception e) {
			try {
				if (conn != null) {
					conn.rollback();
					System.out.println("rollback completely");
				}
			} catch (Exception e2) {
				System.out.println("Can not rollback completely.");
			} finally {
				close(conn, pstmt, null, result);
			}
			e.printStackTrace();
		}
		return users;
	}

	public String modifyUser(String category, String sequenceCategory,
			String[] root, String[] child, String userNo) {
		Connection conn = null;
		ResultSet result = null;
		String msg = UserConstants.MODIFY_USER_MESSAGE;
		try {
			System.out.println("\n1. Creating connection.");
			conn = EmbeddedJavaDB.createConnection();
			conn.setAutoCommit(false);
			String field = null;
			if ("lms_student".equals(sequenceCategory)) {
				field = "student_no";
			} else if ("lms_professor".equals(sequenceCategory)) {
				field = "course";
			} else if ("lms_employee".equals(sequenceCategory)) {
				field = "department";
			}
			StringBuffer modifyRootQuery = new StringBuffer();
			modifyRootQuery.append("UPDATE haksadev.lms_user SET ");
			modifyRootQuery.append("name = ?, age = ?, address = ? ");
			modifyRootQuery.append("WHERE ssn = ? ");
			result = preparedStatementQuery(conn, modifyRootQuery.toString(),
					null, root);
			if (userNo == null) {
				StringBuffer modifyChildQuery = new StringBuffer();
				modifyChildQuery
						.append("UPDATE haksadev." + category + " SET ");
				modifyChildQuery.append(field + " = ? ");
				modifyChildQuery.append("WHERE ssn = ? ");
				result = preparedStatementQuery(conn,
						modifyChildQuery.toString(), null, child);
			} else {
				String[] newChild = new String[3];
				newChild[0] = userNo;
				newChild[1] = child[1];
				newChild[2] = child[0];
				StringBuffer deleteChildQuery = new StringBuffer();
				deleteChildQuery.append("DELETE FROM haksadev." + category
						+ " ");
				deleteChildQuery.append("WHERE ssn = ? ");
				result = preparedStatementQuery(conn,
						deleteChildQuery.toString(), newChild[1], null);
				StringBuffer createChildQuery = new StringBuffer();
				createChildQuery.append("INSERT INTO haksadev."
						+ sequenceCategory + " ");
				createChildQuery.append("VALUES (?,?,?)");
				result = preparedStatementQuery(conn,
						createChildQuery.toString(), null, newChild);
			}
			conn.commit();
			conn.setAutoCommit(true);
		} catch (Exception e) {
			msg = UserConstants.MODIFY_USER_ERROR_MESSAGE;
			try {
				if (conn != null) {
					conn.rollback();
					System.out.println("rollback completely");
				}
			} catch (Exception e2) {
				System.out.println("Can not rollback completely.");
			} finally {
				close(conn, pstmt, null, result);
			}
			e.printStackTrace();
		}

		return msg;
	}

	public ArrayList listUser() {
		Connection conn = null;
		ResultSet result = null;
		String msg = UserConstants.LIST_USER_MESSAGE;
		ArrayList users = null;
		try {
			System.out.println("\n1. Creating connection.");
			conn = EmbeddedJavaDB.createConnection();
			conn.setAutoCommit(false);
			StringBuffer listUserQuery = new StringBuffer();
			listUserQuery.append("SELECT name, age, ssn, adress, '', '', '', '', '', '' from lms_user ");
//			listUserQuery.append("SELECT u.name, u.age, u.ssn, u.address,  ");
//			listUserQuery
//					.append("s.user_no, s.studentid,p.userid, p.course, e.userid, e.department ");
//			listUserQuery
//					.append("FROM lms_user u, lms_student s, lms_professor p, lms_employee e ");
//			listUserQuery					.append("WHERE u.ssn = s.ssn(+) AND u.ssn = p.ssn(+) AND u.ssn = e.ssn(+) ");
			result = preparedStatementQuery(conn, listUserQuery.toString(),
					null, null);
			// System.out.print("5. Results:");
			// printResultSet(rset);
			//conn.commit();
			//conn.setAutoCommit(true);
			// boolean isExist = result.next();
			users = new ArrayList();
			users.add(0, msg);
			while (result.next()) {
				String[] temp = new String[10];
				String[] data = new String[7];
				String type = null;
				String uno = null;
				String etc = null;
				for (int i = 0; i < temp.length; i++) {
					temp[i] = result.getString(i + 1);
				}
				if (temp[5] != null) {
					type = "lms_student";
					uno = temp[4];
					etc = temp[5];
				} else if (temp[7] != null) {
					type = "lms_professor";
					uno = temp[6];
					etc = temp[7];
				} else if (temp[9] != null) {
					type = "lms_employee";
					uno = temp[8];
					etc = temp[9];
				}
				data[0] = type;
				data[1] = temp[0];
				data[2] = temp[1];
				data[3] = temp[2];
				data[4] = temp[3];
				data[5] = uno;
				data[6] = etc;
				users.add(data);
			}
			if (users.size() == 1) {
				msg = UserConstants.LIST_USER_ERROR_MESSAGE;
				users.clear();
				users.add(0, msg);
				throw new com.abreqadhabra.freelec.jbpf.examples.lms.exception.RecordNotFoundException(
						msg);
			}
		} catch (Exception e) {
			try {
				if (conn != null) {
					conn.rollback();
					System.out.println("rollback completely");
				}
			} catch (Exception e2) {
				System.out.println("Can not rollback completely.");
			} finally {
				close(conn, pstmt, null, result);
			}
			e.printStackTrace();
		}
		return users;
	}

} // end of class