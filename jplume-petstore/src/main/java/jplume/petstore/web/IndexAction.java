package jplume.petstore.web;

import java.io.File;

import jplume.conf.Settings;
import jplume.core.Environ;
import jplume.http.HttpFileResponse;
import jplume.http.HttpResponse;
import jplume.http.Response;
import jplume.template.TemplateEngine;
import jplume.view.annotations.PathVar;
import jplume.view.annotations.QueryVar;
import jplume.view.annotations.ViewMethod;

public class IndexAction extends AbstractAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3833500246043084155L;

	public Response index(){
		return render("index.html");
	}
	
	@ViewMethod(methods = {"POST"})
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
	
	@ViewMethod(regex = "^/dynamic/([\\d]+)$")
	public String dynamic(@PathVar int id) {
		return id + "<a href='/d/dynamic/123/test'>go</a>";
	}
	
	@ViewMethod(regex = "^/dynamic/([\\d]+)/([\\w]+)$")
	public static Response dynamic(@PathVar int id, @PathVar String name) {
		return TemplateEngine.get().render("helloworld.html");
	}
	
	public Response media(@PathVar(name="path") String mediaPath) {
		
		String mediaRoot = Settings.get("MEDIA_ROOT");
		String resource = Environ.getContext().getRealPath(new File(new File(mediaRoot), mediaPath).getAbsolutePath());
		File mediaFile = new File(resource);
		if (!mediaFile.exists()) {
			return HttpResponse.notFound();
		}
		return new HttpFileResponse(mediaFile);
	}
}
