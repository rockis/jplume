package jplume.http;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletResponse;

import jplume.conf.Settings;

public class HttpErrorResponse  extends AbstractResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7019066965418251085L;
	private String message;
	private Throwable exception;
	
	public HttpErrorResponse(Throwable e) {
		this(null, e);
	}
	
	public HttpErrorResponse(String message, Throwable e) {
		super(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		this.contentType = "text/html";
		this.exception = e;
		this.message = message == null ? e.getMessage() : message;
	}

	public Throwable getException() {
		return exception;
	}

	@Override
	public InputStream getContent() {
		StringWriter sw = new StringWriter();
			
		if (Settings.isDebug()) {
			if (message != null) {
				sw.write("<h1>" + message + "</h1>");
			}
			exception.printStackTrace(new PrintWriter(sw));
		}else{
			sw.write("<h1>Internal Server Error</h1>");
			sw.write("<h2>" + message + "</h2>");
		}
		return new ByteArrayInputStream(sw.toString().getBytes());
	}
}