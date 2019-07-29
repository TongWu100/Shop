package com.itheima.web.servlet;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.itheima.domain.User;
import com.itheima.service.UserService;

public class userServlet extends baseServlet {
    //�û�ע��
	public void logout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//���session
		HttpSession session = request.getSession();
		//��user��session��ɾ��
		session.removeAttribute("user");
		//���洢�ڿͻ��˵�cookieɾ����
		Cookie cookie_username = new Cookie("cookie_username","");
		Cookie cookie_password = new Cookie("cookie_password","");
		cookie_username.setMaxAge(0);
		cookie_password.setMaxAge(0);
		response.addCookie(cookie_username);
		response.addCookie(cookie_password);
		//��ת����¼ҳ��
		response.sendRedirect(request.getContextPath()+"/login.jsp");
	}
	//�û���¼
	public void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		HttpSession session = request.getSession();
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String inputCode = request.getParameter("checkCode");
		String checkCode = (String)request.getSession().getAttribute("checkcode_session");
		User user = null;
		UserService service = new UserService();
		user = service.login(username,password);
		if(user!=null) {
			String autologin = request.getParameter("autologin");
			//�������������б���
			String username_code = URLEncoder.encode(username, "UTF-8");
			if(autologin!=null) {
				Cookie cookie_username = new Cookie("cookie_username",username_code);
				Cookie cookie_password = new Cookie("cookie_password",user.getPassword());
				//���ó־û�ʱ��
				cookie_username.setMaxAge(60*60);
				cookie_password.setMaxAge(60*60);
				//����cookieЯ��·��
				cookie_username.setPath(request.getContextPath());
				cookie_password.setPath(request.getContextPath());
				//����cookie
				response.addCookie(cookie_username);
				response.addCookie(cookie_password);
			}
			if(!inputCode.equals(checkCode)) {
				request.setAttribute("loginfo", "��֤�����");
				request.getRequestDispatcher("/login.jsp").forward(request, response);
			}else {
				session.setAttribute("user", user);
				response.sendRedirect(request.getContextPath());				
			}
		}else {
			request.setAttribute("loginfo", "�����û������������");
			request.getRequestDispatcher("/login.jsp").forward(request, response);
			//response.sendRedirect(request.getContextPath()+"/login.jsp");
		}
	}
}