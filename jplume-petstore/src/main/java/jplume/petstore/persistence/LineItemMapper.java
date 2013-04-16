package jplume.petstore.persistence;

import java.util.List;

import jplume.petstore.domain.LineItem;

public interface LineItemMapper {

  List<LineItem> getLineItemsByOrderId(int orderId);

  void insertLineItem(LineItem lineItem);

}
