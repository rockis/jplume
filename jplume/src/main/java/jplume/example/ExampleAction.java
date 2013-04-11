package jplume.example;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import jplume.conf.Settings;
import jplume.core.ActionContext;
import jplume.http.HttpFileResponse;
import jplume.http.HttpResponse;
import jplume.http.Request;
import jplume.http.Response;
import jplume.template.TemplateEngine;
import jplume.view.annotations.PathVar;
import jplume.view.annotations.QueryVar;
import jplume.view.annotations.RequireHttpMethod;
import jplume.view.annotations.View;

public class ExampleAction {

	public String index(){
		return "Hello world";
	}
	
	@RequireHttpMethod(method = "POST")
	public String query(@PathVar int id, Request request) {
		return id + "";
	}
	
	public Response query(@PathVar int id, @QueryVar(name = "q", defval = "1") int q, Request request) {
		if (q == 403) {
			return HttpResponse.forbidden();
		}else if (q == 404) {
			return HttpResponse.notFound();
		}else if (q == 500) {
			throw new RuntimeException("test");
		}
		return HttpResponse.ok("ok");
	}
	
	@View(pattern = "^/dynamic/([\\d]+)$")
	public String dynamic(@PathVar int id, Request request) {
		return id + "";
	}
	
	@View(pattern = "^/dynamic/([\\d]+)/([\\w]+)$")
	public Response dynamic(@PathVar int id, @PathVar String name, Request request) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("base", ActionContext.getContextPath());
		return TemplateEngine.get().render("helloworld.html", map);
	}
	
	public Response media(@PathVar(index=1) String mediaPath) {
		String mediaRoot = Settings.get("MEDIA_ROOT");
		if (mediaRoot == null) {
			return HttpResponse.notFound();
		}
		
		File mediaRootFile = new File(mediaRoot);
		File mediaFile = new File(mediaRootFile, mediaPath);
		if (!mediaFile.exists()) {
			return HttpResponse.notFound();
		}
		return new HttpFileResponse(mediaFile);
	}
}
