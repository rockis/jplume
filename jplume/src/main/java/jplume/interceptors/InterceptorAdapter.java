package jplume.interceptors;

import jplume.http.Request;
import jplume.http.Response;

public abstract class InterceptorAdapter implements Interceptor {

	@Override
	public Response processRequest(Request request) {
		return null;
	}

	@Override
	public Response processResponse(Request request, Response response) {
		return null;
	}

	@Override
	public Response processException(Request request, Throwable e) {
		return null;
	}

}
