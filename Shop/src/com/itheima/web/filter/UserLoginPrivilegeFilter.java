package com.itheima.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.itheima.domain.User;

public class UserLoginPrivilegeFilter implements Filter{

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse rep = (HttpServletResponse) response;
		HttpSession session = req.getSession();
		//校验用户是否登录-----校验session中是否存在user对象
		User user = (User) session.getAttribute("user");
		if (user == null) {
			// 没有登录
			rep.sendRedirect(req.getContextPath() + "/login.jsp");
			return;
		}
		chain.doFilter(req, rep);
		
	}
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

}
