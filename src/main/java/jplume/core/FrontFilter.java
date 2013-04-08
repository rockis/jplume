package jplume.core;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jplume.conf.InvalidConfigException;
import jplume.conf.Settings;
import jplume.http.HttpRequest;
import jplume.http.Response;


public class FrontFilter implements Filter{

	private RequestDispatcher dispatcher;
	
	private ServletContext servletContext;
	
	public FrontFilter(){
		
	}
	
	public void doFilter(ServletRequest _req, ServletResponse _resp,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest)_req;
		HttpServletResponse response = (HttpServletResponse)_resp;
		ActionContext.setContext(servletContext);
		
		Response resp = dispatcher.dispatch(new HttpRequest(request));
		resp.apply(response);
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
	}
}
