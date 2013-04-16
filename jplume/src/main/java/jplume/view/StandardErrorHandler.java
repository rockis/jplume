package jplume.view;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import jplume.conf.Settings;
import jplume.conf.URLResolveProvider;
import jplume.conf.URLVisitor;
import jplume.http.Request;
import jplume.http.Response;
import jplume.template.TemplateEngine;
import jplume.utils.ExceptionUtil;

public class StandardErrorHandler implements ErrorHandler {
	
	private TemplateEngine tplEngine;
	
	public StandardErrorHandler() {
		tplEngine = TemplateEngine.get();
	}
	
	public Response handle403(Request request) {
		return tplEngine.render(getClass(), "403.html");
	}
	
	public Response handle404(Request request) {
		final List<String> patterns = new ArrayList<>();
		URLResolveProvider provider = URLResolveProvider.create(Settings.get("ROOT_URLCONF"));
		provider.visit(request.getPath(), new URLVisitor<String>() {
			public String visit(Pattern pattern, String[] indexedVars, Map<String, String> namedVars, ViewMethod method, boolean matched) {
				patterns.add(pattern.toString());
				return null;
			}
		});
		Map<String, Object> data = new HashMap<>();
		data.put("patterns", patterns);
		return tplEngine.render(getClass(), "404.html", data);
	}
	
	public Response handle500(Request request, Throwable e) {
		Map<String, Object> data = new HashMap<String, Object>();
		
		Throwable except = ExceptionUtil.last(e);
		StringWriter sw = new StringWriter();
		except.printStackTrace(new PrintWriter(sw));
		data.put("stacktrace", sw.toString());
		data.put("exception", except);
		return tplEngine.render(getClass(), "500.html", data);
	}
}
