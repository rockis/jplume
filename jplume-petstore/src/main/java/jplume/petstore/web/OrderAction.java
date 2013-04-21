package jplume.petstore.web;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jplume.core.Environ;
import jplume.http.HttpResponse;
import jplume.http.Request;
import jplume.http.Response;
import jplume.petstore.domain.Account;
import jplume.petstore.domain.Cart;
import jplume.petstore.domain.Order;
import jplume.petstore.service.OrderService;
import jplume.view.annotations.PathVar;
import jplume.view.annotations.Prefix;
import jplume.view.annotations.ViewMethod;

@Service
@Prefix(regex="^/order")
public class OrderAction extends AbstractAction {

	private static final long serialVersionUID = -6171288227470176272L;

	@Autowired
	private transient OrderService orderService;

	@ViewMethod(regex="^/list$")
	public Response list() {
		Account acc = (Account)Environ.getRequest().getSession().get("user");
		if (acc == null) {
			return HttpResponse.redirect(AccountAction.class, "signIn");
		}
		List<Order> orders = orderService.getOrdersByUsername(acc.getUsername());
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("orders", orders);
		return render("/order/orders.html", data);
	}

	@ViewMethod(regex = "^/list$")
	public Response listOrders() {
		Account acc = (Account)Environ.getRequest().getSession().get("user");
		List<Order> orderList = orderService.getOrdersByUsername(acc.getUsername());
		Map<String, Object> data = new HashMap<>();
		data.put("orders", orderList);
		return render("/order/list.html", data);
	}

	@ViewMethod(regex="^/new$")
	public Response newOrder() {
		Order order = new Order();
		Account acc = (Account)Environ.getRequest().getSession().get("user");
		Cart cart   = (Cart)Environ.getRequest().getSession().get("cart");
		if (acc == null) {
			return HttpResponse.redirect(AccountAction.class, "signIn");
		} else if (Environ.getRequest().getSession().get("cart") != null) {
			
			order.initOrder(acc, cart);
			Map<String, Object> data = new HashMap<>();
			data.put("order", order);
			data.put("shippingAddressRequired", false);
			return render("/order/new.html", data);
		} else {
			return HttpResponse.notFound();
		}
	}

	@ViewMethod(regex="^/new/submit$")
	public Response submitOrder() {
		
		Order order = new Order();
		Request req = Environ.getRequest();
		try {
			Account acc = (Account)Environ.getRequest().getSession().get("user");
			Cart cart   = (Cart)Environ.getRequest().getSession().get("cart");
			order.initOrder(acc, cart);
			BeanUtils.populate(order, req.getParams());
			
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		if ("true".equals(req.getParam("confirmed"))) {
			orderService.insertOrder(order);
			Environ.getRequest().getSession().put("cart", new Cart());
			return HttpResponse.redirect(getClass(), "view", new String[] { "" + order.getOrderId() });
		}else{
			Map<String, Object> data = new HashMap<>();
			data.put("order", order);
			return render("/order/confirm.html", data);
		}
	}
	//
	@ViewMethod(regex="^/view/([\\d]+)$")
	public Response view(@PathVar int orderId) {
		Account acc = (Account)Environ.getRequest().getSession().get("user");
		if (acc == null) {
			return HttpResponse.redirect(AccountAction.class, "signIn");
		}
		
		Order order = orderService.getOrder(orderId);

		if (acc.getUsername().equals(order.getUsername()) && order != null) {
			Map<String, Object> data = new HashMap<>();
			data.put("order", order);
			return render("/order/view.html", data);
		} else {
			return HttpResponse.notFound();
		}
	 }
	//
	// public void clear() {
	// order = new Order();
	// shippingAddressRequired = false;
	// confirmed = false;
	// orderList = null;
	// }

}
