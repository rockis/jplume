package jplume.conf;

public class URLResolveException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3998734598921148842L;

	public URLResolveException(String msg) {
		super(msg);
	}
	
	public URLResolveException(String msg, Throwable e) {
		super(msg, e);
	}
}