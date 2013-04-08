package jplume.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletResponse;

public class HttpFileResponse extends AbstractResponse {

	private static final MimetypesFileTypeMap mimeTypes = new MimetypesFileTypeMap();
	
	private File file;
	public HttpFileResponse(File file) {
		super(HttpServletResponse.SC_OK);
		this.file = file;
	}

	@Override
	public void apply(HttpServletResponse resp) {
		InputStream is = null;
		try {
			super.apply(resp);
			resp.setContentType(mimeTypes.getContentType(this.file));
			if (file.length() > 0) {
				resp.setContentLength((int)file.length());
			}
			is = new FileInputStream(file);
			byte[] buffer = new byte[1024];
			while(is.read(buffer) > 0) {
				resp.getOutputStream().write(buffer);
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
		}
	}
}
