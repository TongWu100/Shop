package com.itheima.web.servlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;

import com.google.gson.Gson;
import com.itheima.domain.Cart;
import com.itheima.domain.CartItem;
import com.itheima.domain.Category;
import com.itheima.domain.Order;
import com.itheima.domain.OrderItem;
import com.itheima.domain.PageBean;
import com.itheima.domain.Product;
import com.itheima.domain.User;
import com.itheima.service.ProductService;
import com.itheima.utils.CommonUtils;
import com.itheima.utils.JedisUtils2;
import com.itheima.utils.PaymentUtil;
import com.mchange.v2.codegen.bean.BeangenUtils;

import redis.clients.jedis.Jedis;

public class productServlet extends baseServlet {

//	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		String methodName = request.getParameter("method");
//		if("productListByCid".equals(methodName)) {
//			productListByCid(request,response);
//		}else if("categoryList".equals(methodName)) {
//			categoryList(request,response);
//		}else if("Index".equals(methodName)) {
//			Index(request,response);
//		}else if("productInfo".equals(methodName)) {
//			productInfo(request,response);
//		}
//	}
//
//	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		doGet(request, response);
//	}
	//����ҵĶ���
	public void myOrders(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//�ж��û��Ƿ��Ѿ���¼ δ��¼������벻ִ�� ��filter�Ѿ��������жϣ�
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		if (user == null) {
			// û�е�¼
			response.sendRedirect(request.getContextPath() + "/login.jsp");
			return;
		}
		ProductService service = new ProductService();
		//��ѯ���û������ж���������orders��
		//������ÿһ��order����������ǲ�������ȱ��orderItems
		List<Order> orderList = service.findAllOrders(user.getUid());
		//ѭ�����ж��� Ϊÿ��������䶩�������Ϣ
		if(orderList!=null) {
			for (Order order : orderList) {
				//���ÿһ��������oid
				String oid = order.getOid();
				//��ѯ�ö��������ж�����----mapList��װ���Ƕ��������͸ö������е���Ʒ��Ϣ
				 List<Map<String, Object>> mapList = service.findAllOrderItemByOid(oid);
				 //��mapListת����List<orderItem>orderItems
				 for (Map<String, Object> map : mapList) {
						try {
						OrderItem item = new OrderItem();
						// ��map��ȡ��count subtotal ��װ��OrderItem��
						// item.setCount(Integer.parseInt(map.get("count").toString()));
						BeanUtils.populate(item,map);
						//��map��ȡ��pimage pname shop_price��װ��product��
						Product product = new Product();
						BeanUtils.populate(product,map);
						//��product��װ��OrderItem��
						item.setProduct(product);
						//��orderItem��װ��order�е�orderItemList��
						order.getOrderItems().add(item);
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		//orderList��װ���
		request.setAttribute("orderList", orderList);
		request.getRequestDispatcher("/order_list.jsp").forward(request, response);
	}
	//ȷ�϶���
	public void confirmOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//1�����ջ�����Ϣ
		Map<String, String[]> properties = request.getParameterMap();
		Order order = new Order();
		try {
			BeanUtils.populate(order, properties);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		ProductService service = new ProductService();
		service.updateOrderAdrr(order);
		//2����֧��
		//���ѡ�������
		//String pd_FrpId = request.getParameter("pd_FrpId");
		//ֻ����һ���ӿ� ����ӿ��Ѿ��������е����нӿ��� ����ӿ��ǵ�����ƽ̨�ṩ��
		//��������ױ�֧��
		// ��� ֧�������������
		String orderid = request.getParameter("oid");
		//String money = order.getTotal()+"";
		String money = "0.01";
		// ����
		String pd_FrpId = request.getParameter("pd_FrpId");

		// ����֧����˾��Ҫ��Щ����
		String p0_Cmd = "Buy";
		String p1_MerId = ResourceBundle.getBundle("merchantInfo").getString("p1_MerId");
		String p2_Order = orderid;
		String p3_Amt = money;
		String p4_Cur = "CNY";
		String p5_Pid = "";
		String p6_Pcat = "";
		String p7_Pdesc = "";
		// ֧���ɹ��ص���ַ ---- ������֧����˾����ʡ��û�����
		// ������֧�����Է�����ַ
		String p8_Url = ResourceBundle.getBundle("merchantInfo").getString("callback");
		String p9_SAF = "";
		String pa_MP = "";
		String pr_NeedResponse = "1";
		// ����hmac ��Ҫ��Կ
		String keyValue = ResourceBundle.getBundle("merchantInfo").getString("keyValue");
		String hmac = PaymentUtil.buildHmac(p0_Cmd, p1_MerId, p2_Order, p3_Amt, p4_Cur, p5_Pid, p6_Pcat, p7_Pdesc,
				p8_Url, p9_SAF, pa_MP, pd_FrpId, pr_NeedResponse, keyValue);

		String url = "https://www.yeepay.com/app-merchant-proxy/node?pd_FrpId=" + pd_FrpId + "&p0_Cmd=" + p0_Cmd
				+ "&p1_MerId=" + p1_MerId + "&p2_Order=" + p2_Order + "&p3_Amt=" + p3_Amt + "&p4_Cur=" + p4_Cur
				+ "&p5_Pid=" + p5_Pid + "&p6_Pcat=" + p6_Pcat + "&p7_Pdesc=" + p7_Pdesc + "&p8_Url=" + p8_Url
				+ "&p9_SAF=" + p9_SAF + "&pa_MP=" + pa_MP + "&pr_NeedResponse=" + pr_NeedResponse + "&hmac=" + hmac;

		// �ض��򵽵�����֧��ƽ̨
		response.sendRedirect(url);
			
	}
	//�ύ����
	public void submitOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//�ж��û��Ƿ��Ѿ���¼ δ��¼������벻ִ��(filter�Ѿ��������ж�)
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		if(user==null) {
			//û�е�¼
			response.sendRedirect(request.getContextPath()+"/login.jsp");
			return;
		}
		//��װһ��order���� ���ݸ�service��
		Order order = new Order();
		/*
		 *  private String oid;//�ö����Ķ�����		 
		    private Date ordertime;//�ö������µ�ʱ��
		    private double total;//�ö������ܽ��
		    private int state;//�ö�����֧��״̬1�����Ѹ���0����δ����
		    private String addr;//�ö������ջ��˵�ַ
		    private String name;//�ö������ջ�������
		    private String telephone;//�ö������ջ��˵绰
		    private User user;//�ö��������ĸ��û�
		    //�ö������ж��ٶ�����
		    List<OrderItem>orderItems = new ArrayList<OrderItem>();
		 */
		//private String oid;//�ö����Ķ�����
		String oid = CommonUtils.getUUID();
		order.setOid(oid);
	    //private Date ordertime;//�ö������µ�ʱ��
		order.setOrdertime(new Date());
	    //private double total;//�ö������ܽ��
		//���session�еĹ��ﳵ
		Cart cart = (Cart) session.getAttribute("cart");
		double total = cart.getTotal();
		order.setTotal(total);
	    //private int state;//�ö�����֧��״̬1�����Ѹ���0����δ����
		order.setState(0);
	    //private String addr;//�ö������ջ��˵�ַ
		order.setAddr(null);
	    //private String name;//�ö������ջ�������
		order.setName(null);
	    //private String telephone;//�ö������ջ��˵绰
		order.setTelephone(null);
	    //private User user;//�ö��������ĸ��û�
		order.setUser(user);
	    //�ö������ж��ٶ�����
	    //List<OrderItem>orderItems = new ArrayList<OrderItem>();
		//�ӹ��ﳵ��ȡ�ù�����ļ���map
		Map<String, CartItem> cartItems = cart.getCartItems();
		for (Map.Entry<String, CartItem> entry: cartItems.entrySet()) {
			CartItem cartItem = entry.getValue();
			OrderItem orderItem = new OrderItem();
			   //private String itemid;//�������id
			    orderItem.setItemid(CommonUtils.getUUID());
			   //private String count;//����������Ʒ�Ĺ�������
			    orderItem.setCount(cartItem.getBuyNum());
			   //private double subtotal;//����������Ʒ��С��
			    orderItem.setSubtotal(cartItem.getSubtotal());
			   //private Product product;//�������ڲ�����Ʒ
			    orderItem.setProduct(cartItem.getProduct());
			   //private Order order;//�ö����������ĸ�����
			    orderItem.setOrder(order);
			   //���ö�������ӵ������Ķ���������
			order.getOrderItems().add(orderItem);
		}
		//order�����װ����� �������ݵ�service��
		ProductService service = new ProductService();
		service.submitOrder(order);
		
		//��order�浽session��
		session.setAttribute("order", order);
		//��ת��������Ϣҳ
		response.sendRedirect(request.getContextPath()+"/order_info.jsp");
		
	}
	//��չ��ﳵ
	public void clearCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		session.removeAttribute("cart");
		//��ת��cart.jsp
	    response.sendRedirect(request.getContextPath()+"/cart.jsp");
		
	}
	//ɾ����һ��Ʒ
	public void delProFromCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//���Ҫɾ����Ʒ��pid
		String pid = request.getParameter("pid");
		//ɾ��session�еĹ��ﳵ�Ĺ�������е�item
		HttpSession session = request.getSession();
		Cart cart = (Cart) session.getAttribute("cart");
		if(cart!=null) {
			Map<String, CartItem> cartItems = cart.getCartItems();
			//��Ҫ�޸��ܼ۸�
			cart.setTotal(cart.getTotal()-cartItems.get(pid).getSubtotal());
			cartItems.remove(pid);
			cart.setCartItems(cartItems);
		}
		session.setAttribute("cart", cart);
		//��ת��cart.jsp
		response.sendRedirect(request.getContextPath()+"/cart.jsp");
	}
	//����Ʒ��ӵ����ﳵ
	public void addProductToCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	  ProductService service = new ProductService();
	  HttpSession session = request.getSession();
	  //��������Ʒ��pid
		String pid = request.getParameter("pid");
	  //��������Ʒ������
		int buyNum = Integer.parseInt(request.getParameter("buyNum"));
	  //���product����
		Product product = service.findProductInfoByPid(pid);
	  //����С��
		double subtotal = product.getShop_price()*buyNum;
	  //��װcartItem
		CartItem cartItem = new CartItem();
		cartItem.setBuyNum(buyNum);
		cartItem.setProduct(product);
		cartItem.setSubtotal(subtotal);
	 //��ù��ﳵ----�ж���session���Ƿ��Ѿ����ڹ��ﳵ
		Cart cart = (Cart) session.getAttribute("cart");
		if(cart==null) {
			cart = new Cart();
		}
		//��������ŵ����� key��pid
		//���жϹ��ﳵ���Ƿ��Ѿ���������Ʒ��------- ���ж�key�Ƿ��Ѿ������� 
		//������ﳵ���Ѿ����ڸ���Ʒ�� ��������������빺�ﳵ��ԭ������������Ӳ���
		Map<String, CartItem> cartItems = cart.getCartItems();
		double newsubtotal = 0.0;
		if(cartItems.containsKey(pid)) {
			//ȡ��ԭ����Ʒ������
			CartItem cartItem2 = cartItems.get(pid);
			int oldBuyNum = cartItem2.getBuyNum();
			oldBuyNum+=buyNum;
			cartItem2.setBuyNum(oldBuyNum);
			//ԭ����Ʒ��С��
			double oldsubtotal = cartItem2.getSubtotal();
			//����Ʒ��С��
			newsubtotal=buyNum*product.getShop_price();
			cartItem2.setSubtotal(oldsubtotal+newsubtotal);
			cart.setCartItems(cartItems);
		}else {
			//�������û�и���Ʒ			
			cartItems.put(product.getPid(), cartItem);
			newsubtotal=buyNum*product.getShop_price();
		}
		//�����ܼƽ��
		double total = cart.getTotal()+newsubtotal;
		cart.setTotal(total);
		//�����ٴηŻ�session��
		session.setAttribute("cart", cart);
		//ֱ����ת�����ﳵҳ��
		//request.getRequestDispatcher("/cart.jsp").forward(request, response); �����ˢ��ҳ��Ǯ�ظ��ۼƵ�����
		response.sendRedirect(request.getContextPath()+"/cart.jsp");
	}
	//��ʾ��Ʒ���Ĺ���
	public void categoryList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				//׼��category����
				ProductService service = new ProductService();
				//�ȴӻ����в�ѯcategoryList �����ֱ��ʹ��û�еĻ������ݿ��в�ѯ
				//���jedis���� ����redis���ݿ�
				Jedis jedis = JedisUtils2.getJedis();
				String categoryListJson = jedis.get("categoryListJson");
				//�ж�categoryListJson�Ƿ�Ϊ��
				if(categoryListJson==null) {
					//�����ݿ��в�ѯcategory
					System.out.println("����û������ ��ѯ���ݿ�");
				 List<Category> categoryList = service.findAllCategory();
				 Gson gson = new Gson();
				 categoryListJson = gson.toJson(categoryList);
				 jedis.set("categoryListJson", categoryListJson);
					
				}
				response.setContentType("text/html;charset=UTF-8");
				response.getWriter().write(categoryListJson);
	}
	//��ʾ��ҳ�Ĺ���
	public void Index(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				ProductService service = new ProductService();
		
				//׼��������Ʒ--List<Product>
				List<Product> hotProductList = service.findHotProductList();
				//׼��������Ʒ--List<Product>
				List<Product> latestProductList = service.findLatestProductList();
				//׼����������
				//List<Category>categoryList = service.findAllCategory();
				
				//request.setAttribute("categoryList", categoryList);
				request.setAttribute("hotProductList",hotProductList);
				request.setAttribute("latestProductList",latestProductList);
				request.getRequestDispatcher("/index.jsp").forward(request,response);
				
	}
	//��ʾ��Ʒ����ϸ��Ϣ
	public void productInfo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//��õ�ǰҳ����Ʒ�б����Ϣ
				String cid = request.getParameter("cid");
				String currentPage = request.getParameter("currentPage");
				//���Ҫ��ѯ��Ʒ��pid
				String pid = request.getParameter("pid");
				ProductService service = new ProductService();
				Product product = service.findProductInfoByPid(pid);
				
				request.setAttribute("product", product);
				request.setAttribute("cid", cid);
				request.setAttribute("currentPage", currentPage);
				//ת��ǰ�洢cookie
				//��ÿͻ���Я��cookie ���������pids��cookie
				String pids = pid;
				Cookie[] cookies = request.getCookies();
				if(cookies!=null) {
					for (Cookie cookie : cookies) {
						if("pids".equals(cookie.getName())) {
							pids=cookie.getValue();
							//1-3-2���η��ʵ���Ʒpid��8 ------>8-1-3-2
							//1-3-2���η��ʵ���Ʒpid��3 ------>3-1-2
							//1-3-2���η��ʵ���Ʒpid��2 ------>2-1-3
							String[] split = pids.split("-");//{3,1,2}
							List<String> asList = Arrays.asList(split);//[3,1,2]
							LinkedList<String> list = new LinkedList<String>(asList);//[3,1,2]
							//�жϼ������Ƿ���ڵ�ǰpid
							if(list.contains(pid)) {
								list.remove(pid);
								list.addFirst(pid);
							}else {
								//���������
								list.addFirst(pid);
							}
							//��[3,1,2]ת��3-1-2�ַ���
							StringBuffer sb = new StringBuffer();
							for(int i=0;i<list.size()&&i<7;i++) {
								sb.append(list.get(i));
								sb.append("-");//3-1-2-
							}
							//ȥ��-
							pids=sb.substring(0, sb.length()-1);
						}
					}
				}
				Cookie cookie_pids = new Cookie("pids",pids);
				response.addCookie(cookie_pids);
				request.getRequestDispatcher("/product_info.jsp").forward(request, response);
	}
	//������Ʒ���������Ʒ���б�
	public void productListByCid(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//���cid
				String cid = request.getParameter("cid");
				String currentPageStr = request.getParameter("currentPage");
				if(currentPageStr==null)currentPageStr="1";
				int currentPage=Integer.parseInt(currentPageStr);
				int currentCount=12;
				ProductService service = new ProductService();
				PageBean pageBean = service.findProductListByCid(cid,currentPage,currentCount);
		        List<Product> historyProductList = new LinkedList<Product>();
				//��ÿͻ���Я�����ִ�pids��cookie
				Cookie[] cookies = request.getCookies();
				if(cookies!=null) {
					for (Cookie cookie : cookies) {
						if("pids".equals(cookie.getName())) {
							String pids = cookie.getValue();
							String[] split = pids.split("-");
							for(String pid:split) {
								Product pro = service.findProductInfoByPid(pid);
								historyProductList.add(pro);
							}
						}
					}
				}
				//����ʷ��¼�ļ��Ϸŵ�����
				request.setAttribute("historyProductList",historyProductList);
				request.setAttribute("cid", cid);
				request.setAttribute("pageBean", pageBean);
				request.getRequestDispatcher("/product_list.jsp").forward(request,response);
	}
}
