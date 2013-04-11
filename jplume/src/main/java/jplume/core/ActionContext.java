package jplume.core;

import javax.servlet.ServletContext;

public class ActionContext {

	private static ServletContext context = null;
	
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
}
