package jplume.core;

import jplume.http.Request;
import jplume.http.Response;

public class URLPatternGroup extends DispatcherProvider{
	
	private DispatcherProvider[] urlpatterns = new DispatcherProvider[0];
	
	public URLPatternGroup(Class<?> actionClass, DispatcherProvider... patterns) {
		urlpatterns = patterns;
		for (DispatcherProvider pattern : patterns) {
			if (pattern instanceof URLPattern) {
				((URLPattern)pattern).setActionClass(actionClass);
			}
		}
	}

	public URLPatternGroup(DispatcherProvider... patterns) {
		urlpatterns = patterns;
	}

	public Response dispatch(Request request) throws URLDispatchException {
		for (DispatcherProvider pattern : urlpatterns) {
			Response resp = pattern.dispatch(request);
			if (resp != null) return resp;
		}
		return null;
	}

	public void setRegexPrefix(String regexPrefix) {
		for (DispatcherProvider pattern : urlpatterns) {
			pattern.setRegexPrefix(regexPrefix);
		}
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (DispatcherProvider pattern : urlpatterns) {
			sb.append(pattern.toString() + "\n");
		}
		return sb.toString();
	}
}