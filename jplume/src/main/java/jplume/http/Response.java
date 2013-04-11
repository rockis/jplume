package jplume.http;

import javax.servlet.http.HttpServletResponse;

public interface Response {

	public int getCode();
	
	public void apply(HttpServletResponse resp);
	
	public void addHeader(String key, String value);
	
	public boolean hasHeader(String key);
}
