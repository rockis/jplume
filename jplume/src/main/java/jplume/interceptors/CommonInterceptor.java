package jplume.interceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jplume.conf.Settings;
import jplume.http.HttpExceptResponse;
import jplume.http.Request;
import jplume.http.Response;
import jplume.view.ErrorHandler;

public class CommonInterceptor extends InterceptorAdapter {

	private ErrorHandler errorHandler = null;
	
	private Logger logger = LoggerFactory.getLogger(CommonInterceptor.class);
	
	public CommonInterceptor() {
		
		String errorHandlerClass = Settings.get("ERROR_HANDLER");
		try {
			errorHandler = (ErrorHandler) Class.forName(errorHandlerClass).newInstance();
		} catch (Exception e) {
			logger.error("Cannot create errorhandler ", e);
		} 
	}
	
	@Override
	public Response processResponse(Request request, Response response) {
		
		if (errorHandler != null) {
			int code = response.getCode();
			switch(code) {
			case 403:
				return errorHandler.handle403(request);
			case 404:
				return errorHandler.handle404(request);
			case 500:
				if (response instanceof HttpExceptResponse) {
					return errorHandler.handle500(request, ((HttpExceptResponse)response).getException());
				}
			}
		}
		return null;
	}
	
	@Override
	public Response processException(Request request, Throwable e) {
		if (errorHandler != null) {
			return errorHandler.handle500(request, e);
		}
		return null;
	}

}
