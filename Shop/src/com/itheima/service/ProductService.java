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
    //���������Ʒ
	public List<Product> findHotProductList() {
		List<Product> hotProductList =null;
		try {
			hotProductList = dao.findHotProductList();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return hotProductList;
	}

	//���������Ʒ
	public List<Product> findLatestProductList() {
		List<Product> latestProductList =null;
		try {
			latestProductList = dao.findLatestProductList();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return latestProductList;
	}
   //�����Ʒ����
	public List<Category> findAllCategory() {
		List<Category> categoryList =null;
		try {
			categoryList = dao.findAllCategory();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return categoryList;
	}
  //����cid���ָ��cid��ҳ�����Ʒ
	public PageBean findProductListByCid(String cid, int currentPage, int currentCount) {
		//��װһ��pageBean����web��
		PageBean pageBean = new PageBean();
		int totalCount=0;
		List<Product> list=null;
		//1��װ��ǰҳ
		pageBean.setCurrentPage(currentPage);
		//2��װÿҳ��ʾ������
		pageBean.setCurrentCount(currentCount);
		//3��װ������
		try {
			totalCount = dao.getTotalCount(cid);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		pageBean.setTotalCount(totalCount);
		//4��װ��ҳ��
		/*
		 * ������  ��ǰҳ��ʾ������  ��ҳ��
		 * 10   4           3
		 * 11   4           3
		 * 12   4           3
		 * 13   4           4
		 */
		int totalPage = (int) Math.ceil(1.0*totalCount/currentCount);
		pageBean.setTotalPage(totalPage);
		//5��װ��ǰҳ��ʾ������
		//select * from product where cid=? limit ?,?
		//��ǰҳ����ʼ����index�Ĺ�ϵ
		/*
		 * ҳ����limit��ʼ�����Ĺ�ϵ
		 * ҳ��               ��ʼ����              ÿҳ��ʾ����
		 * 1        0         4
		 * 2        4         4
		 * 3        8         4
		 * 
		 * ����index=����ǰҳ��-1��*ÿҳ��ʾ����
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
  //�ύ���� �����������ݺͶ�����������洢�����ݿ���
	public void submitOrder(Order order) {
		try {
			//1��������
			DataSourceUtils.startTransaction();
			//2����dao�洢order�����ݵķ���
			dao.addOrders(order);
			//3����dao�洢orderItem�����ݵķ���
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
   //���ָ���û��Ķ�������
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
