package com.itheima.dao;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.itheima.domain.User;
import com.itheima.utils.DataSourceUtils;

public class UserDao {
    //用户注册
	public int regist(User user) throws SQLException {
		QueryRunner qr =new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "insert into user values(?,?,?,?,?,?,?,?,?,?)";
//		String sql="insert into user values(?,?,?,?,?,?,?,?,?,?)";
//    	Object [] paras = {user.getUid(),user.getUsername(),user.getPassword(),user.getName(),
//    			user.getEmail(),null,user.getBirthday(),user.getSex(),null,null};
//    	int row = qr.update(sql, paras);
		int row = qr.update(sql,user.getUid(),user.getUsername(),user.getPassword(),
				user.getName(),user.getEmail(),user.getTelephone(),user.getBirthday(),user.getSex(),user.getState()
				,user.getCode());
		return row;
	}
    //用户激活
	public void active(String activeCode) throws SQLException {
		QueryRunner qr =new QueryRunner(DataSourceUtils.getDataSource());
		String sql ="update user set state=? where code=?";
		qr.update(sql, 1,activeCode);
	}
   //校验用户是否存在
	public Long checkUsername(String username) throws SQLException {
		QueryRunner qr =new QueryRunner(DataSourceUtils.getDataSource());
		String sql ="select count(*) from user where username=? ";
		Long isExist = (Long) qr.query(sql, new ScalarHandler(), username);
		return isExist;
	}
	//用户登录
	public User login(String username, String password) throws SQLException {
		QueryRunner qr =new QueryRunner(DataSourceUtils.getDataSource());
		String sql ="select * from user where username=? and password=?";
		User user = qr.query(sql, new BeanHandler<User>(User.class), username,password);
		return user;
	}

}
