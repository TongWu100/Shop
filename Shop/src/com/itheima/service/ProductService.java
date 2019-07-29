package com.itheima.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.itheima.dao.ProductDao;
import com.itheima.domain.Category;
import com.itheima.domain.Order;
import com.itheima.domain.OrderItem;
import com.itheima.domain.PageBean;
import com.itheima.domain.Product;
import com.itheima.utils.DataSourceUtils;

public class ProductService {
    	ProductDao dao = new ProductDao();
    //获得热门商品
	public List<Product> findHotProductList() {
		List<Product> hotProductList =null;
		try {
			hotProductList = dao.findHotProductList();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return hotProductList;
	}

	//获得最新商品
	public List<Product> findLatestProductList() {
		List<Product> latestProductList =null;
		try {
			latestProductList = dao.findLatestProductList();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return latestProductList;
	}
   //获得商品分类
	public List<Category> findAllCategory() {
		List<Category> categoryList =null;
		try {
			categoryList = dao.findAllCategory();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return categoryList;
	}
  //按照cid获得指定cid分页后的商品
	public PageBean findProductListByCid(String cid, int currentPage, int currentCount) {
		//封装一个pageBean返回web层
		PageBean pageBean = new PageBean();
		int totalCount=0;
		List<Product> list=null;
		//1封装当前页
		pageBean.setCurrentPage(currentPage);
		//2封装每页显示的条数
		pageBean.setCurrentCount(currentCount);
		//3封装总条数
		try {
			totalCount = dao.getTotalCount(cid);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		pageBean.setTotalCount(totalCount);
		//4封装总页数
		/*
		 * 总条数  当前页显示的条数  总页数
		 * 10   4           3
		 * 11   4           3
		 * 12   4           3
		 * 13   4           4
		 */
		int totalPage = (int) Math.ceil(1.0*totalCount/currentCount);
		pageBean.setTotalPage(totalPage);
		//5封装当前页显示的数据
		//select * from product where cid=? limit ?,?
		//当前页与起始索引index的关系
		/*
		 * 页数与limit起始索引的关系
		 * 页数               起始索引              每页显示条数
		 * 1        0         4
		 * 2        4         4
		 * 3        8         4
		 * 
		 * 索引index=（当前页数-1）*每页显示条数
 		 */
		int index = (currentPage-1)*currentCount;
		try {
			list = dao.findProductByPage(cid,index,currentCount);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		pageBean.setList(list);
		return pageBean;
	}

	public Product findProductInfoByPid(String pid) {
		Product product = null;
		try {
			product = dao.findProductInfoByPid(pid);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return product;
	}
  //提交订单 将订单的数据和订单项的数量存储到数据库中
	public void submitOrder(Order order) {
		try {
			//1开启事务
			DataSourceUtils.startTransaction();
			//2调用dao存储order表数据的方法
			dao.addOrders(order);
			//3调用dao存储orderItem表数据的方法
			dao.addOrderItem(order);
		} catch (SQLException e) {
			try {
				DataSourceUtils.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}finally {
			try {
				DataSourceUtils.commitAndRelease();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}

	public void updateOrderAdrr(Order order) {
		try {
			dao.updateOrderAdrr(order);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
   //获得指定用户的订单集合
	public List<Order> findAllOrders(String uid) {
		List<Order> orderList = null;
		try {
			orderList = dao.findAllOrders(uid);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return orderList;
	}

	public  List<Map<String, Object>> findAllOrderItemByOid(String oid) {
	    List<Map<String, Object>> mapList = null;
	    try {
			mapList = dao.findAllOrderItemByOid(oid);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return mapList;
	}

}
