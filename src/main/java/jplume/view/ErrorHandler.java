package jplume.view;

import jplume.http.Request;
import jplume.http.Response;
import jplume.template.TemplateEngine;

public class ErrorHandler {

	private TemplateEngine tplEngine;
	
	public ErrorHandler() {
		tplEngine = TemplateEngine.get();
	}
	
	public Response handle403(Request request) {
		return tplEngine.render(getClass(), "403.html");
	}
	
	public Response handle404(Request request) {
		System.out.println(tplEngine.render(getClass(), "404.html").getClass());
		return tplEngine.render(getClass(), "404.html");
	}
	
	public Response handle500(Request request) {
		return tplEngine.render(getClass(), "500.html");
	}
}
