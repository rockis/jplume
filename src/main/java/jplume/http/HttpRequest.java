package jplume.http;

import javax.servlet.http.HttpServletRequest;

public class HttpRequest implements Request {

	private final HttpServletRequest rawRequest;
	
	public HttpRequest(HttpServletRequest request) {
		this.rawRequest = request;
	}
	
	public String getPath() {
		return rawRequest.getServletPath();
	}
	
	public Query getQuery() {
		return new Query(this.rawRequest);
	}
}
