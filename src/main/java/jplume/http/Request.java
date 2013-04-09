package jplume.http;

import java.util.Map;

public interface Request {

	public String getPath();
	public Query getQuery();
	
	public String getQueryString();
	
	public String getMethod();
	
	
	public Map<String, String> getMeta();
}
