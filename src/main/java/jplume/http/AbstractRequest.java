package jplume.http;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public abstract class AbstractRequest implements Request {

	protected final HttpServletRequest rawRequest;
	
	public final Map<String, String> META;
	
	public AbstractRequest(HttpServletRequest request) {
		this.rawRequest     = request;
		this.META = Collections.unmodifiableMap(createMeta(request));
	}
	
	protected Map<String, String> createMeta(HttpServletRequest request) {
		Map<String, String> meta = new HashMap<String, String>(System.getenv());
		return meta;
	}
	
	@Override
	public String getPath() {
		return rawRequest.getServletPath();
	}
	
	@Override
	public Query getQuery() {
		return new Query(this.rawRequest);
	}

	@Override
	public String getMethod() {
		return rawRequest.getMethod();
	}

	@Override
	public Map<String, String> getMeta() {
		return META;
	}

	@Override
	public String getQueryString() {
		return rawRequest.getQueryString();
	}
}
