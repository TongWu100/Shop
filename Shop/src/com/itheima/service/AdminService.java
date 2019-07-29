package com.itheima.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.itheima.dao.AdminDao;
import com.itheima.domain.Category;
import com.itheima.domain.Order;
import com.itheima.domain.PageBean;
import com.itheima.domain.Product;

public interface AdminService {
	public List<Category> findAllCategory();
	public void saveProduct(Product product);
	public List<Order> findAllOrders();
	public List<Product> findAllProducts();
	public PageBean findAllProducts(int currentPage, int currentCount);
	public void delProduct(String pid);
	public Product findProductByPid(String pid);
	public void updateProduct(Product product);
	public List<Map<String, Object>> findOrderInfoByOid(String oid);

}
