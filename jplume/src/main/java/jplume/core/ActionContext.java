package jplume.core;

import javax.servlet.ServletContext;

import jplume.http.Request;

public class ActionContext {

	private static ServletContext context = null;
	
	private static ThreadLocal<Request> request = new ThreadLocal<>();
	
	public static void setContext(ServletContext cntx) {
		context = cntx;
	}

	public static ServletContext getContext() {
		return context;
	}
	
	public static String getContextPath() {
		return getContext().getContextPath();
	}
	
	public static String getRealPath(String resource) {
		return getContext().getRealPath(resource);
	}
	
	public static void setRequest(Request req) {
		request.set(req);
	}
	
	public static Request getRequest() {
		return request.get();
	}
}
