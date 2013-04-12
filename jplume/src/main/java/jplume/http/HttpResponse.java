package jplume.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import jplume.conf.Settings;

public class HttpResponse extends AbstractResponse {

	private InputStream content = null;
	
	
	/**
	 * 
	 * @param status: http status code
	 * @param mimeType: eg. text/html, application/json
	 * @param content: content
	 * @param contentType: eg. text/html; charset=utf-8
	 */
	public HttpResponse(int status, InputStream content, String contentType, String charset) {
		super(status);
		this.content = content;
		this.setContentType(contentType);
		this.setCharset(charset);
	}
	
	public HttpResponse(int status, InputStream content) {
		this(status, content, Settings.getDefaultContentType(), null);
	}
	
	public HttpResponse(int status, InputStream content, String contentType) {
		this(status, content, contentType, null);
	}
	
	public HttpResponse(int status) {
		super(status);
	}
	
	public static Response create(int status, String content) {
		return new HttpResponse(status, new ByteArrayInputStream(content.getBytes()));
	}
	
	public static Response ok(String content) {
		return create(HttpServletResponse.SC_OK, content);
	}
	
	public static Response notModified(String mimeType) {
		HttpResponse resp =  new HttpResponse(HttpServletResponse.SC_NOT_MODIFIED, null, mimeType);
		return resp;
	}
	
	public static Response notFound() {
		return new HttpResponse(HttpServletResponse.SC_NOT_FOUND);
	}
	
	public static Response forbidden() {
		return new HttpResponse(HttpServletResponse.SC_FORBIDDEN);
	}
	
	public static Response internalError(Throwable e) {
		return new Http500Response(e);
	}
	
	public static Response internalError(String message, Throwable e) {
		return new Http500Response(message, e);
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
