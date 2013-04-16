package jplume.petstore.web;

import java.util.Iterator;

import jplume.petstore.domain.Cart;
import jplume.petstore.domain.CartItem;
import jplume.petstore.domain.Item;
import jplume.petstore.service.CatalogService;

public class CartActionBean extends AbstractAction {

  private static final long serialVersionUID = -4038684592582714235L;

  private static final String VIEW_CART = "/WEB-INF/jsp/cart/Cart.jsp";
  private static final String CHECK_OUT = "/WEB-INF/jsp/cart/Checkout.jsp";

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
//
//  public Resolution addItemToCart() {
//    if (cart.containsItemId(workingItemId)) {
//      cart.incrementQuantityByItemId(workingItemId);
//    } else {
//      // isInStock is a "real-time" property that must be updated
//      // every time an item is added to the cart, even if other
//      // item details are cached.
//      boolean isInStock = catalogService.isItemInStock(workingItemId);
//      Item item = catalogService.getItem(workingItemId);
//      cart.addItem(item, isInStock);
//    }
//
//    return new ForwardResolution(VIEW_CART);
//  }
//
//  public Resolution removeItemFromCart() {
//
//    Item item = cart.removeItemById(workingItemId);
//
//    if (item == null) {
//      setMessage("Attempted to remove null CartItem from Cart.");
//      return new ForwardResolution(ERROR);
//    } else {
//      return new ForwardResolution(VIEW_CART);
//    }
//  }
//
//  public Resolution updateCartQuantities() {
//    HttpServletRequest request = context.getRequest();
//
//    Iterator<CartItem> cartItems = getCart().getAllCartItems();
//    while (cartItems.hasNext()) {
//      CartItem cartItem = (CartItem) cartItems.next();
//      String itemId = cartItem.getItem().getItemId();
//      try {
//        int quantity = Integer.parseInt((String) request.getParameter(itemId));
//        getCart().setQuantityByItemId(itemId, quantity);
//        if (quantity < 1) {
//          cartItems.remove();
//        }
//      } catch (Exception e) {
//        //ignore parse exceptions on purpose
//      }
//    }
//
//    return new ForwardResolution(VIEW_CART);
//  }
//
//  public ForwardResolution viewCart() {
//    return new ForwardResolution(VIEW_CART);
//  }
//
//  public ForwardResolution checkOut() {
//    return new ForwardResolution(CHECK_OUT);
//  }

  public void clear() {
    cart = new Cart();
    workingItemId = null;
  }

}
