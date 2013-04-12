package jplume.core;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jplume.interceptors.Interceptor;
import jplume.conf.Settings;
import jplume.http.HttpResponse;
import jplume.http.Request;
import jplume.http.Response;

public class RequestDispatcher {

	private Logger logger = LoggerFactory.getLogger(RequestDispatcher.class);
	
	private DispatcherProvider patterns;

	private List<Interceptor> interceptors;
	
	public RequestDispatcher(String urlconf) {
		this(DispatcherProvider.create(urlconf));
	}

	public RequestDispatcher(URL urlconf) {
		this(DispatcherProvider.create(urlconf));
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
	
}