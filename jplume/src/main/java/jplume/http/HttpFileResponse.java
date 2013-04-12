package jplume.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import jplume.utils.Http;

public class HttpFileResponse extends AbstractResponse {

	private File file;
	public HttpFileResponse(File file) {
		super(HttpServletResponse.SC_OK);
		this.file = file;
		this.addHeader("ETag", calcEtag(this.file));
 		this.addHeader("Last-Modified", Http.toGMT(this.file.lastModified()));
 		this.setContentLength((int)this.file.length());
 		this.setContentType(Http.getMimeType(file));
	}

	public static String calcEtag(File file) {
		return String.format("%d:%d", file.length(), file.lastModified());
	}
	
	@Override
	public void apply(HttpServletResponse resp) {
		InputStream is = null;
		OutputStream os = null;
		try {
			super.apply(resp);
			
			is = new FileInputStream(file);
			byte[] buffer = new byte[1024];
			os = resp.getOutputStream();
			while(is.read(buffer) > 0) {
				os.write(buffer);
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
