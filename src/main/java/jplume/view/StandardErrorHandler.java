package jplume.view;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import jplume.http.Request;
import jplume.http.Response;
import jplume.template.TemplateEngine;

public class StandardErrorHandler implements ErrorHandler {
	private TemplateEngine tplEngine;
	
	public StandardErrorHandler() {
		tplEngine = TemplateEngine.get();
	}
	
	public Response handle403(Request request) {
		return tplEngine.render(getClass(), "403.html");
	}
	
	public Response handle404(Request request) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("req", request);
		return tplEngine.render(getClass(), "404.html", data);
	}
	
	public Response handle500(Request request, Throwable e) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("req", request);
		
		Throwable except = e;
		while(except.getCause() != null) {
			except = e.getCause();
		}
		StringWriter sw = new StringWriter();
		except.printStackTrace(new PrintWriter(sw));
		data.put("stacktrace", sw.toString());
		data.put("exception", except);
		return tplEngine.render(getClass(), "500.html", data);
	}
}
