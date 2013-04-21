package jplume.http;

import java.io.InputStream;
import java.util.Map;

public class HttpResponseDelegate implements Response {

	protected Response resp;

	public HttpResponseDelegate(Response resp) {
		this.resp = resp;
	}

	public int getStatus() {
		return resp.getStatus();
	}

	public void addHeader(String key, String value) {
		resp.addHeader(key, value);
	}

	public Map<String, String> getHeaders() {
		return resp.getHeaders();
	}

	public int getContentLength() {
		return resp.getContentLength();
	}

	public String getContentType() {
		return resp.getContentType();
	}

	public String getEncoding() {
		return resp.getEncoding();
	}

	public InputStream getContent() {
		return resp.getContent();
	}
	
}
