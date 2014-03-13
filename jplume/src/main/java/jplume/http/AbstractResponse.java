package jplume.http;

import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractResponse implements Response, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7115164817476737257L;

	protected final int status;
	
	protected Map<String, String> headers = new HashMap<>();
	
	protected int contentLength  = 0;
	
	protected String contentType = null;

	protected String encoding = null;
	
	protected transient InputStream content = null;
	
	public AbstractResponse(int code) {
		this.status = code;
	}

	public int getStatus() {
		return status;
	}

	public void addHeader(String key, String value) {
		this.headers.put(key, value);
	}
	

	public Map<String, String> getHeaders() {
		Map<String, String> headers = new HashMap<>(this.headers);
		return headers;
	}

	@Override
	public int getContentLength() {
		return contentLength;
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public String getEncoding() {
		return encoding;
	}
	
}
