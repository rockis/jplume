package jplume.http;

import static javax.servlet.http.HttpServletResponse.*;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRedirectResponse extends AbstractResponse {
	private Logger logger = LoggerFactory.getLogger(HttpRedirectResponse.class);
	private final String url;
	
	public HttpRedirectResponse(String url) {
		this(url, false);
	}
	
	public HttpRedirectResponse(String url, boolean permanently) {
		super(permanently ? SC_MOVED_PERMANENTLY : SC_MOVED_TEMPORARILY);
		this.url = url;
	}

	@Override
	public void apply(HttpServletResponse resp) {
		super.apply(resp);
		try {
			resp.sendRedirect(url);
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}
}
