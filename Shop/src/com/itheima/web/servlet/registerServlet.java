package com.itheima.web.servlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;

import com.itheima.domain.User;
import com.itheima.utils.CommonUtils;
import com.itheima.utils.MailUtils;
import com.itheima.service.UserService;

public class registerServlet extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String inputCode = request.getParameter("checkCode");
		String checkCode = (String)request.getSession().getAttribute("checkcode_session");
		if(!inputCode.equals(checkCode)) {
			request.setAttribute("registinfo", "验证码错误");
			request.getRequestDispatcher("/register.jsp").forward(request, response);
			return;
		}
		//获取表单数据
		Map<String, String[]> properties = request.getParameterMap();
		User user = new User();
		try {
			//自定义一个类型转换器
			ConvertUtils.register(new Converter() {
				Date parse = null;
				@Override
				public Object convert(Class type, Object value) {
				 //将String转成Date
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
					try {
						parse = format.parse(value.toString());
					} catch (ParseException e) {
						e.printStackTrace();
					}
					return parse;
				}
			}, Date.class);
			BeanUtils.populate(user, properties);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		//private String uid;
		user.setUid(CommonUtils.getUUID());
		//private String telephone;
		user.setTelephone(null);
		//private int state;
		user.setState(0);
	    //private String code;
		String activeCode = CommonUtils.getUUID();
		user.setCode(activeCode);
		
		//将user对象传递给UserService
		UserService service = new UserService();
		boolean isRegisterSuccess=service.regist(user);
		
		//是否注册成功
		if(isRegisterSuccess) {
		   String  emailMsg ="恭喜您注册成功，请点接下面的链接进行激活账户"
		   		+ "<a href='http://localhost:8080/Shop/active?activeCode="+activeCode+"'>"
		   				+ "http://localhost:8080/Shop/active?activeCode="+activeCode+"</a>";
		   try {
			MailUtils.sendMail(user.getEmail(), emailMsg);
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		   response.sendRedirect(request.getContextPath()+"/registerSuccess.jsp");
		}else {
		   response.sendRedirect(request.getContextPath()+"/registerFail.jsp");
		}
			
	
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}