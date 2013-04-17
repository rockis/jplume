package jplume.petstore.web;

import org.springframework.beans.factory.annotation.Autowired;

import jplume.http.HttpResponse;
import jplume.http.Response;
import jplume.petstore.service.CatalogService;
import jplume.view.annotations.Prefix;
import jplume.view.annotations.View;

@Prefix(regex = "^/catalog")
public class CatalogAction {

	@Autowired
	private CatalogService catalogService;
	
	@View(regex="^$")
	public Response index() {
		return HttpResponse.ok("ok");
	}
}
