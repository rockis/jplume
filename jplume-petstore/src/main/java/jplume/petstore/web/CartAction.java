package jplume.petstore.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jplume.core.Environ;
import jplume.http.HttpJsonResponse;
import jplume.http.Response;
import jplume.petstore.domain.Cart;
import jplume.petstore.domain.CartItem;
import jplume.petstore.domain.Item;
import jplume.petstore.service.CatalogService;
import jplume.view.annotations.Prefix;
import jplume.view.annotations.QueryVar;
import jplume.view.annotations.ViewMethod;

@Service
@Prefix(regex = "^/cart")
public class CartAction extends AbstractAction {

	private static final long serialVersionUID = -4038684592582714235L;

	@Autowired
	private transient CatalogService catalogService;

	private Cart getCart() {
		return (Cart)Environ.getRequest().getSession().get("cart");
	}
	
	@ViewMethod(regex = "^$")
	public Response index() {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("cart", Environ.getRequest().getSession().get("cart"));
		return render("/cart/index.html", data);
	}

	@ViewMethod(regex = "^/checkout$")
	public Response checkout() {
		return render("checkout.html");
	}

	@ViewMethod(regex = "^/add$")
	public Response add(@QueryVar(name="itemId", defval="") String itemId) {
		Cart cart = getCart();
		if (cart.containsItemId(itemId)) {
			cart.incrementQuantityByItemId(itemId);
		} else {
			boolean isInStock = catalogService.isItemInStock(itemId);
			Item item = catalogService.getItem(itemId);
			cart.addItem(item, isInStock);
		}
		return HttpJsonResponse.ok();
	}

//	public Response remove() {
//
//		Item item = cart.removeItemById(workingItemId);
//
//		if (item == null) {
//			setMessage("Attempted to remove null CartItem from Cart.");
//			return new ForwardResolution(ERROR);
//		} else {
//			return new ForwardResolution(VIEW_CART);
//		}
//	}

//	public Resolution update() {
//		HttpServletRequest request = context.getRequest();
//
//		Iterator<CartItem> cartItems = getCart().getAllCartItems();
//		while (cartItems.hasNext()) {
//			CartItem cartItem = (CartItem) cartItems.next();
//			String itemId = cartItem.getItem().getItemId();
//			try {
//				int quantity = Integer.parseInt((String) request
//						.getParameter(itemId));
//				getCart().setQuantityByItemId(itemId, quantity);
//				if (quantity < 1) {
//					cartItems.remove();
//				}
//			} catch (Exception e) {
//				// ignore parse exceptions on purpose
//			}
//		}
//
//		return new ForwardResolution(VIEW_CART);
//	}

	@ViewMethod(regex="^/clear$")
	public void clear() {
		Environ.getRequest().getSession().remove("cart");
	}

}
