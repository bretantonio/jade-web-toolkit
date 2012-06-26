package com.abreqadhabra.freelec.jbpf.examples.lms.dao;

final public class UserConstants{
	static public final String DUPLICATE_ERROR_MESSAGE = "입력 에러: 이미 등록된 주민등록번호와 중복됩니다.";
	static public final String CREATE_USER_MESSAGE = "입력 성공: 데이터베이스에 입력되었습니다.";
	static public final String DROP_USER_MESSAGE = "삭제 성공: 데이터베이스에서 삭제되었습니다.";
	static public final String DROP_USER_ERROR_MESSAGE = "삭제 에러: 삭제할 회원번호에 대한 레코드를 찾을 수 없습니다.";
	static public final String MODIFY_USER_MESSAGE = "수정 성공: 데이터베이스의 레코드가 수정되었습니다.";
	static public final String MODIFY_USER_ERROR_MESSAGE = "수정 에러: 데이터베이스 에러가 발생하였습니다.";
	static public final String FIND_USER_MESSAGE = "검색 성공: 데이터베이스에서 검색되었습니다.";
	static public final String FIND_USER_ERROR_MESSAGE = "검색 에러: 해당 회원에 대한 레코드를 찾을 수 없습니다.";
	static public final String LIST_USER_MESSAGE = "출력 성공: 데이터베이스에서 검색되었습니다.";
	static public final String LIST_USER_ERROR_MESSAGE = "출력 에러: 등록된 레코드가 없습니다.";
	private UserConstants()	{
		// this constructor is intentionally private 
	}//UserConstants()

}//class UserConstants
