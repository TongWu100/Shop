package com.itheima.web.servlet;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.IOUtils;

import com.itheima.domain.Category;
import com.itheima.domain.Product;
import com.itheima.service.AdminService;
import com.itheima.utils.BeanFactory;
import com.itheima.utils.CommonUtils;

public class adminAddProductServlet extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//目的1收集表单数据封装成一个Product实体
		//目的2将上传图片存到服务器
		//穿件磁盘文件项工厂
		Product product = new Product();
		//收集数据的容器
		Map<String,Object>map = new HashMap<String,Object>();
		try {
			DiskFileItemFactory factory = new DiskFileItemFactory();
			// 创建文件上传的核心对象
			ServletFileUpload upload = new ServletFileUpload(factory);
			// 解析request获得文件项对象集合
			List<FileItem> parseRequest = upload.parseRequest(request);
			for (FileItem Item : parseRequest) {
				boolean formField = Item.isFormField();
				//判断是否是普通表单项
				if(formField){
					//普通表单项 获得表单的数据 封装到product实体中
					String fieldName = Item.getFieldName();
					String fieldValue = Item.getString("UTF-8");
					map.put(fieldName, fieldValue);
					
				}else{
				   //文件上传项获得文件名称 获得文件内容
					String fileName = Item.getName();
					String path = this.getServletContext().getRealPath("upload");
					InputStream in = Item.getInputStream();
					OutputStream out = new FileOutputStream(path+"/"+fileName);
					IOUtils.copy(in, out);
					in.close();
					out.close();
					Item.delete();
					map.put("pimage", "upload/"+fileName);
				}
			}
			BeanUtils.populate(product, map);
			//是否product对象数据封装完全
			//private String pid
			product.setPid(CommonUtils.getUUID());
			//private Date pdate
			product.setPdate(new Date());
			//private int pflag
			product.setPflage(0);
			//private Category category
			Category category = new Category();
			category.setCid(map.get("cid").toString());
			product.setCategory(category);
			//将封装好的product传递给service
			AdminService service = (AdminService) BeanFactory.getBean("adminService");
			service.saveProduct(product);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
