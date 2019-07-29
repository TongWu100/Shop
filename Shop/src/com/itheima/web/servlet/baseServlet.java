package com.itheima.web.servlet;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@SuppressWarnings("all")
public class baseServlet extends HttpServlet {

	@Override
	protected void service(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
		arg0.setCharacterEncoding("UTF-8");
		String methodName = arg0.getParameter("method");
		//获得当前对象的字节码对象 当前对象是productServlet
		Class clazz = this.getClass();//productServlet.class
		try {
			//获得当前字节码对象的方法
			Method method = clazz.getMethod(methodName, HttpServletRequest.class,HttpServletResponse.class);
			//执行相应的功能方法
			method.invoke(this,arg0,arg1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
