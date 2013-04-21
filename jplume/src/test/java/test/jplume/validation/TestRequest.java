package test.jplume.validation;

import java.util.Map;

import jplume.http.Request;
import jplume.http.Session;

public class TestRequest implements Request {

	private Map<String, String> values;
	TestRequest(Map<String, String> values) {
		this.values = values;
	}
	
	@Override
	public Map<String, String> getMeta() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getQueryString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMethod() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T getParam(String key, T defval) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getParam(String key) {
		return values.get(key);
	}

	@Override
	public String getCharacterEncoding() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getContentLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getContentType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getContextPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProtocol() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRequestURI() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StringBuffer getRequestURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getHeader(String name) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int getIntHeader(String name) {
		return 0;
	}

	@Override
	public String getReferer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Session getSession() {
		// TODO Auto-generated method stub
		return null;
	}
	
	


	public Map<String, String> getParams() {
		return null;
	}
}
