package jplume.http;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

public abstract class AbstractResponse implements Response{

	protected final int status;
	
	protected List<String[]> headers = new ArrayList<String[]>();
	
	protected int contentLength    = -1;
	
	public AbstractResponse(int code) {
		this.status = code;
	}

	public int getCode() {
		return status;
	}

	public void apply(HttpServletResponse resp) {
		resp.setStatus(this.status);
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

	@Override
	public boolean hasHeader(String key) {
		for (String[] header : this.headers) {
			if (header[0].equals(key)) {
				return true;
			}
		}
		return false;
	}
}
