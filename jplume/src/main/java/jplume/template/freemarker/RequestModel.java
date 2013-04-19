package jplume.template.freemarker;

import java.util.Map;

import jplume.core.Environ;
import jplume.http.Request;
import jplume.http.Session;

public class RequestModel implements Request {
	
	
	private Request getRequest() {
		return Environ.getRequest();
	}

	public Map<String, String> getMeta() {
		return getRequest().getMeta();
	}

	public String getPath() {
		return getRequest().getPath();
	}

	public String getQueryString() {
		return getRequest().getQueryString();
	}

	public String getMethod() {
		return getRequest().getMethod();
	}

	public <T> T getParam(String key, T defval) {
		return getRequest().getParam(key, defval);
	}

	public String getParam(String key) {
		return getRequest().getParam(key);
	}

	public String getCharacterEncoding() {
		return getRequest().getCharacterEncoding();
	}

	public int getContentLength() {
		return getRequest().getContentLength();
	}

	public String getContentType() {
		return getRequest().getContentType();
	}

	public String getContextPath() {
		return getRequest().getContextPath();
	}

	public String getProtocol() {
		return getRequest().getProtocol();
	}

	public String getRequestURI() {
		return getRequest().getRequestURI();
	}

	public StringBuffer getRequestURL() {
		return getRequest().getRequestURL();
	}

	public String getHeader(String name) {
		return getRequest().getHeader(name);
	}

	@Override
	public Session getSession() {
		return getRequest().getSession();
	}
	
}
