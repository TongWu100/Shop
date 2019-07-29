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
		//Ŀ��1�ռ������ݷ�װ��һ��Productʵ��
		//Ŀ��2���ϴ�ͼƬ�浽������
		//���������ļ����
		Product product = new Product();
		//�ռ����ݵ�����
		Map<String,Object>map = new HashMap<String,Object>();
		try {
			DiskFileItemFactory factory = new DiskFileItemFactory();
			// �����ļ��ϴ��ĺ��Ķ���
			ServletFileUpload upload = new ServletFileUpload(factory);
			// ����request����ļ�����󼯺�
			List<FileItem> parseRequest = upload.parseRequest(request);
			for (FileItem Item : parseRequest) {
				boolean formField = Item.isFormField();
				//�ж��Ƿ�����ͨ����
				if(formField){
					//��ͨ���� ��ñ������� ��װ��productʵ����
					String fieldName = Item.getFieldName();
					String fieldValue = Item.getString("UTF-8");
					map.put(fieldName, fieldValue);
					
				}else{
				   //�ļ��ϴ������ļ����� ����ļ�����
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
			//�Ƿ�product�������ݷ�װ��ȫ
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
			//����װ�õ�product���ݸ�service
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
