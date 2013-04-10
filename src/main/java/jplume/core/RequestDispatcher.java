package jplume.core;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jplume.Interceptors.Interceptor;
import jplume.conf.Settings;
import jplume.http.HttpResponse;
import jplume.http.Request;
import jplume.http.Response;
import jplume.view.annotations.View;

public class RequestDispatcher {

	private Logger logger = LoggerFactory.getLogger(RequestDispatcher.class);
	
	private DispatcherProvider patterns;

	private List<Interceptor> interceptors;
	
	public RequestDispatcher(String urlconf) {
		this(create(urlconf));
	}

	public RequestDispatcher(URL urlconf) {
		this(create(urlconf));
	}
	
	private RequestDispatcher(DispatcherProvider patterns) {
		this.patterns = patterns;
		this.interceptors = new ArrayList<Interceptor>();
		
		List<String> interClasses = Settings.getList("INTERCEPTORS");
		for (String className : interClasses) {
			try {
				Class<?> c = Class.forName(className);
				if (!Interceptor.class.isAssignableFrom(c)){
					logger.error("Class " + className + " is not implements " + Interceptor.class);
				}
				this.interceptors.add((Interceptor)c.newInstance());
			} catch (Exception e) {
				logger.error("Cannot create interceptor:" + className, e);
			}
		}
	}

	public Response dispatch(Request request) throws URLDispatchException{
		
		Response respTmp = beforeDispatch(request);
		if (respTmp != null) {
			return respTmp;
		}
		Response resp = null;
		try {
			resp = patterns.dispatch(request);
		} catch (Exception e) {
			logger.error("Internal Error:" + request.getRequestURL().toString(), e);
			respTmp = onException(request, e);
			if (respTmp != null) {
				return respTmp;
			}
			resp = HttpResponse.internalError(e);
		}
		if (resp == null) {
			resp = HttpResponse.notFound();
		}
		respTmp = afterDispatch(request, resp);
		if (respTmp != null) {
			return respTmp;
		}
		return resp;
	}

	private Response beforeDispatch(Request request) {
		for (Interceptor inter : interceptors) {
			Response resp = inter.processRequest(request);
			if (resp != null) {
				return resp;
			}
		}
		return null;
	}
	
	private Response afterDispatch(Request request, Response response) {
		for (Interceptor inter : interceptors) {
			Response resp = inter.processResponse(request, response);
			if (resp != null) {
				return resp;
			}
		}
		return null;
	}
	
	private Response onException(Request request, Throwable e) {
		for (Interceptor inter : interceptors) {
			Response resp = inter.processException(request, e);
			if (resp != null) {
				return resp;
			}
		}
		return null;
	}
	
	public static DispatcherProvider patterns(String actionClassName,
			DispatcherProvider... patterns) {
		Class<?> actionClass;
		try {
			if (actionClassName.length() > 0) {
				actionClass = Class.forName(actionClassName);
				return new URLPatternGroup(actionClass, patterns);
			} else {
				return new URLPatternGroup(patterns);
			}
		} catch (ClassNotFoundException e) {
			throw new URLPatternException("Invalid Pattern: No Such Class '"
					+ actionClassName + "'");
		}
	}

	public static DispatcherProvider pattern(String regex, String callback) {
		return new URLPattern(regex, callback);
	}

	public static DispatcherProvider pattern(String regex, DispatcherProvider patterns) {
		patterns.setRegexPrefix(regex);
		return patterns;
	}

	public static DispatcherProvider include(String urlconf) {
		return create(urlconf);
	}

	public static DispatcherProvider create(String urlconf) {
		if (urlconf.indexOf('/') > 0) { // urlconf is xxx.urls
			if (!urlconf.endsWith(".urls")) {
				urlconf = urlconf + ".urls";
			}
			URL url = RequestDispatcher.class.getClassLoader().getResource(
					urlconf);
			if (url == null) {
				throw new URLPatternException("Cannot read urlconf '"
						+ urlconf + "'");
			}
			// urlconf in classpath
			return create(url);
		} else { // urlconf is class name
			try {
				Class<?> actionClass = Class.forName(urlconf);
				return create(actionClass);
			} catch (ClassNotFoundException e) {
				throw new URLPatternException("Cannot read urlconf '"
						+ urlconf + "'", e);
			}
		}
	}

	public static DispatcherProvider create(URL urlconf) {
		assert urlconf != null;

		ScriptEngineManager sem = new ScriptEngineManager();
		ScriptEngine jsEngine = sem.getEngineByName("js");
		try {
			jsEngine.eval("importPackage(Packages.jplume.core);");
			jsEngine.eval("patterns = RequestDispatcher.patterns;");
			jsEngine.eval("_ = pattern = RequestDispatcher.pattern;");
			jsEngine.eval("include = RequestDispatcher.include;");

			jsEngine.eval(new InputStreamReader(urlconf.openStream()));
			return (DispatcherProvider) jsEngine.get("urlpatterns");

		} catch (ScriptException e) {
			e.printStackTrace();
			throw new URLDispatchException("Urlconf '"
					+ urlconf.toString() + "' has an error", e);
		} catch (IOException e) {
			throw new URLDispatchException("Cannot read urlconf '"
					+ urlconf.toString() + "'", e);
		}
	}

	public static DispatcherProvider create(Class<?> actionClass) {
		Method[] views = actionClass.getMethods();
		List<URLPattern> patterns = new ArrayList<URLPattern>();
		for (Method view : views) {
			if (view.getAnnotation(View.class) != null) {
				patterns.add(new URLPattern(view));
			}
		}
		return new URLPatternGroup(patterns.toArray(new URLPattern[0]));
	}
}
