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
    //用户注销
	public void logout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//获得session
		HttpSession session = request.getSession();
		//将user从session中删除
		session.removeAttribute("user");
		//将存储在客户端的cookie删除掉
		Cookie cookie_username = new Cookie("cookie_username","");
		Cookie cookie_password = new Cookie("cookie_password","");
		cookie_username.setMaxAge(0);
		cookie_password.setMaxAge(0);
		response.addCookie(cookie_username);
		response.addCookie(cookie_password);
		//跳转到登录页面
		response.sendRedirect(request.getContextPath()+"/login.jsp");
	}
	//用户登录
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
			//对中文张三进行编码
			String username_code = URLEncoder.encode(username, "UTF-8");
			if(autologin!=null) {
				Cookie cookie_username = new Cookie("cookie_username",username_code);
				Cookie cookie_password = new Cookie("cookie_password",user.getPassword());
				//设置持久化时间
				cookie_username.setMaxAge(60*60);
				cookie_password.setMaxAge(60*60);
				//设置cookie携带路径
				cookie_username.setPath(request.getContextPath());
				cookie_password.setPath(request.getContextPath());
				//发送cookie
				response.addCookie(cookie_username);
				response.addCookie(cookie_password);
			}
			if(!inputCode.equals(checkCode)) {
				request.setAttribute("loginfo", "验证码错误");
				request.getRequestDispatcher("/login.jsp").forward(request, response);
			}else {
				session.setAttribute("user", user);
				response.sendRedirect(request.getContextPath());				
			}
		}else {
			request.setAttribute("loginfo", "您的用户名或密码错误");
			request.getRequestDispatcher("/login.jsp").forward(request, response);
			//response.sendRedirect(request.getContextPath()+"/login.jsp");
		}
	}
}