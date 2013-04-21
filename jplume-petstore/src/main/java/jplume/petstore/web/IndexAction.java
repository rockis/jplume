package jplume.petstore.web;

import java.io.File;

import jplume.conf.Settings;
import jplume.core.Environ;
import jplume.http.HttpFileResponse;
import jplume.http.HttpResponse;
import jplume.http.Request;
import jplume.http.Response;
import jplume.template.TemplateEngine;
import jplume.utils.Http;
import jplume.view.annotations.PathVar;
import jplume.view.annotations.QueryVar;
import jplume.view.annotations.ViewMethod;

public class IndexAction extends AbstractAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3833500246043084155L;

	public Response index(){
//		return HttpResponse.redirect(CatalogAction.class, "index");
		return render("index.html");
	}
	
	public Response help() {
		return render("help.html");
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
		Request request = Environ.getRequest();
		long lastModified = Http.fromGMT(request.getHeader(Request.HD_IF_MODIFIED_SINCE));
		String mediaRoot = Settings.get("MEDIA_ROOT");
		String resource = Environ.getContext().getRealPath(new File(new File(mediaRoot), mediaPath).getAbsolutePath());
		File mediaFile = new File(resource);
		if (!mediaFile.exists()) {
			return HttpResponse.notFound();
		}
		if (mediaFile.lastModified() <= lastModified) {
			return HttpResponse.notModified(Http.getMimeType(mediaFile));
		}
		return new HttpFileResponse(mediaFile);
	}
	
}
