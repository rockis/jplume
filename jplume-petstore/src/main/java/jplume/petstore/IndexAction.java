package jplume.petstore;

import java.io.File;

import jplume.conf.Settings;
import jplume.core.ActionContext;
import jplume.http.HttpFileResponse;
import jplume.http.HttpResponse;
import jplume.http.Request;
import jplume.http.Response;
import jplume.template.TemplateEngine;
import jplume.utils.Http;
import jplume.view.annotations.PathVar;
import jplume.view.annotations.QueryVar;
import jplume.view.annotations.View;

public class IndexAction {

	public String index(){
		return "Hello Petstore";
	}
	
	@View(methods = {"POST"})
	public String query(@PathVar int id) {
		return id + "";
	}
	
	public Response query(@PathVar int id, @QueryVar(name = "q", defval = "1") int q) {
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
	public String dynamic(@PathVar int id) {
		return id + "<a href='/d/dynamic/123/test'>go</a>";
	}
	
	@View(pattern = "^/dynamic/([\\d]+)/([\\w]+)$")
	public static Response dynamic(@PathVar int id, @PathVar String name) {
		return TemplateEngine.get().render("helloworld.html");
	}
	
	public Response media(@PathVar(name="path") String mediaPath) {
		String mediaRoot = Settings.get("MEDIA_ROOT");
		if (mediaRoot == null) {
			return HttpResponse.notFound();
		}
		
		File mediaRootFile = new File(mediaRoot);
		File mediaFile = new File(mediaRootFile, mediaPath);
		if (!mediaFile.exists()) {
			return HttpResponse.notFound();
		}
		String etags = HttpFileResponse.calcEtag(mediaFile);
		Request request = ActionContext.getRequest();
		if (etags.equals(request.getHeader("ETag"))){
			return HttpResponse.notModified(Http.getMimeType(mediaFile));
		}
		return new HttpFileResponse(mediaFile);
	}
}
