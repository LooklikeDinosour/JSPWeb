package com.coding404.user.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class UserDAO { 

	//싱글톤 형태의 클래스로 생성하는 편이 좋습니다.
	//1. 나 자신의 객체를 스테틱으로 선언
	private static UserDAO instance = new UserDAO();

	//2. 직접 생성하지 못하도록 생성자 제한
	private UserDAO() {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	//3. getter를 통해서 객체를 반환
	public static UserDAO getInstance() {
		return instance;
	}

	//데이터베이스 연결 주소
	//오라클 커넥터 : 
	//WEB-INF lib에 jstl.jar,standard.jar, ojdbc11.jar 에 넣기
	private String url = "jdbc:oracle:thin:@localhost:1521:xe";
	private String uid = "JSP";
	private String upw = "JSP";

	/*
	 * @author 20230608 김정수
	 */

	//아이디중복조회
	public int idCheck(String id) {
		int result = 1;

		String sql = "select * from users where id =?";

		//싱글톤 객체 사용시 문제발생우려가 있어서 지역변수가 설정해줘야한다.	
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			//1.Connector
			conn = DriverManager.getConnection(url, uid, upw);

			//2. Pstmt - sql을 실행하기 위한 클래스
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);

			//3. ResultSet
			rs = pstmt.executeQuery();

			if(rs.next()) { //중복 O
				result = 1;

			} else { //중복 X
				result = 0;

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
				pstmt.close();
				rs.close();
			} catch (Exception e2) {
			}		
		}
		return result;
	}


	//회원가입
	public int join(UserVO vo) {

		String sql = "INSERT INTO USERS(ID, PW, NAME, EMAIL, GENDER) VALUES (?, ?, ?, ?, ?)";

		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			conn = DriverManager.getConnection(url, uid, upw);

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, vo.getId());
			pstmt.setString(2, vo.getPw());
			pstmt.setString(3, vo.getName());
			pstmt.setString(4, vo.getEmail());
			pstmt.setString(5, vo.getGender());

			pstmt.executeUpdate(); //성공시 1, 실패시 0;


		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
				pstmt.close();

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}


	//로그인
	public UserVO login(String id, String pw) {

		UserVO vo = null;

		String sql =" SELECT * FROM USERS WHERE ID =? AND PW =?";

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			conn = DriverManager.getConnection(url, uid, upw);

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			pstmt.setString(2, pw);

			rs = pstmt.executeQuery();

			if(rs.next()) {

				String id2 = rs.getString("id");
				String name = rs.getString("name");
				String email = rs.getString("email");
				String gender = rs.getString("gender");
				Timestamp regdate = rs.getTimestamp("regdate");

				vo = new UserVO(id2, null, name, email, gender, regdate);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
				pstmt.close();
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
		return vo;
	}


	//회원정보 조회
	public UserVO getInfo(String id) {

		UserVO vo = null;

		String sql = " SELECT * FROM USERS WHERE ID = ?";

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null; 


		try {

			conn = DriverManager.getConnection(url, uid, upw);
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id); //id

			rs = pstmt.executeQuery(); 

			if(rs.next()) { //id는 pk라서 1행

				String id2 = rs.getString("id");
				String name = rs.getString("name");
				String email = rs.getString("email");
				String gender = rs.getString("gender");

				vo = new UserVO(id2, null, name, email, gender, null);
			}

		} catch (Exception e) {
			e.printStackTrace();

		} finally {

			try {
				conn.close();
				pstmt.close();
				rs.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

		return vo;
	}


	//회원정보업데이트

	public int updateInfo(UserVO vo) {

		int result = 0;
		
		String sql = "update users set pw = ?, name = ?, email = ?, gender = ? where id = ?";

		Connection conn = null;
		PreparedStatement pstmt = null;
	
		try {

			conn = DriverManager.getConnection(url, uid, upw);
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, vo.getPw());
			pstmt.setString(2, vo.getName());
			pstmt.setString(3, vo.getEmail());
			pstmt.setString(4, vo.getGender());
			pstmt.setString(5, vo.getId());

			result = pstmt.executeUpdate(); //성공 1, 실패 0
			
			
			
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			
			try {
				conn.close();
				pstmt.close();
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
		}

		return result;
	}

}