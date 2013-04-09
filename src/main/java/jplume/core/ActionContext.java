package jplume.core;

import javax.servlet.ServletContext;

public class ActionContext {

	private static ThreadLocal<ServletContext> context = new ThreadLocal<ServletContext>();
	
	public static void setContext(ServletContext cntx) {
		context.set(cntx);
	}

	public static ServletContext getContext() {
		return context.get();
	}
	
	public static String getContextPath() {
		return getContext().getContextPath();
	}
	
	public static String getRealPath(String resource) {
		return getContext().getRealPath(resource);
	}
}
