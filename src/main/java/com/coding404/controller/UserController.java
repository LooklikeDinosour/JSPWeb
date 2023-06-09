package com.coding404.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.coding404.user.model.UserVO;
import com.coding404.user.service.UserService;
import com.coding404.user.service.UserServiceImpl;

//1. 확장자패턴으로 변경
@WebServlet("*.user")
public class UserController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public UserController() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doAction(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doAction(request, response);
	}

	//2. get/post 한개로 모으기
	protected void doAction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		//3.요청 분기

		//한글처리
		request.setCharacterEncoding("utf-8");

		String uri =  request.getRequestURI();
		String conPath = request.getContextPath();
		String command = uri.substring(conPath.length());

		System.out.println(command);
		
		//** MVC2에서는 화면을 띄우는 요청도 컨트롤러를 거쳐 나가도록 처리
		//** 기본 이동 전부 FORWARD형식으로 처리
		//** 리다이렉트는 다시 컨트롤러를 태워서 나가는 용도로 사용
		
		//필요한 객체를 if문 바깥에 선언
		UserService service = new UserServiceImpl();
		HttpSession session = request.getSession();
		
		
		if(command.equals("/user/user_join.user")) {
			
			request.getRequestDispatcher("user_join.jsp").forward(request, response);
		
		} else if(command.equals("/user/user_login.user")) {
			
			request.getRequestDispatcher("user_login.jsp").forward(request, response);
		
			//회원가입 요청
		
		} else if(command.equals("/user/joinForm.user")) {
			
			//가입 객체로 만들어서 메서드 호출
			//부모로 형변환하여 받기
			int result = service.join(request, response);
			
			if(result == 1) { // 중복
				//message 1회성으로 실어서 보낸다.
				
				request.setAttribute("msg", "중복된 아이디 입니다.");
				request.getRequestDispatcher("user_join.jsp").forward(request, response);
			} else { //가입성공
				
				response.sendRedirect("user_login.user"); // else if login으로 갔다가 아랫 코드로 타고 나간다.
			}
			
			
		// 로그인	
		} else if(command.equals("/user/loginForm.user")) {
			
			UserVO vo = service.login(request, response);
			
			if (vo == null) {//로그인 실패
				
				request.setAttribute("msg", "아이디 비밀번호를 확인하세요");
				request.getRequestDispatcher("user_login.jsp").forward(request, response);
				
			} else { //로그인 성공
				//세션에 회원정보 저장(자바에서 세션얻는 방법 암기 암기)
				session.setAttribute("user_id", vo.getId());
				session.setAttribute("user_name", vo.getName());
				
				response.sendRedirect("user_mypage.user");
			}
			
		} else if (command.equals("/user/user_mypage.user")) {
			
			request.getRequestDispatcher("user_mypage.jsp").forward(request, response);
		
		//로그아웃	- 인증정보 삭제
		} else if(command.equals("/user/user_logout.user")) {
			
			session.invalidate();
			response.sendRedirect("user_login.user");
		
		//정보수정페이지 
		} else if(command.equals("/user/user_modify.user")) {
			
			//회원정보를 들고와야됌.
			UserVO vo= service.getInfo(request, response);
			request.setAttribute("vo", vo);
			
			request.getRequestDispatcher("user_modify.jsp").forward(request, response);
		
		} else if(command.equals("/user/user_update.user")) {
			
			int result = service.updateInfo(request, response);
			
			if(result == 1) { //성공(유저닉네임이 변경)
				
				String name = request.getParameter("name");
				session.setAttribute("user_name", name);
				
				//out객체를 이용한 메시지 전달
				response.setContentType("text/html; charset=UTF-8;");
				PrintWriter out = response.getWriter();
				out.println("<script>");
				out.println("alert('안녕하세요')");
				out.println("location.href='user_mypage.user';");
				out.println("</script>");
			
			} else { //실패
				response.sendRedirect("user_modify.user");
			}
		}
		 

	}
}
