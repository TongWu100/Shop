package com.itheima.web.servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.itheima.domain.Category;
import com.itheima.domain.Order;
import com.itheima.domain.PageBean;
import com.itheima.domain.Product;
import com.itheima.service.AdminService;
import com.itheima.service.impl.AdminServiceImpl;
import com.itheima.utils.BeanFactory;
@SuppressWarnings("all")
public class AdminServlet extends baseServlet {
	//����oid��ѯ������Ʒ��Ϣ
	public void findOrderInfoByOid(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String oid = request.getParameter("oid");
		//�ý���ϵķ�ʽ���б���----��web����service������
		//ʹ�ù���+����+�����ļ�
		AdminService service = (AdminService) BeanFactory.getBean("adminService");
		List<Map<String,Object>> mapList = service.findOrderInfoByOid(oid);
		Gson gson = new Gson();
		String json = gson.toJson(mapList);
		response.setContentType("text/html;charset=UTF-8");
		response.getWriter().write(json);
	}
	//������Ʒ
	public void updateProductUI(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pid = request.getParameter("pid");
		AdminService service = (AdminService) BeanFactory.getBean("adminService");
		Product product = service.findProductByPid(pid);
		List<Category> categoryList = service.findAllCategory();
		request.setAttribute("product", product);
		request.setAttribute("categoryList", categoryList);
		request.getRequestDispatcher("/admin/product/edit.jsp").forward(request, response);
	}
	//ɾ��ĳ����Ʒ
	public void delProduct(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pid = request.getParameter("pid");
		AdminService service = (AdminService) BeanFactory.getBean("adminService");
		service.delProduct(pid);
		response.sendRedirect(request.getContextPath()+"/admin?method=findAllProducts");
	}
	//��ѯ������Ʒ
	public void findAllProducts(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String currentPageStr = request.getParameter("currentPage");
		if(currentPageStr==null)currentPageStr="1";
		int currentPage=Integer.parseInt(currentPageStr);
		int currentCount=12;
		AdminService service = (AdminService) BeanFactory.getBean("adminService");
		PageBean adminPageBean = service.findAllProducts(currentPage,currentCount);
		//List<Product> productsList = service.findAllProducts();
		request.setAttribute("adminPageBean", adminPageBean);
		request.getRequestDispatcher("/admin/product/list.jsp").forward(request, response);
	}
    //��ѯ���ж�����Ϣ
	public void findAllOrders(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		AdminService service = (AdminService) BeanFactory.getBean("adminService");
		List<Order> ordersList = service.findAllOrders();
		request.setAttribute("ordersList", ordersList);
		request.getRequestDispatcher("/admin/order/list.jsp").forward(request, response);
	}
	//��÷����б���
	public void findAllCategory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//�ṩһ��List<category>���� ת��json��
		AdminService service = (AdminService) BeanFactory.getBean("adminService");
		List<Category> categoryList = service.findAllCategory();
		Gson gson = new Gson();
		String json = gson.toJson(categoryList);
		response.setContentType("text/html;charset=UTF-8");
		response.getWriter().write(json);
	}

}
