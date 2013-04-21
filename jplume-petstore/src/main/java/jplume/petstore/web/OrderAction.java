package jplume.petstore.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jplume.core.Environ;
import jplume.http.HttpResponse;
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

	private static final String CONFIRM_ORDER = "/WEB-INF/jsp/order/ConfirmOrder.jsp";
	private static final String LIST_ORDERS = "/WEB-INF/jsp/order/ListOrders.jsp";
	private static final String NEW_ORDER = "/WEB-INF/jsp/order/NewOrderForm.jsp";
	private static final String SHIPPING = "/WEB-INF/jsp/order/ShippingForm.jsp";
	private static final String VIEW_ORDER = "/WEB-INF/jsp/order/ViewOrder.jsp";

	private static final List<String> CARD_TYPE_LIST;

	@Autowired
	private transient OrderService orderService;

	private Order order = new Order();
	private boolean shippingAddressRequired;
	private boolean confirmed;
	private List<Order> orderList;

	static {
		List<String> cardList = new ArrayList<String>();
		cardList.add("Visa");
		cardList.add("MasterCard");
		cardList.add("American Express");
		CARD_TYPE_LIST = Collections.unmodifiableList(cardList);
	}

	public int getOrderId() {
		return order.getOrderId();
	}

	public void setOrderId(int orderId) {
		order.setOrderId(orderId);
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public boolean isShippingAddressRequired() {
		return shippingAddressRequired;
	}

	public void setShippingAddressRequired(boolean shippingAddressRequired) {
		this.shippingAddressRequired = shippingAddressRequired;
	}

	public boolean isConfirmed() {
		return confirmed;
	}

	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}

	public List<String> getCreditCardTypes() {
		return CARD_TYPE_LIST;
	}

	public List<Order> getOrderList() {
		return orderList;
	}

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
	// public Resolution listOrders() {
	// HttpSession session = context.getRequest().getSession();
	// AccountActionBean accountBean = (AccountActionBean)
	// session.getAttribute("/actions/Account.action");
	// orderList =
	// orderService.getOrdersByUsername(accountBean.getAccount().getUsername());
	// return new ForwardResolution(LIST_ORDERS);
	// }
	//
	@ViewMethod(regex="^/new$")
	public Response newOrder() {
		Order order = new Order();
		Account acc = (Account)Environ.getRequest().getSession().get("user");
		Cart cart   = (Cart)Environ.getRequest().getSession().get("cart");
		if (acc != null) {
			return HttpResponse.redirect(Account.class, "signIn");
		} else if (Environ.getRequest().getSession().get("cart") != null) {
			
			order.initOrder(acc, cart);
			Map<String, Object> data = new HashMap<>();
			data.put("order", order);
			return render("/order/new.html", data);
		} else {
			return HttpResponse.notFound();
		}
	}

	@ViewMethod(regex="^/new/submit$")
	public Response submitOrder() {
//		HttpSession session = context.getRequest().getSession();
//
//		if (shippingAddressRequired) {
//			shippingAddressRequired = false;
//			return new ForwardResolution(SHIPPING);
//		} else if (!isConfirmed()) {
//			return new ForwardResolution(CONFIRM_ORDER);
//		} else if (getOrder() != null) {
//
//			orderService.insertOrder(order);
//
//			CartActionBean cartBean = (CartActionBean) session
//					.getAttribute("/actions/Cart.action");
//			cartBean.clear();
//
//			setMessage("Thank you, your order has been submitted.");
//
//			return new ForwardResolution(VIEW_ORDER);
//		} else {
//			setMessage("An error occurred processing your order (order was null).");
//			return new ForwardResolution(ERROR);
//		}
		return null;
	}
	//
	@ViewMethod(regex="^/view/([\\d]+)$")
	public Response view(@PathVar int orderId) {
		Account acc = (Account)Environ.getRequest().getSession().get("user");
		if (acc == null) {
			return HttpResponse.redirect(Account.class, "signIn");
		}
		
		Order order = orderService.getOrder(orderId);

		if (acc.getUsername().equals(order.getUsername())) {
			return render("/order/view.html");
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
