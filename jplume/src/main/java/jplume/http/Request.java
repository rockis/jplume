package jplume.http;

import java.util.Map;

public interface Request {

	public Map<String, String> getMeta();
	
	public String getPath();
	
	public String getQueryString();
	
	public String getMethod();
	
	public <T> T getParam(String key, T defval);
	
	public String getParam(String key);
	
	public String getCharacterEncoding();

	public int getContentLength();

	public String getContentType();

	public String getContextPath();

	public String getProtocol();

	public String getRequestURI();

	public StringBuffer getRequestURL();
	
	public String getHeader(String name);
	
	public Session getSession();
}
