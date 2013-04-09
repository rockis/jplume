package jplume.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jplume.Interceptors.Interceptor;
import jplume.conf.InvalidConfigException;
import jplume.conf.Settings;
import jplume.http.HttpRequest;
import jplume.http.Request;
import jplume.http.Response;


public class FrontFilter implements Filter{

	private RequestDispatcher dispatcher;
	
	private ServletContext servletContext;
	
	private List<Interceptor> interceptors;
	public FrontFilter(){
		
	}
	
	public void doFilter(ServletRequest _req, ServletResponse _resp,
			FilterChain chain) throws IOException, ServletException {
		
		ActionContext.setContext(servletContext);
		
		
		Request request = new HttpRequest((HttpServletRequest)_req);
		Response respTmp = beforeDispatch(request);
		if (respTmp != null) {
			respTmp.apply((HttpServletResponse)_resp);
			return;
		}
		Response resp = null;
		try {
			resp = dispatcher.dispatch(request);
		} catch (Exception e) {
			respTmp = onException(request, e);
			if (respTmp != null) {
				respTmp.apply((HttpServletResponse)_resp);
				return;
			}
		}
		respTmp = afterDispatch(request, resp);
		if (respTmp != null) {
			respTmp.apply((HttpServletResponse)_resp);
			return;
		}
		resp.apply((HttpServletResponse)_resp);

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
	
	public void destroy() {
		
	}

	public void init(FilterConfig config) throws ServletException {
		String userConfigFile = config.getInitParameter(Settings.JPLUME_USER_CONFIG_FILE);
		try {
			Settings.initalize(userConfigFile);
		} catch (InvalidConfigException e) {
			throw new ServletException(e);
		}
		this.servletContext = config.getServletContext();
		this.dispatcher = new RequestDispatcher(Settings.get("ROOT_URLCONF"));
		this.interceptors = new ArrayList<Interceptor>();
		
		List<String> interClasses = Settings.getList("INTERCEPTORS");
		for (String className : interClasses) {
			try {
				Class<?> c = Class.forName(className);
				if (!Interceptor.class.isAssignableFrom(c)){
					throw new ServletException("Class " + className + " is not implements " + Interceptor.class);
				}
				this.interceptors.add((Interceptor)c.newInstance());
			} catch (ServletException e) {
				throw e;
			} catch (Exception e) {
				throw new ServletException("Cannot create interceptor:" + className, e);
			}
		}
	}
	
}
