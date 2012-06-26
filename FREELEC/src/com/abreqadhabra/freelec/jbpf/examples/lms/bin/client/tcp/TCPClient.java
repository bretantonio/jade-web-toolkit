package com.abreqadhabra.freelec.jbpf.examples.lms.bin.client.tcp;

import java.io.*;
import java.net.*;
import java.util.*;

import com.abreqadhabra.freelec.jbpf.examples.lms.user.*;

public class TCPClient {
	private Socket clientSocket = null;
	private DataOutputStream dos = null;
	private DataInputStream dis = null;
	private ObjectOutputStream oos = null;
	private ObjectInputStream ois = null;
	Thread clientThread;
	private TCPClientGUI gui;

	public TCPClient() throws Exception {
		this("127.0.0.1");
	}

	public TCPClient(String url) throws Exception {
		this(url, 6666);
	}

	public TCPClient(String url, int port) throws Exception {
		this.connect(url, port);
		gui = new TCPClientGUI(this);

		this.socketListening();
	}

	public synchronized void connect(String url, int port) {
		if (url == null | port == 0) {
			url = "127.0.0.1";
			port = 6666;
		}
		if (clientSocket == null) {
			try {
				clientSocket = new Socket(url, port);
				dos = new DataOutputStream(clientSocket.getOutputStream());
				dis = new DataInputStream(clientSocket.getInputStream());
				oos = new ObjectOutputStream(clientSocket.getOutputStream());
				ois = new ObjectInputStream(clientSocket.getInputStream());
			} catch (IOException e) {
				System.out.println("학사 관리 시스템 서버(" + url + ":" + port
						+ ") 접속이 실패하였습니다.!!");
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void socketListening() {
		clientThread = new Thread() {
			public void run() {
				try {
					while (clientSocket != null) {
						String cmd = (String) ois.readObject();
						Object obj = ois.readObject();
						Object resultObject = createResultObject(cmd, obj);
						gui.setResultObject(cmd, resultObject);
						viewRecieveResult(cmd, obj);
					}
				} catch (UnknownHostException ukhe) {
					System.out.println("접속하려는 서버 ip를 정확히 입력해주세요");
				} catch (ConnectException ce) {
					System.out.println("서버와의 접속 에러");
				} catch (SocketException sockee) {
					System.out.println("서버와의 접속 종료 상태");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		clientThread.start();
	}

	public synchronized void close() {
		try {
			if (clientSocket != null) {
				if (dos != null) {
					dos.close();
				}
				if (dis != null) {
					dis.close();
				}
				if (oos != null) {
					oos.close();
				}
				if (ois != null) {
					ois.close();
				}
				clientSocket.close();
			}
		} catch (IOException e) {
			System.out.println("모든 스트림을 닫았습니다.");
		}
		clientSocket = null;
	}

	public static void main(String args[]) {
		TCPClient tcpClient = null;
		try {
			switch (args.length) {
			case 1:
				tcpClient = new TCPClient(args[0]);
				break;
			case 2:
				tcpClient = new TCPClient(args[0], Integer.parseInt(args[1]));
				break;
			default:
				tcpClient = new TCPClient();
				break;
			}

		} catch (UnknownHostException ukhe) {
			System.out.println("접속하려는 서버IP를 정확히 입력하세요.");
		} catch (ConnectException ce) {
			System.out.println("서버와의 접속이 이루어지지 않았습니다.");
		} catch (SocketException se) {
			System.out.println("서버와의 연결이 끊겼습니다!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void viewRecieveResult(String cmd, Object obj) {
		String value = null;
		gui.logTextArea
				.append("------------------------------------------------------------------------------------\n");
		gui.logTextArea.append("명령어:" + cmd + "에 대하여 서버에서 받은 결과:\n");
		if (obj instanceof String) {
			value = (String) obj;
			gui.logTextArea
					.append("------------------------------------------------------------------------------------\n");
			gui.logTextArea.append("메시지: " + value + "\n");
		} else if (obj instanceof ArrayList) {
			ArrayList valueArrayList = (ArrayList) obj;
			for (int i = 0; i < valueArrayList.size(); i++) {
				if (i == 0) {
					String msg = (String) valueArrayList.get(i);
					gui.logTextArea
							.append("------------------------------------------------------------------------------------\n");
					gui.logTextArea.append("메시지:" + msg + "\n");
					gui.logTextArea
							.append("------------------------------------------------------------------------------------\n");
				} else {
					String[] temp = (String[]) valueArrayList.get(i);
					gui.logTextArea.append("배열[" + i + "]      ");
					for (int j = 0; j < temp.length; j++) {
						gui.logTextArea.append(temp[j] + "      ");
					}
					gui.logTextArea
							.append("\n------------------------------------------------------------------------------------\n");
				}
			}
		}

	}

	public Object createResultObject(String cmd, Object obj) {
		if ("create".equals(cmd)) {
			System.out.println("서버로부터 입력 정보에 대한 입력 결과를 전송 받았습니다.");
			String msg = (String) obj;
			return msg;
		} else if ("update".equals(cmd)) {
			System.out.println("서버로부터 입력 정보에 대한 수정 결과를 전송 받았습니다.");
			String msg = (String) obj;
			return msg;
		} else if ("delete".equals(cmd)) {
			System.out.println("서버로부터 삭제 정보에 대한 삭제 결과를 전송 받았습니다.");
			gui.resetOutPutData(true, true);
			listUser("list");
		} else if ("name".equals(cmd)) {
			if (((ArrayList) obj).size() == 1) {
				System.out.println("검색어에 대한 검색결과가 없습니다.");
				return gui.findUserTextField.getText();
			} else {
				System.out.println("서버로부터 검색어에 대한 검색결과를 전송 받았습니다.");
				ArrayList data = extractUserObject((ArrayList) obj);
				gui.setResultTable(data);
				return data.get(0);
			}
		} else if ("ssn".equals(cmd)) {
			if (((ArrayList) obj).size() == 1) {
				System.out.println("검색어에 대한 검색결과가 없습니다.");
				return obj;
			} else {
				System.out.println("서버로부터 검색 결과에 대한 결과를 전송 받았습니다.");
				ArrayList data = extractUserObject((ArrayList) obj);
				gui.setResultTable(data);
				return data.get(0);
			}
		} else if ("list".equals(cmd)) {
			gui.logTextArea.append("서버로부터 회원 데이터의 전체 목록을 전송 받았습니다.\n");
			gui.setUsersTable(extractUserObject((ArrayList) obj));
			return new Boolean(true);
		} else if ("seq".equals(cmd)) {
			gui.logTextArea.append("서버로부터 새로운 시퀀스를 전송 받았습니다.\n");
			String seq = (String) obj;
			gui.setSequence(seq);
			return new Boolean(true);
		}
		gui.logTextArea
				.append("\n------------------------------------------------------------------------------------\n");

		return obj;
	}

	public ArrayList extractUserObject(ArrayList users) {
		ArrayList listData = new ArrayList();
		if (users.size() != 0) {
			String msg = null;
			String[] user = new String[6];
			for (int i = 0; i < users.size(); i++) {
				if (i == 0) {
					msg = (String) users.get(i);
				} else {
					user = (String[]) users.get(i);
				}
				if ("lms_student".equals(user[0])) {
					Student student = new Student();
					student.setName(user[1]);
					student.setAge(user[2]);
					student.setSsn(user[3]);
					student.setAddress(user[4]);
					student.setUserNo(user[5]);
					student.setStudentNo(user[6]);
					student.setType(user[0]);
					listData.add(student);
				} else if ("lms_professor".equals(user[0])) {
					Professor professor = new Professor();
					professor.setName(user[1]);
					professor.setAge(user[2]);
					professor.setSsn(user[3]);
					professor.setAddress(user[4]);
					professor.setUserNo(user[5]);
					professor.setCourse(user[6]);
					professor.setType(user[0]);
					listData.add(professor);
				} else if ("lms_employee".equals(user[0])) {
					Employee employee = new Employee();
					employee.setName(user[1]);
					employee.setAge(user[2]);
					employee.setSsn(user[3]);
					employee.setAddress(user[4]);
					employee.setUserNo(user[5]);
					employee.setDepartment(user[6]);
					employee.setType(user[0]);
					listData.add(employee);
				}
			}
			System.out.println(msg);
			if (users.size() < 2) {
				javax.swing.JOptionPane.showMessageDialog(gui, msg, "전체목록 보기 ",
						javax.swing.JOptionPane.WARNING_MESSAGE);
			}
		}

		return listData;
	}

	public void sendUserObject(String cmd, String value, Object object) {
		try {
			if (cmd != null) {
				oos.writeObject(cmd);
				oos.writeObject(value);
				dos.flush();
				oos.writeObject(object);
				oos.flush();
			} else {
				System.out.println("전송 실패: 요청 명령어가 없습니다.\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createUser(String cmd, String type, String[] insertData) {
		if (cmd.equals("create")) {
			sendUserObject(cmd, null, insertData);
			gui.logTextArea.append("입력요청: 원격서버에 전송되었습니다.\n");
		} else if (cmd.equals("update")) {
			sendUserObject(cmd, type, insertData);
			gui.logTextArea.append("수정 요청: 원격서버에 전송되었습니다.\n");
		}
	}

	public void deleteUser(String cmd, String value) {
		sendUserObject(cmd, value, null);
		gui.logTextArea.append("삭제 요청: 원격서버에 전송되었습니다.");
	}

	public void listUser(String cmd) {
		sendUserObject(cmd, null, null);
		gui.logTextArea.append("전체출력요청: 원격서버에 전송되었습니다.\n");
	}

	public void readUser(String cmd, String value) {
		sendUserObject(cmd, value, null);
		gui.logTextArea.append("검색요청 [" + cmd + " / " + value
				+ "] 원격서버에 전송되었습니다.\n");
	}

	public void createSequence(String cmd, String value) {
		sendUserObject(cmd, value, null);
		gui.logTextArea.append("검색요청 [" + cmd + " / " + value
				+ "] 원격서버에 전송되었습니다.\n");
	}
} // class TCPClient