package jplume.petstore.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jplume.http.Response;
import jplume.petstore.domain.Cart;
import jplume.petstore.domain.CartItem;
import jplume.petstore.domain.Item;
import jplume.petstore.service.CatalogService;
import jplume.view.annotations.Prefix;
import jplume.view.annotations.ViewMethod;

@Service
@Prefix(regex = "^/cart")
public class CartAction extends AbstractAction {

	private static final long serialVersionUID = -4038684592582714235L;

	@Autowired
	private transient CatalogService catalogService;

	private Cart cart = new Cart();
	private String workingItemId;

	public Cart getCart() {
		return cart;
	}

	public void setCart(Cart cart) {
		this.cart = cart;
	}

	public void setWorkingItemId(String workingItemId) {
		this.workingItemId = workingItemId;
	}

	@ViewMethod(regex = "^$")
	public Response index() {
		return render("cart.html");
	}

	@ViewMethod(regex = "^/checkout$")
	public Response checkout() {
		return render("checkout.html");
	}

	@ViewMethod(regex = "^/add$")
	public Response add() {
		if (cart.containsItemId(workingItemId)) {
			cart.incrementQuantityByItemId(workingItemId);
		} else {
			// isInStock is a "real-time" property that must be updated
			// every time an item is added to the cart, even if other
			// item details are cached.
			boolean isInStock = catalogService.isItemInStock(workingItemId);
			Item item = catalogService.getItem(workingItemId);
			cart.addItem(item, isInStock);
		}

		return index();
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
		cart = new Cart();
		workingItemId = null;
	}

}
