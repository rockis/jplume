package jplume.http;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import jplume.converters.Converter;

public abstract class AbstractRequest implements Request {

	protected final HttpServletRequest rawRequest;
	
	public final Map<String, String> META;
	protected final Session session;
	
	public AbstractRequest(HttpServletRequest request) {
		this.rawRequest = request;
		this.session    = new Session(request.getSession());
		this.META       = Collections.unmodifiableMap(createMeta(request));
	}
	
	protected Map<String, String> createMeta(HttpServletRequest request) {
		Map<String, String> meta = new HashMap<String, String>(System.getenv());
		return meta;
	}
	
	@Override
	public String getHeader(String name) {
		return rawRequest.getHeader(name);
	}
	
	@Override
	public int getIntHeader(String name) {
		return rawRequest.getIntHeader(name);
	}

	@Override
	public String getParam(String key) {
		return rawRequest.getParameter(key);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getParam(String key, T defval) {
		String val = rawRequest.getParameter(key);
		if (val == null || !Converter.validate(defval.getClass(), val)){
			return defval;
		}
		return (T)Converter.convert(defval.getClass(), val);
	}
	
	@Override
	public Map<String, String> getParams() {
		Map<String, String> params = new HashMap<>();
		for(Enumeration<String> e = rawRequest.getParameterNames(); e.hasMoreElements();) {
			String key = e.nextElement();
			params.put(key, rawRequest.getParameter(key));
		}
		return params;
	}
	
	public String getReferer() {
		return getHeader(HD_REFERER);
	}

	@Override
	public String getPath() {
		return rawRequest.getServletPath();
	}

	@Override
	public String getMethod() {
		return rawRequest.getMethod();
	}

	@Override
	public Map<String, String> getMeta() {
		return META;
	}

	@Override
	public String getQueryString() {
		return rawRequest.getQueryString();
	}

	@Override
	public String getCharacterEncoding() {
		return rawRequest.getCharacterEncoding();
	}

	@Override
	public int getContentLength() {
		return rawRequest.getContentLength();
	}

	@Override
	public String getContentType() {
		return rawRequest.getContentType();
	}

	@Override
	public String getContextPath() {
		return rawRequest.getContextPath();
	}

	@Override
	public String getProtocol() {
		return rawRequest.getProtocol();
	}

	@Override
	public String getRequestURI() {
		return rawRequest.getRequestURI();
	}

	@Override
	public StringBuffer getRequestURL() {
		return rawRequest.getRequestURL();
	}

	@Override
	public Session getSession() {
		return session;
	}
}
