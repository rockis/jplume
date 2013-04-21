package jplume.http;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import jplume.conf.Settings;
import jplume.core.Environ;

public class HttpResponse extends AbstractResponse {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4155308848218196212L;

	public HttpResponse(int status, InputStream content, String contentType, String encoding) {
		super(status);
		this.content = content;
		this.contentType = contentType;
		this.encoding = encoding;
	}
	
	public HttpResponse(int status) {
		super(status);
	}
	
	public HttpResponse(int status, InputStream content) {
		this(status, content, Settings.getDefaultContentType(), null);
	}
	
	public HttpResponse(int status, InputStream content, String contentType) {
		this(status, content, contentType, null);
	}
	
	public static Response create(int status, String content) {
		return new HttpResponse(status, new ByteArrayInputStream(content.getBytes()));
	}
	
	public static Response create(int status, String content, String contentType) {
		return new HttpResponse(status, new ByteArrayInputStream(content.getBytes()), contentType);
	}
	
	public static Response ok(String content) {
		return create(HttpServletResponse.SC_OK, content);
	}
	
	public static Response redirect(String url) {
		HttpResponse resp = new HttpResponse(302);
		if (url.charAt(0) == '/') {
		}
		resp.addHeader("Location", url);
		return resp;
	}
	
	public static Response redirect(Class<?> clazz, String methodName) {
		return redirect(Environ.reverseURL(clazz, methodName));
	}
	
	public static Response redirect(Class<?> clazz, String methodName, String[] pathVars) {
		return redirect(Environ.reverseURL(clazz, methodName, pathVars));
	}
	
	public static Response redirect(Class<?> clazz, String methodName, Map<String, String> namedVars) {
		return redirect(Environ.reverseURL(clazz, methodName, namedVars));
	}
	
	public static Response notModified(String mimeType) {
		HttpResponse resp =  new HttpResponse(HttpServletResponse.SC_NOT_MODIFIED, null, mimeType);
		return resp;
	}
	
	public static Response notFound() {
		return new HttpResponse(HttpServletResponse.SC_NOT_FOUND);
	}
	
	public static Response forbidden() {
		return new HttpResponse(HttpServletResponse.SC_FORBIDDEN);
	}
	
	public static Response internalError(Throwable e) {
		return new HttpErrorResponse(e);
	}
	
	public static Response internalError(String message, Throwable e) {
		return new HttpErrorResponse(message, e);
	}
	
	
	public static Response json(String result, Object data) {
		Map<String, Object> errResp = new HashMap<String, Object>();
		errResp.put("result", result);
		if (data != null) {
			errResp.put("contents", data);
		}
		InputStream content = new ByteArrayInputStream(JSONObject.fromObject(errResp).toString().getBytes());
		return new HttpResponse(200, content);
		
	}
	/**
	 * @return { 'result' : 'ok', 'contents' : ... }
	 */
	public static Response jsonOk(Object data) {
		return json("ok", data);
	}
	
	public static Response jsonOk() {
		return jsonOk(null);
	}
	
	/**
	 * @param errors
	 * @param fieldErrors
	 * @return { 'result' : 'error', 'contents' : { 'errors' : ..., 'fieldErrors' : ... } }
	 */
	public static Response jsonError(List<String> errors, Map<String, List<String>> fieldErrors) {
		Map<String, Object> data = new HashMap<>();
		data.put("errors", errors);
		data.put("fieldErrors", fieldErrors);
		return json("error", data);
	}
	
	public InputStream getContent() {
		return content;
	}
}
