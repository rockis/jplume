package jplume.petstore.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jplume.http.HttpResponse;
import jplume.http.Response;
import jplume.petstore.domain.Category;
import jplume.petstore.domain.Item;
import jplume.petstore.domain.Product;
import jplume.petstore.service.CatalogService;
import jplume.view.annotations.PathVar;
import jplume.view.annotations.Prefix;
import jplume.view.annotations.QueryVar;
import jplume.view.annotations.ViewMethod;

@Service
@Prefix(regex = "^/catalog")
public class CatalogAction extends AbstractAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8859837428745088581L;
	
	@Autowired
	private CatalogService catalogService;
	
	public List<Category> getCategories() {
		return catalogService.getCategoryList();
	}
	
	@ViewMethod(regex="^$")
	public Response index() {
		List<Category> cs = catalogService.getCategoryList();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("cs", cs);
		return render("catalog/main.html", data);
	}
	
	@ViewMethod(regex="^/([\\w]+)$")
	public Response index(@PathVar String categoryId) {
		Category category = catalogService.getCategory(categoryId);
		if (category == null) {
			return HttpResponse.notFound();
		}
		List<Product> ps = catalogService.getProductListByCategory(categoryId);
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("products", ps);
		data.put("category", category);
		return render("catalog/category.html", data);
	}
	
	@ViewMethod(regex="^/([\\w]+)/([\\w\\-\\d]+)$")
	public Response index(@PathVar String categoryId, @PathVar String productId) {
		Category category = catalogService.getCategory(categoryId);
		if (category == null) {
			return HttpResponse.notFound();
		}
		Product product = catalogService.getProduct(productId);
		if (product == null) {
			return HttpResponse.notFound();
		}
		List<Item> items = catalogService.getItemListByProduct(productId);
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("category", category);
		data.put("product", product);
		data.put("items", items);
		return render("catalog/product.html", data);
	}
	
	@ViewMethod(regex="^/([\\w]+)/([\\w\\-\\d]+)/([\\w\\-\\d]+)$")
	public Response index(@PathVar String categoryId, @PathVar String productId, @PathVar String itemId) {
		Category category = catalogService.getCategory(categoryId);
		if (category == null) {
			return HttpResponse.notFound();
		}
		Product product = catalogService.getProduct(productId);
		if (product == null) {
			return HttpResponse.notFound();
		}
		Item item = catalogService.getItem(itemId);
		if (item == null) {
			return HttpResponse.notFound();
		}
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("category", category);
		data.put("product", product);
		data.put("item", item);
		return render("catalog/item.html", data);
	}
	
	@ViewMethod(regex="^/search$") 
	public Response search(@QueryVar(name="keyword", defval = "") String keyword){
		List<Product> products = catalogService.searchProductList(keyword);
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("products", products);
		return render("catalog/search.html", data);
	}
}
