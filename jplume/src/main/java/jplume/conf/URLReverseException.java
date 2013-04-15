package jplume.conf;

import java.util.Map;

public class URLReverseException extends RuntimeException{

	private static final long serialVersionUID = 3998734598921148842L;

	public URLReverseException(String msg) {
		super(msg);
	}
	
	public URLReverseException(String msg, Throwable e) {
		super(msg, e);
	}
	
	public URLReverseException(String classMethodName, String[] indexed, Map<String, String> named) {
		super(String.format("Reverse for '%s' with arguments '%s' and keyword " +
				"arguments '%s' not found.", classMethodName, indexed, named));
	}


}
