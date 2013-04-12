package jplume.core;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jplume.conf.InvalidConfigException;
import jplume.conf.Settings;
import jplume.http.HttpRequest;
import jplume.http.Request;
import jplume.http.Response;


public class FrontFilter implements Filter{

	private RequestDispatcher dispatcher;
	
	public FrontFilter(){
		
	}

	public void init(FilterConfig config) throws ServletException {
		ActionContext.setContext(config.getServletContext());
		String userConfigFile = config.getInitParameter(Settings.JPLUME_USER_CONFIG_FILE);
		try {
			Settings.initalize(userConfigFile);
		} catch (InvalidConfigException e) {
			throw new ServletException(e);
		}
		
		this.dispatcher = new RequestDispatcher(Settings.get("ROOT_URLCONF"));
		
	}
	
	public void doFilter(ServletRequest _req, ServletResponse _resp,
			FilterChain chain) throws IOException, ServletException {
		Request request = new HttpRequest((HttpServletRequest)_req);
		ActionContext.setRequest(request);
		
		Response resp = dispatcher.dispatch(request);
		resp.apply((HttpServletResponse)_resp);
	}

	public void destroy() {
		
	}
}
