package jplume.interceptors;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.googlecode.htmlcompressor.compressor.HtmlCompressor;

import jplume.http.HttpResponseDelegate;
import jplume.http.Request;
import jplume.http.Response;

public class HtmlCompressorInterceptor extends InterceptorAdapter {

	@Override
	public Response processResponse(Request request, final Response response) {
		
		if (response.getStatus() == 200 && response.getContentType().equals("text/html")) {
			return new HttpResponseDelegate(response) {

				@Override
				public InputStream getContent() {
					InputStream is = super.getContent();
					
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					byte[] buf = new byte[1024];
					int len = 0;
					try {
						while ((len = is.read(buf)) > 0) {
							bos.write(buf, 0, len);
						}
						HtmlCompressor hc = new HtmlCompressor();
						hc.setEnabled(true);
						return new ByteArrayInputStream(hc.compress(bos.toString()).getBytes());
					} catch (IOException e) {
						try {
							is.reset();
						} catch (IOException e1) {
						}
						return is;
					}
				}
			};
		}
		return null;
	}


	
}
