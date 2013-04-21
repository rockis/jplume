package jplume.http;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

public abstract class AbstractResponse implements Response{

	protected final int status;
	
	protected List<String[]> headers = new ArrayList<String[]>();
	
	protected InputStream content = null;
	
	protected int contentLength  = 0;
	
	protected String contentType = null;

	protected String charset = null;
	
	public AbstractResponse(int code) {
		this.status = code;
	}

	public int getStatus() {
		return status;
	}

	public void apply(HttpServletResponse resp) {
		resp.setStatus(this.status);
		if (this.charset != null) {
			resp.setCharacterEncoding(this.charset);
		}
		if (this.contentLength > 0) {
			resp.setContentLength(this.contentLength);
		}
		resp.setContentType(getContentType());
		for (String[] header : this.headers) {
			resp.addHeader(header[0], header[1]);
		}
	}

	public void addHeader(String key, String value) {
		this.headers.add(new String[]{key, value});
	}

	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
	}

	public int getContentLength() {
		return contentLength;
	}

	@Override
	public String getContentType() {
		return this.contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	@Override
	public boolean hasHeader(String key) {
		for (String[] header : this.headers) {
			if (header[0].equals(key)) {
				return true;
			}
		}
		return false;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}
	
}
