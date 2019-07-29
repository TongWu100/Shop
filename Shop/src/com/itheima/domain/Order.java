package com.itheima.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order {
    private String oid;//�ö����Ķ�����
    private Date ordertime;//�ö������µ�ʱ��
    private double total;//�ö������ܽ��
    private int state;//�ö�����֧��״̬1�����Ѹ���0����δ����
    private String addr;//�ö������ջ��˵�ַ
    private String name;//�ö������ջ�������
    private String telephone;//�ö������ջ��˵绰
    private User user;//�ö��������ĸ��û�
    //�ö������ж��ٶ�����
    private List<OrderItem>orderItems = new ArrayList<OrderItem>();
    
	public List<OrderItem> getOrderItems() {
		return orderItems;
	}
	public void setOrderItems(List<OrderItem> orderItems) {
		this.orderItems = orderItems;
	}
	public String getOid() {
		return oid;
	}
	public void setOid(String oid) {
		this.oid = oid;
	}
	public Date getOrdertime() {
		return ordertime;
	}
	public void setOrdertime(Date ordertime) {
		this.ordertime = ordertime;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
    
}
