package jplume.http;

import javax.servlet.http.HttpServletResponse;

public interface Response {

	public int getStatus();
	
	public void addHeader(String key, String value);
	
	public boolean hasHeader(String key);
	
	public int getContentLength();
	
	public String getContentType();

	public void apply(HttpServletResponse resp);
	
}
