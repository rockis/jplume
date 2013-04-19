package jplume.interceptors;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jplume.conf.Settings;
import jplume.http.HttpErrorResponse;
import jplume.http.Request;
import jplume.http.Response;
import jplume.utils.ClassUtil;
import jplume.view.ErrorHandler;

public class CommonInterceptor extends InterceptorAdapter {

	private ErrorHandler errorHandler = null;
	
	private Logger logger = LoggerFactory.getLogger(CommonInterceptor.class);
	
	private static final String printFormat = "[%tc %s] \"%s HTTP/1.1\" %d %d %d";
	
	private boolean debug = false;
	private ThreadLocal<Long> reqTime = new ThreadLocal<>();
	
	public CommonInterceptor() {
		
		debug = Settings.isDebug();
		
		String errorHandlerClass = Settings.get("ERROR_HANDLER");
		
		try {
			errorHandler = (ErrorHandler) ClassUtil.forName(errorHandlerClass).newInstance();
		} catch (Exception e) {
			logger.error("Cannot create errorhandler ", e);
		} 
	}
	
	@Override
	public Response processRequest(Request request) {
		if (debug) {
			reqTime.set(new Date().getTime());
		}
		return null;
	}

	@Override
	public Response processResponse(Request request, Response response) {
		
		if (debug) {
			long duration = new Date().getTime() - reqTime.get();
			String log = String.format(printFormat, new Date(), request.getMethod(), request.getRequestURL(), response.getCode(), duration, response.getContentLength());
			if (response.getCode() == 200)
				System.out.println(log);
			else
				System.err.println(log);
		}
		
		if (errorHandler != null) {
			int code = response.getCode();
			switch(code) {
			case 403:
				return errorHandler.handle403(request);
			case 404:
				return errorHandler.handle404(request);
			case 500:
				if (response instanceof HttpErrorResponse) {
					return errorHandler.handle500(request, ((HttpErrorResponse)response).getException());
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
