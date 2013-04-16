package jplume.petstore.web;

import jplume.http.HttpResponse;
import jplume.http.Response;
import jplume.view.annotations.Prefix;
import jplume.view.annotations.View;

@Prefix(regex = "^/catalog")
public class CatalogAction {

	@View(regex="^$")
	public Response index() {
		return HttpResponse.ok("ok");
	}
}
