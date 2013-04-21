package jplume.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jplume.conf.ObjectFactory;
import jplume.conf.InvalidConfigException;
import jplume.conf.Settings;
import jplume.conf.URLResolveProvider;
import jplume.conf.URLReverser;
import jplume.http.HttpRequest;
import jplume.http.Request;
import jplume.http.Response;


public class FrontFilter implements Filter{

	private RequestDispatcher dispatcher;
	
	public FrontFilter(){
		
	}

	public void init(FilterConfig config) throws ServletException {
		String userConfigFile = config.getInitParameter(Settings.JPLUME_USER_CONFIG_FILE);
		try {
			Settings.initalize(userConfigFile);
		} catch (InvalidConfigException e) {
			throw new ServletException(e);
		}
		Environ.setContext(config.getServletContext());
		try {
			Environ.setActionFactory((ObjectFactory)Class.forName(Settings.get("ACTION_FACTORY")).newInstance());
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException e) {
			throw new ServletException("Couldn't create action factory", e);
		}
		URLResolveProvider urp = URLResolveProvider.create(Settings.get("ROOT_URLCONF"));
		Environ.setUrlReverser(new URLReverser(urp));
		
		this.dispatcher = new RequestDispatcher(urp);
	}
	
	public void doFilter(ServletRequest _req, ServletResponse _resp,
			FilterChain chain) throws IOException, ServletException {
		Request request = new HttpRequest((HttpServletRequest)_req);
		Environ.setRequest(request);
		
		Response resp = dispatcher.dispatch(request);
		
		HttpServletResponse httpResp = (HttpServletResponse)_resp;
		
		httpResp.setStatus(resp.getStatus());
		for(Map.Entry<String, String> entry : resp.getHeaders().entrySet()) {
			httpResp.addHeader(entry.getKey(), entry.getValue());
		}
		httpResp.setContentType(resp.getContentType());
		httpResp.setCharacterEncoding(resp.getEncoding());
		if (resp.getContentLength() > 0)
			httpResp.setContentLength(resp.getContentLength());

		InputStream content = resp.getContent();
		if (content != null) {
			OutputStream os = httpResp.getOutputStream();
			byte[] buf = new byte[1024];
			int len = 0;
			while( (len = content.read(buf)) > 0) {
				os.write(buf, 0, len);
			}
			content.close();
			os.close();
		}
	}
	
	public void destroy() {
		
	}
}
