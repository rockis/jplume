package jplume.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import jplume.conf.Settings;

public class HttpResponse extends AbstractResponse {

	private String charset = null;
	
	private InputStream content = null;
	
	public HttpResponse(int status, String mimeType, InputStream content, String contentType) {
		super(status);
		this.charset = Settings.defaultCharset();
		if (mimeType != null) {
			contentType = mimeType;
		}
		if (contentType == null) {
			contentType = Settings.defaultContentType() + "; charset=" + this.charset;
		}
		
		this.content = content;
		addHeader("Content-Type", contentType);
	}
	
	public HttpResponse(int status, String mimeType, InputStream content) {
		this(status, mimeType, content, null);
	}
	
	public HttpResponse(int status, InputStream content, String contentType) {
		this(status, null, content, contentType);
	}
	
	public HttpResponse(int status, InputStream content) {
		this(status, content, null);
	}
	
	public HttpResponse(int status) {
		super(status);
	}
	
	public static HttpResponse create(int status, String content) {
		HttpResponse resp = new HttpResponse(status, new ByteArrayInputStream(content.getBytes()));
		resp.setContentLength(content.length());
		return resp;
	}
	
	public static HttpResponse ok(String content) {
		HttpResponse resp = create(HttpServletResponse.SC_OK, content);
		resp.setContentLength(content.length());
		return resp;
	}
	
	public static HttpResponse notModified(String mimeType) {
		HttpResponse resp =  new HttpResponse(HttpServletResponse.SC_NOT_MODIFIED, mimeType, null);
		return resp;
	}
	
	public static HttpResponse notFound() {
		return new HttpResponse(HttpServletResponse.SC_NOT_FOUND);
	}
	
	public static HttpResponse internalError(String content) {
		HttpResponse resp =  create(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, content);
		resp.setContentLength(content.length());
		return resp;
	}
	
	@Override
	public void apply(HttpServletResponse resp) {
		try {
			super.apply(resp);
			
			if (contentLength > 0) {
				resp.setContentLength(contentLength);
			}
			
			if (this.content != null) {
				byte[] buffer = new byte[1024];
				while(this.content.read(buffer) > 0) {
					resp.getOutputStream().write(buffer);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
