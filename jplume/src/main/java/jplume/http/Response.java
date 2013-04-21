package jplume.http;

import java.io.InputStream;
import java.util.Map;

public interface Response {

	public int getStatus();
	
	public void addHeader(String key, String value);
	
	public Map<String, String> getHeaders();

	public int getContentLength();
	
	public String getContentType();
	
	public String getEncoding();
	
	public InputStream getContent();
	
}
