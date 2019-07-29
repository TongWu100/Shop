package com.itheima.service.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.itheima.dao.AdminDao;
import com.itheima.domain.Category;
import com.itheima.domain.Order;
import com.itheima.domain.PageBean;
import com.itheima.domain.Product;
import com.itheima.service.AdminService;

public class AdminServiceImpl implements AdminService {
	AdminDao dao = new AdminDao();
	public List<Category> findAllCategory() {
		List<Category> categoryList = null;
		try {
			categoryList = dao.findAllCategory();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return categoryList;
	}
	public void saveProduct(Product product) {
		try {
			dao.saveProduct(product);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	public List<Order> findAllOrders() {
		List<Order> ordersList= null;
		try {
			ordersList = dao.findAllOrders();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ordersList;
	}
	public List<Product> findAllProducts() {
		List<Product> productsList = null;
		try {
		productsList = dao.findAllProducts();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return productsList;
	}
	public PageBean findAllProducts(int currentPage, int currentCount) {
		// 封装pageBean
		PageBean adminPageBean = new PageBean();
		int totalCount=0;
		int totalPage=0;
		List<Product> productLsist=null;
//		 //当前页
//		private int currentPage;
		adminPageBean.setCurrentPage(currentPage);
//		//当前页显示的条数
//		private int currentCount;
		adminPageBean.setCurrentCount(currentCount);
//		//总条数
//		private int totalCount;
		try {
			totalCount = dao.getTotalCount();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		adminPageBean.setTotalCount(totalCount);
//		//总页数
//		private int totalPage;
		totalPage=(int) Math.ceil(1.0*totalCount/currentCount);
		adminPageBean.setTotalPage(totalPage);
//		//放数据的集合
//		private List<T> list;
		int index = (currentPage-1)*currentCount;
		try {
			productLsist = dao.findAllProductsByPage(index,currentCount);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		adminPageBean.setList(productLsist);
		return adminPageBean;
	}
	public void delProduct(String pid) {
		try {
			dao.delProduct(pid);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	public Product findProductByPid(String pid) {
		Product product=null;
		try {
			product = dao.findProductByPid(pid);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return product;
	}
	public void updateProduct(Product product) {
		try {
			dao.updateProduct(product);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	public List<Map<String, Object>> findOrderInfoByOid(String oid) {
		List<Map<String, Object>> mapList =null;
		try {
			mapList = dao.findOrderInfoByOid(oid);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mapList;
	}
}
