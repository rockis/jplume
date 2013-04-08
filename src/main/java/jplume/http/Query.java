package jplume.http;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class Query {

	public Query(HttpServletRequest request) {
		Map<String, String[]> map = request.getParameterMap();
	}
}
