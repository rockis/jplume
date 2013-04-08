package jplume.http;

import javax.servlet.http.HttpServletResponse;

public abstract class AbstractResponse implements Response{

	protected final int status;
	
	public AbstractResponse(int code) {
		this.status = code;
	}

	public int getCode() {
		return status;
	}

	public void apply(HttpServletResponse resp) {
		resp.setStatus(this.status);
	}
	
}
