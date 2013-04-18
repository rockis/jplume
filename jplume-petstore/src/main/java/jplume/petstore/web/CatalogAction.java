package jplume.petstore.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jplume.http.Response;
import jplume.petstore.domain.Category;
import jplume.petstore.service.CatalogService;
import jplume.view.annotations.Prefix;
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
}
