package jplume.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

public class HttpJsonResponse extends AbstractResponse {

	protected JSONObject jsonObject;
	
	public HttpJsonResponse(int code, JSONObject obj) {
		super(code);
		this.jsonObject = obj;
		this.contentType = "application/json";
	}
	
	public HttpJsonResponse(JSONObject obj) {
		this(HttpServletResponse.SC_OK, obj);
	}
	
	@Override
	public InputStream getContent() {
		return new ByteArrayInputStream(jsonObject.toString().getBytes());
	}
	
	public static HttpJsonResponse create(Object obj) {
		return new HttpJsonResponse(JSONObject.fromObject(obj));
	}
	
	/**
	 * @return { 'result' : 'ok', 'contents' : ... }
	 */
	public static HttpJsonResponse ok(Object data) {
		Map<String, Object> errResp = new HashMap<String, Object>();
		errResp.put("result", "ok");
		errResp.put("contents", data);
		return create(errResp);
	}
	
	public static HttpJsonResponse ok() {
		Map<String, Object> errResp = new HashMap<String, Object>();
		errResp.put("result", "ok");
		return create(errResp);
	}
	
	/**
	 * @param errors
	 * @param fieldErrors
	 * @return { 'result' : 'error', 'contents' : { 'errors' : ..., 'fieldErrors' : ... } }
	 */
	public static HttpJsonResponse error(List<String> errors, Map<String, List<String>> fieldErrors) {
		Map<String, Object> errResp = new HashMap<String, Object>();
		errResp.put("result", "error");
		Map<String, Object> contents = new HashMap<>();
		contents.put("errors", errors);
		contents.put("fieldErrors", fieldErrors);
		errResp.put("contents", contents);
		return create(errResp);
	}
}
