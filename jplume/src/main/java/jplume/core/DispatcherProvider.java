package jplume.core;

import jplume.http.Request;
import jplume.http.Response;


public interface DispatcherProvider {
	
	Response dispatch(Request request) throws URLDispatchException;
	
	void setRegexPrefix(String prefix);
}