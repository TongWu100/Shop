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
			request.setAttribute("registinfo", "��֤�����");
			request.getRequestDispatcher("/register.jsp").forward(request, response);
			return;
		}
		//��ȡ������
		Map<String, String[]> properties = request.getParameterMap();
		User user = new User();
		try {
			//�Զ���һ������ת����
			ConvertUtils.register(new Converter() {
				Date parse = null;
				@Override
				public Object convert(Class type, Object value) {
				 //��Stringת��Date
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
		
		//��user���󴫵ݸ�UserService
		UserService service = new UserService();
		boolean isRegisterSuccess=service.regist(user);
		
		//�Ƿ�ע��ɹ�
		if(isRegisterSuccess) {
		   String  emailMsg ="��ϲ��ע��ɹ���������������ӽ��м����˻�"
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