package jplume.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import jplume.utils.Http;

public class HttpFileResponse extends AbstractResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6154433119345043254L;
	
	private File file;
	
	public HttpFileResponse(File file) {
		super(HttpServletResponse.SC_OK);
		this.file = file;
 		this.addHeader("Last-Modified", Http.toGMT(this.file.lastModified()));
 		this.contentLength  = ((int)this.file.length());
 		this.contentType  = Http.getMimeType(file);
	}

	@Override
	public InputStream getContent() {
		try {
			return new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new IllegalStateException("File not found", e);
		}
	}
}
