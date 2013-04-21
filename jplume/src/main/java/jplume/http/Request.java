package jplume.http;

import java.util.Map;

public interface Request {

	public static final String HD_ACCEPT = "Accept";
	public static final String HD_ACCEPT_CHARSET = "Accept-Charset";
	public static final String HD_ACCEPT_ENCODIGN = "Accept-Encoding";
	public static final String HD_ACCEPT_LANGUAGE = "Accept-Language";

	public static final String HD_REFERER = "Referer";
	
	public static final String HD_IF_MODIFIED_SINCE = "If-Modified-Since";
	
	public Map<String, String> getMeta();
	
	public String getPath();
	
	public String getQueryString();
	
	public String getMethod();
	
	public <T> T getParam(String key, T defval);
	
	public String getParam(String key);
	
	public Map<String, String> getParams();
	
	public String getCharacterEncoding();

	public int getContentLength();

	public String getContentType();

	public String getContextPath();

	public String getProtocol();

	public String getRequestURI();

	public StringBuffer getRequestURL();
	
	public String getHeader(String name);
	
	public int getIntHeader(String name);
	
	public Session getSession();
	
	public String getReferer();
}
