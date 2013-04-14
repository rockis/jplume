package jplume.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jplume.interceptors.Interceptor;
import jplume.conf.URLResolveProvider;
import jplume.conf.Settings;
import jplume.conf.IllegalURLPattern;
import jplume.conf.URLVisitor;
import jplume.http.HttpResponse;
import jplume.http.Request;
import jplume.http.Response;
import jplume.view.ViewMethod;

public class RequestDispatcher {

	private Logger logger = LoggerFactory.getLogger(RequestDispatcher.class);
	
	private URLResolveProvider resolver;

	private List<Interceptor> interceptors;
	
	public RequestDispatcher(URLResolveProvider resolver) {
		this.resolver = resolver;
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

	public Response dispatch(final Request request) throws IllegalURLPattern{
		
		Response respTmp = beforeDispatch(request);
		if (respTmp != null) {
			return respTmp;
		}
		Response resp = null;
		try {
			resp = resolver.visit(request.getPath(), new URLVisitor<Response>() {
				@Override
				public Response visit(Pattern pattern, String[] indexedVars, Map<String, String> namedVars, ViewMethod viewMethod, boolean matched) {
					if (!matched) {
						return null;
					}
					return viewMethod.handle(request, indexedVars, namedVars);
				}
			});
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
	
}
