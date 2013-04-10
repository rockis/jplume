package jplume.interceptors;

import jplume.http.Request;
import jplume.http.Response;

public interface Interceptor {

	public Response processRequest(Request request);
	
	public Response processResponse(Request request, Response response);
	
	public Response processException(Request request, Throwable e);
}
