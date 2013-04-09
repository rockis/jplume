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
import jplume.view.annotations.RequireHttpMethod;
import jplume.view.annotations.View;

public class ExampleAction {

	public String index(){
		return "Hello world";
	}
	
	@RequireHttpMethod(method = "POST")
	public String query(@PathVar int id, Request request) {
		request.getQuery();
		return id + "";
	}
	
	@View(pattern = "^/dynamic/([\\d]+)$")
	public String dynamic(@PathVar int id, Request request) {
		request.getQuery();
		return id + "";
	}
	
	@View(pattern = "^/dynamic/([\\d]+)/([\\w]+)$")
	public Response dynamic(@PathVar int id, @PathVar String name, Request request) {
		request.getQuery();
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
