package jplume.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import jplume.conf.Settings;

public class HttpResponse extends AbstractResponse {

	private String charset = null;
	
	private InputStream content = null;
	
	private int contentLength    = -1;
	
	private List<String[]> headers = new ArrayList<String[]>();
	
	public HttpResponse(int status, String mimeType, InputStream content, String contentType) {
		super(status);
		this.charset = Settings.defaultCharset();
		if (mimeType != null) {
			contentType = mimeType;
		}
		if (contentType == null) {
			contentType = Settings.defaultContentType() + "; charset=" + this.charset;
		}
		
		this.content     = content;
		this.headers.add(new String[]{"Content-Type", contentType});
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
	
	public void addHeader(String key, String value) {
		this.headers.add(new String[]{ key, value});
	}
	
	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
	}

	@Override
	public void apply(HttpServletResponse resp) {
		try {
			super.apply(resp);
			for (String[] header : this.headers) {
				resp.addHeader(header[0], header[1]);
			}
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
