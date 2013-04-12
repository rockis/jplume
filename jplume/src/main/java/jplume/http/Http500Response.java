package jplume.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import jplume.conf.Settings;

public class Http500Response  extends AbstractResponse {

	private String message;
	private Throwable exception;
	
	public Http500Response(Throwable e) {
		this(null, e);
	}
	
	public Http500Response(String message, Throwable e) {
		super(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		this.exception = e;
		this.message = message == null ? e.getMessage() : message;
	}

	public Throwable getException() {
		return exception;
	}

	@Override
	public void apply(HttpServletResponse resp) {
		InputStream is = null;
		OutputStream os = null;
		try {
			super.apply(resp);
			resp.setContentType("text/html");
			
			PrintWriter w = resp.getWriter();
			
			if (Settings.isDebug()) {
				if (message != null) {
					w.write("<h1>" + message + "</h1>");
				}
				exception.printStackTrace(w);
			}else{
				w.write("<h1>Internal Server Error</h1>");
				w.write("<h2>" + message + "</h2>");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
				}
			}
		}
	}
}