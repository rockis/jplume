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
import jplume.http.HttpResponse;
import jplume.http.Response;


public class FrontFilter implements Filter{

	private RequestDispatcher requestDispatcher;
	
	public FrontFilter(){
		
	}
	
	public void doFilter(ServletRequest _req, ServletResponse _resp,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest)_req;
		HttpServletResponse response = (HttpServletResponse)_resp;
		
		Response resp = requestDispatcher.dispatch(new HttpRequest(request));
		if (resp == null) {
			HttpResponse.notFound().apply(response);
		}else{
			resp.apply(response);
		}
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
		this.requestDispatcher = new RequestDispatcher(Settings.get("ROOT_URLCONF"));
	}
}
