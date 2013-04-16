package jplume.petstore.persistence;

import java.util.List;

import jplume.petstore.domain.Product;


public interface ProductMapper {

  List<Product> getProductListByCategory(String categoryId);

  Product getProduct(String productId);

  List<Product> searchProductList(String keywords);

}
