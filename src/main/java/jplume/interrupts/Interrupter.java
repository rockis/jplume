package jplume.interrupts;

import jplume.http.Request;
import jplume.http.Response;

public interface Interrupter {

	public Response processRequest(Request request);
	
	public Response processResponse(Request request, Response response);
	
	public Response processException(Request request, Response response, Exception e);
}
