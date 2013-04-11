package jplume.view;

import jplume.http.Request;
import jplume.http.Response;

public interface ErrorHandler {

	public Response handle403(Request request);
	
	public Response handle404(Request request);
	
	public Response handle500(Request request, Throwable e);
}
